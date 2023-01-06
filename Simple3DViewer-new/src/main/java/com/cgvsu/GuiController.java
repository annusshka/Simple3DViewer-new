package com.cgvsu;

import com.cgvsu.math.Math.Vector.Vector3f;
import com.cgvsu.model.TransformedModel;
import com.cgvsu.objwriter.ObjWriter;
import com.cgvsu.render_engine.RenderEngine;
import javafx.collections.FXCollections;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.animation.Animation;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.event.ActionEvent;
import javafx.scene.canvas.Canvas;
import javafx.scene.control.ComboBox;
import javafx.scene.control.SingleSelectionModel;
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
    @FXML
    private ComboBox listOfModels;


    //private TransformedModel transformedModel = null;

    private ArrayList<KeyCode> keyCodes = null;
    private ArrayList<String> modelsNames = new ArrayList<>();



    private Camera camera = new Camera(
            new Vector3f(new float[]{0, 00, 100}),
            new Vector3f(new float[]{0, 0, 0}),
            1.0F, 1, 0.01F, 100);

    private Timeline timeline;

    @FXML
    private void initialize() {
        listOfModels.setLayoutX(400);
        anchorPane.prefWidthProperty().addListener((ov, oldValue, newValue) -> canvas.setWidth(newValue.doubleValue()));
        anchorPane.prefHeightProperty().addListener((ov, oldValue, newValue) -> canvas.setHeight(newValue.doubleValue()));

        timeline = new Timeline();
        timeline.setCycleCount(Animation.INDEFINITE);

        KeyFrame frame = new KeyFrame(Duration.millis(30), event -> {
            double width = canvas.getWidth();
            double height = canvas.getHeight();

            canvas.getGraphicsContext2D().clearRect(0, 0, width, height);
            camera.setAspectRatio((float) (width / height));


            if (models != null) {
                canvas.setOnScroll(this::handleMouseWheelMoved);
                canvas.setOnMousePressed(this::handleMousePressed);
                listOfModels.setItems(FXCollections
                        .observableArrayList(listToArr(modelsNames)));
                RenderEngine.render(canvas.getGraphicsContext2D(), camera, models,
                        (int) width, (int) height);
            }
        });
        timeline.getKeyFrames().add(frame);
        timeline.play();
    }

    FileChooser fileChooser = new FileChooser();

    //возможность сохранить транс и не транс модели
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
            TransformedModel transformedModel = new TransformedModel(mesh);
            transformedModel.setTransformedModel();
            models.add(transformedModel);
            modelsNames.add(file.getName());

            //проверить пустоту
            keyCodes = new ArrayList<KeyCode>();
            // todo: обработка ошибок
        } catch (IOException exception) {

        }
    }

    private Object[] listToArr(ArrayList<String> arrayList) {
        String[] arr = new String[arrayList.size()];
        for (int i = 0; i < arr.length; i++) {
            arr[i] = arrayList.get(i);
        }
        return arr;
    }

    private int getSelectIndex(ComboBox comboBox){
        SingleSelectionModel selectionModel = comboBox.getSelectionModel();
        return selectionModel.getSelectedIndex();
    }

    @FXML
    private void onWriteModelMenuItemClick() throws IOException {
        fileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("Model (*.obj)", "*.obj"));
        fileChooser.setTitle("Save Model");
        fileChooser.setInitialFileName("Saved Model");
        try {
            File file = fileChooser.showSaveDialog((Stage) canvas.getScene().getWindow());
            ObjWriter.writeTransformedModel(file.getAbsolutePath(), models.get(getSelectIndex(listOfModels)));
            // todo: обработка ошибок
        } catch (Exception exception) {

        }
    }

    @FXML
    private void onWriteTransformedModelMenuItemClick() throws IOException {
        fileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("Model (*.obj)", "*.obj"));
        fileChooser.setTitle("Save Model");
        fileChooser.setInitialFileName("Saved Model");
        try {
            File file = fileChooser.showSaveDialog((Stage) canvas.getScene().getWindow());
            ObjWriter.write(file.getAbsolutePath(), models.get(getSelectIndex(listOfModels)).getTransformedModel());
            // todo: обработка ошибок
        } catch (Exception exception) {

        }
    }
    public void onOpenModelFillingPolygons(){
    }

    //камера
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

    //камера
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

    //не менять
    public void handleKeyEvent(KeyEvent e) {
        KeyCode key = e.getCode();
        if (!keyCodes.contains(key)) {
            keyCodes.add(key);
        }
        handleKeys();
    }

    //не менять
    private void handleKeys() {
        if (keyCodes.contains(KeyCode.G)) {
            handleTranslate();
        } else if (keyCodes.contains(KeyCode.E)) {
            handleScale();
        } else if (keyCodes.contains(KeyCode.R)) {
            handleRotate();
        } else {
            handleCameraMove();
        }
    }

    //камера
    public void handleKeyReleased(KeyEvent event) {
        keyCodes.remove(event.getCode());
    }

    //не менять
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

    //не менять
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

    //не менять
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

    //камера
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

    //для актуальной
    @FXML
    public void scaleByX() {
        float scaleParam = models.get(0).getScaleParams().get(0);
        if (scaleParam - 1 <= EPS) {
            models.get(getSelectIndex(listOfModels)).setScaleXParams(scaleParam * SCALE);
        } else {
            models.get(getSelectIndex(listOfModels)).setScaleXParams(SCALE);
        }
    }

    //для актуальной
    @FXML
    public void reduceScaleByX() {
        float scaleParam = models.get(getSelectIndex(listOfModels)).getScaleParams().get(0);
        if (scaleParam - 1 <= EPS) {
            models.get(getSelectIndex(listOfModels)).setScaleXParams(-scaleParam * SCALE);
        } else {
            models.get(getSelectIndex(listOfModels)).setScaleXParams(-SCALE);
        }
    }

    @FXML
    public void scaleByY() {
        float scaleParam = models.get(getSelectIndex(listOfModels)).getScaleParams().get(1);
        if (scaleParam - 1 <= EPS) {
            models.get(getSelectIndex(listOfModels)).setScaleYParams(scaleParam * SCALE);
        } else {
            models.get(getSelectIndex(listOfModels)).setScaleYParams(SCALE);
        }
    }

    @FXML
    public void reduceScaleByY() {
        float scaleParam = models.get(getSelectIndex(listOfModels)).getScaleParams().get(1);
        if (scaleParam - 1 <= EPS) {
            models.get(getSelectIndex(listOfModels)).setScaleYParams(-scaleParam * SCALE);
        } else {
            models.get(getSelectIndex(listOfModels)).setScaleYParams(-SCALE);
        }
    }

    @FXML
    public void scaleByZ() {
        float scaleParam = models.get(getSelectIndex(listOfModels)).getScaleParams().get(2);
        if (scaleParam - 1 <= EPS) {
            models.get(getSelectIndex(listOfModels)).setScaleZParams(scaleParam * SCALE);
        } else {
            models.get(getSelectIndex(listOfModels)).setScaleZParams(SCALE);
        }
    }

    @FXML
    public void reduceScaleByZ() {
        float scaleParam = models.get(getSelectIndex(listOfModels)).getScaleParams().get(2);
        if (scaleParam - 1 <= EPS) {
            models.get(getSelectIndex(listOfModels)).setScaleZParams(-scaleParam * SCALE);
        } else {
            models.get(getSelectIndex(listOfModels)).setScaleZParams(-SCALE);
        }
    }

    @FXML
    public void rotateAroundX() {
        models.get(getSelectIndex(listOfModels)).setRotateXParam(ROTATE_PARAM);
    }

    @FXML
    public void rotateAroundX1() {
        models.get(getSelectIndex(listOfModels)).setRotateXParam(-ROTATE_PARAM);
    }

    @FXML
    public void rotateAroundY() {
        models.get(getSelectIndex(listOfModels)).setRotateYParam(ROTATE_PARAM);
    }

    @FXML
    public void rotateAroundY1() {
        models.get(getSelectIndex(listOfModels)).setRotateYParam(-ROTATE_PARAM);
    }

    @FXML
    public void rotateAroundZ() {
        models.get(getSelectIndex(listOfModels)).setRotateZParam(ROTATE_PARAM);
    }

    @FXML
    public void rotateAroundZ1() {
        models.get(getSelectIndex(listOfModels)).setRotateZParam(-ROTATE_PARAM);
    }

    @FXML
    public void translateX() {
        models.get(getSelectIndex(listOfModels)).setTranslateXParam(TRANSLATION);
    }

    @FXML
    public void translateX1() {
        models.get(getSelectIndex(listOfModels)).setTranslateXParam(-TRANSLATION);
    }

    @FXML
    public void translateY() {
        models.get(getSelectIndex(listOfModels)).setTranslateYParam(TRANSLATION);
    }

    @FXML
    public void translateY1() {
        models.get(getSelectIndex(listOfModels)).setTranslateYParam(-TRANSLATION);
    }

    @FXML
    public void translateZ() {
        models.get(getSelectIndex(listOfModels)).setTranslateZParam(TRANSLATION);
    }

    @FXML
    public void translateZ1() {
        models.get(getSelectIndex(listOfModels)).setTranslateZParam(-TRANSLATION);
    }
}
