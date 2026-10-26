# Triangle Classifier

A tiny command-line application that classifies a triangle based on its side lengths.

## Requirements

- JDK 25

Maven is not required: the project ships with the Maven wrapper (`mvnw`), which downloads the right Maven version on first use.

## Build

```bash
./mvnw verify
```

On Windows, use `mvnw.cmd verify`.

## Run

Pass the three side lengths as arguments:

```bash
java -jar target/triangle-classifier.jar <side1> <side2> <side3>
```

The application prints the triangle type:

| Type        | Rule                        | Example | Output        |
|-------------|-----------------------------|---------|---------------|
| Equilateral | All three sides are equal   | `3 3 3` | `Equilateral` |
| Isosceles   | Exactly two sides are equal | `5 3 5` | `Isosceles`   |
| Scalene     | No sides are equal          | `3 4 5` | `Scalene`     |

An equilateral triangle is always reported as `Equilateral`, never `Isosceles`. Sides are compared by value, so `3`, `3.0` and `3.00` are equal.

## Input rules

Each side length must be:

- a plain decimal number using a dot as decimal separator, such as `3`, `4.5` or `+2.25` (no exponent like `1e3`, no comma like `3,5`);
- at most 100 characters long;
- greater than zero.

The three sides must also form a triangle: the sum of any two sides must be strictly greater than the third. Sides like `1 2 3` form a flat line and are rejected.

## Errors

On invalid input, the application prints an error to standard error and exits with code `1`:

```text
$ java -jar target/triangle-classifier.jar 3 abc 5
Error: Side length 2 is not a valid number: 'abc'. Use plain decimal notation with a dot as decimal separator, e.g. 3.5.
Usage: java -jar triangle-classifier.jar <side1> <side2> <side3>

$ java -jar target/triangle-classifier.jar 3 -4 5
Error: Side length 2 must be greater than zero but was -4.

$ java -jar target/triangle-classifier.jar 1 2 3
Error: Side lengths 1, 2 and 3 do not form a triangle: the sum of any two sides must be greater than the third side.
```
