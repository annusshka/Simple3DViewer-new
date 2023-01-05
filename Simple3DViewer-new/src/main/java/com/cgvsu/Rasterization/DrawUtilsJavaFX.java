package com.cgvsu.Rasterization;



import javafx.scene.canvas.Canvas;
import javafx.scene.paint.Color;

public class DrawUtilsJavaFX extends GraphicsUtils<Canvas> {
    public DrawUtilsJavaFX(Canvas graphics) {
        super(graphics);
    }

    @Override
    public void setPixel(int x, int y, MyColor myColor) {
        graphics.getGraphicsContext2D().getPixelWriter().setColor(x, y, toColor(myColor));
    }

    private Color toColor(MyColor myColor) {
        return Color.color(myColor.getRed(), myColor.getGreen(), myColor.getBlue());
    }
}
