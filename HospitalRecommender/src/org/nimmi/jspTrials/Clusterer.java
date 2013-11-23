package org.nimmi.jspTrials;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.FileSystem;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.io.IntWritable;
import org.apache.hadoop.io.LongWritable;
import org.apache.hadoop.io.SequenceFile;
import org.apache.mahout.clustering.Cluster;
import org.apache.mahout.clustering.iterator.ClusterWritable;
import org.apache.mahout.clustering.kmeans.KMeansDriver;
import org.apache.mahout.clustering.kmeans.Kluster;
import org.apache.mahout.common.distance.EuclideanDistanceMeasure;
import org.apache.mahout.math.RandomAccessSparseVector;
import org.apache.mahout.math.Vector;
import org.apache.mahout.math.VectorWritable;
import org.apache.mahout.clustering.kmeans.Kluster;
import org.apache.mahout.clustering.kmeans.KMeansDriver;
import org.apache.mahout.clustering.topdown.postprocessor.ClusterOutputPostProcessorDriver;
import org.apache.mahout.clustering.classify.WeightedVectorWritable;
import org.apache.mahout.math.VectorWritable;

import com.thoughtworks.xstream.XStream;




public class Clusterer {
	
	
	public static String abspath = "/Users/nimmicv/Documents/workspaceMahout/HospitalRecommender/testData";
	public static void writePointsToFile(List<Vector> points, String fileName, FileSystem fs, Configuration conf) throws IOException
	{
		Path path = new Path(fileName);
		SequenceFile.Writer writer = new SequenceFile.Writer(fs, conf, path, LongWritable.class, VectorWritable.class);
		long recNum = 0;
		VectorWritable vec = new VectorWritable();
		for (Vector point : points)
		{
			vec.set(point);
			writer.append(new LongWritable(recNum++), vec);
		}
		writer.close();
	}
	
	public static Vector getPoints(double[] raw)
	{
		double[] fr = raw;
		Vector vec = new RandomAccessSparseVector(fr.length);
		vec.assign(fr);
		return vec;
	}


	public static List<Vector> getVectorPoints(String inputFile) throws Exception
	{
		List<Vector> points = new ArrayList<Vector>();
		CSVReader csvReader = new CSVReader(new FileReader(inputFile));
		String[] row = null;
	
		while((row = csvReader.readNext()) != null)
		
		{
			double[] dc = new double[2];
			if(!(row[10].equals("Latitude")))
			{
				dc[0] = Double.parseDouble(row[10]);
				dc[1] = Double.parseDouble(row[11]);
				Vector vec = new RandomAccessSparseVector(dc.length);
				vec.assign(dc);
				points.add(vec);
			}
				
			
		}
		csvReader.close();
		return points;
	}
	
