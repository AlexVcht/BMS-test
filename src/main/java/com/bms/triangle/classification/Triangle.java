package com.bms.triangle.classification;

import java.math.BigDecimal;

public record Triangle(BigDecimal firstSide, BigDecimal secondSide, BigDecimal thirdSide) {

    public Triangle {
        requirePositive(firstSide, 1);
        requirePositive(secondSide, 2);
        requirePositive(thirdSide, 3);
        requireTriangleInequality(firstSide, secondSide, thirdSide);
    }

    // An equilateral triangle is also isosceles, so the most specific type is checked first and wins.
    // Sides are compared with compareTo because equals also compares scale: 3 and 3.0 are equal sides.
    public TriangleType classify() {
        boolean firstEqualsSecond = firstSide.compareTo(secondSide) == 0;
        boolean secondEqualsThird = secondSide.compareTo(thirdSide) == 0;
        boolean firstEqualsThird = firstSide.compareTo(thirdSide) == 0;

        if (firstEqualsSecond && secondEqualsThird) {
            return TriangleType.EQUILATERAL;
        }
        if (firstEqualsSecond || secondEqualsThird || firstEqualsThird) {
            return TriangleType.ISOSCELES;
        }
        return TriangleType.SCALENE;
    }

    private static void requirePositive(BigDecimal sideLength, int position) {
        if (sideLength == null) {
            throw new InvalidTriangleException("Side length " + position + " is missing.");
        }
        if (sideLength.signum() <= 0) {
            throw new InvalidTriangleException(
                    "Side length " + position + " must be greater than zero but was " + sideLength.toPlainString() + ".");
        }
    }

    // Strict inequality: when two sides add up exactly to the third, the "triangle" is a flat line and is rejected.
    private static void requireTriangleInequality(BigDecimal firstSide, BigDecimal secondSide, BigDecimal thirdSide) {
        boolean formsTriangle = firstSide.add(secondSide).compareTo(thirdSide) > 0
                && firstSide.add(thirdSide).compareTo(secondSide) > 0
                && secondSide.add(thirdSide).compareTo(firstSide) > 0;

        if (!formsTriangle) {
            throw new InvalidTriangleException("Side lengths " + firstSide.toPlainString() + ", "
                    + secondSide.toPlainString() + " and " + thirdSide.toPlainString()
                    + " do not form a triangle: the sum of any two sides must be greater than the third side.");
        }
    }
}
