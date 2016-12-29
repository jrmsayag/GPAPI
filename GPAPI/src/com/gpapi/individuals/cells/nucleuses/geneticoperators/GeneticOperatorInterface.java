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
 * GeneticOperatorInterface.java
 * ---------------
 * (C) Copyright 2015-2016, by Jeremy Sayag and Contributors.
 *
 * Original Author:  Jeremy Sayag;
 * Contributor(s):   ;
 * 
 */


package com.gpapi.individuals.cells.nucleuses.geneticoperators;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.ThreadLocalRandom;

import com.gpapi.individuals.cells.nucleuses.Nucleus;
import com.gpapi.individuals.cells.nucleuses.modules.AbstractModule;
import com.gpapi.individuals.cells.nucleuses.modules.AbstractProxyNode;
import com.gpapi.individuals.cells.nucleuses.modules.adfs.ADFProxy;
import com.gpapi.individuals.cells.nucleuses.modules.adis.ADIProxy;
import com.gpapi.individuals.cells.nucleuses.modules.adls.ADLProxy;
import com.gpapi.individuals.cells.nucleuses.modules.adrs.ADRProxy;
import com.gpapi.individuals.cells.nucleuses.modules.adss.ADSProxy;
import com.gpapi.individuals.cells.nucleuses.nodes.AbstractNode;
import com.gpapi.individuals.cells.nucleuses.types.AbstractType;



public interface GeneticOperatorInterface extends Serializable {
	
	/**
	 * TODO : Description.
	 * 
	 * @return
	 */
	public List<AbstractProxyNode> getLocalExternalNodes();
	/**
	 * TODO : Description.
	 * 
	 * @return
	 */
	public void setLocalExternalNodes(List<AbstractProxyNode> localExternalNodes);
	
	
	/**
	 * TODO : Description.
	 * 
	 * @return
	 */
	public List<AbstractProxyNode> getLocalInternalNodes();
	/**
	 * TODO : Description.
	 * 
	 * @return
	 */
	public void setLocalInternalNodes(List<AbstractProxyNode> localInternalNodes);
	
	
	/**
	 * TODO : Description.
	 * 
	 * @return
	 */
	public List<AbstractNode> getGlobalExternalNodes();
	/**
	 * TODO : Description.
	 * 
	 * @return
	 */
	public List<AbstractNode> getGlobalInternalNodes();
	
	
	/**
	 * TODO : Description.
	 * 
	 * @return
	 */
	public default List<AbstractNode> getExternalNodes(){
		ArrayList<AbstractNode> externalNodes = new ArrayList<AbstractNode>(getGlobalExternalNodes().size() + getLocalExternalNodes().size());
		externalNodes.addAll(getGlobalExternalNodes());
		externalNodes.addAll(getLocalExternalNodes());
		return externalNodes;
	}
	/**
	 * TODO : Description.
	 * 
	 * @return
	 */
	public default List<AbstractNode> getInternalNodes(){
		ArrayList<AbstractNode> internalNodes = new ArrayList<AbstractNode>(getGlobalInternalNodes().size() + getLocalInternalNodes().size());
		internalNodes.addAll(getGlobalInternalNodes());
		internalNodes.addAll(getLocalInternalNodes());
		return internalNodes;
	}
	
	
	
