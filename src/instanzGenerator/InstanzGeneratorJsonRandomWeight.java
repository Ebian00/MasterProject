package instanzGenerator;

import java.io.IOException;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Random;
import java.util.concurrent.ThreadLocalRandom;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import com.fasterxml.jackson.core.JsonGenerationException;
import com.fasterxml.jackson.core.util.DefaultPrettyPrinter;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ObjectWriter;

public class InstanzGeneratorJsonRandomWeight {

	public static void generateInstanceWithRandomWeight(int numberOfJobs, int numberOfMashines ,int numberOfInterval,
			int maxWeight,int jobOnMaxMachines, String fileName,String path) {
		
		 
		 String type = "Random Weight";
		 Random random = new Random();
		 List<JobInput> listOfJobs = new ArrayList<JobInput>();
	
		 List<int[]> jobsOnMachine = new ArrayList<int[]>();
		 Integer [][] matrix = new Integer [numberOfJobs][3] ;
		 //assign a weight to a job
		 for(int i = 0; i<numberOfJobs; ++i) {
			 matrix[i][0]=random.nextInt(maxWeight ) + 1;
		 }
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
		 for(int i = 0; i<numberOfJobs; ++i) {
			 matrix[i][1]=random.nextInt(numberOfInterval-1 );
		 }
		 for(int i = 0; i<numberOfJobs; ++i) {
			 int difference = random.nextInt(numberOfInterval - matrix[i][1] + 1);
			 matrix[i][2]=(difference)==0? difference + matrix[i][1]+ 1:difference + matrix[i][1];
		 }
		 
		 for (int i = 1; i <= matrix.length; i++) {
			    	listOfJobs.add(new JobInput(i,matrix[i-1][0],matrix[i-1][1],matrix[i-1][2],jobsOnMachine.get(i-1)));
			        
			    
			}
		 
		 JsonInstanz jsonInstanz = new JsonInstanz(numberOfJobs,numberOfMashines,numberOfInterval,maxWeight,listOfJobs, type);
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

			    System.out.println("the instance "+ fileName + " was saved in "+ path);
			
	}

}
