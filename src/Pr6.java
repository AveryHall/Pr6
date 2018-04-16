/*

    Exact Inference Algorithm for Bayesian Networks
    CS 4453: Artificial Intelligence
    Dr. Arisoa Randrianasolo
    Avery Hall
    04/14/18
    Description: The purpose of this project is to implement an exact inference algorithm
    for querying Bayesian Networks. This program implements a version of the exact inference
    by enumeration to answer queries enter by the user on a pre-defined BN. The structure of
    the BN and truth table values have been given in Figure 1 of the Pr6 PDF.

 */

import java.util.ArrayList;
import java.util.Scanner;

public class Pr6 {

    private static double[][] Ptable = {
            {0.1},
            {0.9}
    };
    private static double[][] Stable = {
            {0.3},
            {0.7}
    };
    private static double[][] Ctable = {
            {0.95, 0.05},
            {0.20, 0.80},
            {0.30, 0.70},
            {0.001, 0.999}
    };
    private static double[][] Xtable = {
            {0.9, 0.1},
            {0.2, 0.8}
    };
    private static double[][] Dtable = {
            {0.65, 0.35},
            {0.30, 0.70}
    };

    private static String[] Pmeasure = {"high","low"};
    private static String[] Smeasure = {"true","false"};
    private static String[] Cmeasure = {"true","false"};
    private static String[] Xmeasure = {"pos","neg"};
    private static String[] Dmeasure = {"true","false"};

    private static Node X;
    private static String x;
    private static ArrayList<Node> E = new ArrayList<>();
    private static ArrayList<String> e = new ArrayList<>();


    // MAIN METHOD
    public static void main(String[] args) {

        // Test
//        System.out.println(Ptable[0][0]);

        // Build the Bayesian Network from Fig. 1
        BayesianNetwork bn = buildBayesianNetwork();

        // Gather user input for the query variable X with outcome x and evidence variable(s) E with evidence(s) e.
        gatherInput(bn);

        // Use exact inference by enumeration to solve the input query
        double prob = EnumerationAsk(bn);

        //Check
//        System.out.println("X: " + X.getVariable());
//        System.out.println("x: " + x);
//        for (Node i : E) {
//            System.out.println("E: " + i.getVariable());
//        }
//        for (String i : e) {
//            System.out.println("e: " + i);
//        }

        //Output the probability of the Query outcome given the evidence outcomes
        System.out.printf("The probability is: %.4f", prob);

    }

    // Static Methods
    private static BayesianNetwork buildBayesianNetwork() {

        // Initialize a new Bayesian Network
        BayesianNetwork b = new BayesianNetwork();

        // Initialize nodes (that is, the Variables)
        Node Pollution = new Node("Pollution",Ptable,Pmeasure);
        Node Smoker = new Node("Smoker",Stable,Smeasure);
        Node Cancer = new Node("Cancer",Ctable,Cmeasure);
        Node Xray = new Node("Xray",Xtable,Xmeasure);
        Node Dyspnea = new Node("Dyspnea",Dtable,Dmeasure);

        // Define the relationships between each of the variables
        // Relationships for Pollution
        Pollution.addChild(Cancer);
        Pollution.addPredecessor(Cancer);
        Pollution.addPredecessor(Xray);
        Pollution.addPredecessor(Dyspnea);

        // Relationships for Smoker
        Smoker.addChild(Cancer);
        Smoker.addPredecessor(Cancer);
        Smoker.addPredecessor(Xray);
        Smoker.addPredecessor(Dyspnea);

        // Relationships for Cancer
        Cancer.addParent(Pollution);
        Cancer.addParent(Smoker);
        Cancer.addAncestor(Pollution);
        Cancer.addAncestor(Smoker);
        Cancer.addChild(Xray);
        Cancer.addChild(Dyspnea);
        Cancer.addPredecessor(Xray);
        Cancer.addPredecessor(Dyspnea);

        // Relationships for Xray
        Xray.addParent(Cancer);
        Xray.addAncestor(Cancer);
        Xray.addAncestor(Pollution);
        Xray.addAncestor(Smoker);

        // Relationships for Dyspnea
        Dyspnea.addParent(Cancer);
        Dyspnea.addAncestor(Cancer);
        Dyspnea.addAncestor(Pollution);
        Dyspnea.addAncestor(Smoker);

        // Add the Variables to the Bayesian Network
        b.addVariable(Pollution);
        b.addVariable(Smoker);
        b.addVariable(Cancer);
        b.addVariable(Xray);
        b.addVariable(Dyspnea);

        return b;
    }

