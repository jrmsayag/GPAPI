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
 * ADIArgumentDeletion.java
 * ---------------
 * (C) Copyright 2015-2016, by Jeremy Sayag and Contributors.
 *
 * Original Author:  Jeremy Sayag;
 * Contributor(s):   ;
 * 
 */


package com.gpapi.individuals.cells.nucleuses.geneticoperators.architecturealterationschemes.architecturealterationoperators.adis;

import java.util.ArrayList;
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



public final class ADIArgumentDeletion implements ArchitectureAlterationOperatorInterface {

	private static final long serialVersionUID = 6755093040322751913L;
	
	@Override
	public final boolean architectureAlteration(Nucleus nucleus) {
		List<ADI> adis = nucleus.getAdis();
		if(adis.isEmpty())
			return false;
		
		ThreadLocalRandom generator = ThreadLocalRandom.current();
		ADI adi = adis.get(generator.nextInt(adis.size()));
		
		List<AbstractType> externalArgsTypes = adi.getExternalArgsTypes();
		if(externalArgsTypes.isEmpty())
			return false;
		
		int deletedArgId = ThreadLocalRandom.current().nextInt(externalArgsTypes.size());
		GeneticOperatorInterface geneticOperator = nucleus.getGeneticOperator();
		
		List<AbstractNode> argProxiesToBeReplaced = new ArrayList<AbstractNode>();
		List<AbstractNode> potentialReplacingNodes = new ArrayList<AbstractNode>();
		
		for(AbstractNode leaf : geneticOperator.getDescendantNodes(adi.getRoot(), true)){
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
			
			if(argProxy != adi.getRoot())
				argProxy.getParent().replaceArg(argProxy, replacingNode);
			else
				adi.replaceBranchRoot(argProxy, replacingNode);
		}
		
		externalArgsTypes.remove(deletedArgId);
		List<Argument> args = adi.getArgs();
		args.remove(deletedArgId);
		for(int i = deletedArgId; i < args.size(); i++)
			args.get(i).setId(i);
		
		List<AbstractModule> referencingModules = new ArrayList<AbstractModule>(nucleus.getRpbs());
		if(nucleus.isHierarchicalAdis())
			referencingModules.addAll(adis.subList(adi.getId() + 1, adis.size()));
		
		for(AbstractModule module : referencingModules){
			boolean consolidate = false;
			for(AbstractNode root : module.getBranchRoots()){
				for(AbstractNode node : geneticOperator.getDescendantNodes(root)){
					if(node instanceof ADIProxy){
						ADIProxy adiProxy = (ADIProxy) node;
						if(adiProxy.getId() == adi.getId()){
							adiProxy.getArgs().remove(deletedArgId);
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
	public final ADIArgumentDeletion copy() {
		return new ADIArgumentDeletion();
	}
}
