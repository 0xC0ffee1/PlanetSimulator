package particles;


import Misc.DisplayTime;
import Misc.WorldConstants;
import Textures.ParticleTexture;
import org.joml.Vector2f;
import org.joml.Vector3d;
import org.joml.Vector3f;
import renderEngine.Camera;

public class Particle {

    private Vector3d position;
    private Vector3f velocity;
    private float gravityEffect;
    private float lifeLength;
    private float rotation;
    private float scale;

    private ParticleTexture texture;

    private float elapsedTime = 0;

    private Vector2f texOffset1 = new Vector2f();
    private Vector2f texOffset2 = new Vector2f();
    private float blend;

    private double distance;

    private Vector3f reusableChange = new Vector3f();

    public Particle(ParticleTexture texture, Vector3d position, Vector3f velocity,
                    float gravityEffect, float lifeLength,
                    float rotation, float scale)
    {
        this.texture = texture;
        this.position = position;
        this.velocity = velocity;
        this.gravityEffect = gravityEffect;
        this.lifeLength = lifeLength;
        this.rotation = rotation;
        this.scale = scale;
        ParticleHandler.addParticle(this);
    }

    public ParticleTexture getTexture() {
        return texture;
    }

    public double getDistance() {
        return distance;
    }

    public Vector2f getTexOffset1() {
        return texOffset1;
    }

    public Vector2f getTexOffset2() {
        return texOffset2;
    }

    public float getBlend() {
        return blend;
    }

    public Vector3d getPosition() {
        return position;
    }

    public void setPosition(Vector3d position) {
        this.position = position;
    }

    public Vector3f getVelocity() {
        return velocity;
    }

    public void setVelocity(Vector3f velocity) {
        this.velocity = velocity;
    }

    public float getGravityEffect() {
        return gravityEffect;
    }

    public void setGravityEffect(float gravityEffect) {
        this.gravityEffect = gravityEffect;
    }

    public float getLifeLength() {
        return lifeLength;
    }

    public void setLifeLength(float lifeLength) {
        this.lifeLength = lifeLength;
    }

    public float getRotation() {
        return rotation;
    }

    public void setRotation(float rotation) {
        this.rotation = rotation;
    }

    public float getScale() {
        return scale;
    }

    public void setScale(float scale) {
        this.scale = scale;
    }

    protected boolean update(Camera camera){
        velocity.y += WorldConstants.GRAVITY * gravityEffect * DisplayTime.getRefreshTime();
        reusableChange.set(velocity);
        reusableChange.mul(DisplayTime.getRefreshTime());
        position.add(reusableChange);
        distance = camera.getPosition().distanceSquared(position);
        updateTexCoordInfo();
        elapsedTime += DisplayTime.getRefreshTime();
        return elapsedTime < lifeLength;
    }

    private void updateTexCoordInfo(){
        float lifeFactor = elapsedTime / lifeLength;
        int stageCount = texture.getRows() * texture.getRows();
        float atlasProgression = lifeFactor * stageCount;
        int index1 = (int) Math.floor(atlasProgression);
        int index2 = index1 < stageCount -1 ? index1 + 1 : index1;
        this.blend = atlasProgression % 1;
        setTextureOffset(texOffset1, index1);
        setTextureOffset(texOffset2, index2);
    }

    private void setTextureOffset(Vector2f offset, int index){
        int column = index % texture.getRows();
        int row = index / texture.getRows();
        offset.x = (float) column / texture.getRows();
        offset.y = (float) row / texture.getRows();
    }
}