    private static void gatherInput(BayesianNetwork bn) {

        Scanner sc = new Scanner(System.in);

        // Gather the query variable
        System.out.print("Enter the query variable (");
        for (int i = 0; i < bn.getVariables().size(); i++) {
            if(i < bn.getVariables().size() - 1) { System.out.print(bn.getVariables().get(i).getVariable() + ", "); }
            else { System.out.print(bn.getVariables().get(i).getVariable() + "): "); }
        }

        String Xvar = "";
        boolean invalidInput = true;
        int queryIndex = 0;
        while(invalidInput) {
            Xvar = sc.next();
            for (int i = 0; i < bn.getVariables().size(); i++) {
                if(bn.getVariables().get(i).getVariable().equals(Xvar)) {
                    invalidInput = false;
                    queryIndex = i;
                    X = bn.getVariables().get(i);
                    break; }
            }
            if(invalidInput) {
                System.out.print("Invalid variable, enter another variable: ");
            }
        }

        // Gather input for the query variable's outcome
        String[] querysMeasure = bn.getVariables().get(queryIndex).getMeasure();
        System.out.print("Enter the query variable's outcome (");
        System.out.print(querysMeasure[0] + ", " + querysMeasure[1] + "): ");

        invalidInput = true;
        while(invalidInput) {
            x = sc.next();
            for (String m : querysMeasure) {
                if(m.equals(x)) { invalidInput = false; break; }
            }
            if(invalidInput) {
                System.out.print("Invalid outcome, enter another: ");
            }
        }

        // Gather input for each Evidence Variable and it's outcome
        ArrayList<Node> validEvars = new ArrayList<>();

        for (int i = 0; i < bn.getVariables().size(); i++) {
            if(!bn.getVariables().get(i).getVariable().equals(Xvar)) {
                validEvars.add(bn.getVariables().get(i));
            }
        }

        String Evid = "";
        String EvidOutcome;

        while(!Evid.equals("halt!")) {

            System.out.print("Enter an evidence variable (");
            for (Node i : validEvars) {
                System.out.print(i.getVariable() + ", ");
            }
            System.out.print("or \"halt!\"): ");

            invalidInput = true;
            int EvidIndex = 0;
            while (invalidInput && !Evid.equals("halt!")) {
                Evid = sc.next();
                for (int i = 0; i < validEvars.size(); i++) {
                    if (validEvars.get(i).getVariable().equals(Evid)) {
                        invalidInput = false;
                        EvidIndex = i;
                        E.add(validEvars.get(i));
                        break;
                    }
                }
                if (invalidInput && !Evid.equals("halt!")) {
                    System.out.print("Invalid variable, enter another variable: ");
                }
            }

            // Gather input for the evidence variable's outcome
            if (!Evid.equals("halt!")) {
                System.out.print("Enter the evidence variable's outcome (");
                System.out.print(validEvars.get(EvidIndex).getMeasure()[0] + ", "
                        + validEvars.get(EvidIndex).getMeasure()[1] + "): ");
                invalidInput = true;
                while (invalidInput) {
                    EvidOutcome = sc.next();
                    for (int i = 0; i < validEvars.size(); i++) {
                        if (validEvars.get(EvidIndex).getMeasure()[i].equals(EvidOutcome)) {
                            invalidInput = false;
                            e.add(EvidOutcome);
                            break;
                        }
                    }
                    if (invalidInput) {
                        System.out.print("Invalid outcome, enter another: ");
                    }
                }
            }

            validEvars.remove(EvidIndex);
        }

    }

    private static double EnumerationAsk(BayesianNetwork bn) {

        // Gather the list of relevant variables
        ArrayList<Node> vars = new ArrayList<>();
        ArrayList<Node> AlphaVars = new ArrayList<>();

        // The evidence values for P(X,E1,E2,E3,...) includes x
        // however the evidence for alpha = P(E1,E2,E3,...) does not, since x is not relevant
        ArrayList<String> Pe = new ArrayList<>();
        ArrayList<String> Ae = new ArrayList<>();

        // Include all of the vars that are either X, in E, or are a parent of X or an element of E
        // Do the same for the variables relevant to alpha, except exclude X and any variables not ancestors of some E
        String temp;
        boolean added;
        for(Node i : bn.getVariables()) {
            added = false;
            if(!vars.contains(i)) {
                if (X == i) {
                    vars.add(i);
                    Pe.add(x);
                    added = true;
                }
                if (!added) {
                    for (Node j : E) {
                        if (j == i) {
                            vars.add(i);
                            AlphaVars.add(i);
                            temp = e.get(E.indexOf(i));
                            Pe.add(temp);
                            Ae.add(temp);
                            added = true;
                            break;
                        }
                    }
                }
                if (!added) {
                    for (Node j : i.getPredecessors()) {
                        if (j == X) {
                            vars.add(i);
                            Pe.add("none");
                            added = true;
                            break;
                        }
                    }
                }
                if(!added) {
                    for (Node j : E) {
                        for(Node k : i.getPredecessors()) {
                            if (j == k) {
                                vars.add(i);
                                AlphaVars.add(i);
                                Pe.add("none");
                                Ae.add("none");
                                break;
                            }
                        }
                    }
                }
            }
        }

        // Check
        for (Node i : AlphaVars) {
            System.out.println("AV: " + i.getVariable());
        }

        for (Node i : vars) {
            System.out.println("V: " + i.getVariable());
        }

        for (String i : Ae) {
            System.out.println("Ae: " + i);
        }

        for (String i : Pe) {
            System.out.println("Pe: " + i);
        }

//        System.out.println("Calculate 1/a: ");
        double a = EnumerateAll(AlphaVars, Ae,0);
//        System.out.println("Calculate P: ");
        double p = EnumerateAll(vars, Pe, 0);

//        System.out.println("p: " + p +"\n" + "a: " + a);

        return (p / a);
    }

