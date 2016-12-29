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
 * RPB.java
 * ---------------
 * (C) Copyright 2015-2016, by Jeremy Sayag and Contributors.
 *
 * Original Author:  Jeremy Sayag;
 * Contributor(s):   ;
 * 
 */


package com.gpapi.individuals.cells.nucleuses.modules;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import com.gpapi.individuals.cells.nucleuses.nodes.AbstractNode;
import com.gpapi.individuals.cells.nucleuses.types.AbstractType;
import com.gpapi.individuals.cells.nucleuses.types.Result;



public final class RPB extends AbstractModule {
	
	private static final long serialVersionUID = 821737350672971994L;
	
	public final AbstractNode getRoot() {
		return getBranchRoots().get(0);
	}
	
	
	
	public RPB(int id, AbstractNode root, List<AbstractType> argsTypes, List<String> argsNames) {
		super(id, Arrays.asList(root), argsTypes, argsNames);
	}
	public RPB(int id, AbstractNode root){
		this(id, root, Collections.emptyList(), Collections.emptyList());
	}
	
	
	@Override
	public final String getName() {
		return "rpb" + getId();
	}
	@Override
	public final AbstractType getReturnType() {
		return getRoot().getReturnType();
	}
	
	
	public final Result execute(List<AbstractType> args) throws Exception {
		if(args == null)
			throw new NullPointerException("Argument args can't be null !");
		else if(args.size() != getArgs().size())
			throw new IllegalArgumentException("The given number of argument is different from the RPB's one !");
		
		int i = 0;
		for(AbstractType arg : args){
			getArgs().get(i).setValue(arg);
			i++;
		}
		
		return getRoot().execute();
	}
}
