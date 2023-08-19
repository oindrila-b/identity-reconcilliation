package com.example.identityreconciliation.controller;

import com.example.identityreconciliation.enums.LinkPrecedence;
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
        Response response =  new Response();
        if (!service.doesPhoneNumberExist(phoneNumber) && !service.doesEmailExist(email)) {
            log.info("Neither phone number nor email exists already in database");
            Contact contact = buildContact(email, phoneNumber, LinkPrecedence.Primary, null);
            log.info("Built new contact {}", contact);
            Long id = service.saveContactInformation(contact);
            response = service.processContactToCreateResponseBody(Arrays.asList(contact));
            log.info("Created Response {}", response);
        } else if (service.doesEmailExist(email) && !service.doesPhoneNumberExist(phoneNumber)) {
            log.info("Email exists but phone number doesn't exist in database");
            Contact primaryContact = service.getContactWithPrimaryLinkForEmail(email);
            Contact contact ;
            if (primaryContact == null) {
                contact = buildContact(email, phoneNumber, LinkPrecedence.Primary, null);
                log.info("Primary contact is null creating new primary contact");
            } else {
                contact = buildContact(email, phoneNumber, LinkPrecedence.Secondary, primaryContact.getId());
            }
            log.info("Built new contact {}", contact);
            Long id = service.saveContactInformation(contact);
            response = service.processContactToCreateResponseBody(service.getContactsByEmail(email));
            log.info("Created Response {}", response);
        } else if (!service.doesEmailExist(email) && service.doesPhoneNumberExist(phoneNumber)) {
            log.info("Email doesn't exists but phone number exists in database");
            Contact primaryContact = service.getContactWithPrimaryLinkForPhone(phoneNumber);
            Contact contact;
            if (primaryContact == null) {
                contact = buildContact(email, phoneNumber, LinkPrecedence.Primary, null);
                log.info("Primary contact for phone is null creating new contact");
            } else {
                contact = buildContact(email, phoneNumber, LinkPrecedence.Secondary, primaryContact.getId());
            }
            log.info("Built new contact {}", contact);
            Long id = service.saveContactInformation(contact);
            response = service.processContactToCreateResponseBody(service.getContactsByPhoneNumber(phoneNumber));
            log.info("Created Response {}", response);
        } else {
            log.info("Both email and phoneNumber exist");

            Contact emailContact = service.getContactWithPrimaryLinkForEmail(email);
            Contact phoneContact = service.getContactWithPrimaryLinkForPhone(phoneNumber);

            if (emailContact.equals(phoneContact)) {
                Contact contact = service.getContactWithEmailAndPhoneNumber(email, phoneNumber);
                response = Response.builder()
                        .phoneNumbers(Arrays.asList(contact.getPhoneNumber()))
                        .emailIds(Arrays.asList(contact.getEmail()))
                        .secondaryContactIds(new ArrayList<>())
                        .primaryContactId(contact.getId())
                        .build();
            } else {
                response = Response.builder()
                        .phoneNumbers(Arrays.asList(emailContact.getPhoneNumber(), phoneContact.getPhoneNumber()))
                        .emailIds(Arrays.asList(emailContact.getEmail(), phoneContact.getEmail()))
                        .secondaryContactIds(Arrays.asList(phoneContact.getId()))
                        .primaryContactId(emailContact.getId())
                        .build();
            }

            log.info("Created Response {}", response);
        }
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    private Contact buildContact(String email, Long phoneNumber, LinkPrecedence linkPrecedence, Long linkedId) {
        Contact contact = Contact.builder()
                .email(email)
                .phoneNumber(phoneNumber)
                .linkPrecedence(linkPrecedence)
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .deletedAt(null)
                .linkedId(linkedId)
                .build();

        return contact;
    }

}
