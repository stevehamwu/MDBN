import InterchangeFormat.IFException;
import medicaldiagnosisgraphs.MedicalDiagnosisGraph;

import java.io.FileNotFoundException;
import java.util.Vector;

public class MedicalDiagnosisBayesianNetwork {
    private static final String bnfile = "solved_alarm.bif";  //BayesianNetwork file
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

    public void train(double tolerance, int max) {
        trainer.readSamples(sfile);
        double[] theta1, theta2;
        theta2 = mdg.getArrayTheta();
        System.out.println("original theta:");
        mdg.printTheta();
        System.out.println("**********************************");
        int i = 0;
        double diff = 1.0;
        while(diff > tolerance && i++ < max){
            theta1 = theta2.clone();
            trainer.E_Step();
            trainer.M_Step();
            System.out.println("theta" + i + ":");
            theta2 = mdg.getArrayTheta();
            mdg.printTheta();
            diff = computeDiff(theta1, theta2);
            System.out.println("**********************************");
            System.out.println(diff);
            System.out.println("**********************************");
        }
        System.out.println("Iteration Times : " + i);
    }

    public double computeDiff(double[] theta1, double[] theta2){
        if(theta1 == null || theta2 == null || theta1.length != theta2.length){
            System.out.println("Wrong!");
            return 0.0;
        }

        double diff = 0.0;
        for (int i = 0; i < theta1.length; i++) {
            diff += Math.abs(theta2[i] - theta1[i]);
        }

        return diff;
    }

    public static void main(String[] args) {
        MedicalDiagnosisBayesianNetwork mdbn = new MedicalDiagnosisBayesianNetwork();
        double tolerance = 0.001;
        int max = 100;
        mdbn.train(tolerance, max);
    }
}
