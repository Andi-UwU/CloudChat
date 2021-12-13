package application.service;

import application.domain.FriendRequest;
import application.domain.FriendRequestStatus;
import application.domain.Tuple;
import application.domain.validator.Validator;
import application.exceptions.RepositoryException;
import application.exceptions.ValidationException;
import application.repository.Repository;

import java.io.IOException;
import java.sql.SQLException;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Handles friend requests from the repository
 */
public class FriendRequestService {
    //TODO missing comment
    private Repository<Tuple<Integer,Integer>, FriendRequest> requestRepository;
    private Validator<FriendRequest> validatorRequest;
    public FriendRequestService (Repository<Tuple<Integer, Integer>, FriendRequest> friendRequestDatabase,
                                 Validator<FriendRequest> validator) {
        this.requestRepository=friendRequestDatabase;
        this.validatorRequest =validator;
    }

    /**
     * Find a request
     * @param idFrom Integer
     * @param idTo Integer
     * @return FriendRequest
     * @throws RepositoryException if the request doesn't exist
     * @throws ValidationException if the request is invalid
     * @throws SQLException if the database cannot be reached
     */
    public FriendRequest findRequest(Integer idFrom, Integer idTo) throws RepositoryException, ValidationException, SQLException {
        return requestRepository.find(new Tuple<>(idFrom,idTo));
    }

    /**
     * Adds a friend request
     * @param request FriendRequest
     * @throws RepositoryException if a request already exists
     * @throws ValidationException if the request is invalid
     */
    public void addRequest (FriendRequest request) throws RepositoryException, IOException, ValidationException {
        validatorRequest.validate(request);
        requestRepository.add(request);
    }

    /**
     * Deletes a friend request
     * @param idFrom Integer
     * @param idTo Integer
     * @return FriendRequest
     * @throws ValidationException if the old value is invalid
     * @throws SQLException if the database cannot be reached
     * @throws RepositoryException if the friend request doesn't exist
     * @throws IOException if the old value cannot be parsed
     */
    public FriendRequest deleteRequest (Integer idFrom, Integer idTo) throws ValidationException, SQLException, RepositoryException, IOException {
        return requestRepository.delete(new Tuple<>(idFrom,idTo));
    }

    /**
     * Updates a friend request
     * @param idFrom Integer
     * @param idTo Integer
     * @param status FriendRequestStatus
     * @return FriendRequest
     * @throws ValidationException if the old value is invalid
     * @throws SQLException if the database cannot be reached
     * @throws RepositoryException if the friend request doesn't exist
     * @throws IOException if the old value cannot be parsed
     */
    public FriendRequest updateRequest(Integer idFrom, Integer idTo, String status) throws ValidationException, SQLException, RepositoryException, IOException {
        if (status.equals("PENDING"))
            throw new ValidationException("Cannot update requests to pending! Create new request instead\n");

        FriendRequest request = new FriendRequest(FriendRequestStatus.valueOf(status));
        request.setId(new Tuple<>(idFrom,idTo));

        return requestRepository.update(request);
    }

    /**
     * Returns all the friend requests
     * @return List(FriendRequest)
     * @throws ValidationException if one or more of the entities are invalid
     * @throws SQLException if the database cannot be reached
     */
    public List<FriendRequest> getAll() throws ValidationException, SQLException, RepositoryException {
        return requestRepository.getAll();
    }

    /**
     * Returns all the requests that are sent to a specific user
     * @param id Integer
     * @return List(FriendRequest)
     * @throws ValidationException if the entities are invalid
     * @throws SQLException if the database cannot be reached
     */
    public List<FriendRequest> getAllToUser(Integer id) throws ValidationException, SQLException, RepositoryException {
        return requestRepository.getAll()
                .stream()
                .filter(x-> { return x.getId().getRight().equals(id); })
                .collect(Collectors.toList());
    }

    /**
     * Returns all the requests from a specific user
     * @param id Integer
     * @return List(FriendRequest)
     * @throws ValidationException if the entities are invalid
     * @throws SQLException if the database cannot be reached
     */
    public List<FriendRequest> getAllFromUser(Integer id) throws ValidationException, SQLException, RepositoryException {
        return requestRepository.getAll()
                .stream()
                .filter( x-> { return x.getId().getLeft().equals(id); })
                .collect(Collectors.toList());
    }
    public void deleteRequestsOfUser(Integer id) throws IOException, SQLException, RepositoryException, ValidationException {

        boolean done = false;
        while (!done) {
            for (FriendRequest f : getAll()) {
                if (id.equals(f.getId().getLeft())) {
                    requestRepository.delete(new Tuple<>(id, f.getId().getRight()));
                    break;
                }
                else if (id.equals(f.getId().getRight())) {
                    requestRepository.delete(new Tuple<>(f.getId().getLeft(), id));
                    break;
                }
            }
            done = true;
        }
    }
}
