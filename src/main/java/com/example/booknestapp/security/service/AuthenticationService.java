package com.example.booknestapp.security.service;

public interface AuthenticationService {

    String getAuthenticatedUserEmail();

    boolean checkIsPermitAll(String uriFirst);
}
