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
 * ADSCreation.java
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
import java.util.List;
import java.util.concurrent.ThreadLocalRandom;

import com.gpapi.individuals.cells.nucleuses.Nucleus;
import com.gpapi.individuals.cells.nucleuses.geneticoperators.GeneticOperatorInterface;
import com.gpapi.individuals.cells.nucleuses.geneticoperators.architecturealterationschemes.architecturealterationoperators.ArchitectureAlterationOperatorInterface;
import com.gpapi.individuals.cells.nucleuses.modules.AbstractModule;
import com.gpapi.individuals.cells.nucleuses.modules.adss.ADS;
import com.gpapi.individuals.cells.nucleuses.modules.adss.ADSProxy;
import com.gpapi.individuals.cells.nucleuses.nodes.AbstractNode;
import com.gpapi.individuals.cells.nucleuses.nodes.Glue;



public final class ADSCreation implements ArchitectureAlterationOperatorInterface {

	private static final long serialVersionUID = -646902911029418351L;
	
	private final int maxAdss;
	
	private final int maxAdssSize;
	
	private final List<ADS.Type> potentialAdsTypes;
	
	
	
	public ADSCreation(int maxAdss, int maxAdssSize, List<ADS.Type> potentialAdsTypes){
		if(potentialAdsTypes == null)
			throw new NullPointerException("Potential ADS types must be specified!");
		else if(potentialAdsTypes.isEmpty())
			throw new IllegalArgumentException("Potential ADS types can't be empty!");
		
		this.maxAdss = maxAdss;
		this.maxAdssSize = maxAdssSize;
		
		this.potentialAdsTypes = new ArrayList<ADS.Type>(potentialAdsTypes);
	}
	public ADSCreation(int maxAdssSize, List<ADS.Type> potentialAdsTypes){
		this(Integer.MAX_VALUE, maxAdssSize, potentialAdsTypes);
	}
	public ADSCreation(List<ADS.Type> potentialAdsTypes){
		this(Integer.MAX_VALUE, potentialAdsTypes);
	}
	public ADSCreation(int maxAdss, int maxAdssSize){
		this(maxAdss, maxAdssSize, Arrays.asList(ADS.Type.values()));
	}
	public ADSCreation(int maxAdssSize){
		this(Integer.MAX_VALUE, maxAdssSize);
	}
	public ADSCreation(){
		this(Integer.MAX_VALUE);
	}
	
	
	@Override
	public final boolean architectureAlteration(Nucleus nucleus) {
		List<ADS> adss = nucleus.getAdss();
		if(adss.size() >= maxAdss)
			return false;
		
		ThreadLocalRandom generator = ThreadLocalRandom.current();
		GeneticOperatorInterface geneticOperator = nucleus.getGeneticOperator();
		
		AbstractModule seedModule = geneticOperator.chooseModule(nucleus.getAllModules());
		AbstractNode seedRoot = geneticOperator.chooseBranchRoot(seedModule);
		
		List<AbstractNode> allNodes = geneticOperator.getDescendantNodes(seedRoot);
		AbstractNode newAdsContentRoot = allNodes.get(generator.nextInt(allNodes.size()));
		AbstractNode newAdsContentRootParent = newAdsContentRoot.getParent();
		
		ADS newAds = new ADS(
				adss.size(), 
				potentialAdsTypes.get(generator.nextInt(potentialAdsTypes.size())), 
				newAdsContentRoot.getReturnType(), 
				generator.nextInt(maxAdssSize));
		
		ArrayList<AbstractNode> writeProxyArgs = new ArrayList<AbstractNode>(newAds.getDimension() + 1);
		writeProxyArgs.add(newAdsContentRoot);
		
		List<AbstractNode> leaves = geneticOperator.getDescendantNodes(seedRoot, true);
		for(int i = 0; i < newAds.getDimension(); i++){
			AbstractNode arg = geneticOperator.chooseNodeIn(leaves, ADS.getAdressArgsType());
			if(arg != null)
				arg = arg.copy();
			else
				arg = geneticOperator.findExternalNode(ADS.getAdressArgsType());
			writeProxyArgs.add(arg);
		}
		
		ArrayList<AbstractNode> readProxyArgs = new ArrayList<AbstractNode>(newAds.getDimension());
		for(int i = 1; i < writeProxyArgs.size(); i++)
			readProxyArgs.add(writeProxyArgs.get(i).copy());
		
		ADSProxy newAdsWriteProxy = new ADSProxy(newAds, true);
		newAdsWriteProxy.setArgs(writeProxyArgs);
		
		ADSProxy newAdsReadProxy = new ADSProxy(newAds, false);
		newAdsReadProxy.setArgs(readProxyArgs);
		
		Glue glue = new Glue(newAdsContentRoot.getReturnType());
		glue.setArgs(Arrays.asList(newAdsWriteProxy, newAdsReadProxy));
		
		if(newAdsContentRootParent != null)
			newAdsContentRootParent.replaceArg(newAdsContentRoot, glue);
		else
			seedModule.replaceBranchRoot(newAdsContentRoot, glue);
		seedModule.consolidate();
		
		adss.add(newAds);
		
		return true;
	}
	
	@Override
	public final ADSCreation copy() {
		return new ADSCreation(maxAdss, maxAdssSize, potentialAdsTypes);
	}
}
