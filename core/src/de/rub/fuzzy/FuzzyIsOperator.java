package de.rub.fuzzy;

/**
 * Define an operator to link a linguistic variable to a membership function.
 * That is, define the "is" operator.
 */

public class FuzzyIsOperator extends FuzzyOperator {
    private String linguisticVariable = "";

    private String membershipFunction = "";

    /**
     * Creates a FuzzyIsOperator operator.
     * 
     * @param lv
     *            the linguistic variable.
     * @param mf
     *            the membership function.
     */
    public FuzzyIsOperator(String lv, String mf) {
        this.linguisticVariable = lv;
        this.membershipFunction = mf;
    }

    /**
     * Calculates the degree of membership for a linguistic variable.
     * 
     * @return the degree of membership.
     */
    public double value() {
        return LinguisticVariable
                .getDoM(linguisticVariable, membershipFunction);
    }

    /**
     * Prints a concise representation.
     */
    public String toString() {
        return linguisticVariable + "=" + membershipFunction;
    }
}
