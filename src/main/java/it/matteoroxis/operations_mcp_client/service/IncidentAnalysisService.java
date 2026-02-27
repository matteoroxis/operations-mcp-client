package it.matteoroxis.operations_mcp_client.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import io.modelcontextprotocol.client.McpSyncClient;
import io.modelcontextprotocol.spec.McpSchema;
import it.matteoroxis.operations_mcp_client.model.Incident;
import it.matteoroxis.operations_mcp_client.model.SystemMetrics;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.Map;


@Service
public class IncidentAnalysisService {

    private final McpSyncClient mcpClient;
    private final ChatClient chatClient;
    private final ObjectMapper objectMapper;

    public IncidentAnalysisService(McpSyncClient mcpClient, ChatClient chatClient) {
        this.mcpClient = mcpClient;
        this.chatClient = chatClient;
        this.objectMapper = new ObjectMapper();
    }


    public String analyze() {
        try {

            McpSchema.CallToolRequest systemMetricsRequest = new McpSchema.CallToolRequest("getSystemMetrics", Map.of());
            McpSchema.CallToolResult systemMetricsToolResult = mcpClient.callTool(systemMetricsRequest);
            SystemMetrics systemMetricsTool = parseSystemMetrics(systemMetricsToolResult);

            McpSchema.CallToolRequest recentIncidentsRequest = new McpSchema.CallToolRequest("getRecentIncidents", Map.of());
            McpSchema.CallToolResult recentIncidentsToolResult = mcpClient.callTool(recentIncidentsRequest);
            Incident recentIncidentsTool = parseRecentIncident(recentIncidentsToolResult);

            // Analysis with OpenAI via Spring AI ChatClient
            return chatClient
                    .prompt()
                    .user(String.format(
                            """
                            Analyse the following system metrics and recent incidents and provide operational recommendations:
                            
                            - Average latency: %d ms
                            - Error rate: %.2f%%
                            - Timestamp: %s
                            
                             Recent Incident:
                             - ID: %s
                             - Title: %s
                             - Status: %s
                             - Occurred at: %s
                            
                            Constraints:
                            1) Highlight possible causes (DB, network, CPU/memory saturation, external dependencies).
                            2) Suggest actions in order of priority (quick wins -> structural interventions).
                            3) Indicate any additional metrics to be collected.
                            """,
                            systemMetricsTool.avgLatencyMs(),
                            systemMetricsTool.errorRate() * 100,
                            systemMetricsTool.timestamp(),
                            recentIncidentsTool.id(),
                            recentIncidentsTool.title(),
                            recentIncidentsTool.status(),
                            recentIncidentsTool.timestamp()
                    ))
                    .call()
                    .content();
        } catch (Exception e) {
            return "Error during the analysis: " + e.getMessage();
        }
    }

    private Incident parseRecentIncident(McpSchema.CallToolResult toolResult) {
        try {
            String content = toolResult.content().stream()
                    .filter(c -> c instanceof McpSchema.TextContent)
                    .map(c -> ((McpSchema.TextContent) c).text())
                    .findFirst()
                    .orElse("");

            if (content.contains("{")) {
                @SuppressWarnings("unchecked")
                Map<String, Object> data = objectMapper.readValue(content, Map.class);
                String id = (String) data.getOrDefault("id", "unknown");
                String title = (String) data.getOrDefault("title", "N/A");
                Instant timestamp = Instant.parse((String) data.getOrDefault("timestamp", Instant.now().toString()));
                String status = (String) data.getOrDefault("status", "UNKNOWN");
                return new Incident(id, title, timestamp, status);
            }

            return new Incident("unknown", "N/A", Instant.now(), "UNKNOWN");

        } catch (Exception _) {
            return new Incident("unknown", "N/A", Instant.now(), "UNKNOWN");
        }
    }


    private SystemMetrics parseSystemMetrics(McpSchema.CallToolResult toolResult) {
        try {
            // Estrai il contenuto testuale dal risultato
            String content = toolResult.content().stream()
                    .filter(c -> c instanceof McpSchema.TextContent)
                    .map(c -> ((McpSchema.TextContent) c).text())
                    .findFirst()
                    .orElse("");

            // Se il risultato è in formato JSON, parsealo
            if (content.contains("{")) {
                @SuppressWarnings("unchecked")
                Map<String, Object> data = objectMapper.readValue(content, Map.class);
                int avgLatency = ((Number) data.getOrDefault("avgLatencyMs", 0)).intValue();
                double errorRate = ((Number) data.getOrDefault("errorRate", 0.0)).doubleValue();
                return new SystemMetrics(avgLatency, errorRate, Instant.now());
            }

            // Fallback: valori di default
            return new SystemMetrics(100, 0.05, Instant.now());

        } catch (Exception _) {
            // In caso di errore, restituisci valori di default
            return new SystemMetrics(100, 0.05, Instant.now());
        }
    }
}
