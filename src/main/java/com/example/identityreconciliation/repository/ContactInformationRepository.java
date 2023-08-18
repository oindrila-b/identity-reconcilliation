package com.example.identityreconciliation.repository;

import com.example.identityreconciliation.models.Contact;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ContactInformationRepository extends JpaRepository<Contact, Long> {

    List<Contact> findAllByPhoneNumber(Long phoneNumber);

    List<Contact> findAllByEmailId(String emailId);

    boolean existByEmail(String email);

    boolean existByPhone(Long phoneNumber);

}
