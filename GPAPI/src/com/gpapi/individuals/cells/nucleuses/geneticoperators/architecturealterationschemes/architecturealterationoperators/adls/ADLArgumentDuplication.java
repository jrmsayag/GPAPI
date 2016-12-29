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
 * ADLArgumentDuplication.java
 * ---------------
 * (C) Copyright 2015-2016, by Jeremy Sayag and Contributors.
 *
 * Original Author:  Jeremy Sayag;
 * Contributor(s):   ;
 * 
 */


package com.gpapi.individuals.cells.nucleuses.geneticoperators.architecturealterationschemes.architecturealterationoperators.adls;

import java.util.ArrayList;
import java.util.Collections;
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



public final class ADLArgumentDuplication implements ArchitectureAlterationOperatorInterface {
	
	private static final long serialVersionUID = -3062981998691992500L;
	
	private final int maxArgs;
	
	
	
	public ADLArgumentDuplication(int maxArgs){
		this.maxArgs = maxArgs;
	}
	public ADLArgumentDuplication(){
		this(Integer.MAX_VALUE);
	}
	
	@Override
	public final boolean architectureAlteration(Nucleus nucleus) {
		List<ADL> adls = nucleus.getAdls();
		if(adls.isEmpty())
			return false;
		
		ThreadLocalRandom generator = ThreadLocalRandom.current();
		ADL adl = adls.get(generator.nextInt(adls.size()));
		
		List<AbstractType> externalArgsTypes = adl.getExternalArgsTypes();
		if(externalArgsTypes.isEmpty() || externalArgsTypes.size() >= maxArgs)
			return false;
		
		int originalArgId = generator.nextInt(externalArgsTypes.size());
		Argument duplicateArg = new Argument(originalArgId + 1, externalArgsTypes.get(originalArgId));
		
		GeneticOperatorInterface geneticOperator = nucleus.getGeneticOperator();
		
		for(AbstractNode leaf : geneticOperator.getDescendantNodes(adl.getBranchRoots(), true)){
			if(leaf instanceof ArgumentProxy){
				ArgumentProxy argProxy = (ArgumentProxy) leaf;
				if(argProxy.getId() == originalArgId && generator.nextBoolean())
					argProxy.setArgument(duplicateArg);
			}
		}
		
		List<Argument> args = adl.getArgs();
		for(int i = duplicateArg.getId(); i < args.size(); i++)
			args.get(i).setId(i+1);
		
		args.add(duplicateArg.getId(), duplicateArg);
		externalArgsTypes.add(duplicateArg.getId(), duplicateArg.getReturnType());
		
		List<AbstractModule> referencingModules = new ArrayList<AbstractModule>(nucleus.getRpbs());
		if(nucleus.isHierarchicalAdls())
			referencingModules.addAll(adls.subList(adl.getId() + 1, adls.size()));
		
		for(AbstractModule module : referencingModules){
			boolean consolidate = false;
			for(AbstractNode root : module.getBranchRoots()){
				List<AbstractNode> branchNodes = geneticOperator.getDescendantNodes(root);
				Collections.reverse(branchNodes);
				for(AbstractNode node : branchNodes){
					if(node instanceof ADLProxy){
						ADLProxy adlProxy = (ADLProxy) node;
						if(adlProxy.getId() == adl.getId()){
							List<AbstractNode> newArgs = new ArrayList<AbstractNode>(adlProxy.getArgs());
							newArgs.add(duplicateArg.getId(), 
									geneticOperator.replicateSubtree(newArgs.get(originalArgId)));
							adlProxy.setArgs(newArgs);
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
	public final ADLArgumentDuplication copy() {
		return new ADLArgumentDuplication(maxArgs);
	}
}
