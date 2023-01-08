package com.cgvsu.rasterization;

public class Point {
    private int x;
    private int y;

    public Point(int x, int y) {
        this.x = x;
        this.y = y;
    }

    public int getX() {
        return x;
    }

    public int getY() {
        return y;
    }

    public void setX(int x) {// Здесь мы используем методы построения и установки, чтобы облегчить написание позже
        this.x = x;
    }

    public void setY(int y) {
        this.y = y;
    }
}