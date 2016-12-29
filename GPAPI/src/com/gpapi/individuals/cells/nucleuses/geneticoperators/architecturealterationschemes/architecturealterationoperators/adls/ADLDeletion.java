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
 * ADLDeletion.java
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
import com.gpapi.individuals.cells.nucleuses.modules.adls.ADL;
import com.gpapi.individuals.cells.nucleuses.modules.adls.ADLProxy;
import com.gpapi.individuals.cells.nucleuses.nodes.AbstractNode;



public final class ADLDeletion implements ArchitectureAlterationOperatorInterface {

	private static final long serialVersionUID = -7777814582712489448L;

	@Override
	public final boolean architectureAlteration(Nucleus nucleus) {
		List<ADL> adls = nucleus.getAdls();
		if(adls.isEmpty())
			return false;
		
		int deletedAdlId = ThreadLocalRandom.current().nextInt(adls.size());
		GeneticOperatorInterface geneticOperator = nucleus.getGeneticOperator();
		
		List<AbstractModule> referencingModules = new ArrayList<AbstractModule>(nucleus.getRpbs());
		if(nucleus.isHierarchicalAdls())
			referencingModules.addAll(adls.subList(deletedAdlId + 1, adls.size()));
		
		for(AbstractModule module : referencingModules){
			boolean consolidate = false;
			for(AbstractNode root : module.getBranchRoots()){
				if(adaptBranch(module, root, deletedAdlId, geneticOperator))
					consolidate = true;
			}
			if(consolidate)
				module.consolidate();
		}
		
		for(int i = deletedAdlId + 1; i < adls.size(); i++)
			adls.get(i).setId(i-1);
		adls.remove(deletedAdlId);
		
		return true;
	}
	
	private final boolean adaptBranch(AbstractModule module, AbstractNode root, int deletedAdlId, GeneticOperatorInterface geneticOperator){
		List<AbstractNode> adlProxiesToBeReplaced = new ArrayList<AbstractNode>();
		List<AbstractNode> potentialReplacingNodes = new ArrayList<AbstractNode>();
		
		for(AbstractNode node : geneticOperator.getDescendantNodes(root)){
			if(node instanceof ADLProxy && ((ADLProxy) node).getId() == deletedAdlId)
				adlProxiesToBeReplaced.add(node);
			else if(node.getArgs().isEmpty())
				potentialReplacingNodes.add(node);
		}
		
		for(AbstractNode adlProxy : adlProxiesToBeReplaced){
			AbstractNode replacingNode = geneticOperator.chooseNodeIn(potentialReplacingNodes, adlProxy.getReturnType());
			
			if(replacingNode != null)
				replacingNode = replacingNode.copy();
			else
				replacingNode = geneticOperator.findExternalNode(adlProxy.getReturnType());
			
			if(adlProxy != root)
				adlProxy.getParent().replaceArg(adlProxy, replacingNode);
			else
				module.replaceBranchRoot(adlProxy, replacingNode);
		}
		
		return !adlProxiesToBeReplaced.isEmpty();
	}
	
	@Override
	public final ADLDeletion copy() {
		return new ADLDeletion();
	}
}
