package com.pharmatrace.api.batch;

import static org.neo4j.driver.Values.parameters;

import java.util.List;
import java.util.Optional;

import org.neo4j.driver.Driver;
import org.neo4j.driver.Record;
import org.neo4j.driver.Session;
import org.neo4j.driver.exceptions.Neo4jException;
import org.springframework.stereotype.Repository;

import com.pharmatrace.api.batch.BatchInvestigation.BatchOverview;
import com.pharmatrace.api.batch.BatchInvestigation.FacilityStop;
import com.pharmatrace.api.batch.BatchInvestigation.InspectionEvidence;
import com.pharmatrace.api.batch.BatchInvestigation.JourneyLeg;
import com.pharmatrace.api.batch.BatchInvestigation.RiskExposure;

@Repository
public class BatchRepository {

    private static final String OVERVIEW_QUERY = """
            MATCH (manufacturer:Organization)-[:PRODUCES]->(batch:Batch {id: $batchId})
            MATCH (batch)-[:INSTANCE_OF]->(drug:Drug)
            RETURN batch.id AS id, batch.status AS status, batch.quantity AS quantity,
                   batch.provenanceConfidence AS confidence,
                   toString(batch.manufacturedOn) AS manufacturedOn,
                   toString(batch.expiresOn) AS expiresOn,
                   drug.name AS drugName, drug.strength AS strength,
                   drug.form AS form, drug.packSize AS packSize,
                   manufacturer.name AS manufacturerName,
                   manufacturer.licenseNumber AS manufacturerLicense
            """;

    private static final String JOURNEY_QUERY = """
            MATCH (batch:Batch {id: $batchId})-[:SHIPPED_VIA]->(shipment:Shipment)
            MATCH (shipment)-[:FROM]->(origin:Facility)
            MATCH (shipment)-[:TO]->(destination:Facility)
            OPTIONAL MATCH (origin)<-[:OPERATES]-(originOperator:Organization)
            OPTIONAL MATCH (destination)<-[:OPERATES]-(destinationOperator:Organization)
            OPTIONAL MATCH (origin)-[:LOCATED_IN]->(originLocation:Location)
            OPTIONAL MATCH (destination)-[:LOCATED_IN]->(destinationLocation:Location)
            RETURN shipment.id AS shipmentId, shipment.sequence AS sequence,
                   shipment.status AS status, toString(shipment.departedAt) AS departedAt,
                   toString(shipment.arrivedAt) AS arrivedAt,
                   origin.id AS originId, origin.name AS originName,
                   origin.type AS originType, origin.riskLevel AS originRiskLevel,
                   originOperator.name AS originOperatorName,
                   originLocation.city AS originCity, originLocation.state AS originState,
                   destination.id AS destinationId, destination.name AS destinationName,
                   destination.type AS destinationType,
                   destination.riskLevel AS destinationRiskLevel,
                   destinationOperator.name AS destinationOperatorName,
                   destinationLocation.city AS destinationCity,
                   destinationLocation.state AS destinationState
            ORDER BY shipment.sequence
            """;

    private static final String RISK_QUERY = """
            MATCH (selected:Batch {id: $batchId})-[:SHIPPED_VIA]->(:Shipment)
                  -[:FROM|TO]->(facility:Facility)-[:FLAGGED_FOR]->(incident:RiskEvent)
            MATCH (related:Batch)-[:SHIPPED_VIA]->(:Shipment)-[:FROM|TO]->(facility)
            MATCH (related)-[:INSTANCE_OF]->(drug:Drug)
            WHERE related <> selected
            RETURN DISTINCT related.id AS relatedBatchId, drug.name AS relatedDrugName,
                   facility.id AS facilityId, facility.name AS facilityName,
                   incident.id AS incidentId, incident.type AS incidentType,
                   incident.severity AS severity, incident.status AS incidentStatus,
                   toString(incident.reportedOn) AS reportedOn, incident.summary AS summary
            ORDER BY reportedOn DESC, relatedBatchId
            """;

