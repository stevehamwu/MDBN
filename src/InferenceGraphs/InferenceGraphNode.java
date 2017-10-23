/**
 * InferenceGraphNode.java
 * @author Fabio G. Cozman 
 * Copyright 1996 - 1999, Fabio G. Cozman,
 *          Carnergie Mellon University, Universidade de Sao Paulo
 * fgcozman@usp.br, http://www.cs.cmu.edu/~fgcozman/home.html
 *
 * The JavaBayes distribution is free software; you can
 * redistribute it and/or modify it under the terms of the GNU General
 * Public License as published by the Free Software Foundation (either
 * version 2 of the License or, at your option, any later version), 
 * provided that this notice and the name of the author appear in all 
 * copies. Upon request to the author, some of the packages in the 
 * JavaBayes distribution can be licensed under the GNU Lesser General
 * Public License as published by the Free Software Foundation (either
 * version 2 of the License, or (at your option) any later version).
 * If you're using the software, please notify fgcozman@usp.br so
 * that you can receive updates and patches. JavaBayes is distributed
 * "as is", in the hope that it will be useful, but WITHOUT ANY WARRANTY;
 * without even the implied warranty of MERCHANTABILITY or FITNESS FOR A
 * PARTICULAR PURPOSE. See the GNU General Public License for more details.
 * You should have received a copy of the GNU General Public License
 * along with the JavaBayes distribution. If not, write to the Free
 * Software Foundation, Inc., 675 Mass Ave, Cambridge, MA 02139, USA.
 */
package InferenceGraphs;

import BayesianNetworks.*;

import java.io.*;
import java.awt.*;
import java.util.*;

public class InferenceGraphNode {
    InferenceGraph ig;

    ProbabilityVariable pv;
    ProbabilityFunction pf;

    Vector parents = new Vector();
    Vector children = new Vector();

    private final String defaultInferenceGraphNodeValues[] = { "true", "false" };
    private final BayesNet defaultInferenceGraphNodeBayesNet = null;
    private final Vector defaultInferenceGraphNodeProperties = null;

    /*
     * Constructor for a InferenceGraphNode object. The created
     * node is in an incomplete state; the constructor assumes the
     * node is new and not necessarily attached to the current 
     * network in the InferenceGraph; no parents nor    
     * children are defined for such a node.                   
     */
    InferenceGraphNode(InferenceGraph i_g, String name) {
        ig = i_g;

        // Initialize the variable
        pv = new ProbabilityVariable(defaultInferenceGraphNodeBayesNet,
                                     name, BayesNet.INVALID_INDEX,
                                     defaultInferenceGraphNodeValues,
                                     defaultInferenceGraphNodeProperties);
        // Initialize the probability function
    	init_dists();
    }

    /*
     * Constructor for a InferenceGraphNode object.             
     * Note that parents and children are not properly set here. 
     */
    InferenceGraphNode(InferenceGraph i_g,
    ProbabilityVariable p_v, ProbabilityFunction p_f) {
        ig = i_g;
        pv = p_v;
        pf = p_f;
    }

    /* 
     * Initialization for the probability function               
     * in the InferenceGraphNode.                                
     */
    void init_dists() {
        int i, total_values;
        double new_value;
        Enumeration e;
        InferenceGraphNode pnode;

        // Create the probability_variables
        ProbabilityVariable pvs[] =
            new ProbabilityVariable[parents.size() + 1];
        pvs[0] = pv;

        total_values = pv.number_values();
        new_value = 1.0/((double)(total_values));

        for (i = 1, e = parents.elements(); e.hasMoreElements(); i++) {
            pnode = (InferenceGraphNode)(e.nextElement());
            pvs[i] = pnode.pv;
            total_values *= pnode.pv.number_values();
        }

        // Compute the default (uniformly distributed) values
   	    double dists[] = new double[total_values];
	    for (i=0; i < dists.length; i++)
	        dists[i] = new_value;

        // Construct the ProbabilityFunction
        pf = new ProbabilityFunction(defaultInferenceGraphNodeBayesNet,
                                     pvs, dists,
                                     defaultInferenceGraphNodeProperties);
	}

   /**
     * Get a single value of the probability function in the
     * node given a list of pairs (Variable Value). The list
     * specifies which element of the function is referred to.
     */
    public double get_function_value(String variable_value_pairs[][]) {
            return(pf.evaluate(variable_value_pairs));
    }
    
    /**
     * Get an array containing probability values.
     */
    public double[] get_function_values() {
            return(pf.get_values());
    }

    /**
     * Get an array containing probability values;*/
    public double[] get_function_values(int index) {
          return(pf.get_values());
    }

    /**
     * Set an array containing probability values; 
     */
    public void set_function_values(double[] fv) {
            pf.set_values(fv);
    }

    public ProbabilityFunction get_Prob(){
	return pf;
	} 
    
    /* ******************** Public methods ******************** */

    /**
     * Return the name of the variable in the node.
     */
    public String get_name() {
    	return(pv.get_name());
    }

    /**
     * Set the name of the variable.
     */
    public void set_name(String n) {
        pv.set_name(n);
    }
    
    /**
     * Get the name of all variables in the probability function.
     */
    public String[] get_all_names() {
        String[] ns = new String[pf.number_variables()];
        for (int i=0; i<ns.length; i++)
            ns[i] = pf.get_variable(i).get_name();
        return(ns);
    }

    /**
     * Return the values of the variable in the node.
     */
    public String[] get_values() {
	    return(pv.get_values());
    }
    
    /**
     * Get all values for variables in the function in the node.
     */
    public String[][] get_all_values() {
        int i, j;
        String all_values[][] = new String[pf.number_variables()][];
        DiscreteVariable dv;
        for (i=0; i<pf.number_variables(); i++) {
            dv = pf.get_variable(i);
            all_values[i] = new String[dv.number_values()];
            for (j=0; j<all_values[i].length; j++) {
                all_values[i][j] = dv.get_value(j);
            }
        }
        return(all_values);
    }

    /**
     * Return the number of values in the variable in the node.
     */
    public int get_number_values() {
    	return(pv.number_values());
    }

    /**
     * Indicate whether the node has parents.
     */
    public boolean hasParent() {
    	return(pf.number_variables() > 1);
    }

    /**
     * Return the parents of a node as an Enumeration object.
     */
    public Vector get_parents() {
        return(parents);
    }

    /**
     * Return the children of a node as an Enumeration object.
     */
    public Vector get_children() {
        return(children);
    }

}


