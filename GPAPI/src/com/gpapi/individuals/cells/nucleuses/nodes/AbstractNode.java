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
 * AbstractNode.java
 * ---------------
 * (C) Copyright 2015-2016, by Jeremy Sayag and Contributors.
 *
 * Original Author:  Jeremy Sayag;
 * Contributor(s):   ;
 * 
 */


package com.gpapi.individuals.cells.nucleuses.nodes;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import com.gpapi.individuals.cells.nucleuses.GenotypeView;
import com.gpapi.individuals.cells.nucleuses.geneticoperators.GeneticOperatorInterface;
import com.gpapi.individuals.cells.nucleuses.types.AbstractType;
import com.gpapi.individuals.cells.nucleuses.types.Result;

import edu.uci.ics.jung.graph.Forest;



public abstract class AbstractNode implements Serializable {
	
	private static final long serialVersionUID = 3830624019743146106L;
	
	
	
	public abstract String getName();
	
	
	
	private AbstractNode parent = null;
	/**
	 * Returns the parent node of this node.
	 * 
	 * @return
	 * 			The parent node of this node.
	 * 			Null if not set.
	 */
	public final AbstractNode getParent(){
		return parent;
	}
	/**
	 * Sets the parent node of this node (null permitted, which means
	 * the node is a root node).
	 * 
	 * @param parent
	 * 			The parent node of this node.
	 */
	public final void setParent(AbstractNode parent){
		this.parent = parent;
	}
	
	
	
	private ArrayList<AbstractNode> args = new ArrayList<AbstractNode>();
	/**
	 * TODO : Description.
	 * 
	 * Note : Be careful, the returned list is the internal one, not just a view or a copy!
	 * 
	 * @return
	 * 			The function arguments.
	 */
	public final ArrayList<AbstractNode> getArgs(){ // TODO Note : Now returns an ArrayList instead of List, check if calling implementations can't use it.
		return args;
	}
	/**
	 * TODO : Description.
	 * 
	 * @param args
	 * 			The function arguments.
	 */
	public final void setArgs(List<AbstractNode> args){
		if(args == null)
			throw new NullPointerException("Args can't be null!");
		
		if(!argsMatchRequestedTypes(args))
			throw new RuntimeException("Trying to set args of the wrong type!");
		
		for(AbstractNode node : args)
			node.setParent(this);
		
		this.args = new ArrayList<AbstractNode>(args);
	}
	/**
	 * TODO : Description.
	 * 
	 * @param oldArg
	 * @param newArg
	 */
	public final void replaceArg(AbstractNode oldArg, AbstractNode newArg){
		for(int i = 0; i < args.size(); i++){
			if(args.get(i) == oldArg){
				if(newArg.getReturnType().isTheSameAs(getArgsTypes().get(i))){
					newArg.setParent(this);
					args.set(i, newArg);
					return;
				} else
					throw new RuntimeException("Trying to swap incompatible nodes !");
			}
		}
		throw new RuntimeException("OldArg is not a child of this node!");
	}
	
	
	
	/**
	 * Returns an empty instance of a children class of GPTerminal,
	 * the class indicating the return type of the function.<p>
	 * 
	 * @return
	 * 			An instance of a subclass of GPTerminal.
	 */
	public abstract AbstractType getReturnType();
	/**
	 * Returns an array containing terminals whose types are the same as the 
	 * function arguments must be.
	 * 
	 * @return
	 * 			An array containing terminals whose types are the same as the function
	 * 			arguments must be.
	 */
	public abstract List<AbstractType> getArgsTypes();
	
	
	
	/**
	 * Indicates whether the given list of nodes can be set as the arguments list of
	 * this function.
	 * 
	 * @param args
	 * 			The potential arguments list.
	 * @return
	 * 			True if the given nodes list is compatible.<p>
	 * 			False otherwise.
	 */
	public final boolean argsMatchRequestedTypes(List<AbstractNode> args){
		List<AbstractType> argsTypes = getArgsTypes();
		
		if(args.size() != argsTypes.size())
			return false;
		
		for(int i = 0; i < args.size(); i++){
			if(!args.get(i).getReturnType().isTheSameAs(argsTypes.get(i)))
				return false;
		}
		
		return true;
	}
	/**
	 * TODO : Description.
	 * 
	 * @param argsTypes
	 * 			A list of terminals, representing arguments' types of a function.
	 * @return
	 * 			True if the list of types is compatible (the order matters).<p>
	 * 			False otherwise.
	 */
	public final boolean argsTypesMatchRequestedTypes(List<AbstractType> argsTypes){
		List<AbstractType> correctArgsTypes = getArgsTypes();
		
		if(argsTypes.size() != correctArgsTypes.size())
			return false;
		
		for(int i = 0; i < argsTypes.size(); i++){
			if(!argsTypes.get(i).isTheSameAs(correctArgsTypes.get(i)))
				return false;
		}
		
		return true;
	}
	
	
	
	/**
	 * Returns a mutated copy of this node.
	 * 
	 * If this node is a function, the returned node must be either the root of a 
	 * fully built subtree (i.e. a terminal, or a function whose args are set), or 
	 * a function whose args are not set but must be of the same type as the args 
	 * of this function.
	 * 
	 * If this node is a terminal, the returned node must be the root of a fully 
	 * built subtree.
	 * 
	 * @return
	 * 			TODO : Description.
	 */
	public AbstractNode mutatedCopy(GeneticOperatorInterface geneticOperator){
		return geneticOperator.findMatchingFunction(this);
	}
	
	
	
	/**
	 * Method used to get a copy of this node (the subtree is not copied).
	 * 
	 * @return
	 * 			The copy of this node.
	 */
	public abstract AbstractNode copy();
	/**
	 * Generates a new terminal, of the same type as this one,
	 * and sets randomly its value.<p>
	 * 
	 * @return
	 * 			The new terminal.
	 */
	public AbstractNode generateNew(){
		return copy();
	}
	/**
	 * Main method of nodes, used to compute the value represented by the subtree
	 * this node is the root of, as well as the execution cost.<p>
	 * 
	 * Implementations of this method in subclasses do not need to increment the
	 * execution cost is the returned Result object, since this is done in the
	 * wrapping method execute().<p>
	 * 
	 * @return
	 * 			The value of this node, i.e. the output value of the formula
	 * 			represented by the subtree starting from this node.
	 * @throws
	 * 			Exception in case of any issue during the computation.
	 */
	public abstract Result execute() throws Exception;
	
	
	
	/**
	 * Returns a boolean indicating whether the function is ready to be
	 * executed, i.e. if its arguments are correctly set.<p>
	 * 
	 * @return
	 * 			True if the function can be executed, false otherwise.
	 */
	public final boolean isReady(){
		if(argsMatchRequestedTypes(getArgs()))
			return true;
		else
			return false;
	}
	/**
	 * Method used to recursively build the view of a tree.<p>
	 * 
	 * @param graph
	 * 			The graph to which the current node must be added.
	 * @param parentId
	 * 			The id of the parent node of this node.
	 * @return
	 * 			The id of this node.
	 */
	public final void createView(Forest<GenotypeView.CustomVertex,GenotypeView.CustomEdge> graph, int parentId, String parentName, String edgeName){
		int id = graph.getVertexCount();
		
		graph.addEdge(
				new GenotypeView.CustomEdge(graph.getEdgeCount(), edgeName), 
				new GenotypeView.CustomVertex(parentId, parentName), 
				new GenotypeView.CustomVertex(id, getName()));
		
		for(AbstractNode arg : getArgs())
			arg.createView(graph, id, getName(), "");
	}
}
