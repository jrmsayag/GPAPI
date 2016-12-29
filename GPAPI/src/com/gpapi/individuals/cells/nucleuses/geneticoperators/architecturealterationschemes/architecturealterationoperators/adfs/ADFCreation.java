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
 * ADFCreation.java
 * ---------------
 * (C) Copyright 2015-2016, by Jeremy Sayag and Contributors.
 *
 * Original Author:  Jeremy Sayag;
 * Contributor(s):   ;
 * 
 */


package com.gpapi.individuals.cells.nucleuses.geneticoperators.architecturealterationschemes.architecturealterationoperators.adfs;

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
import com.gpapi.individuals.cells.nucleuses.modules.adfs.ADF;
import com.gpapi.individuals.cells.nucleuses.modules.adfs.ADFProxy;
import com.gpapi.individuals.cells.nucleuses.modules.adss.ADSProxy;
import com.gpapi.individuals.cells.nucleuses.nodes.AbstractNode;
import com.gpapi.individuals.cells.nucleuses.types.AbstractType;



public final class ADFCreation implements ArchitectureAlterationOperatorInterface {

	private static final long serialVersionUID = -646902911029418351L;
	
	private final int maxAdfs;
	
	private final boolean strictParams;
	
	
	
	public ADFCreation(int maxAdfs, boolean strictParams){
		this.maxAdfs = maxAdfs;
		this.strictParams = strictParams;
	}
	public ADFCreation(int maxAdfs){
		this(maxAdfs, true);
	}
	public ADFCreation(boolean strictParams){
		this(Integer.MAX_VALUE, strictParams);
	}
	public ADFCreation(){
		this(Integer.MAX_VALUE);
	}
	
	
	@Override
	public final boolean architectureAlteration(Nucleus nucleus) {
		List<ADF> adfs = nucleus.getAdfs();
		if(adfs.size() >= maxAdfs)
			return false;
		
		ThreadLocalRandom generator = ThreadLocalRandom.current();
		GeneticOperatorInterface geneticOperator = nucleus.getGeneticOperator();
		
		List<AbstractModule> potentialModules = nucleus.getAllModules();
		if(!nucleus.isHierarchicalAdfs())
			potentialModules.removeAll(adfs);
		
		AbstractModule seedModule = geneticOperator.chooseModule(potentialModules);
		AbstractNode seedRoot = geneticOperator.chooseBranchRoot(seedModule);
		
		List<AbstractNode> allNodes = geneticOperator.getDescendantNodes(seedRoot);
		AbstractNode newAdfRoot = allNodes.get(generator.nextInt(allNodes.size()));
		
		AbstractNode newAdfRootBackup = newAdfRoot;
		AbstractNode newAdfRootParent = newAdfRoot.getParent();
		
		List<AbstractNode> newAdfNodesList = geneticOperator.getDescendantNodes(newAdfRoot);
		Set<AbstractNode> forbiddenNodes = buildForbiddenNodes(newAdfNodesList, nucleus.isHierarchicalAdfs());
		List<AbstractNode> argsRootNodes = pickArgsRootNodes(newAdfNodesList, forbiddenNodes, geneticOperator);
		
		List<AbstractType> newAdfArgsTypes = new ArrayList<AbstractType>(argsRootNodes.size());
		for(AbstractNode argRootNode : argsRootNodes)
			newAdfArgsTypes.add(argRootNode.getReturnType());
		
		List<ArgumentProxy> argsProxies = AbstractModule.createArgumentsProxiesFor(newAdfArgsTypes);
		for(int i = 0; i < argsRootNodes.size(); i++){
			AbstractNode argRootNode = argsRootNodes.get(i);
			if(argRootNode == newAdfRoot)
				newAdfRoot = argsProxies.get(i);
			else
				argRootNode.getParent().replaceArg(argRootNode, argsProxies.get(i));
		}
		
		int newAdfId = seedModule instanceof ADF ? seedModule.getId() : adfs.size();
		ADF newAdf = new ADF(newAdfId, newAdfRoot, newAdfArgsTypes);
		
		ADFProxy newAdfProxy = new ADFProxy(newAdf);
		newAdfProxy.setArgs(argsRootNodes);
		
		if(newAdfRootParent != null)
			newAdfRootParent.replaceArg(newAdfRootBackup, newAdfProxy);
		else
			seedModule.replaceBranchRoot(newAdfRootBackup, newAdfProxy);
		seedModule.consolidate();
		
		for(int i = newAdf.getId(); i < adfs.size(); i++)
			adfs.get(i).setId(i+1);
		adfs.add(newAdf.getId(), newAdf);
		
		return true;
	}
	private final List<AbstractNode> pickArgsRootNodes(
			List<AbstractNode> newAdfNodesList, 
			Set<AbstractNode> forbiddenNodes, 
			GeneticOperatorInterface geneticOperator){
		ThreadLocalRandom generator = ThreadLocalRandom.current();
		
		Set<AbstractNode> newAdfNodesSet = new HashSet<AbstractNode>(newAdfNodesList);
		
		ArrayList<AbstractNode> argsRootNodes = new ArrayList<AbstractNode>();
		while(!forbiddenNodes.isEmpty()){
			AbstractNode argRootNode = newAdfNodesList.get(generator.nextInt(newAdfNodesList.size()));
			
			AbstractNode ancestor = argRootNode.getParent();
			while(newAdfNodesSet.contains(ancestor)){
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
			while(newAdfNodesSet.remove(ancestor))
				ancestor = ancestor.getParent();
			
			newAdfNodesSet.removeAll(descendantNodes);
			newAdfNodesList = new ArrayList<AbstractNode>(newAdfNodesSet);
		}
		return argsRootNodes;
	}
	private final Set<AbstractNode> buildForbiddenNodes(
			List<AbstractNode> newAdfNodesList, 
			boolean hierarchicalAdfs){
		HashSet<AbstractNode> forbiddenNodes = new HashSet<AbstractNode>();
		for(AbstractNode node : newAdfNodesList){
			if(node instanceof AbstractProxyNode){
				if(!(hierarchicalAdfs && node instanceof ADFProxy) && 
						!(node instanceof ADSProxy))
					forbiddenNodes.add(node);
			}
		}
		return forbiddenNodes;
	}
	
	@Override
	public final ADFCreation copy() {
		return new ADFCreation(maxAdfs, strictParams);
	}
}
