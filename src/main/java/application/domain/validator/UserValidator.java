package application.domain.validator;


import application.domain.User;
import application.exceptions.ValidationException;

public class UserValidator implements Validator<User>{

    /**
     * Validates the user given
     * @param user User
     * @throws ValidationException entity fields are invalid or null for the specified user
     */
    @Override
    public void validate(User user) throws ValidationException {

        String errors = "";
        if (user.getFirstName().equals(""))
            errors += "Invalid first name!\n";
        else if (user.getFirstName().length()>50)
            errors+= "First name must be under 50 characters\n";
        if (user.getLastName().equals(""))
            errors += "Invalid last name!\n";
        else if (user.getLastName().length()>50)
            errors+= "Last name must be under 50 characters!\n";
        if (user.getId() != null){
            if (user.getId() < 0)
                errors += "Invalid id!\n";
        }
        if (user.getUserName().length()<3 || user.getUserName().length()>25)
            errors+= "Username must be between 3 and 25 characters!\n";
        if (user.getPassWord().length()<4 || user.getPassWord().length()>16)
            errors+= "Password must be between 4 and 16 characters!\n";

        if (!errors.equals(""))
            throw new ValidationException(errors);
    }

}
