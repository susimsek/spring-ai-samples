package io.github.susimsek.springaisamples.service;

import io.github.susimsek.springaisamples.constant.CacheName;
import io.github.susimsek.springaisamples.entity.Message;
import io.github.susimsek.springaisamples.repository.MessageRepository;
import java.util.Map;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class MessageService {

    private final MessageRepository messageRepository;

    @Cacheable(value = CacheName.MESSAGES_CACHE, key = "#locale")
    public Map<String, String> getMessages(String locale) {
        return messageRepository.findByLocale(locale)
            .stream()
            .collect(Collectors.toMap(Message::getCode, Message::getContent));
    }
}