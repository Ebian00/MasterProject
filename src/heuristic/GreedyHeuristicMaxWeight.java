package heuristic;

import java.io.File;
import java.io.IOException;
import java.nio.file.Paths;
import java.time.Duration;
import java.time.Instant;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Random;
import java.util.Scanner;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import com.fasterxml.jackson.core.JsonGenerationException;
import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.core.util.DefaultPrettyPrinter;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ObjectWriter;

import gurobi.GRB;
import gurobi.GRBException;
import gurobi.GRBVar;
import instanzGenerator.JobInput;
import instanzGenerator.JsonInstanz;
import ofisp_calculator.JobOutput;
import ofisp_calculator.JsonOutput;

public class GreedyHeuristicMaxWeight {

	public static void calculateGreedy(String instancePath, String outputPath, String outputName,boolean calculateMashineConflict,
			boolean calculatePrioWithJobOnMashine, boolean calculateMaxJobConflict ) {
		Scanner scanner = new Scanner(System.in);
		System.out.println("Please enter the path to the test instance!");
		String filePaht = "./OFISPInstances/OFISPInstance5000IntervalWeight.json";
		JsonInstanzWithPrio jsonInstanz = null;
		ObjectMapper mapper = new ObjectMapper();
		try {
			jsonInstanz = mapper.readValue(new File(filePaht), JsonInstanzWithPrio.class);
		} catch (JsonParseException e1) {
			e1.printStackTrace();
		} catch (JsonMappingException e1) {
			e1.printStackTrace();
		} catch (IOException e1) {
			e1.printStackTrace();
		}
		//this variable contains the jobs which are chosen by the heuristic
		List<JobOutput> listOfJobsOutput = new ArrayList<JobOutput>();
		Instant start = Instant.now();
		int numberOfJobs = jsonInstanz.getNumberOfJobs();
		 int numberOfMachines= jsonInstanz.getNumberOfMachines();
		 int numberOfInterval= jsonInstanz.getIntervallLenghts();
		 int Objective =0;
		List<JobWithPrio> listOfJobs = jsonInstanz.getListOfJobs();
		for (JobWithPrio job : listOfJobs) {
			job.setJobPrio(job.getJobWeight());
		}
		Map<Integer, Integer> machineConflict = null;
		if(calculateMashineConflict) {
			 machineConflict = calculateMashineConflict(listOfJobs, jsonInstanz.getNumberOfMachines());
			 machineConflict = machineConflict.entrySet().stream()
						.sorted(Map.Entry.comparingByValue(Comparator.reverseOrder()))
						.collect(Collectors.toMap(Entry::getKey, Entry::getValue, (e1, e2) -> e1, LinkedHashMap::new));
		}
		
		if(calculatePrioWithJobOnMashine) {
			jsonInstanz.setListOfJobs(calculatePrioWithJobOnMashine(jsonInstanz.getListOfJobs(), jsonInstanz.getNumberOfMachines()));
			listOfJobs.sort(Comparator.comparing(JobWithPrio::getJobPrio).reversed());
		}
		if(calculateMaxJobConflict) {
			jsonInstanz.setListOfJobs(calculateMaxJobConflict(jsonInstanz.getNumberOfJobs(), jsonInstanz.numberOfMachines,
					jsonInstanz.getIntervallLenghts(), jsonInstanz.getListOfJobs()));
			listOfJobs.sort(Comparator.comparing(JobWithPrio::getJobPrio).reversed());
		}
		if(calculateMashineConflict) {
			Objective = calculateInstanceWithMachineConflict(jsonInstanz.getNumberOfJobs(), jsonInstanz.numberOfMachines,
					jsonInstanz.getIntervallLenghts(), listOfJobs, machineConflict,listOfJobsOutput);
		}
		else {
			Objective = calculateInstance(jsonInstanz.getNumberOfJobs(), jsonInstanz.numberOfMachines,
					jsonInstanz.getIntervallLenghts(),listOfJobs,listOfJobsOutput);
		}

		
		
		
		
	
		JsonOutput jsonOutput = new JsonOutput(numberOfJobs, numberOfMachines, numberOfInterval,
				Objective, listOfJobsOutput);
		ObjectWriter writer = mapper.writer(new DefaultPrettyPrinter());
		try {
			writer.writeValue(Paths.get(outputPath+outputName+ ".json").toFile(), jsonOutput);
		} catch (JsonGenerationException e) {
			e.printStackTrace();
		} catch (JsonMappingException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}

		

	Instant finish = Instant.now();
	System.out.println(Duration.between(start, finish).toMillis()); // in millis
	}

