package com.bms.triangle;

import com.bms.triangle.classification.InvalidTriangleException;
import com.bms.triangle.classification.Triangle;
import com.bms.triangle.classification.TriangleType;
import com.bms.triangle.input.InvalidInputException;
import com.bms.triangle.input.SideLengths;
import com.bms.triangle.input.SideLengthsParser;

public final class TriangleClassifierApplication {

    private static final String USAGE = "Usage: java -jar triangle-classifier.jar <side1> <side2> <side3>";

    private TriangleClassifierApplication() {
    }

    public static void main(String[] arguments) {
        try {
            SideLengths sideLengths = SideLengthsParser.parse(arguments);
            Triangle triangle = new Triangle(sideLengths.firstSide(), sideLengths.secondSide(), sideLengths.thirdSide());
            TriangleType triangleType = triangle.classify();

            System.out.println(triangleType.displayName());

        } catch (InvalidInputException exception) {
            System.err.println("Error: " + exception.getMessage());
            System.err.println(USAGE);
            System.exit(1);
        } catch (InvalidTriangleException exception) {
            System.err.println("Error: " + exception.getMessage());
            System.exit(1);
        }
    }
}
