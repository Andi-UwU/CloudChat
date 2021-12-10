package application.domain.validator;

import application.domain.Friendship;
import application.exceptions.ValidationException;

import java.util.Objects;

public class FriendshipValidator implements Validator<Friendship> {

    /**
     * Validates the friendship given
     * @param friendship Friendship
     * @throws ValidationException if:
     * at least 1 id is null
     * - if the left id is greater
     * - if the ids are equal
     */
    @Override
    public void validate(Friendship friendship) throws ValidationException {
        String errors = "";

        if(friendship.getId().getLeft() != null && friendship.getId().getRight() != null){
            if (friendship.getId().getLeft() > friendship.getId().getRight())
                errors += "Invalid IDs order!\n";
            if (Objects.equals(friendship.getId().getLeft(), friendship.getId().getRight()))
                errors += "An user cannot be friend to himself!\n";
        }
        else {
            if (friendship.getId().getLeft() == null)
                errors += "Invalid left ID!\n";
            if (friendship.getId().getRight() == null)
                errors += "Invalid right ID!\n";
        }

        if (!errors.equals(""))
            throw new ValidationException(errors);
    }
}
