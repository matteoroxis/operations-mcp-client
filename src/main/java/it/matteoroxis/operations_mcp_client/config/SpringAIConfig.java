package it.matteoroxis.operations_mcp_client.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.ai.chat.client.ChatClient;


@Configuration
public class SpringAIConfig {

    @Bean
    public ChatClient openAiChatClient() {
        return ChatClient.builder(openAiChatClient().build());
    }
}
