import InterchangeFormat.IFException;
import medicaldiagnosisgraphs.MedicalDiagnosisGraph;

import java.io.FileNotFoundException;
import java.util.Vector;

public class MedicalDiagnosisBayesianNetwork {
    private static final String bnfile = "alarm_com.bif";  //BayesianNetwork file
    private static final String sfile = "records.dat"; //Sample file
    private MedicalDiagnosisGraph mdg;
    private BayesianTrainer trainer;
    private Vector<Sample> slist;

    public MedicalDiagnosisBayesianNetwork() {
        try {
            mdg = new MedicalDiagnosisGraph("./data/" + bnfile);
            trainer = new BayesianTrainer(mdg);
        } catch (FileNotFoundException e) {
            System.out.println("File not found!!!");
        } catch (IFException e) {
            e.printStackTrace();
        }
    }

    public void train() {
        trainer.readSamples(sfile);
        trainer.E_Step();
        slist = trainer.getSlist();
    }

    public static void main(String[] args) {
        MedicalDiagnosisBayesianNetwork mdbn = new MedicalDiagnosisBayesianNetwork();
        mdbn.train();
        mdbn.mdg.printTheta();
    }
}
