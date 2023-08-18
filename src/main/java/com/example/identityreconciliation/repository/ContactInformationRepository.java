package com.example.identityreconciliation.repository;

import com.example.identityreconciliation.models.Contact;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ContactInformationRepository extends JpaRepository<Contact, Long> {

    List<Contact> findAllByPhoneNumber(Long phoneNumber);

    List<Contact> findAllByEmail(String emailId);

    Contact findByEmail(String email);

    Contact findByPhoneNumber(Long phoneNumber);

    Contact findByEmailAndPhoneNumber(String email, Long phone);

}
