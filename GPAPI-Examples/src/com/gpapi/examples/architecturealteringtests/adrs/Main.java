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


package com.gpapi.examples.architecturealteringtests.adrs;

import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Executors;

import javax.swing.JFrame;
import javax.swing.SwingUtilities;

import com.gpapi.algorithm.GlobalFitnessAlgorithm;
import com.gpapi.algorithm.GlobalFitnessAlgorithmBuilder;
import com.gpapi.algorithm.breedingoperators.ParentSelection;
import com.gpapi.algorithm.generalpurposeoperators.Colonization;
import com.gpapi.algorithm.generalpurposeoperators.KillOldest;
import com.gpapi.algorithm.naturalselectionstrategies.Random;
import com.gpapi.algorithm.naturalselectionstrategies.Tournament;
import com.gpapi.algorithm.stagnationdetectors.MinFitnessChange;
import com.gpapi.individuals.EvolvedIndividualBuilder;
import com.gpapi.individuals.cells.nucleuses.geneticoperators.PointTypingCrossover;
import com.gpapi.individuals.cells.nucleuses.geneticoperators.architecturealterationschemes.FrequencyArchitectureAlteration;
import com.gpapi.individuals.cells.nucleuses.geneticoperators.architecturealterationschemes.architecturealterationoperators.ArchitectureAlterationOperatorInterface;
import com.gpapi.individuals.cells.nucleuses.geneticoperators.architecturealterationschemes.architecturealterationoperators.CompositeArchitectureAlterationOperator;
import com.gpapi.individuals.cells.nucleuses.geneticoperators.architecturealterationschemes.architecturealterationoperators.adrs.ADRArgumentCreation;
import com.gpapi.individuals.cells.nucleuses.geneticoperators.architecturealterationschemes.architecturealterationoperators.adrs.ADRArgumentDeletion;
import com.gpapi.individuals.cells.nucleuses.geneticoperators.architecturealterationschemes.architecturealterationoperators.adrs.ADRArgumentDuplication;
import com.gpapi.individuals.cells.nucleuses.geneticoperators.architecturealterationschemes.architecturealterationoperators.adrs.ADRCreation;
import com.gpapi.individuals.cells.nucleuses.geneticoperators.architecturealterationschemes.architecturealterationoperators.adrs.ADRDeletion;
import com.gpapi.individuals.cells.nucleuses.geneticoperators.architecturealterationschemes.architecturealterationoperators.adrs.ADRDuplication;
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
import com.gpapi.individuals.cells.nucleuses.nodes.realfunctions.Divide;
import com.gpapi.individuals.cells.nucleuses.nodes.realfunctions.Mult;
import com.gpapi.individuals.cells.nucleuses.nodes.realfunctions.Subtract;
import com.gpapi.individuals.cells.nucleuses.nodes.realfunctions.Sum;
import com.gpapi.individuals.cells.nucleuses.types.RealValue;



public class Main {

	public static void main(String[] args) throws InvocationTargetException, InterruptedException, ExecutionException {
		ArrayList<AbstractNode> globalExternalNodes = new ArrayList<AbstractNode>();
			globalExternalNodes.add(new Constant(RealValue.createWithIncrement(1.0, 0.0)));
			globalExternalNodes.add(new Constant(RealValue.create()));
		
		ArrayList<AbstractNode> globalInternalNodes = new ArrayList<AbstractNode>();
			globalInternalNodes.add(new Sum());
			globalInternalNodes.add(new Subtract());
			globalInternalNodes.add(new Mult());
			globalInternalNodes.add(new Divide());
		
		HashMap<PointMutationOperatorInterface,Integer> pointMutationOperators = new HashMap<PointMutationOperatorInterface,Integer>();
			pointMutationOperators.put(new Regenerate(), 2);
			pointMutationOperators.put(new Delegate(), 2);
			pointMutationOperators.put(new Shrink(), 1);
		CompositePointMutationOperator pointMutationOperator = new CompositePointMutationOperator(pointMutationOperators);
		
		HashMap<ArchitectureAlterationOperatorInterface,Integer> architectureAlteringOperators = new HashMap<ArchitectureAlterationOperatorInterface,Integer>();
			architectureAlteringOperators.put(new ADRCreation(100, 25), 2);
			architectureAlteringOperators.put(new ADRDuplication(), 1);
			architectureAlteringOperators.put(new ADRDeletion(), 3);
			architectureAlteringOperators.put(new ADRArgumentCreation(), 2);
			architectureAlteringOperators.put(new ADRArgumentDuplication(), 1);
			architectureAlteringOperators.put(new ADRArgumentDeletion(), 3);
		CompositeArchitectureAlterationOperator architectureAlteringOperator = new CompositeArchitectureAlterationOperator(architectureAlteringOperators);
		
		PointTypingCrossover geneticOperator = new PointTypingCrossover(
				new FrequencyCrossover(0.02), 
				new SizeFairCrossoverNodesSelection(), 
				new FrequencyMutation(pointMutationOperator, 0.02), 
				new FrequencyArchitectureAlteration(architectureAlteringOperator, 0.05), 
				globalExternalNodes, 
				globalInternalNodes);
		
		MinFitnessChange stagnationDetector = new MinFitnessChange(3, 50, 0.1);
		
		GlobalFitnessAlgorithm algorithm = GlobalFitnessAlgorithmBuilder.create(
					new FitnessFunction(10, 10), 
					1000, 
					EvolvedIndividualBuilder.create(geneticOperator)
						.setInitRpbsSize(5)
						.setRpbsArgsTypes(Arrays.asList(Arrays.asList(RealValue.create())))
						.setRpbsArgsNames(Arrays.asList(Arrays.asList("N")))
						/*.setInitAdrsSize(5)
						.setAdrsTypes(Arrays.asList(RealValue.create()))
						.setAdrsArgsTypes(Arrays.asList(Arrays.asList(RealValue.create())))
						.setAdrsMaxCalls(Arrays.asList(100))
						.setAdrsMaxRecursionDepths(Arrays.asList(25))*/
						.setHierarchicalAdrs(true)
						.build(), 
					5000, 
					4)
				.setNThreads(4)
				.setBreedingOperator(new ParentSelection(new Tournament(10)))
				.setGeneralPurposeOperator(new KillOldest(5, new Colonization(0.25, 25, new Random(false), new Random(false), stagnationDetector)))
				.build();
		
		algorithm.addObserver(stagnationDetector);
		
		algorithm.getView().setMaxDisplayableFitness(50.0);
		
		SwingUtilities.invokeAndWait(() ->
			{
				JFrame mainFrame = new JFrame();
				mainFrame.setContentPane(algorithm.getView().getPanel());
				mainFrame.setTitle("Architecture altering tests (Factorial)");
				mainFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
				mainFrame.pack();
				mainFrame.setVisible(true);
			});
		
		Executors.newSingleThreadExecutor().submit(algorithm).get();
	}
}
