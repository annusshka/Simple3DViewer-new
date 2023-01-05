package com.cgvsu;

import com.cgvsu.model.Model;
import com.cgvsu.objreader.NormalUtils;
import com.cgvsu.objreader.ObjReader;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Stage;

import java.io.IOException;
import java.util.Objects;

public class Simple3DViewer extends Application {

    @Override
    public void start(Stage stage) throws IOException {
        //AnchorPane viewport = FXMLLoader.load(Objects.requireNonNull(getClass().getResource("fxml/gui.fxml")));
        FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("fxml/gui.fxml"));
        AnchorPane viewport = fxmlLoader.load();
        //AnchorPane viewport = FXMLLoader.load(getClass().getResource("fxml/gui.fxml"));

        Scene scene = new Scene(viewport);

        GuiController controller = fxmlLoader.getController();
        scene.setOnKeyPressed(controller::handleKeyEvent);
        scene.setOnKeyReleased(controller::handleKeyReleased);

        stage.setWidth(800);
        stage.setHeight(600);
        viewport.prefWidthProperty().bind(scene.widthProperty());
        viewport.prefHeightProperty().bind(scene.heightProperty());

        stage.setTitle("Simple3DViewer");
        stage.setScene(scene);
        stage.show();
    }

    public static void main(String[] args) {
        launch();
    }
}