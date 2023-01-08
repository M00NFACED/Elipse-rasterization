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
            double localZeroY =  sqrt(height * height * (1 - ((x - centerPoint.getX()) * (x - centerPoint.getX()))
                    / (width * width + 0.0)));
            int startBorder = (int) (-localZeroY + centerPoint.getY() + 0.5);
            int endBorder = (int) (localZeroY + centerPoint.getY() + 0.5);
            for (int y = startBorder; y < endBorder; y++) {
                pixelWriter.setColor(x, y, calculateNewColor(centralColor, borderColor, new Point(x, y), centerPoint, findPointOnEllipse(new Point(x, y), centerPoint, width, height)));
            }
        }
    }


    public static double distanceBetweenTwoPoints(Point point1, Point point2) {
        return Math.sqrt((point1.getX() - point2.getX()) * (point1.getX() - point2.getX()) + (point1.getY() - point2.getY()) * (point1.getY() - point2.getY()));
    }

    private static Color calculateNewColor(Color centralColor, Color borderColor, Point currentPoint, Point centralPoint, Point borderPoint) {
        double centralColorRed = centralColor.getRed();
        double centralColorGreen = centralColor.getGreen();
        double centralColorBlue = centralColor.getBlue();
        double borderColorRed = borderColor.getRed();
        double borderColorGreen = borderColor.getGreen();
        double borderColorBlue = borderColor.getBlue();
        double distanceBetweenCentralAndBorderPoints = distanceBetweenTwoPoints(centralPoint, borderPoint);
        double distanceBetweenCentralAndCurrentPoints = distanceBetweenTwoPoints(centralPoint, currentPoint);
        double distanceBetweenCentralAndCurrentPointsInPercent = distanceBetweenCentralAndCurrentPoints / distanceBetweenCentralAndBorderPoints;
        double newColorRed = centralColorRed + distanceBetweenCentralAndCurrentPointsInPercent * (borderColorRed - centralColorRed);
        double newColorGreen = centralColorGreen + distanceBetweenCentralAndCurrentPointsInPercent * (borderColorGreen - centralColorGreen);
        double newColorBlue = centralColorBlue + distanceBetweenCentralAndCurrentPointsInPercent * (borderColorBlue - centralColorBlue);



        return new Color(newColorRed, newColorGreen, newColorBlue, 1.0);

    }

//   Расчет взят отсюда: https://ip76.ru/theory-and-practice/inellipse-line/

    private static Point findPointOnEllipse(Point currentPoint, Point centralPoint, int width, int height) {
        Point crossingPoint = new Point(0, 0);
        if (currentPoint.getX() - centralPoint.getX() != 0) {
            /*
            Выразим Y из уравнения прямой:
            y = (x - x0) * (y2 - y0) / (x2 - x0) + y0
            Уравнение прямой в своем стандартном варианте имеет вид:
            y = k * x + b
            Таким образом, имеем следующие коэффициенты:
            */

            double k = (double) (currentPoint.getY() - centralPoint.getY()) / (currentPoint.getX() - centralPoint.getX());
            double b = centralPoint.getY() - k * centralPoint.getX();

            /*
            Подготовим уравнение эллипса. В таком виде, как выше, оно не сильно облегчит работу,
            поэтому, путем несложных манипуляций, приведем его к виду:
            height^2 * (x - x0)^2 + width^2 * (y - y0)^2 - width^2 * height^2 = 0

            Подставим y в модифицированное уравнение эллипса:
            height^2 * (x - x0)^2 + width^2 * (k * x + b - y0)^2 - width^2 * height^2 = 0

            Для упрощения вида уравнения и себе жизни введем пару констант:
            */

            double S = b - centralPoint.getY();
            double V = (double) width * width * height * height;
            /*
            Уравнение приобретает некую законченность и воздушность восприятия:
            height^2 * (x - x0)^2 + width^2 * (k * x + S)^2 - V = 0

            Путем таких же несложных преобразований приходим к следующему виду:
            x^2 * (height^2 + width^2 * k^2) + x * 2 *(width^2 * k * S - height^2 * x0) +
            + (height^2 * x0^2 + width^2 * S^2 - V) = 0

            это обычное квадратное уравнение, у которого следующие коэффициенты:
            */

            double A = height * height + width * width * k * k;
            double B = 2 * (width * width * k * S - height * height * centralPoint.getX());
            double C = (double) height * height * centralPoint.getX() * centralPoint.getX() + width * width * S * S - V;

            double D = B * B - 4 * A * C; //Дискриминант
            double x1 = (-B + sqrt(D)) / (2 * A);
            double x2 = (-B - sqrt(D)) / (2 * A);
            double y1 = k * x1 + b;
            double y2 = k * x2 + b;
            if (distanceBetweenTwoPoints(currentPoint, new Point((int) (x1 + 0.5), (int) (y1+0.5))) < distanceBetweenTwoPoints(currentPoint, new Point((int) (x2 + 0.5), (int) (y1+0.5)))) {
                crossingPoint.setX((int) (x1 + 0.5));
                crossingPoint.setY((int) (y1 + 0.5));
            } else {
                crossingPoint.setX((int) Math.round(x2));
                crossingPoint.setY((int) Math.round(y2));
            }
        } else {
            /*При строго (или почти) вертикальной линии значение X нам известно.
            Осталось найти Y. Снова берем уравнение эллипса*/
            double W = height * height;
            double A = 1;
            double B = -2 * centralPoint.getY();
            double C = centralPoint.getY() * centralPoint.getY() - W;
            double D = B * B - 4 * A * C;
            int y1 = (int) ((-B + sqrt(D)) / (2 * A) + 0.5);
            int y2 = (int) ((-B - sqrt(D)) / (2 * A) + 0.5);
            crossingPoint.setX(currentPoint.getX());
            if (distanceBetweenTwoPoints(currentPoint, new Point(currentPoint.getX(), y1)) < distanceBetweenTwoPoints(currentPoint, new Point(currentPoint.getX(), y2))) {
                crossingPoint.setY(y1);
            } else {
                crossingPoint.setY(y2);
            }
        }

        return crossingPoint;
    }

}
