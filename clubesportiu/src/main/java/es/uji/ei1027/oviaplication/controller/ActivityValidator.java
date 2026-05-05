package es.uji.ei1027.oviaplication.controller;

import es.uji.ei1027.oviaplication.model.Activity;
import org.springframework.validation.Errors;
import org.springframework.validation.Validator;

public class ActivityValidator implements Validator {
    @Override
    public boolean supports(Class<?> clazz) {
        return Activity.class.equals(clazz);
    }

    @Override
    public void validate(Object target, Errors errors) {
        Activity activity = (Activity) target;

        if (activity.getDate() == null) {
            errors.rejectValue("date", "obligatorio", "Hace falta introducir una fecha");
        }

        if (activity.getTime() == null) {
            errors.rejectValue("time", "obligatorio", "Hace falta introducir la hora de comienzo");
        }

        if (activity.getLocation() == null || activity.getLocation().length() > 150 || activity.getLocation().trim().isEmpty()) {
            errors.rejectValue("location", "obligatorio", "Hace falta introducir un lugar valido");
        }

        if (activity.getCapacity() == null || activity.getCapacity() <= 0) {
            errors.rejectValue("capacity", "obligatorio", "La capacidad debe ser al menos 1");
        }

        if (activity.getPrice() == null || activity.getPrice() < 0) {
            errors.rejectValue("price", "obligatorio", "El precio no puede ser negativo");
        }

        if (activity.getDescription() != null && activity.getDescription().length() > 500) {
            errors.rejectValue("description", "obligatorio", "Hace falta introducir una descripción valida");
        }

        if (activity.getName() == null || activity.getName().length() > 50 || activity.getName().trim().isEmpty()) {
            errors.rejectValue("name", "obligatorio", "Hace falta introducir un nombre valido");
        }

    }

    @Override
    public Errors validateObject(Object target) {
        return Validator.super.validateObject(target);
    }
}