package com.bms.triangle;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import java.nio.charset.StandardCharsets;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

class TriangleClassifierApplicationTest {

    private final ByteArrayOutputStream output = new ByteArrayOutputStream();
    private final ByteArrayOutputStream error = new ByteArrayOutputStream();

    private int run(String... arguments) {
        return TriangleClassifierApplication.run(arguments,
                new PrintStream(output, true, StandardCharsets.UTF_8),
                new PrintStream(error, true, StandardCharsets.UTF_8));
    }

    private String printedOutput() {
        return output.toString(StandardCharsets.UTF_8);
    }

    private String printedError() {
        return error.toString(StandardCharsets.UTF_8);
    }

    @ParameterizedTest
    @CsvSource({
            "3, 3, 3, Equilateral",
            "5, 3, 5, Isosceles",
            "3, 4, 5, Scalene"})
    void printsExactlyTheTriangleTypeAndExitsWithSuccess(String firstSide, String secondSide, String thirdSide,
                                                          String expectedOutput) {
        int exitCode = run(firstSide, secondSide, thirdSide);

        assertEquals(TriangleClassifierApplication.SUCCESS_EXIT_CODE, exitCode);
        assertEquals(expectedOutput + System.lineSeparator(), printedOutput());
        assertEquals("", printedError());
    }

    @Test
    void printsErrorWithUsageAndExitsWithErrorOnInvalidInput() {
        int exitCode = run("3", "abc", "5");

        assertEquals(TriangleClassifierApplication.ERROR_EXIT_CODE, exitCode);
        assertEquals("", printedOutput());
        assertTrue(printedError().startsWith("Error: Side length 2 is not a valid number"));
        assertTrue(printedError().contains("Usage: java -jar triangle-classifier.jar"));
    }

    @Test
    void printsErrorWithoutUsageAndExitsWithErrorWhenSidesDoNotFormATriangle() {
        int exitCode = run("1", "2", "3");

        assertEquals(TriangleClassifierApplication.ERROR_EXIT_CODE, exitCode);
        assertEquals("", printedOutput());
        assertTrue(printedError().startsWith("Error: Side lengths 1, 2 and 3 do not form a triangle"));
        assertFalse(printedError().contains("Usage:"));
    }
}
