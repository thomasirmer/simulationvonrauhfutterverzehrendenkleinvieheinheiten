package de.rub.fuzzy;

import java.io.FileReader;
import java.io.IOException;
import java.io.LineNumberReader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.StringTokenizer;

/**
 * A catalog of fuzzy rules and linguistic variables. They are stored in global,
 * static structures. Rules and LV can be read from a file. The <code>set</code>
 * and <code>get</code> methods are basic ways to fuzzify and defuzzify variables,
 * respectively. Actual evaluation of rules is done by the <code>evallAllRules</code>
 * method.
 */

public class Catalog {

    public static int debug = 0;

    /**
     * Linguistic variables are identified by a "+" char.
     */
    public final static String ID_LV = "+";

    /**
     * Members are identified by a "-" char.
     */
    public final static String ID_MEMBER = "-";

    /**
     * The comment character.
     */
    public final static String ID_COMMENT = "#";

    /**
     * The list of all linguistic variables mapped by name.
     */
    public static HashMap<String, LinguisticVariable> allLV = new HashMap<String, LinguisticVariable>();

    /**
     * The list of all rules.
     */
    public static ArrayList<Rule> allRules = new ArrayList<Rule>();

    /**
     * The list of LV's in all conclusions of rules.
     */
    public static HashMap<String, LinguisticVariable> allConclusionLV = new HashMap<String, LinguisticVariable>();

    /**
     * Adds a rule to the catalog and updates the list of conclusion LV's.
     * 
     * @param fr
     *            a processed fuzzy rule.
     */
    public static void addRule(Rule fr) {
        allRules.add(fr);

        LinguisticVariable[] lvs = fr.getConclusionLVs();
        for (int i = 0; i < lvs.length; i++) {
            allConclusionLV.put(lvs[i].getName(), lvs[i]);
        }
    }

    /**
     * Evaluates all rules in the catalog.
     */
    public static void evalAllRules() {
        // reset all LV in the conclusion
        for (LinguisticVariable lv : allConclusionLV.values()) {
            lv.reset();
        }

        // evaluate all rules
        for (Rule fr : allRules) {
            fr.evalRule();
        }
    }

    /**
     * Gets the defuzzified value of a linguistic variable after the rules have
     * fired.
     * 
     * @param name
     *            the name of the LV.
     * @return the defuzzified value.
     * @throws FuzzyNotAConclusionException
     */
    public static double get(String name) {
        LinguisticVariable lv = allConclusionLV.get(name);
        if (debug > 2) {
            System.out.println("defuzzify: lv=" + name + " defuzz="
                    + lv.defuzzify());
        }
        if (lv != null) {
            return lv.defuzzify();
        } else {
            throw new IllegalArgumentException("can't defuzzify '" + name
                    + "': not a conclusion");
        }

    }

    /**
     * Sets the crisp value for given linguistic variable.
     * 
     * @param name
     *            the name of the LV.
     * 
     * @param crisp
     *            the numerical crisp value.
     * @throws FuzzyNotALinguisticVariableException
     */
    public static void set(String name, double crisp) {
        LinguisticVariable lv = LinguisticVariable.getLV(name);
        if (lv != null) {
            lv.setCrisp(crisp);
        } else {
            throw new IllegalArgumentException("can't find LV '" + name + "'");
        }
    }

    /**
     * Reads linguistic variables and fuzzy rules from a file. Layout example:
     * 
     * <pre>
     *                       # define the temperature...
     *                       + temperature
     *                       - cold 5,10,20,30
     *                       - hot  10,20,30,40
     *                       # ...and the valve position
     *                       + valve
     *                       - shut -5,0,0,5
     *                       - open 0,5,5,10
     *                       # some simple rules
     *                       if temperature=cold then valve=open
     *                       if temperature=hot then valve=shut 
     * </pre>
     * 
     * @param fileName
     *            the name of the file, including the suffix (for example,
     *            'bathtub.fzy')
     * @throws FuzzyNoThenPartException
     * @throws IOException
     * @throws FuzzyInputException
     */
    public static void readFile(String fileName)
            throws FuzzyNoThenPartException, IOException, FuzzyInputException {
        LinguisticVariable currentLV = null;

        // open file
        LineNumberReader in = new LineNumberReader(new FileReader(fileName));

        String line = null;
        line = in.readLine();

        while (line != null) {

            if (debug > 0) {
                System.out.println("line " + in.getLineNumber() + ":<" + line
                        + ">");
            }

            // process line
            line = line.trim();

            if (debug > 1) {
                System.out.println("  trimmed:<" + line + ">");
            }

            if (line.startsWith(ID_COMMENT) || line.equals("")) {
                // skip comments
                if (debug > 2) {
                    System.out.println("  skipping comment...");
                }

            } else if (line.startsWith(ID_LV)) {
                // read a LV
                String lvname = line.substring(ID_LV.length()).trim();
                currentLV = new LinguisticVariable(lvname);

                if (debug > 1) {
                    System.out.println("  LV=" + currentLV);
                }

            } else if (line.startsWith(ID_MEMBER)) {
                // read members of previous LV
                StringTokenizer token = new StringTokenizer(line
                        .substring(ID_MEMBER.length()), " \t,");
                String name = token.nextToken().trim();

                if (currentLV == null) {
                    throw new RuntimeException("found member '" + name
                            + "', but no LV at line " + in.getLineNumber());
                }

                double p1 = 0, p2 = 0, p3 = 0, p4 = 0;
                try {
                    p1 = Double.parseDouble(token.nextToken());
                    p2 = Double.parseDouble(token.nextToken());
                    p3 = Double.parseDouble(token.nextToken());
                    p4 = Double.parseDouble(token.nextToken());
                } catch (NumberFormatException e) {
                    throw new FuzzyInputException("number format errror in "
                            + fileName + " line " + in.getLineNumber());
                }
                currentLV.addMember(new MembershipFunction(currentLV, name, p1,
                        p2, p3, p4));

                if (debug > 1) {
                    System.out.println("    member="
                            + currentLV.getMember(name));
                }

            } else if (line.startsWith(Rule.IF) && line.indexOf(Rule.THEN) >= 0) {
                // read rules
                addRule(new Rule(line));
                if (debug > 1) {
                    System.out.println("  rule: " + line);
                }
            } else {
                throw new FuzzyInputException("possible error at line "
                        + in.getLineNumber() + " in file '" + fileName + "':<"
                        + line + ">");
            }
            line = in.readLine();
        }
    }
}
