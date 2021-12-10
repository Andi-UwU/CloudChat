package application.domain;

import java.util.Objects;

/**
 * Tuple to store two generic values
 * @param <E1> generic type
 * @param <E2> generic type
 */
public class Tuple <E1, E2>{
    private E1 e1;
    private E2 e2;

    /**
     * Constructor
     * @param e1 generic type E1
     * @param e2 generic type E2
     */
    public Tuple(E1 e1, E2 e2){
        this.e1 = e1;
        this.e2 = e2;
    }

    /**
     * Gets left component
     * @return generic type E1
     */
    public E1 getLeft() {
        return e1;
    }

    /**
     * Sets left component
     * @param e1 generic type E1
     */
    public void setLeft(E1 e1) {
        this.e1 = e1;
    }

    /**
     * Gets right component
     * @return generic type E2
     */
    public E2 getRight() {
        return e2;
    }

    /**
     * Sets right component
     * @param e2 generic type E2
     */
    public void setRight(E2 e2) {
        this.e2 = e2;
    }

    /**
     * Test the equality of two tuples
     * @param obj Tuple
     * @return
     * true, if this object is equal to obj ;
     * false, otherwise
     */
    @Override
    public boolean equals(Object obj) {
        if (!(obj instanceof Tuple))
            return false;
        return this.e1.equals(((Tuple) obj).e1) && this.e2.equals(((Tuple) obj).e2);
    }

    /**
     * @return int - hashCode of the object
     */
    @Override
    public int hashCode() {

        return Objects.hash(e1, e2);
    }
}