	public static Map<Integer, Integer> calculateMashineConflict(List<JobWithPrio> jobs, int numberOfMashines) {
		Map<Integer, Integer> machineConflict = new HashMap<Integer, Integer>();
		for (int i = 1; i <= numberOfMashines; ++i) {
			machineConflict.put(i, 0);
		}
		for (JobWithPrio job : jobs) {
			for (Integer machine : job.getJobsOnMachine()) {
				machineConflict.put(machine, machineConflict.get(machine) + 1);
			}
		}

		return machineConflict;

	}

	public static List<JobWithPrio> calculatePrioWithJobOnMashine(List<JobWithPrio> jobs, int numberOfMaschines) {
		for (JobWithPrio job : jobs) {
			int jobsOnMaschines = numberOfMaschines / job.getJobsOnMachine().length;
			job.setJobPrio(job.getJobPrio() + jobsOnMaschines);
		}
		return jobs;

	}

	public static List<JobWithPrio> calculateMaxJobConflict(int numberOfJobs, int numberOfMashines, int intevallLength,
			List<JobWithPrio> listOfJobs) {
		Map<Integer, Map<Integer, ArrayList<Integer>>> jobOverlaping = new LinkedHashMap<Integer, Map<Integer, ArrayList<Integer>>>();

		for (int i = 1; i <= numberOfMashines; ++i) {
			jobOverlaping.put(i, new LinkedHashMap<Integer, ArrayList<Integer>>());
			for (int j = 0; j <= intevallLength; ++j) {
				jobOverlaping.get(i).put(j, new ArrayList<Integer>());
			}
		}

		for (int i = 0; i < numberOfJobs; ++i) {
			JobWithPrio job = listOfJobs.get(i);
			Integer[] temp = job.getJobsOnMachine();
			for (Integer jobsOnMachine : temp) {
				for (int j = job.getStartTime(); j < job.getFinishTime(); ++j) {
					jobOverlaping.get(jobsOnMachine).get(j).add(job.getJobNumber());
				}
			}
		}

		for (int i = 1; i <= numberOfMashines; ++i) {
			Map<Integer, ArrayList<Integer>> mashinesWithIntervall = jobOverlaping.get(i);
			ArrayList<Integer> temp = new ArrayList<Integer>();
			ArrayList<Integer> calculated = new ArrayList<Integer>();
			for (int j = 0; j <= intevallLength; ++j) {
				ArrayList<Integer> intervall = mashinesWithIntervall.get(j);
				temp.addAll(intervall);
				temp.removeAll(calculated);
				if (temp.size() > 0) {
					for (int k = 0; k < temp.size(); ++k) {
						JobWithPrio jwp = listOfJobs.get(temp.get(k) - 1);
						jwp.setJobPrio(jwp.getJobPrio() + intervall.size());
					}
					calculated.clear();
					calculated.addAll(intervall);
					calculated.removeAll(temp);
					for (int k = 0; k < calculated.size(); ++k) {
						JobWithPrio jwp = listOfJobs.get(calculated.get(k) - 1);
						jwp.setJobPrio(jwp.getJobPrio() + temp.size());
					}
					calculated.clear();
					calculated.addAll(intervall);
				}

				temp.clear();
			}
		}
		return listOfJobs;
	}
	
