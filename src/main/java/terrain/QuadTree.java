package terrain;

import Misc.Maths;
import org.joml.*;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;


class Node {
    int x, y, value;

    Node(int x, int y, int value) {
        this.x = x;
        this.y = y;
        this.value = value; /* some data*/
    }
}

/* Using two points of Rectangular (Top,Left) & (Bottom , Right)*/
class Boundary {
    public float getxMin() {
        return xMin;
    }

    public float getyMin() {
        return yMin;
    }

    public float getxMax() {
        return xMax;
    }

    public float getyMax() {
        return yMax;
    }

    public float getzMax() {
        return zMax;
    }

    public float getzMin() {
        return zMin;
    }

    public Vector3f size(){
        return new Vector3f(xMax-xMin, yMax-yMin, zMax-zMin);
    }

    public Boundary(float xMin, float yMin, float zMin,  float xMax, float yMax, float zMax) {
        this.xMin = xMin;
        this.yMin = yMin;
        this.zMin = zMin;
        this.xMax = xMax;
        this.yMax = yMax;
        this.zMax = zMax;
    }

    public boolean inRange(int x, int y) {
        return (x >= this.getxMin() && x <= this.getxMax()
                && y >= this.getyMin() && y <= this.getyMax());
    }

    float xMin, yMin, zMin, xMax, yMax, zMax;

}

public class QuadTree {
    int level = 0;
    List<QuadTree> children;
    Boundary boundary;
    QuadTree parent;

    private Matrix4f localToWorld;
    private Vector3f center;
    private Vector3d sphereCenter;
    private int size; //The size of the root node
    private Vector3f translate;


    //Used to instantiate the root node
    public QuadTree(int level, int size, Matrix4f localToWorld, Vector3f translate){
        this(level, size, new Boundary(-size,-size,0, size,size, 0), localToWorld, translate);
    }

    QuadTree(int level, int size, Boundary b, Matrix4f localToWorld, Vector3f translate) {
        this.level = level;
        children = new ArrayList<>();
        this.translate = translate;
        this.boundary = b;
        this.size = size;
        this.localToWorld = localToWorld;
        this.center = new Vector3f((boundary.xMin + boundary.xMax) / 2, (boundary.yMin + boundary.yMax) / 2, (boundary.zMin + boundary.zMax) / 2);

        this.sphereCenter = new Vector3d(center);
        Maths.applyMatrix4To3DVector(sphereCenter, localToWorld);
        sphereCenter.normalize();
        sphereCenter.mul(size);
        sphereCenter.add(translate);
    }


    public Vector3f getTranslate() {
        return translate;
    }

    public QuadTree getParent() {
        return parent;
    }

    public void setParent(QuadTree parent) {
        this.parent = parent;
    }

    public ArrayList<QuadTree> getLeafNodes(){
        ArrayList<QuadTree> children = new ArrayList<>();
        getLeafNodes(this, children);
        return children;
    }

    public static void getLeafNodes(QuadTree node, ArrayList<QuadTree> target){
        if(node.children.size() == 0){
            target.add(node);
            return;
        }
        for(QuadTree child : node.children){
            getLeafNodes(child, target);
        }
    }

    void split() {

        //Bottom left
        QuadTree b1 = new QuadTree(this.level + 1, size, new Boundary(
                this.boundary.getxMin(), this.boundary.getyMin(),0, center.x,
                center.y, 0), localToWorld, translate);
        b1.setParent(this);

        //Bottom right
        QuadTree b2 = new QuadTree(this.level + 1, size, new Boundary(center.x, this.boundary.getyMin(), 0, this.boundary.getxMax(), center.y, 0), localToWorld, translate);
        b2.setParent(this);
        //Top left
        QuadTree b3  =  new QuadTree(this.level + 1, size, new Boundary(this.boundary.getxMin(), center.y, 0, center.x, this.boundary.getyMax(), 0), localToWorld, translate);
        b3.setParent(this);
        //Top right
        QuadTree b4 = new QuadTree(this.level + 1, size, new Boundary(center.x, center.y, 0, this.boundary.getxMax(), this.boundary.getyMax(), 0), localToWorld, translate);
        b4.setParent(this);

        children.add(b1);
        children.add(b2);
        children.add(b3);
        children.add(b4);

    }

    public Vector3f getCenter() {
        return center;
    }

    public Vector3d getSphereCenter() {
        return sphereCenter;
    }

    public double distanceToChild(QuadTree child, Vector3d pos){
        return child.sphereCenter.distance(pos);
    }


    void insert(Vector3d pos) {
        if (distanceToChild(this, pos) < boundary.size().x * 2 && boundary.size().x > 512) {
            split();
            for(QuadTree child : children){
                child.insert(pos);
            }
        }
    }
}
