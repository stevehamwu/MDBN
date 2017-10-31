import InterchangeFormat.IFException;
import medicaldiagnosisgraphs.MedicalDiagnosisGraph;
import medicaldiagnosisgraphs.MedicalDiagnosisGraphNode;

import java.io.*;
import java.util.Enumeration;
import java.util.Vector;

public class MedicalDiagnosisBayesianNetwork {
    private static final String bnfile = "alarm_zeros.bif";  //BayesianNetwork file
    private static final String initfile = "init_alarm.bif";    //Initial file
    private static final String sfile = "records.dat"; //Sample file
    private static final String outfile = "alarm_result.bif";   //Output file
    private MedicalDiagnosisGraph mdg;
    private BayesianTrainer trainer;
    private Vector<Sample> slist;

    public MedicalDiagnosisBayesianNetwork() {
        try {
            mdg = new MedicalDiagnosisGraph("./data/" + bnfile);
            initMDG();
            save(initfile);
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

    public void save(String filename){
        try {
            File file = new File("./data/" + filename);
            if(!file.exists())
                file.createNewFile();
            FileOutputStream fos = new FileOutputStream(file);
            PrintStream ps = new PrintStream(fos);
            mdg.save_bif(ps);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void initMDG(){
        for(Enumeration e = mdg.elements(); e.hasMoreElements(); ){
            MedicalDiagnosisGraphNode node = (MedicalDiagnosisGraphNode)(e.nextElement());
            double[] probs = node.get_function_values();
            int totalsize = probs.length;
            int length = node.get_number_values();
            int interval = totalsize/length;
            for (int i = 0; i < interval; i++) {
                double curr = 1.0;
                int j;
                for (j = 0; j < length - 1; j++) {
                    double rd = Math.random()*curr;
                    probs[i + interval*j] = rd;
                    curr -= rd;
                }
                probs[i + interval*j] = curr;
            }
        }
    }

    public static void main(String[] args) {
        MedicalDiagnosisBayesianNetwork mdbn = new MedicalDiagnosisBayesianNetwork();
        double tolerance = 0.00001;
        int max = 100;
        mdbn.train(tolerance, max);
        mdbn.save(outfile);
    }
}
