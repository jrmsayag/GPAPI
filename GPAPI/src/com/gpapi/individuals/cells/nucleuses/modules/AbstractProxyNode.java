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
 * AbstractProxyNode.java
 * ---------------
 * (C) Copyright 2015-2016, by Jeremy Sayag and Contributors.
 *
 * Original Author:  Jeremy Sayag;
 * Contributor(s):   ;
 * 
 */


package com.gpapi.individuals.cells.nucleuses.modules;

import com.gpapi.individuals.cells.nucleuses.nodes.AbstractNode;
import com.gpapi.individuals.cells.nucleuses.types.AbstractType;



public abstract class AbstractProxyNode extends AbstractNode {

	private static final long serialVersionUID = 8215270082952354977L;
	
	private AbstractProxyNodeSlot slot;
	protected final AbstractProxyNodeSlot getSlot() {
		return slot;
	}
	protected final void setSlot(AbstractProxyNodeSlot slot) {
		if(slot == null)
			throw new NullPointerException("Slot can't be null!");
		else if(!slot.getReturnType().isTheSameAs(getSlot().getReturnType()))
			throw new RuntimeException("Trying to set a slot whose return type is not compatible with the previous slot's one!");
		else
			this.slot = slot;
	}
	
	
	
	public AbstractProxyNode(AbstractProxyNodeSlot slot){
		if(slot == null)
			throw new NullPointerException("A slot must be specified!");
		else
			this.slot = slot;
	}
	
	public final int getId(){
		return getSlot().getId();
	}
	
	@Override
	public String getName(){
		return getSlot().getName();
	}
	@Override
	public AbstractType getReturnType() {
		return getSlot().getReturnType();
	}
}
