package com.example.triangle;

import com.example.triangle.input.InvalidInputException;
import com.example.triangle.input.SideLengths;
import com.example.triangle.input.SideLengthsParser;

public final class TriangleClassifierApplication {

    private static final String USAGE = "Usage: java -jar triangle-classifier.jar <side1> <side2> <side3>";

    private TriangleClassifierApplication() {
    }

    public static void main(String[] arguments) {
        try {
            SideLengths sideLengths = SideLengthsParser.parse(arguments);

            System.out.println("Side lengths: "
                    + sideLengths.firstSide().toPlainString() + ", "
                    + sideLengths.secondSide().toPlainString() + ", "
                    + sideLengths.thirdSide().toPlainString());

        } catch (InvalidInputException exception) {
            System.err.println("Error: " + exception.getMessage());
            System.err.println(USAGE);
            System.exit(1);
        }
    }
}
