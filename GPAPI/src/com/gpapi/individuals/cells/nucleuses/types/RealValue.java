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
	
	private final double increment;
	public final double getIncrement() {
		return increment;
	}
	
	
	
	private RealValue(double value, double increment){
		setValue(value);
		this.increment = increment;
	}
	
	
	@Override
	public final void mutate(){
		if(getIncrement() > 0.0)
			setValue(getValue() + ThreadLocalRandom.current().nextDouble(-getIncrement(), getIncrement()));
	}
	
	@Override
	public final RealValue copy() {
		return createWithIncrement(getValue(), getIncrement());
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
		return new RealValue(value, increment);
	}
}
