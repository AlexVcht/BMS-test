package com.bms.triangle.classification;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;

import java.math.BigDecimal;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

class TriangleTest {

    private static Triangle triangle(String firstSide, String secondSide, String thirdSide) {
        return new Triangle(new BigDecimal(firstSide), new BigDecimal(secondSide), new BigDecimal(thirdSide));
    }

    @ParameterizedTest
    @CsvSource({
            "3, 3, 3",
            "3, 3.0, 3.00",
            "0.00000000000000000001, 0.00000000000000000001, 0.00000000000000000001"})
    void classifiesThreeEqualSidesAsEquilateralNotIsosceles(String firstSide, String secondSide, String thirdSide) {
        assertEquals(TriangleType.EQUILATERAL, triangle(firstSide, secondSide, thirdSide).classify());
    }

    @ParameterizedTest
    @CsvSource({
            "5, 5, 3",
            "5, 3, 5",
            "3, 5, 5",
            "5, 5.0, 8",
            "1, 1, 1.00000000000000000001"})
    void classifiesExactlyTwoEqualSidesInAnyPositionAsIsosceles(String firstSide, String secondSide, String thirdSide) {
        assertEquals(TriangleType.ISOSCELES, triangle(firstSide, secondSide, thirdSide).classify());
    }

    @ParameterizedTest
    @CsvSource({
            "3, 4, 5",
            "1, 1.00000000000000000001, 1.00000000000000000002"})
    void classifiesNoEqualSidesAsScalene(String firstSide, String secondSide, String thirdSide) {
        assertEquals(TriangleType.SCALENE, triangle(firstSide, secondSide, thirdSide).classify());
    }

    @ParameterizedTest
    @CsvSource({
            "0, 4, 5, 1",
            "3, -4, 5, 2",
            "3, 4, 0.0, 3"})
    void rejectsZeroOrNegativeSideLength(String firstSide, String secondSide, String thirdSide, int invalidPosition) {
        InvalidTriangleException exception = assertThrows(InvalidTriangleException.class,
                () -> triangle(firstSide, secondSide, thirdSide));

        assertTrue(exception.getMessage().contains("Side length " + invalidPosition + " must be greater than zero"));
    }

    @Test
    void rejectsMissingSideLength() {
        assertThrows(InvalidTriangleException.class, () -> new Triangle(BigDecimal.ONE, null, BigDecimal.ONE));
    }

    @ParameterizedTest
    @CsvSource({
            "1, 2, 5",
            "5, 1, 2",
            "2, 5, 1",
            "1, 2, 3",
            "3, 1, 2",
            "2, 3, 1",
            "1, 1, 2.00000000000000000001"})
    void rejectsSidesWhereOneIsAtLeastTheSumOfTheOtherTwo(String firstSide, String secondSide, String thirdSide) {
        InvalidTriangleException exception = assertThrows(InvalidTriangleException.class,
                () -> triangle(firstSide, secondSide, thirdSide));

        assertTrue(exception.getMessage().contains("do not form a triangle"));
    }

    @Test
    void acceptsSidesBarelySatisfyingTheTriangleInequality() {
        assertEquals(TriangleType.ISOSCELES, triangle("1", "1", "1.99999999999999999999").classify());
    }
}
