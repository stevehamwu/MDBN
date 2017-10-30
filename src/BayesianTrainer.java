import BayesianNetworks.ProbabilityVariable;
import medicaldiagnosisgraphs.MedicalDiagnosisGraph;
import medicaldiagnosisgraphs.MedicalDiagnosisGraphNode;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.Enumeration;
import java.util.Vector;

public class BayesianTrainer {
    private Vector<Sample> slist;
    private MedicalDiagnosisGraph mdg;
    BayesianTrainer(MedicalDiagnosisGraph mdg){
        this.mdg = mdg;
    }

    public void readSamples(String filename){
        slist = new Vector<Sample>();
        try {
            Sample sample;

            FileReader fr = new FileReader("./data/" + filename);
            BufferedReader br = new BufferedReader(fr);

            String str = br.readLine();
            while(str != null) {
                sample = new Sample(str.replaceAll("\"", "").split(" "));
                slist.add(sample);
                str = br.readLine();
            }
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public Vector<Sample> getSlist() {
        return slist;
    }

    public MedicalDiagnosisGraph getMDG() {
        return mdg;
    }

    public void E_Step(){
        for (Enumeration e = slist.elements(); e.hasMoreElements(); ) {
            Sample sample = (Sample)(e.nextElement());
            if (!sample.isComplete()) {
                sample.completement(mdg);
                /*Sample[] comsamples = sample.getComsamples();
                for (int i = 0; i < comsamples.length; i++) {
                    comsamples[i].print();
                }*/
            }
        }
    }

    public void M_Step(){
        for(Enumeration e = mdg.elements();e.hasMoreElements(); ){
            MedicalDiagnosisGraphNode node = (MedicalDiagnosisGraphNode)(e.nextElement());
            ProbabilityVariable[] pfs = (ProbabilityVariable[]) node.get_Prob().get_variables();
            Vector<String[][]> product = Cartesian_product(pfs);
            /*for (int i = 0; i < product.size(); i++) {
                String[][] prod = product.elementAt(i);
                for (int j = 0; j < prod.length; j++) {
                    System.out.print(prod[j][0] + ":" + prod[j][1] + " ");
                }
                System.out.println();
            }*/
            updateProb(product);
            mdg.genTheta();
        }
    }

    private void updateProb(Vector<String[][]> pvs) {
        for (Enumeration e = pvs.elements(); e.hasMoreElements();){
            double weight = 0.0;
            String[][] pv = (String[][])(e.nextElement());
            String name = pv[0][0];
            for (Enumeration e1 = slist.elements(); e1.hasMoreElements();){
                Sample sample = (Sample)(e1.nextElement());
                if(sample.match(pv)){
                    weight += sample.getWeight();
                }else {
                    for(Sample comsample:sample.getComsamples()){
                        if(comsample.match(pv)){
                            weight += comsample.getWeight();
                            break;
                        }
                    }
                }
            }
            double prob = weight/slist.size();
            MedicalDiagnosisGraphNode node = mdg.get_node(name);
            node.set_function_value(pv, prob);
        }
    }

    //get cartesian product of all ProbabilityVariables's values
    private Vector<String[][]> Cartesian_product(ProbabilityVariable[] pfs){
        Vector<String[][]> product = new Vector<String[][]>();
        for (int i = 0; i < pfs[0].get_values().length; i++) {
            String[] s = new String[2];
            s[0] = pfs[0].get_name();
            s[1] = pfs[0].get_values()[i];
            String[][] str = new String[][]{s};
            product.add(str);
        }

        for (int i = 1; i < pfs.length; i++) {
            int length = pfs[i].get_values().length;
            String[][] s = new String[length][2];
            for (int j = 0; j < length; j++) {
                s[j][0] = pfs[i].get_name();
                s[j][1] = pfs[i].get_values()[j];
            }
            product = join(product, s);
        }
        return product;
    }

    private Vector<String[][]> join(Vector<String[][]> value1, String[][] value2){
        Vector<String[][]> value = new Vector<>();
        for(Enumeration e = value1.elements(); e.hasMoreElements();){
            String[][] v1 = (String[][])(e.nextElement());
            for (int i = 0; i < value2.length; i++) {
                value.add(extend(v1, value2[i]));
            }
            for(String[] v2:value2){
                value.add(extend(v1, v2));
            }
        }

        return value;
    }

    private String[][] extend(String[][] str1, String[] str2){
        String[][] str = new String[str1.length+1][2];
        int i;
        for (i = 0; i < str1.length; i++) {
            str[i] = str1[i];
        }
        str[i] = str2;

        return str;
    }

}
