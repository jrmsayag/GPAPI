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
 * AbstractModule.java
 * ---------------
 * (C) Copyright 2015-2016, by Jeremy Sayag and Contributors.
 *
 * Original Author:  Jeremy Sayag;
 * Contributor(s):   ;
 * 
 */


package com.gpapi.individuals.cells.nucleuses.modules;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import com.gpapi.individuals.cells.nucleuses.nodes.AbstractNode;
import com.gpapi.individuals.cells.nucleuses.types.AbstractType;



public abstract class AbstractModule extends AbstractProxyNodeSlot {
	
	private static final long serialVersionUID = -4171852292394316267L;
	
	private final ArrayList<AbstractNode> branchRoots;
	/**
	 * TODO : Description.
	 * <p>
	 * Note : Be careful, the returned list is the internal one, not just a view or a copy!
	 * 
	 * @return
	 */
	public final ArrayList<AbstractNode> getBranchRoots() {
		return branchRoots;
	}

	private final ArrayList<Argument> args = new ArrayList<Argument>();
	/**
	 * TODO : Description.
	 * <p>
	 * Note : Be careful, the returned list is the internal one, not just a view or a copy!
	 * 
	 * @return
	 */
	public final ArrayList<Argument> getArgs() {
		return args;
	}
	
	private final int[] branchSizes;
	public final int getBranchSize(int branchIndex) {
		return branchSizes[branchIndex];
	}

	private final int[] branchDepths;
	public final int getBranchDepth(int branchIndex) {
		return branchDepths[branchIndex];
	}
	
	
	
	/**
	 * TODO : Description.
	 * 
	 * @param id
	 * @param branchRoots
	 * @param argsTypes
	 * @param argsNames
	 */
	public AbstractModule(int id, List<AbstractNode> branchRoots, List<AbstractType> argsTypes, List<String> argsNames){
		super(id);
		
		if(branchRoots == null)
			throw new NullPointerException("branchRoots can't be null!");
		else if(argsTypes == null)
			throw new NullPointerException("argsTypes can't be null!");
		
		for(int i = 0; i < argsTypes.size(); i++){
			if(argsNames != null)
				args.add(new Argument(i, argsNames.get(i), argsTypes.get(i)));
			else
				args.add(new Argument(i, argsTypes.get(i)));
		}
		
		for(AbstractNode root : branchRoots)
			root.setParent(null);
		
		this.branchRoots = new ArrayList<AbstractNode>(branchRoots);
		
		branchSizes = new int[branchRoots.size()];
		branchDepths = new int[branchRoots.size()];
		
		consolidate();
	}
	public AbstractModule(int id, List<AbstractNode> branchRoots, List<AbstractType> argsTypes){
		this(id, branchRoots, argsTypes, null);
	}
	
	
	/**
	 * Rebuilds size and depth and sets up arguments' proxies.
	 * To be called each time the branches' bodies are modified.
	 */
	public final void consolidate(){
		for(int i = 0; i < getBranchRoots().size(); i++){
			branchSizes[i] = 0;
			branchDepths[i] = 0;
			
			List<AbstractNode> nextLevel = Arrays.asList(getBranchRoots().get(i));
			while(!nextLevel.isEmpty()){
				List<AbstractNode> currentLevel = nextLevel;
				
				branchSizes[i] += currentLevel.size();
				branchDepths[i]++;
				
				nextLevel = new ArrayList<AbstractNode>();
				for(AbstractNode node : currentLevel){
					if(!nextLevel.addAll(node.getArgs()) && node instanceof ArgumentProxy){
						ArgumentProxy argProxy = (ArgumentProxy) node;
						argProxy.setArgument(getArgs().get(argProxy.getId()));
					}
				}
			}
		}
	}
	
	
	/**
	 * TODO : Description.
	 * <p>
	 * Note : This method does not call consolidate() itself, so this should be done externally
	 * whenever the root replacement could lead to size or depth changes, as well as to make
	 * sure the argument proxies are connected to the right arguments (for instance if the new 
	 * branch body comes from another module).
	 * 
	 * @param oldBranchRoot
	 * @param newBranchRoot
	 */
	public final void replaceBranchRoot(AbstractNode oldBranchRoot, AbstractNode newBranchRoot){
		for(int i = 0; i < getBranchRoots().size(); i++){
			if(getBranchRoots().get(i) == oldBranchRoot){
				if(newBranchRoot.getReturnType().isTheSameAs(oldBranchRoot.getReturnType())){
					newBranchRoot.setParent(null);
					getBranchRoots().set(i, newBranchRoot);
					return;
				} else
					throw new RuntimeException("Trying to swap incompatible branch roots!");
			}
		}
		throw new RuntimeException("oldBranchRoot is not the root of a branch of this module!");
	}
	
	
	public final int getSize(){
		int size = 0;
		for(int branchSize : branchSizes)
			size += branchSize;
		return size;
	}
	public final int getDepth(){
		int depth = 0;
		for(int branchDepth : branchDepths)
			depth = Math.max(depth, branchDepth);
		return depth;
	}
	
	
	public final ArrayList<AbstractType> getArgsTypes() {
		ArrayList<AbstractType> argsTypes = new ArrayList<AbstractType>(getArgs().size());
		for(Argument arg : getArgs())
			argsTypes.add(arg.getReturnType());
		return argsTypes;
	}
	public final ArrayList<String> getArgsNames() {
		ArrayList<String> argsNames = new ArrayList<String>(getArgs().size());
		for(Argument arg : getArgs())
			argsNames.add(arg.getName());
		return argsNames;
	}
	
	
	public static final ArrayList<ArgumentProxy> createArgumentsProxiesFor(List<AbstractType> argsTypes){
		ArrayList<ArgumentProxy> argumentProxies = new ArrayList<ArgumentProxy>(argsTypes.size());
		for(int i = 0; i < argsTypes.size(); i++)
			argumentProxies.add(new ArgumentProxy(new Argument(i, argsTypes.get(i))));
		return argumentProxies;
	}
}
