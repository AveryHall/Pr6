import java.util.ArrayList;

public class Node {

    // Private Fields
    private ArrayList<Node> Parents;
    private ArrayList<Node> Children;
    private ArrayList<Node> Ancestors;
    private ArrayList<Node> Predecessors;
    private String Variable;
    private double[][] Table;
    private String[] Measure;

    // Default Constructor
    Node(String V, double[][] T, String[] M) {
        Parents = new ArrayList<>();
        Children = new ArrayList<>();
        Ancestors = new ArrayList<>();
        Predecessors = new ArrayList<>();
        Variable = V;
        Table = T;
        Measure = M;
    }

    // Overloaded Constructor
    public Node(ArrayList<Node> P, ArrayList<Node> C, ArrayList<Node> A, ArrayList<Node> Pr, String V, double[][] T)
    {
        Parents = P;
        Children = C;
        Ancestors = A;
        Predecessors = Pr;
        Variable = V;
        Table = T;
    }

    // Methods
    ArrayList<Node> getParents() {
        return Parents;
    }

    void setParents(ArrayList<Node> parents) {
        Parents = parents;
    }

    ArrayList<Node> getChildren() {
        return Children;
    }

    void setChildren(ArrayList<Node> children) {
        Children = children;
    }

    ArrayList<Node> getAncestors() {
        return Ancestors;
    }

    void setAncestors(ArrayList<Node> ancestors) {
        Ancestors = ancestors;
    }

    ArrayList<Node> getPredecessors() {
        return Predecessors;
    }

    void setPredecessors(ArrayList<Node> predecessors) {
        Predecessors = predecessors;
    }

    String getVariable() {
        return Variable;
    }

    void setVariable(String variable) {
        Variable = variable;
    }

    double[][] getTable() {
        return Table;
    }

    void setTable(double[][] table) {
        Table = table;
    }

    String[] getMeasure() {
        return Measure;
    }

    void setMeasure(String[] measure) {
        Measure = measure;
    }

    void addParent(Node var) {
        Parents.add(var);
    }

    void addChild(Node var) {
        Children.add(var);
    }

    void addAncestor(Node var) {
        Ancestors.add(var);
    }

    void addPredecessor(Node var) {
        Predecessors.add(var);
    }

}
