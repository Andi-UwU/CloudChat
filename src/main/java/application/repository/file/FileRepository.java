package application.repository.file;

import application.domain.Entity;
import application.domain.validator.Validator;
import application.exceptions.RepositoryException;
import application.exceptions.ValidationException;
import application.repository.memory.MemoryRepository;

import java.io.*;
import java.util.Arrays;
import java.util.List;

public abstract class FileRepository<ID, E extends Entity<ID>> extends MemoryRepository<ID, E> {

    /**
     * fileName - the file name from which entities are extracted
     * validator - validate the extracted entities
     */
    private String fileName;
    private Validator<E> validator;

    /**
     * constructor
     * @param fileName
     * @param validator
     * @throws ValidationException
     * @throws IOException
     */
    public FileRepository(String fileName, Validator validator) throws ValidationException, IOException, RepositoryException {
        super();
        this.fileName = fileName;
        this.validator = validator;
        loadData();
    }

    /**
     * create an entity using a list of attributes
     * @param attributes
     * @return the entity created
     */
    protected abstract E extractEntity(List<String> attributes);

    /**
     * create an entity as String
     * @param entity
     * @return String representing the given entity
     */
    protected abstract String createEntityAsString(E entity);

    /**
     * loads entity from the file
     * @throws ValidationException
     * @throws IOException
     */
    private void loadData() throws ValidationException, IOException, RepositoryException {

        BufferedReader br = new BufferedReader(new FileReader(fileName));

        String line;
        while ((line = br.readLine()) != null){
            List<String> attributes = Arrays.asList(line.split(";"));
            E entity = extractEntity(attributes);

            validator.validate(entity);
            super.add(entity);
        }

        br.close();

    }

    /**
     * store entities to the file
     * @throws IOException
     */
    private void storeData() throws IOException {
        BufferedWriter bw = new BufferedWriter(new FileWriter(fileName));

        Iterable<E> list = super.getAll();

        for(E e : list){
            bw.write(createEntityAsString(e));
            bw.newLine();
        }

        bw.close();
    }

    /**
     * adds an entity to the repository
     * @param entity
     * @return the entity, if it exists already in the repository
     * @return null, if the entity has been added
     * @throws IllegalArgumentException
     * @throws IOException
     */
    @Override
    public E add(E entity) throws IllegalArgumentException, IOException, RepositoryException {
        super.add(entity);
        storeData();
        return entity;
    }

    /**
     * deletes an entity with the given id
     * @param id
     * @return the entity that was deleted
     * @return null, if the entity does not exist
     * @throws IOException
     */
    @Override
    public E delete(ID id) throws IOException, RepositoryException {
        E e = super.delete(id);
        if (e != null){//if the entity has been deleted from repository
            storeData();
        }
        return e;
    }

    /**
     * replace the given entity with the entity from repository with the same id
     * @param entity
     * @return
     * the entity that was replaced, if there existed an entity with the same id as the given entity
     * null, otherwise
     * @throws IOException
     */
    @Override
    public E update(E entity) throws IOException, RepositoryException {
        E e = super.update(entity);
        if (e != null){//if the entity has been modified
            storeData();
        }
        return e;
    }
}
