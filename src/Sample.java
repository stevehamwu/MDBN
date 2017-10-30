import BayesianNetworks.ProbabilityVariable;
import medicaldiagnosisgraphs.MedicalDiagnosisGraph;
import medicaldiagnosisgraphs.MedicalDiagnosisGraphNode;

import java.util.Vector;

class Sample {
    private final String[] names = {"Hypovolemia", "StrokeVolume", "LVFailure", "LVEDVolume", "PCWP", "CVP",
            "History", "MinVolSet", "VentMach", "Disconnect", "VentTube", "KinkedTube", "Press", "ErrLowOutput",
            "HRBP", "ErrCauter", "HREKG", "HRSat", "BP", "CO", "HR", "TPR", "Anaphylaxis", "InsuffAnesth", "PAP",
            "PulmEmbolus", "FiO2", "Catechol", "SaO2", "Shunt", "PVSat", "MinVol", "ExpCO2", "ArtCO2", "VentAlv",
            "VentLung", "Intubation"};

    private String[] values = new String[37];
    private int[] num_values = new int[37];
    private Sample[] comsamples;
    private int incomplete = -1;
    public int length = names.length;

    private double weight = 1.0;

    public double getWeight(){
        return weight;
    }

    public void setWeight(double w){
        weight = w;
    }

    public String getName(int index){
        return names[index];
    }

    public String getValue(int index){
        return values[index];
    }

    public String getValue(String name){
        for (int i = 0; i < length; i++) {
            if(names[i].equals(name)){
                return values[i];
            }
        }
        return null;
    }

    public String[] getNames(){
        return names;
    }

    public String[] getValues(){
        return values;
    }

    public void setValues(String[] values){
        this.values = values;
    }

    public Sample(String[] values){
        setValues(values);
    }

    public boolean isComplete(){
        for(int i = 0; i < length; i++){
            if(values[i].equals("?")){
                incomplete = i;
                return false;
            }
        }
        return true;
    }

    public void completement(MedicalDiagnosisGraph mdg){
        Vector<MedicalDiagnosisGraphNode> nodes = mdg.get_nodes();
        MedicalDiagnosisGraphNode node = nodes.elementAt(incomplete);
        int num = node.get_number_values();
        comsamples = new Sample[num];
        double[] prob = new double[num];
        double sum = 0.0;
        for (int i = 0; i < num; i++) {
            String[] tvalues = values.clone();
            tvalues[incomplete] = node.get_values()[i];
            comsamples[i] = new Sample(tvalues);
            prob[i] = comsamples[i].getProb(mdg);
            sum += prob[i];
        }

        //Normalization
        for (int i = 0; i < num; i++) {
            prob[i] /= sum;
            comsamples[i].setWeight(prob[i]);
        }
    }

    //Compute P(Xi|D, theta0)
    public double getProb(MedicalDiagnosisGraph mdg){
        double prob = 1.0;
        Vector<MedicalDiagnosisGraphNode> nodes = mdg.get_nodes();
        for (int i = 0; i < length; i++) {
            MedicalDiagnosisGraphNode node = nodes.elementAt(i);
            ProbabilityVariable[] pf = (ProbabilityVariable[]) node.get_Prob().get_variables();
            String[][] s = new String[pf.length][2];

            for (int j = 0; j < pf.length; j++) {
                String name = pf[j].get_name();
                s[j][0] = new String(name);
                s[j][1] = new String(getValue(name));
            }
            prob *= node.get_function_value(s);
        }
        return prob;
    }

    public Sample[] getComsamples() {
        return comsamples;
    }

    public void print(){
        for (int i = 0; i < names.length; i++) {
            System.out.print(names[i] + ": " + values[i] + "\t");
        }
        System.out.println("weight: " + weight);
    }

}
