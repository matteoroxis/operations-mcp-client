package it.matteoroxis.operations_mcp_client.service;

import io.modelcontextprotocol.client.McpClient;
import org.springframework.stereotype.Service;

@Service
public class IncidentAnalysisService {

    private final McpClient mcpClient;

    public IncidentAnalysisService(McpClient mcpClient){
        this.mcpClient = mcpClient;
    }

    public McpContext buildContext(){

    }


}
