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
 * GeneticOperator.java
 * ---------------
 * (C) Copyright 2015-2016, by Jeremy Sayag and Contributors.
 *
 * Original Author:  Jeremy Sayag;
 * Contributor(s):   ;
 * 
 */


package com.gpapi.examples.painting;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map.Entry;
import java.util.concurrent.ThreadLocalRandom;

import com.gpapi.examples.painting.nodes.external.ColorNode;
import com.gpapi.examples.painting.nodes.external.TerminationNode;
import com.gpapi.examples.painting.nodes.external.EmptyVertexList;
import com.gpapi.examples.painting.nodes.external.VertexNode;
import com.gpapi.examples.painting.nodes.internal.Concat;
import com.gpapi.examples.painting.nodes.internal.PaintOval;
import com.gpapi.examples.painting.nodes.internal.PaintPolygon;
import com.gpapi.examples.painting.nodes.types.Color;
import com.gpapi.examples.painting.nodes.types.Vertex;
import com.gpapi.examples.painting.nodes.types.Void;
import com.gpapi.individuals.cells.nucleuses.Nucleus;
import com.gpapi.individuals.cells.nucleuses.geneticoperators.GeneticOperatorInterface;
import com.gpapi.individuals.cells.nucleuses.geneticoperators.crossoverschemes.crossovernodesselectionschemes.StandardCrossoverNodesSelection;
import com.gpapi.individuals.cells.nucleuses.geneticoperators.mutationschemes.FrequencyMutation;
import com.gpapi.individuals.cells.nucleuses.geneticoperators.mutationschemes.pointmutationoperators.Delegate;
import com.gpapi.individuals.cells.nucleuses.modules.AbstractProxyNode;
import com.gpapi.individuals.cells.nucleuses.modules.RPB;
import com.gpapi.individuals.cells.nucleuses.nodes.AbstractNode;
import com.gpapi.individuals.cells.nucleuses.types.AbstractType;



public final class GeneticOperator implements GeneticOperatorInterface {
	
	private static final long serialVersionUID = -5618784262547182897L;
	
	public static enum Shapes {
		Polygons,
		Ovals,
		Both
	}
	
	private final int width;
	public final int getWidth() {
		return width;
	}
	
	private final int height;
	public final int getHeight() {
		return height;
	}
	
	private final StandardCrossoverNodesSelection crossoverScheme;
	private final FrequencyMutation mutationScheme;
	
	private final Shapes potentialShapes;
	
	private final int maxPolygonVertices;
	
	private final int initPolygonVertices;
	
	private final int alpha;
	
	private final int colorIncrement;
	
	private final int vertexIncrement;
	
	private final int nCrossoverNodes;
	
	
	
	public GeneticOperator(
			Shapes potentialShapes, 
			int maxPolygonVertices, 
			int initPolygonVertices, 
			int alpha,
			int colorIncrement, 
			int vertexIncrement, 
			int width, 
			int height, 
			int nCrossoverNodes, 
			double mutationFrequency){
		
		crossoverScheme = new StandardCrossoverNodesSelection();
		mutationScheme = new FrequencyMutation(new Delegate(), mutationFrequency);
		
		this.potentialShapes = potentialShapes;
		this.maxPolygonVertices = maxPolygonVertices;
		this.initPolygonVertices = initPolygonVertices;
		this.alpha = alpha;
		this.colorIncrement = colorIncrement;
		this.vertexIncrement = vertexIncrement;
		this.width = width;
		this.height = height;
		this.nCrossoverNodes = nCrossoverNodes;
	}
	
	
	@Override
	public final List<AbstractProxyNode> getLocalExternalNodes() {
		return Collections.emptyList();
	}
	@Override
	public final void setLocalExternalNodes(List<AbstractProxyNode> localExternalNodes) {
		// Do nothing
	}

	@Override
	public final List<AbstractProxyNode> getLocalInternalNodes() {
		return Collections.emptyList();
	}
	@Override
	public final void setLocalInternalNodes(List<AbstractProxyNode> localInternalNodes) {
		// Do nothing
	}

	@Override
	public final List<AbstractNode> getGlobalExternalNodes() {
		return Collections.emptyList();
	}
	@Override
	public final List<AbstractNode> getGlobalInternalNodes() {
		return Collections.emptyList();
	}
	

