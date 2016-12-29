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
 * Main.java
 * ---------------
 * (C) Copyright 2015-2016, by Jeremy Sayag and Contributors.
 *
 * Original Author:  Jeremy Sayag;
 * Contributor(s):   ;
 * 
 */


package com.gpapi.examples.lexicaseselectiontest.datachallenge;

import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadLocalRandom;

import javax.swing.JFrame;
import javax.swing.SwingUtilities;

import com.gpapi.DataManager;
import com.gpapi.GenerationStatistics;
import com.gpapi.algorithm.GlobalFitnessAlgorithm;
import com.gpapi.algorithm.GlobalFitnessAlgorithmBuilder;
import com.gpapi.algorithm.breedingoperators.OffspringSelection;
import com.gpapi.algorithm.naturalselectionstrategies.Lexicase;
import com.gpapi.individuals.EvolvedIndividualBuilder;
import com.gpapi.individuals.cells.nucleuses.geneticoperators.PointTypingCrossover;
import com.gpapi.individuals.cells.nucleuses.geneticoperators.architecturealterationschemes.NoAlteration;
import com.gpapi.individuals.cells.nucleuses.geneticoperators.crossoverschemes.crossovernodesselectionschemes.SizeFairCrossoverNodesSelection;
import com.gpapi.individuals.cells.nucleuses.geneticoperators.crossoverschemes.crossoverplanningschemes.NPointsCrossover;
import com.gpapi.individuals.cells.nucleuses.geneticoperators.mutationschemes.FrequencyMutation;
import com.gpapi.individuals.cells.nucleuses.geneticoperators.mutationschemes.pointmutationoperators.CompositePointMutationOperator;
import com.gpapi.individuals.cells.nucleuses.geneticoperators.mutationschemes.pointmutationoperators.Delegate;
import com.gpapi.individuals.cells.nucleuses.geneticoperators.mutationschemes.pointmutationoperators.PointMutationOperatorInterface;
import com.gpapi.individuals.cells.nucleuses.geneticoperators.mutationschemes.pointmutationoperators.Regenerate;
import com.gpapi.individuals.cells.nucleuses.geneticoperators.mutationschemes.pointmutationoperators.Shrink;
import com.gpapi.individuals.cells.nucleuses.nodes.AbstractNode;
import com.gpapi.individuals.cells.nucleuses.nodes.Constant;
import com.gpapi.individuals.cells.nucleuses.nodes.booleanfunctions.And;
import com.gpapi.individuals.cells.nucleuses.nodes.booleanfunctions.GreaterThan;
import com.gpapi.individuals.cells.nucleuses.nodes.booleanfunctions.Not;
import com.gpapi.individuals.cells.nucleuses.nodes.booleanfunctions.Or;
import com.gpapi.individuals.cells.nucleuses.nodes.realfunctions.Abs;
import com.gpapi.individuals.cells.nucleuses.nodes.realfunctions.Cos;
import com.gpapi.individuals.cells.nucleuses.nodes.realfunctions.Divide;
import com.gpapi.individuals.cells.nucleuses.nodes.realfunctions.Mult;
import com.gpapi.individuals.cells.nucleuses.nodes.realfunctions.Sin;
import com.gpapi.individuals.cells.nucleuses.nodes.realfunctions.Subtract;
import com.gpapi.individuals.cells.nucleuses.nodes.realfunctions.Sum;
import com.gpapi.individuals.cells.nucleuses.nodes.realfunctions.Ternary;
import com.gpapi.individuals.cells.nucleuses.types.BooleanValue;
import com.gpapi.individuals.cells.nucleuses.types.RealValue;



public class Main {

