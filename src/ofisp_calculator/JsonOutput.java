package ofisp_calculator;

import java.util.ArrayList;
import java.util.List;

import instanzGenerator.JobInput;

public class JsonOutput {

	int numberOfJobs;
	int numberOfMachines;
	int intervallLenghts;
	double value;
	List<JobOutput> listOfJobs = new ArrayList<JobOutput>();
	
 public JsonOutput() {
		super();
	}

	public JsonOutput(int numberOfJobs, int numberOfMachines, int intervallLenghts, double value,
			List<JobOutput> listOfJobs) {
		super();
		this.numberOfJobs = numberOfJobs;
		this.numberOfMachines = numberOfMachines;
		this.intervallLenghts = intervallLenghts;
		this.value = value;
		this.listOfJobs = listOfJobs;
	}

	public int getNumberOfJobs() {
		return numberOfJobs;
	}

	public void setNumberOfJobs(int numberOfJobs) {
		this.numberOfJobs = numberOfJobs;
	}

	public int getNumberOfMachines() {
		return numberOfMachines;
	}

	public void setNumberOfMachines(int numberOfMachines) {
		this.numberOfMachines = numberOfMachines;
	}

	public int getIntervallLenghts() {
		return intervallLenghts;
	}

	public void setIntervallLenghts(int intervallLenghts) {
		this.intervallLenghts = intervallLenghts;
	}

	public double getValue() {
		return value;
	}

	public void setValue(double value) {
		this.value = value;
	}

	public List<JobOutput> getListOfJobs() {
		return listOfJobs;
	}

	public void setListOfJobs(List<JobOutput> listOfJobs) {
		this.listOfJobs = listOfJobs;
	}
}
