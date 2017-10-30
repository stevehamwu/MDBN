import BayesianNetworks.ProbabilityFunction;
import BayesianNetworks.ProbabilityVariable;
import InferenceGraphs.InferenceGraph;
import InferenceGraphs.InferenceGraphNode;
import InterchangeFormat.IFException;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.Enumeration;
import java.util.Vector;

public class DJ{
	public static void main(String[] args){
		try{
		    // Read the example file and output the probability of B
			InferenceGraph G = new InferenceGraph("./data/alarm_com.bif");
			Vector nodes = G.get_nodes();
			InferenceGraphNode n = ((InferenceGraphNode)nodes.elementAt(1));
			System.out.println("The First Output:");
			System.out.println(n.get_name());
			System.out.println(n.get_number_values());

			n.get_Prob().print();

			//Create string of variable-value pairs for probability table at node 0*/
			String name = n.get_name();
			Vector<InferenceGraphNode> parents = n.get_parents();
			for(Enumeration e = parents.elements(); e.hasMoreElements();){
				InferenceGraphNode no = (InferenceGraphNode)(e.nextElement());
			}
			String[][] s = new String[3][2];
			s[0][0] = "StrokeVolume";
			s[0][1] = "Normal";
			s[1][0] = "LVFailure";
			s[1][1] = "True";
			s[2][0] = "Hypovolemia";
			s[2][1] = "False";
			ProbabilityFunction pf = n.get_Prob();

			//Compute probability with given variable-value pairs;
            System.out.println("The Second Output:");
			System.out.println(n.get_function_value(s));
			ProbabilityVariable[] pvs = (ProbabilityVariable[]) pf.get_variables();

            //get_parents() works too;
			Vector children = n.get_children();
			for (Enumeration e = children
					.elements(); e.hasMoreElements();) {
                InferenceGraphNode no = (InferenceGraphNode)(e.nextElement());
                System.out.println("The Third Output:");
				System.out.println("\t" + no.get_name());
                //Get the probability table object for the node -> Look at ProbabilityFunction.java for info
				no.get_Prob().print();
			}				
			
		}
		catch(IFException e){
			System.out.println("Formatting Incorrect " + e.toString());
		}
		catch(FileNotFoundException e) {
			System.out.println("File not found " + e.toString());
		}
		catch(IOException e){
			System.out.println("File not found " + e.toString());
		}
	}
}
