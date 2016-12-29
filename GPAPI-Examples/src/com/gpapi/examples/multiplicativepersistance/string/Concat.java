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


package com.gpapi.examples.multiplicativepersistance.string;

import java.util.ArrayList;
import java.util.Arrays;

import com.gpapi.individuals.cells.nucleuses.geneticoperators.GeneticOperatorInterface;
import com.gpapi.individuals.cells.nucleuses.nodes.AbstractNode;
import com.gpapi.individuals.cells.nucleuses.types.AbstractType;
import com.gpapi.individuals.cells.nucleuses.types.RealValue;
import com.gpapi.individuals.cells.nucleuses.types.Result;



public final class Concat extends AbstractNode {
	
	private static final long serialVersionUID = 6787675932785840352L;
	
	private static final String name = "CON";

	private static final DigitsList returnType = new DigitsList();
	
	private static final ArrayList<AbstractType> argsTypes = new ArrayList<AbstractType>(Arrays.asList(
			new DigitsList(),
			RealValue.create(),
			new DigitsList()));
	
	
	
	@Override
	public final String getName() {
		return name;
	}
	
	
	@Override
	public final Concat mutatedCopy(GeneticOperatorInterface geneticOperator){
		return copy();
	}
	
	
	@Override
	public final DigitsList getReturnType() {
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
		
		DigitsList arg0 = (DigitsList) arg0Result.getValue();
		RealValue arg1 = (RealValue) arg1Result.getValue();
		DigitsList arg2 = (DigitsList) arg2Result.getValue();
		
		arg0.getValue().add(arg1);
		arg0.getValue().addAll(arg2.getValue());
		
		return arg0Result.incrementCost(arg1Result.getCost() + arg2Result.getCost() + 3);
	}
}
