package ofisp_calculator;

import java.util.ArrayList;
import java.util.List;

import instanzGenerator.JobInput;

public class JsonOutput {

	int numberOfJobs;
	int numberOfMachines;
	int intervallLenghts;
	double objective;
	String calculatedBy;

	String type;
	String description;
	List<JobOutput> listOfJobs = new ArrayList<JobOutput>();
	
 public JsonOutput() {
		super();
	}

	

	public JsonOutput(int numberOfJobs, int numberOfMachines, int intervallLenghts, double objective, String type,
		String description, List<JobOutput> listOfJobs, String calculatedBy) {
	super();
	this.numberOfJobs = numberOfJobs;
	this.numberOfMachines = numberOfMachines;
	this.intervallLenghts = intervallLenghts;
	this.objective = objective;
	this.type = type;
	this.description = description;
	this.listOfJobs = listOfJobs;
	this.calculatedBy = calculatedBy;
}

	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}
	public String getDescription() {
		return description;
	}
	public void setDescription(String description) {
		this.description = description;
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

	public double getObjective() {
		return objective;
	}

	public void setObjective(double value) {
		this.objective = value;
	}

	public List<JobOutput> getListOfJobs() {
		return listOfJobs;
	}

	public void setListOfJobs(List<JobOutput> listOfJobs) {
		this.listOfJobs = listOfJobs;
	}



	public String getCalculatedBy() {
		return calculatedBy;
	}



	public void setCalculatedBy(String calculatedBy) {
		this.calculatedBy = calculatedBy;
	}
}
