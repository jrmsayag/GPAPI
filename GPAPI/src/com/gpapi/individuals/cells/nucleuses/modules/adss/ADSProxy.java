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
 * ADSProxy.java
 * ---------------
 * (C) Copyright 2015-2016, by Jeremy Sayag and Contributors.
 *
 * Original Author:  Jeremy Sayag;
 * Contributor(s):   ;
 * 
 */


package com.gpapi.individuals.cells.nucleuses.modules.adss;

import java.util.ArrayList;
import java.util.List;

import com.gpapi.individuals.cells.nucleuses.modules.AbstractProxyNode;
import com.gpapi.individuals.cells.nucleuses.nodes.AbstractNode;
import com.gpapi.individuals.cells.nucleuses.types.AbstractType;
import com.gpapi.individuals.cells.nucleuses.types.Result;



public final class ADSProxy extends AbstractProxyNode {

	private static final long serialVersionUID = -7280894122503802398L;
	
	public final ADS getAds() {
		return (ADS) getSlot();
	}
	public final void setAds(ADS ads) {
		if(ads.getDimension() != getAds().getDimension())
			throw new RuntimeException("Trying to set an ads whose dimension is not the same as the previous ads's one!");
		else
			setSlot(ads);
	}
	
	private final boolean write;
	public final boolean isWrite() {
		return write;
	}
	
	
	
	public ADSProxy(ADS ads, boolean write){
		super(ads);
		this.write = write;
	}
	

	@Override
	public final String getName(){
		return super.getName() + (write ? "-w" : "-r");
	}
	@Override
	public final List<AbstractType> getArgsTypes() {
		List<AbstractType> argsTypes = new ArrayList<AbstractType>();
		
		if(isWrite())
			argsTypes.add(getReturnType());
		
		for(int i = 0; i < getAds().getDimension(); i++)
			argsTypes.add(ADS.getAdressArgsType());
		
		return argsTypes;
	}
	
	
	@Override
	public final ADSProxy copy() {
		return new ADSProxy(getAds(), isWrite());
	}
	@Override
	public final Result execute() throws Exception {
		List<Result> argsResults = new ArrayList<Result>(getArgs().size());
		for(AbstractNode arg : getArgs())
			argsResults.add(arg.execute());
		
		Result result;
		if(isWrite())
			result = getAds().write(argsResults);
		else
			result = getAds().read(argsResults);
		
		for(Result argResult : argsResults)
			result.incrementCost(argResult.getCost());
		
		return result;
	}
}
