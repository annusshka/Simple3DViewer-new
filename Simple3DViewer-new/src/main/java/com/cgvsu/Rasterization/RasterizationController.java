package com.cgvsu.Rasterization;

import javafx.fxml.FXML;
import javafx.scene.canvas.Canvas;
import javafx.scene.layout.AnchorPane;

public class RasterizationController {

    @FXML
    AnchorPane anchorPane;
    @FXML
    private Canvas canvas;

    @FXML
    private void initialize() {
        anchorPane.prefWidthProperty().addListener((ov, oldValue, newValue) -> canvas.setWidth(newValue.doubleValue()));
        anchorPane.prefHeightProperty().addListener((ov, oldValue, newValue) -> canvas.setHeight(newValue.doubleValue()));
        GraphicsUtils graphicsUtils = new DrawUtilsJavaFX(canvas);


        Rasterization.fillTriangle(graphicsUtils,
                768, 396, 768, 251.02536, 622.8789, 251.02536,
                MyColor.BLUE, MyColor.BLUE, MyColor.BLUE);

        //Here are some examples of how to work with it
        Rasterization.fillTriangle(graphicsUtils,
                10, 10, 200, 400, 300, 200,
                MyColor.RED, MyColor.GREEN, MyColor.BLUE);
        Rasterization.fillTriangle(graphicsUtils,
                400, 500, 600, 300, 500, 500,
                MyColor.RED, MyColor.GREEN, MyColor.BLUE);
        Rasterization.fillTriangle(graphicsUtils,
                500, 100, 400, 300, 300, 200,
                MyColor.RED, MyColor.GREEN, MyColor.BLUE);
        Rasterization.fillTriangle(graphicsUtils,
                200, 100, 500, 100, 300, 200,
                MyColor.RED, MyColor.GREEN, MyColor.BLUE);
    }

}