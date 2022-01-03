package application.domain.validator;


import application.domain.User;
import application.exceptions.ValidationException;

public class UserValidator implements Validator<User>{
    /**
     * validates the user given
     * @param user User
     * @throws ValidationException
     * entity fields are null or ID is less than 0
     */
    @Override
    public void validate(User user) throws ValidationException {

        String errors = "";
        if (user.getFirstName().equals(""))
            errors += "Invalid first name!\n";
        if (user.getLastName().equals(""))
            errors += "Invalid last name!\n";
        if (user.getId() != null){
            if (user.getId() < 0)
                errors += "Invalid id!\n";
        }

        if (!errors.equals(""))
            throw new ValidationException(errors);
    }
}
