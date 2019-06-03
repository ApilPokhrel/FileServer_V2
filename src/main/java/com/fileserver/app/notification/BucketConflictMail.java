package com.fileserver.app.notification;

import com.fileserver.app.entity.Mail;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;
import org.thymeleaf.context.Context;
import org.thymeleaf.spring5.SpringTemplateEngine;

import javax.mail.MessagingException;
import javax.mail.internet.MimeMessage;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;


@Service
public class BucketConflictMail {

    private JavaMailSender javaMailSender;
    private SpringTemplateEngine templateEngine;

    @Autowired
    public BucketConflictMail(JavaMailSender javaMailSender, SpringTemplateEngine templateEngine){
        this.javaMailSender = javaMailSender;
        this.templateEngine = templateEngine;
    }




    public void sendSimpleMessage(Mail mail) throws Exception {
        MimeMessage message = javaMailSender.createMimeMessage();
        MimeMessageHelper helper = null;
        try {
            helper = new MimeMessageHelper(message,
                    MimeMessageHelper.MULTIPART_MODE_MIXED_RELATED,
                    StandardCharsets.UTF_8.name());


//        helper.addAttachment("logo.png", new ClassPathResource("memorynotfound-logo.png"));

            Context context = new Context();

            context.setVariables(mail.getModel());
            String html = templateEngine.process("mail/bucket_conflict", context);

            helper.setTo(mail.getTo());
            helper.setText(html, true);
            helper.setSubject(mail.getSubject());
            helper.setFrom(mail.getFrom());

        }catch (MessagingException e){
            throw new Exception("Error sending Bucket Conflict Mail");
        }

        javaMailSender.send(message);
    }
}
