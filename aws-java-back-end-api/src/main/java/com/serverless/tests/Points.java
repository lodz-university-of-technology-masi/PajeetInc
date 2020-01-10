package com.serverless.tests;

import java.util.List;

public class Points {

    private double points;
    private List<Boolean> corrects;

    Points() {
    }

    Points(double points, List<Boolean> corrects) {
        this.points = points;
        this.corrects = corrects;
    }

    public double getPoints() {
        return points;
    }

    public List<Boolean> getCorrects() {
        return corrects;
    }
}