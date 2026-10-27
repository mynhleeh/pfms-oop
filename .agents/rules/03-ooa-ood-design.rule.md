---
description: "Object-Oriented Analysis and Design (OOA/OOD) principles, UML diagramming, SOLID, and GoF patterns."
globs: ["**/analysis/**", "**/design/**", "**/requirements/**"]
---

# 03. OOA & OOD Engineering Standards

This rule governs Object-Oriented Analysis (OOA) and Object-Oriented Design (OOD) practices for the PFMS project, adhering to standard software engineering conventions.

## 1. Boundary Separation: OOA (Analysis) vs. OOD (Design)

1. **Analysis Phase (OOA deliverables):**
   - Model the domain mechanics independently of technical frameworks and programming languages.
   - Maintain a strict conceptual boundary between:
     - **Entities:** Domain concepts with unique identity and evolving lifecycles (e.g., `Wallet`, `SavingsAccount`, `DebtContract`, `GoalFund`).
     - **Value Objects:** Immutable domain concepts defined solely by their attributes, with no distinct identity (e.g., `Money`, `InterestRate`, `DateRange`, `TransactionCategory`).
2. **Design Phase (OOD deliverables):**
   - Realize object-oriented domain models into an architecturally sound Java implementation.
   - Enforce clean separation across layers:
     - `Domain/Model`: Entities, Value Objects, Domain Exceptions.
     - `Repository`: Interfaces abstracting data persistence and retrieval.
     - `Service`: Business transaction coordination, interest computations, and state transitions.
     - `Pattern`: Strategy, Factory, Observer, State implementations.
     - `UI/Controller`: Request intake, input validation, and view rendering.

## 2. SOLID Discipline & GoF Design Patterns

1. **Rigorous SOLID Adherence:**
   - **Single Responsibility (SRP):** Each class owns one cohesive responsibility (e.g., decouple interest calculation algorithms from savings account state persistence).
   - **Open/Closed (OCP):** Extend behaviors via polymorphism and interfaces without altering tested codebase.
   - **Liskov Substitution (LSP):** Subclasses must seamlessly satisfy superclass behavioral contracts without throwing unexpected runtime exceptions.
   - **Interface Segregation (ISP):** Prefer small, client-specific interfaces over monolithic contracts.
   - **Dependency Inversion (DIP):** High-level services must depend on abstractions (interfaces), never on low-level file or database drivers.
2. **Targeted GoF Patterns in PFMS:**
   - **Strategy Pattern:** For interchangeable interest calculation formulas, debt payment amortization schedules, and portfolio allocation logic.
   - **Factory Pattern:** For instantiating heterogeneous financial transactions (`ExpenseTransaction`, `IncomeTransaction`, `TransferTransaction`).
   - **State Pattern:** For governing lifecycle transitions of savings accounts (`ACTIVE`, `SETTLED_MATURED`, `SETTLED_EARLY`) and debt agreements (`ACTIVE`, `PAID`).
   - **Observer Pattern:** For automated wallet balance synchronization upon transaction commits or loan repayments.

## 3. UML Diagram Standards (Mermaid & PlantUML)

1. **Class Diagrams:**
   - Mandate explicit member visibility (`+` public, `-` private, `#` protected).
   - Annotate exact multiplicity and relationships: Inheritance (`<|--`), Realization (`<|..`), Aggregation (`o--`), Composition (`*--`), Dependency (`..>`).
2. **Sequence Diagrams:**
   - Distinguish between synchronous and asynchronous calls; clearly demarcate alternate paths, loops, and optional blocks using `alt`, `loop`, and `opt`.
