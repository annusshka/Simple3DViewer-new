package com.cgvsu.math.Math.Matrix;

import com.cgvsu.math.Math.Vector.Vector;
import com.cgvsu.math.Math.Vector.Vector4f;

public class Matrix4f extends Matrix {

    private static final int length = 16;

    private static final int size = 4;

    private float[] vector = new float[length];

    public Matrix4f(float[] vector) {
        super(vector, size);
        this.vector = vector;
    }

    public Matrix4f() {
        super(new float[length], size);
    }

    @Override
    public Matrix getZeroMatrix(final int size) {
        return new Matrix4f(new float[length]);
    }

    @Override
    public Vector getZeroVector(int size) {
        if (size != this.getSize()) {
            size = this.getSize();
        }
        return new Vector4f(new float[size]);
    }

    public static Matrix getZeroMatrix() {
        return new Matrix4f(new float[length]);
    }

    public Matrix createIdentityMatrix(final float value) {
        Matrix4f matrix = new Matrix4f(new float[size * size]);

        int indexMainDiagonal = 0;
        for (int index = 0; index < matrix.getLength(); index++) {

            if (index == indexMainDiagonal * size + indexMainDiagonal) {
                matrix.set(index, value);
                indexMainDiagonal++;
            }
        }

        return matrix;
    }

    @Override
    public Matrix createIdentityMatrix() {
        return createIdentityMatrix(1);
    }

    /**
     * Метод раскладывает матрицу 4х4 по первой строке на 4 матрицы размера 3х3, затем для каждой матрицы вызывает
     * метод поиска определителя в Matrix3x
     * @param matrix
     * @return возвращает определитель
     */
    public static float getMatrixDeterminant(final Matrix matrix) {
        float determinant = 0.0f;
        int indexCol, indexCol1, indexCol2, indexCol3;
        int indexRow = 0;

        for (int index = 0; index < matrix.getSize(); index++) {
            indexCol = index % matrix.getSize();
            indexCol1 = 0;
            int sign = (int) Math.pow(-1, indexCol + indexRow);

            if (indexCol1 == indexCol) {
                indexCol1++;
            }

            indexCol2 = indexCol1 + 1;
            if (indexCol2 == indexCol) {
                indexCol2++;
            }

            indexCol3 = indexCol2 + 1;
            if (indexCol3 == indexCol) {
                indexCol3++;
            }

            float[] m = new float[9];
            for (int index1 = 0; index1 < m.length; index1 += 3) {
                m[index1] = matrix.get((indexRow + 1) * matrix.getSize() + indexCol1);
                m[index1 + 1] = matrix.get((indexRow + 1) * matrix.getSize() + indexCol2);
                m[index1 + 2] = matrix.get((indexRow + 1) * matrix.getSize() + indexCol3);
                indexRow++;
            }
            indexRow = 0;

            determinant += sign * matrix.get(index) * Matrix3f.getMatrixDeterminant(new Matrix3f(m));
        }

        return determinant;
    }
}