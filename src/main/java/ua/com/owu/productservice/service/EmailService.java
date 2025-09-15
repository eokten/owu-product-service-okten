package ua.com.owu.productservice.service;

import jakarta.mail.internet.MimeMessage;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.core.io.Resource;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;
import ua.com.owu.productservice.dto.SendEmailDto;
import ua.com.owu.productservice.service.template.TemplateService;

import java.nio.charset.StandardCharsets;

@Service
@RequiredArgsConstructor
public class EmailService {

    private final JavaMailSender javaMailSender;

    private final TemplateService templateService;

    @Value("classpath:templates/email/product-created.html")
    private Resource exampleAttachment;

    @Value("classpath:static/images/logo.png")
    private Resource logo;

    @Value("${app.email.sender}")
    private String emailSender;

    @SneakyThrows
    public void sendEmail(SendEmailDto sendEmailDto) {
        SimpleMailMessage mailMessage = new SimpleMailMessage();

        mailMessage.setFrom(emailSender);
        mailMessage.setTo(sendEmailDto.to());
        mailMessage.setSubject(sendEmailDto.subject());

        String content = templateService.renderTemplate(sendEmailDto.templateName(), sendEmailDto.contextData());
        mailMessage.setText(content);

        javaMailSender.send(mailMessage);
    }

    @SneakyThrows
    public void sendEmailWithTemplate(SendEmailDto sendEmailDto) {
        MimeMessage mimeMessage = javaMailSender.createMimeMessage();
        MimeMessageHelper helper = new MimeMessageHelper(mimeMessage, true, StandardCharsets.UTF_8.name());

        helper.setFrom(emailSender);
        helper.setTo(sendEmailDto.to());
        helper.setSubject(sendEmailDto.subject());
        String content = templateService.renderTemplate(sendEmailDto.templateName(), sendEmailDto.contextData());
        helper.setText(content, true);

        helper.addInline("logo", logo);

        if (sendEmailDto.contextData().containsKey("attachment")) {
            ByteArrayResource resource = new ByteArrayResource((byte[]) sendEmailDto.contextData().get("attachment"));
            helper.addAttachment("product-image", resource, "image/png");
        }

        javaMailSender.send(mimeMessage);
    }
}
