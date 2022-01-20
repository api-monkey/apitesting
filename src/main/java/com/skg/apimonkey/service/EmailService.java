package com.skg.apimonkey.service;

public interface EmailService {

    void sendSimpleMail(String to, String subject, String text);
}
