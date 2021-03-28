package heuristic;

import java.util.ArrayList;
import java.util.List;

public class JsonInstanzWithPrio {

	int numberOfJobs;
	int numberOfMachines;
	int intervallLenghts;
	int maxWeight;
	List<JobWithPrio> listOfJobs = new ArrayList<JobWithPrio>();
	
 public JsonInstanzWithPrio() {
		super();
	}

	public JsonInstanzWithPrio(int numberOfJobs, int numberOfMachines, int intervallLenghts, int maxWeight,
			List<JobWithPrio> listOfJobs) {
		super();
		this.numberOfJobs = numberOfJobs;
		this.numberOfMachines = numberOfMachines;
		this.intervallLenghts = intervallLenghts;
		this.maxWeight = maxWeight;
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

	public int getMaxWeight() {
		return maxWeight;
	}

	public void setMaxWeight(int maxWeight) {
		this.maxWeight = maxWeight;
	}

	public List<JobWithPrio> getListOfJobs() {
		return listOfJobs;
	}

	public void setListOfJobs(List<JobWithPrio> listOfJobs) {
		this.listOfJobs = listOfJobs;
	}
}
