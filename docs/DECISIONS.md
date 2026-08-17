# Architecture decisions

## ADR-001: Pharmaceutical provenance and indirect risk exposure

**Decision:** Build an investigation tool rather than an inventory tracker.

**Reason:** The valuable questions concern variable-length routes, shared intermediaries, and indirect exposure. These are relationship questions for which a graph database provides a clear modeling and query advantage.

## ADR-002: Java 17 and Spring Boot backend

**Decision:** Use Java 17, Spring Boot, Maven, and the official Neo4j Java driver.

**Reason:** This directly reflects Yashwanth's strongest professional experience and keeps all Cypher execution in an explicit repository layer. The official driver also satisfies the assignment requirement without a custom CognoDB SDK.

## ADR-003: React and TypeScript frontend

**Decision:** Use a React/Next.js TypeScript frontend as a separate user-facing application.

**Reason:** It provides a polished, responsive interface while keeping the graph database inaccessible from the browser. The web app communicates only with the Spring Boot API.

## ADR-004: Explain results with evidence paths

**Decision:** Every risk result should include the nodes and relationships that justify it.

**Reason:** An explainable route is more useful than an opaque score and demonstrates why graph traversal matters.
