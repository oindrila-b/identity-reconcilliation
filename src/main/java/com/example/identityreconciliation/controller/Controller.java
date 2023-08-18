package com.example.identityreconciliation.controller;

import com.example.identityreconciliation.models.Contact;
import com.example.identityreconciliation.response.Response;
import com.example.identityreconciliation.service.ContactInformationService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

@RestController
@RequestMapping(value = "/identify")
@Slf4j
public class Controller {

    @Autowired
    private ContactInformationService service;

    @GetMapping
    public ResponseEntity<Response> identify(@RequestParam String email, @RequestParam Long phoneNumber) {

        Response response = new Response();

        if (!service.doesPhoneNumberExist(phoneNumber) && !service.doesEmailExist(email)) {
           Contact contact = Contact.builder()
                   .email(email)
                   .phoneNumber(phoneNumber)
                   .linkPrecedence("primary")
                   .createdAt(LocalDateTime.now())
                   .updatedAt(LocalDateTime.now())
                   .deletedAt(null)
                   .build();

           Long id = service.saveContactInformation(contact);
           response = Response.builder()
                   .primaryContactId(id)
                   .secondaryContactIds(new ArrayList<>())
                   .emailIds(Arrays.asList(email))
                   .phoneNumbers(Arrays.asList(phoneNumber))
                   .build();
        } else if (service.doesEmailExist(email) && !service.doesPhoneNumberExist(phoneNumber)) {
            Contact primaryContact = service.getContactWithPrimaryLinkForEmail(email);
            Contact contact = Contact.builder()
                    .email(email)
                    .phoneNumber(phoneNumber)
                    .linkPrecedence("secondary")
                    .linkedId(primaryContact.getId())
                    .createdAt(LocalDateTime.now())
                    .updatedAt(LocalDateTime.now())
                    .deletedAt(null)
                    .build();
            Long id = service.saveContactInformation(contact);

            response = service.processContactToCreateResponseBody(service.getContactsByEmail(email));
        } else if (!service.doesEmailExist(email) && service.doesPhoneNumberExist(phoneNumber)) {
            Contact primaryContact = service.getContactWithPrimaryLinkForPhone(phoneNumber);
            Contact contact = Contact.builder()
                    .email(email)
                    .phoneNumber(phoneNumber)
                    .linkPrecedence("secondary")
                    .linkedId(primaryContact.getId())
                    .createdAt(LocalDateTime.now())
                    .updatedAt(LocalDateTime.now())
                    .deletedAt(null)
                    .build();
            Long id = service.saveContactInformation(contact);
            response = service.processContactToCreateResponseBody(service.getContactsByPhoneNumber(phoneNumber));
        } else {
            Contact contact = service.getContactWithEmailAndPhoneNumber(email, phoneNumber);
            response = Response.builder()
                    .phoneNumbers(Arrays.asList(contact.getPhoneNumber()))
                    .emailIds(Arrays.asList(contact.getEmail()))
                    .secondaryContactIds(new ArrayList<>())
                    .primaryContactId(contact.getId())
                    .build();
        }
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

}
