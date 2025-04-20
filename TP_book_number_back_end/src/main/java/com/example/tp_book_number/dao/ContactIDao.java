package com.example.tp_book_number.dao;

import com.example.tp_book_number.beans.Contact;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ContactIDao extends JpaRepository<Contact, Long> {
    Contact findByName(String name);
    Contact findByNameAndNumber(String name, String number);
}