package de.rub.fuzzy;

/**
 * This exception is thrown if the input probably contains an error.
 */

public class FuzzyInputException extends Exception {

    private static final long serialVersionUID = 1L;

    /**
     * Constructor
     * 
     * @param s
     *            a message
     */
    public FuzzyInputException(String s) {
        super(s);
    }

    /**
     * Constructor
     */
    public FuzzyInputException() {
        super();
    }
}