	public List<hospitalList> findHospitals(String zipcode) throws Exception{
		int k =50;
		String datafile = abspath+"/data/UpdatedDataSet.csv";
		List<Vector> vectors = getVectorPoints(datafile);
		double[] latLon = {33.925454,-87.78949};
		readCSV rc = null;
		String state =  readCSV.findCity(latLon);

		double dist = 0;
		double smallestDist=0;
		Vector newVec = getPoints(latLon);
		IntWritable nearestCluster = null ; 
		int clusterPos = -1;
		ArrayList<Double> distC = new ArrayList<Double>();
		double smallestdistC = 0;
		List<Vector> finalPoints = new ArrayList<Vector>();
		String nearestC[] = null;
		File testData = new File(abspath);	
		List<String> finalLat = new ArrayList<String>();
		List<String> finalLon = new ArrayList<String>();
		File outputDir = new File(abspath+"/output");
		if(!testData.exists())
		{
			testData.mkdir();
			
		}
		testData= new File(abspath+"/points");
		if(!testData.exists())
		{
			testData.mkdir();
		}
		
		Configuration conf = new Configuration();
		FileSystem fs = FileSystem.get(conf);
		writePointsToFile(vectors, abspath+"/points/file1", fs, conf);
		
		Path path = new Path(abspath+"/clusters/part-00000");
		SequenceFile.Writer writer = new SequenceFile.Writer(fs, conf, path, Text.class,Kluster.class);// );
		
		for (int i =0; i<k; i++)
		{
			Vector vec = vectors.get(i);
			Kluster cluster = new Kluster(vec, i, new EuclideanDistanceMeasure());
			writer.append(new Text(cluster.getIdentifier()), cluster);		
		}
		writer.close();
		if(!outputDir.exists())
		{
			KMeansDriver.run(conf, new Path(abspath+"/points"), new Path(abspath+"/clusters"), new Path(abspath+"/output"), new EuclideanDistanceMeasure(), 0.001,50, true, 0.0, false);
		}
		SequenceFile.Reader reader = new SequenceFile.Reader(fs, new Path(abspath+"/output/"+Cluster.CLUSTERED_POINTS_DIR+ "/part-m-00000"), conf);
		//SequenceFile.Reader reader = new SequenceFile.Reader(fs, new Path( "output/clusters-1/part-r-00000"), conf);
		IntWritable key = new IntWritable();
		WeightedVectorWritable value = new WeightedVectorWritable();
		/*while(reader.next(key, value))
		{
			System.out.println(value.toString() + " belongs to cluster " +key.toString() );
		
		}*/
		SequenceFile.Reader reader1 = new SequenceFile.Reader(fs, new Path(abspath+"/output/clusters-50-final/part-r-00000"), conf);

		IntWritable key1 = new IntWritable();
		ClusterWritable value1 = new ClusterWritable();    
		while (reader1.next(key1, value1))
		{
    	
		    	dist = value1.getValue().getCenter().getDistanceSquared(newVec);
		  //  	System.out.println("Cluster = " + value1.getValue().toString()+" Distance = "+ dist);
		    	if (smallestDist== 0)
		    	{
		    		smallestDist = dist;
		    		nearestCluster = key1;
		    		clusterPos++;
		    	}
		    	else if (dist<= smallestDist)
		      	{
		      		smallestDist = dist ;
		      		 nearestCluster = key1;
		      		 clusterPos++;
		      	}
		
		      /*System.out.println(value1.getValue().getCenter()+ " ------------ "
		                + key1.toString());
		        System.out.println(value1.getValue() + " ------------ "
            + key1.toString());*/

		}
		System.out.println("Nearest Cluster = " + clusterPos+ "  distance= " + smallestDist);

		reader.close();
		Path input= new Path(abspath+"/output");

		Path output= new Path(abspath+"/postoutput");

		// ClusterOutputPostProcessorDriver.run(input, output, true);
		FileSystem fso = output.getFileSystem(conf);

		//print cluster2 points
		SequenceFile.Reader reader2 = new SequenceFile.Reader(fso, new Path(abspath+"/output/"+Cluster.CLUSTERED_POINTS_DIR+ "/part-m-00000"), conf);
		IntWritable key2 = new IntWritable();
		WeightedVectorWritable value2 = new WeightedVectorWritable();
	
		while (reader2.next(key2, value2))
		{
			if(key2.get()== clusterPos)
			{
				finalPoints.add(value2.getVector());	
      
			}
      
		}
		reader2.close();

		for (int t = 0; t <finalPoints.size();t++)
		{
			distC.add(finalPoints.get(t).getDistanceSquared(newVec)) ; 
			finalLat.add(finalPoints.get(t).toString().substring(3, 11)) ;
			finalLon.add(finalPoints.get(t).toString().substring(15, 24));
			System.out.println("Vector= "+finalPoints.get(t).toString()+ " Distance" +distC.get(t));
			System.out.println("Latitude = " + finalLat.get(t)+ "Longitude = "+finalLon.get(t));
		}
   
		CSVReader csvReader = new CSVReader(new FileReader(datafile));
		String[] headerLine =csvReader.readNext();

		String[] data = null;
		 List<hospitalList> pass = new ArrayList();

		while((data = csvReader.readNext()) != null )
		{
			for (int j=0; j<finalPoints.size();j++)
			{
				if(data[10].contains(finalLat.get(j)) && data[11].contains(finalLon.get(j))) 
				{	
					if(data[5].contentEquals(state) )
					{
				
						hospitalList hl = new hospitalList(data[1],data[2],data[3],data[4],data[5],data[6],data[7],data[8],data[9],data[10],data[11]);
						pass.add(hl);
					
						System.out.println("\nAddress:\n " +data[1]+ "\n" +data[2]+ "\n" +data[4]+ "\n" +data[5]+ "\n" +data[6]);
						System.out.println("User rating:\n " +data[7]+"% users rated between 0 and 6");
						System.out.println(  data[8]+"% users rated between 7 and 8");
						System.out.println(data[9]+"% users rated between 9 and 10");	
					}
				}
					
			}
	
		}
		//XStream xstream = new XStream();
		//System.out.println(xstream.toXML(pass));
		return pass;

			}
  	 
	}
	

