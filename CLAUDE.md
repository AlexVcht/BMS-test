# CLAUDE.md

Guidance for Claude Code when working in this repository.

## Project

A tiny, production-ready Java application that classifies a triangle based on its side lengths.

## Tech stack

- **Java 25**: use modern language features where they help readability (records, sealed types, pattern matching, switch expressions).
- **Never use `var`**: always declare the explicit type of local variables.
- **Maven** is the only build tool, always run through the Maven wrapper (`./mvnw`), never a locally installed `mvn`.
- **No frameworks**: no Spring, Quarkus, Micronaut, Lombok, etc. Use the JDK standard library. A test library (JUnit 5, plus AssertJ if useful) is the only expected dependency. Ask before adding any other dependency.
- **No formatting or static analysis plugins** (Spotless, Checkstyle, etc.).

## Naming

- Use explicit, descriptive names for classes, methods, variables, and packages.
- **No abbreviations**: `CustomerRepository`, not `CustRepo`; `configuration`, not `cfg`; `request`, not `req`.
- A name should say what the thing is or does without needing to read its implementation.

## Testing

- Write unit tests only where they add value. **Do not aim for full coverage.**
- Focus on behavior, business rules, edge cases, and failure paths: boundaries, empty or null input, invalid states, error handling.
- Do not test trivial code: getters, setters, records, simple constructors, plain delegation.
- Test names describe the scenario and the expected outcome.

## Definition of done

A task is done only when this passes:

```bash
./mvnw verify
```

Run it before reporting a task as complete. If it fails, fix it; never skip or disable tests to make it pass.

## Working agreement

- **If anything is unclear or ambiguous, ask before implementing.** Don't guess at requirements.
- Keep the application small and simple. Don't add abstractions or features that weren't requested.
- **Never commit or push unless explicitly asked.**
