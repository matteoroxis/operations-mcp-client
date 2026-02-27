# operations-mcp-client

An **Enterprise Operations Assistant** built with Model Context Protocol (MCP), Java 25 and Spring Boot 3.5.

## 🚀 Description

This project integrates:
- **Model Context Protocol (MCP)**: for communication with external tools via `McpSyncClient`
- **Spring AI 1.1.2**: for OpenAI GPT-4 integration
- **Spring Boot 3.5**: for enterprise architecture

The application retrieves system metrics and recent incidents via MCP, processes them and provides intelligent operational recommendations using GPT-4.

## 📋 Prerequisites

- **JDK 25** (or JDK 21+)
- **Maven 3.8+**
- **MCP Server** running at `http://localhost:8081/mcp` (Streamable HTTP)
- **OpenAI API Key**

## ⚙️ Configuration

### 1. Set the OpenAI API Key

**Windows (PowerShell):**
```powershell
$env:OPENAI_API_KEY="sk-your-api-key-here"
```

**Linux/Mac:**
```bash
export OPENAI_API_KEY="sk-your-api-key-here"
```

Or edit `src/main/resources/application.properties` and replace `${OPENAI_API_KEY}` with your key directly.

### 2. Configure the MCP Server

The client expects a Streamable HTTP MCP server available at:
```
http://localhost:8081/mcp
```

The server must expose the following tools:
- `getSystemMetrics` → returns `{ avgLatencyMs, errorRate }`
- `getRecentIncidents` → returns a list of `[{ id, title, timestamp, status }]`

### 3. Application Properties

```properties
spring.application.name=operations-mcp-client

# MCP Client
spring.ai.mcp.client.type=SYNC
spring.ai.mcp.client.request-timeout=30s
spring.ai.mcp.client.toolcallback.enabled=false
spring.ai.mcp.client.streamable-http.connections.tools-server.url=http://localhost:8081
spring.ai.mcp.client.streamable-http.connections.tools-server.endpoint=/mcp

# OpenAI
spring.ai.openai.api-key=${OPENAI_API_KEY}
spring.ai.openai.chat.options.model=gpt-4
spring.ai.openai.chat.options.temperature=0.7
```

### 4. Install Dependencies

```bash
mvn clean install
```

## 🏃 Running the Application

```bash
mvn spring-boot:run
```

### Testing the Endpoint

```bash
curl http://localhost:8080/api/incidents/analyze
```

Or open in browser: [http://localhost:8080/api/incidents/analyze](http://localhost:8080/api/incidents/analyze)

## 📁 Project Structure

```
operations-mcp-client/
├── src/main/java/it/matteoroxis/operations_mcp_client/
│   ├── config/
│   │   ├── McpClientConfig.java           # McpSyncClient configuration
│   │   └── SpringAIConfig.java            # Spring AI ChatClient configuration
│   ├── controller/
│   │   └── IncidentController.java        # REST controller GET /api/incidents/analyze
│   ├── model/
│   │   ├── Incident.java                  # Record: id, title, timestamp, status
│   │   └── SystemMetrics.java             # Record: avgLatencyMs, errorRate, timestamp
│   ├── service/
│   │   └── IncidentAnalysisService.java   # Core analysis service
│   └── OperationsMcpClientApplication.java
└── src/main/resources/
    └── application.properties
```

## 🔧 How It Works

### Execution Flow

```
GET /api/incidents/analyze
        │
        ▼
IncidentAnalysisService.analyze()
        │
        ├── McpSyncClient → callTool("getSystemMetrics")   → SystemMetrics
        ├── McpSyncClient → callTool("getRecentIncidents") → List<Incident>
        │
        ▼
ChatClient (OpenAI GPT-4)
        │
        ▼
Operational recommendations in natural language
```

### Models

**`Incident`**
```java
public record Incident(String id, String title, Instant timestamp, String status) {}
```

**`SystemMetrics`**
```java
public record SystemMetrics(int avgLatencyMs, double errorRate, Instant timestamp) {}
```

### REST Endpoint

| Method | Path | Description |
|--------|------|-------------|
| `GET` | `/api/incidents/analyze` | Analyses metrics and incidents, returns GPT-4 recommendations |

### Prompt sent to GPT-4

The service dynamically builds a prompt containing:
- **System metrics**: average latency, error rate, timestamp
- **Recent incidents list**: ID, title, status, timestamp
- **Analysis constraints**: likely causes, prioritised actions, additional metrics to collect

## 📦 Main Dependencies

| Dependency | Version | Purpose |
|-----------|---------|---------|
| `spring-boot-starter-web` | 3.5.10 | REST API |
| `spring-ai-starter-mcp-client` | 1.1.2 | MCP Client |
| `spring-ai-starter-model-openai` | 1.1.2 | OpenAI integration |
| `jackson-databind` | - | JSON parsing of MCP responses |

## 🐛 Troubleshooting

### Error: "Connection refused" on startup

The MCP server is not available at `http://localhost:8081`. Make sure the server is running before starting the client.

### Error: "No compiler is provided"

Maven cannot find the JDK. Make sure `JAVA_HOME` points to a JDK:

```powershell
# Windows
$env:JAVA_HOME="C:\Program Files\Java\jdk-25"
```

### Error: "Invalid API Key"

Verify that the `OPENAI_API_KEY` environment variable is set correctly.

### Incident fields show "N/A" or "UNKNOWN"

Make sure the MCP server returns JSON in the expected format:
```json
[
  { "id": "INC-42", "title": "...", "timestamp": 1234567890, "status": "Resolved" }
]
```
The `timestamp` field must be an epoch number in seconds (not an ISO-8601 string).

## 📚 Technologies Used

- **Java 25**
- **Spring Boot 3.5.10**
- **Spring AI 1.1.2**
- **MCP Java SDK** (via `spring-ai-starter-mcp-client`)
- **OpenAI GPT-4**
- **Jackson**
- **Maven**

## 📄 License

This project is an educational example.

## 👤 Author

Matteo Roxis
