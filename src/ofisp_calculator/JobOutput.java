package ofisp_calculator;

import java.util.List;

public class JobOutput {
	
	public JobOutput() {
		super();
		// TODO Auto-generated constructor stub
	}
	int jobNumber;
	int jobProfit;
	int startTime;
	int finishTime;
	int machine;
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
	public JobOutput(int jobNumber, int jobWeight, int startTime, int finishTime, int jobsOnMachine) {
		super();
		this.jobNumber = jobNumber;
		this.jobProfit = jobWeight;
		this.startTime = startTime;
		this.finishTime = finishTime;
		this.machine = jobsOnMachine;
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
	public int getMachine() {
		return machine;
	}
	public void setMachine(int jobsOnMachine) {
		this.machine = jobsOnMachine;
	}

}
