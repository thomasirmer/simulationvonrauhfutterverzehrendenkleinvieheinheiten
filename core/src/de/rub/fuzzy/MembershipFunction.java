package de.rub.fuzzy;

/**
 * Linguistic variables are defined by a set of membership functions. A
 * membership function is defined as a trapezoid curve representing the degree
 * of membership, therefore, four values are needed to define the degree of
 * membership. The degree of membership ranges between 0 and 1.
 */

public class MembershipFunction {

    /**
     * The name of the membership function.
     */
    private String name = "";

    /**
     * The linguistic variable to which this instance belongs.
     */
    private LinguisticVariable linguisticVariable = null;

    /**
     * The x-coordinate of rise-begin.
     */
    private double p1 = 0.0;

    /**
     * The x-coordinate of rise-end.
     */
    private double p2 = 1.0;

    /**
     * The x-coordinate of fall-begin.
     */
    private double p3 = 2.0;

    /**
     * The x-coordinate of fall-end.
     */
    private double p4 = 3.0;

    /**
     * The sum of all degrees of membership in rule conclusions.
     */
    private double sumDoM = 0.0;

    /**
     * The number of DoMs in rule conclusions
     */
    private int numberDoM = 0;

    /**
     * Creates a standard trapezoidal membership function.
     * 
     * @param lv
     *            The linguistic variable to which this instance belongs.
     * @param name
     *            The name of the membership function.
     * @param p1
     *            The left x-coordinate with value 0.
     * @param p2
     *            The left x-coordinate with value 1.
     * @param p3
     *            The right x-coordinate with value 1.
     * @param p4
     *            The right x-coordinate with value 0.
     */
    public MembershipFunction(LinguisticVariable lv, String name, double p1,
            double p2, double p3, double p4) {
        this.linguisticVariable = lv;
        this.name = name;
        this.p1 = p1;
        this.p2 = p2;
        this.p3 = p3;
        this.p4 = p4;
    }

    /**
     * Calculates the degree of membership at a given postion.
     * 
     * @param position
     *            The x-coordinate where the DoM is calculated.
     * @return the degree of membership as a value between 0 and 1.
     */
    public double degreeOfMembership(double position) {
        if (position <= p1)
            return 0.0;
        if (p1 < position && position < p2)
            return (position - p1) / (p2 - p1);
        if (p2 <= position && position <= p3)
            return 1.0;
        if (p3 < position && position < p4)
            return 1 - (position - p3) / (p4 - p3);
        if (p4 <= position)
            return 0.0;
        return 0.0;
    }

    /**
     * Stores the degree of membership for the conclusion of a single rule.
     * These values are used to calculate the total degree of membership for all
     * rules.
     * 
     * @param value
     *            The degree of membership for a single rule conclusion.
     */
    public void setDoM(double value) {
        sumDoM += value;
        numberDoM++;
    }

    /**
     * Returns the center of gravity of the trapezoid needed for
     * defuzzyfication.
     * 
     * @return the center of gravity.
     */
    public double getCenterOfGravity() {
        double cog = (p4 * p4 + p4 * p3 - p1 * p1 - p1 * p2 + p3 * p3 - p2 * p2)
                / (p4 - p1 + p3 - p2) / 3.0;

        if (Catalog.debug > 1) {
            System.out.println("member " + name + ": centerOfGravity=" + cog);
        }
        return cog;
    }

    /**
     * Returns the area of the trapezoid.
     * 
     * @return the area.
     */
    public double area() {
        double area = (p2 - p1 + p4 - p3) / 2 + p3 - p2;

        if (Catalog.debug > 1) {
            System.out.println("member " + name + ": area=" + area);
        }
        return area;
    }

    /**
     * Evaluates a rule conclusion by calculating the volume over the membership
     * function area. The thickness is defined as the average degree of
     * membership.
     * 
     * @return the membership area times the weighted DoM of a rule conclusion,
     *         or 0 is the rule has not been fired.
     * 
     */
    public double volume() {
        double vol = 0;

        if (numberDoM == 0) {
            // if no rule has been fired, assume null value
            vol = 0;
        } else {
            vol = (sumDoM / numberDoM) * area();
        }

        if (Catalog.debug > 1) {
            System.out.println("member " + name + ": vol=" + vol);
        }

        return vol;
    }

    /**
     * Returns the name.
     * 
     * @return the name.
     */
    String getName() {
        return name;
    }

    /**
     * Returns the linguistic variable.
     * 
     * @return the linguistic variable.
     */
    public LinguisticVariable getLinguisticVariable() {
        return linguisticVariable;
    }

    /**
     * Resets the sum and number of DoMs to zero.
     */
    public void reset() {
        sumDoM = 0;
        numberDoM = 0;
    }

    /**
     * Prints a concise representation.
     */
    public String toString() {
        return name + "(" + linguisticVariable + ")" + "[" + p1 + "," + p2
                + "," + p3 + "," + p4 + "]";
    }
}
