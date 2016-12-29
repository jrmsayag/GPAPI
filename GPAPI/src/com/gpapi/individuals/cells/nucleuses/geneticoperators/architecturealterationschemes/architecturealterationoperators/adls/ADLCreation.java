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
 * ADLCreation.java
 * ---------------
 * (C) Copyright 2015-2016, by Jeremy Sayag and Contributors.
 *
 * Original Author:  Jeremy Sayag;
 * Contributor(s):   ;
 * 
 */


package com.gpapi.individuals.cells.nucleuses.geneticoperators.architecturealterationschemes.architecturealterationoperators.adls;

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
import com.gpapi.individuals.cells.nucleuses.modules.adls.ADL;
import com.gpapi.individuals.cells.nucleuses.modules.adls.ADLProxy;
import com.gpapi.individuals.cells.nucleuses.modules.adss.ADSProxy;
import com.gpapi.individuals.cells.nucleuses.nodes.AbstractNode;
import com.gpapi.individuals.cells.nucleuses.types.AbstractType;



public final class ADLCreation implements ArchitectureAlterationOperatorInterface {

	private static final long serialVersionUID = -646902911029418351L;
	
	private final int maxAdls;
	
	private final boolean strictParams;
	
	private final int maxCalls;
	
	private final int maxIterations;
	
	
	
	public ADLCreation(int maxAdls, boolean strictParams, int maxCalls, int maxIterations){
		this.maxAdls = maxAdls;
		this.strictParams = strictParams;
		this.maxCalls = maxCalls;
		this.maxIterations = maxIterations;
	}
	public ADLCreation(int maxAdls, int maxCalls, int maxIterations){
		this(maxAdls, true, maxCalls, maxIterations);
	}
	public ADLCreation(boolean strictParams, int maxCalls, int maxIterations){
		this(Integer.MAX_VALUE, strictParams, maxCalls, maxIterations);
	}
	public ADLCreation(int maxCalls, int maxIterations){
		this(Integer.MAX_VALUE, maxCalls, maxIterations);
	}
	
	
	@Override
	public final boolean architectureAlteration(Nucleus nucleus) {
		List<ADL> adls = nucleus.getAdls();
		if(adls.size() >= maxAdls)
			return false;
		
		ThreadLocalRandom generator = ThreadLocalRandom.current();
		GeneticOperatorInterface geneticOperator = nucleus.getGeneticOperator();
		
		List<AbstractModule> potentialModules = new ArrayList<AbstractModule>(nucleus.getRpbs());
		if(nucleus.isHierarchicalAdls())
			potentialModules.addAll(adls);
		
		AbstractModule seedModule = geneticOperator.chooseModule(potentialModules);
		AbstractNode seedRoot = geneticOperator.chooseBranchRoot(seedModule);
		
		List<AbstractNode> allNodes = geneticOperator.getDescendantNodes(seedRoot);
		AbstractNode newAdlBodyRoot = allNodes.get(generator.nextInt(allNodes.size()));
		
		AbstractNode newAdlBodyRootBackup = newAdlBodyRoot;
		AbstractNode newAdlBodyRootParent = newAdlBodyRoot.getParent();
		
		List<AbstractNode> newAdlBodyNodesList = geneticOperator.getDescendantNodes(newAdlBodyRoot);
		Set<AbstractNode> forbiddenNodes = buildForbiddenNodes(newAdlBodyNodesList, nucleus.isHierarchicalAdls());
		List<AbstractNode> argsRootNodes = pickArgsRootNodes(newAdlBodyNodesList, forbiddenNodes, geneticOperator);
		
		List<AbstractType> newAdlArgsTypes = new ArrayList<AbstractType>(argsRootNodes.size());
		for(AbstractNode argRootNode : argsRootNodes)
			newAdlArgsTypes.add(argRootNode.getReturnType());
		
		List<ArgumentProxy> argsProxies = AbstractModule.createArgumentsProxiesFor(newAdlArgsTypes);
		for(int i = 0; i < argsRootNodes.size(); i++){
			AbstractNode argRootNode = argsRootNodes.get(i);
			if(argRootNode == newAdlBodyRoot)
				newAdlBodyRoot = argsProxies.get(i);
			else
				argRootNode.getParent().replaceArg(argRootNode, argsProxies.get(i));
		}
		
		int newAdlId = seedModule instanceof ADL ? seedModule.getId() : adls.size();
		ADL newAdl = new ADL(
				newAdlId, 
				geneticOperator.findExternalNode(allNodes.get(generator.nextInt(allNodes.size())).getReturnType()), 
				geneticOperator.findExternalNode(ADL.getConditionReturnType()), 
				newAdlBodyRoot, 
				newAdlArgsTypes, 
				maxCalls, 
				maxIterations);
		
		ADLProxy newAdlProxy = new ADLProxy(newAdl);
		newAdlProxy.setArgs(argsRootNodes);
		
		if(newAdlBodyRootParent != null)
			newAdlBodyRootParent.replaceArg(newAdlBodyRootBackup, newAdlProxy);
		else
			seedModule.replaceBranchRoot(newAdlBodyRootBackup, newAdlProxy);
		seedModule.consolidate();
		
		for(int i = newAdl.getId(); i < adls.size(); i++)
			adls.get(i).setId(i+1);
		adls.add(newAdl.getId(), newAdl);
		
		return true;
	}
	private final List<AbstractNode> pickArgsRootNodes(
			List<AbstractNode> newAdlBodyNodesList, 
			Set<AbstractNode> forbiddenNodes, 
			GeneticOperatorInterface geneticOperator){
		ThreadLocalRandom generator = ThreadLocalRandom.current();
		
		Set<AbstractNode> newAdlBodyNodesSet = new HashSet<AbstractNode>(newAdlBodyNodesList);
		
		ArrayList<AbstractNode> argsRootNodes = new ArrayList<AbstractNode>();
		while(!forbiddenNodes.isEmpty()){
			AbstractNode argRootNode = newAdlBodyNodesList.get(generator.nextInt(newAdlBodyNodesList.size()));
			
			AbstractNode ancestor = argRootNode.getParent();
			while(newAdlBodyNodesSet.contains(ancestor)){
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
			while(newAdlBodyNodesSet.remove(ancestor))
				ancestor = ancestor.getParent();
			
			newAdlBodyNodesSet.removeAll(descendantNodes);
			newAdlBodyNodesList = new ArrayList<AbstractNode>(newAdlBodyNodesSet);
		}
		return argsRootNodes;
	}
	private final Set<AbstractNode> buildForbiddenNodes(
			List<AbstractNode> newAdlBodyNodesList, 
			boolean hierarchicalAdls){
		HashSet<AbstractNode> forbiddenNodes = new HashSet<AbstractNode>();
		for(AbstractNode node : newAdlBodyNodesList){
			if(node instanceof AbstractProxyNode){
				if(!(hierarchicalAdls && node instanceof ADLProxy) && 
						!(node instanceof ADFProxy) && 
						!(node instanceof ADSProxy))
					forbiddenNodes.add(node);
			}
		}
		return forbiddenNodes;
	}
	
	@Override
	public final ADLCreation copy() {
		return new ADLCreation(maxAdls, strictParams, maxCalls, maxIterations);
	}
}
