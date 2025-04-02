package com.hitorro.controller;

import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.client.advisor.MessageChatMemoryAdvisor;
import org.springframework.ai.chat.memory.InMemoryChatMemory;
import org.springframework.ai.chat.prompt.Prompt;
import org.springframework.ai.chat.prompt.PromptTemplate;
import org.springframework.ai.converter.ListOutputConverter;
import org.springframework.core.convert.support.DefaultConversionService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@RestController
public class ChatController {


    private final ChatClient chatClient;
    //private final VectorStore vectorStore;

    public ChatController(ChatClient.Builder chatBuilder/*, VectorStore vectorStore*/) {
        //this.vectorStore = vectorStore;


        this.chatClient = chatBuilder
                .defaultSystem("You are useful assistant, expert in hurricanes.") // Set the system prompt
                .defaultAdvisors(new MessageChatMemoryAdvisor(new InMemoryChatMemory())) // Enable chat memory
                //.defaultAdvisors(new QuestionAnswerAdvisor(vectorStore)) // Enable RAG
                .build();
    }

    @GetMapping("/ai/chat")
    public String chat(@RequestParam(value = "message", defaultValue = "charles dickens") String message) {

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

         String res =  chatClient.prompt(prompt).call().content();
         return res;
    }
}
