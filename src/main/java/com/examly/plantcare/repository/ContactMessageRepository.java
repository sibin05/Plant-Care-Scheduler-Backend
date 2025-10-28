package com.examly.plantcare.repository;

import com.examly.plantcare.entity.ContactMessage;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ContactMessageRepository extends JpaRepository<ContactMessage, Long> {
    List<ContactMessage> findByIsReadOrderByCreatedDateDesc(Boolean isRead);
}