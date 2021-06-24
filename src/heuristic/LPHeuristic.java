package heuristic;

import java.io.File;
import java.io.IOException;
import java.nio.file.Paths;
import java.time.Duration;
import java.time.Instant;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import com.fasterxml.jackson.core.JsonGenerationException;
import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.core.util.DefaultPrettyPrinter;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ObjectWriter;

import gurobi.*;
import instanzGenerator.JobInput;
import instanzGenerator.JsonInstanz;
import ofisp_calculator.JobOutput;
import ofisp_calculator.JsonOutput;

public class LPHeuristic {
	public static void calculateLPHeuristic(String instancePath, String outputPath, String outputName ) {

		JsonInstanz jsonInstanz = null;
		ObjectMapper mapper = new ObjectMapper();
		try {
			jsonInstanz = mapper.readValue(new File(instancePath), JsonInstanz.class);
		} catch (JsonParseException e1) {
			e1.printStackTrace();
		} catch (JsonMappingException e1) {
			e1.printStackTrace();
		} catch (IOException e1) {
			e1.printStackTrace();
		}

		// the start variable keeps the starting time of the algorithm
		Instant start = Instant.now();
		try {
			int numberOfJobs = jsonInstanz.getNumberOfJobs();
			int numberOfMachines = jsonInstanz.getNumberOfMachines();
			int numberOfInterval = jsonInstanz.getIntervalLenghts();
			// Create empty environment, set options, and start
			GRBEnv env = new GRBEnv(true);
//			env.set("logFile", "MasterProjekt.log");
			env.start();

			// Create empty model
			GRBModel model = new GRBModel(env);

			// Create variables

			// this Map contains all the objective variables as a gorubi variable
			Map<Integer, ArrayList<GRBVar>> objectiveVariables = new LinkedHashMap<Integer, ArrayList<GRBVar>>();
			// this Map sets the variables on the interval which the job is assigned to
			Map<Integer, Map<Integer, ArrayList<GRBVar>>> jobOverlaping = new LinkedHashMap<Integer, Map<Integer, ArrayList<GRBVar>>>();
			// Initialize the joboverlaping map
			for (int i = 0; i < numberOfJobs; ++i) {
				objectiveVariables.put(i, new ArrayList<GRBVar>());
			}
			for (int i = 1; i <= numberOfMachines; ++i) {
				jobOverlaping.put(i, new LinkedHashMap<Integer, ArrayList<GRBVar>>());
				for (int j = 0; j <= numberOfInterval; ++j) {
					jobOverlaping.get(i).put(j, new ArrayList<GRBVar>());
				}
			}
			// Fill the matrix and adding the jobs to the model and setting the
			// joboverlapings
			for (int i = 0; i < numberOfJobs; ++i) {
				JobInput job = jsonInstanz.getListOfJobs().get(i);
				int[] temp = job.getJobsOnMachine();
				for (Integer jobsOnMachine : temp) {
					GRBVar var = model.addVar(0, 1.0, 0, GRB.CONTINUOUS,
							"x" + job.getJobNumber() + "," + jobsOnMachine);
					objectiveVariables.get(i).add(var);
					int startTime = job.getStartTime();
					int endTime = job.getFinishTime();
					for (int j = startTime; j < endTime; ++j) {
						jobOverlaping.get(jobsOnMachine).get(j).add(var);
					}
				}
				;
			}

			// Set objective: maximize x + y + 2 z
			int numberOfObjectiveVariables = 0;
			GRBLinExpr expr = new GRBLinExpr();
			for (int i = 0; i < numberOfJobs; ++i) {
				int weight = jsonInstanz.getListOfJobs().get(i).getJobProfit();
				List<GRBVar> list = objectiveVariables.get(i);
				int lenght = list.size();
				for (int j = 0; j < lenght; ++j) {
					expr.addTerm(weight, list.get(j));
					++numberOfObjectiveVariables;
				}
			}

			model.setObjective(expr, GRB.MAXIMIZE);

			// Add the constraints that a job can only be done by one machine : x12 + x14 +
			// x16 <=1
			int size = objectiveVariables.size();
			//System.out.println("number of objective variables : " + numberOfObjectiveVariables);

			for (int i = 0; i < size; ++i) {
				expr = new GRBLinExpr();
				List<GRBVar> list = objectiveVariables.get(i);
				int lenght = list.size();
				for (int j = 0; j < lenght; ++j) {
					expr.addTerm(1.0, list.get(j));
				}

				model.addConstr(expr, GRB.LESS_EQUAL, 1.0, "d" + i);

			}
			// Add constraint: x + 2 y + 3 z <= 4
			int totalNumberOfConstraints = 0;
			for (int i = 1; i <= numberOfMachines; ++i) {
				ArrayList<GRBVar> compare = new ArrayList<GRBVar>();
				expr = new GRBLinExpr();
				Map<Integer, ArrayList<GRBVar>> temp = jobOverlaping.get(i);
				for (int j = 0; j < temp.size(); ++j) {
					ArrayList<GRBVar> check = temp.get(j);
					if (check.size() > 0 && !check.equals(compare)) {

						compare = check;
						expr = new GRBLinExpr();
						for (int k = 0; k < check.size(); ++k) {
							expr.addTerm(1.0, check.get(k));

						}
						model.addConstr(expr, GRB.LESS_EQUAL, 1.0, "c" + i + j);
						++totalNumberOfConstraints;
					}
				}

			}
		    //model.write(numberOfJobs + "Heuristik-Model-Output.mst");
			model.optimize();

			List<GRBVar> choosenJobs = new ArrayList<GRBVar>();
			List<GRBVar> brockenJobs = new ArrayList<GRBVar>();
			// this function puts the variable in two list, if a variable = 1 it comes in
			// the list for choosenJobs and
			// if the variable has brocken value it comes into the brockeJobs list
			for (int i = 0; i < jsonInstanz.getNumberOfJobs(); ++i) {
				List<GRBVar> list = objectiveVariables.get(i);
				int lenght = list.size();
				for (int j = 0; j < lenght; ++j) {
					GRBVar var = list.get(j);

					if (var.get(GRB.DoubleAttr.X) > 0) {

						if (java.lang.Double.compare(var.get(GRB.DoubleAttr.X), 1) == 0) {
							choosenJobs.add(var);
						} else {
							//System.out.println(var.get(GRB.DoubleAttr.X));
							brockenJobs.add(var);
						}
					}
				}

			}
			// sorting the brocken variables based on their value
			Collections.sort(brockenJobs, new Comparator<GRBVar>() {
				@Override
				public int compare(GRBVar o1, GRBVar o2) {
					int result = 0;
					try {
						result = Double.compare(o2.get(GRB.DoubleAttr.X), o1.get(GRB.DoubleAttr.X));
					} catch (GRBException e) {
						e.printStackTrace();
					}
					return result;
				}
			});

			List<JobOutput> listOfJobsOutput = new ArrayList<JobOutput>();
			int Objective = calculateInstance(numberOfJobs, jsonInstanz.getNumberOfMachines(),
					jsonInstanz.getIntervalLenghts(), choosenJobs, brockenJobs, jsonInstanz, listOfJobsOutput);

			JsonOutput jsonOutput = new JsonOutput(numberOfJobs, numberOfMachines, numberOfInterval,
					Objective, jsonInstanz.getType(),jsonInstanz.getDescription(),listOfJobsOutput);
			ObjectWriter writer = mapper.writer(new DefaultPrettyPrinter());
			try {
				writer.writeValue(Paths
						.get(outputPath+outputName+ ".json")
						.toFile(), jsonOutput);
			} catch (JsonGenerationException e) {
				e.printStackTrace();
			} catch (JsonMappingException e) {
				e.printStackTrace();
			} catch (IOException e) {
				e.printStackTrace();
			}

			// Dispose of model and environment
			model.dispose();
			env.dispose();

		} catch (GRBException e) {
			System.out.println("Error code: " + e.getErrorCode() + ". " + e.getMessage());
		}
		Instant finish = Instant.now();
		 double time = Duration.between(start, finish).toMillis();
		System.out.println("time in seconds = " + time/(1000));

	}