	/**
	 * TODO : Description.
	 * 
	 * @param adssProxies
	 * @param adfsProxies
	 * @param adisProxies
	 * @param adrsProxies
	 * @param adlsProxies
	 * @param argumentsTypes
	 * @param argumentsNames
	 * @return
	 */
	public default List<List<AbstractProxyNode>> buildLocalNodes(
			List<ADSProxy> adssProxies, 
			List<ADFProxy> adfsProxies, 
			List<ADIProxy> adisProxies, 
			List<ADRProxy> adrsProxies, 
			List<ADLProxy> adlsProxies, 
			List<AbstractType> argumentsTypes){
		ArrayList<AbstractProxyNode> localExternalNodes = new ArrayList<AbstractProxyNode>();
		ArrayList<AbstractProxyNode> localInternalNodes = new ArrayList<AbstractProxyNode>();
		
		for(ADSProxy newAdsProxy : adssProxies){
			if(newAdsProxy.getArgsTypes().isEmpty())
				localExternalNodes.add(newAdsProxy);
			else
				localInternalNodes.add(newAdsProxy);
		}
		
		for(ADFProxy newAdfProxy : adfsProxies){
			if(newAdfProxy.getArgsTypes().isEmpty())
				localExternalNodes.add(newAdfProxy);
			else
				localInternalNodes.add(newAdfProxy);
		}
		
		for(ADIProxy newAdiProxy : adisProxies){
			if(newAdiProxy.getArgsTypes().isEmpty())
				localExternalNodes.add(newAdiProxy);
			else
				localInternalNodes.add(newAdiProxy);
		}
		
		for(ADRProxy newAdrProxy : adrsProxies){
			if(newAdrProxy.getArgsTypes().isEmpty())
				localExternalNodes.add(newAdrProxy);
			else
				localInternalNodes.add(newAdrProxy);
		}
		
		for(ADLProxy newAdlProxy : adlsProxies){
			if(newAdlProxy.getArgsTypes().isEmpty())
				localExternalNodes.add(newAdlProxy);
			else
				localInternalNodes.add(newAdlProxy);
		}
		
		localExternalNodes.addAll(AbstractModule.createArgumentsProxiesFor(argumentsTypes));
		
		return Arrays.asList(localExternalNodes, localInternalNodes);
	}
	
	
	
	
	
	/**
	 * Returns a copy of this genetic operator. This copy will typically be used 
	 * in egg cells created by this GeneticOperatorInterface, or in original 
	 * individuals created by populations.
	 * For this reason, every object that is referenced by this GeneticOperatorInterface 
	 * and cannot or must not be shared between individuals, must be copied as well.
	 * This is typically the case of every object that could really be considered to be
	 * part of the genetic operator, for instance the functions and terminals list
	 * (the functions and terminals themselves do not necessarily need to be copied
	 * since a genetic operator would typically use their copy() or generateNew()
	 * method), the mutation operators, etc.
	 * 
	 * @return
	 */
	public GeneticOperatorInterface copy();
	
	/**
	 * TODO : Description.
	 * 
	 * @param parent1Nucleus
	 * 			The {@link Nucleus} this genetic operator belongs to (i.e. the Nucleus
	 * 			from the parent whose makeChildWith() method has been invoked).
	 * @param parent2Nucleus
	 * 			The Nucleus from the other parent (parent2).
	 * @return
	 */
	public Nucleus mergeNucleuses(Nucleus parent1Nucleus, Nucleus parent2Nucleus);
	
	
	
	
	
