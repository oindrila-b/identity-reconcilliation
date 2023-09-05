package com.example.identityreconciliation.controller;

import com.example.identityreconciliation.enums.LinkPrecedence;
import com.example.identityreconciliation.models.Contact;
import com.example.identityreconciliation.response.Response;
import com.example.identityreconciliation.service.ContactInformationService;
import jakarta.annotation.Nullable;
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
@RequestMapping(path = "/identify")
@Slf4j
public class Controller {

    @Autowired
    private ContactInformationService service;

    @GetMapping
    public ResponseEntity<Response> identify(@RequestParam @Nullable String email, @RequestParam @Nullable Long phoneNumber) {
        Response response;

        if (email == null && phoneNumber!= null) {
            log.info("Email is null");
            response = service.processContactToCreateResponseBody(service.getContactsByPhoneNumber(phoneNumber));
            log.info("Created Response {}", response);
            return new ResponseEntity<>(response, HttpStatus.OK);
        }
        if (email != null && phoneNumber== null){
            log.info("Phone is Null");
            response = service.processContactToCreateResponseBody(service.getContactsByEmail(email));
            log.info("Created Response {}", response);
            return new ResponseEntity<>(response, HttpStatus.OK);
        }

        if (!service.doesPhoneNumberExist(phoneNumber) && !service.doesEmailExist(email)) {
            log.info("Neither phone number nor email exists already in database");
            Contact contact = service.buildContact(email, phoneNumber, LinkPrecedence.Primary, null);
            log.info("Built new contact {}", contact);
            Long id = service.saveContactInformation(contact);
            response = service.processContactToCreateResponseBody(Arrays.asList(contact));
            log.info("Created Response {}", response);
        } else if (service.doesEmailExist(email) && !service.doesPhoneNumberExist(phoneNumber)) {
            log.info("Email exists but phone number doesn't exist in database");
            Contact primaryContact = service.getContactWithPrimaryLinkForEmail(email);
            Contact contact ;
            if (primaryContact == null) {
                contact = service.buildContact(email, phoneNumber, LinkPrecedence.Primary, null);
                log.info("Primary contact is null creating new primary contact");
            } else {
                contact = service.buildContact(email, phoneNumber, LinkPrecedence.Secondary, primaryContact.getId());
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
                contact = service.buildContact(email, phoneNumber, LinkPrecedence.Primary, null);
                log.info("Primary contact for phone is null creating new contact");
            } else {
                contact = service.buildContact(email, phoneNumber, LinkPrecedence.Secondary, primaryContact.getId());
            }
            log.info("Built new contact {}", contact);
            Long id = service.saveContactInformation(contact);
            response = service.processContactToCreateResponseBody(service.getContactsByPhoneNumber(phoneNumber));
            log.info("Created Response {}", response);
        } else {
            log.info("Both email and phoneNumber exist");
            List<Contact> emailContacts = service.getContactsByEmail(email);
            List<Contact> phoneContact = service.getContactsByPhoneNumber(phoneNumber);

            Contact primaryEmailContact = service.returnPrimaryContactIfExists(emailContacts);
            Contact primaryPhoneContact = service.returnPrimaryContactIfExists(phoneContact);
            log.info("Primary email contact {} ", primaryEmailContact);
            log.info("Primary phone contact {}", primaryPhoneContact);

            if (primaryEmailContact!= null && primaryPhoneContact!= null && !primaryEmailContact.equals(primaryPhoneContact)){
                emailContacts.addAll(phoneContact);
                List<Long> id = new ArrayList<>();
                id.add(primaryPhoneContact.getId());
                log.info("Secondary ids {} ", id);
                response =  Response.builder()
                        .primaryContactId(primaryEmailContact.getId())
                        .secondaryContactIds(id)
                        .emailIds(Arrays.asList(primaryEmailContact.getEmail(), primaryPhoneContact.getEmail()))
                        .phoneNumbers(Arrays.asList(primaryEmailContact.getPhoneNumber(), primaryPhoneContact.getPhoneNumber()))
                        .build();
            } else {
                emailContacts.addAll(phoneContact);

                log.info("All Contacts {}", emailContacts);
                response =  Response.builder()
                        .primaryContactId(emailContacts.get(0).getId())
                        .secondaryContactIds(Arrays.asList(emailContacts.get(1).getId()))
                        .emailIds(service.extractEmails(emailContacts))
                        .phoneNumbers(service.extractPhoneNumber(emailContacts))
                        .build();
            }

            }

            log.info("Created Response {}", response);
        return new ResponseEntity<>(response, HttpStatus.OK);
    }





}
