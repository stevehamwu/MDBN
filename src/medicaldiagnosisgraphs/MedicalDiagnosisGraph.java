package medicaldiagnosisgraphs;

import BayesianNetworks.BayesNet;
import BayesianNetworks.DiscreteVariable;
import BayesianNetworks.ProbabilityFunction;
import BayesianNetworks.ProbabilityVariable;
import InterchangeFormat.IFException;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.PrintStream;
import java.util.Enumeration;
import java.util.Hashtable;
import java.util.Vector;

public class MedicalDiagnosisGraph {
	BayesNet bn;

	Vector nodes = new Vector();

	Vector<double[]> theta;

	private final String defaultBayesNetName = "MedicalDiagnosisNetwork";

	public final static int MARGINAL_POSTERIOR = 1;
	public final static int EXPECTATION = 2;
	public final static int EXPLANATION = 3;
	public final static int FULL_EXPLANATION = 4;
	public final static int SENSITIVITY_ANALYSIS = 5;

	/*
     * Simple constructor for an InferenceGraph.
     */
	public MedicalDiagnosisGraph(BayesNet bn) {
		this.bn = new BayesNet(bn);
		convert_bayes_net();
		genTheta();
	}

	/*
     * Constructor for an InferenceGraph.
     */
	public MedicalDiagnosisGraph(String filename) throws FileNotFoundException, IFException {
		bn = new BayesNet(new java.io.DataInputStream(new FileInputStream(filename)));
		convert_bayes_net();
		genTheta();
	}

	public Vector<double[]> getTheta(){
		return theta;
	}

	public void genTheta() {
		theta = new Vector<>();
		for (Enumeration e = nodes.elements(); e.hasMoreElements(); ) {
			MedicalDiagnosisGraphNode node = (MedicalDiagnosisGraphNode)(e.nextElement());
			theta.add(node.get_function_values());
		}
	}

	public void printTheta(){
		for (Enumeration e = theta.elements(); e.hasMoreElements();) {
			double[] thetat = (double[])(e.nextElement());
			for(double t:thetat){
				System.out.print(t + " ");
			}
			System.out.println();
		}
	}

 /*
   * Get the contents of the graph.
   */

	public BayesNet get_bayes_net() {
		return convert_graph();
	}

	/*
         * Convert a BayesNet object to the InferenceGraph
         * structure; returns true if the conversion is successful.
         */
	boolean convert_bayes_net() {
		ProbabilityVariable pv = null;
		ProbabilityFunction pf = null;

		for (int i = 0; i < bn.number_variables(); i++) {
			pv = bn.get_probability_variable(i);
			pf = null;
			for (int j = 0; j < bn.number_probability_functions(); j++) {
				pf = bn.get_probability_function(j);
				if (pf.get_variable(0) == pv)
					break;
			}

			if (pf == null)
				return false;

			nodes.addElement(new MedicalDiagnosisGraphNode(this, pv, pf));
		}
		generate_parents_and_children();

		return true;
	}

	/*
     * Generate the parents and children for the nodes.
     */
	private void generate_parents_and_children() {
		int i, j;
		DiscreteVariable variables[];
		ProbabilityFunction pf;
		MedicalDiagnosisGraphNode base_node, node;
		Enumeration e;

		for (e = nodes.elements(); e.hasMoreElements(); ) {
			base_node = (MedicalDiagnosisGraphNode) (e.nextElement());

			pf = base_node.pf;
			variables = pf.get_variables();

			for (i = 1; i < variables.length; i++) {
				node = get_node(variables[i]);
				if (node == null)
					continue;
				base_node.parents.addElement(node);
				node.children.addElement(base_node);
			}
		}
	}


	/*
     * Get the node corresponding to a given variable.
     */
	private MedicalDiagnosisGraphNode get_node(DiscreteVariable dv) {
		MedicalDiagnosisGraphNode node;
		for (Enumeration e = nodes.elements(); e.hasMoreElements(); ) {
			node = (MedicalDiagnosisGraphNode)e.nextElement();
			if (node.pv == dv)
				return node;
		}
		return null;
	}