    private static double EnumerateAll(ArrayList<Node> V, ArrayList<String> ev, int Vi) {

//        System.out.println();
//        System.out.println("V: ");
//        for (Node i : V) {
//            System.out.println(i.getVariable());
//        }
//        System.out.println("ev: ");
//        for (String i : ev) {
//            System.out.println(i);
//        }
//        System.out.println();
//        System.out.println();

        // Base Step: Check if all variables have been used, if so end recursion
        if (Vi == V.size()) { return 1.0; }

        // Else, grab the first variable from vars and it's evidence value from the parallel list ev
        Node Y = V.get(Vi);
        String y = ev.get(Vi);

        // Then check if Y has a value y in e, if so:
        if (!y.equals("none")) {

            // Calculate P(Y=y | Parents(Y)) X EnumerateAll(V,e)
            // If no parents, then just P(Y=y) X EnumerateAll(V,e)
            if(Y.getParents().size() == 0) {


                for (int i = 0; i < Y.getMeasure().length; i++) {
                    if(Y.getMeasure()[i].equals(y)) {
//                        System.out.println("Vi: "+Vi+" "+ Y.getTable()[i][0] + " " + Y.getVariable());
                        return Y.getTable()[i][0] * EnumerateAll(V,ev,Vi+1);
                    }
                }


            } else { // Else, for each parent get it's y and determine P(Y=y | Pa1 = p1y, Pa2=p2y, ...) X EnumerateAll(V,e)

                int tableindex = 0;
                // So, determine the number of parents of Y
                for (int i = 0; i < Y.getParents().size(); i++) {

                    // pow is a temp variable used to help determine the index of the correct entry in the table for
                    // the given values of the Parent(s)
                    int pow = Y.getParents().size()-1-i;

//                    System.out.println("i: " + i);

                    // And for each parent get its possible evidence values
                    String yofParent = ev.get(V.indexOf(Y.getParents().get(i)));

//                    System.out.println("Y: " + Y.getVariable());
//                    System.out.println("P: " + Y.getParents().get(i).getVariable());
//                    System.out.println("yofP: " + yofParent);

                    String[] measureofParent = Y.getParents().get(i).getMeasure();

                    //Then find if its evidence value indicates a 0 or 1 and use that to calculate the table entry index
                    for (int j = 0; j < measureofParent.length; j++) {
                        if(measureofParent[j].equals(yofParent)) {
                            tableindex += Math.pow(2, pow) * j;
//                            System.out.println(tableindex);
                        }
                    }
                }

                //Then find if Y's evidence value indicates a 0 or 1 and use that to calculate the table entry's index
                for (int i = 0; i < Y.getMeasure().length; i++) {
                    if (Y.getMeasure()[i].equals(y)) {
//                        System.out.println("Vi: "+Vi+" "+Y.getTable()[tableindex][i] + " " + Y.getVariable());
                        return Y.getTable()[tableindex][i] * EnumerateAll(V, ev, Vi + 1);
                    }
                }
            }

        } else {

            //Else, for each possible measurement value of Y, set y = measure and ca
            String y1 = Y.getMeasure()[0];
            String y2 = Y.getMeasure()[1];

            ArrayList<String> ey1 = new ArrayList<>();
            ArrayList<String> ey2 = new ArrayList<>();

            ey1.addAll(ev);
            ey2.addAll(ev);

            ey1.set(Vi, y1);
            ey2.set(Vi, y2);

//            System.out.println(y1);
//            System.out.println(y2);
//            System.out.println("Split");

            return EnumerateAll(V, ey1, Vi) + EnumerateAll(V,ey2,Vi);

        }

        return -100.0;
    }

}
