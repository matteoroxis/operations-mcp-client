# operations-mcp-client

Un Enterprise Operations Assistant costruito con Model Context Protocol (MCP), Java e Spring Boot.

## 🚀 Descrizione

Questo progetto integra:
- **Model Context Protocol (MCP)**: per la comunicazione con strumenti esterni
- **Spring AI**: per l'integrazione con OpenAI GPT-4
- **Spring Boot**: per l'architettura enterprise

L'applicazione analizza le metriche di sistema ottenute tramite MCP e fornisce raccomandazioni intelligenti usando GPT-4.

## 📋 Prerequisiti

- **JDK 17+** (consigliato JDK 21 o superiore)
- **Maven 3.8+**
- **Node.js 18+** (per il server MCP)
- **Chiave API OpenAI**

## ⚙️ Configurazione

### 1. Configura la chiave API di OpenAI

Imposta la variabile d'ambiente `OPENAI_API_KEY`:

**Windows (PowerShell):**
```powershell
$env:OPENAI_API_KEY="sk-your-api-key-here"
```

**Linux/Mac:**
```bash
export OPENAI_API_KEY="sk-your-api-key-here"
```

Oppure modifica `src/main/resources/application.properties` e sostituisci `${OPENAI_API_KEY}` con la tua chiave direttamente.

### 2. Installa le dipendenze

```bash
mvn clean install
```

### 3. Verifica Node.js

Il server MCP richiede Node.js. Verifica l'installazione:

```bash
node --version
npx --version
```

## 🏃 Esecuzione

### Avvio dell'applicazione

```bash
mvn spring-boot:run
```

### Test dell'endpoint

Una volta avviata l'applicazione, puoi testare l'endpoint di analisi:

```bash
curl http://localhost:8080/api/incidents/analyze
```

Oppure apri nel browser: [http://localhost:8080/api/incidents/analyze](http://localhost:8080/api/incidents/analyze)

## 📁 Struttura del Progetto

```
operations-mcp-client/
├── src/main/java/it/matteoroxis/operations_mcp_client/
│   ├── config/
│   │   ├── McpClientConfiguration.java    # Configurazione MCP Client
│   │   └── SpringAIConfig.java            # Configurazione Spring AI
│   ├── controller/
│   │   └── IncidentController.java        # REST controller
│   ├── model/
│   │   ├── SystemMetrics.java             # Modello metriche sistema
│   │   └── Incident.java                  # Modello incidente
│   ├── service/
│   │   └── IncidentAnalysisService.java   # Servizio di analisi
│   └── OperationsMcpClientApplication.java
└── src/main/resources/
    └── application.properties              # Configurazione Spring
```

## 🔧 Funzionalità

### IncidentAnalysisService

Il servizio principale che:
1. **Recupera le metriche** di sistema tramite il protocollo MCP
2. **Analizza i dati** usando OpenAI GPT-4
3. **Fornisce raccomandazioni** operative

### Endpoint REST

- `GET /api/incidents/analyze`: Analizza le metriche di sistema correnti e restituisce raccomandazioni

## 🐛 Troubleshooting

### Errore: "No compiler is provided"

Maven non trova il JDK. Assicurati che `JAVA_HOME` punti a un JDK (non JRE):

```powershell
# Windows
$env:JAVA_HOME="C:\Program Files\Java\jdk-21"

# Linux/Mac
export JAVA_HOME="/usr/lib/jvm/java-21-openjdk"
```

### Errore: "Cannot connect to MCP server"

Verifica che Node.js sia installato e che `npx` sia disponibile:

```bash
npx -y @modelcontextprotocol/server-everything --version
```

### Errore: "Invalid API Key"

Verifica che la variabile d'ambiente `OPENAI_API_KEY` sia configurata correttamente.

## 📚 Tecnologie Utilizzate

- **Spring Boot 3.5.10**: Framework Java enterprise
- **Spring AI 1.1.2**: Integrazione con modelli AI
- **Model Context Protocol SDK 0.17.0**: Client per MCP
- **OpenAI GPT-4**: Modello di linguaggio per l'analisi
- **Jackson**: Parsing JSON
- **Maven**: Build tool

## 📄 Licenza

Questo progetto è un esempio educativo.

## 👤 Autore

Matteo Roxis
