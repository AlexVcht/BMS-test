package com.example.triangle.input;

import java.math.BigDecimal;
import java.util.regex.Pattern;

public final class SideLengthsParser {

    private static final int EXPECTED_ARGUMENT_COUNT = 3;
    private static final int MAXIMUM_INTEGER_DIGITS = 20;
    private static final int MAXIMUM_DECIMAL_DIGITS = 20;
    private static final int MAXIMUM_ARGUMENT_LENGTH = 100;
    private static final int MAXIMUM_ARGUMENT_LENGTH_IN_MESSAGE = 30;

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
        if (argument != null && argument.length() > MAXIMUM_ARGUMENT_LENGTH) {
            throw new InvalidInputException("Side length " + position + " is longer than " + MAXIMUM_ARGUMENT_LENGTH
                    + " characters: " + quoteForMessage(argument) + ".");
        }
        if (argument == null || !PLAIN_DECIMAL_NUMBER.matcher(argument).matches()) {
            throw new InvalidInputException("Side length " + position + " is not a valid number: " + quoteForMessage(argument)
                    + ". Use plain decimal notation with a dot as decimal separator, e.g. 3.5.");
        }

        BigDecimal sideLength = new BigDecimal(argument);
        // Trailing zeros carry no value, so 1.000 counts as 1 and 100 still counts as three integer digits.
        BigDecimal significantValue = sideLength.stripTrailingZeros();
        int decimalDigits = Math.max(significantValue.scale(), 0);
        int integerDigits = Math.max(significantValue.precision() - significantValue.scale(), 0);

        if (integerDigits > MAXIMUM_INTEGER_DIGITS) {
            throw new InvalidInputException("Side length " + position + " has more than " + MAXIMUM_INTEGER_DIGITS
                    + " digits before the decimal point: " + quoteForMessage(argument) + ".");
        }
        if (decimalDigits > MAXIMUM_DECIMAL_DIGITS) {
            throw new InvalidInputException("Side length " + position + " has more than " + MAXIMUM_DECIMAL_DIGITS
                    + " digits after the decimal point: " + quoteForMessage(argument) + ".");
        }
        return sideLength;
    }

    // Arguments can be arbitrarily long, so only the beginning is echoed back to keep error messages readable.
    private static String quoteForMessage(String argument) {
        if (argument == null || argument.length() <= MAXIMUM_ARGUMENT_LENGTH_IN_MESSAGE) {
            return "'" + argument + "'";
        }
        return "'" + argument.substring(0, MAXIMUM_ARGUMENT_LENGTH_IN_MESSAGE) + "...' (" + argument.length() + " characters)";
    }
}
