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
 * TerminationNode.java
 * ---------------
 * (C) Copyright 2015-2016, by Jeremy Sayag and Contributors.
 *
 * Original Author:  Jeremy Sayag;
 * Contributor(s):   ;
 * 
 */


package com.gpapi.examples.painting.nodes.external;

import java.util.ArrayList;

import com.gpapi.examples.painting.nodes.types.Void;
import com.gpapi.individuals.cells.nucleuses.geneticoperators.GeneticOperatorInterface;
import com.gpapi.individuals.cells.nucleuses.nodes.AbstractNode;
import com.gpapi.individuals.cells.nucleuses.types.AbstractType;
import com.gpapi.individuals.cells.nucleuses.types.Result;



public final class TerminationNode extends AbstractNode {

	private static final long serialVersionUID = 6069619293181312238L;
	
	private static final ArrayList<AbstractType> argsTypes = new ArrayList<AbstractType>();
	@Override
	public final ArrayList<AbstractType> getArgsTypes() {
		return argsTypes;
	}
	
	private static final Void returnType = new Void();
	@Override
	public final Void getReturnType() {
		return returnType;
	}
	
	
	
	@Override
	public final String getName() {
		return returnType.toString();
	}
	
	
	@Override
	public final TerminationNode mutatedCopy(GeneticOperatorInterface geneticOperator) {
		return copy();
	}
	

	@Override
	public final TerminationNode copy() {
		return new TerminationNode();
	}

	@Override
	public final Result execute() {
		return new Result(returnType.copy());
	}
}