	/**
	 * TODO : Description.
	 * <p>
	 * PTC2 algorithm from <em>Two Fast Tree-Creation Algorithms for Genetic Programming</em> (Luke 2000).
	 * 
	 * @param returnType
	 * @param size
	 * @return
	 */
	/*
	 *  TODO : Improve the implementation in order to remove the small bias in favor
	 *  of bigger trees due to internal nodes arities.
	 */
	public default AbstractNode generateTree(AbstractType returnType, int size){
		if(size <= 1)
			return findExternalNode(returnType);
		
		ThreadLocalRandom generator = ThreadLocalRandom.current();
		
		size--;
		AbstractNode rootNode = findInternalNode(returnType);
		
		ArrayList<AbstractNode> slots = new ArrayList<AbstractNode>(size);
		
		ArrayList<AbstractNode> tempArgs = new ArrayList<AbstractNode>();
		for(AbstractType type : rootNode.getArgsTypes())
			tempArgs.add(findExternalNode(type));
		rootNode.setArgs(tempArgs);
		slots.addAll(tempArgs);
		
		while(size > slots.size()){
			AbstractNode slot = slots.remove(generator.nextInt(slots.size()));
			
			size--;
			AbstractNode node = findInternalNode(slot.getReturnType());
			slot.getParent().replaceArg(slot, node);
			
			tempArgs.clear();
			for(AbstractType type : node.getArgsTypes())
				tempArgs.add(findExternalNode(type));
			node.setArgs(tempArgs);
			slots.addAll(tempArgs);
		}
		
		for(AbstractNode slot : slots)
			slot.getParent().replaceArg(slot, findExternalNode(slot.getReturnType()));
		
		return rootNode;
	}
	
	
	
	
	/**
	 * TODO : Description.
	 * 
	 * @param root
	 * @return
	 */
	public default AbstractNode replicateSubtree(AbstractNode root){
		AbstractNode newRoot = root.copy();
		
		ArrayList<AbstractNode> newRootArgs = new ArrayList<AbstractNode>(root.getArgs().size());
		for(AbstractNode arg : root.getArgs())
			newRootArgs.add(replicateSubtree(arg));
		newRoot.setArgs(newRootArgs);
		
		return newRoot;
	}
	
	
	
	
	/**
	 * Returns a list containing all nodes that are in the subtrees starting
	 * at the given root nodes (including the root nodes).
	 * <p>
	 * Nodes in the returned list must be ordered in increasing depth, i.e.
	 * a node appearing before another node is ensured to have same or
	 * lower depth than this other node (root nodes are considered to have
	 * a depth of 0). Note that this implies that the list returned starts 
	 * with the given root nodes.
	 * 
	 * @param rootNodes
	 * 			The root nodes from which the subtrees whose nodes are to
	 * 			be listed start.
	 * @param leavesOnly
	 * 			If this parameter is true, only the leaf nodes are included
	 * 			in the returned list (still in increasing depth order).
	 * @return
	 * 			The list containing the nodes to be listed.
	 */
	public default List<AbstractNode> getDescendantNodes(List<AbstractNode> rootNodes, boolean leavesOnly){
		ArrayList<AbstractNode> descendantNodes = new ArrayList<AbstractNode>();
		ArrayList<AbstractNode> nextLevel = new ArrayList<AbstractNode>(rootNodes);
		
		while(!nextLevel.isEmpty()){
			ArrayList<AbstractNode> currentLevel = nextLevel;
			
			if(!leavesOnly)
				descendantNodes.addAll(currentLevel);
			
			nextLevel = new ArrayList<AbstractNode>();
			for(AbstractNode node : currentLevel){
				if(!nextLevel.addAll(node.getArgs()) && leavesOnly)
					descendantNodes.add(node);
			}
		}
		
		return descendantNodes;
	}
	public default List<AbstractNode> getDescendantNodes(List<AbstractNode> rootNodes){
		return getDescendantNodes(rootNodes, false);
	}
	public default List<AbstractNode> getDescendantNodes(AbstractNode rootNode, boolean leavesOnly){
		return getDescendantNodes(Arrays.asList(rootNode), leavesOnly);
	}
	public default List<AbstractNode> getDescendantNodes(AbstractNode rootNode){
		return getDescendantNodes(rootNode, false);
	}
	
	
	
	
	/**
	 * Randomly chooses a module in the given list, with each module having a
	 * probability of being chosen that is either proportional to its size 
	 * (as given by its AbstractModule.getSize() method), or uniform, depending
	 * on the value of the sizeProportional parameter.
	 * 
	 * @param potentialModules
	 * 			The list of modules that can be chosen.
	 * @param sizeProportional
	 * 			A parameter to indicate if all modules have the same probability of
	 * 			being chosen or if the probabilities must be size-weighted.
	 * @return
	 * 			The chosen module.
	 */
	public default AbstractModule chooseModule(List<AbstractModule> potentialModules, boolean sizeProportional){
		if(potentialModules == null)
			throw new NullPointerException("Potential modules must be specified!");
		else if(potentialModules.isEmpty())
			throw new IllegalArgumentException("Potential modules can't be empty!");
		
		AbstractModule chosenModule = null;
		if(!sizeProportional)
			chosenModule = potentialModules.get(ThreadLocalRandom.current().nextInt(potentialModules.size()));
		else {
			int potentialModulesWeight = 0;
			for(AbstractModule module : potentialModules)
				potentialModulesWeight += module.getSize();
			
			int count = 0;
			int random = ThreadLocalRandom.current().nextInt(potentialModulesWeight);
			
			for(AbstractModule module : potentialModules){
				count += module.getSize();
				if(random < count){
					chosenModule = module;
					break;
				}
			}
		}
		return chosenModule;
	}
	public default AbstractModule chooseModule(List<AbstractModule> potentialModules){
		return chooseModule(potentialModules, true);
	}
	
