// Deterministic demonstration dataset for PharmaTrace.
// The dataset is fictional but reflects a realistic pharmaceutical supply network.

MERGE (hyderabad:Location {id: 'LOC-HYD'})
SET hyderabad.city = 'Hyderabad', hyderabad.state = 'Telangana', hyderabad.country = 'India';

MERGE (bengaluru:Location {id: 'LOC-BLR'})
SET bengaluru.city = 'Bengaluru', bengaluru.state = 'Karnataka', bengaluru.country = 'India';

MERGE (mysuru:Location {id: 'LOC-MYS'})
SET mysuru.city = 'Mysuru', mysuru.state = 'Karnataka', mysuru.country = 'India';

MERGE (chennai:Location {id: 'LOC-MAA'})
SET chennai.city = 'Chennai', chennai.state = 'Tamil Nadu', chennai.country = 'India';

MERGE (asteria:Organization {id: 'ORG-ASTERIA'})
SET asteria.name = 'Asteria Pharma Labs', asteria.type = 'MANUFACTURER',
    asteria.licenseNumber = 'MFG-TG-20491', asteria.verified = true;

MERGE (northstar:Organization {id: 'ORG-NORTHSTAR'})
SET northstar.name = 'NorthStar Medical Logistics', northstar.type = 'LOGISTICS',
    northstar.licenseNumber = 'LOG-KA-77210', northstar.verified = true;

MERGE (mediroute:Organization {id: 'ORG-MEDIROUTE'})
SET mediroute.name = 'MediRoute Distribution', mediroute.type = 'DISTRIBUTOR',
    mediroute.licenseNumber = 'DST-KA-11804', mediroute.verified = true;

MERGE (carewell:Organization {id: 'ORG-CAREWELL'})
SET carewell.name = 'Carewell Pharmacy Group', carewell.type = 'PHARMACY',
    carewell.licenseNumber = 'PHA-KA-39007', carewell.verified = true;

MERGE (southern:Organization {id: 'ORG-SOUTHERN'})
SET southern.name = 'Southern Cross Distribution', southern.type = 'DISTRIBUTOR',
    southern.licenseNumber = 'DST-TN-90821', southern.verified = true;

MERGE (plant:Facility {id: 'FAC-ASTERIA-01'})
SET plant.name = 'Asteria Formulations Plant 1', plant.type = 'MANUFACTURING_PLANT',
    plant.riskLevel = 'LOW', plant.verified = true;

MERGE (warehouse:Facility {id: 'FAC-NORTHSTAR-BLR'})
SET warehouse.name = 'NorthStar Regional Warehouse', warehouse.type = 'WAREHOUSE',
    warehouse.riskLevel = 'LOW', warehouse.verified = true;

MERGE (hub07:Facility {id: 'FAC-MEDIROUTE-07'})
SET hub07.name = 'MediRoute Hub 07', hub07.type = 'DISTRIBUTION_HUB',
    hub07.riskLevel = 'MEDIUM', hub07.verified = true;

MERGE (pharmacy:Facility {id: 'FAC-CAREWELL-IND'})
SET pharmacy.name = 'Carewell Pharmacy - Indiranagar', pharmacy.type = 'PHARMACY',
    pharmacy.riskLevel = 'LOW', pharmacy.verified = true;

MERGE (mysuruHub:Facility {id: 'FAC-NORTHSTAR-MYS'})
SET mysuruHub.name = 'NorthStar Mysuru Hub', mysuruHub.type = 'DISTRIBUTION_HUB',
    mysuruHub.riskLevel = 'LOW', mysuruHub.verified = true;

MERGE (southernHub:Facility {id: 'FAC-SOUTHERN-MAA'})
SET southernHub.name = 'Southern Cross Chennai Hub', southernHub.type = 'DISTRIBUTION_HUB',
    southernHub.riskLevel = 'LOW', southernHub.verified = true;

