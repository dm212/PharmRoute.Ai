"use client";

import { FormEvent, useCallback, useEffect, useState } from "react";

type FacilityStop = {
  id: string;
  name: string;
  type: string;
  riskLevel: string;
  operatorName: string;
  city: string;
  state: string;
};

type JourneyLeg = {
  shipmentId: string;
  sequence: number;
  status: string;
  departedAt: string;
  arrivedAt: string;
  origin: FacilityStop;
  destination: FacilityStop;
};

type Investigation = {
  batch: {
    id: string;
    status: string;
    quantity: number;
    provenanceConfidence: number;
    manufacturedOn: string;
    expiresOn: string;
    drugName: string;
    strength: string;
    form: string;
    packSize: string;
    manufacturerName: string;
    manufacturerLicense: string;
  };
  journey: JourneyLeg[];
  riskExposures: Array<{
    relatedBatchId: string;
    relatedDrugName: string;
    facilityId: string;
    facilityName: string;
    incidentId: string;
    incidentType: string;
    severity: string;
    incidentStatus: string;
    reportedOn: string;
    summary: string;
  }>;
  inspections: Array<{
    inspectionId: string;
    result: string;
    performedAt: string;
    authorityName: string;
    facilityName: string;
  }>;
};

const API_BASE = process.env.NEXT_PUBLIC_API_BASE_URL ?? "http://localhost:8080";
const DEFAULT_BATCH = "BT-2026-0812-A17";

function formatDate(value?: string) {
  if (!value) return "Not recorded";
  const parsed = new Date(value);
  return Number.isNaN(parsed.getTime())
    ? value
    : new Intl.DateTimeFormat("en-IN", { day: "2-digit", month: "short", hour: "2-digit", minute: "2-digit" }).format(parsed);
}

function readable(value?: string) {
  if (!value) return "Unknown";
  return value.toLowerCase().replaceAll("_", " ").replace(/\b\w/g, (letter) => letter.toUpperCase());
}

