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
 * ArgumentProxy.java
 * ---------------
 * (C) Copyright 2015-2016, by Jeremy Sayag and Contributors.
 *
 * Original Author:  Jeremy Sayag;
 * Contributor(s):   ;
 * 
 */


package com.gpapi.individuals.cells.nucleuses.modules;

import java.util.Collections;
import java.util.List;

import com.gpapi.individuals.cells.nucleuses.types.AbstractType;
import com.gpapi.individuals.cells.nucleuses.types.Result;



public final class ArgumentProxy extends AbstractProxyNode {

	private static final long serialVersionUID = -6877052337272183621L;
	
	private static final List<AbstractType> argsTypes = Collections.unmodifiableList(Collections.emptyList());
	
	public final Argument getArgument() {
		return (Argument) getSlot();
	}
	public final void setArgument(Argument argument) {
		setSlot(argument);
	}
	
	
	
	public ArgumentProxy(Argument argument){
		super(argument);
	}
	
	
	@Override
	public final List<AbstractType> getArgsTypes() {
		return argsTypes;
	}
	
	
	@Override
	public final ArgumentProxy copy() {
		return new ArgumentProxy(getArgument());
	}
	@Override
	public final Result execute() {
		return new Result(getArgument().getValue().copy());
	}
}
