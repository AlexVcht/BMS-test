package com.bms.triangle;

import com.bms.triangle.classification.InvalidTriangleException;
import com.bms.triangle.classification.Triangle;
import com.bms.triangle.classification.TriangleType;
import com.bms.triangle.input.InvalidInputException;
import com.bms.triangle.input.SideLengths;
import com.bms.triangle.input.SideLengthsParser;

import java.io.PrintStream;

public final class TriangleClassifierApplication {

    static final int SUCCESS_EXIT_CODE = 0;
    static final int ERROR_EXIT_CODE = 1;

    private static final String USAGE = "Usage: java -jar triangle-classifier.jar <side1> <side2> <side3>";

    private TriangleClassifierApplication() {
    }

    public static void main(String[] arguments) {
        System.exit(run(arguments, System.out, System.err));
    }

    // Separated from main so the printed output and exit code can be tested without exiting the JVM.
    static int run(String[] arguments, PrintStream output, PrintStream error) {
        try {
            SideLengths sideLengths = SideLengthsParser.parse(arguments);
            Triangle triangle = new Triangle(sideLengths.firstSide(), sideLengths.secondSide(), sideLengths.thirdSide());
            TriangleType triangleType = triangle.classify();

            output.println(triangleType.displayName());
            return SUCCESS_EXIT_CODE;

        } catch (InvalidInputException exception) {
            error.println("Error: " + exception.getMessage());
            error.println(USAGE);
            return ERROR_EXIT_CODE;
        } catch (InvalidTriangleException exception) {
            error.println("Error: " + exception.getMessage());
            return ERROR_EXIT_CODE;
        }
    }
}
