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
 * Color.java
 * ---------------
 * (C) Copyright 2015-2016, by Jeremy Sayag and Contributors.
 *
 * Original Author:  Jeremy Sayag;
 * Contributor(s):   ;
 * 
 */


package com.gpapi.examples.painting.nodes.types;

import java.util.concurrent.ThreadLocalRandom;

import com.gpapi.individuals.cells.nucleuses.types.AbstractType;



public final class Color extends AbstractType {
	
	private static final long serialVersionUID = 2146766823585577754L;
	
	public int a;
	public final int getA(){
		return a;
	}
	public final void setA(int a) {
		this.a = a;
	}

	private int r;
	public final int getR(){
		return 0xFF & r;
	}
	public final void setR(int r){
		this.r = r;
	}
	
	private int g;
	public final int getG(){
		return 0xFF & g;
	}
	public void setG(int g){
		this.g = g;
	}
	
	private int b;
	public final int getB(){
		return 0xFF & b;
	}
	public void setB(int b){
		this.b = b;
	}
	
	private final int increment;
	public final int getIncrement() {
		return increment;
	}
	
	
	
	public Color(int r, int g, int b, int a, int increment){
		setR(r);
		setG(g);
		setB(b);
		setA(a);
		this.increment = increment;
	}
	public Color(int a, int increment){
		this(127 + generateIncrement(127), 
				127 + generateIncrement(127), 
				127 + generateIncrement(127), 
				a,
				increment);
	}
	
	
	@Override
	public final void mutate() {
		setR(getR() + generateIncrement(getIncrement()));
		setG(getG() + generateIncrement(getIncrement()));
		setB(getB() + generateIncrement(getIncrement()));
		setA(getA());
	}
	private static final int generateIncrement(int increment){
		ThreadLocalRandom generator = ThreadLocalRandom.current();
		return generator.nextInt(increment+1) - generator.nextInt(increment+1);
	}
	
	
	@Override
	public final Color copy(){
		return new Color(getR(), getG(), getB(), getA(), getIncrement());
	}
	@Override
	public final String toString(){
		return String.format("C(%d,%d,%d,%d)", r, g, b, a);
	}
}