MATCH (a:Organization {id: 'ORG-ASTERIA'}), (b:Facility {id: 'FAC-ASTERIA-01'}) MERGE (a)-[:OPERATES]->(b);
MATCH (a:Organization {id: 'ORG-NORTHSTAR'}), (b:Facility {id: 'FAC-NORTHSTAR-BLR'}) MERGE (a)-[:OPERATES]->(b);
MATCH (a:Organization {id: 'ORG-NORTHSTAR'}), (b:Facility {id: 'FAC-NORTHSTAR-MYS'}) MERGE (a)-[:OPERATES]->(b);
MATCH (a:Organization {id: 'ORG-MEDIROUTE'}), (b:Facility {id: 'FAC-MEDIROUTE-07'}) MERGE (a)-[:OPERATES]->(b);
MATCH (a:Organization {id: 'ORG-CAREWELL'}), (b:Facility {id: 'FAC-CAREWELL-IND'}) MERGE (a)-[:OPERATES]->(b);
MATCH (a:Organization {id: 'ORG-SOUTHERN'}), (b:Facility {id: 'FAC-SOUTHERN-MAA'}) MERGE (a)-[:OPERATES]->(b);

MATCH (a:Facility {id: 'FAC-ASTERIA-01'}), (b:Location {id: 'LOC-HYD'}) MERGE (a)-[:LOCATED_IN]->(b);
MATCH (a:Facility {id: 'FAC-NORTHSTAR-BLR'}), (b:Location {id: 'LOC-BLR'}) MERGE (a)-[:LOCATED_IN]->(b);
MATCH (a:Facility {id: 'FAC-MEDIROUTE-07'}), (b:Location {id: 'LOC-BLR'}) MERGE (a)-[:LOCATED_IN]->(b);
MATCH (a:Facility {id: 'FAC-CAREWELL-IND'}), (b:Location {id: 'LOC-BLR'}) MERGE (a)-[:LOCATED_IN]->(b);
MATCH (a:Facility {id: 'FAC-NORTHSTAR-MYS'}), (b:Location {id: 'LOC-MYS'}) MERGE (a)-[:LOCATED_IN]->(b);
MATCH (a:Facility {id: 'FAC-SOUTHERN-MAA'}), (b:Location {id: 'LOC-MAA'}) MERGE (a)-[:LOCATED_IN]->(b);

MERGE (cardiovex:Drug {id: 'DRUG-CARDIOVEX-20'})
SET cardiovex.name = 'Cardiovex', cardiovex.strength = '20 mg',
    cardiovex.form = 'Tablet', cardiovex.packSize = '10-tablet blister pack';

MERGE (neurocalm:Drug {id: 'DRUG-NEUROCALM-10'})
SET neurocalm.name = 'NeuroCalm', neurocalm.strength = '10 mg',
    neurocalm.form = 'Tablet', neurocalm.packSize = '15-tablet blister pack';

MERGE (batchA:Batch {id: 'BT-2026-0812-A17'})
SET batchA.manufacturedOn = date('2026-08-12'), batchA.expiresOn = date('2028-08-11'),
    batchA.status = 'IN_TRANSIT', batchA.quantity = 2400, batchA.provenanceConfidence = 86;

MERGE (batchB:Batch {id: 'BT-2026-0809-C04'})
SET batchB.manufacturedOn = date('2026-08-09'), batchB.expiresOn = date('2028-08-08'),
    batchB.status = 'DELIVERED', batchB.quantity = 1800, batchB.provenanceConfidence = 73;

MERGE (batchC:Batch {id: 'BT-2026-0810-N22'})
SET batchC.manufacturedOn = date('2026-08-10'), batchC.expiresOn = date('2028-08-09'),
    batchC.status = 'DELIVERED', batchC.quantity = 3200, batchC.provenanceConfidence = 91;

MERGE (batchD:Batch {id: 'BT-2026-0811-A09'})
SET batchD.manufacturedOn = date('2026-08-11'), batchD.expiresOn = date('2028-08-10'),
    batchD.status = 'IN_TRANSIT', batchD.quantity = 2100, batchD.provenanceConfidence = 95;

