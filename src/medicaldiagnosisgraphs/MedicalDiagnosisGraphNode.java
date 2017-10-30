package medicaldiagnosisgraphs;

import BayesianNetworks.BayesNet;
import BayesianNetworks.DiscreteVariable;
import BayesianNetworks.ProbabilityFunction;
import BayesianNetworks.ProbabilityVariable;

import java.util.Enumeration;
import java.util.Vector;

public class MedicalDiagnosisGraphNode {
    MedicalDiagnosisGraph mdg;

    ProbabilityVariable pv;
    ProbabilityFunction pf;

    Vector parents = new Vector();
    Vector children = new Vector();

    private final String defaultMedicalDiagnosisGraphNodeValues[] = {"true", "false"};
    private final BayesNet defaultMedicalDiagnosisGraphNodeBayesNet = null;
    private final Vector defaultMedicalDiagnosisGraphNodeProperties = null;

    /*
     * Constructor for a MedicalDiagnosisGraphNode object. The created
     * node is in an incomplete state; the constructor assumes the
     * node is new and not necessarily attached to the current
     * network in the MedicalDiagnosisGraph; no parents nor
     * children are defined for such a node.
     */
    MedicalDiagnosisGraphNode(MedicalDiagnosisGraph mdg, String name) {
        this.mdg = mdg;

        pv = new ProbabilityVariable(defaultMedicalDiagnosisGraphNodeBayesNet, name, BayesNet.INVALID_INDEX, defaultMedicalDiagnosisGraphNodeValues, defaultMedicalDiagnosisGraphNodeProperties);

        init_dists();
    }

    /*
     * Constructor for a MedicalDiagnosisGraphNode object.
     * Note that parents and children are not properly set here.
     */
    MedicalDiagnosisGraphNode(MedicalDiagnosisGraph mdg, ProbabilityVariable pv, ProbabilityFunction pf){
        this.mdg = mdg;
        this.pv = pv;
        this.pf = pf;
    }

    /*
     * Initialization for the probability function
     * in the MedicalDiagnosisGraphNode.
     */
    void init_dists() {
        int i, total_values;
        double new_value;
        Enumeration e;
        MedicalDiagnosisGraphNode pnode;

        // Create the probability_variables
        ProbabilityVariable pvs[] = new ProbabilityVariable[parents.size() + 1];
        pvs[0] = pv;

        total_values = pv.number_values();
        new_value = 1.0/((double)(total_values));

        for (i = 1, e = parents.elements(); e.hasMoreElements(); i++){
            pnode = (MedicalDiagnosisGraphNode)(e.nextElement());
            pvs[i] = pnode.pv;
            total_values *= pnode.pv.number_values();
        }

        // Compute the default (uniformly distributed) values
        double dists[] = new double[total_values];
        for(i = 0; i < dists.length; i++)
            dists[i] = new_value;

        pf = new ProbabilityFunction(defaultMedicalDiagnosisGraphNodeBayesNet, pvs, dists, defaultMedicalDiagnosisGraphNodeProperties);
    }

    /**
     * Get a single value of the probability function in the
     * node given a list of pairs (Variable Value). The list
     * specifies which element of the function is referred to.
     */
    public double get_function_value(String variable_value_pairs[][]){
        return pf.evaluate(variable_value_pairs);
    }

    /**
     * Get an array containing probability values.
     */
    public double[] get_function_values(){
        return pf.get_values();
    }

    /**
     * Get an array containing probability values;
     */
    public double[] get_function_values(int index) { return pf.get_values(); }

    /**
     * Set a single value of the probability function in the
     * node given a list of pairs (Variable Value). The list
     * specifies which element of the function is referred to.
     */
    public void set_function_value(String variable_value_pairs[][], double value){
        pf.set_value(variable_value_pairs, value);
    }

    /**
     * Set an array containing probability values;
     */
    public void set_function_values(double[] fv){
        pf.set_values(fv);
    }

    public ProbabilityFunction get_Prob() {
        return pf;
    }
    /* ******************** Public methods ******************** */

    /**
     * Return the name of the variable in the node.
     */
    public String get_name(){
        return pv.get_name();
    }

    /**
     * Set the name of the variable.
     */
    public void set_name(String n){
        pv.set_name(n);
    }

    /**
     * Get the name of all variables in the probability function.
     */
    public String[] get_all_names(){
        String[] ns = new String[pf.number_variables()];
        for(int i = 0; i < ns.length; i++)
            ns[i] = pf.get_variable(i).get_name();
        return ns;
    }

    /**
     * Return the values of the variable in the node.
     */
    public String[] get_values(){
        return pv.get_values();
    }

    /**
     * Get all values for variables in the function in the node.
     */
    public String[][] get_all_values(){
        int i, j;
        String all_values[][] = new String[pf.number_variables()][];
        DiscreteVariable dv;

        for(i = 0; i < pf.number_variables(); i++){
            dv = pf.get_variable(i);
            all_values[i] = new String[dv.number_values()];
            for(j = 0; j < all_values[i].length; j++){
                all_values[i][j] = dv.get_value(j);
            }
        }
        return all_values;
    }

    /**
     * Return the number of values in the variable in the node.
     */
    public int get_number_values(){
        return pv.number_values();
    }

    /**
     * Indicate whether the node has parents.
     */
    public boolean hasParent(){
        return pf.number_variables() > 1;
    }

    /**
     * Return the parents of a node as an Enumeration object.
     */
    public Vector get_parents(){
        return(parents);
    }

    /**
     * Return the children of a node as an Enumeration object.
     */
    public Vector get_children() {
        return children;
    }


}
