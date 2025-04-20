package com.example.tp_book_number.WS;

import com.example.tp_book_number.beans.Contact;
import com.example.tp_book_number.dao.ContactIDao;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/contact")
@CrossOrigin("*")
public class ContactRest {

    @Autowired
    private ContactIDao contactDao;

    @GetMapping("/")
    public List<Contact> findAll() {
        return contactDao.findAll();
    }

    @PostMapping("/batch")
    public ResponseEntity<String> saveContactsBatch(@RequestBody List<Contact> contacts) {
        try {
            int savedCount = 0;
            int updatedCount = 0;

            for (Contact contact : contacts) {
                // Vérifier si un contact avec le même nom existe déjà
                Contact existingContact = contactDao.findByName(contact.getName());

                if (existingContact == null) {
                    // Nouveau contact, on le sauvegarde
                    contactDao.save(contact);
                    savedCount++;
                } else if (!existingContact.getNumber().equals(contact.getNumber())) {
                    // Contact existant avec numéro différent, on met à jour
                    existingContact.setNumber(contact.getNumber());
                    contactDao.save(existingContact);
                    updatedCount++;
                }
                // Si le contact existe déjà avec le même numéro, on ne fait rien
            }

            return ResponseEntity.ok("Synchronisation terminée: " + savedCount + " nouveaux contacts ajoutés, "
                    + updatedCount + " contacts mis à jour");
        } catch (Exception e) {
            return ResponseEntity.status(500).body("Erreur lors de la sauvegarde des contacts: " + e.getMessage());
        }
    }

    @GetMapping("/name/{name}")
    public Contact findByName(@PathVariable String name) {
        return contactDao.findByName(name);
    }

    @PostMapping("/")
    public Contact save(@RequestBody Contact contact) {
        return contactDao.save(contact);
    }

    @DeleteMapping("/id/{id}")
    public void deleteById(@PathVariable Long id) {
        contactDao.deleteById(id);
    }
}