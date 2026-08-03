# Local RAG Document Analyzer 🦙

![Java](https://img.shields.io/badge/Java-17-blue)
![Spring Boot](https://img.shields.io/badge/Spring_Boot-3.4.0-brightgreen)
![Spring AI](https://img.shields.io/badge/Spring_AI-1.0.0--M6-green)
![Ollama](https://img.shields.io/badge/Ollama-Local_LLM-black)

I built this project to get hands-on with Retrieval-Augmented Generation (RAG) without paying for external APIs or sending private documents to the cloud. 

It is a local Spring Boot application that reads a PDF, converts the text into vector embeddings, and lets you ask questions about the document using a local LLM (Mistral). I also added a vanilla HTML/JS frontend that uses Server-Sent Events to stream the model's responses in real time, making it feel just like ChatGPT.

## 🛠️ The Tech Stack
* **Backend:** Java 17, Spring Boot 3.4
* **AI Integration:** Spring AI
* **Local Inference:** Ollama
* **Models:** Mistral (for chat) and `mxbai-embed-large` (for vector embeddings)
* **Frontend:** Vanilla HTML, CSS, and JavaScript 

## ⚙️ How it Works
1. **Ingestion:** On startup, the app uses Apache Tika to read a local PDF and split it into manageable chunks.
2. **Embedding:** It passes those chunks to Ollama to generate vector embeddings and saves them locally using Spring's `SimpleVectorStore`.
3. **Retrieval:** When you ask a question in the UI, the backend embeds your query, searches the vector store for the most relevant document chunks, and packages everything into a prompt.
4. **Streaming:** Mistral generates the answer, and Spring WebFlux (`Flux<String>`) streams the text back to the frontend word by word.

## 🚀 How to Run It Locally

### 1. Prerequisites
You will need Java 17 installed, along with [Ollama](https://ollama.com/) running on your machine.

### 2. Pull the Models
Open your terminal and download the required models into your local Ollama instance:
```bash
ollama pull mistral
ollama pull mxbai-embed-large
