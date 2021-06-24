package main;

import heuristic.Greedy;
import heuristic.LPHeuristic;
import instanzGenerator.IGRandomProfit;
import instanzGenerator.IGIntervalProfit;
import instanzGenerator.IGIntervalProfitPrecent;
import instanzGenerator.IGIntervalProfitCor;
import ofisp_calculator.OFISP_Calculator;
import picocli.CommandLine;
import picocli.CommandLine.Command;
import picocli.CommandLine.Option;

@Command(name = "OFISP", mixinStandardHelpOptions = true, version = "OFISP 1.0", description = "This program generates and calculates instances of the "
		+ "Operational Fixed Interval Scheduling Problems")
public class OFISP implements Runnable {

	@Option(names = { "-slph",
			"--solveLPHeuristic" }, defaultValue = "false", description = "calculates an instance with the lp heristic")
	boolean lpHeuristic;
	@Option(names = { "-s",
			"--solveLP" }, defaultValue = "false", description = "calculates an instance with the Gurobi")
	boolean lp;
	@Option(names = { "-gh",
			"--greedy" }, defaultValue = "false", description = "calculates an instance with the greedy heristic")
	boolean greedy;
	@Option(names = { "-cmc",
			"--calculateMachineConflict" }, defaultValue = "false", description = "defines whether a greedy should use the MachineConflict contraint")
	boolean calculateMachineConflict;
	@Option(names = { "-cjm",
			"--calculatePrioWithJobOnMachine" }, defaultValue = "false", description = "defines whether a greedy should use the calculatePrioWithJobOnMachine contraint")
	boolean calculatePrioWithJobOnMachine;
	@Option(names = { "-cjc",
			"--calculateMaxJobConflict" }, defaultValue = "false", description = "defines whether a greedy should use the calculateMaxJobConflict contraint")
	boolean calculateMaxJobConflict;
	@Option(names = { "-fn", "--fileName" }, description = "name of the file to be generated or to be read")
	String fileName;
	@Option(names = { "-ipf", "--inputFile" }, description = "path of the instance to be to be read")
	String pathOfTheInstanceToBeRead;
	@Option(names = { "-opp", "--outputPath" }, description = "path of the instance to be to be read")
	String pathOfTheInstanceToBesSavedTo;
	@Option(names = { "-g", "--generateInstance" }, description = "generate an OFISP instance")
	boolean generate;
	@Option(names = { "-t",
			"--instanceType" }, description = "instance type to be generated, the options are rp for random instance"
					+ " ip for the interval profit and ipc for interval profit with correlation and ipp for interval profit prozent")
	String instanceType;
	@Option(names = { "-nm", "--numberOfMachines" },defaultValue = "0", description = "file to be generated or to be read")
	int numberOfMachines;
	@Option(names = { "-ni", "--numberOfIntervals" },defaultValue = "0", description = "number of machine to be generated")
	int numberOfIntervals;
	@Option(names = { "-nj", "--numberOfJobs" },defaultValue = "0", description = "number of intervals to be generated")
	int numberOfJobs;
	@Option(names = { "-nmm", "--jobOnMaxMachines" },defaultValue = "0", description = "number of jobs to be generated")
	int jobOnMaxMachines;
	@Option(names = { "-cor","--correlation" }, defaultValue = "0", description = " defines the correlation for a job weight")
	int correlation;
	@Option(names = { "-mw", "--maxProfit" }, defaultValue = "0",description = "max weight which a job can have ")
	int maxProfit;
	@Option(names = { "-p", "--precentage" }, defaultValue = "0",description = "precentage that a Job is assigned to a machine, it should be an integer between 1 and 100")
	int precentage;
	@Option(names = { "-grp",
			"--generateRandomProfit" }, arity = "7", description = "this is the command to generate an instance with random weight. 7 parameter in the following sequence are needed"
					+ "1. number of jobs, 2. number of machines, 3. number of intervals, 4. max weight distribution, 5. max jobs on a machine, 6. the file name to be generated, 7. the path where the file should be generated ")
	String[] configurationsRandomProfit;
	@Option(names = { "-gip",
			"--generateIntervalProfit" }, arity = "6", description = "this is the command to generate an instance with random weight. 6 parameter in the following sequence are needed"
					+ "1. number of jobs, 2. number of machines, 3. number of intervals, 4.  max jobs on a machine, "
					+ "5. the file name to be generated, 6. the path where the file should be generated ")
	String[] configurationsIntervalProfit;
	@Option(names = { "-gipc",
			"--generateIntervalProfitCorrelation" }, arity = "7", description = "this is the command to generate an instance with random weight. 7 parameter in the following sequence are needed"
					+ "1. number of jobs, 2. number of machines, 3. number of intervals, 4. max  jobs on a machine, 5. correlation, 6. the file name to be generated, 7. the path where the file should be generated ")
	String[] configurationsIntervalProfitCorrelation;
	@Option(names = { "-gipp",
			"--generateIntervalProfitPrecent" }, arity = "6", description = "this is the command to generate an instance with random weight. 6 parameter in the following sequence are needed"
					+ "1. number of jobs, 2. number of machines, 3. number of intervals, 4. precentage of assigning a jobs on a machine, 5. the file name to be generated, 6. the path where the file should be generated ")
	String[] configurationsIntervalProfitPrecentage;
	@Option(names = { "-sc",
			"--solveCompact" }, arity = "3", description = "this is the command to calculate an OFISP instance. 3 parameter in the following sequence are needed"
					+ "1. path of the instance file, 2. path of the output file, 3. name of the file")
	String[] calculateOFISPInstance;
	@Option(names = { "-slphc",
			"--solveLPHeuristicCompact" }, arity = "3", description = "this is the command to calculate solve an instance with"
					+ "a lp heuristic. 3 parameter in the following sequence are needed"
					+ "1. path of the instance file, 2. path of the output file, 3. name of the output file")
	String[] lpHeuristicCompact;
	@Option(names = { "-sghc",
			"--solveGreedyHeuristicCompact" }, arity = "6", description = "this is the command to calculate solve an instance with"
					+ "the greedy heuristic.6 parameter in the following sequence are needed, for the last three parameter you have to choose 0 or 1. if you want the constraint to be calculated take 1 else 0"
					+ "1. path of the instance file, 2. path of the output file, 3. name of the output file, 4. whether to calculate the machine conflict constraint, 5. whether to calculate the number of jobs on a machine constraint, "
					+ "6. whether to calculate the max job conflict contraint ")
	String[] greedyHeuristicCompact;

