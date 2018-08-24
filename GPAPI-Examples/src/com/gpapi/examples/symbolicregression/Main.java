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


package com.gpapi.examples.symbolicregression;

import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import javax.swing.JFrame;
import javax.swing.SwingUtilities;

import com.gpapi.DataManager;
import com.gpapi.GenerationStatistics;
import com.gpapi.algorithm.GlobalFitnessAlgorithm;
import com.gpapi.algorithm.GlobalFitnessAlgorithmBuilder;
import com.gpapi.algorithm.breedingoperators.ParentSelection;
import com.gpapi.algorithm.generalpurposeoperators.Colonization;
import com.gpapi.algorithm.generalpurposeoperators.KillOldest;
import com.gpapi.algorithm.generalpurposeoperators.Recycling;
import com.gpapi.algorithm.naturalselectionstrategies.Random;
import com.gpapi.algorithm.naturalselectionstrategies.Tournament;
import com.gpapi.algorithm.stagnationdetectors.MinFitnessChange;
import com.gpapi.individuals.EvolvedIndividualBuilder;
import com.gpapi.individuals.cells.nucleuses.geneticoperators.PointTypingCrossover;
import com.gpapi.individuals.cells.nucleuses.geneticoperators.architecturealterationschemes.NoAlteration;
import com.gpapi.individuals.cells.nucleuses.geneticoperators.crossoverschemes.crossovernodesselectionschemes.SizeFairCrossoverNodesSelection;
import com.gpapi.individuals.cells.nucleuses.geneticoperators.crossoverschemes.crossoverplanningschemes.FrequencyCrossover;
import com.gpapi.individuals.cells.nucleuses.geneticoperators.mutationschemes.FrequencyMutation;
import com.gpapi.individuals.cells.nucleuses.geneticoperators.mutationschemes.pointmutationoperators.Delegate;
import com.gpapi.individuals.cells.nucleuses.nodes.AbstractNode;
import com.gpapi.individuals.cells.nucleuses.nodes.Constant;
import com.gpapi.individuals.cells.nucleuses.nodes.realfunctions.Cos;
import com.gpapi.individuals.cells.nucleuses.nodes.realfunctions.Divide;
import com.gpapi.individuals.cells.nucleuses.nodes.realfunctions.Exp;
import com.gpapi.individuals.cells.nucleuses.nodes.realfunctions.Log;
import com.gpapi.individuals.cells.nucleuses.nodes.realfunctions.Mult;
import com.gpapi.individuals.cells.nucleuses.nodes.realfunctions.Sin;
import com.gpapi.individuals.cells.nucleuses.nodes.realfunctions.Subtract;
import com.gpapi.individuals.cells.nucleuses.nodes.realfunctions.Sum;
import com.gpapi.individuals.cells.nucleuses.types.RealValue;



public class Main {
	
	public static void main(String[] args) throws InvocationTargetException, InterruptedException, ExecutionException, IOException {
		ArrayList<AbstractNode> globalExternalNodes = new ArrayList<AbstractNode>();
			globalExternalNodes.add(new Constant(RealValue.createWithGenerationParameters(0.0, 1.0, 0.1), 1.0));
		
		ArrayList<AbstractNode> globalInternalNodes = new ArrayList<AbstractNode>();
			globalInternalNodes.add(new Sum());
			globalInternalNodes.add(new Subtract());
			globalInternalNodes.add(new Mult());
			globalInternalNodes.add(new Divide());
			globalInternalNodes.add(new Cos());
			globalInternalNodes.add(new Sin());
			globalInternalNodes.add(new Exp());
			globalInternalNodes.add(new Log());
		
		PointTypingCrossover geneticOperator = new PointTypingCrossover(
				new FrequencyCrossover(0.05), 
				new SizeFairCrossoverNodesSelection(), 
				new FrequencyMutation(new Delegate(), 0.02), 
				new NoAlteration(), 
				globalExternalNodes, 
				globalInternalNodes);
		
		MinFitnessChange shortStagnationDetector = new MinFitnessChange(3, 25, 0.1);
		MinFitnessChange longStagnationDetector = new MinFitnessChange(3, 100, 0.01);
		MinFitnessChange colonizationStagnationDetector = new MinFitnessChange(3, 25, 0.01);
		
		GlobalFitnessAlgorithm algorithm = GlobalFitnessAlgorithmBuilder.create(
					new FitnessFunction(), 
					1000, 
					EvolvedIndividualBuilder.create(geneticOperator)
						.setInitRpbsSize(25)
						.setRpbsTypes(Arrays.asList(RealValue.create()))
						.setRpbsArgsTypes(Arrays.asList(Arrays.asList(RealValue.create())))
						.setRpbsArgsNames(Arrays.asList(Arrays.asList("X")))
						/*.setInitAdfsSize(5)
						.setAdfsTypes(Arrays.asList(RealValue.create(), RealValue.create()))
						.setAdfsArgsTypes(Arrays.asList(
									Arrays.asList(RealValue.create(), RealValue.create()),
									Arrays.asList(RealValue.create(), RealValue.create())))
						.setHierarchicalAdfs(true)*/
						.build(), 
					5000, 
					4)
				.setNThreads(4)
				.setRecordingFrequency(1)
				.setBreedingOperator(new ParentSelection(new Tournament(5), 50, 0.5, -1))
				.setGeneralPurposeOperator(
						new KillOldest(5, 
								new Recycling(2, 50, shortStagnationDetector, 
										new Recycling(0, 100, longStagnationDetector, 
												new Colonization(0.05, 25, new Random(false), new Random(false), colonizationStagnationDetector)))))
				.build();
		
		algorithm.addObserver(shortStagnationDetector);
		algorithm.addObserver(longStagnationDetector);
		algorithm.addObserver(colonizationStagnationDetector);
		
		algorithm.getView().setMaxGenerationsDisplayed(75);
		algorithm.getView().setMaxDisplayableFitness(25.0);
		
		SwingUtilities.invokeAndWait(() ->
			{
				JFrame mainFrame = new JFrame();
				mainFrame.setContentPane(algorithm.getView().getPanel());
				mainFrame.setTitle("Symbolic regression");
				mainFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
				mainFrame.pack();
				mainFrame.setVisible(true);
			});
		
		ExecutorService executor = Executors.newSingleThreadExecutor();
		List<GenerationStatistics> results = executor.submit(algorithm).get();
		
		GenerationStatistics lastGeneration = results.get(results.size()-1);
		DataManager.saveIndividual(
				lastGeneration.getBestIndividualFor(lastGeneration.getBestPopulation()), 
				"symbolicRegression/results/evolvedFunction.ser");
		DataManager.exportSummary(results, "symbolicRegression/results/summary.csv");
	}
}
