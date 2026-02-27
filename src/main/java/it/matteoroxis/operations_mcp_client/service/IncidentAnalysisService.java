package it.matteoroxis.operations_mcp_client.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import io.modelcontextprotocol.client.McpSyncClient;
import io.modelcontextprotocol.spec.McpSchema;
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
            // 1) Creazione richiesta tool MCP
            McpSchema.CallToolRequest request = new McpSchema.CallToolRequest("getSystemMetrics", Map.of());

            // 2) Chiamata SINCRONA al tool MCP (blocking)
            //    Nel client SYNC callTool restituisce direttamente CallToolResult
            McpSchema.CallToolResult toolResult = mcpClient.callTool(request);
            // 3) Parsing risultato
            SystemMetrics systemMetrics = parseSystemMetrics(toolResult);

//            return systemMetrics.toString();

            // 4) Analisi con OpenAI via Spring AI ChatClient
            return chatClient
                    .prompt()
                    .user(String.format(
                            """
Analyse the following system metrics and provide operational recommendations:

- Average latency: %d ms
- Error rate: %.2f%%
- Timestamp: %s

                            Constraints:
1) Highlight possible causes (DB, network, CPU/memory saturation, external dependencies).
2) Suggest actions in order of priority (quick wins -> structural interventions).
3) Indicate any additional metrics to be collected.
                            """,
                            systemMetrics.avgLatencyMs(),
                            systemMetrics.errorRate() * 100,
                            systemMetrics.timestamp()
                    ))
                    .call()
                    .content();

        } catch (Exception e) {
            return "Error during the analysis: " + e.getMessage();
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
