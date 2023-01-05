package com.cgvsu.math.Math.Vector;

/**
 * Класс вектора размерности 4, реализация абстрактного класса Vector
 */

public class Vector4f extends Vector {

    private static final int size = 4;

    private float[] vector = new float[size];

    public Vector4f(float[] vector) {
        super(vector, size);
        this.vector = vector;
    }

    public Vector4f() {
        super(new float[size], size);
    }

    @Override
    public Vector getZeroVector(int size) {
        if (size != this.getSize()) {
            size = this.getSize();
        }
        return new Vector4f(new float[size]);
    }
}