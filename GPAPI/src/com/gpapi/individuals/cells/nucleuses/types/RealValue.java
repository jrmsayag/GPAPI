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
 * RealValue.java
 * ---------------
 * (C) Copyright 2015-2016, by Jeremy Sayag and Contributors.
 *
 * Original Author:  Jeremy Sayag;
 * Contributor(s):   ;
 * 
 */


package com.gpapi.individuals.cells.nucleuses.types;

import java.util.concurrent.ThreadLocalRandom;



public final class RealValue extends AbstractType {
	
	private static final long serialVersionUID = -2980916826158146690L;
	
	private double value;
	public final double getValue(){
		return value;
	}
	public final void setValue(double value){
		this.value = value;
	}
	
	private double increment;
	public final double getIncrement() {
		return increment;
	}
	public void setIncrement(double increment) {
		this.increment = increment;
	}
	
	private double newValue;
	public double getNewValue() {
		return newValue;
	}
	public void setNewValue(double newValue) {
		this.newValue = newValue;
	}
	
	private double newIncrement;
	public double getNewIncrement() {
		return newIncrement;
	}
	public void setNewIncrement(double newIncrement) {
		this.newIncrement = newIncrement;
	}
	
	
	
	private RealValue(double value, double increment, double newValue, double newIncrement){
		setValue(value);
		setIncrement(increment);
		setNewValue(newValue);
		setNewIncrement(newIncrement);
	}
	
	
	@Override
	public final void mutate(){
		if(getIncrement() > 0.0)
			setValue(getValue() + ThreadLocalRandom.current().nextDouble(-getIncrement(), getIncrement()));
	}
	
	@Override
	public final RealValue copy() {
		return createWithGenerationParameters(getValue(), getIncrement(), getNewValue(), getNewIncrement());
	}
	@Override
	public final RealValue generateNew(){
		return createWithGenerationParameters(getNewValue(), getNewIncrement(), getIncrement());
	}
	
	@Override
	public final String toString(){
		String name = String.valueOf(getValue());
		int index = name.indexOf('.');
		if(index < 0)
			return name;
		else
			return name.substring(0, Math.min(name.length(), index+3));
	}
	
	
	public static final RealValue create(){
		return create(0.0);
	}
	public static final RealValue create(double value){
		return createWithIncrement(value, 1.0);
	}
	public static final RealValue createWithIncrement(double increment){
		return createWithIncrement(0.0, increment);
	}
	public static final RealValue createWithIncrement(double value, double increment){
		return createWithGenerationParameters(value, increment, value, increment);
	}
	public static final RealValue createWithGenerationParameters(
			double newValue,
			double newIncrement,
			double increment
	){
		double value = newValue;
		if(newIncrement > 0.0)
			value += ThreadLocalRandom.current().nextDouble(-newIncrement, newIncrement);
		
		return createWithGenerationParameters(value, increment, newValue, newIncrement);
	}
	public static final RealValue createWithGenerationParameters(
			double value,
			double increment,
			double newValue,
			double newIncrement
	){
		return new RealValue(value, increment, newValue, newIncrement);
	}
}
