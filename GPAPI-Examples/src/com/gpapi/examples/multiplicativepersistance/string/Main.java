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


package com.gpapi.examples.multiplicativepersistance.string;

import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Executors;

import javax.swing.JFrame;
import javax.swing.SwingUtilities;

import com.gpapi.DataManager;
import com.gpapi.GenerationStatistics;
import com.gpapi.algorithm.GlobalFitnessAlgorithm;
import com.gpapi.algorithm.GlobalFitnessAlgorithmBuilder;
import com.gpapi.algorithm.breedingoperators.ParentSelection;
import com.gpapi.algorithm.generalpurposeoperators.KillOldest;
import com.gpapi.algorithm.generalpurposeoperators.Migration;
import com.gpapi.algorithm.naturalselectionstrategies.Random;
import com.gpapi.algorithm.naturalselectionstrategies.Tournament;
import com.gpapi.algorithm.stagnationdetectors.MinFitnessChange;
import com.gpapi.individuals.EvolvedIndividualBuilder;
import com.gpapi.individuals.cells.nucleuses.geneticoperators.PointTypingCrossover;
import com.gpapi.individuals.cells.nucleuses.geneticoperators.architecturealterationschemes.NoAlteration;
import com.gpapi.individuals.cells.nucleuses.geneticoperators.crossoverschemes.crossovernodesselectionschemes.SizeFairCrossoverNodesSelection;
import com.gpapi.individuals.cells.nucleuses.geneticoperators.crossoverschemes.crossoverplanningschemes.FrequencyCrossover;
import com.gpapi.individuals.cells.nucleuses.geneticoperators.mutationschemes.FrequencyMutation;
import com.gpapi.individuals.cells.nucleuses.geneticoperators.mutationschemes.pointmutationoperators.CompositePointMutationOperator;
import com.gpapi.individuals.cells.nucleuses.geneticoperators.mutationschemes.pointmutationoperators.Delegate;
import com.gpapi.individuals.cells.nucleuses.geneticoperators.mutationschemes.pointmutationoperators.PointMutationOperatorInterface;
import com.gpapi.individuals.cells.nucleuses.geneticoperators.mutationschemes.pointmutationoperators.Regenerate;
import com.gpapi.individuals.cells.nucleuses.nodes.AbstractNode;
import com.gpapi.individuals.cells.nucleuses.nodes.Constant;
import com.gpapi.individuals.cells.nucleuses.nodes.realfunctions.Floor;
import com.gpapi.individuals.cells.nucleuses.types.RealValue;



public class Main {

	public static void main(String[] args) throws InvocationTargetException, InterruptedException, ExecutionException, IOException {
		ArrayList<AbstractNode> globalExternalNodes = new ArrayList<AbstractNode>();
			globalExternalNodes.add(new Constant(RealValue.createWithIncrement(10)));
			globalExternalNodes.add(new EmptyDigitsList());
		
		ArrayList<AbstractNode> globalInternalNodes = new ArrayList<AbstractNode>();
			globalInternalNodes.add(new Concat());
			globalInternalNodes.add(new Floor());
		
		HashMap<PointMutationOperatorInterface,Integer> pointMutationOperators = new HashMap<PointMutationOperatorInterface,Integer>();
			//pointMutationOperators.put(new Regenerate(5), 1); Seem to find an optimum more often with this...
			pointMutationOperators.put(new Regenerate(), 1);
			pointMutationOperators.put(new Delegate(), 1);
		CompositePointMutationOperator pointMutationOperator = new CompositePointMutationOperator(pointMutationOperators);
		
		PointTypingCrossover geneticOperator = new PointTypingCrossover(
			new FrequencyCrossover(0.02), 
			new SizeFairCrossoverNodesSelection(), 
			new FrequencyMutation(pointMutationOperator, 0.01), 
			new NoAlteration(), 
			globalExternalNodes, 
			globalInternalNodes);
		
		MinFitnessChange stagnationDetector = new MinFitnessChange(3, 50, 1.0);
		
		GlobalFitnessAlgorithm algorithm = GlobalFitnessAlgorithmBuilder.create(
					new FitnessFunction(), 
					1000, 
					EvolvedIndividualBuilder.create(geneticOperator)
						.setInitRpbsSize(10)
						.setRpbsTypes(Arrays.asList(new DigitsList()))
						.setInitAdfsSize(10)
						.setAdfsTypes(Arrays.asList(new DigitsList(), new DigitsList(), new DigitsList()))
						.setAdfsArgsTypes(Arrays.asList(
								Arrays.asList(), 
								Arrays.asList(RealValue.create()), 
								Arrays.asList(RealValue.create(), RealValue.create())))
						.setHierarchicalAdfs(true)
						.build(), 
					2500, 
					5)
				.setNThreads(5)
				.setBreedingOperator(new ParentSelection(new Tournament(7)))
				.setGeneralPurposeOperator(new KillOldest(3, new Migration(0.03, 25, new Random(false), new Random(false), stagnationDetector)))
				.build();
		
		algorithm.addObserver(stagnationDetector);
		
		algorithm.getView().setMaxGenerationsDisplayed(200);
		
		SwingUtilities.invokeAndWait(() ->
			{
				JFrame mainFrame = new JFrame();
				mainFrame.setContentPane(algorithm.getView().getPanel());
				mainFrame.setTitle("Multiplicative persistance");
				mainFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
				mainFrame.pack();
				mainFrame.setVisible(true);
			});
		
		GenerationStatistics bestGeneration = Executors.newSingleThreadExecutor().submit(algorithm).get().get(0);
		DataManager.saveIndividual(bestGeneration.getBestIndividualFor(bestGeneration.getBestPopulation()), "multiplicativepersistance/best_string.ser");
	}
}
