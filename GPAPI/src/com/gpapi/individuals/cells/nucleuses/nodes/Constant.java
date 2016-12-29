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
 * Constant.java
 * ---------------
 * (C) Copyright 2015-2016, by Jeremy Sayag and Contributors.
 *
 * Original Author:  Jeremy Sayag;
 * Contributor(s):   ;
 * 
 */


package com.gpapi.individuals.cells.nucleuses.nodes;

import java.util.Collections;
import java.util.List;
import java.util.concurrent.ThreadLocalRandom;

import com.gpapi.individuals.cells.nucleuses.geneticoperators.GeneticOperatorInterface;
import com.gpapi.individuals.cells.nucleuses.types.AbstractType;
import com.gpapi.individuals.cells.nucleuses.types.Result;



public final class Constant extends AbstractNode {

	private static final long serialVersionUID = -6338549569518673633L;
	
	private static final List<AbstractType> argsTypes = Collections.unmodifiableList(Collections.emptyList());
	@Override
	public final List<AbstractType> getArgsTypes() {
		return argsTypes;
	}
	
	private final AbstractType returnType;
	@Override
	public final AbstractType getReturnType() {
		return returnType;
	}
	
	
	
	public Constant(AbstractType returnType){
		if(returnType == null)
			throw new NullPointerException("A return type must be specified!");
		else
			this.returnType = returnType;
	}
	
	
	@Override
	public final String getName() {
		return getReturnType().toString();
	}
	
	
	@Override
	public final AbstractNode mutatedCopy(GeneticOperatorInterface geneticOperator){
		if(ThreadLocalRandom.current().nextBoolean())
			return generateNew();
		else
			return super.mutatedCopy(geneticOperator);
	}
	

	@Override
	public final Constant copy() {
		return new Constant(getReturnType().copy());
	}
	
	@Override
	public final Constant generateNew(){
		AbstractType value = execute().getValue();
		value.mutate();
		return new Constant(value);
	}

	@Override
	public final Result execute() {
		return new Result(getReturnType().copy());
	}
}
