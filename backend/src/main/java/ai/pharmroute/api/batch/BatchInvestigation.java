package ai.pharmroute.api.batch;

import java.util.List;

public record BatchInvestigation(
        BatchOverview batch,
        List<JourneyLeg> journey,
        List<RiskExposure> riskExposures,
        List<InspectionEvidence> inspections) {

    public record BatchOverview(
            String id,
            String status,
            long quantity,
            long provenanceConfidence,
            String manufacturedOn,
            String expiresOn,
            String drugName,
            String strength,
            String form,
            String packSize,
            String manufacturerName,
            String manufacturerLicense) {
    }

    public record JourneyLeg(
            String shipmentId,
            long sequence,
            String status,
            String departedAt,
            String arrivedAt,
            FacilityStop origin,
            FacilityStop destination) {
    }

    public record FacilityStop(
            String id,
            String name,
            String type,
            String riskLevel,
            String operatorName,
            String city,
            String state) {
    }

    public record RiskExposure(
            String relatedBatchId,
            String relatedDrugName,
            String facilityId,
            String facilityName,
            String incidentId,
            String incidentType,
            String severity,
            String incidentStatus,
            String reportedOn,
            String summary) {
    }

    public record InspectionEvidence(
            String inspectionId,
            String result,
            String performedAt,
            String notes,
            String authorityName,
            String facilityName) {
    }
}
