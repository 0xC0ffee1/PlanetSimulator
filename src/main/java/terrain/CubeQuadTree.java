package terrain;

import org.joml.Matrix4f;
import org.joml.Vector2i;
import org.joml.Vector3d;
import org.joml.Vector3f;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;


class SideData{
    public Matrix4f transform, worldToLocal;
    public QuadTree quadTree;

    public SideData(Matrix4f transform, Matrix4f worldToLocal, QuadTree quadTree) {
        this.transform = transform;
        this.worldToLocal = worldToLocal;
        this.quadTree = quadTree;
    }
}

public class CubeQuadTree {
    private final List<SideData> sides;
    final List<Matrix4f> transforms = new ArrayList<>();

    public List<Matrix4f> getTransforms() {
        return transforms;
    }

    public CubeQuadTree(Vector3f translate, int RADIUS){
        sides = new ArrayList<>();




        // +Y
        Matrix4f m = new Matrix4f();
        m.rotateX((float) (-Math.PI / 2));
        m.mulLocal(new Matrix4f().translate(new Vector3f(0,RADIUS,0)));
        transforms.add(m);

        // -Y
        Matrix4f m1 = new Matrix4f();
        m1.rotateX((float) (Math.PI / 2));
        m1.mulLocal(new Matrix4f().translate(new Vector3f(0,-RADIUS,0)));
        transforms.add(m1);

        //+ X
        Matrix4f m2 = new Matrix4f();
        m2.rotateY((float) (Math.PI / 2));
        m2.mulLocal(new Matrix4f().translate(new Vector3f(RADIUS,0,0)));
        transforms.add(m2);

        //- X
        Matrix4f m3 = new Matrix4f();
        m3.rotateY((float) (-Math.PI / 2));
        m3.mulLocal(new Matrix4f().translate(new Vector3f(-RADIUS,0,0)));
        transforms.add(m3);

        //+ Z
        Matrix4f m4 = new Matrix4f();
        m4.mulLocal(new Matrix4f().translate(new Vector3f(0,0,RADIUS)));
        transforms.add(m4);

        //- Z
        Matrix4f m5 = new Matrix4f();
        m5.rotateY((float) Math.PI);
        m5.mulLocal(new Matrix4f().translate(new Vector3f(0,0,-RADIUS)));
        transforms.add(m5);

        for(Matrix4f t : transforms){
            sides.add(new SideData(new Matrix4f(t), new Matrix4f(t).invert(), new QuadTree(0, RADIUS, t, new Vector3f(translate))));
        }
    }

    public List<SideData> getChildren(){
        return sides;
    }

    public void insert(Vector3d pos){
        for(SideData s : sides){
            s.quadTree.insert(pos);
        }
    }

}
