package application.domain.validator;

import application.exceptions.ValidationException;

public interface Validator<T> {
    /**
     * Validates a given entity
     * @param entity generic type T
     * @throws ValidationException if the entity is not valid
     */
    void validate(T entity) throws ValidationException;
}
