import InterchangeFormat.IFException;
import medicaldiagnosisgraphs.MedicalDiagnosisGraph;
import medicaldiagnosisgraphs.MedicalDiagnosisGraphNode;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.Enumeration;
import java.util.Vector;

public class test {
    public static void main(String[] args){
        try {
            // Read the example file and output the probability of B
            MedicalDiagnosisGraph G = new MedicalDiagnosisGraph("./data/alarm_zeros.bif");
            Vector nodes = G.get_nodes();
            MedicalDiagnosisGraphNode n = ((MedicalDiagnosisGraphNode)nodes.elementAt(0));
            System.out.println("The First Output:");
            System.out.println(n.get_name());
            n.get_Prob().print();

            // Create string of variable-value pairs for probability table at node 0
            String[][] s = new String[1][2];
            s[0][0] = "B";
            s[0][0] = "False";

            // Compute probability with given variable-value pairs;
            System.out.println("The Second Output:");
            //System.out.println(n.get_function_value(s));

            // get_parents() works too;
            Vector children = n.get_children();
            for(Enumeration e = children.elements(); e.hasMoreElements(); ){
                MedicalDiagnosisGraphNode no = (MedicalDiagnosisGraphNode)(e.nextElement());
                System.out.println("The Third Output:");
                System.out.println("\t" + no.get_name());
                // Get the probability table object for the node -> Look at ProbilityFunction.java for info
                no.get_Prob().print();
            }
        } catch (FileNotFoundException e) {
            System.out.println("File not found " + e.toString());
        } catch (IFException e) {
            System.out.println("Formatting Incorrect " + e.toString());
        }
        catch(IOException e){
            System.out.println("File not found " + e.toString());
        }
    }
}
