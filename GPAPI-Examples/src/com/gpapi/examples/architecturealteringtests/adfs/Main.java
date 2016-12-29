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


package com.gpapi.examples.architecturealteringtests.adfs;

import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
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
import com.gpapi.algorithm.generalpurposeoperators.KillOldest;
import com.gpapi.algorithm.generalpurposeoperators.PeriodicMigration;
import com.gpapi.algorithm.naturalselectionstrategies.Random;
import com.gpapi.algorithm.naturalselectionstrategies.Tournament;
import com.gpapi.individuals.EvolvedIndividualBuilder;
import com.gpapi.individuals.cells.nucleuses.geneticoperators.PointTypingCrossover;
import com.gpapi.individuals.cells.nucleuses.geneticoperators.architecturealterationschemes.FrequencyArchitectureAlteration;
import com.gpapi.individuals.cells.nucleuses.geneticoperators.architecturealterationschemes.architecturealterationoperators.ArchitectureAlterationOperatorInterface;
import com.gpapi.individuals.cells.nucleuses.geneticoperators.architecturealterationschemes.architecturealterationoperators.CompositeArchitectureAlterationOperator;
import com.gpapi.individuals.cells.nucleuses.geneticoperators.architecturealterationschemes.architecturealterationoperators.adfs.ADFArgumentCreation;
import com.gpapi.individuals.cells.nucleuses.geneticoperators.architecturealterationschemes.architecturealterationoperators.adfs.ADFArgumentDeletion;
import com.gpapi.individuals.cells.nucleuses.geneticoperators.architecturealterationschemes.architecturealterationoperators.adfs.ADFArgumentDuplication;
import com.gpapi.individuals.cells.nucleuses.geneticoperators.architecturealterationschemes.architecturealterationoperators.adfs.ADFCreation;
import com.gpapi.individuals.cells.nucleuses.geneticoperators.architecturealterationschemes.architecturealterationoperators.adfs.ADFDeletion;
import com.gpapi.individuals.cells.nucleuses.geneticoperators.architecturealterationschemes.architecturealterationoperators.adfs.ADFDuplication;
import com.gpapi.individuals.cells.nucleuses.geneticoperators.crossoverschemes.crossovernodesselectionschemes.SizeFairCrossoverNodesSelection;
import com.gpapi.individuals.cells.nucleuses.geneticoperators.crossoverschemes.crossoverplanningschemes.FrequencyCrossover;
import com.gpapi.individuals.cells.nucleuses.geneticoperators.mutationschemes.FrequencyMutation;
import com.gpapi.individuals.cells.nucleuses.geneticoperators.mutationschemes.pointmutationoperators.CompositePointMutationOperator;
import com.gpapi.individuals.cells.nucleuses.geneticoperators.mutationschemes.pointmutationoperators.Delegate;
import com.gpapi.individuals.cells.nucleuses.geneticoperators.mutationschemes.pointmutationoperators.PointMutationOperatorInterface;
import com.gpapi.individuals.cells.nucleuses.geneticoperators.mutationschemes.pointmutationoperators.Regenerate;
import com.gpapi.individuals.cells.nucleuses.geneticoperators.mutationschemes.pointmutationoperators.Shrink;
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
			globalExternalNodes.add(new Constant(RealValue.create()));
		
		ArrayList<AbstractNode> globalInternalNodes = new ArrayList<AbstractNode>();
			globalInternalNodes.add(new Sum());
			globalInternalNodes.add(new Subtract());
			globalInternalNodes.add(new Mult());
			globalInternalNodes.add(new Divide());
			globalInternalNodes.add(new Cos());
			globalInternalNodes.add(new Sin());
			globalInternalNodes.add(new Exp());
			globalInternalNodes.add(new Log());
		
		HashMap<PointMutationOperatorInterface,Integer> pointMutationOperators = new HashMap<PointMutationOperatorInterface,Integer>();
			pointMutationOperators.put(new Regenerate(), 2);
			pointMutationOperators.put(new Delegate(), 2);
			pointMutationOperators.put(new Shrink(), 1);
		CompositePointMutationOperator pointMutationOperator = new CompositePointMutationOperator(pointMutationOperators);
		
		HashMap<ArchitectureAlterationOperatorInterface,Integer> architectureAlteringOperators = new HashMap<ArchitectureAlterationOperatorInterface,Integer>();
			architectureAlteringOperators.put(new ADFCreation(), 2);
			architectureAlteringOperators.put(new ADFDuplication(), 1);
			architectureAlteringOperators.put(new ADFDeletion(), 3);
			architectureAlteringOperators.put(new ADFArgumentCreation(), 2);
			architectureAlteringOperators.put(new ADFArgumentDuplication(), 1);
			architectureAlteringOperators.put(new ADFArgumentDeletion(), 3);
		CompositeArchitectureAlterationOperator architectureAlteringOperator = new CompositeArchitectureAlterationOperator(architectureAlteringOperators);
		
		PointTypingCrossover geneticOperator = new PointTypingCrossover(
			new FrequencyCrossover(0.02), 
			new SizeFairCrossoverNodesSelection(), 
			new FrequencyMutation(pointMutationOperator, 0.02), 
			new FrequencyArchitectureAlteration(architectureAlteringOperator, 0.05), 
			globalExternalNodes, 
			globalInternalNodes);
		
		GlobalFitnessAlgorithm algorithm = GlobalFitnessAlgorithmBuilder.create(
					new FitnessFunction(), 
					500, 
					EvolvedIndividualBuilder.create(geneticOperator)
						.setMinInitRpbsSize(15)
						.setMaxInitRpbsSize(35)
						.setRpbsTypes(Arrays.asList(RealValue.create()))
						.setRpbsArgsTypes(Arrays.asList(Arrays.asList(RealValue.create())))
						.setRpbsArgsNames(Arrays.asList(Arrays.asList("X")))
						/*.setInitAdfsSize(10)
						.setAdfsTypes(Arrays.asList(RealValue.create()))
						.setAdfsArgsTypes(Arrays.asList(Arrays.asList(RealValue.create())))*/
						.setHierarchicalAdfs(true)
						.build(), 
					2500, 
					4)
				.setNThreads(4)
				.setRecordingFrequency(1)
				.setBreedingOperator(new ParentSelection(new Tournament(7)))
				.setGeneralPurposeOperator(new KillOldest(5, new PeriodicMigration(75, 0.05, new Random(false))))
				.build();
		algorithm.getView().setMaxGenerationsDisplayed(50);
		algorithm.getView().setMaxDisplayableFitness(50.0);
		
		SwingUtilities.invokeAndWait(() -> 
			{
				JFrame mainFrame = new JFrame();
				mainFrame.setContentPane(algorithm.getView().getPanel());
				mainFrame.setTitle("Architecture altering tests (Symbolic regression)");
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
