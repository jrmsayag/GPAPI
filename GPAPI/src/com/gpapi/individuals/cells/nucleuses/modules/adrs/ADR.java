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
 * ADR.java
 * ---------------
 * (C) Copyright 2015-2016, by Jeremy Sayag and Contributors.
 *
 * Original Author:  Jeremy Sayag;
 * Contributor(s):   ;
 * 
 */


package com.gpapi.individuals.cells.nucleuses.modules.adrs;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import com.gpapi.individuals.cells.nucleuses.modules.AbstractModule;
import com.gpapi.individuals.cells.nucleuses.nodes.AbstractNode;
import com.gpapi.individuals.cells.nucleuses.types.AbstractType;
import com.gpapi.individuals.cells.nucleuses.types.RealValue;
import com.gpapi.individuals.cells.nucleuses.types.Result;



public final class ADR extends AbstractModule {
	
	private static final long serialVersionUID = 5099291834120139392L;
	
	private static final RealValue conditionReturnType = RealValue.create();
	public static final RealValue getConditionReturnType() {
		return conditionReturnType;
	}

	public final AbstractNode getConditionRoot() {
		return getBranchRoots().get(0);
	}
	
	public final AbstractNode getBodyRoot() {
		return getBranchRoots().get(1);
	}
	
	public final AbstractNode getGroundRoot() {
		return getBranchRoots().get(2);
	}
	
	public final int getConditionSize() {
		return getBranchSize(0);
	}
	
	public final int getBodySize() {
		return getBranchSize(1);
	}
	
	public final int getGroundSize() {
		return getBranchSize(2);
	}
	
	public final int getConditionDepth() {
		return getBranchDepth(0);
	}
	
	public final int getBodyDepth() {
		return getBranchDepth(1);
	}
	
	public final int getGroundDepth() {
		return getBranchDepth(2);
	}

	private final int maxCalls;
	public final int getMaxCalls() {
		return maxCalls;
	}
	
	private final int maxRecursionDepth;
	public final int getMaxRecursionDepth() {
		return maxRecursionDepth;
	}
	
	private int currentCalls = 0;
	private int currentRecursionDepth = 0;
	
	
	
	public ADR(
			int id, 
			AbstractNode conditionRoot, 
			AbstractNode bodyRoot, 
			AbstractNode groundRoot, 
			List<AbstractType> argsTypes, 
			int maxCalls, 
			int maxRecursionDepth) {
		super(id, Arrays.asList(conditionRoot, bodyRoot, groundRoot), argsTypes);
		
		this.maxCalls = maxCalls;
		this.maxRecursionDepth = maxRecursionDepth;
		
		reset();
	}
	public ADR(
			int id, 
			AbstractNode conditionRoot, 
			AbstractNode bodyRoot, 
			AbstractNode groundRoot, 
			int maxCalls, 
			int maxRecursionDepth){
		this(
				id, 
				conditionRoot, 
				bodyRoot, 
				groundRoot, 
				Collections.emptyList(), 
				maxCalls, 
				maxRecursionDepth);
	}
	
	
	public final void reset(){
		currentCalls = 0;
	}
	
	
	@Override
	public final String getName() {
		return "adr" + getId();
	}
	@Override
	public final AbstractType getReturnType() {
		return getGroundRoot().getReturnType();
	}
	
	
	public final Result execute(List<Result> argsResults) throws Exception {
		if(argsResults == null)
			throw new NullPointerException("Argument argsResult can't be null !");
		else if(argsResults.size() != getArgs().size())
			throw new IllegalArgumentException("The given number of argument is different from the ADR's one !");
		
		ArrayList<AbstractType> previousArgs = new ArrayList<AbstractType>(getArgs().size());
		
		int i = 0;
		for(Result argResult : argsResults){
			previousArgs.add(getArgs().get(i).getValue());
			getArgs().get(i).setValue(argResult.getValue());
			i++;
		}
		
		Result result;
		if(currentCalls < maxCalls){
			currentCalls++;
			
			currentRecursionDepth++;
			if(currentRecursionDepth <= maxRecursionDepth){
				Result conditionResult = getConditionRoot().execute();
				if(((RealValue) conditionResult.getValue()).getValue() > 0.0)
					result = getBodyRoot().execute().incrementCost(conditionResult.getCost());
				else
					result = getGroundRoot().execute().incrementCost(conditionResult.getCost());
			} else
				result = getGroundRoot().execute();
			currentRecursionDepth--;
		} else 
			result = getGroundRoot().execute();
		
		for(i = 0; i < getArgs().size(); i++)
			getArgs().get(i).setValue(previousArgs.get(i));
		
		return result;
	}
}