export default function Home() {
  const [batchId, setBatchId] = useState(DEFAULT_BATCH);
  const [data, setData] = useState<Investigation | null>(null);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState<string | null>(null);
  const [connected, setConnected] = useState(false);

  const loadInvestigation = useCallback(async (requestedId: string) => {
    setLoading(true);
    setError(null);
    try {
      const response = await fetch(`${API_BASE}/api/v1/batches/${encodeURIComponent(requestedId)}/investigation`);
      const payload = await response.json();
      if (!response.ok) throw new Error(payload.message ?? "Unable to investigate this batch.");
      setData(payload);
      setConnected(true);
    } catch (requestError) {
      setData(null);
      setConnected(false);
      setError(requestError instanceof Error ? requestError.message : "The investigation service is unavailable.");
    } finally {
      setLoading(false);
    }
  }, []);

  useEffect(() => {
    const controller = new AbortController();

    fetch(`${API_BASE}/api/v1/batches/${DEFAULT_BATCH}/investigation`, {
      signal: controller.signal,
    })
      .then(async (response) => {
        const payload = await response.json();
        if (!response.ok) throw new Error(payload.message ?? "Unable to investigate this batch.");
        return payload as Investigation;
      })
      .then((payload) => {
        setData(payload);
        setConnected(true);
      })
      .catch((requestError: unknown) => {
        if (requestError instanceof DOMException && requestError.name === "AbortError") return;
        setError(requestError instanceof Error ? requestError.message : "The investigation service is unavailable.");
        setConnected(false);
      })
      .finally(() => {
        if (!controller.signal.aborted) setLoading(false);
      });

    return () => controller.abort();
  }, []);

  function investigate(event: FormEvent<HTMLFormElement>) {
    event.preventDefault();
    const normalized = batchId.trim().toUpperCase();
    if (!normalized) {
      setError("Enter a medicine batch ID to begin an investigation.");
      return;
    }
    setBatchId(normalized);
    void loadInvestigation(normalized);
  }

  const stops = data?.journey.length
    ? [data.journey[0].origin, ...data.journey.map((leg) => leg.destination)]
    : [];
  const primaryRisk = data?.riskExposures[0];

  return (
    <main>
      <header className="topbar">
        <a className="brand" href="#top" aria-label="PharmRoute.Ai home">
          <span className="brand-mark">P</span>
          <span><strong>PharmRoute.Ai</strong><small>Supply-chain intelligence</small></span>
        </a>
        <div className={`system-status ${connected ? "" : "offline"}`} aria-live="polite">
          <span className="status-dot" /> {connected ? "CognoDB connected" : "Checking graph"}
        </div>
      </header>

      <section className="hero" id="top">
        <div>
          <p className="eyebrow">Explainable medicine provenance</p>
          <h1>Follow every hand-off.<br />Find every hidden risk.</h1>
          <p className="hero-copy">Trace a pharmaceutical batch through the supply network and uncover indirect exposure to flagged facilities, routes, and incidents.</p>
        </div>
        <form className="search-card" onSubmit={investigate}>
          <label htmlFor="batch-id">Medicine batch ID</label>
          <div className="search-row">
            <input id="batch-id" value={batchId} onChange={(event) => setBatchId(event.target.value)} placeholder="e.g. BT-2026-0812-A17" autoComplete="off" aria-describedby="batch-hint" />
            <button type="submit" disabled={loading}>{loading ? "Investigating…" : "Investigate batch"}</button>
          </div>
          <p id="batch-hint">Try the seeded batch {DEFAULT_BATCH}</p>
        </form>
      </section>

      <section className="workspace" aria-label="Batch investigation result" aria-busy={loading}>
        {loading && (
          <div className="state-card" role="status">
            <span className="loader" />
            <div><strong>Traversing the supply graph</strong><p>Following shipments, facilities, inspections, and connected incidents.</p></div>
          </div>
        )}

        {!loading && error && (
          <div className="state-card error-state" role="alert">
            <span className="state-symbol">!</span>
            <div><strong>Investigation unavailable</strong><p>{error}</p><button type="button" onClick={() => void loadInvestigation(batchId)}>Try again</button></div>
          </div>
        )}

        {!loading && data && (
          <>
            <div className="result-heading">
              <div><p className="eyebrow">Investigation result</p><h2>{data.batch.id}</h2><p>{data.batch.drugName} {data.batch.strength} · {data.batch.packSize}</p></div>
              <div className="confidence-card"><span>Provenance confidence</span><strong>{data.batch.provenanceConfidence}%</strong><div className="meter"><i style={{ width: `${data.batch.provenanceConfidence}%` }} /></div></div>
            </div>

            <div className="summary-grid">
              <article className="summary-card"><span className="summary-icon safe">✓</span><div><small>Identity</small><strong>{readable(data.batch.status)} batch</strong></div></article>
              <article className="summary-card"><span className="summary-icon">{stops.length}</span><div><small>Journey</small><strong>{stops.length} verified hand-offs</strong></div></article>
              <article className="summary-card alert-card"><span className="summary-icon alert">!</span><div><small>Exposure</small><strong>{data.riskExposures.length} connected {data.riskExposures.length === 1 ? "batch" : "batches"} need review</strong></div></article>
            </div>

            <div className="content-grid">
              <article className="panel journey-panel">
                <div className="panel-heading"><div><p className="eyebrow">Multi-hop traversal</p><h3>Batch journey</h3></div><span className="pill">{data.journey.length * 3} relationships</span></div>
                {stops.length === 0 ? <p className="empty-copy">No shipment events have been recorded for this batch.</p> : (
                  <ol className="timeline">
                    {stops.map((stop, index) => {
                      const leg = index === 0 ? data.journey[0] : data.journey[index - 1];
                      const needsReview = stop.riskLevel !== "LOW";
                      return (
                        <li key={`${stop.id}-${index}`}>
                          <span className={`timeline-dot ${needsReview ? "warning" : ""}`}>{index + 1}</span>
                          <div><small>{readable(stop.type)}</small><strong>{stop.name}</strong><span>{stop.city}, {stop.state}{stop.operatorName ? ` · ${stop.operatorName}` : ""}</span></div>
                          <div className="stop-meta"><time>{formatDate(index === 0 ? leg.departedAt : leg.arrivedAt)}</time><span className={needsReview ? "review" : "verified"}>{needsReview ? "Review" : "Verified"}</span></div>
                        </li>
                      );
                    })}
                  </ol>
                )}
              </article>

              <aside className="panel risk-panel">
                <div className="panel-heading"><div><p className="eyebrow">Relationship evidence</p><h3>{primaryRisk ? "Why review this route?" : "No indirect exposure"}</h3></div></div>
                {primaryRisk ? (
                  <>
                    <div className="risk-callout"><span className="risk-symbol">!</span><div><strong>{readable(primaryRisk.incidentType)}</strong><p>{primaryRisk.summary}</p></div></div>
                    <div className="evidence-path" aria-label="Graph evidence path"><span>{data.batch.id}</span><b>→</b><span>{primaryRisk.facilityName}</span><b>→</b><span>{primaryRisk.incidentId}</span></div>
                    <dl className="risk-facts"><div><dt>Related batches</dt><dd>{data.riskExposures.length}</dd></div><div><dt>Incident severity</dt><dd>{readable(primaryRisk.severity)}</dd></div><div><dt>Reported</dt><dd>{primaryRisk.reportedOn}</dd></div></dl>
                    <div className="related-list">{data.riskExposures.map((risk) => <div key={risk.relatedBatchId}><span>{risk.relatedBatchId}</span><small>{risk.relatedDrugName}</small></div>)}</div>
                  </>
                ) : <p className="empty-copy">This batch has no known connection to a flagged facility or open incident.</p>}
              </aside>
            </div>
          </>
        )}
      </section>

      <footer className="site-footer">
        <strong>Demonstration data only.</strong> All organizations, medicines, facilities,
        incidents, identifiers, and operational records shown here are synthetic and do not
        represent real supply-chain or patient data. Any resemblance to an actual entity or
        product is coincidental.
      </footer>
    </main>
  );
}
