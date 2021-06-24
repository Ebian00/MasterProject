package ofisp_calculator;

import java.io.File;
import java.io.IOException;
import java.nio.file.Paths;
import java.time.Duration;
import java.time.Instant;
import java.util.ArrayList;
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


public class OFISP_Calculator {
	public static void calculateInstance(String instancePath, String outputPath, String outputName) {
		
		JsonInstanz jsonInstanz = null;
		System.out.println("we are in the gurobi and the variables are :" + instancePath + " and " + outputPath + " and " +outputName);
		ObjectMapper mapper = new ObjectMapper();
		try {
			 jsonInstanz = mapper.readValue(new File(instancePath), JsonInstanz.class);
		} catch (JsonParseException e1) {
			System.out.println("file could not be read or found");
			e1.printStackTrace();
		} catch (JsonMappingException e1) {
			e1.printStackTrace();
		} catch (IOException e1) {
			e1.printStackTrace();
		}
		//Instant start = Instant.now();
		try {
			int numberOfJobs = jsonInstanz.getNumberOfJobs();
			 int numberOfMachines= jsonInstanz.getNumberOfMachines();
			 int numberOfInterval= jsonInstanz.getIntervalLenghts();
			// Create empty environment, set options, and start
			GRBEnv env = new GRBEnv(true);
			env.set("logFile", "MasterProjekt.log");
			env.start();

			// Create empty model
			GRBModel model = new GRBModel(env);
		
			// Create variables
			 Map<Integer, ArrayList<GRBVar>> objectiveVariables = new LinkedHashMap<Integer, ArrayList<GRBVar>>();
			 Map<Integer, Map<Integer, ArrayList<GRBVar>>> jobOverlaping = new LinkedHashMap<Integer, Map<Integer, ArrayList<GRBVar>>>();
			 //Initialize the joboverlaping map
			 for (int i =0; i <= numberOfJobs; ++i) {
				 objectiveVariables.put(i, new ArrayList<GRBVar>());
				}
			for (int i =1; i <=numberOfMachines; ++i) {
				jobOverlaping.put(i, new LinkedHashMap<Integer, ArrayList<GRBVar>>());
					for (int j = 0;j<=numberOfInterval;++j) {
						jobOverlaping.get(i).put(j, new ArrayList<GRBVar>());
					}
				}
			 //Fill the matrix and adding the jobs to the model and setting the joboverlapings
			for (int i =0; i < numberOfJobs; ++i) {
				JobInput job = jsonInstanz.getListOfJobs().get(i);
				int[] temp = job.getJobsOnMachine();
				 for(Integer jobsOnMachine : temp) {
						GRBVar var = model.addVar(0.0, 1.0, 0.0, GRB.BINARY, "x"+job.getJobNumber()+","+jobsOnMachine);
						objectiveVariables.get(i).add(var);
						int startTime = job.getStartTime();
						int endTime = job.getFinishTime();
						for(int j = startTime; j <endTime; ++j) {
							jobOverlaping.get(jobsOnMachine).get(j).add(var);
						}
				};
			}
			
			


			// Set objective: maximize x1.1 + x1.2  + x2.1 ....
			int numberOfObjectiveVariables = 0;
			GRBLinExpr expr = new GRBLinExpr();
			for (int i =0; i < numberOfJobs; ++i) {
				int weight = jsonInstanz.getListOfJobs().get(i).getJobProfit();
				List<GRBVar> list = objectiveVariables.get(i);
				int lenght = list.size();
				for(int j = 0; j <lenght; ++j) {
					expr.addTerm(	weight, list.get(j));
					++numberOfObjectiveVariables;
				}
			}
			
			model.setObjective(expr, GRB.MAXIMIZE);
			
			// Add the constraints that a job can only be done by one machine : x12 + x14 + x16 <=1 
			int size = objectiveVariables.size();
			//System.out.println("number of objective variables : " + numberOfObjectiveVariables);
			
			for (int i =0; i < size; ++i) {
				expr = new GRBLinExpr();
				List<GRBVar> list = objectiveVariables.get(i);
				int lenght =list.size();
				for(int j = 0; j <lenght; ++j) {
					expr.addTerm(1.0, list.get(j));
				}
		
				model.addConstr(expr, GRB.LESS_EQUAL, 1.0, "d"+i);
				
			}
			// Add constraint: x + 2 y + 3 z <= 4
			int totalNumberOfConstraints = 0;
			for (int i = 1; i <= numberOfMachines; ++i) {
				ArrayList<GRBVar> compare = new ArrayList<GRBVar>();
				expr = new GRBLinExpr();
				Map<Integer, ArrayList<GRBVar>> temp = jobOverlaping.get(i);
				for (int j = 0;j<temp.size();++j) {
					ArrayList<GRBVar> check = temp.get(j);
					if(check.size()>0 && !check.equals(compare)) {
						
					compare = check;
					expr = new GRBLinExpr();
					for (int k = 0;k<check.size();++k) {
						expr.addTerm(1.0, check.get(k));
						
					}
					model.addConstr(expr, GRB.LESS_EQUAL, 1.0, "c"+i+j);
					++totalNumberOfConstraints;
				}
		}
				
			}
			//System.out.println("total number of constraints : " + totalNumberOfConstraints);

		
		

			// Optimize model
			model.optimize();
			List<JobOutput> listOfJobsOutput = new ArrayList<JobOutput>();
			for (int i =1; i <= numberOfJobs; ++i) {
				List<GRBVar> list = objectiveVariables.get(i-1);
				JobOutput jobOutput = new JobOutput();
				jobOutput.setJobNumber(i);
				int lenght =list.size();
				for(int j = 0; j <lenght; ++j) {
					GRBVar var = list.get(j);
					if(Double.compare(var.get(GRB.DoubleAttr.X),1.0)== 0) {
						JobInput jobInput = jsonInstanz.getListOfJobs().get(i-1);
						jobOutput.setFinishTime(jobInput.getFinishTime());
						jobOutput.setStartTime(jobInput.getStartTime());
						jobOutput.setJobNumber(jobInput.getJobNumber());
						jobOutput.setJobProfit(jobInput.getJobProfit());
						jobOutput.setMachine(jobInput.getJobsOnMachine()[j]);
						listOfJobsOutput.add(jobOutput);
						
					}
				}
			
			}
			 
		 JsonOutput jsonOutput = new JsonOutput(numberOfJobs,numberOfMachines,numberOfInterval,model.get(GRB.DoubleAttr.ObjVal),jsonInstanz.getType(),jsonInstanz.getDescription(),listOfJobsOutput);
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
		


			// Dispose of model and environment
			model.dispose();
			env.dispose();

		} catch (GRBException e) {
			System.out.println("Error code: " + e.getErrorCode() + ". " + e.getMessage());
		}
		//Instant finish = Instant.now();
		//System.out.println(Duration.between(start, finish).toMillis());  //in millis
	}
}