package com.cgvsu.rasterizationfxapp;

import com.cgvsu.rasterization.Point;
import com.cgvsu.rasterization.Rasterization;
import javafx.fxml.FXML;
import javafx.scene.canvas.Canvas;
import javafx.scene.layout.AnchorPane;
import javafx.scene.paint.Color;

public class RasterizationController {

    @FXML
    AnchorPane anchorPane;
    @FXML
    private Canvas canvas;

    @FXML
    private void initialize() {
        anchorPane.prefWidthProperty().addListener((ov, oldValue, newValue) -> canvas.setWidth(newValue.doubleValue()));
        anchorPane.prefHeightProperty().addListener((ov, oldValue, newValue) -> canvas.setHeight(newValue.doubleValue()));

        Rasterization.drawFilledEllipseWithInterpolation(canvas.getGraphicsContext2D(), new Point(100, 300), 300, 150, Color.BLUE, Color.RED);
        Rasterization.drawFilledEllipseWithInterpolation(canvas.getGraphicsContext2D(), new Point(100, 100), 100, 50, Color.PINK, Color.GREEN);
        Rasterization.drawFilledEllipseWithInterpolation(canvas.getGraphicsContext2D(), new Point(700, 300), 100, 200, Color.YELLOW, Color.BROWN);
        Rasterization.drawFilledEllipseWithInterpolation(canvas.getGraphicsContext2D(), new Point(450, 80), 250, 60, Color.BLACK, Color.PURPLE);
        Rasterization.drawFilledEllipseWithInterpolation(canvas.getGraphicsContext2D(), new Point(550, 480), 80, 80, Color.RED, Color.BLUE);
        Rasterization.drawFilledEllipseWithInterpolation(canvas.getGraphicsContext2D(), new Point(50, 480), 40, 80, Color.RED, Color.BLUE);
    }

}