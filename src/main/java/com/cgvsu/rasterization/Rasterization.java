package com.cgvsu.rasterization;

import javafx.scene.canvas.GraphicsContext;
import javafx.scene.image.PixelWriter;
import javafx.scene.paint.Color;

import static java.lang.Math.sqrt;

public class Rasterization {

    //  Метод отрисовки эллипса
    public static void drawFilledEllipseWithInterpolation
    (GraphicsContext gc, Point centerPoint, int width, int height, Color centralColor, Color borderColor) {
        PixelWriter pixelWriter = gc.getPixelWriter();
        for (int x = centerPoint.getX() - width; x < centerPoint.getX() + width; x++) {
            double localZeroY = Math.sqrt(height * height * (1 - ((x - centerPoint.getX()) * (x - centerPoint.getX()))
                    / (width * width + 0.0)));
            int startBorder = (int) (-localZeroY + centerPoint.getY() + 0.5);
            int endBorder = (int) (localZeroY + centerPoint.getY() + 0.5);
            for (int y = startBorder; y < endBorder; y++) {
                pixelWriter.setColor(x, y, calcColor(centralColor, borderColor, new Point(x, y), centerPoint, findPointOnEllipse(new Point(x, y), centerPoint, width, height)));
            }
        }
    }

/*
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
*/

    public static double distanceBetweenTwoPoints(Point point1, Point point2) {
        return Math.sqrt((point1.getX() - point2.getX()) * (point1.getX() - point2.getX()) + (point1.getY() - point2.getY()) * (point1.getY() - point2.getY()));
    }

    private static Color calcColor(Color centralColor, Color borderColor, Point currentPoint, Point centralPoint, Point borderPoint) {
        double centralColorRed = centralColor.getRed();
        double centralColorGreen = centralColor.getGreen();
        double centralColorBlue = centralColor.getBlue();
        double borderColorRed = borderColor.getRed();
        double borderColorGreen = borderColor.getGreen();
        double borderColorBlue = borderColor.getBlue();

        double newColorRed = centralColorRed + distanceBetweenTwoPoints(centralPoint, currentPoint) / distanceBetweenTwoPoints(centralPoint, borderPoint) * (borderColorRed - centralColorRed);
        double newColorGreen = centralColorGreen + distanceBetweenTwoPoints(centralPoint, currentPoint) / distanceBetweenTwoPoints(centralPoint, borderPoint) * (borderColorGreen - centralColorGreen);
        double newColorBlue = centralColorBlue + distanceBetweenTwoPoints(centralPoint, currentPoint) / distanceBetweenTwoPoints(centralPoint, borderPoint) * (borderColorBlue - centralColorBlue);


        if (newColorRed < 0 || newColorRed > 1 || newColorGreen < 0 || newColorGreen > 1 || newColorBlue < 0 || newColorBlue > 1) {
            System.out.println("Test");
        }
        return new Color(newColorRed, newColorGreen, newColorBlue, 1.0);

    }

//   Расчет взят отсюда: https://ip76.ru/theory-and-practice/inellipse-line/

    private static Point findPointOnEllipse(Point currentPoint, Point centralPoint, int width, int height) {
        Point crossingPoint = new Point(0, 0);
        if (currentPoint.getX() - centralPoint.getX() != 0) {
            double k = (double) (currentPoint.getY() - centralPoint.getY()) / (currentPoint.getX() - centralPoint.getX());
            double b = centralPoint.getY() - k * centralPoint.getX();
            double S = b - centralPoint.getY();
            double V = (double) width * width * height * height;
            double A;
            A = height * height + width * width * k * k;
            double B;
            B = 2 * (width * width * k * S - height * height * centralPoint.getX());
            double C;
            C = (double) height * height * centralPoint.getX() * centralPoint.getX() + width * width * S * S - V;
            double D;
            D = B * B - 4 * A * C;
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
            int y1 = (int) ((-B + sqrt(D)) / (2 * A)+0.5);
            int y2 = (int) ((-B - sqrt(D)) / (2 * A)+0.5);
            crossingPoint.setX(currentPoint.getX());
            if (distanceBetweenTwoPoints(currentPoint, new Point(currentPoint.getX(),y1)) < (sqrt((y2 - currentPoint.getY()) * (y2 - currentPoint.getY())))) {
                crossingPoint.setY((int) (y1 + 0.5));
            } else {
                crossingPoint.setY((int) (y2 + 0.5));
            }
        }

        return crossingPoint;
    }

}
