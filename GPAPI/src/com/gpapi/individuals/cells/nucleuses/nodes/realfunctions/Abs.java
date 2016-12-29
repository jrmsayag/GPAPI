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
 * Abs.java
 * ---------------
 * (C) Copyright 2015-2016, by Jeremy Sayag and Contributors.
 *
 * Original Author:  Jeremy Sayag;
 * Contributor(s):   ;
 * 
 */


package com.gpapi.individuals.cells.nucleuses.nodes.realfunctions;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import com.gpapi.individuals.cells.nucleuses.nodes.AbstractNode;
import com.gpapi.individuals.cells.nucleuses.types.AbstractType;
import com.gpapi.individuals.cells.nucleuses.types.RealValue;
import com.gpapi.individuals.cells.nucleuses.types.Result;



/**
 * 
 * Implements the absolute value function.<p>
 *
 */

public final class Abs extends AbstractNode {
	
	private static final long serialVersionUID = 4910193903136740163L;
	
	private static final String name = "Abs";

	private static final RealValue returnType = RealValue.create();
	
	private static final List<AbstractType> argsTypes = Collections.unmodifiableList(Arrays.asList(RealValue.create()));
	
	
	
	@Override
	public final String getName() {
		return name;
	}
	@Override
	public final RealValue getReturnType() {
		return returnType;
	}
	@Override
	public final List<AbstractType> getArgsTypes() {
		return argsTypes;
	}
	
	
	@Override
	public final Abs copy(){
		return new Abs();
	}
	@Override
	public final Result execute() throws Exception {
		Result result = getArgs().get(0).execute();
		
		RealValue arg = (RealValue) result.getValue();
		arg.setValue(Math.abs(arg.getValue()));
		
		return result.incrementCost(1);
	}
}
