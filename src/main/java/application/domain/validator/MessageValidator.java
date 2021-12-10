package application.domain.validator;

import application.domain.Message;
import application.domain.User;
import application.exceptions.ValidationException;


public class MessageValidator implements Validator<Message> {

    /**
     * Validates a given message
     * @param message Message
     * @throws ValidationException if the message is invalid (null IDs, null text or replying to more than 1 user)
     */
    @Override
    public void validate(Message message) throws ValidationException {
        String errors = "";

        if (message.getFrom() == null)
            errors += "Invalid sender!\n";
        if (message.getTo() == null)
            errors += "Invalid receiver/s!\n";
        for (User user : message.getTo()){
            if (user == null) {
                errors += "Invalid receiver";
            }
        }
        if (message.getText().equals("")){
            errors += "Invalid text!\n";
        }
        if (message.getReplyOf().isPresent() && message.getTo().size() != 1)
            errors += "A reply message must have only one receiver!\n";

        if (!errors.equals(""))
            throw new ValidationException(errors);
    }
}