    /*
     * Convert the MedicalDiagnosisGraph structure to a BayesNet object.
     */
	BayesNet convert_graph(){
		int i;
		Enumeration e;
		MedicalDiagnosisGraphNode node;

		// Create the arrays of variables and functions
		ProbabilityVariable pvs[] = new ProbabilityVariable[nodes.size()];
		ProbabilityFunction pfs[] = new ProbabilityFunction[nodes.size()];

		// Insert the empty arrays
		bn.set_probability_variables(pvs);
		bn.set_probability_functions(pfs);

		// Collect all variables and functions in the nodes
		// into the new BayesNet
		for(i = 0, e = nodes.elements(); e.hasMoreElements(); i++){
			node = (MedicalDiagnosisGraphNode)(e.nextElement());
			bn.set_probability_variable(i, node.pv);
			bn.set_probability_function(i, node.pf);
		}
		return bn;
	}

  	/*
  	 * Generate a valid name for a new variable.
   	*/
  	private String generate_name(int i){
  		MedicalDiagnosisGraphNode no;

		// generate names of the form a..z, a1..z1, a2..z2, etc.
		char namec = (char) ((int) 'a' + i % 26);
		int suffix = i / 26;
		String name;
		if(suffix > 0)
			name = new String("" + namec + suffix);
		else
			name = new String("" + namec);
		// check whether there is a variable with this name
		for(Enumeration e = nodes.elements(); e.hasMoreElements();){
			no = (MedicalDiagnosisGraphNode)(e.nextElement());
			if(no.get_name().equals(name))
				return generate_name(i+1);
		}
		return name;
	}

	/* *************** PUBLIC METHODS ********************* */

	/**
	 * Get the name of the network.
	 */
	public String get_name(){
		return bn.get_name();
	}

	/**
	 * Set the name of the network.
	 */
	public void set_name(String n){
		bn.set_name(n);
	}

	/**
	 * Get the properties of the network.
	 */
	public Vector get_network_properties() {
		return bn.get_properties();
	}

	/**
	 * Set the properties of the network.
	 */
	public void set_network_properties(Vector prop){
		bn.set_properties(prop);
	}

	/**
	 * Remove a property from the network.
	 */
	public void remove_network_property(int index){
		bn.remove_property(index);
	}

	/**
	 * Add a property to the network.
	 */
	public void add_network_property(String prop){
		bn.add_property(prop);
	}

	/**
	 * Determine whether or not a name is valid and/or repeated.
	 */
	public String check_name(String n){
		MedicalDiagnosisGraphNode no;
		String nn = validate_value(n);
		for(Enumeration e = nodes.elements(); e.hasMoreElements(); ){
			no = (MedicalDiagnosisGraphNode)(e.nextElement());
			if(no.get_name().equals(nn))
				return null;
		}
		return nn;
	}

	/**
	 * Check whether a string is a valid name.
	 */
	public String validate_value(String value){
		StringBuffer str = new StringBuffer(value);
		for(int i = 0; i < str.length(); i++){
			if(str.charAt(i) == ' ')
				str.setCharAt(i, '_');
		}
		return str.toString();
	}

	/**
	 * Print the QuasiBayesNet.
	 */
	public void print_bayes_net(PrintStream out) {
		BayesNet bn = get_bayes_net();
		bn.print(out);
	}

	/**
	 * Save the Bayesian network into a PrintStream in the BIF InterchangeFormat.
	 */
	public void save_bif(PrintStream out){
		BayesNet bn = get_bayes_net();
		bn.save_bif(out);
	}

	/**
	 * Print method for an MedicalDiagnosisGraph
	 */
	public void print(){
		print(System.out);
	}

	public void print(PrintStream out){
		BayesNet bn = get_bayes_net();
		bn.print(out);
	}

	/**
	 * Get the nodes in the network.
	 */
	public Vector get_nodes() {
		return nodes;
	}

	/**
	 * Get the nodes in the network as an Enumeration object.
	 */
	public Enumeration elements(){
		return nodes.elements();
	}

	/**
	 * Get the number of variables in the network
	 */
	public int number_nodes(){
		return nodes.size();
	}

	/**
	 * Create a new node in the network.
	 */
	public void create_node(){
		String n = generate_name(nodes.size());
		nodes.addElement(new MedicalDiagnosisGraphNode(this, n));

		// Synchronize the BayesNet object and the graph.
		convert_graph();
	}

