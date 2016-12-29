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
 * ADLArgumentDeletion.java
 * ---------------
 * (C) Copyright 2015-2016, by Jeremy Sayag and Contributors.
 *
 * Original Author:  Jeremy Sayag;
 * Contributor(s):   ;
 * 
 */


package com.gpapi.individuals.cells.nucleuses.geneticoperators.architecturealterationschemes.architecturealterationoperators.adls;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ThreadLocalRandom;

import com.gpapi.individuals.cells.nucleuses.Nucleus;
import com.gpapi.individuals.cells.nucleuses.geneticoperators.GeneticOperatorInterface;
import com.gpapi.individuals.cells.nucleuses.geneticoperators.architecturealterationschemes.architecturealterationoperators.ArchitectureAlterationOperatorInterface;
import com.gpapi.individuals.cells.nucleuses.modules.AbstractModule;
import com.gpapi.individuals.cells.nucleuses.modules.Argument;
import com.gpapi.individuals.cells.nucleuses.modules.ArgumentProxy;
import com.gpapi.individuals.cells.nucleuses.modules.adls.ADL;
import com.gpapi.individuals.cells.nucleuses.modules.adls.ADLProxy;
import com.gpapi.individuals.cells.nucleuses.nodes.AbstractNode;
import com.gpapi.individuals.cells.nucleuses.types.AbstractType;



public final class ADLArgumentDeletion implements ArchitectureAlterationOperatorInterface {

	private static final long serialVersionUID = 6755093040322751913L;
	
	@Override
	public final boolean architectureAlteration(Nucleus nucleus) {
		List<ADL> adls = nucleus.getAdls();
		if(adls.isEmpty())
			return false;
		
		ThreadLocalRandom generator = ThreadLocalRandom.current();
		ADL adl = adls.get(generator.nextInt(adls.size()));
		
		List<AbstractType> externalArgsTypes = adl.getExternalArgsTypes();
		if(externalArgsTypes.isEmpty())
			return false;
		
		int deletedArgId = ThreadLocalRandom.current().nextInt(externalArgsTypes.size());
		GeneticOperatorInterface geneticOperator = nucleus.getGeneticOperator();
		
		for(AbstractNode root : adl.getBranchRoots()){
			List<AbstractNode> argProxiesToBeReplaced = new ArrayList<AbstractNode>();
			List<AbstractNode> potentialReplacingNodes = new ArrayList<AbstractNode>();
			
			for(AbstractNode leaf : geneticOperator.getDescendantNodes(root, true)){
				if(leaf instanceof ArgumentProxy && ((ArgumentProxy) leaf).getId() == deletedArgId)
					argProxiesToBeReplaced.add(leaf);
				else
					potentialReplacingNodes.add(leaf);
			}
			
			for(AbstractNode argProxy : argProxiesToBeReplaced){
				AbstractNode replacingNode = geneticOperator.chooseNodeIn(potentialReplacingNodes, argProxy.getReturnType());
				
				if(replacingNode != null)
					replacingNode = replacingNode.copy();
				else
					replacingNode = geneticOperator.findExternalNode(argProxy.getReturnType());
				
				if(argProxy != root)
					argProxy.getParent().replaceArg(argProxy, replacingNode);
				else
					adl.replaceBranchRoot(argProxy, replacingNode);
			}
		}
		
		externalArgsTypes.remove(deletedArgId);
		List<Argument> args = adl.getArgs();
		args.remove(deletedArgId);
		for(int i = deletedArgId; i < args.size(); i++)
			args.get(i).setId(i);
		
		List<AbstractModule> referencingModules = new ArrayList<AbstractModule>(nucleus.getRpbs());
		if(nucleus.isHierarchicalAdls())
			referencingModules.addAll(adls.subList(adl.getId() + 1, adls.size()));
		
		for(AbstractModule module : referencingModules){
			boolean consolidate = false;
			for(AbstractNode root : module.getBranchRoots()){
				for(AbstractNode node : geneticOperator.getDescendantNodes(root)){
					if(node instanceof ADLProxy){
						ADLProxy adlProxy = (ADLProxy) node;
						if(adlProxy.getId() == adl.getId()){
							adlProxy.getArgs().remove(deletedArgId);
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

	@Override
	public final ADLArgumentDeletion copy() {
		return new ADLArgumentDeletion();
	}
}
