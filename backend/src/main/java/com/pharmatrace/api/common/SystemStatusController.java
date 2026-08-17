package com.pharmatrace.api.common;

import java.util.Map;

import org.neo4j.driver.Driver;
import org.neo4j.driver.Session;
import org.neo4j.driver.exceptions.Neo4jException;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/system")
public class SystemStatusController {

    private final Driver driver;

    public SystemStatusController(Driver driver) {
        this.driver = driver;
    }

    @GetMapping("/status")
    public Map<String, Object> status() {
        try (Session session = driver.session()) {
            return session.executeRead(transaction -> {
                var counts = transaction.run("""
                        MATCH (node)
                        OPTIONAL MATCH ()-[relationship]->()
                        RETURN count(DISTINCT node) AS nodes,
                               count(DISTINCT relationship) AS relationships
                        """).single();
                var domainCounts = transaction.run("""
                        OPTIONAL MATCH (batch:Batch)
                        OPTIONAL MATCH (:Organization)-[produces:PRODUCES]->(:Batch)
                        OPTIONAL MATCH (:Batch)-[shippedVia:SHIPPED_VIA]->(:Shipment)
                        RETURN count(DISTINCT batch) AS batches,
                               count(DISTINCT produces) AS produces,
                               count(DISTINCT shippedVia) AS shipmentLinks
                        """).single();
                return Map.of(
                        "status", "CONNECTED",
                        "nodes", counts.get("nodes").asLong(),
                        "relationships", counts.get("relationships").asLong(),
                        "batches", domainCounts.get("batches").asLong(),
                        "produces", domainCounts.get("produces").asLong(),
                        "shipmentLinks", domainCounts.get("shipmentLinks").asLong());
            });
        } catch (Neo4jException exception) {
            return Map.of("status", "UNAVAILABLE", "nodes", 0, "relationships", 0);
        }
    }
}
