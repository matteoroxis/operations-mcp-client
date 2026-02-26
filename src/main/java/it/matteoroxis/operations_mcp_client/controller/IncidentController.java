package it.matteoroxis.operations_mcp_client.controller;

import it.matteoroxis.operations_mcp_client.service.IncidentAnalysisService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/incidents")
public class IncidentController {

    private final IncidentAnalysisService incidentAnalysisService;

    public IncidentController(IncidentAnalysisService incidentAnalysisService) {
        this.incidentAnalysisService = incidentAnalysisService;
    }

    @GetMapping("/analyze")
    public String analyzeSystemMetrics() {
        return incidentAnalysisService.analyze();
    }
}
