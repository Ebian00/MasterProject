package heuristic;

import java.util.Arrays;
import java.util.List;

public class JobWithPrio {
	
	@Override
	public String toString() {
		return "JobWithPrio [jobNumber=" + jobNumber + ", jobWeight=" + jobProfit + ", startTime=" + startTime
				+ ", finishTime=" + finishTime + ", jobPrio=" + jobPrio + ", jobsOnMachine="
				+ Arrays.toString(jobsOnMachine) + "]";
	}
	public JobWithPrio() {
		super();
	}
	int jobNumber;
	public JobWithPrio(int jobNumber, int jobProfit, int startTime, int finishTime, int jobPrio, Integer[] jobsOnMachine) {
		super();
		this.jobNumber = jobNumber;
		this.jobProfit = jobProfit;
		this.startTime = startTime;
		this.finishTime = finishTime;
		this.jobPrio = jobPrio;
		this.jobsOnMachine = jobsOnMachine;
	}
	int jobProfit;
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
	public int getJobProfit() {
		return jobProfit;
	}
	public void setJobProfit(int jobWeight) {
		this.jobProfit = jobWeight;
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
