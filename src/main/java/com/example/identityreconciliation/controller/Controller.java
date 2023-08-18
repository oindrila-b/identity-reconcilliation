package com.example.identityreconciliation.controller;

import com.example.identityreconciliation.response.Response;
import com.example.identityreconciliation.service.ContactInformationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping(value = "/identify")
public class Controller {

    @Autowired
    ContactInformationService service;

    @GetMapping
    public ResponseEntity<Response> identify(@PathVariable  String email, @PathVariable Long phoneNumber) {

        if (service.doesEmailExist(email)) {
            System.out.println("OKay");
        }


        return new ResponseEntity<Response>(new Response(), HttpStatus.OK);
    }

}
