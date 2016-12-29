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
 * Glue.java
 * ---------------
 * (C) Copyright 2015-2016, by Jeremy Sayag and Contributors.
 *
 * Original Author:  Jeremy Sayag;
 * Contributor(s):   ;
 * 
 */


package com.gpapi.individuals.cells.nucleuses.nodes;

import java.util.Arrays;
import java.util.List;

import com.gpapi.individuals.cells.nucleuses.types.AbstractType;
import com.gpapi.individuals.cells.nucleuses.types.Result;



public final class Glue extends AbstractNode {

	private static final long serialVersionUID = -5185466217152048120L;
	
	private static final String name = "Glue";
	
	private final AbstractType type;
	
	
	
	public Glue(AbstractType type){
		this.type = type;
	}
	
	@Override
	public final String getName() {
		return name;
	}

	@Override
	public final AbstractType getReturnType() {
		return type;
	}

	@Override
	public final List<AbstractType> getArgsTypes() {
		return Arrays.asList(type, type);
	}

	@Override
	public final Glue copy() {
		return new Glue(type);
	}

	@Override
	public final Result execute() throws Exception {
		Result firstResult = getArgs().get(0).execute();
		return getArgs().get(1).execute().incrementCost(firstResult.getCost());
	}
}
