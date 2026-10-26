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

## Design decisions

- **Exact arithmetic with `BigDecimal`.** Side lengths are compared exactly, so `3`, `3.0` and `3.00` are equal sides and no floating-point rounding can turn an isosceles triangle into a scalene one.
  Comparisons use `compareTo`, because `BigDecimal.equals` also compares scale. Trade-off: values that have no finite decimal form, such as ⅓, cannot be entered.
- **Two packages, two responsibilities.** `input` turns raw command-line arguments into numbers and reports malformed input. `classification` holds the domain: `Triangle` checks its own rules
  (positive sides, triangle inequality) in its constructor, so an invalid triangle cannot exist. `SideLengths` is kept separate from `Triangle` so the input format can evolve, for example reading from
  a file or adding units, without touching the domain.
- **Input errors and domain errors are kept apart.** Each has its own exception type. Only malformed input prints the usage line, because a well-formed but impossible triangle is not a usage mistake.
- **Strict input first.** Only plain decimal notation is accepted: no exponent, no comma, no `.5`. Accepting more later is backward compatible, while rejecting something that used to be accepted would
  break existing callers. Each argument is capped at 100 characters, checked before parsing, so a huge argument cannot make parsing slow.
- **Equilateral wins over isosceles.** Every equilateral triangle is also isosceles, so the most specific type is checked first.
- **Small and dependency-free.** Plain JDK, with JUnit as the only test dependency, packaged as a jar that runs with `java -jar`.

## Limitations

- Degenerate triangles, where two sides add up exactly to the third, are rejected rather than classified.

## Use of AI

This project was built with Claude Code. [`CLAUDE.md`](CLAUDE.md) sets the rules it had to follow: no frameworks, explicit naming, tests only where they add value, and `./mvnw verify` as the
definition of done. Each step was reviewed before it was committed.

### How I worked with the AI

I split the work into small steps, one prompt each, and after each one I read the diff, ran `./mvnw verify` and tried the jar by hand before committing.

1. **Setup**: the Java/Maven boilerplate, `CLAUDE.md` and `.gitignore`, to have a working first step.
2. **Input parser**: I asked it to handle edge cases such as null and non-numeric values and to be defensive, which is why only plain decimal numbers of at most 100 characters are accepted.
3. **Triangle domain**: I asked it to enforce the business rules, such as rejecting zero or negative lengths and identifying impossible triangles.
4. **AI review**: a review of all the code, looking for missed edge cases and gaps in tests and documentation.
5. **My review**: I checked the result against the brief and removed what was over-engineered for the scope (arbitrary 20-digit limits, error-message truncation). I also caught a regression in `main` that no test covered, which led to the end-to-end tests in `TriangleClassifierApplicationTest`.
