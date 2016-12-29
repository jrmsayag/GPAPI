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
 * ADRCreation.java
 * ---------------
 * (C) Copyright 2015-2016, by Jeremy Sayag and Contributors.
 *
 * Original Author:  Jeremy Sayag;
 * Contributor(s):   ;
 * 
 */


package com.gpapi.individuals.cells.nucleuses.geneticoperators.architecturealterationschemes.architecturealterationoperators.adrs;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.concurrent.ThreadLocalRandom;

import com.gpapi.individuals.cells.nucleuses.Nucleus;
import com.gpapi.individuals.cells.nucleuses.geneticoperators.GeneticOperatorInterface;
import com.gpapi.individuals.cells.nucleuses.geneticoperators.architecturealterationschemes.architecturealterationoperators.ArchitectureAlterationOperatorInterface;
import com.gpapi.individuals.cells.nucleuses.modules.AbstractModule;
import com.gpapi.individuals.cells.nucleuses.modules.AbstractProxyNode;
import com.gpapi.individuals.cells.nucleuses.modules.ArgumentProxy;
import com.gpapi.individuals.cells.nucleuses.modules.adfs.ADFProxy;
import com.gpapi.individuals.cells.nucleuses.modules.adrs.ADR;
import com.gpapi.individuals.cells.nucleuses.modules.adrs.ADRProxy;
import com.gpapi.individuals.cells.nucleuses.modules.adss.ADSProxy;
import com.gpapi.individuals.cells.nucleuses.nodes.AbstractNode;
import com.gpapi.individuals.cells.nucleuses.types.AbstractType;



public final class ADRCreation implements ArchitectureAlterationOperatorInterface {

	private static final long serialVersionUID = -646902911029418351L;
	
	private final int maxAdrs;
	
	private final boolean strictParams;
	
	private final int maxCalls;
	
	private final int maxRecursionDepth;
	
	
	
