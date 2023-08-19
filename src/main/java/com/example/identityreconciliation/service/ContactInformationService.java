package com.example.identityreconciliation.service;

import com.example.identityreconciliation.enums.LinkPrecedence;
import com.example.identityreconciliation.models.Contact;
import com.example.identityreconciliation.repository.ContactInformationRepository;
import com.example.identityreconciliation.response.Response;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
@Slf4j
@NoArgsConstructor
@AllArgsConstructor
public class ContactInformationService {

    @Autowired
    private ContactInformationRepository contactInformationRepository;

    public Long saveContactInformation(Contact contact) {
        log.info("Saving contact info{} ", contact);
        Contact c = new Contact();
        if (contact!=null){
            c = contactInformationRepository.save(contact);
        }
        log.info("Saved contact info with id {}", c.getId() );
        return c.getId();
    }

    public List<Contact> getContactsByPhoneNumber(Long phoneNumber){
        log.info("Received phone number {} for contact retrieval", phoneNumber);
        List<Contact> retrievedContacts;
        retrievedContacts = contactInformationRepository.findAllByPhoneNumber(phoneNumber);

        if (retrievedContacts!=null && !retrievedContacts.isEmpty()) return retrievedContacts;
        return new ArrayList<>();
    }

    public List<Contact> getContactsByEmail(String email){
        log.info("Received email {} for contact retrieval", email);
        List<Contact> retrievedContacts = new ArrayList<>();
        retrievedContacts = contactInformationRepository.findAllByEmail(email);
        log.info("Retrieved Contacts {}", retrievedContacts);
        return retrievedContacts;
    }

    public Response processContactToCreateResponseBody(List<Contact> contactList) {
        Response response;
        List<String> emails = new ArrayList<>();
        List<Long> phoneNumbers = new ArrayList<>();
        List<Long> secondaryIds = new ArrayList<>();
        Long primaryId = 0L;
        for (Contact c: contactList) {
            if (c.getLinkPrecedence() == LinkPrecedence.Primary) primaryId = c.getId();
            if (!emails.contains(c.getEmail())) {
                if (c.getEmail()!=null){
                    emails.add(c.getEmail());
                }
            }
            if (!phoneNumbers.contains(c.getPhoneNumber()))
            {
                if (c.getPhoneNumber()!=null){
                    phoneNumbers.add(c.getPhoneNumber());
                }
            }
            if (c.getLinkPrecedence() == LinkPrecedence.Secondary) {
                if (!secondaryIds.contains(c.getId())){
                    secondaryIds.add(c.getId());
                }
            }
        }

        response = Response.builder().
                emailIds(emails).
                phoneNumbers(phoneNumbers).
                secondaryContactIds(secondaryIds).
                primaryContactId(primaryId).build();

        log.info("Created Response : {}" , response);

        return response;
    }

    public boolean doesEmailExist(String email) {
        return contactInformationRepository.findByEmail(email)!= null && !contactInformationRepository.findByEmail(email).isEmpty();
    }

    public boolean doesPhoneNumberExist(Long phone) {
        return contactInformationRepository.findByPhoneNumber(phone) != null && !contactInformationRepository.findByPhoneNumber(phone).isEmpty();
    }

    public Contact getContactWithPrimaryLinkForEmail(String email) {
        List<Contact> retrievedList = contactInformationRepository.findAllByEmail(email);
        for (Contact c: retrievedList) {
            if (c.getLinkPrecedence() == LinkPrecedence.Primary) return c;
        }
        return null;
    }

    public Contact getContactWithPrimaryLinkForPhone(Long phoneNumber) {
        List<Contact> retrievedList = contactInformationRepository.findAllByPhoneNumber(phoneNumber);
        for (Contact c: retrievedList) {
            if (c.getLinkPrecedence() == LinkPrecedence.Primary) {
                log.info("Retrieved Contact {}",c);
                return c;
            }
        }
        return null;
    }

    public Contact getContactWithEmailAndPhoneNumber(String email, Long phoneNumber) {
        return contactInformationRepository.findByEmailAndPhoneNumber(email, phoneNumber);
    }

}
