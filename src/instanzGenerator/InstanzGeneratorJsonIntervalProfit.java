package instanzGenerator;

import java.io.BufferedWriter;
import java.io.ByteArrayOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.StringWriter;
import java.lang.reflect.Array;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Random;
import java.util.Scanner;
import java.util.concurrent.ThreadLocalRandom;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import com.fasterxml.jackson.core.JsonGenerationException;
import com.fasterxml.jackson.core.util.DefaultPrettyPrinter;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ObjectWriter;

public class InstanzGeneratorJsonIntervalProfit {

	public static void generateInstanceWithIntevalProfit(int numberOfJobs, int numberOfMashines ,int numberOfInterval,
			int jobOnMaxMachines, String fileName,String path) {
		 
		 String type = "Interval Profit";
		 String description = "this is an interval profit instance with " + numberOfJobs+ "jobs and "+ numberOfMashines 
				 +" machines and an interval lenght of" + numberOfInterval;
		 	
		 Random random = new Random();
		 List<JobInput> listOfJobs = new ArrayList<JobInput>();

		 List<int[]> jobsOnMachine = new ArrayList<int[]>();
		 Integer [][] matrix = new Integer [numberOfJobs][3] ;
	

		 for(int i = 0; i<numberOfJobs; ++i) {
			 
			 List<Integer> temp = IntStream.rangeClosed(1, numberOfMashines).boxed().collect(Collectors.toList());
			 Collections.shuffle(temp);
			 int numberOfMachinesForThisJob = ThreadLocalRandom.current().nextInt(1, jobOnMaxMachines+1);
			 int[] machines = new int [numberOfMachinesForThisJob];
			 for(int j=0;j<numberOfMachinesForThisJob;++j) {
				 machines[j]=temp.get(j);
				 
			 }
			 jobsOnMachine.add(machines);
			
		 }
		 //generate an interval for a job
		 for(int i = 0; i<numberOfJobs; ++i) {
			 matrix[i][1]=random.nextInt(numberOfInterval-1 );
		 }
		 for(int i = 0; i<numberOfJobs; ++i) {
			 int difference = random.nextInt(numberOfInterval - matrix[i][1] + 1);
			 matrix[i][2]=(difference)==0? difference + matrix[i][1]+ 1:difference + matrix[i][1];
		 }
		 //assign a profit to a job
		 for(int i = 0; i<numberOfJobs; ++i) {
			 matrix[i][0]=matrix[i][2]-matrix[i][1];
		 }
		 for (int i = 1; i <= matrix.length; i++) {
			    	listOfJobs.add(new JobInput(i,matrix[i-1][0],matrix[i-1][1],matrix[i-1][2],jobsOnMachine.get(i-1)));
			        
			    
			}
		 
		 JsonInstanz jsonInstanz = new JsonInstanz(numberOfJobs,numberOfMashines,numberOfInterval,type,description,listOfJobs);
		     ObjectMapper mapper = new ObjectMapper();
		    ObjectWriter writer = mapper.writer(new DefaultPrettyPrinter());
		    try {
		    	writer.writeValue(Paths.get(path+fileName+ ".json").toFile(), jsonInstanz);
			} catch (JsonGenerationException e) {
				e.printStackTrace();
			} catch (JsonMappingException e) {
				e.printStackTrace();
			} catch (IOException e) {
				e.printStackTrace();
			}


		    

		
			    System.out.println("Doneeeeeee");
			
	}

}