	public static int calculateInstance(int numberOfJobs, int numberOfMashines, int intevallLength,List<JobWithPrio> listOfJobs,List<JobOutput> listOfJobsOutput) {
		Map<Integer, Map<Integer, Integer>> jobOverlaping = new LinkedHashMap<Integer, Map<Integer, Integer>>();
		Random random = new Random();
		int objective = 0;
		for (int i = 1; i <= numberOfMashines; ++i) {
			jobOverlaping.put(i, new LinkedHashMap<Integer, Integer>());
			for (int j = 0; j <= intevallLength; ++j) {
				jobOverlaping.get(i).put(j, 0);
			}
		}
		for (JobWithPrio job : listOfJobs) {
			List<Integer> machines = Arrays.asList(job.getJobsOnMachine());
			Collections.shuffle(machines);
			// System.out.println(job);
			boolean notOccupied = true;
			boolean jobAssinged = false;
			int numberOfMachines = job.getJobsOnMachine().length;
			int machineNumber = 0;
			for (int n = 0; n <numberOfMachines && !jobAssinged; ++n) {
				notOccupied = true;
				machineNumber = machines.get(n);
				for (int i = job.getStartTime(); i < job.getFinishTime() && notOccupied; ++i) {
					for (int j = job.getStartTime(); j < job.getFinishTime() && notOccupied; ++j) {
						notOccupied = jobOverlaping.get(machineNumber).get(j) == 0 ? true : false;
					}
					if (notOccupied) {
						for (int j = job.getStartTime(); j < job.getFinishTime(); ++j) {
							jobOverlaping.get(machineNumber).put(j, 1);
							jobAssinged = true;
						}
					}
				}
			}
			if (jobAssinged) {
				JobOutput jobOutput = new JobOutput();
				jobOutput.setJobNumber(job.getJobNumber());
				jobOutput.setFinishTime(job.getFinishTime());
				jobOutput.setStartTime(job.getStartTime());
				jobOutput.setJobNumber(job.getJobNumber());
				jobOutput.setJobWeight(job.getJobWeight());
				jobOutput.setMachine(machineNumber);
				listOfJobsOutput.add(jobOutput);
				objective += job.getJobWeight();
			}
		}
		return objective;
	}

	public static int calculateInstanceWithMachineConflict(int numberOfJobs, int numberOfMashines, int intevallLength,List<JobWithPrio> listOfJobs, Map<Integer, Integer> machineConflict, List<JobOutput> listOfJobsOutput) {
		Map<Integer, Map<Integer, Integer>> jobOverlaping = new LinkedHashMap<Integer, Map<Integer, Integer>>();
		int objective = 0;
		for (int i = 1; i <= numberOfMashines; ++i) {
			jobOverlaping.put(i, new LinkedHashMap<Integer, Integer>());
			for (int j = 0; j <= intevallLength; ++j) {
				jobOverlaping.get(i).put(j, 0);
			}
		}
		for (JobWithPrio job : listOfJobs) {
			int currentMachine = 0;
			List<Integer> machines = Arrays.asList(job.getJobsOnMachine());
			Map<Integer, Integer> sorted = new HashMap<Integer, Integer>();
			for (int i = 0; i < machines.size(); ++i) {
				sorted.put(machines.get(i), machineConflict.get(machines.get(i)));
			}
			sorted = sorted.entrySet().stream().sorted(Map.Entry.comparingByValue(Comparator.reverseOrder()))
					.collect(Collectors.toMap(Entry::getKey, Entry::getValue, (e1, e2) -> e1, LinkedHashMap::new));
			List<Integer> machineSorted = new ArrayList<Integer>();
			sorted.forEach((key, value) -> {
				machineSorted.add(key);
			});
			boolean occupied = true;
			boolean jobAssinged = false;
			for (int n = 0; n < machineSorted.size() && !jobAssinged; ++n) {
				occupied = true;
				currentMachine = machineSorted.get(n);
				for (int i = job.getStartTime(); i < job.getFinishTime() && occupied; ++i) {
					for (int j = job.getStartTime(); j < job.getFinishTime() && occupied; ++j) {
						occupied = jobOverlaping.get(currentMachine).get(j) == 0 ? true : false;
					}
					if (occupied) {
						for (int j = job.getStartTime(); j < job.getFinishTime(); ++j) {
							jobOverlaping.get(currentMachine).put(j, 1);
						}
						jobAssinged = true;
					}
				}
			}
			if (jobAssinged) {
				JobOutput jobOutput = new JobOutput();
				jobOutput.setJobNumber(job.getJobNumber());
				jobOutput.setFinishTime(job.getFinishTime());
				jobOutput.setStartTime(job.getStartTime());
				jobOutput.setJobNumber(job.getJobNumber());
				jobOutput.setJobWeight(job.getJobWeight());
				jobOutput.setMachine(currentMachine);
				listOfJobsOutput.add(jobOutput);
				objective += job.getJobWeight();
				objective += job.getJobWeight();
				for (Integer m : machineSorted) {
					machineConflict.put(m, sorted.get(m) - 1);
				}
			}
		}
		return objective;
	}
}