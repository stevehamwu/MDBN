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
    private Vector<double[]> theta;

    BayesianTrainer(MedicalDiagnosisGraph mdg){
        this.mdg = mdg;
        theta = mdg.getTheta();
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
            String[][] s = new String[pfs.length][2];
            for(int i = 0; i < pfs.length; i++){
                s[i][0] = pfs[i].get_name();
            }
        }
    }
}
