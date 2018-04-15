import java.util.ArrayList;

class BayesianNetwork {

    // Private Field
    private ArrayList<Node> Variables;

    // Constructor
    BayesianNetwork() {
        Variables = new ArrayList<>();
    }

    // Methods
    ArrayList<Node> getVariables() {
        return Variables;
    }

    void setVariables(ArrayList<Node> variables) {
        Variables = variables;
    }

    void addVariable(Node var) {
        Variables.add(var);
    }
}
