# PharmaTrace walkthrough guide

Target length: 4–5 minutes.

## 1. Problem and graph fit — 45 seconds

Introduce PharmaTrace as an investigator-facing pharmaceutical supply-chain tool. Explain that batches, shipments, facilities, organizations, inspections, and incidents form a connected network. The useful question is not merely “what row contains this batch?” but “which route did it take, what risky facility did it touch, and which other batches share that exposure?”

## 2. Architecture — 30 seconds

Show the README architecture diagram. Mention the React/Next.js frontend, Spring Boot REST API, parameterized Cypher queries, and CognoDB Cloud graph.

## 3. Live investigation — 2 minutes

Open the public application. Search for `BT-2026-0812-A17` and highlight:

- the 86% provenance confidence;
- four supply-chain stops across three shipment legs;
- the medium-risk distribution hub;
- the packaging-tampering incident;
- the two related batches discovered through the shared facility;
- the explicit evidence path from selected batch to facility to incident.

If time permits, briefly search `BT-2026-0814-I31` to contrast the packaging-risk path with a high-severity cold-chain breach and failed inspection.

## 4. Code and data model — 60 seconds

Show `scripts/seed.cypher`, then the parameterized queries in `BatchRepository.java`. Point out controller/service/repository separation, environment-based credentials, error handling, tests, Dockerfile, Render Blueprint, and GitHub Actions.

## 5. Close — 30 seconds

Reinforce why the graph earns its place: multi-hop traversal and evidence paths are direct relationships instead of repeated joins or recursive relational queries. End by showing the public demo and GitHub links in the README.

## Recording checklist

- Wake the Render API by opening `/actuator/health` before recording.
- Use a clean browser window at 1440×900 or similar.
- Keep the seeded batch ID copied.
- Hide bookmarks, notifications, credentials, and unrelated tabs.
- Verify microphone level and record one short test clip first.
