# Simple spring ai experiments

## Overview
Starter playground to work with chat and embeddings using spring AI

## Prerequisites
- Java 17/21
- Maven 3.3+ for building and managing the project

## Technologies
- **Spring Boot**: Simplifies the bootstrapping and development of new Spring Framework applications.
- **Spring AI**: Provides AI capabilities integrated seamlessly with Spring applications. In this project, we use local LLM `spring-ai-ollama-spring-boot-starter` for integrating the LLama3 llm.
- **Ollama**: a tool to run Large Language Models locally for manage safe private data.

## Getting Started

1. **Install and run LLama3 with Ollama**:
    ```bash
    ollama run llama3 

2. **Clone the Repository**:
   ```bash
   git clone https://your-repository-url-here
   cd cloned_name

3. **Build the Application**:
   ```bash
   mvn clean install

4. **Run the Application**:
   ```bash
   mvn spring-boot:run



Structured output:

https://docs.spring.io/spring-ai/reference/api/structured-output-converter.html

Embeddings:

https://docs.spring.io/spring-ai/reference/api/embeddings/ollama-embeddings.html

Embeddings demo:

https://github.com/tzolov/spring-ai-ollama-huggingface-demo/blob/main/src/main/java/springai/example/ollamahf/SpringAiOllamaHuggingFaceApplication.java