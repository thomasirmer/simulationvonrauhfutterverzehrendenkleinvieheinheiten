package de.rub.fuzzy;

/**
 * Define a fuzzy operator for Boolean "not".
 */

public class FuzzyNot extends FuzzyOperator {
    private FuzzyOperator operand = null;

    /**
     * Creates a fuzzy operator for "not".
     * 
     * @param operand
     *            the operatator to negate.
     */
    public FuzzyNot(FuzzyOperator operand) {
        this.operand = operand;
    }

    /**
     * Calculates the negative of the operand.
     * 
     * @return the value of the operand subtracted from 1.
     */
    public double value() {
        return 1 - operand.value();
    }

    /**
     * Prints a concise representation using parenthesis.
     */
    public String toString() {
        return "[-" + operand + "]";
    }
}
