package medicaldiagnosisgraphs;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.util.*;

import BayesianNetworks.*;
import InterchangeFormat.IFException;

public class MedicalDiagonosisGraph {
	BayesNet bn;

	Vector nodes = new Vector();

	private final String defaultBayesNetName = "MedicalDiagonosisNetwork";

	public final static int MARGINAL_POSTERIOR = 1;
	public final static int EXPECTATION = 2;
	public final static int EXPLANATION = 3;
	public final static int FULL_EXPLANATION = 4;
	public final static int SENSITIVITY_ANALYSIS = 5;

	public MedicalDiagonosisGraph(String filename) throws FileNotFoundException, IFException {
		bn = new BayesNet(new java.io.DataInputStream(new FileInputStream(filename)));

	}
}