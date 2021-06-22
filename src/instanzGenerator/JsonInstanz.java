package instanzGenerator;

import java.util.ArrayList;
import java.util.List;

public class JsonInstanz {

	int numberOfJobs;
	int numberOfMachines;
	int intervalLenghts;
	String type;
	String description;
	List<JobInput> listOfJobs = new ArrayList<JobInput>();
	
	public JsonInstanz(int numberOfJobs, int numberOfMachines, int intervalLenghts, String type, String description,
			List<JobInput> listOfJobs) {
		super();
		this.numberOfJobs = numberOfJobs;
		this.numberOfMachines = numberOfMachines;
		this.intervalLenghts = intervalLenghts;
		this.type = type;
		this.description = description;
		this.listOfJobs = listOfJobs;
	}






	
	
 public JsonInstanz() {
		super();
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

	public void setIntervallLenghts(int intervallLenghts) {
		this.intervalLenghts = intervallLenghts;
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

	
	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}
}
