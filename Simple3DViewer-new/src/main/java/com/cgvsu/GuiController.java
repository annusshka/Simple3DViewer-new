package com.cgvsu;

import com.cgvsu.math.Math.Vector.Vector3f;
import com.cgvsu.model.TransformedModel;
import com.cgvsu.objwriter.ObjWriter;
import com.cgvsu.render_engine.RenderEngine;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.animation.Animation;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.event.ActionEvent;
import javafx.scene.canvas.Canvas;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.ScrollEvent;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Stage;
import javafx.stage.FileChooser;
import javafx.util.Duration;

import java.awt.event.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.io.IOException;
import java.io.File;
import java.util.ArrayList;

import com.cgvsu.model.Model;
import com.cgvsu.objreader.ObjReader;
import com.cgvsu.render_engine.Camera;

public class GuiController {
    private final static boolean willItWriteInformationToConsole = true;

    final private float TRANSLATION = 1F;

    final private float SCALE = 0.05F;

    final private float ROTATE_PARAM = 1F;

    static final float EPS = 1e-6f;

    @FXML
    AnchorPane anchorPane;

    @FXML
    private Canvas canvas;

    private ArrayList<TransformedModel> models = new ArrayList<>();

    private TransformedModel transformedModel = null;

    private ArrayList<KeyCode> keyCodes = null;

    private Camera camera = new Camera(
            new Vector3f(new float[]{0, 00, 100}),
            new Vector3f(new float[]{0, 0, 0}),
            1.0F, 1, 0.01F, 100);

    private Timeline timeline;

    @FXML
    private void initialize() {
        anchorPane.prefWidthProperty().addListener((ov, oldValue, newValue) -> canvas.setWidth(newValue.doubleValue()));
        anchorPane.prefHeightProperty().addListener((ov, oldValue, newValue) -> canvas.setHeight(newValue.doubleValue()));

        timeline = new Timeline();
        timeline.setCycleCount(Animation.INDEFINITE);

        KeyFrame frame = new KeyFrame(Duration.millis(30), event -> {
            double width = canvas.getWidth();
            double height = canvas.getHeight();

            canvas.getGraphicsContext2D().clearRect(0, 0, width, height);
            camera.setAspectRatio((float) (width / height));

            if (transformedModel != null) {
                canvas.setOnScroll(this::handleMouseWheelMoved);
                canvas.setOnMousePressed(this::handleMousePressed);
                RenderEngine.render(canvas.getGraphicsContext2D(), camera, models,
                        (int) width, (int) height);
            }
        });
        timeline.getKeyFrames().add(frame);
        timeline.play();
    }

    FileChooser fileChooser = new FileChooser();

    @FXML
    private void onOpenModelMenuItemClick() {
        FileChooser fileChooser = new FileChooser();
        fileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("Model (*.obj)", "*.obj"));
        fileChooser.setTitle("Load Model");

        File file = fileChooser.showOpenDialog((Stage) canvas.getScene().getWindow());
        if (file == null) {
            return;
        }

        Path fileName = Path.of(file.getAbsolutePath());

