package com.cgvsu.Rasterization;


import java.awt.*;

public class DrawUtilsSwing extends GraphicsUtils<Graphics> {


    public DrawUtilsSwing(Graphics graphics) {
        super(graphics);
    }

    @Override
    public void setPixel(int x, int y, MyColor myColor) {
        graphics.setColor(toColor(myColor));
        graphics.drawLine(x, y, x, y);
    }

    private Color toColor(MyColor myColor) {
        return new Color((int) (255 * myColor.getRed()), (int) (255 * myColor.getGreen()), (int) (255 * myColor.getBlue()));
    }


}