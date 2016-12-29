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


package com.gpapi.examples.architecturealteringtests.adis;

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
import com.gpapi.individuals.cells.nucleuses.geneticoperators.architecturealterationschemes.architecturealterationoperators.adis.ADIArgumentCreation;
import com.gpapi.individuals.cells.nucleuses.geneticoperators.architecturealterationschemes.architecturealterationoperators.adis.ADIArgumentDeletion;
import com.gpapi.individuals.cells.nucleuses.geneticoperators.architecturealterationschemes.architecturealterationoperators.adis.ADIArgumentDuplication;
import com.gpapi.individuals.cells.nucleuses.geneticoperators.architecturealterationschemes.architecturealterationoperators.adis.ADICreation;
import com.gpapi.individuals.cells.nucleuses.geneticoperators.architecturealterationschemes.architecturealterationoperators.adis.ADIDeletion;
import com.gpapi.individuals.cells.nucleuses.geneticoperators.architecturealterationschemes.architecturealterationoperators.adis.ADIDuplication;
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
			architectureAlteringOperators.put(new ADICreation(
					1, 
					Arrays.asList(FitnessFunction.ADIS_FIELDS_TYPES), 
					Arrays.asList(FitnessFunction.ADIS_FIELDS_NAMES)), 2);
			architectureAlteringOperators.put(new ADIDuplication(), 1);
			architectureAlteringOperators.put(new ADIDeletion(), 3);
			architectureAlteringOperators.put(new ADIArgumentCreation(), 2);
			architectureAlteringOperators.put(new ADIArgumentDuplication(), 1);
			architectureAlteringOperators.put(new ADIArgumentDeletion(), 3);
		CompositeArchitectureAlterationOperator architectureAlteringOperator = new CompositeArchitectureAlterationOperator(architectureAlteringOperators);
		
		PointTypingCrossover geneticOperator = new PointTypingCrossover(
				new FrequencyCrossover(0.02), 
				new SizeFairCrossoverNodesSelection(), 
				new FrequencyMutation(pointMutationOperator, 0.02), 
				new FrequencyArchitectureAlteration(architectureAlteringOperator, 0.10), 
				globalExternalNodes, 
				globalInternalNodes);
		
		MinFitnessChange stagnationDetector = new MinFitnessChange(3, 50, 5.0);
		
		GlobalFitnessAlgorithm algorithm = GlobalFitnessAlgorithmBuilder.create(
					new FitnessFunction(5.0, 10, 100), 
					1000, 
					EvolvedIndividualBuilder.create(geneticOperator)
						.setInitRpbsSize(10)
						.setRpbsTypes(Arrays.asList(RealValue.create(), RealValue.create()))
						.setRpbsArgsTypes(Arrays.asList(Arrays.asList(RealValue.create()), Arrays.asList(RealValue.create())))
						.setRpbsArgsNames(Arrays.asList(Arrays.asList("LEN"), Arrays.asList("LEN")))
						// ADIS
						/*.setInitAdisSize(10)
						.setAdisTypes(Arrays.asList(RealValue.create()))
						.setAdisExternalArgsTypes(Arrays.asList(Arrays.asList(RealValue.create())))
						.setAdisCollectionIds(Arrays.asList(0))
						.setAdisFieldsTypes(Arrays.asList(FitnessFunction.Series.FIELDS_TYPES))
						.setAdisFieldsNames(Arrays.asList(FitnessFunction.Series.FIELDS_NAMES))
						.setAdisMaxCalls(Arrays.asList(1))
						.setHierarchicalAdis(true)*/
						// ADSS
						.setAdssStorageTypes(Arrays.asList(ADS.Type.Stack, ADS.Type.Stack))
						.setAdssDataTypes(Arrays.asList(RealValue.create(), RealValue.create()))
						.setAdssMaxSizes(Arrays.asList(5, 5))
						.build(), 
					1000, 
					3)
				.setNThreads(3)
				.setBreedingOperator(new ParentSelection(new Tournament(5)))
				.setGeneralPurposeOperator(new KillOldest(5, new Colonization(0.25, 25, new Random(false), new Random(false), stagnationDetector)))
				.build();
		
		algorithm.addObserver(stagnationDetector);
		
		algorithm.getView().setMaxDisplayableFitness(150.0);
		
		SwingUtilities.invokeAndWait(() ->
			{
				JFrame mainFrame = new JFrame();
				mainFrame.setContentPane(algorithm.getView().getPanel());
				mainFrame.setTitle("Architecture altering tests (Averages and Products)");
				mainFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
				mainFrame.pack();
				mainFrame.setVisible(true);
			});
		
		Executors.newSingleThreadExecutor().submit(algorithm).get();
	}
}
