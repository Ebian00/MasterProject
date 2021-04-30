package main;

import heuristic.GreedyHeuristicMaxProfit;
import heuristic.LPHeuristic;
import instanzGenerator.InstanzGeneratorJsonRandomProfit;
import instanzGenerator.InstanzGeneratorJsonIntervalProfit;
import instanzGenerator.InstanzGeneratorJsonIntervalProfitWithCor;
import ofisp_calculator.OFISP_Calculator;
import picocli.CommandLine;
import picocli.CommandLine.Command;
import picocli.CommandLine.Option;

@Command(name = "OFISP", mixinStandardHelpOptions = true, version = "OFISP 1.0", description = "This program generates and calculates instances of the Operational Fixed Scheduling "
		+ "Interval Problems")
public class OFISP implements Runnable {

	@Option(names = { "-lph",
			"--solveLPHeuristic" }, defaultValue = "false", description = "calculates an instance with the lp heristic")
	boolean lpHeuristic;
	@Option(names = { "-gh",
			"--greedy" }, defaultValue = "false", description = "calculates an instance with the greedy heristic")
	boolean greedy;
	@Option(names = { "-cmc",
			"--calculateMachineConflict" }, defaultValue = "false", description = "defines whether a greedy should use the calculateMachineConflict contraint")
	boolean calculateMachineConflict;
	@Option(names = { "-cpjm",
			"--calculatePrioWithJobOnMachine" }, defaultValue = "false", description = "defines whether a greedy should use the calculatePrioWithJobOnMachine contraint")
	boolean calculatePrioWithJobOnMachine;
	@Option(names = { "-cmjc",
			"--calculateMaxJobConflict" }, defaultValue = "false", description = "defines whether a greedy should use the calculateMaxJobConflict contraint")
	boolean calculateMaxJobConflict;
	@Option(names = { "-f", "--file" }, description = "name of the file to be generated or to be read")
	String fileName;
	@Option(names = { "-ipf", "--inputFile" }, description = "path of the instance to be to be read")
	String pathOfTheInstanceToBeRead;
	@Option(names = { "-opf", "--outputFile" }, description = "path of the instance to be to be read")
	String pathOfTheInstanceToBesSavedTo;
	@Option(names = { "-g", "--generateInstance" }, description = "generate an OFISP instance")
	boolean generate;
	@Option(names = { "-t",
			"--instanceType" }, description = "instance type to be generated, the options are ri for random instance"
					+ " wi for the weight instance and wci for weight with corrolation")
	String instanceType;
	@Option(names = { "-nm", "--numberOfMachines" }, description = "file to be generated or to be read")
	int numberOfMachines;
	@Option(names = { "-ni", "--numberOfIntervals" }, description = "number of machine to be generated")
	int numberOfIntervals;
	@Option(names = { "-nj", "--numberOfJobs" }, description = "number of intervals to be generated")
	int numberOfJobs;
	@Option(names = { "-nmm", "--jobOnMaxMachines" }, description = "number of jobs to be generated")
	int jobOnMaxMachines;
	@Option(names = { "-cor", "--corrolation" }, defaultValue = "10", description = "the corrolation of a job weight")
	int corrolation;
	@Option(names = { "-mw", "--maxWeight" }, description = "max weight which a job can have ")
	int maxWeight;
	@Option(names = { "-grw",
			"--generateInstanceWithRandomWeight" }, arity = "8", description = "this is the command to generate an instance with random weight. 7 parameter in the following sequence are needed"
					+ "1. number of jobs, 2. number of machines, 3. number of intervals, 4. max weight distribution, 5. max jobs on a machine, 6. the file name to be generated, 7. the path where the file should be generated ")
	String[] configurationsForRandomWeight;
	@Option(names = { "-giw",
			"--generateInstanceWithIntervalWeight" }, arity = "7", description = "this is the command to generate an instance with random weight. 6 parameter in the following sequence are needed"
					+ "1. number of jobs, 2. number of machines, 3. number of intervals, 4.  max jobs on a machine, 5. the file name to be generated, 6. the path where the file should be generated ")
	String[] configurationsForIntervalWeight;
	@Option(names = { "-gicw",
			"--generateInstanceWithIntervalCorWeight" }, arity = "8", description = "this is the command to generate an instance with random weight. 7 parameter in the following sequence are needed"
					+ "1. number of jobs, 2. number of machines, 3. number of intervals, 4. max  jobs on a machine, 5. corrolation, 6. the file name to be generated, 7. the path where the file should be generated ")
	String[] configurationsForIntervalCorrolationWeight;
	@Option(names = { "-s",
			"--solve" }, arity = "3", description = "this is the command to calculate an OFISP instance. 3 parameter in the following sequence are needed"
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
		// OFISP ofisp = CommandLine.populateCommand(new OFISP(), args);
		int exitCode = new CommandLine(new OFISP()).execute(args);
		System.exit(exitCode);
	}

