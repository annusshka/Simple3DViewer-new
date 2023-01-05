package com.cgvsu;

import com.cgvsu.math.Math.Matrix.Matrix;
import com.cgvsu.math.Math.Matrix.Matrix4f;
import com.cgvsu.math.Math.Vector.Vector3f;
import com.cgvsu.render_engine.MyGraphicConveyor;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

public class MyGraphicConveyorTest {

    static final float EPS = 1e-6f;

    @Test
    public void getRotateMatrix() throws Matrix.MatrixException {
        Matrix4f matrix4f =
                new Matrix4f(new float[]{1, 0, 0, 0, 0, 0.8660254f, 0.5f, 0, 0, -0.5f, 0.8660254f, 0, 0, 0, 0, 1});
        Vector3f vector3f = new Vector3f(new float[]{30, 0, 0});
        Assertions.assertTrue(MyGraphicConveyor.getRotateMatrix(vector3f).isEqualMatrix(matrix4f));

        Matrix4f matrix4f2 =
                new Matrix4f(new float[]{0.8660254f, 0, -0.5f, 0, 0, 1, 0, 0, 0.5f, 0, 0.8660254f, 0, 0, 0, 0, 1});
        Vector3f vector3f2 = new Vector3f(new float[]{0, 30, 0});
        Assertions.assertTrue(MyGraphicConveyor.getRotateMatrix(vector3f2).isEqualMatrix(matrix4f2));

        Matrix4f matrix4f3 =
                new Matrix4f(new float[]{0.8660254f, 0.5f, 0, 0, -0.5f, 0.8660254f, 0, 0, 0, 0, 1, 0, 0, 0, 0, 1});
        Vector3f vector3f3 = new Vector3f(new float[]{0, 0, 30});
        Assertions.assertTrue(MyGraphicConveyor.getRotateMatrix(vector3f3).isEqualMatrix(matrix4f3));

        Matrix4f matrix4f4 =
                new Matrix4f(new float[]{
                        0.4330125f, 0.5f, -0.7499949f, 0,
                        -0.25f, 0.8660254f, 0.43301f, 0,
                        0.866025f, 0, 0.5f, 0,
                        0, 0, 0, 1});
        Vector3f vector3f4 = new Vector3f(new float[]{0, 60, 30});
        Assertions.assertTrue(MyGraphicConveyor.getRotateMatrix(vector3f4).isEqualMatrix(matrix4f4));
    }

    @Test
    public void setScaleMatrix() {
        Vector3f vector3f = new Vector3f(new float[]{0.5f, 3, 1});
        Matrix4f identityMatrix = (Matrix4f) new Matrix4f().createIdentityMatrix();
        Matrix4f matrix4f = new Matrix4f(new float[]{0.5f, 0, 0, 0, 0, 3, 0, 0, 0, 0, 1, 0, 0, 0, 0, 1});
        MyGraphicConveyor.setScaleMatrix(identityMatrix, vector3f);
        Assertions.assertTrue(identityMatrix.isEqualMatrix(matrix4f));
    }

    @Test
    public void addTranslate() {
        Vector3f vector3f = new Vector3f(new float[]{0.5f, 3, 1});
        Matrix4f identityMatrix = (Matrix4f) new Matrix4f().createIdentityMatrix();
        Matrix4f matrix4f = new Matrix4f(new float[]{1, 0, 0, 0.5f, 0, 1, 0, 3, 0, 0, 1, 1, 0, 0, 0, 1});
        MyGraphicConveyor.addTranslate(identityMatrix, vector3f);
        Assertions.assertTrue(identityMatrix.isEqualMatrix(matrix4f));
    }

    @Test
    public void rotateScaleTranslate() throws Matrix.MatrixException {
        Vector3f scaleVector = new Vector3f(new float[]{0, 5.4f, 1});
        Vector3f rotateVector = new Vector3f(new float[]{60, 30, 120});
        Vector3f translateVector = new Vector3f(new float[]{5, 0, -6.1f});
        Matrix4f matrix4f = new Matrix4f(new float[]{
                -0.4330125f, 1.1691425f, 0.8749993f, 5,
                -0.74999497f, -3.3749712f, -0.2165075f, 0,
                0.5f, -4.04996f, 0.433012f, -6.1f,
                0, 0, 0, 1});

        Assertions.assertTrue(MyGraphicConveyor.rotateScaleTranslate(rotateVector, scaleVector, translateVector).
                isEqualMatrix(matrix4f));

    }
}
