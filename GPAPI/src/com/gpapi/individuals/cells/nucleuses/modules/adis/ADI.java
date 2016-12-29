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
 * ADI.java
 * ---------------
 * (C) Copyright 2015-2016, by Jeremy Sayag and Contributors.
 *
 * Original Author:  Jeremy Sayag;
 * Contributor(s):   ;
 * 
 */


package com.gpapi.individuals.cells.nucleuses.modules.adis;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import com.gpapi.individuals.cells.nucleuses.modules.AbstractModule;
import com.gpapi.individuals.cells.nucleuses.nodes.AbstractNode;
import com.gpapi.individuals.cells.nucleuses.types.AbstractType;
import com.gpapi.individuals.cells.nucleuses.types.RealValue;
import com.gpapi.individuals.cells.nucleuses.types.Result;



public final class ADI extends AbstractModule {

	private static final long serialVersionUID = 2194339380541872844L;
	
	public final AbstractNode getRoot() {
		return getBranchRoots().get(0);
	}
	
	private final List<AbstractType> externalArgsTypes;
	/**
	 * TODO : Description.
	 * <p>
	 * Note : Be careful, the returned list is the internal one, not just a view or a copy!
	 * 
	 * @return
	 */
	public final List<AbstractType> getExternalArgsTypes() {
		return externalArgsTypes;
	}
	
	private final List<AbstractType> fieldsTypes;
	public final List<AbstractType> getFieldsTypes() {
		return fieldsTypes;
	}
	
	private final List<String> fieldsNames;
	public final List<String> getFieldsNames() {
		return fieldsNames;
	}
	
	private final AbstractType defaultValue;
	public final AbstractType getDefaultValue() {
		return defaultValue;
	}

	private final int collectionId;
	public final int getCollectionId() {
		return collectionId;
	}

	private final int maxCalls;
	public final int getMaxCalls() {
		return maxCalls;
	}
	
	
	private List<List<AbstractType>> collection = Collections.emptyList();
	public final List<List<AbstractType>> getCollection() {
		return collection;
	}
	public final void setCollection(List<List<AbstractType>> collection) {
		if(collection == null)
			throw new NullPointerException("Argument collection can't be null !");
		else
			this.collection = collection;
	}
	
	private int currentCalls = 0;
	private AbstractType lastValue = null;
	
	
	
	public ADI(
			int id, 
			AbstractType defaultValue, 
			AbstractNode root, 
			List<AbstractType> externalArgsTypes, 
			int collectionId, 
			List<AbstractType> fieldsTypes, 
			List<String> fieldsNames, 
			int maxCalls) {
		super(id, Arrays.asList(root), argsTypesFor(externalArgsTypes, fieldsTypes));
		
		if(defaultValue == null)
			throw new NullPointerException("A default value must be specified !");
		else if(!root.getReturnType().isTheSameAs(defaultValue))
			throw new IllegalArgumentException("Types of root and default value don't correspond !");
		
		getArgs().get(getArgs().size() - 2).setManualName("index");
		getArgs().get(getArgs().size() - 1).setManualName("len");
		for(int i = 0; i < fieldsTypes.size(); i++)
			getArgs().get(i + externalArgsTypes.size()).setManualName(fieldsNames.get(i));
		
		this.defaultValue = defaultValue;
		
		this.externalArgsTypes = new ArrayList<AbstractType>(externalArgsTypes);
		
		this.fieldsTypes = Collections.unmodifiableList(new ArrayList<AbstractType>(fieldsTypes));
		this.fieldsNames = Collections.unmodifiableList(new ArrayList<String>(fieldsNames));
		
		this.collectionId = collectionId;
		this.maxCalls = maxCalls;
		
		reset();
	}
	public ADI(
			int id, 
			AbstractType defaultValue, 
			AbstractNode root, 
			int collectionId, 
			List<AbstractType> fieldsTypes, 
			List<String> fieldsNames, 
			int maxCalls) {
		this(
				id, 
				defaultValue, 
				root, 
				Collections.emptyList(), 
				collectionId, 
				fieldsTypes, 
				fieldsNames, 
				maxCalls);
	}
	
	public final void reset(){
		currentCalls = 0;
		lastValue = getDefaultValue().copy();
	}
	
	
	@Override
	public final String getName() {
		return "adi" + getId();
	}
	@Override
	public final AbstractType getReturnType() {
		return getRoot().getReturnType();
	}
	
	
	public final Result execute(List<Result> argsResults) throws Exception {
		Result result = null;
		
		if(currentCalls < getMaxCalls() && !getCollection().isEmpty()){
			if(argsResults == null)
				throw new NullPointerException("Argument argsResult can't be null !");
			else if(argsResults.size() != getExternalArgsTypes().size())
				throw new IllegalArgumentException("The given number of argument is different from the ADI's one !");
			
			int i = 0;
			for(Result argResult : argsResults){
				getArgs().get(i).setValue(argResult.getValue());
				i++;
			}
			
			getArgs().get(getArgs().size()-1).setValue(RealValue.create(getCollection().size()));
			
			int index = 0;
			for(List<AbstractType> values : getCollection()){
				if(values == null)
					throw new NullPointerException("The current fields list is null !");
				else if(values.size() != getFieldsTypes().size())
					throw new IllegalArgumentException("The current fields list is not the same size as the ADI's one !");
				
				i = 0;
				for(AbstractType value : values){
					getArgs().get(i + getExternalArgsTypes().size()).setValue(value);
					i++;
				}
				
				getArgs().get(getArgs().size()-2).setValue(RealValue.create(index));
				
				if(result != null)
					result = getRoot().execute().incrementCost(result.getCost());
				else
					result = getRoot().execute();
				
				index++;
			}
			currentCalls++;
		} else
			result = new Result(lastValue);
		
		lastValue = result.getValue().copy();
		
		return result;
	}
	
	
	public static final List<AbstractType> argsTypesFor(List<AbstractType> externalArgsTypes, List<AbstractType> fieldsTypes){
		ArrayList<AbstractType> allTypes = new ArrayList<AbstractType>(externalArgsTypes.size() + fieldsTypes.size() + 2);
		allTypes.addAll(externalArgsTypes);
		allTypes.addAll(fieldsTypes);
		allTypes.add(RealValue.create());
		allTypes.add(RealValue.create());
		return allTypes;
	}
}
