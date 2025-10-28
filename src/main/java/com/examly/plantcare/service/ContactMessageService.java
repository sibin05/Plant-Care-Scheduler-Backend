package com.examly.plantcare.service;

import com.examly.plantcare.entity.ContactMessage;
import com.examly.plantcare.repository.ContactMessageRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ContactMessageService {

    @Autowired
    private ContactMessageRepository repository;

    public ContactMessage saveMessage(ContactMessage message) {
        return repository.save(message);
    }

    public List<ContactMessage> getAllMessages() {
        return repository.findAll();
    }

    public List<ContactMessage> getUnreadMessages() {
        return repository.findByIsReadOrderByCreatedDateDesc(false);
    }
}