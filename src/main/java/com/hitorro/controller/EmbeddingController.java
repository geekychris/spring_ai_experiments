package com.hitorro.controller;

import com.hitorro.dao.Author;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.client.advisor.MessageChatMemoryAdvisor;
import org.springframework.ai.chat.client.advisor.QuestionAnswerAdvisor;
import org.springframework.ai.chat.memory.InMemoryChatMemory;
import org.springframework.ai.chat.prompt.Prompt;
import org.springframework.ai.chat.prompt.PromptTemplate;
import org.springframework.ai.converter.ListOutputConverter;
import org.springframework.ai.embedding.EmbeddingModel;
import org.springframework.ai.embedding.EmbeddingResponse;
import org.springframework.ai.reader.pdf.PagePdfDocumentReader;
import org.springframework.ai.transformer.splitter.TokenTextSplitter;
import org.springframework.ai.vectorstore.SimpleVectorStore;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.convert.support.DefaultConversionService;
import org.springframework.core.io.Resource;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Map;
import java.util.Scanner;

@RestController
public class EmbeddingController {

    private final EmbeddingModel embeddingModel;

    private final ChatClient chatClient;

    private final ChatClient vectorChatClient;

    @Autowired
    public EmbeddingController(EmbeddingModel embeddingModel, ChatClient.Builder chatBuilder, @Value("classpath:milton.pdf") Resource pdf) {
        this.embeddingModel = embeddingModel;

        SimpleVectorStore vectorStore =  SimpleVectorStore.builder(embeddingModel).build();;
        vectorStore.add(new TokenTextSplitter().split(new PagePdfDocumentReader(pdf).read()));

        this.chatClient = chatBuilder
                .defaultSystem("You are useful assistant, expert in hurricanes.") // Set the system prompt
                .defaultAdvisors(new MessageChatMemoryAdvisor(new InMemoryChatMemory())) // Enable chat memory
                //.defaultAdvisors(new QuestionAnswerAdvisor(vectorStore)) // Enable RAG
                .build();

        this.vectorChatClient = chatBuilder
                .defaultSystem("You are useful assistant, expert in hurricanes.") // Set the system prompt
                .defaultAdvisors(new MessageChatMemoryAdvisor(new InMemoryChatMemory())) // Enable chat memory
                .defaultAdvisors(new QuestionAnswerAdvisor(vectorStore)) // Enable RAG
                .build();
    }

    @GetMapping("/ai/embedding")
    public Map embed(@RequestParam(value = "message", defaultValue = "Tell me a joke") String message) {
        EmbeddingResponse embeddingResponse = this.embeddingModel.embedForResponse(List.of(message));
        return Map.of("embedding", embeddingResponse);
    }



    @GetMapping("/ai/chat")
    public Author chat(@RequestParam(value = "message", defaultValue = "charles dickens") String author) {

        String promptMessage = """
                Generate a list of books written by the author {author}.
                If the author is not recognized don't invent but write null inside author field.
                Each book is constituted by a book's array , each with a title and a year as values to contain.
                If you aren't positive that a book belongs to this author please don't include it.
                {format}
                """;

        String format = format = "json";

        PromptTemplate promptTemplate = new PromptTemplate(promptMessage, Map.of("author",author,"format", format));
        Prompt prompt = promptTemplate.create();
        Author a = chatClient.prompt(prompt).call().entity(Author.class);
        return a;
    }

    @GetMapping("/ai/chatvector")
    public String chatVector(@RequestParam(value = "message", defaultValue = "charles dickens") String value) {



        // 3. Start the chat loop
        System.out.println("\nI am your Hurricane Milton assistant.\n");
        System.out.print("\nUSER: ");
        System.out.println("\nASSISTANT: " +
                vectorChatClient.prompt(value) // Get the user input
                        .call()
                        .content());

        return vectorChatClient.prompt(value) // Get the user input
                .call()
                .content();
    }


    // Not really the way to return this but you can see the content comes back as json from the chat
    @GetMapping("/ai/chatjson")
    public String chatJs(@RequestParam(value = "message", defaultValue = "charles dickens") String message) {

        String promptMessage = """
                Generate a list of books written by the author {author}.
                If the author is not recognized don't invent but write null inside author field.
                Each book is constituted by a book's array , each with a title and a year as values to contain.
                If you aren't positive that a book belongs to this author please don't include it.
                {format}
                """;

        String author = message;
        ListOutputConverter listOutputConverter = new ListOutputConverter(new DefaultConversionService());
        String format = listOutputConverter.getFormat();
        format = "json";
        // FormatProvider. getFormat()
//        var outputParser = new StructuredOutputConverter<Author>(Author.class);
//        String format = outputParser.getFormat();
//        System.out.println("format = " + format);

        PromptTemplate promptTemplate = new PromptTemplate(promptMessage, Map.of("author",author,"format", format));
        Prompt prompt = promptTemplate.create();
        String  result = chatClient.prompt(prompt).call().content();
        //String res =  chatClient.prompt(prompt).call().content();
        return result;
    }
}