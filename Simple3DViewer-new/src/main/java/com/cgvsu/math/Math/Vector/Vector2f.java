package com.cgvsu.math.Math.Vector;

/**
 * Класс вектора размерности 2, реализация абстрактного класса Vector
 */

public class Vector2f extends Vector {

    private static final int size = 2;

    private float[] vector = new float[size];

    public Vector2f(float[] vector) {
        super(vector, size);
        this.vector = vector;
    }

    public Vector2f() {
        super(new float[size], size);
    }

    @Override
    public Vector getZeroVector(int size) {
        if (size != this.getSize()) {
            size = this.getSize();
        }
        return new Vector2f(new float[size]);
    }
}