package com.careerin.api.service;

import com.careerin.api.model.Mail;

import javax.mail.MessagingException;

public interface MailService {

    void sendEmailVerification(String emailVerificationUrl, String toAddress, String otp);

    void sendResetLink(String resetPasswordLink, String toAddress);

    void sendAccountChangeEmail(String action, String actionStatus, String toAddress);

    void send(Mail mail) throws MessagingException;


}
