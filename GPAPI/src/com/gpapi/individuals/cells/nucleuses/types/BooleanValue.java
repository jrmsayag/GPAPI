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
 * BooleanValue.java
 * ---------------
 * (C) Copyright 2015-2016, by Jeremy Sayag and Contributors.
 *
 * Original Author:  Jeremy Sayag;
 * Contributor(s):   ;
 * 
 */


package com.gpapi.individuals.cells.nucleuses.types;

import java.util.concurrent.ThreadLocalRandom;



public final class BooleanValue extends AbstractType {
	
	private static final long serialVersionUID = 6180348494843406149L;
	
	private boolean value;
	public final boolean getValue(){
		return value;
	}
	public final void setValue(boolean value){
		this.value = value;
	}
	
	private final boolean mutable;
	public final boolean isMutable() {
		return mutable;
	}
	
	
	
	private BooleanValue(boolean value, boolean mutable){
		setValue(value);
		this.mutable = mutable;
	}
	
	
	@Override
	public final void mutate(){
		if(isMutable())
			setValue(ThreadLocalRandom.current().nextBoolean());
	}
	
	@Override
	public final BooleanValue copy() {
		return createMutable(getValue(), isMutable());
	}
	
	@Override
	public final String toString(){
		return String.valueOf(getValue());
	}
	
	
	public static final BooleanValue create(){
		return create(true);
	}
	public static final BooleanValue create(boolean value){
		return createMutable(value, true);
	}
	public static final BooleanValue createMutable(boolean mutable){
		return createMutable(true, mutable);
	}
	public static final BooleanValue createMutable(boolean value, boolean mutable){
		return new BooleanValue(value, mutable);
	}
}