	public ADRCreation(int maxAdrs, boolean strictParams, int maxCalls, int maxRecursionDepth){
		this.maxAdrs = maxAdrs;
		this.strictParams = strictParams;
		this.maxCalls = maxCalls;
		this.maxRecursionDepth = maxRecursionDepth;
	}
	public ADRCreation(int maxAdrs, int maxCalls, int maxRecursionDepth){
		this(maxAdrs, true, maxCalls, maxRecursionDepth);
	}
	public ADRCreation(boolean strictParams, int maxCalls, int maxRecursionDepth){
		this(Integer.MAX_VALUE, strictParams, maxCalls, maxRecursionDepth);
	}
	public ADRCreation(int maxCalls, int maxRecursionDepth){
		this(Integer.MAX_VALUE, maxCalls, maxRecursionDepth);
	}
	
	
	@Override
	public final boolean architectureAlteration(Nucleus nucleus) {
		List<ADR> adrs = nucleus.getAdrs();
		if(adrs.size() >= maxAdrs)
			return false;
		
		ThreadLocalRandom generator = ThreadLocalRandom.current();
		GeneticOperatorInterface geneticOperator = nucleus.getGeneticOperator();
		
		List<AbstractModule> potentialModules = new ArrayList<AbstractModule>(nucleus.getRpbs());
		if(nucleus.isHierarchicalAdrs())
			potentialModules.addAll(adrs);
		
		AbstractModule seedModule = geneticOperator.chooseModule(potentialModules);
		AbstractNode seedRoot = geneticOperator.chooseBranchRoot(seedModule);
		
		List<AbstractNode> allNodes = geneticOperator.getDescendantNodes(seedRoot);
		AbstractNode newAdrBodyRoot = allNodes.get(generator.nextInt(allNodes.size()));
		
		AbstractNode newAdrBodyRootBackup = newAdrBodyRoot;
		AbstractNode newAdrBodyRootParent = newAdrBodyRoot.getParent();
		
		List<AbstractNode> newAdrBodyNodesList = geneticOperator.getDescendantNodes(newAdrBodyRoot);
		Set<AbstractNode> forbiddenNodes = buildForbiddenNodes(seedModule, newAdrBodyNodesList, nucleus.isHierarchicalAdrs());
		List<AbstractNode> argsRootNodes = pickArgsRootNodes(newAdrBodyNodesList, forbiddenNodes, geneticOperator);
		
		List<AbstractType> newAdrArgsTypes = new ArrayList<AbstractType>(argsRootNodes.size());
		for(AbstractNode argRootNode : argsRootNodes)
			newAdrArgsTypes.add(argRootNode.getReturnType());
		
		List<ArgumentProxy> argsProxies = AbstractModule.createArgumentsProxiesFor(newAdrArgsTypes);
		for(int i = 0; i < argsRootNodes.size(); i++){
			AbstractNode argRootNode = argsRootNodes.get(i);
			if(argRootNode == newAdrBodyRoot)
				newAdrBodyRoot = argsProxies.get(i);
			else
				argRootNode.getParent().replaceArg(argRootNode, argsProxies.get(i));
		}
		
		int newAdrId = seedModule instanceof ADR ? seedModule.getId() : adrs.size();
		ADR newAdr = new ADR(
				newAdrId, 
				geneticOperator.findExternalNode(ADR.getConditionReturnType()), 
				newAdrBodyRoot, 
				geneticOperator.replicateSubtree(newAdrBodyRoot), 
				newAdrArgsTypes, 
				maxCalls, 
				maxRecursionDepth);
		
		ADRProxy newAdrProxy = new ADRProxy(newAdr);
		newAdrProxy.setArgs(argsRootNodes);
		
		if(newAdrBodyRootParent != null)
			newAdrBodyRootParent.replaceArg(newAdrBodyRootBackup, newAdrProxy);
		else
			seedModule.replaceBranchRoot(newAdrBodyRootBackup, newAdrProxy);
		seedModule.consolidate();
		
		for(int i = newAdr.getId(); i < adrs.size(); i++)
			adrs.get(i).setId(i+1);
		adrs.add(newAdr.getId(), newAdr);
		
		return true;
	}
	private final List<AbstractNode> pickArgsRootNodes(
			List<AbstractNode> newAdrBodyNodesList, 
			Set<AbstractNode> forbiddenNodes, 
			GeneticOperatorInterface geneticOperator){
		ThreadLocalRandom generator = ThreadLocalRandom.current();
		
		Set<AbstractNode> newAdrBodyNodesSet = new HashSet<AbstractNode>(newAdrBodyNodesList);
		
		ArrayList<AbstractNode> argsRootNodes = new ArrayList<AbstractNode>();
		while(!forbiddenNodes.isEmpty()){
			AbstractNode argRootNode = newAdrBodyNodesList.get(generator.nextInt(newAdrBodyNodesList.size()));
			
			AbstractNode ancestor = argRootNode.getParent();
			while(newAdrBodyNodesSet.contains(ancestor)){
				if(forbiddenNodes.contains(ancestor))
					argRootNode = ancestor;
				ancestor = ancestor.getParent();
			}
			
			List<AbstractNode> descendantNodes = geneticOperator.getDescendantNodes(argRootNode);
			
			boolean buildNewArg = !strictParams;
			for(AbstractNode descendantNode : descendantNodes){
				if(buildNewArg || forbiddenNodes.contains(descendantNode)){
					buildNewArg = true;
					break;
				}
			}
			
			if(buildNewArg){
				argsRootNodes.add(argRootNode);
				forbiddenNodes.removeAll(descendantNodes);
			}
			
			ancestor = argRootNode.getParent();
			while(newAdrBodyNodesSet.remove(ancestor))
				ancestor = ancestor.getParent();
			
			newAdrBodyNodesSet.removeAll(descendantNodes);
			newAdrBodyNodesList = new ArrayList<AbstractNode>(newAdrBodyNodesSet);
		}
		return argsRootNodes;
	}
	private final Set<AbstractNode> buildForbiddenNodes(
			AbstractModule seedModule, 
			List<AbstractNode> newAdrBodyNodesList, 
			boolean hierarchicalAdrs){
		HashSet<AbstractNode> forbiddenNodes = new HashSet<AbstractNode>();
		for(AbstractNode node : newAdrBodyNodesList){
			if(node instanceof AbstractProxyNode){
				if(node instanceof ADRProxy){
					ADRProxy adrProxy = (ADRProxy) node;
					if(!hierarchicalAdrs)
						forbiddenNodes.add(node);
					else if(seedModule instanceof ADR && adrProxy.getId() == seedModule.getId())
						forbiddenNodes.add(node);
				} else if(!(node instanceof ADFProxy || node instanceof ADSProxy))
					forbiddenNodes.add(node);
			}
		}
		return forbiddenNodes;
	}
	
	@Override
	public final ADRCreation copy() {
		return new ADRCreation(maxAdrs, strictParams, maxCalls, maxRecursionDepth);
	}
}
