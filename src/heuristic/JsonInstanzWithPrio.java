package heuristic;

import java.util.ArrayList;
import java.util.List;

public class JsonInstanzWithPrio {

	int numberOfJobs;
	int numberOfMachines;
	int intervalLenghts;
	String type;
	String description;
	List<JobWithPrio> listOfJobs = new ArrayList<JobWithPrio>();
	
 public JsonInstanzWithPrio() {
		super();
	}



	public JsonInstanzWithPrio(int numberOfJobs, int numberOfMachines, int intervallLenghts, String type,
		String description, List<JobWithPrio> listOfJobs) {
	super();
	this.numberOfJobs = numberOfJobs;
	this.numberOfMachines = numberOfMachines;
	this.intervalLenghts = intervallLenghts;
	this.type = type;
	this.description = description;
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

	public int getIntervalLenghts() {
		return intervalLenghts;
	}

	public void setIntervalLenghts(int intervallLenghts) {
		this.intervalLenghts = intervallLenghts;
	}

	

	public List<JobWithPrio> getListOfJobs() {
		return listOfJobs;
	}

	public void setListOfJobs(List<JobWithPrio> listOfJobs) {
		this.listOfJobs = listOfJobs;
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
}
