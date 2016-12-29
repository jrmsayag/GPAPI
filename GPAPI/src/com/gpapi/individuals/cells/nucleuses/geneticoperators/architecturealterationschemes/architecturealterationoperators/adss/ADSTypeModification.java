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
 * ADSTypeModification.java
 * ---------------
 * (C) Copyright 2015-2016, by Jeremy Sayag and Contributors.
 *
 * Original Author:  Jeremy Sayag;
 * Contributor(s):   ;
 * 
 */


package com.gpapi.individuals.cells.nucleuses.geneticoperators.architecturealterationschemes.architecturealterationoperators.adss;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.ThreadLocalRandom;

import com.gpapi.individuals.cells.nucleuses.Nucleus;
import com.gpapi.individuals.cells.nucleuses.geneticoperators.GeneticOperatorInterface;
import com.gpapi.individuals.cells.nucleuses.geneticoperators.architecturealterationschemes.architecturealterationoperators.ArchitectureAlterationOperatorInterface;
import com.gpapi.individuals.cells.nucleuses.modules.AbstractModule;
import com.gpapi.individuals.cells.nucleuses.modules.adss.ADS;
import com.gpapi.individuals.cells.nucleuses.modules.adss.ADSProxy;
import com.gpapi.individuals.cells.nucleuses.nodes.AbstractNode;



public final class ADSTypeModification implements ArchitectureAlterationOperatorInterface {

	private static final long serialVersionUID = -7849750907395886653L;
	
	private final List<ADS.Type> potentialAdsTypes;
	
	
	
	public ADSTypeModification(List<ADS.Type> potentialAdsTypes){
		if(potentialAdsTypes == null)
			throw new NullPointerException("Potential ADS types must be specified!");
		else if(potentialAdsTypes.isEmpty())
			throw new IllegalArgumentException("Potential ADS types can't be empty!");
		else
			this.potentialAdsTypes = new ArrayList<ADS.Type>(potentialAdsTypes);
	}
	public ADSTypeModification(){
		this(Arrays.asList(ADS.Type.values()));
	}
	
	
	@Override
	public final boolean architectureAlteration(Nucleus nucleus) {
		List<ADS> adss = nucleus.getAdss();
		if(adss.isEmpty())
			return false;
		
		ThreadLocalRandom generator = ThreadLocalRandom.current();
		ADS ads = adss.get(generator.nextInt(adss.size()));
		
		ADS.Type newAdsType = potentialAdsTypes.get(generator.nextInt(potentialAdsTypes.size()));
		
		if(newAdsType.equals(ads.getStorageType()))
			return false;
		
		int oldDimension = ads.getDimension();
		ads.setStorageType(newAdsType);
		int newDimension = ads.getDimension();
		
		if(newDimension != oldDimension){
			int dimensionDelta = newDimension - oldDimension;
			GeneticOperatorInterface geneticOperator = nucleus.getGeneticOperator();
			
			List<AbstractNode> newAdressArgs = new ArrayList<AbstractNode>(Math.max(0, dimensionDelta));
			for(int i = 0; i < dimensionDelta; i++)
				newAdressArgs.add(geneticOperator.findExternalNode(ADS.getAdressArgsType()));
			
			for(AbstractModule module : nucleus.getAllModules()){
				boolean consolidate = false;
				for(AbstractNode root : module.getBranchRoots()){
					List<AbstractNode> branchNodes = geneticOperator.getDescendantNodes(root);
					Collections.reverse(branchNodes);
					for(AbstractNode node : branchNodes){
						if(node instanceof ADSProxy){
							ADSProxy adsProxy = (ADSProxy) node;
							if(adsProxy.getId() == ads.getId()){
								adaptAdsCall(adsProxy, newAdressArgs, dimensionDelta);
								consolidate = true;
							}
						}
					}
				}
				if(consolidate)
					module.consolidate();
			}
		}
		
		return true;
	}
	private final void adaptAdsCall(ADSProxy adsProxy, List<AbstractNode> newAdressArgs, int dimensionDelta){
		ArrayList<AbstractNode> newArgs = new ArrayList<AbstractNode>(adsProxy.getArgs());
		if(dimensionDelta > 0){
			for(AbstractNode newArg : newAdressArgs)
				newArgs.add(newArg.copy());
		} else {
			for(int i = 0; i < -dimensionDelta; i++)
				newArgs.remove(newArgs.size()-1);
		}
		adsProxy.setArgs(newArgs);
	}

	@Override
	public final ADSTypeModification copy() {
		return new ADSTypeModification(potentialAdsTypes);
	}
}
