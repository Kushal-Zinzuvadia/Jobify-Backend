package org.example.jobify.request;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class UserRoleRequest {
    private String email;
    private String role;
}
