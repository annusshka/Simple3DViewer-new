package com.cgvsu.math.Math.Vector;

public abstract class Vector {

    public static class VectorException extends Exception {

        public VectorException(String message) {
            super(message);
        }
    }

    protected final int size;

    protected float[] vector;

    public Vector(float[] vector, final int size) {
        if (vector.length == size) {
            this.vector = vector;
            this.size = size;
        } else if (size > 0) {
            float[] rightVector = new float[size];
            System.arraycopy(vector, 0, rightVector, 0, Math.min(vector.length, size));
            this.vector = rightVector;
            this.size = size;
        } else {
            this.vector = new float[0];
            this.size = 0;
        }
    }

    static final float EPS = 1e-6f;

    public int getSize() {
        return size;
    }

    public float[] getVector() {
        return vector;
    }

    public void setData(float[] data) {
        if (data.length == size) {
            this.vector = data;
        } else {
            float[] rightVector = new float[size];
            System.arraycopy(data, 0, rightVector, 0, Math.min(data.length, size));
            this.vector = rightVector;
        }
    }

    public float get(final int index) {
        float element = 0;
        if (index >= 0 && index < this.getSize()) {
            element = vector[index];
        }
        return element;
    }

    public void set(final int index, final float value) {
        if (index >= 0 && index < getVector().length) {
            vector[index] = value;
        }
    }

    public abstract Vector getZeroVector(final int size);

    protected static boolean isEqualSize(final Vector v1, final Vector v2) {
        return v1.getSize() == v2.getSize();
    }

    public boolean isEqual(final Vector otherVector) {
        if (isEqualSize(this, otherVector)) {
            for (int index = 0; index < this.getVector().length; index++) {
                if (Math.abs(this.get(index) - otherVector.get(index)) >= EPS) {
                    return false;
                }
            }
        } else {
            return false;
        }

        return true;
    }

    public static Vector sumVector(final Vector vector1, final Vector vector2) throws VectorException {
        Vector resultVector = vector1.getZeroVector(vector1.getSize());

        if (isEqualSize(vector1, vector2)) {
            for (int index = 0; index < vector1.getSize(); index++) {
                resultVector.getVector()[index] = vector1.get(index) + vector2.get(index);
            }
        } else {
            throw new VectorException("Vectors of different sizes can't be summed");
        }

        return resultVector;
    }

    public Vector minusVector(final Vector vector1, final Vector vector2) throws VectorException {
        Vector resultVector = vector1.getZeroVector(vector1.getSize());

        if (isEqualSize(vector1, vector2)) {
            for (int index = 0; index < vector1.getSize(); index++) {
                resultVector.getVector()[index] = vector1.get(index) - vector2.get(index);
            }

        } else {
            throw new VectorException("Vectors of different sizes can't be summed");
        }

        return resultVector;
    }

    public Vector sumWithConstant(final float constant) {
        for (int index = 0; index < this.getSize(); index++) {
            this.getVector()[index] += constant;
        }
        return this;
    }

    public Vector minusWithConstant(final float constant) {
        return sumWithConstant(-constant);
    }

    public Vector multiplicateVectorOnConstant(final float constant) {
        for (int index = 0; index < this.getSize(); index++) {
            this.getVector()[index] *= constant;
        }
        return this;
    }

    public Vector divideVectorOnConstant(final float constant) throws VectorException {
        if (Math.abs(0 - constant) < EPS) {
            throw new VectorException("Division by zero");
        }

        return multiplicateVectorOnConstant((1.0f / constant));
    }

    public float getVectorLength() {
        float length = 0;
        for (float value: this.getVector()) {
            length += Math.pow(value, 2);
        }
        return (float) Math.sqrt(length);
    }

    public Vector normalizeVector() throws VectorException {
        return divideVectorOnConstant(getVectorLength());
    }

    public static float dotProduct(final Vector vector1, final Vector vector2) {
        float scalar = 0;
        if (isEqualSize(vector1, vector2)) {
            for (int index = 0; index < vector1.getSize(); index++) {
                scalar += vector1.get(index) * vector2.get(index);
            }
        }
        return scalar;
    }

    public float dotProduct(final Vector vector) {
        return dotProduct(this, vector);
    }

    public void swapElements(final int index, final int changingIndex) {
        float changingValue = this.get(index);
        this.set(index, this.get(changingIndex));
        this.set(changingIndex,changingValue);
    }
}
