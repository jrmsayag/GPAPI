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
 * ADFArgumentDuplication.java
 * ---------------
 * (C) Copyright 2015-2016, by Jeremy Sayag and Contributors.
 *
 * Original Author:  Jeremy Sayag;
 * Contributor(s):   ;
 * 
 */


package com.gpapi.individuals.cells.nucleuses.geneticoperators.architecturealterationschemes.architecturealterationoperators.adfs;

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
import com.gpapi.individuals.cells.nucleuses.modules.adfs.ADF;
import com.gpapi.individuals.cells.nucleuses.modules.adfs.ADFProxy;
import com.gpapi.individuals.cells.nucleuses.nodes.AbstractNode;



public final class ADFArgumentDuplication implements ArchitectureAlterationOperatorInterface {
	
	private static final long serialVersionUID = -3062981998691992500L;
	
	private final int maxArgs;
	
	
	
	public ADFArgumentDuplication(int maxArgs){
		this.maxArgs = maxArgs;
	}
	public ADFArgumentDuplication(){
		this(Integer.MAX_VALUE);
	}
	
	@Override
	public final boolean architectureAlteration(Nucleus nucleus) {
		List<ADF> adfs = nucleus.getAdfs();
		if(adfs.isEmpty())
			return false;
		
		ThreadLocalRandom generator = ThreadLocalRandom.current();
		ADF adf = adfs.get(generator.nextInt(adfs.size()));
		
		List<Argument> args = adf.getArgs();
		if(args.isEmpty() || args.size() >= maxArgs)
			return false;
		
		Argument originalArg = args.get(generator.nextInt(args.size()));
		Argument duplicateArg = new Argument(originalArg.getId() + 1, originalArg.getReturnType());
		
		GeneticOperatorInterface geneticOperator = nucleus.getGeneticOperator();
		
		for(AbstractNode leaf : geneticOperator.getDescendantNodes(adf.getRoot(), true)){
			if(leaf instanceof ArgumentProxy){
				ArgumentProxy argProxy = (ArgumentProxy) leaf;
				if(argProxy.getId() == originalArg.getId() && generator.nextBoolean())
					argProxy.setArgument(duplicateArg);
			}
		}
		
		for(int i = duplicateArg.getId(); i < args.size(); i++)
			args.get(i).setId(i+1);
		args.add(duplicateArg.getId(), duplicateArg);
		
		for(AbstractModule module : nucleus.getAllModules()){
			boolean consolidate = false;
			for(AbstractNode root : module.getBranchRoots()){
				List<AbstractNode> branchNodes = geneticOperator.getDescendantNodes(root);
				Collections.reverse(branchNodes);
				for(AbstractNode node : branchNodes){
					if(node instanceof ADFProxy){
						ADFProxy adfProxy = (ADFProxy) node;
						if(adfProxy.getId() == adf.getId()){
							List<AbstractNode> newArgs = new ArrayList<AbstractNode>(adfProxy.getArgs());
							newArgs.add(duplicateArg.getId(), 
									geneticOperator.replicateSubtree(newArgs.get(originalArg.getId())));
							adfProxy.setArgs(newArgs);
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
	public final ADFArgumentDuplication copy() {
		return new ADFArgumentDuplication(maxArgs);
	}
}
