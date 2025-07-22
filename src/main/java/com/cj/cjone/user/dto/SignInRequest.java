package com.cj.cjone.user.dto;

public record SignInRequest(
        String email,
        String password
) {

}