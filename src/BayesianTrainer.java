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
//            ProbabilityVariable[] pfs = (ProbabilityVariable[]) node.get_Prob().get_variables();
            Vector<String[][]> product = node.get_prob_string();

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
            double weight1 = 0.0, weight2 = 0.0;
            String[][] pv = (String[][])(e.nextElement());
            String[][] pv_parents = null;
            boolean flag = false;
            if(pv.length == 1){
                weight2 = slist.size();
                flag = true;
            }else {
                pv_parents = new String[pv.length-1][2];
                for (int i = 0; i < pv.length - 1; i++) {
                    pv_parents[i] = pv[i+1];
                }
            }
            String name = pv[0][0];
            for (Enumeration e1 = slist.elements(); e1.hasMoreElements();){
                Sample sample = (Sample)(e1.nextElement());
                if(sample.match(pv)){
                    weight1 += sample.getWeight();
                }else {
                    for(Sample comsample:sample.getComsamples()){
                        if(comsample.match(pv)){
                            weight1 += comsample.getWeight();
                            break;
                        }
                    }
                }
                if(!flag){
                    if(sample.match(pv_parents)){
                        weight2 += sample.getWeight();
                    }else{
                        for(Sample comsample:sample.getComsamples()){
                            if(comsample.match(pv_parents)){
                                weight2 += comsample.getWeight();
                                break;
                            }
                        }
                    }
                }
            }
            double prob;
            if(weight2 == 0.0){
                prob = 0.0;
                System.out.println("");
            }else{
                prob = weight1/weight2;
            }
            MedicalDiagnosisGraphNode node = mdg.get_node(name);
            node.set_function_value(pv, prob);
        }
    }



}
