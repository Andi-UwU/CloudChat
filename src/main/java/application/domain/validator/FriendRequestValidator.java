package application.domain.validator;

import application.domain.FriendRequest;
import application.exceptions.ValidationException;

import java.util.Objects;

public class FriendRequestValidator implements Validator<FriendRequest> {

        /**
         * Validates a friend request
         * @param request FriendRequest
         * @throws ValidationException
         * at least 1 ID is null
         * - if the IDs are equal
         */
        @Override
        public void validate(FriendRequest request) throws ValidationException {
            String errors = "";

            if(request.getId().getLeft() != null && request.getId().getRight() != null) {
                if (Objects.equals(request.getId().getLeft(), request.getId().getRight()))
                    errors += "An user cannot be friend to himself!\n";
            }
            else {
                if (request.getId().getLeft() == null)
                    errors += "Invalid left ID!\n";
                if (request.getId().getRight() == null)
                    errors += "Invalid right ID!\n";
            }

            if (!errors.equals(""))
                throw new ValidationException(errors);
        }
}
