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
 * AbstractCrossoverOperator.java
 * ---------------
 * (C) Copyright 2015-2016, by Jeremy Sayag and Contributors.
 *
 * Original Author:  Jeremy Sayag;
 * Contributor(s):   ;
 * 
 */


package com.gpapi.individuals.cells.nucleuses.geneticoperators;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import com.gpapi.individuals.cells.nucleuses.geneticoperators.crossoverschemes.crossovernodesselectionschemes.CrossoverNodesSelectionSchemeInterface;
import com.gpapi.individuals.cells.nucleuses.geneticoperators.crossoverschemes.crossoverplanningschemes.CrossoverPlanningSchemeInterface;
import com.gpapi.individuals.cells.nucleuses.geneticoperators.mutationschemes.MutationSchemeInterface;
import com.gpapi.individuals.cells.nucleuses.modules.AbstractProxyNode;
import com.gpapi.individuals.cells.nucleuses.nodes.AbstractNode;



public abstract class AbstractCrossoverOperator implements GeneticOperatorInterface {
	
	private static final long serialVersionUID = -6072424959696728544L;
	
	
	private List<AbstractProxyNode> localExternalNodes = Collections.unmodifiableList(new ArrayList<AbstractProxyNode>());
	@Override
	public final List<AbstractProxyNode> getLocalExternalNodes() {
		return localExternalNodes;
	}
	@Override
	public final void setLocalExternalNodes(List<AbstractProxyNode> localExternalNodes) {
		if(localExternalNodes == null)
			throw new NullPointerException("localExternalNodes can't be null!");
		else
			this.localExternalNodes = Collections.unmodifiableList(new ArrayList<AbstractProxyNode>(localExternalNodes));
	}
	
	
	private List<AbstractProxyNode> localInternalNodes = Collections.unmodifiableList(new ArrayList<AbstractProxyNode>());
	@Override
	public final List<AbstractProxyNode> getLocalInternalNodes() {
		return localInternalNodes;
	}
	@Override
	public final void setLocalInternalNodes(List<AbstractProxyNode> localInternalNodes) {
		if(localInternalNodes == null)
			throw new NullPointerException("localInternalNodes can't be null!");
		else
			this.localInternalNodes = Collections.unmodifiableList(new ArrayList<AbstractProxyNode>(localInternalNodes));
	}
	
	
	private final List<AbstractNode> globalExternalNodes;
	@Override
	public final List<AbstractNode> getGlobalExternalNodes(){
		return globalExternalNodes;
	}
	
	
	private final List<AbstractNode> globalInternalNodes;
	@Override
	public final List<AbstractNode> getGlobalInternalNodes(){
		return globalInternalNodes;
	}
	
	
	private final CrossoverPlanningSchemeInterface crossoverPlanningScheme;
	public final CrossoverPlanningSchemeInterface getCrossoverPlanningScheme() {
		return crossoverPlanningScheme;
	}
	
	
	private final CrossoverNodesSelectionSchemeInterface crossoverNodesSelectionScheme;
	public final CrossoverNodesSelectionSchemeInterface getCrossoverNodesSelectionScheme() {
		return crossoverNodesSelectionScheme;
	}


	private final MutationSchemeInterface mutationScheme;
	public final MutationSchemeInterface getMutationScheme() {
		return mutationScheme;
	}
	
	
	
	/**
	 * TODO : Description.
	 * 
	 * @param mutationScheme
	 * @param architectureAlteringOperator
	 * @param globalExternalNodes
	 * @param globalInternalNodes
	 * @param crossoverPoints
	 */
	public AbstractCrossoverOperator(
			CrossoverPlanningSchemeInterface crossoverPlanningScheme, 
			CrossoverNodesSelectionSchemeInterface crossoverNodesSelectionScheme, 
			MutationSchemeInterface mutationScheme, 
			List<AbstractNode> globalExternalNodes, 
			List<AbstractNode> globalInternalNodes){
		this.crossoverPlanningScheme = crossoverPlanningScheme.copy();
		this.crossoverNodesSelectionScheme = crossoverNodesSelectionScheme.copy();
		this.mutationScheme = mutationScheme.copy();
		
		this.globalExternalNodes = Collections.unmodifiableList(new ArrayList<AbstractNode>(globalExternalNodes));
		this.globalInternalNodes = Collections.unmodifiableList(new ArrayList<AbstractNode>(globalInternalNodes));
	}
}