	public static int calculateInstance(int numberOfJobs, int numberOfMashines, int intevallLength,
			List<GRBVar> choosenJobs, List<GRBVar> brockenJobs, JsonInstanz jsonInstanz,
			List<JobOutput> listOfJobsOutput) {
		Map<Integer, Map<Integer, Integer>> jobOverlaping = new LinkedHashMap<Integer, Map<Integer, Integer>>();
		int objective = 0;
		for (int i = 1; i <= numberOfMashines; ++i) {
			jobOverlaping.put(i, new LinkedHashMap<Integer, Integer>());
			for (int j = 0; j <= intevallLength; ++j) {
				jobOverlaping.get(i).put(j, 0);
			}
		}
		// Set the choosen variable by the lp relaxation
		for (GRBVar grbVar : choosenJobs) {
		/*	try {
				System.out.println(grbVar.get(GRB.StringAttr.VarName));
			} catch (GRBException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}*/
			String[] variable = null;
			try {
				variable = grbVar.get(GRB.StringAttr.VarName).substring(1, grbVar.get(GRB.StringAttr.VarName).length())
						.split(",");
			} catch (GRBException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			int jobNumber = Integer.parseInt(variable[0]) - 1;
			int machineNumber = Integer.parseInt(variable[1]);
			JobInput job = jsonInstanz.getListOfJobs().get(jobNumber);
			for (int j = job.getStartTime(); j < job.getFinishTime(); ++j) {
				jobOverlaping.get(machineNumber).put(j, 1);

			}
			JobOutput jobOutput = new JobOutput();
			jobOutput.setJobNumber(job.getJobNumber());
			jobOutput.setFinishTime(job.getFinishTime());
			jobOutput.setStartTime(job.getStartTime());
			jobOutput.setJobNumber(job.getJobNumber());
			jobOutput.setJobProfit(job.getJobProfit());
			jobOutput.setMachine(machineNumber);
			listOfJobsOutput.add(jobOutput);
			objective += job.getJobProfit();
		}

		// check if the broken variables by the lp relaxation can be assigned to a
		// machine
		Map<Integer, Boolean> listOfCalculatedJobs = new LinkedHashMap<Integer, Boolean>();
		for (int i = 1; i <= numberOfJobs; ++i) {
			listOfCalculatedJobs.put(i, false);
		}
		for (GRBVar grbVar : brockenJobs) {
			/*try {
				System.out.println(grbVar.get(GRB.StringAttr.VarName));
			} catch (GRBException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}*/
			String[] variable = null;
			try {
				variable = grbVar.get(GRB.StringAttr.VarName).substring(1, grbVar.get(GRB.StringAttr.VarName).length())
						.split(",");
			} catch (GRBException e) {
				e.printStackTrace();
			}
			int jobNumber = Integer.parseInt(variable[0]) ;
			if (!listOfCalculatedJobs.get(jobNumber)) {

				int machineNumber = Integer.parseInt(variable[1]);
				JobInput job = jsonInstanz.getListOfJobs().get(jobNumber-1);
				//System.out.println(" Job Number in JsonInstanz : " + job.getJobNumber());

				boolean occupied = true;
				boolean jobAssinged = false;
				for (int n = 0; n < 1 && !jobAssinged; ++n) {
					occupied = true;
					int currentMachine = machineNumber;
					for (int i = job.getStartTime(); i < job.getFinishTime() && occupied; ++i) {
						for (int j = job.getStartTime(); j < job.getFinishTime() && occupied; ++j) {
							occupied = jobOverlaping.get(currentMachine).get(j) == 0 ? true : false;
						}
						if (occupied) {

//							System.out.println("brocken job " + job.getJobNumber() + " has been taken to the solution");
							for (int j = job.getStartTime(); j < job.getFinishTime(); ++j) {
								jobOverlaping.get(currentMachine).put(j, 1);

							}
							jobAssinged = true;
						}
					}
				}
				if (jobAssinged) {
					listOfCalculatedJobs.put(jobNumber, true);
					objective += job.getJobProfit();
					JobOutput jobOutput = new JobOutput();
					jobOutput.setJobNumber(job.getJobNumber());

					jobOutput.setFinishTime(job.getFinishTime());
					jobOutput.setStartTime(job.getStartTime());
					jobOutput.setJobNumber(job.getJobNumber());
					jobOutput.setJobProfit(job.getJobProfit());
					jobOutput.setMachine(machineNumber);
					listOfJobsOutput.add(jobOutput);

				}
			}
		}
		return objective;

	}
}