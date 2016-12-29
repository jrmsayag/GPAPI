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
 * ADS.java
 * ---------------
 * (C) Copyright 2015-2016, by Jeremy Sayag and Contributors.
 *
 * Original Author:  Jeremy Sayag;
 * Contributor(s):   ;
 * 
 */


package com.gpapi.individuals.cells.nucleuses.modules.adss;

import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

import com.gpapi.individuals.cells.nucleuses.modules.AbstractModule;
import com.gpapi.individuals.cells.nucleuses.types.AbstractType;
import com.gpapi.individuals.cells.nucleuses.types.RealValue;
import com.gpapi.individuals.cells.nucleuses.types.Result;



public final class ADS extends AbstractModule {

	private static final long serialVersionUID = 3325580127730344161L;
	
	private static final RealValue adressArgsType = RealValue.create();
	public static final RealValue getAdressArgsType() {
		return adressArgsType;
	}

	public enum Type {
		Variable,
		Stack,
		Queue,
		ArrayList
	}
	
	private Type storageType;
	public final Type getStorageType() {
		return storageType;
	}
	public final void setStorageType(Type storageType) {
		if(storageType == null)
			throw new NullPointerException("A storage type must be specified!");
		else
			this.storageType = storageType;
	}
	
	private int maxSize;
	public final int getMaxSize() {
		return maxSize;
	}
	public final void setMaxSize(int maxSize) {
		this.maxSize = maxSize;
		while(stackOrQueue.size() > maxSize && !stackOrQueue.isEmpty())
			stackOrQueue.removeFirst();
		while(arrayList.size() > maxSize && !arrayList.isEmpty())
			arrayList.remove(arrayList.size()-1);
	}
	
	private final AbstractType returnType;
	@Override
	public final AbstractType getReturnType() {
		return returnType;
	}
	
	private AbstractType variable = null;
	private final LinkedList<AbstractType> stackOrQueue = new LinkedList<AbstractType>();
	private final ArrayList<AbstractType> arrayList = new ArrayList<AbstractType>();
	
	
	
	/**
	 * TODO : Description.
	 * <p>
	 * Note : The given return type will also be the default values when reading
	 * a storage that haven't been written to yet.
	 * 
	 * @param id
	 * @param storageType
	 * @param returnType
	 * @param maxSize
	 */
	public ADS(int id, Type storageType, AbstractType returnType, int maxSize) {
		super(id, Collections.emptyList(), Collections.emptyList());
		
		if(returnType == null)
			throw new NullPointerException("A return type must be specified!");
		else
			this.returnType = returnType;
		
		setMaxSize(maxSize);
		setStorageType(storageType);
	}
	public ADS(int id, Type storageType, AbstractType dataType) {
		this(id, storageType, dataType, 1);
	}
	
	
	public final void clear(){
		variable = null;
		stackOrQueue.clear();
		arrayList.clear();
	}
	
	
	@Override
	public final String getName() {
		return "ads" + getId();
	}
	
	public final int getDimension() {
		if(getStorageType().equals(Type.Variable))
			return 0;
		else if(getStorageType().equals(Type.Stack))
			return 0;
		else if(getStorageType().equals(Type.Queue))
			return 0;
		else if(getStorageType().equals(Type.ArrayList))
			return 1;
		else
			throw new RuntimeException("Unknown storage type :  " + getStorageType() + " !");
	}
	
	
	public final Result read(List<Result> argsResults){
		if(argsResults == null)
			throw new NullPointerException("Argument argsResult can't be null !");
		
		if(getStorageType().equals(Type.Variable) && variable != null){
			if(!argsResults.isEmpty())
				throw new IllegalArgumentException("Given the ADS type, argument argsResult must be empty !");
			else
				return new Result(variable.copy());
		} else if(getStorageType().equals(Type.Stack) && !stackOrQueue.isEmpty()) {
			if(!argsResults.isEmpty())
				throw new IllegalArgumentException("Given the ADS type, argument argsResult must be empty !");
			else
				return new Result(stackOrQueue.removeFirst());
		} else if(getStorageType().equals(Type.Queue) && !stackOrQueue.isEmpty()) {
			if(!argsResults.isEmpty())
				throw new IllegalArgumentException("Given the ADS type, argument argsResult must be empty !");
			else
				return new Result(stackOrQueue.removeLast());
		} else if(!arrayList.isEmpty()){
			if(argsResults.size() != 1)
				throw new IllegalArgumentException("The given number of argument doesn't correspond to the ADS type !");
			else if(!getAdressArgsType().getClass().isInstance(argsResults.get(0).getValue()))
				throw new IllegalArgumentException("Adress argument is of the wrong type !");
			
			RealValue rawIndex = getAdressArgsType().getClass().cast(argsResults.get(0).getValue());
			int index = correctRawIndex(rawIndex.getValue(), false);
			
			return new Result(arrayList.get(index).copy());
		} else
			return new Result(returnType.copy());
	}
	public final Result write(List<Result> argsResults){
		if(argsResults == null)
			throw new NullPointerException("Argument argsResult can't be null !");
		
		if(getStorageType().equals(Type.Variable)){
			if(argsResults.size() != 1)
				throw new IllegalArgumentException("The given number of argument doesn't correspond to the ADS type !");
			else if(!getReturnType().getClass().isInstance(argsResults.get(0).getValue()))
				throw new IllegalArgumentException("The value to write doesn't match the return type !");
			else
				variable = argsResults.get(0).getValue().copy();
		} else if(getStorageType().equals(Type.Stack) || getStorageType().equals(Type.Queue)){
			if(argsResults.size() != 1)
				throw new IllegalArgumentException("The given number of argument doesn't correspond to the ADS type !");
			else if(!getReturnType().getClass().isInstance(argsResults.get(0).getValue()))
				throw new IllegalArgumentException("The value to write doesn't match the return type !");
			else if(stackOrQueue.size() < getMaxSize())
				stackOrQueue.addFirst(argsResults.get(0).getValue().copy());
		} else {
			if(argsResults.size() != 2)
				throw new IllegalArgumentException("The given number of argument doesn't correspond to the ADS type !");
			else if(!getReturnType().getClass().isInstance(argsResults.get(0).getValue()))
				throw new IllegalArgumentException("The value to write doesn't match the return type !");
			else if(!getAdressArgsType().getClass().isInstance(argsResults.get(1).getValue()))
				throw new IllegalArgumentException("Adress argument is of the wrong type !");
			
			RealValue rawIndex = getAdressArgsType().getClass().cast(argsResults.get(1).getValue());
			int index = correctRawIndex(rawIndex.getValue(), true);
			
			if(index < arrayList.size())
				arrayList.set(index, argsResults.get(0).getValue().copy());
			else if(arrayList.size() < getMaxSize())
				arrayList.add(argsResults.get(0).getValue().copy());
		}
		return new Result(argsResults.get(0).getValue());
	}
	private final int correctRawIndex(double rawIndex, boolean write){
		rawIndex = Math.abs(rawIndex);
		
		int min = 0;
		int max = arrayList.size() - (write ? 0 : 1);
		
		return Math.max(min, Math.min(max, (int) rawIndex));
	}
}
