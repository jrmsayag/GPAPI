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
 * Vertex.java
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



public final class Vertex extends AbstractType {
	
	private static final long serialVersionUID = 8182222874961265389L;
	
	private int x;
	public final int getX(){
		return x;
	}
	public final void setX(int x){
		this.x = x;
	}
	
	private int y;
	public final int getY(){
		return y;
	}
	public void setY(int y){
		this.y = y;
	}

	private final int increment;
	public final int getIncrement() {
		return increment;
	}
	
	
	
	public Vertex(int x, int y, int increment){
		setX(x);
		setY(y);
		this.increment = increment;
	}
	public Vertex(int increment){
		this(generateIncrement(increment), generateIncrement(increment), increment);
	}
	
	
	@Override
	public final void mutate() {
		setX(getX() + generateIncrement(getIncrement()));
		setY(getY() + generateIncrement(getIncrement()));
	}
	private static final int generateIncrement(int increment){
		ThreadLocalRandom generator = ThreadLocalRandom.current();
		return generator.nextInt(increment+1) - generator.nextInt(increment+1);
	}
	
	
	@Override
	public final Vertex copy(){
		return new Vertex(getX(), getY(), getIncrement());
	}
	@Override
	public final Vertex generateNew(){
		return new Vertex(getIncrement());
	}
	@Override
	public final String toString(){
		return String.format("V(%d,%d)", getX(), getY());
	}
}
