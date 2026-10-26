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

    @Test
    void parsesThreeValidNumbersIncludingDecimals() {
        SideLengths sideLengths = SideLengthsParser.parse(new String[]{"3", "4.5", "0.000001"});

        assertEquals(new SideLengths(new BigDecimal("3"), new BigDecimal("4.5"), new BigDecimal("0.000001")), sideLengths);
    }

    @ParameterizedTest
    @ValueSource(strings = {
            "0", "-3", "+3", "007", "1.000", "12345678901234567890.12345678901234567890"})
    void acceptsPlainDecimalNumber(String validValue) {
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
    void acceptsValueOfExactlyOneHundredCharacters() {
        String oneHundredCharacters = "1." + "0".repeat(98);

        SideLengths sideLengths = SideLengthsParser.parse(new String[]{"3", "4", oneHundredCharacters});

        assertEquals(new BigDecimal(oneHundredCharacters), sideLengths.thirdSide());
    }

    @Test
    void rejectsValueLongerThanOneHundredCharactersWithoutEchoingIt() {
        String veryLongNumber = "1".repeat(100_000);

        InvalidInputException exception = assertThrows(InvalidInputException.class,
                () -> SideLengthsParser.parse(new String[]{"3", veryLongNumber, "5"}));

        assertEquals("Side length 2 is longer than 100 characters (100000 characters).", exception.getMessage());
    }
}
