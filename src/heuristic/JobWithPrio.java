package heuristic;

import java.util.Arrays;
import java.util.List;

public class JobWithPrio {
	
	@Override
	public String toString() {
		return "JobWithPrio [jobNumber=" + jobNumber + ", jobWeight=" + jobWeight + ", startTime=" + startTime
				+ ", finishTime=" + finishTime + ", jobPrio=" + jobPrio + ", jobsOnMachine="
				+ Arrays.toString(jobsOnMachine) + "]";
	}
	public JobWithPrio() {
		super();
	}
	int jobNumber;
	public JobWithPrio(int jobNumber, int jobWeight, int startTime, int finishTime, int jobPrio, Integer[] jobsOnMachine) {
		super();
		this.jobNumber = jobNumber;
		this.jobWeight = jobWeight;
		this.startTime = startTime;
		this.finishTime = finishTime;
		this.jobPrio = jobPrio;
		this.jobsOnMachine = jobsOnMachine;
	}
	int jobWeight;
	int startTime;
	int finishTime;
	int jobPrio;
	public int getJobPrio() {
		return jobPrio;
	}
	public void setJobPrio(int jobPrio) {
		this.jobPrio = jobPrio;
	}
	Integer[] jobsOnMachine;

	
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

	public void setStartTime(int startTime) {
		this.startTime = startTime;
	}
	public int getFinishTime() {
		return finishTime;
	}
	public void setFinishTime(int finishTime) {
		this.finishTime = finishTime;
	}
	public Integer[] getJobsOnMachine() {
		return jobsOnMachine;
	}
	public void setJobsOnMachine(Integer[] jobsOnMachine) {
		this.jobsOnMachine = jobsOnMachine;
	}

}
