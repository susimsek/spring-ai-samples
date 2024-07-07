package io.github.susimsek.springaisamples.repository;

import io.github.susimsek.springaisamples.entity.Message;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;

public interface MessageRepository extends JpaRepository<Message, Long> {

    List<Message> findByLocale(String locale);

}