MATCH (a:Organization {id: 'ORG-ASTERIA'}), (b:Batch {id: 'BT-2026-0812-A17'}) MERGE (a)-[:PRODUCES]->(b);
MATCH (a:Organization {id: 'ORG-ASTERIA'}), (b:Batch {id: 'BT-2026-0809-C04'}) MERGE (a)-[:PRODUCES]->(b);
MATCH (a:Organization {id: 'ORG-ASTERIA'}), (b:Batch {id: 'BT-2026-0810-N22'}) MERGE (a)-[:PRODUCES]->(b);
MATCH (a:Organization {id: 'ORG-ASTERIA'}), (b:Batch {id: 'BT-2026-0811-A09'}) MERGE (a)-[:PRODUCES]->(b);
MATCH (a:Batch {id: 'BT-2026-0812-A17'}), (b:Drug {id: 'DRUG-CARDIOVEX-20'}) MERGE (a)-[:INSTANCE_OF]->(b);
MATCH (a:Batch {id: 'BT-2026-0809-C04'}), (b:Drug {id: 'DRUG-CARDIOVEX-20'}) MERGE (a)-[:INSTANCE_OF]->(b);
MATCH (a:Batch {id: 'BT-2026-0810-N22'}), (b:Drug {id: 'DRUG-NEUROCALM-10'}) MERGE (a)-[:INSTANCE_OF]->(b);
MATCH (a:Batch {id: 'BT-2026-0811-A09'}), (b:Drug {id: 'DRUG-CARDIOVEX-20'}) MERGE (a)-[:INSTANCE_OF]->(b);

// Selected batch: four facilities across three shipment legs.
MERGE (a1:Shipment {id: 'SHP-A17-01'})
SET a1.sequence = 1, a1.departedAt = datetime('2026-08-12T08:40:00+05:30'),
    a1.arrivedAt = datetime('2026-08-13T16:15:00+05:30'), a1.status = 'DELIVERED';
MATCH (a:Batch {id: 'BT-2026-0812-A17'}), (b:Shipment {id: 'SHP-A17-01'}) MERGE (a)-[:SHIPPED_VIA]->(b);
MATCH (a:Shipment {id: 'SHP-A17-01'}), (b:Facility {id: 'FAC-ASTERIA-01'}) MERGE (a)-[:FROM]->(b);
MATCH (a:Shipment {id: 'SHP-A17-01'}), (b:Facility {id: 'FAC-NORTHSTAR-BLR'}) MERGE (a)-[:TO]->(b);

MERGE (a2:Shipment {id: 'SHP-A17-02'})
SET a2.sequence = 2, a2.departedAt = datetime('2026-08-14T07:20:00+05:30'),
    a2.arrivedAt = datetime('2026-08-14T11:25:00+05:30'), a2.status = 'DELIVERED';
MATCH (a:Batch {id: 'BT-2026-0812-A17'}), (b:Shipment {id: 'SHP-A17-02'}) MERGE (a)-[:SHIPPED_VIA]->(b);
MATCH (a:Shipment {id: 'SHP-A17-02'}), (b:Facility {id: 'FAC-NORTHSTAR-BLR'}) MERGE (a)-[:FROM]->(b);
MATCH (a:Shipment {id: 'SHP-A17-02'}), (b:Facility {id: 'FAC-MEDIROUTE-07'}) MERGE (a)-[:TO]->(b);

MERGE (a3:Shipment {id: 'SHP-A17-03'})
SET a3.sequence = 3, a3.departedAt = datetime('2026-08-15T07:45:00+05:30'),
    a3.arrivedAt = datetime('2026-08-15T09:10:00+05:30'), a3.status = 'DELIVERED';
MATCH (a:Batch {id: 'BT-2026-0812-A17'}), (b:Shipment {id: 'SHP-A17-03'}) MERGE (a)-[:SHIPPED_VIA]->(b);
MATCH (a:Shipment {id: 'SHP-A17-03'}), (b:Facility {id: 'FAC-MEDIROUTE-07'}) MERGE (a)-[:FROM]->(b);
MATCH (a:Shipment {id: 'SHP-A17-03'}), (b:Facility {id: 'FAC-CAREWELL-IND'}) MERGE (a)-[:TO]->(b);

// Two related batches share the flagged hub; another follows a clean route.
MERGE (b1:Shipment {id: 'SHP-C04-01'})
SET b1.sequence = 1, b1.departedAt = datetime('2026-08-10T09:10:00+05:30'),
    b1.arrivedAt = datetime('2026-08-11T14:30:00+05:30'), b1.status = 'DELIVERED';
