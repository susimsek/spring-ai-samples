package io.github.susimsek.springaisamples.repository;

import io.github.susimsek.springaisamples.entity.Message;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface MessageRepository extends JpaRepository<Message, Long> {

    Optional<Message> findByLocaleAndCode(String locale, String code);

    @Query("SELECT m FROM Message m WHERE m.locale = :locale AND m.code LIKE CONCAT(:prefix, '%')")
    List<Message> findByLocaleAndCodePrefix(@Param("locale") String locale, @Param("prefix") String prefix);

    List<Message> findByLocale(String locale);

}