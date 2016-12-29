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
 * ADFDeletion.java
 * ---------------
 * (C) Copyright 2015-2016, by Jeremy Sayag and Contributors.
 *
 * Original Author:  Jeremy Sayag;
 * Contributor(s):   ;
 * 
 */


package com.gpapi.individuals.cells.nucleuses.geneticoperators.architecturealterationschemes.architecturealterationoperators.adfs;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ThreadLocalRandom;

import com.gpapi.individuals.cells.nucleuses.Nucleus;
import com.gpapi.individuals.cells.nucleuses.geneticoperators.GeneticOperatorInterface;
import com.gpapi.individuals.cells.nucleuses.geneticoperators.architecturealterationschemes.architecturealterationoperators.ArchitectureAlterationOperatorInterface;
import com.gpapi.individuals.cells.nucleuses.modules.AbstractModule;
import com.gpapi.individuals.cells.nucleuses.modules.adfs.ADF;
import com.gpapi.individuals.cells.nucleuses.modules.adfs.ADFProxy;
import com.gpapi.individuals.cells.nucleuses.nodes.AbstractNode;



public final class ADFDeletion implements ArchitectureAlterationOperatorInterface {

	private static final long serialVersionUID = -7777814582712489448L;

	@Override
	public final boolean architectureAlteration(Nucleus nucleus) {
		List<ADF> adfs = nucleus.getAdfs();
		if(adfs.isEmpty())
			return false;
		
		int deletedAdfId = ThreadLocalRandom.current().nextInt(adfs.size());
		adfs.remove(deletedAdfId);
		
		GeneticOperatorInterface geneticOperator = nucleus.getGeneticOperator();
		
		for(AbstractModule module : nucleus.getAllModules()){
			boolean consolidate = false;
			for(AbstractNode root : module.getBranchRoots()){
				if(adaptBranch(module, root, deletedAdfId, geneticOperator))
					consolidate = true;
			}
			if(consolidate)
				module.consolidate();
		}
		
		for(int i = deletedAdfId; i < adfs.size(); i++)
			adfs.get(i).setId(i);
		
		return true;
	}
	
	private final boolean adaptBranch(AbstractModule module, AbstractNode root, int deletedAdfId, GeneticOperatorInterface geneticOperator){
		List<AbstractNode> adfProxiesToBeReplaced = new ArrayList<AbstractNode>();
		List<AbstractNode> potentialReplacingNodes = new ArrayList<AbstractNode>();
		
		for(AbstractNode node : geneticOperator.getDescendantNodes(root)){
			if(node instanceof ADFProxy && ((ADFProxy) node).getId() == deletedAdfId)
				adfProxiesToBeReplaced.add(node);
			else if(node.getArgs().isEmpty())
				potentialReplacingNodes.add(node);
		}
		
		for(AbstractNode adfProxy : adfProxiesToBeReplaced){
			AbstractNode replacingNode = geneticOperator.chooseNodeIn(potentialReplacingNodes, adfProxy.getReturnType());
			
			if(replacingNode != null)
				replacingNode = replacingNode.copy();
			else
				replacingNode = geneticOperator.findExternalNode(adfProxy.getReturnType());
			
			if(adfProxy != root)
				adfProxy.getParent().replaceArg(adfProxy, replacingNode);
			else
				module.replaceBranchRoot(adfProxy, replacingNode);
		}
		
		return !adfProxiesToBeReplaced.isEmpty();
	}
	
	@Override
	public final ADFDeletion copy() {
		return new ADFDeletion();
	}
}