	boolean usageHelpRequested;

	public static void main(String[] args) {
		int exitCode = new CommandLine(new OFISP()).execute(args);
		System.exit(exitCode);
	}

	@Override
	public void run() {

		if (configurationsRandomProfit != null) {

			int numberOfJobs = 0;
			int numberOfMachines = 0;
			int numberOfIntevals = 0;
			int maxProfitDistribution = 0;
			int jobOnMaxMachine = 0;
			String fileName = "";
			String outputPath = "";
			try {
				numberOfJobs = Integer.parseInt(configurationsRandomProfit[0]);
			} catch (Exception e) {
				System.out
						.println("Error: could not convert the number of jobs, please give a positive integer number");
				return;
			}
			try {
				numberOfMachines = Integer.parseInt(configurationsRandomProfit[1]);
			} catch (Exception e) {
				System.out.println(
						"Error: could not convert the number of machines, please give a positive integer number");
				return;
			}
			try {
				numberOfIntevals = Integer.parseInt(configurationsRandomProfit[2]);
			} catch (Exception e) {
				System.out.println(
						"Error: could not convert the number of Intervals, please give a positive integer number");
				return;
			}
			try {
				maxProfitDistribution = Integer.parseInt(configurationsRandomProfit[3]);
			} catch (Exception e) {
				System.out.println(
						"Error: could not convert the max weight distribution, please give a positive integer number");
				return;
			}
			try {
				jobOnMaxMachine = Integer.parseInt(configurationsRandomProfit[4]);
			} catch (Exception e) {
				System.out.println(
						"Error: could not convert the number of machines for a job, please give a positive integer number");
				return;
			}
			try {
				fileName = configurationsRandomProfit[5].toString();
			} catch (Exception e) {
				System.out.println("Error: could not parse the filename, please give a filename as string");
				return;
			}
			try {
				outputPath = configurationsRandomProfit[6].toString();
			} catch (Exception e) {
				System.out.println("Error: could not parse the outputPath, please give a valid path");
				return;
			}
			IGRandomProfit.generateInstanceWithRandomProfit(numberOfJobs, numberOfMachines,
					numberOfIntevals, maxProfitDistribution, jobOnMaxMachine, fileName, outputPath);
		} else if (configurationsIntervalProfit != null) {
			int numberOfJobs = 0;
			int numberOfMachines = 0;
			int numberOfIntevals = 0;
			int jobOnMaxMachine = 0;
			String fileName = "";
			String outputPath = "";
			try {
				numberOfJobs = Integer.parseInt(configurationsIntervalProfit[0]);
			} catch (Exception e) {
				System.out
						.println("Error: could not convert the number of jobs, please give a positive integer number");
				return;
			}
			try {
				numberOfMachines = Integer.parseInt(configurationsIntervalProfit[1]);
			} catch (Exception e) {
				System.out.println(
						"Error: could not convert the number of machines, please give a positive integer number");
				return;
			}
			try {
				numberOfIntevals = Integer.parseInt(configurationsIntervalProfit[2]);
			} catch (Exception e) {
				System.out.println(
						"Error: could not convert the number of Intervals, please give a positive integer number");
				return;
			}

			try {
				jobOnMaxMachine = Integer.parseInt(configurationsIntervalProfit[3]);
			} catch (Exception e) {
				System.out.println(
						"Error: could not convert the number of machines for a job, please give a positive integer number");
				return;
			}
			try {
				fileName = configurationsIntervalProfit[4].toString();
			} catch (Exception e) {
				System.out.println("Error: could not parse the filename, please give a filename as string");
				return;
			}
			try {
				outputPath = configurationsIntervalProfit[5].toString();
			} catch (Exception e) {
				System.out.println("Error: could not parse the outputPath, please give a valid path");
				return;
			}
			IGIntervalProfit.generateInstanceWithIntervalProfit(numberOfJobs, numberOfMachines,
					numberOfIntevals, jobOnMaxMachine, fileName, outputPath);

		} else if (configurationsIntervalProfitCorrelation != null) {

			int numberOfJobs = 0;
			int numberOfMachines = 0;
			int numberOfIntevals = 0;
			int jobOnMaxMachine = 0;
			int correlation = 0;
			String fileName = "";
			String outputPath = "";
			try {
				numberOfJobs = Integer.parseInt(configurationsIntervalProfitCorrelation[0]);
			} catch (Exception e) {
				System.out
						.println("Error: could not convert the number of jobs, please give a positive integer number");
				return;
			}
			try {
				numberOfMachines = Integer.parseInt(configurationsIntervalProfitCorrelation[1]);
			} catch (Exception e) {
				System.out.println(
						"Error: could not convert the number of machines, please give a positive integer number");
				return;
			}
			try {
				numberOfIntevals = Integer.parseInt(configurationsIntervalProfitCorrelation[2]);
			} catch (Exception e) {
				System.out.println(
						"Error: could not convert the number of Intervals, please give a positive integer number");
				return;
			}

			try {
				jobOnMaxMachine = Integer.parseInt(configurationsIntervalProfitCorrelation[3]);
			} catch (Exception e) {
				System.out.println(
						"Error: could not convert the number of machines for a job, please give a positive integer number");
				return;
			}
			try {
				correlation = Integer.parseInt(configurationsIntervalProfitCorrelation[4]);
			} catch (Exception e) {
				System.out.println("Error: could not convert the correlation, please give a positive integer number");
				return;
			}
			try {
				fileName = configurationsIntervalProfitCorrelation[5].toString();
			} catch (Exception e) {
				System.out.println("Error: could not parse the filename, please give a filename as string");
				return;
			}
			try {
				outputPath = configurationsIntervalProfitCorrelation[6].toString();
			} catch (Exception e) {
				System.out.println("Error: could not parse the outputPath, please give a valid path");
				return;
			}
			IGIntervalProfitCor.generateInstanceIntervalCorrelationProfit(numberOfJobs,
					numberOfMachines, numberOfIntevals, jobOnMaxMachine, correlation, fileName, outputPath);

		} 
		else if (configurationsIntervalProfitPrecentage != null) {

			int numberOfJobs = 0;
			int numberOfMachines = 0;
			int numberOfIntevals = 0;
			
			int percentage = 0;
			String fileName = "";
			String outputPath = "";
			try {
				numberOfJobs = Integer.parseInt(configurationsIntervalProfitPrecentage[0]);
			} catch (Exception e) {
				System.out
						.println("Error: could not convert the number of jobs, please give a positive integer number");
				return;
			}
			try {
				numberOfMachines = Integer.parseInt(configurationsIntervalProfitPrecentage[1]);
			} catch (Exception e) {
				System.out.println(
						"Error: could not convert the number of machines, please give a positive integer number");
				return;
			}
			try {
				numberOfIntevals = Integer.parseInt(configurationsIntervalProfitPrecentage[2]);
			} catch (Exception e) {
				System.out.println(
						"Error: could not convert the number of Intervals, please give a positive integer number");
				return;
			}

			try {
				percentage = Integer.parseInt(configurationsIntervalProfitPrecentage[3]);
			} catch (Exception e) {
				System.out.println("Error: could not convert the correlation, please give a positive integer number");
				return;
			}
			try {
				fileName = configurationsIntervalProfitPrecentage[4].toString();
			} catch (Exception e) {
				System.out.println("Error: could not parse the filename, please give a filename as string");
				return;
			}
			try {
				outputPath = configurationsIntervalProfitPrecentage[5].toString();
			} catch (Exception e) {
				System.out.println("Error: could not parse the outputPath, please give a valid path");
				return;
			}
			IGIntervalProfitPrecent.generateInstanceIntervalProfitPrecentage(numberOfJobs,
					numberOfMachines, numberOfIntevals, percentage, fileName, outputPath);

		}else if (calculateOFISPInstance != null) {

			String fileName = "";
			String outputPath = "";
			String inputPath = "";
			try {
				inputPath = calculateOFISPInstance[0].toString();
			} catch (Exception e) {
				System.out.println("Error: could not parse the input path, please give a valid path");
				return;
			}
			try {
				outputPath = calculateOFISPInstance[1].toString();
			} catch (Exception e) {
				System.out.println("Error: could not parse the output path, please give a output path as string");
				return;
			}
			try {
				fileName = calculateOFISPInstance[2].toString();
			} catch (Exception e) {
				System.out.println("Error: could not parse the filename, please give a filename as string");
				return;
			}
			OFISP_Calculator.calculateInstance(inputPath, outputPath, fileName);
		} else if (lpHeuristicCompact != null) {
			String fileName = "";
			String outputPath = "";
			String inputPath = "";

			try {
				inputPath = lpHeuristicCompact[0].toString();
			} catch (Exception e) {
				System.out.println("Error: could not parse the input path, please give a valid path");
				return;
			}
			try {
				outputPath = lpHeuristicCompact[1].toString();
			} catch (Exception e) {
				System.out.println("Error: could not parse the output path, please give a output path as string");
				return;
			}
			try {
				fileName = lpHeuristicCompact[2].toString();
			} catch (Exception e) {
				System.out.println("Error: could not parse the filename, please give a filename as string");
				return;
			}
			LPHeuristic.calculateLPHeuristic(inputPath, outputPath, fileName);
		} else if (greedyHeuristicCompact != null) {
			String fileName = "";
			String outputPath = "";
			String inputPath = "";
			boolean machineConflict;
			boolean prioWithJobOnMachine;
			boolean maxJobConflict;
			try {
				inputPath = greedyHeuristicCompact[0].toString();
			} catch (Exception e) {
				System.out.println("Error: could not parse the input path, please give a valid path");
				return;
			}
			try {
				outputPath = greedyHeuristicCompact[1].toString();
			} catch (Exception e) {
				System.out.println("Error: could not parse the output path, please give a output path as string");
				return;
			}
			try {
				fileName = greedyHeuristicCompact[2].toString();
			} catch (Exception e) {
				System.out.println("Error: could not parse the filename, please give a filename as string");
				return;
			}
			try {
				machineConflict = greedyHeuristicCompact[3].equals("0") ? false : true;
			} catch (Exception e) {
				machineConflict = false;
				System.out.println(
						"Error: could not set the calculateMashineConflict constraint, the constraint will be set to false");
			}
			try {
				prioWithJobOnMachine = greedyHeuristicCompact[4].equals("0") ? false : true;
			} catch (Exception e) {
				prioWithJobOnMachine = false;
				System.out.println(
						"Error: could not set the calculatePrioWithJobOnMashine constraint, the constraint will be set to false");
			}
			try {
				maxJobConflict = greedyHeuristicCompact[5].equals("0") ? false : true;
			} catch (Exception e) {
				maxJobConflict = false;
				System.out.println(
						"Error: could not set the calculateMaxJobConflict constraint, the constraint will be set to false");

			}
			Greedy.calculateGreedy(inputPath, outputPath, fileName, machineConflict,
					prioWithJobOnMachine, maxJobConflict);
		} else if (lpHeuristic) {

			String file = "";
			String outputPath = "";
			String inputPath = "";
			try {
				if(pathOfTheInstanceToBeRead.equals(null) ) {
					System.out.println("Error: no path has been selected to read from!");
					return;
				}
				inputPath = pathOfTheInstanceToBeRead;
			} catch (Exception e) {
				System.out.println("Error: could not parse the input path, please give a valid path");
				return;
			}
			try {
				if(pathOfTheInstanceToBesSavedTo.equals(null) ) {
					System.out.println("Error: no path has been selected to save the file!");
					return;
				}
				outputPath = pathOfTheInstanceToBesSavedTo;
			} catch (Exception e) {
				System.out.println("Error: could not parse the output path, please give a output path as string");
				return;
			}
			try {
				if(fileName.equals(null) ) {
					System.out.println("Error: no file name has been selected!");
					return;
				}
				file = fileName;
			} catch (Exception e) {
				System.out.println("Error: could not parse the filename, please give a filename as string");
				return;
			}
			LPHeuristic.calculateLPHeuristic(inputPath, outputPath, file);
		} else if (greedy) {
			String file = "";
			String outputPath = "";
			String inputPath = "";
			boolean machineConflict;
			boolean prioWithJobOnMachine;
			boolean maxJobConflict;
			try {
				if(pathOfTheInstanceToBeRead.equals(null) ) {
					System.out.println("Error: no path has been selected to read from!");
					return;
				}
				inputPath = pathOfTheInstanceToBeRead;
			} catch (Exception e) {
				System.out.println("Error: could not parse the input path, please give a valid path");
				return;
			}
			try {
				if(pathOfTheInstanceToBesSavedTo.equals(null) ) {
					System.out.println("Error: no path has been selected to save the file!");
					return;
				}
				outputPath = pathOfTheInstanceToBesSavedTo;
			} catch (Exception e) {
				System.out.println("Error: could not parse the output path, please give a output path as string");
				return;
			}
			try {
				if(fileName.equals(null) ) {
					System.out.println("Error: no file name has been selected!");
					return;
				}
				file = fileName;
			} catch (Exception e) {
				System.out.println("Error: could not parse the filename, please give a filename as string");
				return;
			}
			try {
				machineConflict = calculateMachineConflict ? true : false ;
			} catch (Exception e) {
				machineConflict = false;
				System.out.println(
						"Error: could not set the calculateMashineConflict constraint, the constraint will be set to false");
			}
			try {
				prioWithJobOnMachine = calculatePrioWithJobOnMachine ? true : false  ;
			} catch (Exception e) {
				prioWithJobOnMachine = false;
				System.out.println(
						"Error: could not set the calculatePrioWithJobOnMashine constraint, the constraint will be set to false");
			}
			try {
				maxJobConflict = calculateMaxJobConflict ? true : false  ;
			} catch (Exception e) {
				maxJobConflict = false;
				System.out.println(
						"Error: could not set the calculateMaxJobConflict constraint, the constraint will be set to false");
			}
			Greedy.calculateGreedy(inputPath, outputPath, file, machineConflict, prioWithJobOnMachine,
					maxJobConflict);
		} 
		else if (lp){
			String file = "";
			String outputPath = "";
			String inputPath = "";
			try {
				if(pathOfTheInstanceToBeRead.equals(null) ) {
					System.out.println("Error: the input path is null!");
					return;
				}
				inputPath = pathOfTheInstanceToBeRead;
			} catch (Exception e) {
				System.out.println("Error: could not parse the input path, please give a valid path");
				return;
			}
			try {
				if(pathOfTheInstanceToBesSavedTo.equals(null) ) {
					System.out.println("Error: the output path is null!");
					return;
				}
				outputPath = pathOfTheInstanceToBesSavedTo;
			} catch (Exception e) {
				System.out.println("Error: could not parse the output path, please give a output path as string");
				return;
			}
			try {
				
				if(fileName.equals(null) ) {
					System.out.println("Error: no file name has been selected!");
					return;
				}
				file = fileName;
			} catch (Exception e) {
				System.out.println("Error: could not parse the filename, please give a filename as string");
				return;
			}
			OFISP_Calculator.calculateInstance(inputPath, outputPath, file);
		}
		else if (generate){
			if(instanceType != null) {
				int jobs = 0;
				int machines = 0;
				int intervals = 0;
				int jobOnMachine = 0;
				int cor = 1;
				int percent = 0;
				int maxProfitTemp = 0;
				String fileNameTemp = "";
				String outputPath = "";
			
				try {
					jobs = numberOfJobs;
					if(jobs==0 ) {
						System.out.println("Error: the number of jobs can not be 0!");
						return;
					}
				} catch (Exception e) {
					System.out
							.println("Error: could not convert the number of jobs, please give a positive integer number");
					return;
				}
				try {
					machines =numberOfMachines ;
					if(machines==0 ) {
						System.out.println("Error: the number of machines can not be 0!");
						return;
					}
				} catch (Exception e) {
					System.out.println(
							"Error: could not convert the number of machines, please give a positive integer number");
					return;
				}
				try {
					intervals = numberOfIntervals;
					if(intervals==0 ) {
						System.out.println("Error: the number of intervals can not be 0!");
						return;
					}
				} catch (Exception e) {
					System.out.println(
							"Error: could not convert the number of Intervals, please give a positive integer number");
					return;
				}
				
				try {
					if(fileName.equals(null) ) {
					System.out.println("Error: no file name has been selected!");
					return;
				}
					fileNameTemp = fileName ;
				} catch (Exception e) {
					System.out.println("Error: could not parse the filename, please give a filename as string");
					return;
				}
				try {
					if(pathOfTheInstanceToBesSavedTo.equals(null) ) {
						System.out.println("Error: no path has been selected to save the file!");
						return;
					}
					outputPath = pathOfTheInstanceToBesSavedTo;
				} catch (Exception e) {
					System.out.println("Error: could not parse the outputPath, please give a valid path");
					return;
				}
				if(instanceType.equals("ip")) {
					try {
						if(jobOnMaxMachines==0 ) {
							System.out.println("Error: the number of jobOnMaxMachines can not be 0!");
							return;
						}
						jobOnMachine = jobOnMaxMachines ;
					} catch (Exception e) {
						System.out.println(
								"Error: could not convert the jobOnMachine parameter, please give a positive integer number");
						return;
					}
					try {
					IGIntervalProfit.generateInstanceWithIntervalProfit( jobs,  machines , intervals,
							jobOnMachine,  fileNameTemp, outputPath);
				}catch (Exception e) {
					System.out.println("Error in generating the instance");
					return;
				}
				}
				else if(instanceType.equals("ipc")) {
					try {
						if(jobOnMaxMachines==0 ) {
						System.out.println("Error: the number of jobOnMaxMachines can not be 0!");
						return;
					}
						jobOnMachine = jobOnMaxMachines ;
					} catch (Exception e) {
						System.out.println(
								"Error: could not convert the jobOnMachine parameter, please give a positive integer number");
						return;
					}
					try {
						if(correlation==0 ) {
							System.out.println("Error: the number of correlation can not be 0!");
							return;
						}
						cor = correlation ;
					} catch (Exception e) {
						System.out.println(
								"Error: could not convert the correlation parameter, please give a positive integer number");
						return;
					}
					IGIntervalProfitCor.generateInstanceIntervalCorrelationProfit( jobs,  machines , intervals,
							 jobOnMaxMachines, cor,  fileNameTemp, outputPath);
				}
				else if(instanceType.equals("ipp")) {
					try {
						if(precentage==0 ) {
							System.out.println("Error: the number of precentage can not be 0!");
							return;
						}
						percent = precentage ;
					} catch (Exception e) {
						System.out.println(
								"Error: could not convert the precentage parameter, please give a positive integer number");
						return;
					}
					IGIntervalProfitPrecent.generateInstanceIntervalProfitPrecentage( jobs,  machines , intervals,
							percent,  fileNameTemp, outputPath);
				}
				else if(instanceType.equals("rp")) {
					try {
						if(jobOnMaxMachines==0 ) {
							System.out.println("Error: the number of jobOnMaxMachines can not be 0!");
							return;
						}
						jobOnMachine = jobOnMaxMachines ;
					} catch (Exception e) {
						System.out.println(
								"Error: could not convert the jobOnMachine parameter, please give a positive integer number");
						return;
					}
					try {
						if(maxProfit==0 ) {
							System.out.println("Error: the number of maxProfit can not be 0!");
							return;
						}
						maxProfitTemp = maxProfit ;
					} catch (Exception e) {
						System.out.println(
								"Error: could not convert the maxProfit parameter, please give a positive integer number");
						return;
					}
					IGRandomProfit.generateInstanceWithRandomProfit( jobs,  machines , intervals,
							maxProfitTemp, jobOnMachine,  fileNameTemp, outputPath);
				}
				else {
					System.out.println("the selected type is not defined");
					return;
				}
			}
			else {
				System.out.println("no type has been selected");
				return;
			}
		}else {
		
			System.out.println("Please choose a command or call help.");
		}

	}
}
