package de.rub.fuzzy;

/**
 * This exception is thrown if the "then" part of a rule is missing.
 */

public class FuzzyNoThenPartException extends Exception {

    private static final long serialVersionUID = 1L;

    /**
     * Constructor
     * 
     * @param s
     *            a message
     */
    public FuzzyNoThenPartException(String s) {
        super(s);
    }

    /**
     * Constructor
     */
    public FuzzyNoThenPartException() {
        super();
    }
}
