package com.bms.triangle.input;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.junit.jupiter.params.provider.ValueSource;

import java.math.BigDecimal;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

class SideLengthsParserTest {

    private static final String TWENTY_DIGITS = "12345678901234567890";

    @Test
    void parsesThreeValidNumbersIncludingDecimals() {
        SideLengths sideLengths = SideLengthsParser.parse(new String[]{"3", "4.5", "0.000001"});

        assertEquals(new SideLengths(new BigDecimal("3"), new BigDecimal("4.5"), new BigDecimal("0.000001")), sideLengths);
    }

    @ParameterizedTest
    @ValueSource(strings = {
            "0", "-3", "+3",
            TWENTY_DIGITS,
            "0." + TWENTY_DIGITS,
            TWENTY_DIGITS + "." + TWENTY_DIGITS,
            "1.0000000000000000000000000",
            "0000000000000000000000001"})
    void acceptsPlainNumberWithinDigitLimits(String validValue) {
        SideLengths sideLengths = SideLengthsParser.parse(new String[]{"3", validValue, "5"});

        assertEquals(new BigDecimal(validValue), sideLengths.secondSide());
    }

    @Test
    void rejectsNullArguments() {
        assertThrows(InvalidInputException.class, () -> SideLengthsParser.parse(null));
    }

    static Stream<Arguments> wrongArgumentCounts() {
        return Stream.of(
                Arguments.of((Object) new String[]{}),
                Arguments.of((Object) new String[]{"3", "4"}),
                Arguments.of((Object) new String[]{"3", "4", "5", "6"}));
    }

    @ParameterizedTest
    @MethodSource("wrongArgumentCounts")
    void rejectsArgumentCountOtherThanThree(String[] arguments) {
        InvalidInputException exception = assertThrows(InvalidInputException.class, () -> SideLengthsParser.parse(arguments));

        assertTrue(exception.getMessage().contains("received " + arguments.length));
    }

    @ParameterizedTest
    @ValueSource(strings = {
            "abc", "", " ", " 3", "3,5", "1.2.3", ".5", "5.", "NaN", "Infinity", "0x10",
            "1e3", "1E-5", "1e999999999", "٣"})
    void rejectsValueThatIsNotAPlainDecimalNumber(String invalidValue) {
        InvalidInputException exception = assertThrows(InvalidInputException.class,
                () -> SideLengthsParser.parse(new String[]{"3", invalidValue, "5"}));

        assertTrue(exception.getMessage().contains("Side length 2 is not a valid number"));
    }

    @Test
    void rejectsNullValue() {
        assertThrows(InvalidInputException.class, () -> SideLengthsParser.parse(new String[]{"3", "4", null}));
    }

    @Test
    void rejectsMoreThanTwentyDigitsBeforeDecimalPoint() {
        InvalidInputException exception = assertThrows(InvalidInputException.class,
                () -> SideLengthsParser.parse(new String[]{"3", "4", "1" + TWENTY_DIGITS}));

        assertTrue(exception.getMessage().contains("Side length 3 has more than 20 digits before the decimal point"));
    }

    @Test
    void rejectsMoreThanTwentyDigitsAfterDecimalPoint() {
        InvalidInputException exception = assertThrows(InvalidInputException.class,
                () -> SideLengthsParser.parse(new String[]{"0." + TWENTY_DIGITS + "1", "4", "5"}));

        assertTrue(exception.getMessage().contains("Side length 1 has more than 20 digits after the decimal point"));
    }

    @Test
    void acceptsValueOfExactlyOneHundredCharacters() {
        String oneHundredCharacters = "1." + "0".repeat(98);

        SideLengths sideLengths = SideLengthsParser.parse(new String[]{"3", "4", oneHundredCharacters});

        assertEquals(new BigDecimal(oneHundredCharacters), sideLengths.thirdSide());
    }

    @Test
    void rejectsValueLongerThanOneHundredCharactersAndTruncatesItInErrorMessage() {
        String veryLongNumber = "1".repeat(100_000);

        InvalidInputException exception = assertThrows(InvalidInputException.class,
                () -> SideLengthsParser.parse(new String[]{"3", veryLongNumber, "5"}));

        assertTrue(exception.getMessage().contains("Side length 2 is longer than 100 characters"));
        assertTrue(exception.getMessage().contains("'" + "1".repeat(30) + "...' (100000 characters)"));
        assertTrue(exception.getMessage().length() < 200);
    }

    @Test
    void truncatesLongNumberInDigitLimitErrorMessage() {
        String longNumber = "1".repeat(41);

        InvalidInputException exception = assertThrows(InvalidInputException.class,
                () -> SideLengthsParser.parse(new String[]{"3", "4", longNumber}));

        assertTrue(exception.getMessage().contains("'" + "1".repeat(30) + "...' (41 characters)"));
    }
}
