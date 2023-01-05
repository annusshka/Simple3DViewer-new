package com.cgvsu.Rasterization;


public abstract class GraphicsUtils<T> {
    T graphics;

    public GraphicsUtils(T graphics) {
        this.graphics = graphics;
    }

    public abstract void setPixel(int x, int y, MyColor myColor);

}