        try {
            String fileContent = Files.readString(fileName);
            Model mesh = ObjReader.read(fileContent, willItWriteInformationToConsole);
            transformedModel = new TransformedModel(mesh);
            transformedModel.setTransformedModel();
            models.add(transformedModel);
            keyCodes = new ArrayList<KeyCode>();
            // todo: обработка ошибок
        } catch (IOException exception) {

        }
    }

    @FXML
    private void onWriteModelMenuItemClick() throws IOException {
        fileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("Model (*.obj)", "*.obj"));
        fileChooser.setTitle("Save Model");
        fileChooser.setInitialFileName("Saved Model");
        try {
            File file = fileChooser.showSaveDialog((Stage) canvas.getScene().getWindow());
            ObjWriter.writeTransformedModel(file.getAbsolutePath(), models.get(models.size() - 1));
            // todo: обработка ошибок
        } catch (Exception exception) {

        }
    }
    public void onOpenModelFillingPolygons(){
    }

    private void handleMouseWheelMoved(ScrollEvent event) {
        final double notches = event.getDeltaY();
        final float x = camera.getPosition().get(0);
        final float y = camera.getPosition().get(1);
        final float z = camera.getPosition().get(2);
        final float signX = x < 0 ? 1 : -1;
        final float signY = y < 0 ? 1 : -1;
        final float signZ = z < 0 ? 1 : -1;
        final float max = Math.max(Math.max(Math.abs(x), Math.abs(y)), Math.abs(z));

        if (notches > 0) {
            if (max - x < EPS) {
                camera.movePosition(new Vector3f(new float[]{signX * TRANSLATION, 0, 0}));
            } else if (max - y < EPS) {
                camera.movePosition(new Vector3f(new float[]{0, signY * TRANSLATION, 0}));
            } else {
                camera.movePosition(new Vector3f(new float[]{0, 0, signZ * TRANSLATION}));
            }
        } else {
            if (max - x < EPS) {
                camera.movePosition(new Vector3f(new float[]{-signX * TRANSLATION, 0, 0}));
            } else if (max - y < EPS) {
                camera.movePosition(new Vector3f(new float[]{0, -signY * TRANSLATION, 0}));
            } else {
                camera.movePosition(new Vector3f(new float[]{0, 0, -signZ * TRANSLATION}));
            }
        }
    }

    private void handleMousePressed(javafx.scene.input.MouseEvent event) {
        var ref = new Object() {
            float prevX = (float) event.getX();
            float prevY = (float) event.getY();
        };
        canvas.setOnMouseDragged(mouseEvent -> {
            final float actualX = (float) mouseEvent.getX();
            final float actualY = (float) mouseEvent.getY();
            float dx = ref.prevX - actualX;
            final float dy = actualY - ref.prevY;
            final float dxy = Math.abs(dx) - Math.abs(dy);
            float dz = (float) Math.sqrt(Math.pow(dx, 2) + Math.pow(dy, 2));

            if (dxy >= EPS && (camera.getPosition().get(0) <= EPS && dx < 0 ||
                    camera.getPosition().get(0) > EPS && dx > 0)) {
                dz *= -1;
            } else if (dxy < EPS) { //если больше перемещаем по y, то по z не перемещаем
                dz = 0;
            }
            if (camera.getPosition().get(2) <= EPS) {
                dx *= -1;
            }

            ref.prevX = actualX;
            ref.prevY = actualY;
            camera.movePosition(new Vector3f(new float[]{dx * 0.01f, dy * 0.01f, dz * 0.01f}));
        });
    }

    public void handleKeyEvent(KeyEvent e) {
        KeyCode key = e.getCode();
        if (!keyCodes.contains(key)) {
            keyCodes.add(key);
        }
        handleKeys();
    }

    private void handleKeys() {
        if (keyCodes.contains(KeyCode.G)) {
            handleTranslate();
        } else if (keyCodes.contains(KeyCode.E)) {
            canvas.setOnScroll(this::handleMouseWheelMoved);
            handleScale();
        } else if (keyCodes.contains(KeyCode.R)) {
            handleRotate();
        } else {
            handleCameraMove();
        }
    }

    public void handleKeyReleased(KeyEvent event) {
        keyCodes.remove(event.getCode());
    }

    private void handleTranslate() {
        if (keyCodes.contains(KeyCode.D)) {
            translateX1();
        } else if (keyCodes.contains(KeyCode.A)) {
            translateX();
        }
        if (keyCodes.contains(KeyCode.W)) {
            translateY();
        } else if (keyCodes.contains(KeyCode.S)) {
            translateY1();
        }
        if (keyCodes.contains(KeyCode.UP)) {
            translateZ();
        } else if (keyCodes.contains(KeyCode.DOWN)) {
            translateZ1();
        }
    }

    private void handleScale() {
        if (keyCodes.contains(KeyCode.D)) {
            scaleByX();
        } else if (keyCodes.contains(KeyCode.A)) {
            reduceScaleByX();
        }
        if (keyCodes.contains(KeyCode.W)) {
            scaleByY();
        } else if (keyCodes.contains(KeyCode.S)) {
            reduceScaleByY();
        }
        if (keyCodes.contains(KeyCode.UP)) {
            scaleByZ();
        } else if (keyCodes.contains(KeyCode.DOWN)) {
            reduceScaleByZ();
        }
    }

    private void handleRotate() {
        if (keyCodes.contains(KeyCode.W)) {
            rotateAroundX();
        }
        if (keyCodes.contains(KeyCode.D)) {
            rotateAroundY();
        }
        if (keyCodes.contains(KeyCode.DOWN)) {
            rotateAroundZ();
        }
        if (keyCodes.contains(KeyCode.S)) {
            rotateAroundX1();
        }
        if (keyCodes.contains(KeyCode.A)) {
            rotateAroundY1();
        }
        if (keyCodes.contains(KeyCode.UP)) {
            rotateAroundZ1();
        }
    }

    private void handleCameraMove() {
        if (keyCodes.contains(KeyCode.W)) {
            handleCameraUp();
        } else if (keyCodes.contains(KeyCode.S)) {
            handleCameraDown();
        }
        if (keyCodes.contains(KeyCode.D)) {
            handleCameraRight();
        } else if (keyCodes.contains(KeyCode.A)) {
            handleCameraLeft();
        }
        if (keyCodes.contains(KeyCode.UP)) {
            handleCameraForward();
        } else if (keyCodes.contains(KeyCode.DOWN)) {
            handleCameraBackward();
        }
    }

    // 9 параметров
    @FXML
    public void handleCameraForward() {
        camera.movePosition(new Vector3f(new float[]{0, 0, -TRANSLATION}));
    }

    @FXML
    public void handleCameraBackward() {
        camera.movePosition(new Vector3f(new float[]{0, 0, TRANSLATION}));
    }

    @FXML
    public void handleCameraLeft() {
        camera.movePosition(new Vector3f(new float[]{TRANSLATION, 0, 0}));
    }

    @FXML
    public void handleCameraRight() {
        camera.movePosition(new Vector3f(new float[]{-TRANSLATION, 0, 0}));
    }

    @FXML
    public void handleCameraUp() {
        camera.movePosition(new Vector3f(new float[]{0, TRANSLATION, 0}));
    }

    @FXML
    public void handleCameraDown() {
        camera.movePosition(new Vector3f(new float[]{0, -TRANSLATION, 0}));
    }

    @FXML
    public void scaleByX() {
        float scaleParam = transformedModel.getScaleParams().get(0);
        if (scaleParam - 1 <= EPS) {
            transformedModel.setScaleXParams(scaleParam * SCALE);
        } else {
            transformedModel.setScaleXParams(SCALE);
        }
    }

    @FXML
    public void reduceScaleByX() {
        float scaleParam = transformedModel.getScaleParams().get(0);
        if (scaleParam - 1 <= EPS) {
            transformedModel.setScaleXParams(-scaleParam * SCALE);
        } else {
            transformedModel.setScaleXParams(-SCALE);
        }
    }

    @FXML
    public void scaleByY() {
        float scaleParam = transformedModel.getScaleParams().get(1);
        if (scaleParam - 1 <= EPS) {
            transformedModel.setScaleYParams(scaleParam * SCALE);
        } else {
            transformedModel.setScaleYParams(SCALE);
        }
    }

    @FXML
    public void reduceScaleByY() {
        float scaleParam = transformedModel.getScaleParams().get(1);
        if (scaleParam - 1 <= EPS) {
            transformedModel.setScaleYParams(-scaleParam * SCALE);
        } else {
            transformedModel.setScaleYParams(-SCALE);
        }
    }

    @FXML
    public void scaleByZ() {
        float scaleParam = transformedModel.getScaleParams().get(2);
        if (scaleParam - 1 <= EPS) {
            transformedModel.setScaleZParams(scaleParam * SCALE);
        } else {
            transformedModel.setScaleZParams(SCALE);
        }
    }

    @FXML
    public void reduceScaleByZ() {
        float scaleParam = transformedModel.getScaleParams().get(2);
        if (scaleParam - 1 <= EPS) {
            transformedModel.setScaleZParams(-scaleParam * SCALE);
        } else {
            transformedModel.setScaleZParams(-SCALE);
        }
    }

    @FXML
    public void rotateAroundX() {
        transformedModel.setRotateXParam(ROTATE_PARAM);
    }

    @FXML
    public void rotateAroundX1() {
        transformedModel.setRotateXParam(-ROTATE_PARAM);
    }

    @FXML
    public void rotateAroundY() {
        transformedModel.setRotateYParam(ROTATE_PARAM);
    }

    @FXML
    public void rotateAroundY1() {
        transformedModel.setRotateYParam(-ROTATE_PARAM);
    }

    @FXML
    public void rotateAroundZ() {
        transformedModel.setRotateZParam(ROTATE_PARAM);
    }

    @FXML
    public void rotateAroundZ1() {
        transformedModel.setRotateZParam(-ROTATE_PARAM);
    }

    @FXML
    public void translateX() {
        transformedModel.setTranslateXParam(TRANSLATION);
    }

    @FXML
    public void translateX1() {
        transformedModel.setTranslateXParam(-TRANSLATION);
    }

    @FXML
    public void translateY() {
        transformedModel.setTranslateYParam(TRANSLATION);
    }

    @FXML
    public void translateY1() {
        transformedModel.setTranslateYParam(-TRANSLATION);
    }

    @FXML
    public void translateZ() {
        transformedModel.setTranslateZParam(TRANSLATION);
    }

    @FXML
    public void translateZ1() {
        transformedModel.setTranslateZParam(-TRANSLATION);
    }
}