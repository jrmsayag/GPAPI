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
 * Concat.java
 * ---------------
 * (C) Copyright 2015-2016, by Jeremy Sayag and Contributors.
 *
 * Original Author:  Jeremy Sayag;
 * Contributor(s):   ;
 * 
 */


package com.gpapi.examples.painting.nodes.internal;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.concurrent.ThreadLocalRandom;

import com.gpapi.examples.painting.nodes.external.EmptyVertexList;
import com.gpapi.examples.painting.nodes.external.VertexNode;
import com.gpapi.examples.painting.nodes.types.Vertex;
import com.gpapi.examples.painting.nodes.types.VertexList;
import com.gpapi.individuals.cells.nucleuses.geneticoperators.GeneticOperatorInterface;
import com.gpapi.individuals.cells.nucleuses.nodes.AbstractNode;
import com.gpapi.individuals.cells.nucleuses.types.AbstractType;
import com.gpapi.individuals.cells.nucleuses.types.Result;



public final class Concat extends AbstractNode {
	
	private static final long serialVersionUID = 6787675932785840352L;
	
	private static final String name = "CON";

	private static final VertexList returnType = new VertexList();
	
	private static final ArrayList<AbstractType> argsTypes = new ArrayList<AbstractType>(Arrays.asList(
			new VertexList(),
			new Vertex(0),
			new VertexList()));
	
	
	
	@Override
	public final AbstractNode mutatedCopy(GeneticOperatorInterface geneticOperator) {
		try{
			if(ThreadLocalRandom.current().nextBoolean())
				return removeVertex(ThreadLocalRandom.current().nextBoolean(), geneticOperator);
			else{
				Vertex newVertex = (Vertex) getArgs().get(1).execute().getValue();
				newVertex.mutate();
				return addVertex(new VertexNode(newVertex), ThreadLocalRandom.current().nextBoolean(), geneticOperator);
			}
		} catch(Exception e){
			throw new RuntimeException(e);
		}
	}
	private final AbstractNode removeVertex(boolean insertBefore, GeneticOperatorInterface geneticOperator){
		int newRootIndex = insertBefore ? 0 : 2;
		int insertedSubtreeIndex = insertBefore ? 2 : 0;
		
		if(getArgs().get(newRootIndex) instanceof EmptyVertexList)
			return geneticOperator.replicateSubtree(getArgs().get(insertedSubtreeIndex));
		else {
			Concat newRoot = (Concat) geneticOperator.replicateSubtree(getArgs().get(newRootIndex));
			EmptyVertexList connector = getFirstFreeConnectorFrom(newRoot, insertBefore);
			connector.getParent().replaceArg(connector, geneticOperator.replicateSubtree(getArgs().get(insertedSubtreeIndex)));
			return newRoot;
		}
	}
	private final AbstractNode addVertex(AbstractNode newVertex, boolean before, GeneticOperatorInterface geneticOperator) throws Exception {
		int mutatedSubtreeIndex = before ? 0 : 2;
		int copiedSubtreeIndex = before ? 2 : 0;
		
		AbstractNode newLeftSubtree;
		AbstractNode newRightSubtree;
		
		if(getArgs().get(mutatedSubtreeIndex) instanceof EmptyVertexList){
			Concat newVertexContainer = copy();
			newVertexContainer.setArgs(Arrays.asList(new EmptyVertexList(), newVertex, new EmptyVertexList()));
			
			newLeftSubtree = before ? newVertexContainer : geneticOperator.replicateSubtree(getArgs().get(copiedSubtreeIndex));
			newRightSubtree = before ? geneticOperator.replicateSubtree(getArgs().get(copiedSubtreeIndex)) : newVertexContainer;
		} else {
			Concat mutatedSubtree = (Concat) geneticOperator.replicateSubtree(getArgs().get(mutatedSubtreeIndex));
			EmptyVertexList connector = getFirstFreeConnectorFrom(mutatedSubtree, before);
			
			Concat newVertexContainer = copy();
			newVertexContainer.setArgs(Arrays.asList(new EmptyVertexList(), newVertex, new EmptyVertexList()));
			connector.getParent().replaceArg(connector, newVertexContainer);
			
			newLeftSubtree = before ? mutatedSubtree : geneticOperator.replicateSubtree(getArgs().get(copiedSubtreeIndex));
			newRightSubtree = before ? geneticOperator.replicateSubtree(getArgs().get(copiedSubtreeIndex)) : mutatedSubtree;
		}
		
		Concat newRoot = copy();
		newRoot.setArgs(Arrays.asList(newLeftSubtree, new VertexNode((Vertex) getArgs().get(1).execute().getValue()), newRightSubtree));
		return newRoot;
	}
	private final EmptyVertexList getFirstFreeConnectorFrom(Concat root, boolean toTheRight){
		int argIndex = toTheRight ? 2 : 0;
		while(root.getArgs().get(argIndex) instanceof Concat)
			root = (Concat) root.getArgs().get(argIndex);
		return (EmptyVertexList) root.getArgs().get(argIndex);
	}
	
	
	@Override
	public final String getName() {
		return name;
	}
	@Override
	public final VertexList getReturnType() {
		return returnType;
	}
	@Override
	public final ArrayList<AbstractType> getArgsTypes() {
		return argsTypes;
	}
	
	
	@Override
	public final Concat copy() {
		return new Concat();
	}
	@Override
	public final Result execute() throws Exception {
		Result arg0Result = getArgs().get(0).execute();
		Result arg1Result = getArgs().get(1).execute();
		Result arg2Result = getArgs().get(2).execute();
		
		VertexList arg0 = (VertexList) arg0Result.getValue();
		Vertex arg1 = (Vertex) arg1Result.getValue();
		VertexList arg2 = (VertexList) arg2Result.getValue();
		
		arg0.getValue().add(arg1);
		arg0.getValue().addAll(arg2.getValue());
		
		return arg0Result.incrementCost(arg1Result.getCost() + arg2Result.getCost() + 3);
	}
}