MATCH (a:Batch {id: 'BT-2026-0809-C04'}), (b:Shipment {id: 'SHP-C04-01'}) MERGE (a)-[:SHIPPED_VIA]->(b);
MATCH (a:Shipment {id: 'SHP-C04-01'}), (b:Facility {id: 'FAC-ASTERIA-01'}) MERGE (a)-[:FROM]->(b);
MATCH (a:Shipment {id: 'SHP-C04-01'}), (b:Facility {id: 'FAC-MEDIROUTE-07'}) MERGE (a)-[:TO]->(b);

MERGE (c1:Shipment {id: 'SHP-N22-01'})
SET c1.sequence = 1, c1.departedAt = datetime('2026-08-11T06:30:00+05:30'),
    c1.arrivedAt = datetime('2026-08-12T10:40:00+05:30'), c1.status = 'DELIVERED';
MATCH (a:Batch {id: 'BT-2026-0810-N22'}), (b:Shipment {id: 'SHP-N22-01'}) MERGE (a)-[:SHIPPED_VIA]->(b);
MATCH (a:Shipment {id: 'SHP-N22-01'}), (b:Facility {id: 'FAC-ASTERIA-01'}) MERGE (a)-[:FROM]->(b);
MATCH (a:Shipment {id: 'SHP-N22-01'}), (b:Facility {id: 'FAC-MEDIROUTE-07'}) MERGE (a)-[:TO]->(b);

MERGE (d1:Shipment {id: 'SHP-A09-01'})
SET d1.sequence = 1, d1.departedAt = datetime('2026-08-12T07:00:00+05:30'),
    d1.arrivedAt = datetime('2026-08-13T18:00:00+05:30'), d1.status = 'DELIVERED';
MATCH (a:Batch {id: 'BT-2026-0811-A09'}), (b:Shipment {id: 'SHP-A09-01'}) MERGE (a)-[:SHIPPED_VIA]->(b);
MATCH (a:Shipment {id: 'SHP-A09-01'}), (b:Facility {id: 'FAC-ASTERIA-01'}) MERGE (a)-[:FROM]->(b);
MATCH (a:Shipment {id: 'SHP-A09-01'}), (b:Facility {id: 'FAC-SOUTHERN-MAA'}) MERGE (a)-[:TO]->(b);

MERGE (cdsco:Authority {id: 'AUTH-CDSCO-SOUTH'})
SET cdsco.name = 'Central Drug Standards - South Zone', cdsco.jurisdiction = 'South India';

MERGE (inspection:Inspection {id: 'INSP-A17-01'})
SET inspection.performedAt = datetime('2026-08-13T17:00:00+05:30'),
    inspection.result = 'PASSED', inspection.notes = 'Packaging and seal verified';
MATCH (a:Batch {id: 'BT-2026-0812-A17'}), (b:Inspection {id: 'INSP-A17-01'}) MERGE (a)-[:HAS_INSPECTION]->(b);
MATCH (a:Inspection {id: 'INSP-A17-01'}), (b:Authority {id: 'AUTH-CDSCO-SOUTH'}) MERGE (a)-[:PERFORMED_BY]->(b);
MATCH (a:Inspection {id: 'INSP-A17-01'}), (b:Facility {id: 'FAC-NORTHSTAR-BLR'}) MERGE (a)-[:AT_FACILITY]->(b);

MERGE (incident:RiskEvent {id: 'INC-104'})
SET incident.type = 'PACKAGING_TAMPERING', incident.severity = 'MEDIUM',
    incident.status = 'OPEN', incident.reportedOn = date('2026-08-16'),
    incident.summary = 'Packaging anomalies found on a separate batch handled at the facility';
MATCH (a:Facility {id: 'FAC-MEDIROUTE-07'}), (b:RiskEvent {id: 'INC-104'}) MERGE (a)-[:FLAGGED_FOR]->(b);
MATCH (a:Batch {id: 'BT-2026-0809-C04'}), (b:RiskEvent {id: 'INC-104'}) MERGE (a)-[:ASSOCIATED_WITH]->(b);
