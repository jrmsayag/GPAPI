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
 * ADICreation.java
 * ---------------
 * (C) Copyright 2015-2016, by Jeremy Sayag and Contributors.
 *
 * Original Author:  Jeremy Sayag;
 * Contributor(s):   ;
 * 
 */


package com.gpapi.individuals.cells.nucleuses.geneticoperators.architecturealterationschemes.architecturealterationoperators.adis;

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
import com.gpapi.individuals.cells.nucleuses.modules.adis.ADI;
import com.gpapi.individuals.cells.nucleuses.modules.adis.ADIProxy;
import com.gpapi.individuals.cells.nucleuses.modules.adss.ADSProxy;
import com.gpapi.individuals.cells.nucleuses.nodes.AbstractNode;
import com.gpapi.individuals.cells.nucleuses.types.AbstractType;



public final class ADICreation implements ArchitectureAlterationOperatorInterface {

	private static final long serialVersionUID = -646902911029418351L;
	
	private final int maxAdis;
	
	private final boolean strictParams;
	
	private final int maxCalls;
	
	private final List<List<AbstractType>> potentialFieldsTypes;
	
	private final List<List<String>> potentialFieldsNames;
	
	
	
	public ADICreation(
			int maxAdis, 
			boolean strictParams, 
			int maxCalls, 
			List<List<AbstractType>> potentialFieldsTypes, 
			List<List<String>> potentialFieldsNames){
		this.maxAdis = maxAdis;
		this.strictParams = strictParams;
		this.maxCalls = maxCalls;
		
		this.potentialFieldsTypes = new ArrayList<List<AbstractType>>(potentialFieldsTypes.size());
		for(List<AbstractType> fieldsTypes : potentialFieldsTypes)
			this.potentialFieldsTypes.add(new ArrayList<AbstractType>(fieldsTypes));
		
		this.potentialFieldsNames = new ArrayList<List<String>>(potentialFieldsNames.size());
		for(List<String> fieldsNames : potentialFieldsNames)
			this.potentialFieldsNames.add(new ArrayList<String>(fieldsNames));
	}
	public ADICreation(int maxAdis, int maxCalls, List<List<AbstractType>> fieldsTypes, List<List<String>> fieldsNames){
		this(maxAdis, true, maxCalls, fieldsTypes, fieldsNames);
	}
	public ADICreation(boolean strictParams, int maxCalls, List<List<AbstractType>> fieldsTypes, List<List<String>> fieldsNames){
		this(Integer.MAX_VALUE, strictParams, maxCalls, fieldsTypes, fieldsNames);
	}
	public ADICreation(int maxCalls, List<List<AbstractType>> fieldsTypes, List<List<String>> fieldsNames){
		this(Integer.MAX_VALUE, maxCalls, fieldsTypes, fieldsNames);
	}
	
	
	@Override
	public final boolean architectureAlteration(Nucleus nucleus) {
		List<ADI> adis = nucleus.getAdis();
		if(adis.size() >= maxAdis)
			return false;
		
		ThreadLocalRandom generator = ThreadLocalRandom.current();
		GeneticOperatorInterface geneticOperator = nucleus.getGeneticOperator();
		
		List<AbstractModule> potentialModules = new ArrayList<AbstractModule>(nucleus.getRpbs());
		if(nucleus.isHierarchicalAdis())
			potentialModules.addAll(adis);
		
		AbstractModule seedModule = geneticOperator.chooseModule(potentialModules);
		AbstractNode seedRoot = geneticOperator.chooseBranchRoot(seedModule);
		
		List<AbstractNode> allNodes = geneticOperator.getDescendantNodes(seedRoot);
		AbstractNode newAdiRoot = allNodes.get(generator.nextInt(allNodes.size()));
		
		AbstractNode newAdiRootBackup = newAdiRoot;
		AbstractNode newAdiRootParent = newAdiRoot.getParent();
		
		List<AbstractNode> newAdiNodesList = geneticOperator.getDescendantNodes(newAdiRoot);
		Set<AbstractNode> forbiddenNodes = buildForbiddenNodes(newAdiNodesList, nucleus.isHierarchicalAdis());
		List<AbstractNode> argsRootNodes = pickArgsRootNodes(newAdiNodesList, forbiddenNodes, geneticOperator);
		
		List<AbstractType> newAdiArgsTypes = new ArrayList<AbstractType>(argsRootNodes.size());
		for(AbstractNode argRootNode : argsRootNodes)
			newAdiArgsTypes.add(argRootNode.getReturnType());
		
		List<ArgumentProxy> argsProxies = AbstractModule.createArgumentsProxiesFor(newAdiArgsTypes);
		for(int i = 0; i < argsRootNodes.size(); i++){
			AbstractNode argRootNode = argsRootNodes.get(i);
			if(argRootNode == newAdiRoot)
				newAdiRoot = argsProxies.get(i);
			else
				argRootNode.getParent().replaceArg(argRootNode, argsProxies.get(i));
		}
		
		int newAdiId = seedModule instanceof ADI ? seedModule.getId() : adis.size();
		int newAdiCollectionId = generator.nextInt(potentialFieldsTypes.size());
		ADI newAdi = new ADI(
				newAdiId, 
				newAdiRoot.getReturnType().copy(), 
				newAdiRoot, 
				newAdiArgsTypes, 
				newAdiCollectionId, 
				potentialFieldsTypes.get(newAdiCollectionId), 
				potentialFieldsNames.get(newAdiCollectionId), 
				maxCalls);
		
		ADIProxy newAdiProxy = new ADIProxy(newAdi);
		newAdiProxy.setArgs(argsRootNodes);
		
		if(newAdiRootParent != null)
			newAdiRootParent.replaceArg(newAdiRootBackup, newAdiProxy);
		else
			seedModule.replaceBranchRoot(newAdiRootBackup, newAdiProxy);
		seedModule.consolidate();
		
		for(int i = newAdi.getId(); i < adis.size(); i++)
			adis.get(i).setId(i+1);
		adis.add(newAdi.getId(), newAdi);
		
		return true;
	}
	private final List<AbstractNode> pickArgsRootNodes(
			List<AbstractNode> newAdiNodesList, 
			Set<AbstractNode> forbiddenNodes, 
			GeneticOperatorInterface geneticOperator){
		ThreadLocalRandom generator = ThreadLocalRandom.current();
		
		Set<AbstractNode> newAdiNodesSet = new HashSet<AbstractNode>(newAdiNodesList);
		
		ArrayList<AbstractNode> argsRootNodes = new ArrayList<AbstractNode>();
		while(!forbiddenNodes.isEmpty()){
			AbstractNode argRootNode = newAdiNodesList.get(generator.nextInt(newAdiNodesList.size()));
			
			AbstractNode ancestor = argRootNode.getParent();
			while(newAdiNodesSet.contains(ancestor)){
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
			while(newAdiNodesSet.remove(ancestor))
				ancestor = ancestor.getParent();
			
			newAdiNodesSet.removeAll(descendantNodes);
			newAdiNodesList = new ArrayList<AbstractNode>(newAdiNodesSet);
		}
		return argsRootNodes;
	}
	private final Set<AbstractNode> buildForbiddenNodes(
			List<AbstractNode> newAdiNodesList, 
			boolean hierarchicalAdis){
		HashSet<AbstractNode> forbiddenNodes = new HashSet<AbstractNode>();
		for(AbstractNode node : newAdiNodesList){
			if(node instanceof AbstractProxyNode){
				if(!(hierarchicalAdis && node instanceof ADIProxy) && 
						!(node instanceof ADFProxy) && 
						!(node instanceof ADSProxy))
					forbiddenNodes.add(node);
			}
		}
		return forbiddenNodes;
	}
	
	@Override
	public final ADICreation copy() {
		return new ADICreation(maxAdis, strictParams, maxCalls, potentialFieldsTypes, potentialFieldsNames);
	}
}
