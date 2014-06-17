package de.rub.fuzzy;

import java.util.HashMap;

/**
 * Define a linguistic variable with a crisp value and a set of membership
 * function. Defuzzification evalutes a linguistic variable after rules have
 * fired.
 */

public class LinguisticVariable {

    /**
     * The name of the linguistic variable.
     */
    private String name = "";

    /**
     * The crisp input value
     */
    private double crisp = 0.0;

    /**
     * The list of all members mapped by name.
     */
    private HashMap<String, MembershipFunction> members = new HashMap<String, MembershipFunction>();

    /**
     * Creates a new linguistic variable and registers it.
     * 
     * @param name
     *            the name of the linguistic variable.
     */
    public LinguisticVariable(String name) {
        this.name = name;
        Catalog.allLV.put(name, this);
    }

    /**
     * Adds a new member to the linguistic variable. For example, add "hot" to
     * to LV "temperature".
     * 
     * @param member
     *            the additional member.
     */
    public void addMember(MembershipFunction member) {
        members.put(member.getName(), member);
    }

    /**
     * Sets the crisp value.
     * 
     * @param crisp
     *            a crisp value. For example, "32.0" deg C for the LV
     *            "temperature".
     */
    public void setCrisp(double crisp) {
        this.crisp = crisp;
    }

    /**
     * Sets the crisp value for the LV name.
     * 
     * @param name
     *            the name of the linguistic variable. For example,
     *            "temperature".
     * 
     * @param crisp
     *            a deterministic value. For example, 32.0 deg Celsius
     */
    public static void setCrisp(String name, double crisp) {
        LinguisticVariable lv = Catalog.allLV.get(name);
        if (lv != null)
            lv.setCrisp(crisp);
    }

    /**
     * Print the values of each member and the corresponding degree of
     * membership.
     */
    public void print() {
        System.out.println("\tLV=" + name + ", crisp=" + crisp);
        for (MembershipFunction mf : members.values()) {
            System.out.println("\t\t" + mf.getName() + ":\t"
                    + mf.degreeOfMembership(crisp));
        }
    }

    /**
     * Gets the degree of membership for a given member.
     * 
     * @param name
     *            the name of the member.
     * @return the degree of membership (between 0.0 and 1.0).
     */
    public double getDoM(String name) {
        MembershipFunction mf = members.get(name);
        if (mf != null) {
            return mf.degreeOfMembership(crisp);
        } else {
            return 0;
        }
    }

    /**
     * Gets the degree of membership for a given LV and member.
     * 
     * @param lvName
     *            the name of the linguistic variable (for example,
     *            "temperature").
     * @param memberName
     *            the name of the membe (for example, "cold").
     * @return the degree of membership.
     */
    public static double getDoM(String lvName, String memberName) {
        LinguisticVariable lv = Catalog.allLV.get(lvName);
        if (lv == null) {
            return 0;
        }

        MembershipFunction mf = lv.members.get(memberName);
        if (mf == null) {
            return 0;
        }

        return mf.degreeOfMembership(lv.crisp);
    }

    /**
     * Calculates the result of a LV by defuzzifying it. Uses the method of
     * center of gravity (see 'Wissensbasierte Methoden' [Hartmann],
     * Teilflaechenschwerpunkt, S. 106). It is assumed that the rules have been
     * fired and the members will be reset (?)
     * 
     * @return the final crisp value found by defuzzification
     */
    public double defuzzify() {
        double f = 0.0;
        double sum = 0.0;
        for (MembershipFunction mf : members.values()) {
            double volume = mf.volume();
            f += mf.getCenterOfGravity() * volume;
            sum += volume;

            if (Catalog.debug > 2) {
                System.out.println("zielWert: name=" + mf + " vol=" + volume
                        + " f=" + f + " sum=" + sum);
            }
        }

        // if no entries, return 0
        if (sum == 0) {
            return 0;
        } else {
            return f / sum;
        }
    }

    /**
     * Gets a linguistic variable.
     * 
     * @param name
     *            the name of the LV.
     * @return the linguistic variable.
     */
    public static LinguisticVariable getLV(String name) {
        return Catalog.allLV.get(name);
    }

    /**
     * Gets a member.
     */
    public MembershipFunction getMember(String name) {
        return members.get(name);
    }

    /**
     * Gets the name.
     */
    public String getName() {
        return name;
    }

    /**
     * Resets all members.
     */
    public void reset() {
        for (MembershipFunction mf : members.values())
            mf.reset();
    }

    /**
     * Prints a concise representation.
     */
    public String toString() {
        return name + "/" + crisp;
    }
}
