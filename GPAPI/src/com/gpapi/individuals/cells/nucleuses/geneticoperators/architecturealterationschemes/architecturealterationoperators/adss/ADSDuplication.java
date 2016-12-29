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
 * ADSDuplication.java
 * ---------------
 * (C) Copyright 2015-2016, by Jeremy Sayag and Contributors.
 *
 * Original Author:  Jeremy Sayag;
 * Contributor(s):   ;
 * 
 */


package com.gpapi.individuals.cells.nucleuses.geneticoperators.architecturealterationschemes.architecturealterationoperators.adss;

import java.util.List;
import java.util.concurrent.ThreadLocalRandom;

import com.gpapi.individuals.cells.nucleuses.Nucleus;
import com.gpapi.individuals.cells.nucleuses.geneticoperators.architecturealterationschemes.architecturealterationoperators.ArchitectureAlterationOperatorInterface;
import com.gpapi.individuals.cells.nucleuses.modules.adss.ADS;
import com.gpapi.individuals.cells.nucleuses.modules.adss.ADSProxy;
import com.gpapi.individuals.cells.nucleuses.nodes.AbstractNode;



public final class ADSDuplication implements ArchitectureAlterationOperatorInterface {
	
	private static final long serialVersionUID = 3195633330208156613L;
	
	private final int maxAdss;
	
	
	
	public ADSDuplication(int maxAdss){
		this.maxAdss = maxAdss;
	}
	public ADSDuplication(){
		this(Integer.MAX_VALUE);
	}
	
	@Override
	public final boolean architectureAlteration(Nucleus nucleus) {
		List<ADS> adss = nucleus.getAdss();
		if(adss.isEmpty() || adss.size() >= maxAdss)
			return false;
		
		ThreadLocalRandom generator = ThreadLocalRandom.current();
		ADS originalAds = adss.get(generator.nextInt(adss.size()));
		ADS duplicateAds = new ADS(
				originalAds.getId() + 1, 
				originalAds.getStorageType(), 
				originalAds.getReturnType(), 
				originalAds.getMaxSize());
		
		for(AbstractNode node : nucleus.getAllNodes()){
			if(node instanceof ADSProxy){
				ADSProxy adsProxy = (ADSProxy) node;
				if(adsProxy.getId() == originalAds.getId() && generator.nextBoolean())
					adsProxy.setAds(duplicateAds);
			}
		}
		
		for(int i = duplicateAds.getId(); i < adss.size(); i++)
			adss.get(i).setId(i+1);
		adss.add(duplicateAds.getId(), duplicateAds);
		
		return true;
	}
	
	@Override
	public final ADSDuplication copy() {
		return new ADSDuplication(maxAdss);
	}
}
