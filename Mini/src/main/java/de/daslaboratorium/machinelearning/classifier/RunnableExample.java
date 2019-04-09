package main.java.de.daslaboratorium.machinelearning.classifier;

import java.util.Arrays;

import java.util.Scanner;

import java.lang.*;
import java.util.Vector;

import javax.swing.JFrame;
import javax.swing.JTextArea;

import com.datastax.driver.core.Cluster;
import com.datastax.driver.core.ResultSet;
import com.datastax.driver.core.Row;
import com.datastax.driver.core.Session;

import main.java.de.daslaboratorium.machinelearning.classifier.bayes.BayesClassifier;
import main.java.de.daslaboratorium.machinelearning.classifier.Classifier;

public class RunnableExample2 {
	static Cluster cluster;
	static Session session;
	static ResultSet results;
	static ResultSet results1;
	static ResultSet results2;
	static ResultSet results3;

	static Row rows;
    public static void main(String[] args) {
    	

    	try
		{	cluster = Cluster.builder().addContactPoint("127.0.0.1").build();
			session = cluster.connect();
		
			session.execute("CREATE KEYSPACE IF NOT EXISTS titanic WITH replication " + "= {'class':'SimpleStrategy','replication_factor':1}; ");
			session.execute("USE titanic");
		
			//create users table
			session.execute("CREATE TABLE IF NOT EXISTS titanic(ID int primary key, class text, age text, sex text, survival text);");
			session.execute("CREATE TABLE IF NOT EXISTS testing(ID int primary key, class text, age text, sex text, survival text);");
			  		  
			final Classifier<String, String> bayes =
	                new BayesClassifier<String, String>();
		  
		    results = session.execute("SELECT * FROM titanic where survival='yes' ");
		    results1 = session.execute("SELECT * FROM titanic where survival='no' ");
		    	   
		  for (Row row : results) {
		         final String[] First =row.getString("survival").split("\\s");
			     bayes.learn("Maximum age-group: adult", Arrays.asList(First));
				   }
		  for (Row row : results1) {
		         final String[] Third =row.getString("survival").split("\\s");
		         bayes.learn("Maximum age-group: child", Arrays.asList(Third));
		  }
		
		  //System.out.println("Hi");

		  results3 = session.execute("SELECT * FROM testing");
		  for (Row row : results3) 
				{ 
		           final String[] unknownText1 = row.getString("survival").split("\\s");
		             System.out.print( bayes.classify(Arrays.asList(unknownText1)).getCategory());
		             JFrame jframe = new JFrame();
		             JTextArea jtext = new JTextArea();
		          
		             System.out.format("--------Sex is: %s\t\n",row.getString("sex"));
		             
				}
			   	bayes.setMemoryCapacity(500);
		    }
		catch(Exception e)
		{
		   System.out.println("Error: "+e.getMessage());	
		}
		
	   cluster.close();

    }

		    
}
