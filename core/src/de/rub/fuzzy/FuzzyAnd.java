package de.rub.fuzzy;

/**
 * Define a fuzzy operator for Boolean "and".
 */

public class FuzzyAnd extends FuzzyOperator {
    private FuzzyOperator operand1 = null;
    private FuzzyOperator operand2 = null;

    /**
     * Creates a new FuzzyOperator for logical "and". The operands can be
     * nested.
     * 
     * @param operand1
     *            the first operand.
     * @param operand2
     *            the second operand.
     */
    public FuzzyAnd(FuzzyOperator operand1, FuzzyOperator operand2) {
        this.operand1 = operand1;
        this.operand2 = operand2;
    }

    /**
     * Calculates the logical "and" of two fuzzy values using the minimum value.
     * 
     * @return the minimum value representing "and".
     */
    public double value() {
        double value1 = operand1.value();
        double value2 = operand2.value();
        return (value1 < value2) ? value1 : value2;
    }

    /**
     * Prints a concise representation using parenthesis.
     */
    public String toString() {
        return "(" + operand1 + "&" + operand2 + ")";
    }
}