    private static final String INSPECTION_QUERY = """
            MATCH (batch:Batch {id: $batchId})-[:HAS_INSPECTION]->(inspection:Inspection)
            MATCH (inspection)-[:PERFORMED_BY]->(authority:Authority)
            MATCH (inspection)-[:AT_FACILITY]->(facility:Facility)
            RETURN inspection.id AS inspectionId, inspection.result AS result,
                   toString(inspection.performedAt) AS performedAt,
                   inspection.notes AS notes, authority.name AS authorityName,
                   facility.name AS facilityName
            ORDER BY inspection.performedAt DESC
            """;

    private final Driver driver;

    public BatchRepository(Driver driver) {
        this.driver = driver;
    }

    public Optional<BatchInvestigation> investigate(String batchId) {
        try (Session session = driver.session()) {
            return session.executeRead(transaction -> {
                Optional<BatchOverview> overview = transaction.run(
                                OVERVIEW_QUERY, parameters("batchId", batchId))
                        .stream()
                        .findFirst()
                        .map(this::mapOverview);

                if (overview.isEmpty()) {
                    return Optional.empty();
                }

                List<JourneyLeg> journey = transaction.run(
                                JOURNEY_QUERY, parameters("batchId", batchId))
                        .list(this::mapJourneyLeg);
                List<RiskExposure> risks = transaction.run(
                                RISK_QUERY, parameters("batchId", batchId))
                        .list(this::mapRiskExposure);
                List<InspectionEvidence> inspections = transaction.run(
                                INSPECTION_QUERY, parameters("batchId", batchId))
                        .list(this::mapInspection);

                return Optional.of(new BatchInvestigation(
                        overview.get(), journey, risks, inspections));
            });
        } catch (Neo4jException exception) {
            throw new GraphUnavailableException(exception);
        }
    }

    private BatchOverview mapOverview(Record row) {
        return new BatchOverview(
                text(row, "id"), text(row, "status"), number(row, "quantity"),
                number(row, "confidence"), text(row, "manufacturedOn"),
                text(row, "expiresOn"), text(row, "drugName"), text(row, "strength"),
                text(row, "form"), text(row, "packSize"), text(row, "manufacturerName"),
                text(row, "manufacturerLicense"));
    }

    private JourneyLeg mapJourneyLeg(Record row) {
        FacilityStop origin = new FacilityStop(
                text(row, "originId"), text(row, "originName"), text(row, "originType"),
                text(row, "originRiskLevel"), text(row, "originOperatorName"),
                text(row, "originCity"), text(row, "originState"));
        FacilityStop destination = new FacilityStop(
                text(row, "destinationId"), text(row, "destinationName"),
                text(row, "destinationType"), text(row, "destinationRiskLevel"),
                text(row, "destinationOperatorName"), text(row, "destinationCity"),
                text(row, "destinationState"));
        return new JourneyLeg(
                text(row, "shipmentId"), number(row, "sequence"), text(row, "status"),
                text(row, "departedAt"), text(row, "arrivedAt"), origin, destination);
    }

    private RiskExposure mapRiskExposure(Record row) {
        return new RiskExposure(
                text(row, "relatedBatchId"), text(row, "relatedDrugName"),
                text(row, "facilityId"), text(row, "facilityName"),
                text(row, "incidentId"), text(row, "incidentType"),
                text(row, "severity"), text(row, "incidentStatus"),
                text(row, "reportedOn"), text(row, "summary"));
    }

    private InspectionEvidence mapInspection(Record row) {
        return new InspectionEvidence(
                text(row, "inspectionId"), text(row, "result"), text(row, "performedAt"),
                text(row, "notes"), text(row, "authorityName"), text(row, "facilityName"));
    }

    private String text(Record row, String key) {
        return row.get(key).isNull() ? null : row.get(key).asString();
    }

    private long number(Record row, String key) {
        return row.get(key).isNull() ? 0 : row.get(key).asLong();
    }
}
