package com.cgvsu.render_engine;

import com.cgvsu.math.Math.Matrix.Matrix4f;
import com.cgvsu.math.Math.Vector.Vector;
import com.cgvsu.math.Math.Vector.Vector3f;

public class Camera {

    public Camera(
            final Vector3f position,
            final Vector3f target,
            final float fov,
            final float aspectRatio,
            final float nearPlane,
            final float farPlane) {
        this.position = position;
        this.target = target;
        this.fov = fov;
        this.aspectRatio = aspectRatio;
        this.nearPlane = nearPlane;
        this.farPlane = farPlane;
    }

    public void setPosition(final Vector3f position) {
        this.position = position;
    }

    public void setTarget(final Vector3f target) {
        this.target = target;
    }

    public void setAspectRatio(final float aspectRatio) {
        this.aspectRatio = aspectRatio;
    }

    public Vector3f getPosition() {
        return position;
    }

    public Vector3f getTarget() {
        return target;
    }

    public void movePosition(final Vector3f translation) {
        try {
            position = (Vector3f) Vector.sumVector(position, translation);
        } catch (Vector.VectorException e) {
            throw new RuntimeException(e);
        }
    }

    public void moveTarget(final Vector3f translation) {
        try {
            target = (Vector3f) Vector3f.sumVector(target, translation);
        } catch (Vector.VectorException e) {
            throw new RuntimeException(e);
        }
    }

    public Matrix4f getViewMatrix() {
        try {
            return MyGraphicConveyor.lookAt(position, target);
        } catch (Vector.VectorException e) {
            throw new RuntimeException(e);
        }
    }

    public Matrix4f getProjectionMatrix() {
        return MyGraphicConveyor.perspective(fov, aspectRatio, nearPlane, farPlane);
    }

    private Vector3f position;
    private Vector3f target;
    private float fov;
    private float aspectRatio;
    private float nearPlane;
    private float farPlane;
}