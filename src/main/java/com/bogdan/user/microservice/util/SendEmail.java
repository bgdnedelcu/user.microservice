package com.bogdan.user.microservice.util;

import com.bogdan.user.microservice.constants.AppConstants;
import com.bogdan.user.microservice.constants.EmailConstants;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import javax.mail.Message;
import javax.mail.PasswordAuthentication;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;
import java.util.Date;
import java.util.Properties;

@NoArgsConstructor
@Setter
@Getter
@Slf4j
@Component
public class SendEmail {

    public void sendRegisterEmail(final String key, final String email) {

        log.debug("ENTER sendEmail with email = " + email);
        try {
            Properties props = new Properties();
            props.put("mail.smtp.auth", "true");
            props.put("mail.smtp.starttls.enable", "true");
            props.put("mail.smtp.host", EmailConstants.SMTP_SERVER);
            props.put("mail.smtp.port", "587");
            Session session = Session.getInstance(props, new javax.mail.Authenticator() {
                protected PasswordAuthentication getPasswordAuthentication() {
                    return new PasswordAuthentication(EmailConstants.USER_MAIL, EmailConstants.PASS_MAIL);
                }
            });

            Message msg = new MimeMessage(session);
            msg.setFrom(new InternetAddress(EmailConstants.SENDER_EMAIL_ADDRESS, false));
            msg.setRecipients(Message.RecipientType.TO, InternetAddress.parse(email));
            msg.setSubject(EmailConstants.MAIL_BODY);
            msg.setContent(EmailConstants.BODY_MAIL + "http://" + AppConstants.DOMAIN + ":" + AppConstants.PORT
                    + "/videoplatform/api/account/finishregistration/" + key, "text/html");
            msg.setSentDate(new Date());
            Transport.send(msg);

        } catch (Exception e) {
            System.out.println(e.getMessage());
        }
    }

}