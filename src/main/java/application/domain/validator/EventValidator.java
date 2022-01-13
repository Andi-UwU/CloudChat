package application.domain.validator;

import application.domain.Event;
import application.exceptions.ValidationException;

public class EventValidator implements Validator<Event> {

    @Override
    public void validate(Event entity) throws ValidationException {
        String errors = "";

        if (entity.getAuthor() == null)
            errors += "The author of the event is not set!\n";
        if (entity.getTitle() == null)
            errors += "The title of the event is not set!\n";
        if (entity.getDescription() == null)
            errors += "The description of the event is not set!\n";
        if (entity.getCreationDate() == null)
            errors += "The creation date of the event is not set!\n";
        if (entity.getCreationDate() == null)
            errors += "The date of the event is not set!\n";
        if (entity.getCreationDate() != null && entity.getCreationDate() != null) {
            if (entity.getCreationDate().toLocalDate().isAfter(entity.getEventDate()))
                errors += "The date of the event can not be before creating date!\n";
        }
        if (!errors.equals(""))
            throw new ValidationException(errors);
    }
}
