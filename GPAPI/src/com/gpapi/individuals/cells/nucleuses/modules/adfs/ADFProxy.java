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
 * ADFProxy.java
 * ---------------
 * (C) Copyright 2015-2016, by Jeremy Sayag and Contributors.
 *
 * Original Author:  Jeremy Sayag;
 * Contributor(s):   ;
 * 
 */


package com.gpapi.individuals.cells.nucleuses.modules.adfs;

import java.util.ArrayList;
import java.util.List;

import com.gpapi.individuals.cells.nucleuses.modules.AbstractProxyNode;
import com.gpapi.individuals.cells.nucleuses.nodes.AbstractNode;
import com.gpapi.individuals.cells.nucleuses.types.AbstractType;
import com.gpapi.individuals.cells.nucleuses.types.Result;



public final class ADFProxy extends AbstractProxyNode {

	private static final long serialVersionUID = -6806934081843219061L;
	
	public final ADF getAdf() {
		return (ADF) getSlot();
	}
	public final void setAdf(ADF adf) {
		ADF oldAdf = getAdf();
		setSlot(adf);
		if(!argsTypesMatchRequestedTypes(oldAdf.getArgsTypes())){
			setSlot(oldAdf);
			throw new RuntimeException("Trying to set an adf whose args types are more retrictive than the previous adf's ones!");
		}
	}
	
	
	
	public ADFProxy(ADF adf){
		super(adf);
	}
	
	
	@Override
	public final ArrayList<AbstractType> getArgsTypes() {
		return getAdf().getArgsTypes();
	}
	
	
	@Override
	public final ADFProxy copy() {
		return new ADFProxy(getAdf());
	}
	@Override
	public final Result execute() throws Exception {
		List<Result> argsResults = new ArrayList<Result>(getArgs().size());
		for(AbstractNode arg : getArgs())
			argsResults.add(arg.execute());
		
		Result result = getAdf().execute(argsResults);
		for(Result argResult : argsResults)
			result.incrementCost(argResult.getCost());
		
		return result;
	}
}