	public static void main(String[] args) throws IOException, InvocationTargetException, InterruptedException, ExecutionException {
		
		// Loading data.
		
		Parser.parse("datachallenge/data.csv");
		
		List<Sample> trainSamples = Parser.getTrainSamples();
		//List<Sample> testSamples = Parser.getSubmitSamples();
		
		int nSamples = 1000;
		ThreadLocalRandom generator = ThreadLocalRandom.current();
		ArrayList<Sample> restrictedTrainSamples = new ArrayList<Sample>(nSamples);
		for(int i = 0; i < nSamples; i++)
			restrictedTrainSamples.add(trainSamples.get(generator.nextInt(trainSamples.size())));
		
		
		// Creating codons.
		
		ArrayList<AbstractNode> globalExternalNodes = new ArrayList<AbstractNode>();
			globalExternalNodes.add(new Constant(RealValue.create()));
			globalExternalNodes.add(new Constant(BooleanValue.create()));
			
		ArrayList<AbstractNode> globalInternalNodes = new ArrayList<AbstractNode>();
			globalInternalNodes.add(new Sum());
			globalInternalNodes.add(new Subtract());
			globalInternalNodes.add(new Mult());
			globalInternalNodes.add(new Divide());
			
			globalInternalNodes.add(new Abs());
			
			globalInternalNodes.add(new Cos());
			globalInternalNodes.add(new Sin());
			
			globalInternalNodes.add(new Ternary());
			
			globalInternalNodes.add(new And());
			globalInternalNodes.add(new Or());
			globalInternalNodes.add(new Not());
			
			globalInternalNodes.add(new GreaterThan());
			globalInternalNodes.add(new GreaterThan());
			globalInternalNodes.add(new GreaterThan());
		
		
		// Creating genetic operator.
		
		HashMap<PointMutationOperatorInterface,Integer> pointMutationOperators = new HashMap<PointMutationOperatorInterface,Integer>();
			pointMutationOperators.put(new Regenerate(), 2);
			pointMutationOperators.put(new Delegate(), 2);
			pointMutationOperators.put(new Shrink(), 1);
		CompositePointMutationOperator pointMutationOperator = new CompositePointMutationOperator(pointMutationOperators);
		
		PointTypingCrossover geneticOperator = new PointTypingCrossover(
				new NPointsCrossover(1), 
				new SizeFairCrossoverNodesSelection(), 
				new FrequencyMutation(pointMutationOperator, 0.05), 
				new NoAlteration(), 
				globalExternalNodes, 
				globalInternalNodes);
		
		
		// Creating the algorithm.
		
		GlobalFitnessAlgorithm algorithm = GlobalFitnessAlgorithmBuilder.create(
					new FitnessFunction(restrictedTrainSamples), 
					500, 
					EvolvedIndividualBuilder.create(geneticOperator)
						.setMinInitRpbsSize(15)
						.setMaxInitRpbsSize(25)
						.setRpbsTypes(Arrays.asList(RealValue.create()))
						.setRpbsArgsTypes(Arrays.asList(Sample.argsTypes))
						.setRpbsArgsNames(Arrays.asList(Sample.argsNames))
						/*.setInitAdfsSize(5)
						.setAdfsTypes(Arrays.asList(RealValue.create(), RealValue.create(), RealValue.create()))
						.setAdfsArgsTypes(Arrays.asList(
									Arrays.asList(RealValue.create(), RealValue.create()),
									Arrays.asList(RealValue.create(), RealValue.create(), RealValue.create(), RealValue.create()),
									Arrays.asList(RealValue.create(), RealValue.create(), RealValue.create(), RealValue.create())))*/
						.build(),
					500, 
					4)
				.setNThreads(8)
				.setBreedingOperator(new OffspringSelection(new Lexicase(Integer.MAX_VALUE, Integer.MAX_VALUE, 0.5, true), 10, 0.5, 1, 0.75, 50))
				.build();
		algorithm.getView().setMaxGenerationsDisplayed(25);
		algorithm.getView().setMaxDisplayableFitness(1.0);
		algorithm.getView().setMinDisplayableFitness(0.5);
		
		
		// Launching !
		
		SwingUtilities.invokeAndWait(() ->
			{
				JFrame mainFrame = new JFrame();
				mainFrame.setContentPane(algorithm.getView().getPanel());
				mainFrame.setTitle("Lexicase selection test (Shots success predictor)");
				mainFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
				mainFrame.pack();
				mainFrame.setVisible(true);
			});
	
		ExecutorService executor = Executors.newSingleThreadExecutor();
		List<GenerationStatistics> results = executor.submit(algorithm).get();
		
		
		// Saving.
		
		GenerationStatistics lastGeneration = results.get(results.size()-1);
		DataManager.saveIndividual(
				lastGeneration.getBestIndividualFor(lastGeneration.getBestPopulation()), 
				"datachallenge/results/evolvedPredictor.ser");
	}
}
