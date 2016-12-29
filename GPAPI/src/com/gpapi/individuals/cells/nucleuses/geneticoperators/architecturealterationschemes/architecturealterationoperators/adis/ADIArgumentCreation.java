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
 * ADIArgumentCreation.java
 * ---------------
 * (C) Copyright 2015-2016, by Jeremy Sayag and Contributors.
 *
 * Original Author:  Jeremy Sayag;
 * Contributor(s):   ;
 * 
 */


package com.gpapi.individuals.cells.nucleuses.geneticoperators.architecturealterationschemes.architecturealterationoperators.adis;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.concurrent.ThreadLocalRandom;

import com.gpapi.individuals.cells.nucleuses.Nucleus;
import com.gpapi.individuals.cells.nucleuses.geneticoperators.GeneticOperatorInterface;
import com.gpapi.individuals.cells.nucleuses.geneticoperators.architecturealterationschemes.architecturealterationoperators.ArchitectureAlterationOperatorInterface;
import com.gpapi.individuals.cells.nucleuses.modules.AbstractModule;
import com.gpapi.individuals.cells.nucleuses.modules.Argument;
import com.gpapi.individuals.cells.nucleuses.modules.ArgumentProxy;
import com.gpapi.individuals.cells.nucleuses.modules.adis.ADI;
import com.gpapi.individuals.cells.nucleuses.modules.adis.ADIProxy;
import com.gpapi.individuals.cells.nucleuses.nodes.AbstractNode;
import com.gpapi.individuals.cells.nucleuses.types.AbstractType;



public final class ADIArgumentCreation implements ArchitectureAlterationOperatorInterface {

	private static final long serialVersionUID = -7849750907395886653L;
	
	private final int maxArgs;
	
	
	
	public ADIArgumentCreation(int maxArgs){
		this.maxArgs = maxArgs;
	}
	public ADIArgumentCreation(){
		this(Integer.MAX_VALUE);
	}
	
	
	@Override
	public final boolean architectureAlteration(Nucleus nucleus) {
		List<ADI> adis = nucleus.getAdis();
		if(adis.isEmpty())
			return false;
		
		ADI adi = adis.get(ThreadLocalRandom.current().nextInt(adis.size()));
		
		List<AbstractType> externalArgsTypes = adi.getExternalArgsTypes();
		if(externalArgsTypes.size() >= maxArgs)
			return false;
		
		GeneticOperatorInterface geneticOperator = nucleus.getGeneticOperator();
		
		AbstractNode argSubtreeRoot = chooseArgSubtreeRoot(adi, geneticOperator);
		if(argSubtreeRoot == null)
			return false;
		
		Argument newArg = new Argument(externalArgsTypes.size(), argSubtreeRoot.getReturnType());
		
		List<Argument> args = adi.getArgs();
		for(int i = newArg.getId(); i < args.size(); i++)
			args.get(i).setId(i+1);
		
		args.add(newArg.getId(), newArg);
		externalArgsTypes.add(newArg.getReturnType());
		
		ArgumentProxy newArgProxy = new ArgumentProxy(newArg);
		if(argSubtreeRoot != adi.getRoot())
			argSubtreeRoot.getParent().replaceArg(argSubtreeRoot, newArgProxy);
		else
			adi.replaceBranchRoot(argSubtreeRoot, newArgProxy);
		adi.consolidate();
		
		List<AbstractModule> referencingModules = new ArrayList<AbstractModule>(nucleus.getRpbs());
		if(nucleus.isHierarchicalAdis())
			referencingModules.addAll(adis.subList(adi.getId() + 1, adis.size()));
		
		for(AbstractModule module : referencingModules){
			boolean consolidate = false;
			for(AbstractNode root : module.getBranchRoots()){
				List<AbstractNode> branchNodes = geneticOperator.getDescendantNodes(root);
				Collections.reverse(branchNodes);
				for(AbstractNode node : branchNodes){
					if(node instanceof ADIProxy){
						ADIProxy adiProxy = (ADIProxy) node;
						if(adiProxy.getId() == adi.getId()){
							adaptAdiCall(adiProxy, argSubtreeRoot, geneticOperator);
							consolidate = true;
						}
					}
				}
			}
			if(consolidate)
				module.consolidate();
		}
		
		return true;
	}
	private final AbstractNode chooseArgSubtreeRoot(ADI adi, GeneticOperatorInterface geneticOperator){
		List<AbstractNode> allNodesList = geneticOperator.getDescendantNodes(adi.getRoot());
		HashSet<AbstractNode> selectableNodesSet = new HashSet<AbstractNode>(allNodesList);
		
		for(AbstractNode node : allNodesList){
			if(node instanceof ArgumentProxy){
				ArgumentProxy argProxy = (ArgumentProxy) node;
				if(argProxy.getId() >= adi.getExternalArgsTypes().size()){
					AbstractNode ancestorNode = argProxy;
					while(selectableNodesSet.remove(ancestorNode))
						ancestorNode = ancestorNode.getParent();
				}
			}
		}
		
		if(!selectableNodesSet.isEmpty()){
			List<AbstractNode> selectableNodesList = new ArrayList<AbstractNode>(selectableNodesSet);
			return selectableNodesList.get(ThreadLocalRandom.current().nextInt(selectableNodesList.size()));
		} else
			return null;
	}
	private final void adaptAdiCall(ADIProxy adiProxy, AbstractNode originalArgSubtree, GeneticOperatorInterface geneticOperator){
		AbstractNode newArgSubtree = geneticOperator.replicateSubtree(originalArgSubtree);
		
		for(AbstractNode leaf : geneticOperator.getDescendantNodes(newArgSubtree, true)){
			if(leaf instanceof ArgumentProxy){
				ArgumentProxy argProxy = (ArgumentProxy) leaf;
				AbstractNode replacingSubtree = geneticOperator.replicateSubtree(adiProxy.getArgs().get(argProxy.getId()));
				if(argProxy != newArgSubtree)
					argProxy.getParent().replaceArg(argProxy, replacingSubtree);
				else
					newArgSubtree = replacingSubtree;
			}
		}
		
		List<AbstractNode> newArgs = new ArrayList<AbstractNode>(adiProxy.getArgs());
		newArgs.add(newArgSubtree);
		adiProxy.setArgs(newArgs);
	}

	@Override
	public final ADIArgumentCreation copy() {
		return new ADIArgumentCreation(maxArgs);
	}
}
