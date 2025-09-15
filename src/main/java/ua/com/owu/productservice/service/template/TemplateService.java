package ua.com.owu.productservice.service.template;

import java.util.Map;

public interface TemplateService {

    String renderTemplate(String templateName, Map<String, Object> contextData);
}
