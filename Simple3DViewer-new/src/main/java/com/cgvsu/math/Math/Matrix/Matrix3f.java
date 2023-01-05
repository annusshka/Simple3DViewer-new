package com.cgvsu.math.Math.Matrix;

import com.cgvsu.math.Math.Vector.Vector;
import com.cgvsu.math.Math.Vector.Vector3f;

public class Matrix3f extends Matrix {

    private static final int size = 3;

    private static final int length = 9;

    private float[] vector = new float[length];

    public Matrix3f(float[] vector) {
        super(vector, size);
        this.vector = vector;
    }

    public Matrix3f() {
        super(new float[length], size);
    }

    @Override
    public Matrix getZeroMatrix(int size) {
        return new Matrix3f(new float[length]);
    }

    @Override
    public Vector getZeroVector(int size) {
        if (size != this.getSize()) {
            size = this.getSize();
        }
        return new Vector3f(new float[size]);
    }

    public static Matrix getZeroMatrix() {
        return new Matrix3f(new float[length]);
    }

    @Override
    public Matrix createIdentityMatrix(final float value) {
        Matrix3f matrix = new Matrix3f(new float[size * size]);

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
        return createIdentityMatrix(1.0f);
    }


    /**
     * Метод раскладывает матрицу 3х3 по первой строке на 3 минора
     * @param matrix
     * @return возвращает определитель
     */
    public static float getMatrixDeterminant(final Matrix3f matrix) {
        float determinant = 0.0f;

        for (int index = 0; index < matrix.getSize(); index++) {
            int sign = (int) Math.pow(-1, index % matrix.getSize());

            determinant += sign * matrix.getVector()[index] * matrix.getMinor(index);
        }

        return determinant;
    }

    private float getMinor(final int index) {
        float value1, value2;

        final int indexCol = index % size;
        final int indexRow = index / size;
        int indexCol1 = 0;
        int indexRow1 = 0;

        if (indexCol1 == indexCol) {
            indexCol1++;
        }

        int indexCol2 = indexCol1 + 1;
        if (indexCol2 == indexCol) {
            indexCol2++;
        }

        if (indexRow1 == indexRow) {
            indexRow1++;
        }

        int indexRow2 = indexRow1 + 1;
        if (indexRow2 == indexRow) {
            indexRow2++;
        }

        value1 = this.get((indexRow1) * size + indexCol1) * this.get((indexRow2) * size + indexCol2);
        value2 = this.get((indexRow1) * size + indexCol2) * this.get((indexRow2) * size + indexCol1);
        return value1 - value2;
    }
}