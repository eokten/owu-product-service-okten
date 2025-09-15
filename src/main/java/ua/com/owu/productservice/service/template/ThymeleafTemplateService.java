package ua.com.owu.productservice.service.template;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.thymeleaf.context.Context;
import org.thymeleaf.spring6.SpringTemplateEngine;

import java.util.Locale;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class ThymeleafTemplateService implements TemplateService {

    private final SpringTemplateEngine templateEngine;

    @Override
    public String renderTemplate(String templateName, Map<String, Object> contextData) {
        Context context = new Context(Locale.getDefault(), contextData);
        return templateEngine.process(templateName, context);
    }
}
