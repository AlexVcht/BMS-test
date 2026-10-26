package com.example.triangle.classification;

public enum TriangleType {
    EQUILATERAL("Equilateral"),
    ISOSCELES("Isosceles"),
    SCALENE("Scalene");

    private final String displayName;

    TriangleType(String displayName) {
        this.displayName = displayName;
    }

    public String displayName() {
        return displayName;
    }
}
