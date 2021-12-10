package application.repository.file;

import application.domain.User;
import application.domain.validator.UserValidator;
import application.exceptions.RepositoryException;
import application.exceptions.ValidationException;

import java.io.IOException;
import java.util.List;

public class UserFileRepository extends FileRepository<Integer, User>{

    /**
     * constructor
     * @param fileName
     * @param validator
     * @throws ValidationException
     * @throws IOException
     */
    public UserFileRepository(String fileName, UserValidator validator) throws ValidationException, IOException, RepositoryException {
        super(fileName, validator);
    }

    /**
     * create an entity using a list of attributes
     * @param attributes
     * @return the entity created
     */
    @Override
    protected User extractEntity(List<String> attributes) {
        User user = new User(attributes.get(1), attributes.get(2));
        user.setId(Integer.parseInt(attributes.get(0)));

        return user;
    }

    /**
     * create an entity as String
     * @param entity
     * @return String representing the given entity
     */
    @Override
    protected String createEntityAsString(User entity) {
        String line = new String(entity.getId() + ";" + entity.getFirstName() + ";" + entity.getLastName());
        return line;
    }
}
