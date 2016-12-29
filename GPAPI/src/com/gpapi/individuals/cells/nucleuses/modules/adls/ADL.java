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
 * ADL.java
 * ---------------
 * (C) Copyright 2015-2016, by Jeremy Sayag and Contributors.
 *
 * Original Author:  Jeremy Sayag;
 * Contributor(s):   ;
 * 
 */


package com.gpapi.individuals.cells.nucleuses.modules.adls;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import com.gpapi.individuals.cells.nucleuses.modules.AbstractModule;
import com.gpapi.individuals.cells.nucleuses.nodes.AbstractNode;
import com.gpapi.individuals.cells.nucleuses.types.AbstractType;
import com.gpapi.individuals.cells.nucleuses.types.RealValue;
import com.gpapi.individuals.cells.nucleuses.types.Result;



public final class ADL extends AbstractModule {
	
	private static final long serialVersionUID = 5099291834120139392L;
	
	private static final RealValue conditionReturnType = RealValue.create();
	public static final RealValue getConditionReturnType() {
		return conditionReturnType;
	}
	
	public final AbstractNode getInitializationRoot() {
		return getBranchRoots().get(0);
	}

	public final AbstractNode getConditionRoot() {
		return getBranchRoots().get(1);
	}
	
	public final AbstractNode getBodyRoot() {
		return getBranchRoots().get(2);
	}
	
	public final int getInitializationSize() {
		return getBranchSize(0);
	}
	
	public final int getConditionSize() {
		return getBranchSize(1);
	}
	
	public final int getBodySize() {
		return getBranchSize(2);
	}
	
	public final int getInitializationDepth() {
		return getBranchDepth(0);
	}
	
	public final int getConditionDepth() {
		return getBranchDepth(1);
	}
	
	public final int getBodyDepth() {
		return getBranchDepth(2);
	}
	
	private final List<AbstractType> externalArgsTypes;
	/**
	 * TODO : Description.
	 * <p>
	 * Note : Be careful, the returned list is the internal one, not just a view or a copy!
	 * 
	 * @return
	 */
	public final List<AbstractType> getExternalArgsTypes() {
		return externalArgsTypes;
	}
	
	private final int maxCalls;
	public final int getMaxCalls() {
		return maxCalls;
	}
	
	private final int maxIterations;
	public final int getMaxIterations() {
		return maxIterations;
	}
	
	private int currentCalls = 0;
	
	
	
	public ADL(
			int id, 
			AbstractNode initializationRoot, 
			AbstractNode conditionRoot, 
			AbstractNode bodyRoot, 
			List<AbstractType> externalArgsTypes, 
			int maxCalls, 
			int maxIterations) {
		super(id, Arrays.asList(initializationRoot, conditionRoot, bodyRoot), argsTypesFor(externalArgsTypes));
		
		getArgs().get(getArgs().size() - 1).setManualName("index");
		
		this.externalArgsTypes = new ArrayList<AbstractType>(externalArgsTypes);
		
		this.maxCalls = maxCalls;
		this.maxIterations = maxIterations;
		
		reset();
	}
	public ADL(
			int id, 
			AbstractNode initializationRoot, 
			AbstractNode conditionRoot, 
			AbstractNode bodyRoot, 
			int maxCalls, 
			int maxIterations){
		this(
				id, 
				initializationRoot, 
				conditionRoot, 
				bodyRoot, 
				Collections.emptyList(), 
				maxCalls, 
				maxIterations);
	}
	
	
	public final void reset(){
		currentCalls = 0;
	}
	
	
	@Override
	public final String getName() {
		return "adl" + getId();
	}
	@Override
	public final AbstractType getReturnType() {
		return getBodyRoot().getReturnType();
	}
	
	
	public final Result execute(List<Result> argsResults) throws Exception {
		if(argsResults == null)
			throw new NullPointerException("Argument argsResult can't be null !");
		else if(argsResults.size() != getExternalArgsTypes().size())
			throw new IllegalArgumentException("The given number of argument is different from the ADL's one !");
		
		int i = 0;
		for(Result argResult : argsResults){
			getArgs().get(i).setValue(argResult.getValue());
			i++;
		}
		
		int index = 0;
		getArgs().get(getArgs().size()-1).setValue(RealValue.create(index));
		Result result = getInitializationRoot().execute();
		
		Result conditionResult;
		do {
			result = getBodyRoot().execute().incrementCost(result.getCost());
			
			index++;
			if(currentCalls >= getMaxCalls() || index >= getMaxIterations())
				break;
			else {
				getArgs().get(getArgs().size()-1).setValue(RealValue.create(index));
				conditionResult = getConditionRoot().execute();
				result.incrementCost(conditionResult.getCost());
			}
		} while(((RealValue) conditionResult.getValue()).getValue() > 0.0);
		
		if(currentCalls < getMaxCalls())
			currentCalls++;
		
		return result;
	}
	
	
	public static final List<AbstractType> argsTypesFor(List<AbstractType> argsTypes){
		ArrayList<AbstractType> allTypes = new ArrayList<AbstractType>(argsTypes.size() + 1);
		allTypes.addAll(argsTypes);
		allTypes.add(RealValue.create());
		return allTypes;
	}
}