	/**
	 * Randomly chooses a branch root among the given module's branches roots.
	 * Each branch root has a probability of being chosen that is either 
	 * proportional to the size of the corresponding branch (the size of branch 
	 * i is given by the method AbstractModule.getBranchSize(i)), or uniform,
	 * depending on the value of the sizeProportional parameter.
	 * 
	 * @param module
	 * 			The module for which a branch root must be chosen.
	 * @param sizeProportional
	 * 			A parameter to indicate if all branches roots have the same 
	 * 			probability of being chosen or if the probabilities must be 
	 * 			size-weighted.
	 * @return
	 * 			The chosen branch root.
	 */
	public default AbstractNode chooseBranchRoot(AbstractModule module, boolean sizeProportional){
		if(module == null)
			throw new NullPointerException("A module must be specified!");
		
		AbstractNode chosenRoot = null;
		if(!sizeProportional) {
			ArrayList<AbstractNode> branchesRoots = module.getBranchRoots();
			chosenRoot = branchesRoots.get(ThreadLocalRandom.current().nextInt(branchesRoots.size()));
		} else {
			int moduleWeight = module.getSize();
			
			int count = 0;
			int random = ThreadLocalRandom.current().nextInt(moduleWeight);
			
			for(int i = 0; i < module.getBranchRoots().size(); i++){
				count += module.getBranchSize(i);
				if(random < count){
					chosenRoot = module.getBranchRoots().get(i);
					break;
				}
			}
		}
		return chosenRoot;
	}
	public default AbstractNode chooseBranchRoot(AbstractModule module){
		return chooseBranchRoot(module, true);
	}
	
	
	
	
	/**
	 * Chooses randomly an external node whose return type matches the one given as parameter.
	 * 
	 * @param returnType
	 * 			The desired return return type.
	 * @return
	 * 			A copy of the chosen node, generated with the generateNew() method.
	 */
	public default AbstractNode findExternalNode(AbstractType returnType){
		AbstractNode node = chooseNodeIn(getExternalNodes(), returnType);
		if(node != null)
			return node.generateNew();
		else
			throw new RuntimeException("No external node whose return type matches " + returnType.getClass().getSimpleName() + " found !");
	}
	/**
	 * Chooses randomly an internal node whose return type matches the one given as parameter.
	 * 
	 * @param returnType
	 * 			The desired return type.
	 * @return
	 * 			A copy of the chosen node, generated with the copy() method.
	 */
	public default AbstractNode findInternalNode(AbstractType returnType){
		AbstractNode node = chooseNodeIn(getInternalNodes(), returnType);
		if(node != null)
			return node.copy();
		else
			throw new RuntimeException("No internal node whose return type matches " + returnType.getClass().getSimpleName() + " found !");
	}
	/**
	 * TODO : Description.
	 * <p>
	 * Note : The returned node is the original, not a copy generated via copy() or generateNew().
	 * 
	 * @param potentialNodes
	 * @param returnType
	 * @return
	 */
	public default AbstractNode chooseNodeIn(List<AbstractNode> potentialNodes, AbstractType returnType){
		if(potentialNodes.isEmpty())
			return null;
		
		int randomizer = ThreadLocalRandom.current().nextInt(potentialNodes.size());
		
		for(int i = 0; i < potentialNodes.size(); i++){
			AbstractNode node = potentialNodes.get((i + randomizer) % potentialNodes.size());
			if(node.getReturnType().isTheSameAs(returnType))
				return node;
		}
		
		return null;
	}
	
	
	/**
	 * Chooses randomly a node that is compatible with the source node, which means whose 
	 * return type and whose arguments' types are the same as the source node's ones.
	 * 
	 * @param sourceNode
	 * 			The function for which a matching function must be found.
	 * @return
	 * 			The chosen function, or null if none was found.
	 */
	public default AbstractNode findMatchingFunction(AbstractNode sourceNode){
		if(sourceNode.getArgsTypes().isEmpty())
			return findExternalNode(sourceNode.getReturnType());
		else {
			List<AbstractNode> internalNodes = getInternalNodes();
			int randomizer = ThreadLocalRandom.current().nextInt(internalNodes.size());
			for(int i = 0; i < internalNodes.size(); i++){
				AbstractNode node = internalNodes.get((randomizer + i) % internalNodes.size());
				if(node.getReturnType().isTheSameAs(sourceNode.getReturnType())){
					if(node.argsTypesMatchRequestedTypes(sourceNode.getArgsTypes()))
						return node.copy();
				}
			}
			throw new RuntimeException("No node matching " + sourceNode + " found !");
		}
	}
}
