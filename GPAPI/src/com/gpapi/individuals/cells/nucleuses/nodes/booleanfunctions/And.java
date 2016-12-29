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
 * And.java
 * ---------------
 * (C) Copyright 2015-2016, by Jeremy Sayag and Contributors.
 *
 * Original Author:  Jeremy Sayag;
 * Contributor(s):   ;
 * 
 */


package com.gpapi.individuals.cells.nucleuses.nodes.booleanfunctions;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import com.gpapi.individuals.cells.nucleuses.nodes.AbstractNode;
import com.gpapi.individuals.cells.nucleuses.types.AbstractType;
import com.gpapi.individuals.cells.nucleuses.types.BooleanValue;
import com.gpapi.individuals.cells.nucleuses.types.Result;



public final class And extends AbstractNode {
	
	private static final long serialVersionUID = -5196213406444638049L;
	
	private static final String name = "&&";

	private static final BooleanValue returnType = BooleanValue.create();
	
	private static final List<AbstractType> argsTypes = Collections.unmodifiableList(Arrays.asList(BooleanValue.create(), BooleanValue.create()));
	
	
	
	@Override
	public final String getName() {
		return name;
	}
	@Override
	public final BooleanValue getReturnType() {
		return returnType;
	}
	@Override
	public final List<AbstractType> getArgsTypes() {
		return argsTypes;
	}
	
	
	@Override
	public final And copy() {
		return new And();
	}
	@Override
	public final Result execute() throws Exception {
		Result arg0Result = getArgs().get(0).execute();
		Result arg1Result = getArgs().get(1).execute();
		
		BooleanValue arg0 = (BooleanValue) arg0Result.getValue();
		BooleanValue arg1 = (BooleanValue) arg1Result.getValue();
		arg0.setValue(arg0.getValue() && arg1.getValue());
		
		return arg0Result.incrementCost(arg1Result.getCost() + 2);
	}
}
