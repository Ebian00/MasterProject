package instanzGenerator;

import java.util.Arrays;
import java.util.List;

public class JobInput {
	
	public JobInput() {
		super();
		// TODO Auto-generated constructor stub
	}
	int jobNumber;
	int jobWeight;
	int startTime;
	@Override
	public String toString() {
		return "JobInput [jobNumber=" + jobNumber + ", jobWeight=" + jobWeight + ", startTime=" + startTime
				+ ", finishTime=" + finishTime + ", jobsOnMachine=" + Arrays.toString(jobsOnMachine) + "]";
	}
	int finishTime;
	int[] jobsOnMachine;
	public int getJobNumber() {
		return jobNumber;
	}
	public void setJobNumber(int jobNumber) {
		this.jobNumber = jobNumber;
	}
	public int getJobWeight() {
		return jobWeight;
	}
	public void setJobWeight(int jobWeight) {
		this.jobWeight = jobWeight;
	}
	public int getStartTime() {
		return startTime;
	}
	public JobInput(int jobNumber, int jobWeight, int startTime, int finishTime, int[] jobsOnMachine) {
		super();
		this.jobNumber = jobNumber;
		this.jobWeight = jobWeight;
		this.startTime = startTime;
		this.finishTime = finishTime;
		this.jobsOnMachine = jobsOnMachine;
	}
	public void setStartTime(int startTime) {
		this.startTime = startTime;
	}
	public int getFinishTime() {
		return finishTime;
	}
	public void setFinishTime(int finishTime) {
		this.finishTime = finishTime;
	}
	public int[] getJobsOnMachine() {
		return jobsOnMachine;
	}
	public void setJobsOnMachine(int[] jobsOnMachine) {
		this.jobsOnMachine = jobsOnMachine;
	}

}
