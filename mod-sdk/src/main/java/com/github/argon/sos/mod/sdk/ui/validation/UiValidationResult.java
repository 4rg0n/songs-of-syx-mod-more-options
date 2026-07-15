package com.github.argon.sos.mod.sdk.ui.validation;

import lombok.*;
import snake2d.util.gui.renderable.RENDEROBJ;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Getter
@ToString
@RequiredArgsConstructor
public class UiValidationResult {
    private final Map<RENDEROBJ, ValidationErrors<?>> errors;

    public UiValidationResult() {
        errors = new HashMap<>();
    }

    public <Element> void addError(final RENDEROBJ uiElement, final Class<Element> uiElementClass, final String errorMessage) {
        addErrors(uiElement, uiElementClass, List.of(errorMessage));
    }

    public <Element> void addErrors(final RENDEROBJ uiElement, final Class<Element> uiElementClass, final List<String> errorMessages) {
        ValidationErrors<Element> errors = ValidationErrors.<Element>builder()
            .uiElement(uiElement)
            .errors(errorMessages.stream()
                .map(ValidationErrors.ValidationError::new)
                .toList())
            .uiElementClass(uiElementClass)
            .build();

        addErrors(uiElement, errors);
    }

    public void addErrors(final RENDEROBJ uiElement, final ValidationErrors<?> errors) {
        this.errors.put(uiElement, errors);
    }

    public boolean hasError() {
        return !errors.isEmpty();
    }

    @Builder
    public record ValidationErrors<Element>(
        RENDEROBJ uiElement,
        Class<Element> uiElementClass,
        @Singular List<ValidationError> errors
    ) {
        private Element getUiElement() {
            return uiElementClass().cast(uiElement);
        }

        @Builder
        public record ValidationError(String message) {}
    }
}
