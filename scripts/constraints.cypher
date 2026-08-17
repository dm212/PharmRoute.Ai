// PharmaTrace uniqueness constraints and lookup indexes.
// Safe to run repeatedly against CognoDB.

CREATE CONSTRAINT organization_id IF NOT EXISTS
FOR (node:Organization) REQUIRE node.id IS UNIQUE;

CREATE CONSTRAINT facility_id IF NOT EXISTS
FOR (node:Facility) REQUIRE node.id IS UNIQUE;

CREATE CONSTRAINT location_id IF NOT EXISTS
FOR (node:Location) REQUIRE node.id IS UNIQUE;

CREATE CONSTRAINT drug_id IF NOT EXISTS
FOR (node:Drug) REQUIRE node.id IS UNIQUE;

CREATE CONSTRAINT batch_id IF NOT EXISTS
FOR (node:Batch) REQUIRE node.id IS UNIQUE;

CREATE CONSTRAINT shipment_id IF NOT EXISTS
FOR (node:Shipment) REQUIRE node.id IS UNIQUE;

CREATE CONSTRAINT inspection_id IF NOT EXISTS
FOR (node:Inspection) REQUIRE node.id IS UNIQUE;

CREATE CONSTRAINT authority_id IF NOT EXISTS
FOR (node:Authority) REQUIRE node.id IS UNIQUE;

CREATE CONSTRAINT risk_event_id IF NOT EXISTS
FOR (node:RiskEvent) REQUIRE node.id IS UNIQUE;

CREATE INDEX batch_status IF NOT EXISTS
FOR (node:Batch) ON (node.status);

CREATE INDEX facility_risk_level IF NOT EXISTS
FOR (node:Facility) ON (node.riskLevel);
