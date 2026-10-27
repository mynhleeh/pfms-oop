---
description: "Java clean code principles, financial data types (BigDecimal), self-documenting code, and JUnit 5 testing."
globs: ["**/*.java", "**/src/**", "**/test/**"]
---

# 04. Clean Code Java & Testing Discipline

This rule establishes development, code hygiene, and automated testing standards for all Java source code and test suites within the PFMS project.

## 1. Financial Arithmetic & Type Safety

1. **Mandatory `BigDecimal` Usage:**
   - Strictly forbid the use of `float` or `double` for account balances, transaction sums, currency conversions, or interest rates to prevent IEEE-754 binary floating-point rounding errors.
   - Every division and arithmetic operation involving fractions must explicitly define scale and rounding mode: `RoundingMode.HALF_EVEN` (banker's rounding) as the default, unless the branch's requirements deliverable defines another rounding policy. Any other mode (for example `RoundingMode.HALF_UP`) is permitted only where the requirements deliverable documents the exception, and the code cites that business rule.
2. **Invariants & Boundary Validation:**
   - Validate that all transaction amounts are strictly positive: `amount.compareTo(BigDecimal.ZERO) > 0`.
   - Enforce wallet non-negativity unless an explicit overdraft credit facility is enabled for the account type.
   - Mandate the standard `java.time` API (`LocalDate`, `LocalDateTime`) for all temporal calculations.

## 2. Clean Code Hygiene & Self-Documenting Design

1. **Single-Responsibility Functions:**
   - Methods must perform a single coherent action and generally not exceed **25 lines of code**.
   - Decompose nested conditionals or multi-step algorithms into descriptive `private` helper methods.
2. **Self-Documenting Code (Zero Redundant Comments):**
   - Eliminate trivial comments that merely parrot language syntax (e.g., `// get balance`, `// calculate total`, `// return list`).
   - Reserve comments strictly for non-obvious mathematical algorithms or explicit references to business rules (`BR-*` identifiers) in the branch's requirements deliverable.
   - Limit JavaDoc strictly to public API contracts and major interface definitions.
3. **Contextual Naming Standards:**
   - Strictly forbid single- or double-letter variables (`a, b, x, temp, res, data`).
   - Use contextually distinct domain names: `sourceWallet` vs. `targetWallet` (never `wallet1, wallet2`), `calculateAccruedInterest()` vs. `calculatePenaltyFee()`.

## 3. Automated Testing Standards (JUnit 5 & Mockito)

1. **Test Structure & Given-When-Then:**
   - Every domain entity and business service must be covered by a corresponding `*Test.java` suite.
   - Structure all test methods following the **Given - When - Then** (or Arrange - Act - Assert) pattern.
2. **Boundary & Edge Case Verification:**
   - Mandatory test cases: Zero and negative amounts, overdraft attempts, withdrawal exceeding balance, maturity dates preceding start dates, and interest accrual across leap years.
3. **Isolation via Mockito:**
   - Decouple service tests from filesystem or persistence layers using Mockito (`@Mock`, `@InjectMocks`, `verify()`).
