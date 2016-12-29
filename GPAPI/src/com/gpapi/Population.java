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
 * Population.java
 * ---------------
 * (C) Copyright 2015-2016, by Jeremy Sayag and Contributors.
 *
 * Original Author:  Jeremy Sayag;
 * Contributor(s):   ;
 * 
 */


package com.gpapi;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Iterator;

import com.gpapi.individuals.EvolvedIndividual;



/**
 * 
 * TODO : Description.
 * 
 * Though this class doesn't implement the List interface (because it would necessarily break
 * the contract of the equals() and hashCode() methods as well as a few other incompatibilities),
 * the underlying collection is an ArrayList, and methods with the same signatures as their
 * equivalent in the List interface directly rely on the internal ArrayList's implementations.
 * So for instance the get(int) method runs in constant time.
 * 
 * @author sayag
 *
 */

public final class Population implements Collection<EvolvedIndividual>, Serializable {
	
	private static final long serialVersionUID = -6578949620252336483L;
	
	private int targetSize = 0;
	public final int getTargetSize() {
		return targetSize;
	}
	public final void setTargetSize(int targetSize) {
		this.targetSize = targetSize;
	}
	
	private final ArrayList<EvolvedIndividual> individuals = new ArrayList<EvolvedIndividual>();
	
	
	
	public Population(EvolvedIndividual initIndividual, int targetSize){
		if(initIndividual == null && targetSize > 0)
			throw new NullPointerException("An initial individual must be specified!");
		
		for(int i = 0; i < targetSize; i++)
			individuals.add(initIndividual.generateNew());
		
		this.targetSize = targetSize;
	}
	public Population(){}
	
	
	@Override
	public final boolean add(EvolvedIndividual newIndividual){
		return individuals.add(newIndividual);
	}
	@Override
	public final boolean addAll(Collection<? extends EvolvedIndividual> newIndividuals){
		return individuals.addAll(newIndividuals);
	}
	
	
	@Override
	public final void clear(){
		individuals.clear();
	}
	public final void clearBetween(int beginingIndex, int endIndex){
		individuals.subList(beginingIndex, endIndex).clear();
	}
	
	
	@Override
	public final boolean contains(Object o) {
		return individuals.contains(o);
	}
	@Override
	public final boolean containsAll(Collection<?> c) {
		return individuals.containsAll(c);
	}
	
	
	public final EvolvedIndividual get(int index){
		return individuals.get(index);
	}
	
	
	public final boolean isFitnessReady() {
		if(isEmpty())
			return true;
		
		for(EvolvedIndividual individual : individuals){
			if(!individual.isFitnessReady())
				return false;
		}
		
		return true;
	}
	
	
	@Override
	public final boolean isEmpty() {
		return individuals.isEmpty();
	}
	
	
	@Override
	public final Iterator<EvolvedIndividual> iterator() {
		return individuals.iterator();
	}
	
	
	public final EvolvedIndividual remove(int index){
		return individuals.remove(index);
	}
	@Override
	public final boolean remove(Object o){
		return individuals.remove(o);
	}
	@Override
	public final boolean removeAll(Collection<?> c){
		return individuals.removeAll(c);
	}
	
	
	@Override
	public final boolean retainAll(Collection<?> c) {
		return individuals.retainAll(c);
	}
	
	
	@Override
	public final int size(){
		return individuals.size();
	}
	
	
	public final void sort(){
		if(!isFitnessReady())
			throw new RuntimeException(
					"The fitness of all individuals in the population must be ready to sort it!");
		Collections.sort(individuals);
	}
	
	
	@Override
	public final Object[] toArray() {
		return individuals.toArray();
	}
	@Override
	public final <T> T[] toArray(T[] a) {
		return individuals.toArray(a);
	}
}