	/**
	 * Create an arc from parent to child.
	 */
	public boolean create_arc(MedicalDiagnosisGraphNode parent, MedicalDiagnosisGraphNode child){
		// Check whether the given parent is already a parent of the
		// given child.
		for(Enumeration e = child.parents.elements(); e.hasMoreElements(); ){
			if(parent == ((MedicalDiagnosisGraphNode)(e.nextElement())))
				return false;
		}

		// First put child into the children of parent
		parent.children.addElement(child);
		// Second put parent into the parents of child
		child.parents.addElement(parent);

		// The parent is not further affected by the arc.
		// The child must have its ProbabilityFunction
		// object updated.
		child.init_dists();

		// Synchronize the QuasiBayesNet object and the graph.
		convert_graph();

		return true;
	}

	/**
	 * Delete a node in the network.
	 */
	public void delete_node(MedicalDiagnosisGraphNode node){
		Enumeration e;
		MedicalDiagnosisGraphNode parent, child;

		// First, remove node from all its children
		for(e = node.children.elements(); e.hasMoreElements(); ){
			child = (MedicalDiagnosisGraphNode)(e.nextElement());
			child.parents.removeElement(node);
			child.init_dists();
		}

		// Second remove parent into the parents of child
		for(e = node.parents.elements(); e.hasMoreElements(); ){
			parent = (MedicalDiagnosisGraphNode)(e.nextElement());
			parent.children.removeElement(node);
		}

		// Third remove the node itself
		nodes.removeElement(node);

		// Synchronize the QuasiBayesNet object and the graph.
		convert_graph();
	}

	/**
	 * Delete the arc from parent to child.
	 */
	public void delete_arc(MedicalDiagnosisGraphNode parent, MedicalDiagnosisGraphNode child){
		// First remove child into the children of parent
		parent.children.removeElement(child);
		// Second remove parent into the parents of child
		child.parents.removeElement(parent);

		// The parent is not further affected by the arc.
		// The child must have its ProbabilityFunction
		// object updated.
		child.init_dists();

		// Synchronize the QuasiBayesNet object and the graph.
		convert_graph();
	}

	/**
	 * Determines whether the connection of
	 * bottom_node to head_node would cause the
	 * network to have a cycle.
	 */
	public boolean hasCycle(MedicalDiagnosisGraphNode bottom_node, MedicalDiagnosisGraphNode head_node){
		Vector children;
		Enumeration e;
		MedicalDiagnosisGraphNode next_node, child_node;

		// Array with enough space to have all nodes
		MedicalDiagnosisGraphNode listed_nodes[] = new MedicalDiagnosisGraphNode[nodes.size()];

		// Hashtable for efficient lookup of already listed nodes
		Hashtable hashed_nodes = new Hashtable();

		// Index of last node in listed_nodes
		int last_listed_node_index = 0;

		// Initialize: head_node is marked and inserted
		int current_listed_node_index = 0;
		listed_nodes[0] = head_node;
		hashed_nodes.put(head_node.pv.get_name(), head_node);

		// Now expand for children until no more children, or
		// when a child is equal to bottom_node
		while(current_listed_node_index <= last_listed_node_index){
			// Select the next node to be expanded
			next_node = listed_nodes[current_listed_node_index];
			// Update the index that indicates nodes to be expanded
			current_listed_node_index++;

			// Get all children of the node being expanded
			children = next_node.children;
			// Expand the node: put all its children into list
			for(e = children.elements(); e.hasMoreElements(); ){
				child_node = (MedicalDiagnosisGraphNode)(e.nextElement());
				if(child_node == bottom_node)	// Cycle is detected
					return true;
				if(hashed_nodes.containsKey(child_node.pv.get_name())){
					hashed_nodes.put(child_node.pv.get_name(), child_node);
					last_listed_node_index++;
					listed_nodes[last_listed_node_index] = child_node;
				}
			}
		}
		return false;
	}

	/**
	 * Change the values of a variable. Note that, if the number
	 * of new values is different from the number of current values,
	 * this operation resets the
	 * probability values of the variable and all its children.
	 */
	public void change_values(MedicalDiagnosisGraphNode node, String values[]){
		MedicalDiagnosisGraphNode cnode;
		Vector children;
		Enumeration e;

		if(node.pv.number_values() == values.length){
			node.pv.set_values(values);
			return;
		}

		node.pv.set_values(values);
		node.init_dists();

		children = node.get_children();
		for(e = children.elements(); e.hasMoreElements(); ){
			cnode = (MedicalDiagnosisGraphNode)(e.nextElement());
			cnode.init_dists();
		}

		// Synchronize the BayesNet object and the graph.
		convert_graph();
	}

}