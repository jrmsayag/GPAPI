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


package com.gpapi.examples.architecturealteringtests.adls;

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
import com.gpapi.individuals.cells.nucleuses.geneticoperators.architecturealterationschemes.architecturealterationoperators.adls.ADLArgumentCreation;
import com.gpapi.individuals.cells.nucleuses.geneticoperators.architecturealterationschemes.architecturealterationoperators.adls.ADLArgumentDeletion;
import com.gpapi.individuals.cells.nucleuses.geneticoperators.architecturealterationschemes.architecturealterationoperators.adls.ADLArgumentDuplication;
import com.gpapi.individuals.cells.nucleuses.geneticoperators.architecturealterationschemes.architecturealterationoperators.adls.ADLCreation;
import com.gpapi.individuals.cells.nucleuses.geneticoperators.architecturealterationschemes.architecturealterationoperators.adls.ADLDeletion;
import com.gpapi.individuals.cells.nucleuses.geneticoperators.architecturealterationschemes.architecturealterationoperators.adls.ADLDuplication;
import com.gpapi.individuals.cells.nucleuses.geneticoperators.crossoverschemes.crossovernodesselectionschemes.SizeFairCrossoverNodesSelection;
import com.gpapi.individuals.cells.nucleuses.geneticoperators.crossoverschemes.crossoverplanningschemes.FrequencyCrossover;
import com.gpapi.individuals.cells.nucleuses.geneticoperators.mutationschemes.FrequencyMutation;
import com.gpapi.individuals.cells.nucleuses.geneticoperators.mutationschemes.pointmutationoperators.CompositePointMutationOperator;
import com.gpapi.individuals.cells.nucleuses.geneticoperators.mutationschemes.pointmutationoperators.Delegate;
import com.gpapi.individuals.cells.nucleuses.geneticoperators.mutationschemes.pointmutationoperators.PointMutationOperatorInterface;
import com.gpapi.individuals.cells.nucleuses.geneticoperators.mutationschemes.pointmutationoperators.Regenerate;
import com.gpapi.individuals.cells.nucleuses.geneticoperators.mutationschemes.pointmutationoperators.Shrink;
import com.gpapi.individuals.cells.nucleuses.modules.adss.ADS;
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
			architectureAlteringOperators.put(new ADLCreation(1, 10), 2);
			architectureAlteringOperators.put(new ADLDuplication(), 1);
			architectureAlteringOperators.put(new ADLDeletion(), 3);
			architectureAlteringOperators.put(new ADLArgumentCreation(), 2);
			architectureAlteringOperators.put(new ADLArgumentDuplication(), 1);
			architectureAlteringOperators.put(new ADLArgumentDeletion(), 3);
		CompositeArchitectureAlterationOperator architectureAlteringOperator = new CompositeArchitectureAlterationOperator(architectureAlteringOperators);
		
		PointTypingCrossover geneticOperator = new PointTypingCrossover(
				new FrequencyCrossover(0.02), 
				new SizeFairCrossoverNodesSelection(), 
				new FrequencyMutation(pointMutationOperator, 0.02), 
				new FrequencyArchitectureAlteration(architectureAlteringOperator, 0.05), 
				globalExternalNodes, 
				globalInternalNodes);
		
		MinFitnessChange stagnationDetector = new MinFitnessChange(3, 50, 5.0);
		
		GlobalFitnessAlgorithm algorithm = GlobalFitnessAlgorithmBuilder.create(
					new FitnessFunction(5.0, 25, 5), 
					1000, 
					EvolvedIndividualBuilder.create(geneticOperator)
						.setInitRpbsSize(10)
						.setRpbsArgsTypes(Arrays.asList(Arrays.asList(RealValue.create(), RealValue.create())))
						.setRpbsArgsNames(Arrays.asList(Arrays.asList("x", "power")))
						// ADLS
						.setInitAdlsSize(10)
						.setAdlsInitializationTypes(Arrays.asList(RealValue.create()))
						.setAdlsBodyTypes(Arrays.asList(RealValue.create()))
						.setAdlsArgsTypes(Arrays.asList(Arrays.asList(RealValue.create(), RealValue.create())))
						.setAdlsMaxCalls(Arrays.asList(1))
						.setAdlsMaxIterations(Arrays.asList(10))
						.setHierarchicalAdls(true)
						// ADSS
						.setAdssStorageTypes(Arrays.asList(ADS.Type.Variable))
						.setAdssDataTypes(Arrays.asList(RealValue.create()))
						.setAdssMaxSizes(Arrays.asList(5))
						.build(), 
					1000, 
					4)
				.setNThreads(4)
				.setBreedingOperator(new ParentSelection(new Tournament(10)))
				.setGeneralPurposeOperator(new KillOldest(5, new Colonization(0.25, 25, new Random(false), new Random(false), stagnationDetector)))
				.build();
		
		algorithm.addObserver(stagnationDetector);
		
		algorithm.getView().setMaxDisplayableFitness(150.0);
		
		SwingUtilities.invokeAndWait(() ->
			{
				JFrame mainFrame = new JFrame();
				mainFrame.setContentPane(algorithm.getView().getPanel());
				mainFrame.setTitle("Architecture altering tests (Powers)");
				mainFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
				mainFrame.pack();
				mainFrame.setVisible(true);
			});
		
		Executors.newSingleThreadExecutor().submit(algorithm).get();
	}
}
