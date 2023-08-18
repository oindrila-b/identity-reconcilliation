package com.example.identityreconciliation.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Response {

    private Long primaryContactId;
    private List<String> emailIds;
    private List<Long> phoneNumbers;
    private List<Long> secondaryContactIds;

}
