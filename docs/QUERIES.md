# Main graph queries

All application versions of these queries use named parameters through the official Neo4j Java driver. User input is never concatenated into Cypher.

## 1. Batch overview

Purpose: retrieve the selected batch, its medicine, and its manufacturer.

```cypher
MATCH (manufacturer:Organization)-[:PRODUCES]->(batch:Batch {id: $batchId})
MATCH (batch)-[:INSTANCE_OF]->(drug:Drug)
RETURN batch, drug, manufacturer
```

## 2. Multi-hop provenance traversal

Purpose: reconstruct an ordered journey across batch, shipment, facility, organization, and location nodes.

```cypher
MATCH (batch:Batch {id: $batchId})-[:SHIPPED_VIA]->(shipment:Shipment)
MATCH (shipment)-[:FROM]->(origin:Facility)
MATCH (shipment)-[:TO]->(destination:Facility)
OPTIONAL MATCH (origin)<-[:OPERATES]-(originOperator:Organization)
OPTIONAL MATCH (destination)<-[:OPERATES]-(destinationOperator:Organization)
OPTIONAL MATCH (origin)-[:LOCATED_IN]->(originLocation:Location)
OPTIONAL MATCH (destination)-[:LOCATED_IN]->(destinationLocation:Location)
RETURN shipment, origin, destination,
       originOperator, destinationOperator,
       originLocation, destinationLocation
ORDER BY shipment.sequence
```

This traversal crosses multiple relationship types and reconstructs an arbitrary number of shipment legs without a fixed set of SQL joins.

## 3. Indirect risk exposure

Purpose: discover other batches connected through a shared facility that has a risk event, and return the evidence that explains the exposure.

```cypher
MATCH (selected:Batch {id: $batchId})-[:SHIPPED_VIA]->(:Shipment)
      -[:FROM|TO]->(facility:Facility)-[:FLAGGED_FOR]->(incident:RiskEvent)
MATCH (related:Batch)-[:SHIPPED_VIA]->(:Shipment)-[:FROM|TO]->(facility)
WHERE related <> selected
RETURN DISTINCT related, facility, incident
ORDER BY incident.reportedOn DESC, related.id
```

This is the deliberately graph-native query: it follows shared neighbours and returns the connecting evidence path. A relational implementation would require repeated joins across batch-shipment-facility mappings and additional logic as the path expands.

## 4. Inspection evidence

Purpose: show who inspected the batch, where it happened, and the outcome.

```cypher
MATCH (batch:Batch {id: $batchId})-[:HAS_INSPECTION]->(inspection:Inspection)
MATCH (inspection)-[:PERFORMED_BY]->(authority:Authority)
MATCH (inspection)-[:AT_FACILITY]->(facility:Facility)
RETURN inspection, authority, facility
ORDER BY inspection.performedAt DESC
```