	@Override
	public void run() {

		if (configurationsForRandomWeight != null) {

			int numberOfJobs = 0;
			int numberOfMachines = 0;
			int numberOfIntevals = 0;
			int maxWeightDistribution = 0;
			int jobOnMaxMachine = 0;
			String fileName = "";
			String outputPath = "";
			try {
				numberOfJobs = Integer.parseInt(configurationsForRandomWeight[1]);
			} catch (Exception e) {
				System.out
						.println("Error: could not convert the number of jobs, please give a positive integer number");
				return;
			}
			try {
				numberOfMachines = Integer.parseInt(configurationsForRandomWeight[2]);
			} catch (Exception e) {
				System.out.println(
						"Error: could not convert the number of machines, please give a positive integer number");
				return;
			}
			try {
				numberOfIntevals = Integer.parseInt(configurationsForRandomWeight[3]);
			} catch (Exception e) {
				System.out.println(
						"Error: could not convert the number of Intervals, please give a positive integer number");
				return;
			}
			try {
				maxWeightDistribution = Integer.parseInt(configurationsForRandomWeight[4]);
			} catch (Exception e) {
				System.out.println(
						"Error: could not convert the max weight distribution, please give a positive integer number");
				return;
			}
			try {
				jobOnMaxMachine = Integer.parseInt(configurationsForRandomWeight[5]);
			} catch (Exception e) {
				System.out.println(
						"Error: could not convert the number of machines for a job, please give a positive integer number");
				return;
			}
			try {
				fileName = configurationsForRandomWeight[6].toString();
			} catch (Exception e) {
				System.out.println("Error: could not parse the filename, please give a filename as string");
				return;
			}
			try {
				outputPath = configurationsForRandomWeight[7].toString();
			} catch (Exception e) {
				System.out.println("Error: could not parse the outputPath, please give a valid path");
				return;
			}
			InstanzGeneratorJsonRandomProfit.generateInstanceWithRandomProfit(numberOfJobs, numberOfMachines,
					numberOfIntevals, maxWeightDistribution, jobOnMaxMachine, fileName, outputPath);
		} else if (configurationsForIntervalWeight != null) {
			int numberOfJobs = 0;
			int numberOfMachines = 0;
			int numberOfIntevals = 0;
			int jobOnMaxMachine = 0;
			String fileName = "";
			String outputPath = "";
			try {
				numberOfJobs = Integer.parseInt(configurationsForRandomWeight[1]);
			} catch (Exception e) {
				System.out
						.println("Error: could not convert the number of jobs, please give a positive integer number");
				return;
			}
			try {
				numberOfMachines = Integer.parseInt(configurationsForRandomWeight[2]);
			} catch (Exception e) {
				System.out.println(
						"Error: could not convert the number of machines, please give a positive integer number");
				return;
			}
			try {
				numberOfIntevals = Integer.parseInt(configurationsForRandomWeight[3]);
			} catch (Exception e) {
				System.out.println(
						"Error: could not convert the number of Intervals, please give a positive integer number");
				return;
			}

			try {
				jobOnMaxMachine = Integer.parseInt(configurationsForRandomWeight[4]);
			} catch (Exception e) {
				System.out.println(
						"Error: could not convert the number of machines for a job, please give a positive integer number");
				return;
			}
			try {
				fileName = configurationsForRandomWeight[5].toString();
			} catch (Exception e) {
				System.out.println("Error: could not parse the filename, please give a filename as string");
				return;
			}
			try {
				outputPath = configurationsForRandomWeight[6].toString();
			} catch (Exception e) {
				System.out.println("Error: could not parse the outputPath, please give a valid path");
				return;
			}
			InstanzGeneratorJsonIntervalProfit.generateInstanceWithIntevalProfit(numberOfJobs, numberOfMachines,
					numberOfIntevals, jobOnMaxMachine, fileName, outputPath);

		} else if (configurationsForIntervalCorrolationWeight != null) {

			int numberOfJobs = 0;
			int numberOfMachines = 0;
			int numberOfIntevals = 0;
			int jobOnMaxMachine = 0;
			int corrolation = 0;
			String fileName = "";
			String outputPath = "";
			try {
				numberOfJobs = Integer.parseInt(configurationsForRandomWeight[1]);
			} catch (Exception e) {
				System.out
						.println("Error: could not convert the number of jobs, please give a positive integer number");
				return;
			}
			try {
				numberOfMachines = Integer.parseInt(configurationsForRandomWeight[2]);
			} catch (Exception e) {
				System.out.println(
						"Error: could not convert the number of machines, please give a positive integer number");
				return;
			}
			try {
				numberOfIntevals = Integer.parseInt(configurationsForRandomWeight[3]);
			} catch (Exception e) {
				System.out.println(
						"Error: could not convert the number of Intervals, please give a positive integer number");
				return;
			}

			try {
				jobOnMaxMachine = Integer.parseInt(configurationsForRandomWeight[5]);
			} catch (Exception e) {
				System.out.println(
						"Error: could not convert the number of machines for a job, please give a positive integer number");
				return;
			}
			try {
				corrolation = Integer.parseInt(configurationsForRandomWeight[4]);
			} catch (Exception e) {
				System.out.println("Error: could not convert the corrolation, please give a positive integer number");
				return;
			}
			try {
				fileName = configurationsForRandomWeight[6].toString();
			} catch (Exception e) {
				System.out.println("Error: could not parse the filename, please give a filename as string");
				return;
			}
			try {
				outputPath = configurationsForRandomWeight[7].toString();
			} catch (Exception e) {
				System.out.println("Error: could not parse the outputPath, please give a valid path");
				return;
			}
			InstanzGeneratorJsonIntervalProfitWithCor.generateInstanceWithIntevalCorrolationProfit(numberOfJobs,numberOfMachines,numberOfIntevals,jobOnMaxMachine,corrolation,fileName,outputPath);

		} else if (calculateOFISPInstance != null) {
			
			String fileName = "";
			String outputPath = "";
			String inputPath = "";
			try {
				inputPath = configurationsForRandomWeight[1].toString();
			} catch (Exception e) {
				System.out.println("Error: could not parse the input path, please give a valid path");
				return;
			}
			try {
				outputPath = configurationsForRandomWeight[2].toString();
			} catch (Exception e) {
				System.out.println("Error: could not parse the output path, please give a output path as string");
				return;
			}
			try {
				fileName = configurationsForRandomWeight[3].toString();
			} catch (Exception e) {
				System.out.println("Error: could not parse the filename, please give a filename as string");
				return;
			}
			OFISP_Calculator.calculateInstance(inputPath, outputPath,fileName);
		} else if (lpHeuristicCompact != null) {
			String fileName = "";
			String outputPath = "";
			String inputPath = "";
	
			try {
				inputPath = configurationsForRandomWeight[1].toString();
			} catch (Exception e) {
				System.out.println("Error: could not parse the input path, please give a valid path");
				return;
			}
			try {
				outputPath = configurationsForRandomWeight[2].toString();
			} catch (Exception e) {
				System.out.println("Error: could not parse the output path, please give a output path as string");
				return;
			}
			try {
				fileName = configurationsForRandomWeight[3].toString();
			} catch (Exception e) {
				System.out.println("Error: could not parse the filename, please give a filename as string");
				return;
			}
			LPHeuristic.calculateLPHeuristic(inputPath, outputPath,fileName);
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
				System.out.println("Error: could not set the calculateMashineConflict constraint, the constraint will be set to false");
			}
			try {
				prioWithJobOnMachine = greedyHeuristicCompact[4].equals("0") ? false : true;
			} catch (Exception e) {
				prioWithJobOnMachine = false;
				System.out.println("Error: could not set the calculatePrioWithJobOnMashine constraint, the constraint will be set to false");
			}
			try {
				maxJobConflict = greedyHeuristicCompact[5].equals("0") ? false : true;
			} catch (Exception e) {
				maxJobConflict = false;
				System.out.println("Error: could not set the calculateMaxJobConflict constraint, the constraint will be set to false");
				
			}
			GreedyHeuristicMaxProfit.calculateGreedy(inputPath, outputPath,
					fileName, machineConflict,
					prioWithJobOnMachine,
					maxJobConflict);
		} else if (lpHeuristic) {

			String file = "";
			String outputPath = "";
			String inputPath = "";
			try {
				inputPath = pathOfTheInstanceToBeRead;
			} catch (Exception e) {
				System.out.println("Error: could not parse the input path, please give a valid path");
				return;
			}
			try {
				outputPath = pathOfTheInstanceToBesSavedTo;
			} catch (Exception e) {
				System.out.println("Error: could not parse the output path, please give a output path as string");
				return;
			}
			try {
				file = fileName;
			} catch (Exception e) {
				System.out.println("Error: could not parse the filename, please give a filename as string");
				return;
			}
			LPHeuristic.calculateLPHeuristic(pathOfTheInstanceToBeRead, pathOfTheInstanceToBesSavedTo, fileName);
		} else if (greedy) {
			String file = "";
		String outputPath = "";
		String inputPath = "";
		boolean machineConflict;
		boolean prioWithJobOnMachine;
		boolean maxJobConflict;
		try {
			inputPath = pathOfTheInstanceToBeRead;
		} catch (Exception e) {
			System.out.println("Error: could not parse the input path, please give a valid path");
			return;
		}
		try {
			outputPath = pathOfTheInstanceToBesSavedTo;
		} catch (Exception e) {
			System.out.println("Error: could not parse the output path, please give a output path as string");
			return;
		}
		try {
			file = fileName;
		} catch (Exception e) {
			System.out.println("Error: could not parse the filename, please give a filename as string");
			return;
		}
		try {
			machineConflict = calculateMachineConflict ? false : true;
		} catch (Exception e) {
			machineConflict = false;
			System.out.println("Error: could not set the calculateMashineConflict constraint, the constraint will be set to false");
		}
		try {
			prioWithJobOnMachine = calculatePrioWithJobOnMachine ? false : true;
		} catch (Exception e) {
			prioWithJobOnMachine = false;
			System.out.println("Error: could not set the calculatePrioWithJobOnMashine constraint, the constraint will be set to false");
		}
		try {
			maxJobConflict =calculateMaxJobConflict? false : true;
		} catch (Exception e) {
			maxJobConflict = false;
			System.out.println("Error: could not set the calculateMaxJobConflict constraint, the constraint will be set to false");
		}
		GreedyHeuristicMaxProfit.calculateGreedy(inputPath, outputPath,file, machineConflict,prioWithJobOnMachine,maxJobConflict);
		} else {
			System.out.println("Please choose a command or call help.");
		}

	}
}
