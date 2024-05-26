package io.github.susmisek.springaisamples.config;

import java.io.File;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import lombok.extern.slf4j.Slf4j;
import org.springframework.ai.document.Document;
import org.springframework.ai.embedding.EmbeddingClient;
import org.springframework.ai.reader.TextReader;
import org.springframework.ai.transformer.splitter.TextSplitter;
import org.springframework.ai.transformer.splitter.TokenTextSplitter;
import org.springframework.ai.vectorstore.SimpleVectorStore;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.Resource;

@Configuration(proxyBeanMethods = false)
@Slf4j
public class RagConfig {

    private static final String DEFAULT_VECTOR_STORE_NAME = "vectorstore.json";
    private static final String DATA_DIRECTORY = "data";

    @Value("${vectorstore.name:" + DEFAULT_VECTOR_STORE_NAME + "}")
    private String vectorStoreName;

    @Value("classpath:/docs/olympic-faq.txt")
    private Resource faq;

    @Bean
    SimpleVectorStore simpleVectorStore(EmbeddingClient embeddingClient,
                                        TextReader textReader,
                                        TextSplitter textSplitter) {
        var simpleVectorStore = new SimpleVectorStore(embeddingClient);
        var vectorStoreFile = getVectorStoreFile();
        try {
            if (vectorStoreFile.exists()) {
                log.info("Vector store file '{}' exists. Loading vectors from file.",
                    vectorStoreFile.getAbsolutePath());
                simpleVectorStore.load(vectorStoreFile);
            } else {
                log.info("Vector store file '{}' does not exist. Loading documents from '{}'.",
                    vectorStoreFile.getAbsolutePath(), faq.getFilename());
                textReader.getCustomMetadata().put("filename", "olympic-faq.txt");
                List<Document> documents = textReader.get();
                List<Document> splitDocuments = textSplitter.apply(documents);
                simpleVectorStore.add(splitDocuments);
                simpleVectorStore.save(vectorStoreFile);
                log.info("Documents loaded and vectors saved to file '{}'.", vectorStoreFile.getAbsolutePath());
            }
        } catch (Exception e) {
            log.error("Error occurred while configuring SimpleVectorStore: {}", e.getMessage());
        }
        return simpleVectorStore;
    }

    @Bean
    TextReader textReader() {
        return new TextReader(faq);
    }

    @Bean
    TextSplitter textSplitter() {
        return new TokenTextSplitter();
    }

    File getVectorStoreFile() {
        if (vectorStoreName == null || vectorStoreName.isEmpty()) {
            throw new IllegalArgumentException("Vector store name must not be null or empty");
        }
        Path path = Paths.get("src", "main", "resources", DATA_DIRECTORY, vectorStoreName);
        return path.toFile();
    }
}
