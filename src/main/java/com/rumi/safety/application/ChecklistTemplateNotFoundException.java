package com.rumi.safety.application;

import java.util.UUID;

public class ChecklistTemplateNotFoundException extends ResourceNotFoundException {

    private ChecklistTemplateNotFoundException(String message) {
        super(message);
    }

    public static ChecklistTemplateNotFoundException noTemplate() {
        return new ChecklistTemplateNotFoundException("no checklist template exists");
    }

    public static ChecklistTemplateNotFoundException withId(UUID templateId) {
        return new ChecklistTemplateNotFoundException("checklist template " + templateId + " does not exist");
    }
}