	@Override
	public final Nucleus mergeNucleuses(Nucleus parent1Nucleus, Nucleus parent2Nucleus) {
		AbstractNode newRpbRoot = replicateSubtree(parent1Nucleus.getRpbs().get(0).getRoot());
		
		List<AbstractNode> parent1NodesList = getDescendantNodes(newRpbRoot);
		List<AbstractNode> parent2NodesList = getDescendantNodes(parent2Nucleus.getRpbs().get(0).getRoot());
		
		ArrayList<AbstractNode> parent1NodesSubList = new ArrayList<AbstractNode>();
		for(AbstractNode node : parent1NodesList){
			if(node instanceof PaintPolygon || node instanceof PaintOval){
				parent1NodesSubList.add(node);
				parent1NodesSubList.add(node.getArgs().get(1));
				parent1NodesSubList.add(node.getArgs().get(2));
				parent1NodesSubList.add(node.getArgs().get(3));
			} else if(node instanceof TerminationNode)
				parent1NodesSubList.add(node);
		}
		
		ArrayList<AbstractNode> parent2NodesSubList = new ArrayList<AbstractNode>();
		for(AbstractNode node : parent2NodesList){
			if(node instanceof PaintPolygon || node instanceof PaintOval){
				parent2NodesSubList.add(node);
				parent2NodesSubList.add(node.getArgs().get(1));
				parent2NodesSubList.add(node.getArgs().get(2));
				parent2NodesSubList.add(node.getArgs().get(3));
			} else if(node instanceof TerminationNode)
				parent2NodesSubList.add(node);
		}
		
		List<AbstractNode> parent1CrossoverNodes = crossoverScheme.chooseFirstBranchCrossoverNodes(parent1NodesSubList, nCrossoverNodes, this);
		HashMap<AbstractNode,AbstractNode> crossoverNodes = crossoverScheme.chooseMatchingCrossoverNodes(parent1CrossoverNodes, parent2NodesSubList, this);
		
		for(Entry<AbstractNode,AbstractNode> crossoverNodesPair : crossoverNodes.entrySet()){
			AbstractNode crossoverNode1 = crossoverNodesPair.getKey();
			AbstractNode crossoverNode2 = crossoverNodesPair.getValue();
			
			if(crossoverNode1.getParent() != null)
				crossoverNode1.getParent().replaceArg(crossoverNode1, crossoverNode2);
			else{
				crossoverNode2.setParent(null);
				newRpbRoot = crossoverNode2;
			}
		}
		
		return new Nucleus(
				copy(), 
				Arrays.asList(new RPB(parent1Nucleus.getRpbs().get(0).getId(), mutationScheme.mutateBranch(newRpbRoot, this))), 
				Collections.emptyList(), 
				false, 
				Collections.emptyList(), 
				false, 
				Collections.emptyList(), 
				false, 
				Collections.emptyList(), 
				false,
				Collections.emptyList());
	}
	
	@Override
	public final AbstractNode generateTree(AbstractType returnType, int size){
		if(returnType.isTheSameAs(new Void())){
			int nVertices;
			AbstractNode shape;
			
			if(Shapes.Ovals.equals(potentialShapes) || (Shapes.Both.equals(potentialShapes) && ThreadLocalRandom.current().nextBoolean())){
				nVertices = 2;
				shape = new PaintOval();
			} else {
				nVertices = initPolygonVertices;
				shape = new PaintPolygon(maxPolygonVertices);
			}
			
			int x = ThreadLocalRandom.current().nextInt(-width / 2, width / 2);
			int y = ThreadLocalRandom.current().nextInt(-height / 2, height / 2);
			shape.setArgs(Arrays.asList(
					new TerminationNode(),
					new VertexNode(new Vertex(x, y, vertexIncrement)),
					generateVerticesList(nVertices),
					new ColorNode(new Color(alpha, colorIncrement)),
					new TerminationNode()));
			
			return shape;
		} else
			throw new RuntimeException("Unexpected tree generation requested!");
	}
	private final AbstractNode generateVerticesList(int n){
		Concat rootConcat = new Concat();
		Concat currentConcat = rootConcat;
		
		ThreadLocalRandom randomizer = ThreadLocalRandom.current();
		
		for(int i = 1; i < n; i++){
			boolean left = randomizer.nextBoolean();
			Concat newConcat = new Concat();
			currentConcat.setArgs(Arrays.asList(
					left ? newConcat : new EmptyVertexList(),
					new VertexNode(new Vertex(vertexIncrement)), 
					left ? new EmptyVertexList() : newConcat));
			currentConcat = newConcat;
		}
		currentConcat.setArgs(Arrays.asList(
				new EmptyVertexList(),
				new VertexNode(new Vertex(vertexIncrement)), 
				new EmptyVertexList()));
		
		return rootConcat;
	}
	
	@Override
	public final GeneticOperator copy() {
		return new GeneticOperator(
				potentialShapes, 
				maxPolygonVertices, 
				initPolygonVertices, 
				alpha, 
				colorIncrement, 
				vertexIncrement, 
				width, 
				height, 
				nCrossoverNodes,
				mutationScheme.getFrequency());
	}
}
