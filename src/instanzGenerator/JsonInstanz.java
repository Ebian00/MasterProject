package instanzGenerator;

import java.util.ArrayList;
import java.util.List;

public class JsonInstanz {

	int numberOfJobs;
	int numberOfMachines;
	int intervallLenghts;
	int maxWeight;
	String type;
	int corrolation = 0;
	public JsonInstanz(int numberOfJobs, int numberOfMachines, int intervallLenghts, int maxWeight,
			List<JobInput> listOfJobs, String type, int corrolation) {
		super();
		this.numberOfJobs = numberOfJobs;
		this.numberOfMachines = numberOfMachines;
		this.intervallLenghts = intervallLenghts;
		this.maxWeight = maxWeight;
		this.type = type;
		this.corrolation = corrolation;
		this.listOfJobs = listOfJobs;
	}

	List<JobInput> listOfJobs = new ArrayList<JobInput>();
	
 public JsonInstanz() {
		super();
	}

	public JsonInstanz(int numberOfJobs, int numberOfMachines, int intervallLenghts, int maxWeight,
			List<JobInput> listOfJobs, String type) {
		super();
		this.numberOfJobs = numberOfJobs;
		this.numberOfMachines = numberOfMachines;
		this.intervallLenghts = intervallLenghts;
		this.maxWeight = maxWeight;
		this.listOfJobs = listOfJobs;
		this.type = type;
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

	public List<JobInput> getListOfJobs() {
		return listOfJobs;
	}

	public void setListOfJobs(List<JobInput> listOfJobs) {
		this.listOfJobs = listOfJobs;
	}

	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}

	public int getCorrolation() {
		return corrolation;
	}

	public void setCorrolation(int corrolation) {
		this.corrolation = corrolation;
	}
}
