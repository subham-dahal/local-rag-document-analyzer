# Local RAG Document Analyzer 🦙

[![CI](https://github.com/subham-dahal/local-rag-document-analyzer/actions/workflows/ci.yml/badge.svg)](https://github.com/subham-dahal/local-rag-document-analyzer/actions/workflows/ci.yml)
![Java](https://img.shields.io/badge/Java-17-blue)
![Spring Boot](https://img.shields.io/badge/Spring_Boot-3.4.0-brightgreen)
![Spring AI](https://img.shields.io/badge/Spring_AI-1.0.0--M6-green)
![Ollama](https://img.shields.io/badge/Ollama-Local_LLM-black)

I built this project to get hands-on with Retrieval-Augmented Generation (RAG) without paying for external APIs or sending private documents to the cloud.

It is a local Spring Boot application that reads a PDF, converts the text into vector embeddings, and lets you ask questions about the document using a local model (Mistral) running in Ollama. It also comes with a simple HTML/JS chat page.

## 🛠️ The Tech Stack
* **Backend:** Java 17, Spring Boot 3.4
* **AI integration:** Spring AI
* **Local inference:** Ollama
* **Models:** Mistral (chat) and `mxbai-embed-large` (embeddings)
* **Document parsing:** Apache Tika
* **Frontend:** Vanilla HTML, CSS and JavaScript
* **Testing:** JUnit 5, Mockito, Spring MockMvc

## ⚙️ How it Works
1. **Ingestion:** On startup, the app reads the PDF with Apache Tika and splits it into 500-token chunks.
2. **Embedding:** Each chunk is turned into a vector with `mxbai-embed-large` and kept in memory in Spring AI's `SimpleVectorStore`.
3. **Retrieval:** When you ask a question, a `QuestionAnswerAdvisor` looks up the chunks most similar to it and adds them to the prompt.
4. **Answering:** Mistral answers using those chunks as context, and `/api/ask` returns the answer as plain text.

## 🚀 How to Run It Locally

### 1. Prerequisites
Java 17 and [Ollama](https://ollama.com/download) (available for macOS, Windows and Linux). Make sure Ollama is running.

### 2. Pull the models
```bash
ollama pull mistral
ollama pull mxbai-embed-large
```

### 3. Start the app

**macOS / Linux**
```bash
./mvnw spring-boot:run
```

**Windows**
```powershell
.\mvnw.cmd spring-boot:run
```

Open **http://localhost:8080** and ask something about the document. The sample PDF is a short summary of Liverpool FC, so you could try *"Where do Liverpool play their home games?"*

You can also call the API directly:

```bash
curl "http://localhost:8080/api/ask?query=How%20many%20Champions%20League%20titles%20have%20Liverpool%20won%3F"
```

(On Windows PowerShell, use `curl.exe` so you get the real curl rather than the PowerShell alias.)

### Using your own document
Replace `src/main/resources/docs/Document-Analyzer.pdf`, or point the app at another file. Anything Apache Tika can read works (PDF, DOCX, TXT and more).

```bash
# macOS / Linux
./mvnw spring-boot:run -Dspring-boot.run.arguments="--app.document.location=file:/path/to/your.pdf"
```

```powershell
# Windows
.\mvnw.cmd spring-boot:run "-Dspring-boot.run.arguments=--app.document.location=file:C:/path/to/your.pdf"
```

## 🔧 Configuration

In `src/main/resources/application.properties`:

| Property | Default | Description |
|----------|---------|-------------|
| `spring.ai.ollama.base-url` | `http://localhost:11434` | Where Ollama is running |
| `spring.ai.ollama.chat.options.model` | `mistral` | Chat model |
| `spring.ai.ollama.embedding.options.model` | `mxbai-embed-large` | Embedding model |
| `app.document.location` | `classpath:docs/Document-Analyzer.pdf` | Document loaded on startup |

## 🧪 Tests

```bash
./mvnw test          # macOS / Linux
.\mvnw.cmd test      # Windows
```

The tests don't need Ollama running because the models and the vector store are mocked:

* `DocumentIngestionServiceTest`: runs the real Tika reader and splitter on the sample PDF and on a long text file
* `ChatControllerTest`: the `/api/ask` endpoint, that the retrieval advisor is set up, and that a missing question returns 400
* `VectorStoreConfigTest`: an empty store, and loading a saved store from disk
* `DocumentAnalyzerApplicationTests`: the whole application starts

GitHub Actions runs them on Linux, Windows and macOS on every push.

## 📁 Project Structure

```
src/main/java/com/documentanalyzer/
  DocumentAnalyzerApplication.java
  config/VectorStoreConfig.java           vector store setup
  service/DocumentIngestionService.java   read, split, embed and store the document
  controller/ChatController.java          GET /api/ask
src/main/resources/
  application.properties
  docs/Document-Analyzer.pdf              sample document
  static/index.html                       chat page
```

## 🔭 What's Next
* Stream answers word by word to the chat page instead of waiting for the full answer
* Save the vector store to disk so the document doesn't need to be embedded again on every startup
* Let users upload their own documents from the UI
