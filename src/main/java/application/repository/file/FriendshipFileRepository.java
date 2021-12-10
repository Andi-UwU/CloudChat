package application.repository.file;

import application.domain.Friendship;
import application.domain.Tuple;
import application.domain.User;
import application.exceptions.RepositoryException;
import application.exceptions.ValidationException;
import application.domain.validator.Validator;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.List;

public class FriendshipFileRepository extends FileRepository<Tuple<Integer, Integer>, Friendship> {

    /**
     * constructor
     * @param fileName
     * @param validator
     * @throws ValidationException
     * @throws IOException
     */
    public FriendshipFileRepository(String fileName, Validator validator) throws ValidationException, IOException, RepositoryException {
        super(fileName, validator);
    }

    /**
     * create an entity using a list of attributes
     * @param attributes
     * @return the entity created
     */
    @Override
    protected Friendship extractEntity(List<String> attributes) {
        Friendship friendship = new Friendship();
        Integer left = Integer.parseInt(attributes.get(0));
        Integer right = Integer.parseInt(attributes.get(1));
        LocalDateTime date = LocalDateTime.parse(attributes.get(2));

        Tuple<Integer, Integer> id = new Tuple<Integer, Integer>(left, right);

        friendship.setId(id);
        friendship.setDate(date);

        return friendship;
    }

    /**
     * create an entity as String
     * @param entity
     * @return String representing the given entity
     */
    @Override
    protected String createEntityAsString(Friendship entity) {
        return entity.getId().getLeft() + ";" + entity.getId().getRight() + ";" + entity.getDate();
    }

}
