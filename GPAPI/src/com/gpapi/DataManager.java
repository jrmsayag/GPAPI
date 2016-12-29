/* ===============================================================
 * GPAPI : A Genetic-Programming library for the Java(tm) platform
 * ===============================================================
 *
 * (C) Copyright 2015-2016, by Jeremy Sayag and Contributors.
 *
 * Project Info:  https://github.com/jrmsayag/gpapi
 * 
 * This file is part of GPAPI.
 *
 * This library is free software: you can redistribute it and/or modify it
 * under the terms of the GNU Lesser General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This library is distributed in the hope that it will be useful, but
 * WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY
 * or FITNESS FOR A PARTICULAR PURPOSE. See the GNU Lesser General Public
 * License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this library. If not, see http://www.gnu.org/licenses/.
 *
 * [Oracle and Java are registered trademarks of Oracle and/or its affiliates. 
 * Other names may be trademarks of their respective owners.]
 *
 * ---------------
 * DataManager.java
 * ---------------
 * (C) Copyright 2015-2016, by Jeremy Sayag and Contributors.
 *
 * Original Author:  Jeremy Sayag;
 * Contributor(s):   ;
 * 
 */


package com.gpapi;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.PrintWriter;
import java.io.Serializable;
import java.util.List;

import com.gpapi.individuals.EvolvedIndividual;



public final class DataManager {
	
	public static final void saveIndividual(EvolvedIndividual individual, String to) throws IOException {
		genericSave(individual, to);
	}
	public static final EvolvedIndividual loadIndividual(String from) 
			throws FileNotFoundException, IOException, ClassNotFoundException {
		return (EvolvedIndividual) genericLoad(from);
	}
	
	public static final void savePopulation(Population population, String to) throws IOException {
		genericSave(population, to);
	}
	public static final Population loadPopulation(String from) 
			throws FileNotFoundException, IOException, ClassNotFoundException {
		return (Population) genericLoad(from);
	}
	
	public static final void saveGenerationSnapshot(GenerationSnapshot snapshot, String to) throws IOException {
		genericSave(snapshot, to);
	}
	public static final GenerationSnapshot loadGenerationSnapshot(String from)
			throws FileNotFoundException, IOException, ClassNotFoundException {
		return (GenerationSnapshot) genericLoad(from);
	}
	
	public static final void saveGenerationStatistics(GenerationStatistics statistics, String to) throws IOException {
		genericSave(statistics, to);
	}
	public static final GenerationStatistics loadGenerationStatistics(String from)
			throws FileNotFoundException, IOException, ClassNotFoundException {
		return (GenerationStatistics) genericLoad(from);
	}
	
	public static final void genericSave(Serializable object, String to) throws IOException {
		FileOutputStream fout = new FileOutputStream(to);
		ObjectOutputStream out = new ObjectOutputStream(fout);
		out.writeObject(object);
		out.close();
	}
	public static final Object genericLoad(String from) 
			throws FileNotFoundException, IOException, ClassNotFoundException {
		ObjectInputStream in = new ObjectInputStream(new FileInputStream(from));
		Object object = in.readObject();
		in.close();
		return object;
	}
	
	/**
	 * TODO : Description.
	 * 
	 * Note : Every GenerationStatistics must refer to the same populations, in the 
	 * same order in the list returned by GenerationStatistics.getPopulations(),
	 * in order for the exported results to be coherent.
	 * 
	 * @param results
	 * @param filename
	 * @throws FileNotFoundException
	 */
	public static final void exportSummary(List<GenerationStatistics> results, String filename) throws FileNotFoundException {
		if(filename == null)
			throw new NullPointerException("A filename must be specified!");
		else if(results == null)
			throw new NullPointerException("Results can't be null!");
		else if(results.isEmpty())
			throw new IllegalArgumentException("Results can't be empty!");
		
		PrintWriter writer = new PrintWriter(filename);
		
		int nPopulations = results.get(0).getPopulations().size();
		
		writer.print("generation,");
		writer.print("best_individual_population,");
		writer.print("best_individual_raw_fitness,");
		writer.print("best_individual_structural_complexity,");
		writer.print("best_individual_execution_cost,");
		for(int i = 0; i < nPopulations; i++){
			writer.print("population_" + i + "_median_fitness,");
			writer.print("population_" + i + "_median_structural_complexity,");
			writer.print("population_" + i + "_median_execution_cost,");
			writer.print("population_" + i + "_best_individual_raw_fitness,");
			writer.print("population_" + i + "_best_individual_structural_complexity,");
			writer.print("population_" + i + "_best_individual_execution_cost");
			if(i < nPopulations-1)
				writer.print(",");
			else
				writer.println();
		}
		
		for(GenerationStatistics generation : results){
			writer.print(generation.getGeneration());
			writer.print(",");
			writer.print(generation.getPopulations().indexOf(generation.getBestPopulation()));
			writer.print(",");
			writer.print(generation.getBestIndividualFor(generation.getBestPopulation()).getRawFitness());
			writer.print(",");
			writer.print(generation.getBestIndividualFor(generation.getBestPopulation()).getEggCell().getNucleus().getTotalSize());
			writer.print(",");
			writer.print(generation.getBestIndividualFor(generation.getBestPopulation()).getLastExecutionCost());
			writer.print(",");
			for(int i = 0; i < nPopulations; i++){
				writer.print(generation.getMedianFitnessFor(generation.getPopulations().get(i)));
				writer.print(",");
				writer.print(generation.getMedianStructuralComplexityFor(generation.getPopulations().get(i)));
				writer.print(",");
				writer.print(generation.getMedianExecutionCostFor(generation.getPopulations().get(i)));
				writer.print(",");
				writer.print(generation.getBestIndividualFor(generation.getPopulations().get(i)).getRawFitness());
				writer.print(",");
				writer.print(generation.getBestIndividualFor(generation.getPopulations().get(i)).getEggCell().getNucleus().getTotalSize());
				writer.print(",");
				writer.print(generation.getBestIndividualFor(generation.getPopulations().get(i)).getLastExecutionCost());
				if(i < generation.getPopulations().size()-1)
					writer.print(",");
				else
					writer.println();
			}
		}
		
		writer.close();
	}
}
