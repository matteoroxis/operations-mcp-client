package it.matteoroxis.operations_mcp_client.config;


import io.modelcontextprotocol.client.McpClient;
import io.modelcontextprotocol.client.McpSyncClient;
import io.modelcontextprotocol.client.transport.HttpClientStreamableHttpTransport;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.time.Duration;

@Configuration
public class McpClientConfig {


    @Bean(destroyMethod = "close")
    public McpSyncClient mcpSyncClient() {

        var transport = HttpClientStreamableHttpTransport
                .builder("http://localhost:8081")  // base URL
                .endpoint("/mcp")                  // MCP endpoint
                .build();

        return McpClient.sync(transport)
                .requestTimeout(Duration.ofSeconds(30))
                .build();
    }

}
