package com.cgvsu.rasterization;

import javafx.scene.canvas.GraphicsContext;
import javafx.scene.image.PixelWriter;
import javafx.scene.paint.Color;

import static java.lang.Math.abs;
import static java.lang.Math.sqrt;

public class Rasterization {

    //  Метод отрисовки эллипса
    public static void drawEllipse(final GraphicsContext graphicsContext,
                                   final Point centralPoint,
                                   final int width,
                                   final int height,
                                   Color centralColor,
                                   Color borderColor) {
        final PixelWriter pixelWriter = graphicsContext.getPixelWriter();
        int x1, x2, x;
        int z = 1;
        Color currentColor = centralColor;
        for (int i = centralPoint.getY() - height; i <= centralPoint.getY() + height; i++, z++) {
            x = (int) Math.sqrt(width * width * (1 - (double) (i - centralPoint.getY()) * (double) (i - centralPoint.getY()) / (double) height / (double) height));
            x1 = centralPoint.getX() - x;
            x2 = centralPoint.getX() + x;
            for (int j = x1; j <= x2; j++) {

                pixelWriter.setColor(j, i, currentColor);
                currentColor = calcColor(centralColor,
                        borderColor,
                        new Point(j, i),
                        centralPoint,
                        findPointOnEllipse(new Point(j, i), centralPoint, width, height),
                        width);

            }
        }
        pixelWriter.setColor(centralPoint.getX() + 1,
                centralPoint.getY(),
                calcColor(centralColor,
                        borderColor,
                        new Point(centralPoint.getX() + 1, centralPoint.getY()),
                        centralPoint,
                        findPointOnEllipse(new Point(centralPoint.getX() + 1, centralPoint.getY()),
                                centralPoint,
                                width,
                                height),
                        width));
    }


    private static Color calcColor(Color centralColor, Color borderColor, Point currentPoint, Point centralPoint, Point borderPoint, int width) {
        double cRed = toRGB(centralColor.getRed());
        double cGreen = toRGB(centralColor.getGreen());
        double cBlue = toRGB(centralColor.getBlue());
        double bRed = toRGB(borderColor.getRed());
        double bGreen = toRGB(borderColor.getGreen());
        double bBlue = toRGB(borderColor.getBlue());
        int newRed;
        int newGreen;
        int newBlue;
        if (abs(currentPoint.getX() - centralPoint.getX()) > width / 4 || abs(currentPoint.getY() - centralPoint.getY()) < 1) {

            newRed = (int) (cRed + (currentPoint.getX() - centralPoint.getX()) * (bRed - cRed) / (borderPoint.getX() - centralPoint.getX()) + 0.5);
            newGreen = (int) (cGreen + (currentPoint.getX() - centralPoint.getX()) * (bGreen - cGreen) / (borderPoint.getX() - centralPoint.getX()) + 0.5);
            newBlue = (int) (cBlue + (currentPoint.getX() - centralPoint.getX()) * (bBlue - cBlue) / (borderPoint.getX() - centralPoint.getX()) + 0.5);


        } else {
            newRed = (int) (cRed + (currentPoint.getY() - centralPoint.getY()) * (bRed - cRed) / (borderPoint.getY() - centralPoint.getY()) + 0.5);
            newGreen = (int) (cGreen + (currentPoint.getY() - centralPoint.getY()) * (bGreen - cGreen) / (borderPoint.getY() - centralPoint.getY()) + 0.5);
            newBlue = (int) (cBlue + (currentPoint.getY() - centralPoint.getY()) * (bBlue - cBlue) / (borderPoint.getY() - centralPoint.getY()) + 0.5);
        }

        return Color.rgb(newRed, newGreen, newBlue);

    }

    private static int toRGB(double c) {
        return (int) (c * 186);
    }

//   Расчет взят отсюда: https://ip76.ru/theory-and-practice/inellipse-line/

    private static Point findPointOnEllipse(Point currentPoint, Point centralPoint, int width, int height) {
        Point crossingPoint = new Point(0, 0);
        if (currentPoint.getX() - centralPoint.getX() != 0) {
            double k = (double) (currentPoint.getY() - centralPoint.getY()) / (currentPoint.getX() - centralPoint.getX());
            double b = centralPoint.getY() - k * centralPoint.getX();
            double S = b - centralPoint.getY();
            double V = (double) width * width * height * height;
            double A = height * height + width * width * k * k;
            double B = 2 * (width * width * k * S - height * height * centralPoint.getX());
            double C = (double) height * height * centralPoint.getX() * centralPoint.getX() + width * width * S * S - V;
            double D = B * B - 4 * A * C;
            double x1 = (-B + sqrt(D)) / (2 * A);
            double x2 = (-B - sqrt(D)) / (2 * A);
            double y1 = k * x1 + b;
            double y2 = k * x2 + b;
            if (sqrt((x1 - currentPoint.getX()) * (x1 - currentPoint.getX()) + (y1 - currentPoint.getY()) * (y1 - currentPoint.getY())) < (sqrt((x2 - currentPoint.getX()) * (x2 - currentPoint.getX()) + (y2 - currentPoint.getY()) * (y2 - currentPoint.getY())))) {
                crossingPoint.setX((int) (x1 + 0.5));
                crossingPoint.setY((int) (y1 + 0.5));
            } else {
                crossingPoint.setX((int) (x2 + 0.5));
                crossingPoint.setY((int) (y2 + 0.5));
            }
        } else {
            double W = height * height;
            double A = 1;
            double B = -2 * centralPoint.getY();
            double C = centralPoint.getY() * centralPoint.getY() - W;
            double D = B * B - 4 * A * C;
            double y1 = (-B + sqrt(D)) / (2 * A);
            double y2 = (-B - sqrt(D)) / (2 * A);
            crossingPoint.setX(currentPoint.getX());
            if (sqrt((y1 - currentPoint.getY()) * (y1 - currentPoint.getY())) < (sqrt((y2 - currentPoint.getY()) * (y2 - currentPoint.getY())))) {
                crossingPoint.setY((int) (y1 + 0.5));
            } else {
                crossingPoint.setY((int) (y2 + 0.5));
            }
        }

        return crossingPoint;
    }

}
