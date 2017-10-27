import java.io.*;
import java.util.Enumeration;
import java.util.LinkedList;
import java.util.List;

class Sample {
    private final String[] names = {"Hypovolemia", "StrokeVolume", "LVFailure", "LVEDVolume", "PCWP", "CVP",
            "History", "MinVolSet", "VentMach", "Disconnect", "VentTube", "KinkedTube", "Press", "ErrLowOutput",
            "HRBP", "ErrCauter", "HREKG", "HRSat", "BP", "CO", "HR", "TPR", "Anaphylaxis", "InsuffAnesth", "PAP",
            "PulmEmbolus", "Fi02", "Catechol", "Sa02", "Shunt", "PVSat", "MinVol", "ExpC02", "ArtC02", "VentAlv",
            "VentLung", "Intubation"};

    private String[] values = new String[37];

    public String[] getNames(){
        return names;
    }

    public String[] getValues(){
        return values;
    }

    public void setValues(String[] values){
        this.values = values;
    }

    public Sample(){

    }

    public Sample(String[] values){
        this.setValues(values);
    }
}

public class ReadSamples {

    public static void main(String[] args){
        try {
            Sample sample;
            List<Sample> slist = new LinkedList<Sample>();

            FileReader fr = new FileReader("./data/records.dat");
            BufferedReader br = new BufferedReader(fr);
            String str = br.readLine();
            while(str != null){
                sample = new Sample(str.replaceAll("\"", "").split(" "));
                slist.add(sample);
                str = br.readLine();
            }
            for(Sample s:slist){
                String[] names = s.getNames();
                String[] values = s.getValues();
                for (int i = 0; i < names.length; i++) {
                    System.out.println("name: "+ names[i] + " value: " + values[i]);
                }
                System.out.println("************************************");
            }
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
