package de.rub.fuzzy;

import java.util.StringTokenizer;

/**
 * A fuzzy rule is an IF-THEN rule, where the antecedent is composed of
 * nested fuzzy operators and the consequent is a single linguistic variable.
 */

public class Rule {

    public static final String IF = "if";

    public static final String THEN = "then";

    public static final String AND = "and";

    public static final String OR = "or";

    public static final String NOT = "not";

    private FuzzyOperator ifOperator = null;

    private MembershipFunction[] conclusionMembers = new MembershipFunction[0];

    /**
     * Create a new fuzzy rule with a single if-operator and a list of
     * conclusion members. Note that fuzzy operators are nested.
     * 
     * @param ifOperator
     *            the root fuzzy operator for the ifOperator.
     * @param conclusionMembers
     *            a list of members for which the degrees of memberships are to
     *            be computed.
     */
    public Rule(FuzzyOperator ifOperator,
            MembershipFunction[] conclusionMembers) {
        this.ifOperator = ifOperator;
        this.conclusionMembers = conclusionMembers;
    }

    /**
     * Creates a new fuzzy rule from text input. An example is: if temp=hot and
     * flow=high then valve=open. The usual operator precedence of Boolean logic
     * applies. Side effect: set the if-operator and member conclusion
     * attributes.
     * 
     * @param text
     *            the rule as a line of text.
     * @throws FuzzyNoThenPartException
     */
    public Rule(String text) throws FuzzyNoThenPartException {

        // set blanks next to parenthesis

        StringTokenizer token = new StringTokenizer(text, "()", true);
        text = "";
        while (token.hasMoreTokens())
            text += " " + token.nextToken() + " ";

        if (Catalog.debug > 2) {
            System.out.println("fr: post parenth=<" + text + ">");
        }

        // split text into tokens by: blank, tab and "="
        token = new StringTokenizer(text, " \t=");
        String[] s = new String[token.countTokens()];
        int idxThen = 0;
        for (int i = 0; i < s.length; i++) {
            s[i] = token.nextToken();
            if (s[i].equals(THEN)) {
                idxThen = i;
                if (Catalog.debug > 2) {
                    System.out.println("fr: idxThen=" + idxThen);
                }
            }
        }

        // check if then-part is missing...
        if (idxThen == 0) {
            throw new FuzzyNoThenPartException("didn't find then part:<" + text
                    + ">");
        }

        // split in if- and then-tokens...
        String[] ifTokens = new String[idxThen - 1];
        String[] thenTokens = new String[s.length - 1 - idxThen];
        for (int i = 1; i < idxThen; i++) {
            ifTokens[i - 1] = s[i];
            if (Catalog.debug > 2) {
                System.out
                        .println("fr: i=" + i + " ifToken=" + ifTokens[i - 1]);
            }
        }

        for (int i = idxThen + 1; i < s.length; i++) {
            thenTokens[i - idxThen - 1] = s[i];
            if (Catalog.debug > 2) {
                System.out.println("fr: i=" + i + " thenTokens="
                        + thenTokens[i - idxThen - 1]);
            }
        }

        // transform if-term to an operator
        ifOperator = transformTerm(ifTokens);

        if (Catalog.debug > 0) {
            System.out.println("ifOperator=" + ifOperator);
        }

        // store the LV of the the-part
        conclusionMembers = new MembershipFunction[(thenTokens.length + 1) / 3];
        for (int i = 0; i < (thenTokens.length + 1) / 3; i++)
            conclusionMembers[i] = LinguisticVariable.getLV(thenTokens[i * 3])
                    .getMember(thenTokens[i * 3 + 1]);

        if (Catalog.debug > 0) {
            for (int i = 0; i < conclusionMembers.length; i++) {
                System.out.println("conclusionMembers[" + i + "]="
                        + conclusionMembers[i]);
            }
        }

    }

    /**
     * Gets a list of LVs found in the conclusion of a rule.
     * 
     * @return a list of LVs.
     */
    public LinguisticVariable[] getConclusionLVs() {
        LinguisticVariable[] lvs = new LinguisticVariable[conclusionMembers.length];
        for (int i = 0; i < lvs.length; i++)
            lvs[i] = conclusionMembers[i].getLinguisticVariable();
        return lvs;
    }

    /**
     * Evaluates the ifOperator and sets the DoMs of the conclusions.
     */
    public void evalRule() {
        double value = ifOperator.value();
        if (Catalog.debug > 2) {
            System.out.println("evalRule: if-part=" + ifOperator
                    + " value=" + ifOperator.value());
        }
        for (int i = 0; i < conclusionMembers.length; i++) {
            if (Catalog.debug > 2) {
                System.out.println("evalRule: conclusionMembers[" + i + "]="
                        + conclusionMembers[i]);
            }
            conclusionMembers[i].setDoM(value);
        }
    }

    /**
     * Transforms an array of string tokens into a nested set of fuzzy
     * operators.
     * 
     * @param s
     *            a rule respresents as an array of string tokens.
     * 
     * @return the root fuzzy operator.
     */
    public FuzzyOperator transformTerm(String[] s) {

        // if only two terms remain, return rest
        if (s.length == 2) {
            return new FuzzyIsOperator(s[0], s[1]);
        }

        // remove parenthesis at beginning and end of line
        if (s[0].equals("(") && s[s.length - 1].equals(")")) {
            String[] tmp = new String[s.length - 2];
            for (int i = 1; i < s.length - 1; i++)
                tmp[i - 1] = s[i];
            return transformTerm(tmp);
        }

        // find next priority operator
        int opPos = -1;
        int parenLevel = 0;
        String operator = "";
        for (int i = 0; i < s.length; i++) {
            if (s[i].equals("("))
                parenLevel++;
            else if (s[i].equals(")"))
                parenLevel--;
            else if (parenLevel == 0) {
                if (s[i].equals(OR) && !operator.equals(OR)) {
                    opPos = i;
                    operator = OR;
                } else if (s[i].equals(AND) && !operator.equals(OR)
                        && !operator.equals(AND)) {
                    opPos = i;
                    operator = AND;
                } else if (s[i].equals(NOT) && !operator.equals(OR)
                        && !operator.equals(AND) && !operator.equals(NOT)) {
                    opPos = i;
                    operator = NOT;
                }
            }
        }

        // store text in two terms (before and after the operator)
        String[] s1 = null;
        String[] s2 = null;
        if (opPos > 0) {
            s1 = new String[opPos];
            for (int i = 0; i < opPos; i++)
                s1[i] = s[i];
        }
        if (opPos < s.length - 1) {
            s2 = new String[s.length - opPos - 1];
            for (int i = 0; i < s2.length; i++)
                s2[i] = s[i + opPos + 1];
        }

        // evaluate the partail terms found so far
        if (operator.equals(NOT))
            return (new FuzzyNot(transformTerm(s2)));
        if (operator.equals(AND))
            return (new FuzzyAnd(transformTerm(s1), transformTerm(s2)));
        if (operator.equals(OR))
            return (new FuzzyOr(transformTerm(s1), transformTerm(s2)));

        // This should never happen...
        throw new IllegalArgumentException(
                "can't transform token array to operator");
    }
}