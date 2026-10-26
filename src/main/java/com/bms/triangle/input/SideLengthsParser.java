package com.bms.triangle.input;

import java.math.BigDecimal;
import java.util.regex.Pattern;

public final class SideLengthsParser {

    private static final int EXPECTED_ARGUMENT_COUNT = 3;
    private static final int MAXIMUM_ARGUMENT_LENGTH = 100;

    // Plain decimal notation only: no exponent (e.g. 1e9), no comma, ASCII digits only.
    private static final Pattern PLAIN_DECIMAL_NUMBER = Pattern.compile("[+-]?\\d+(\\.\\d+)?");

    private SideLengthsParser() {
    }

    public static SideLengths parse(String[] arguments) {
        if (arguments == null || arguments.length != EXPECTED_ARGUMENT_COUNT) {
            int receivedCount = arguments == null ? 0 : arguments.length;
            throw new InvalidInputException(
                    "Expected exactly " + EXPECTED_ARGUMENT_COUNT + " side lengths but received " + receivedCount + ".");
        }

        return new SideLengths(
                parseSideLength(arguments[0], 1),
                parseSideLength(arguments[1], 2),
                parseSideLength(arguments[2], 3));
    }

    private static BigDecimal parseSideLength(String argument, int position) {
        // Checked before parsing: building a BigDecimal from hundreds of thousands of digits takes seconds.
        // The argument is not echoed back, as it can be arbitrarily long.
        if (argument != null && argument.length() > MAXIMUM_ARGUMENT_LENGTH) {
            throw new InvalidInputException("Side length " + position + " is longer than " + MAXIMUM_ARGUMENT_LENGTH
                    + " characters (" + argument.length() + " characters).");
        }
        if (argument == null || !PLAIN_DECIMAL_NUMBER.matcher(argument).matches()) {
            throw new InvalidInputException("Side length " + position + " is not a valid number: '" + argument
                    + "'. Use plain decimal notation with a dot as decimal separator, e.g. 3.5.");
        }
        return new BigDecimal(argument);
    }
}
