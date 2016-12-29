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
 * FrequencyArchitectureAlteration.java
 * ---------------
 * (C) Copyright 2015-2016, by Jeremy Sayag and Contributors.
 *
 * Original Author:  Jeremy Sayag;
 * Contributor(s):   ;
 * 
 */


package com.gpapi.individuals.cells.nucleuses.geneticoperators.architecturealterationschemes;

import java.util.concurrent.ThreadLocalRandom;

import com.gpapi.individuals.cells.nucleuses.Nucleus;
import com.gpapi.individuals.cells.nucleuses.geneticoperators.GeneticOperatorInterface;
import com.gpapi.individuals.cells.nucleuses.geneticoperators.architecturealterationschemes.architecturealterationoperators.ArchitectureAlterationOperatorInterface;



public final class FrequencyArchitectureAlteration extends AbstractArchitectureAlterationScheme {

	private static final long serialVersionUID = -8852924978177695084L;
	
	private final double frequency;
	
	
	
	public FrequencyArchitectureAlteration(ArchitectureAlterationOperatorInterface architectureAlterationOperator, double frequency) {
		super(architectureAlterationOperator);
		this.frequency = frequency;
	}

	@Override
	public final Nucleus alterArchitecture(Nucleus nucleus, GeneticOperatorInterface geneticOperator) {
		if(ThreadLocalRandom.current().nextDouble() < frequency)
			getArchitectureAlterationOperator().architectureAlteration(nucleus);
		return nucleus;
	}

	@Override
	public final FrequencyArchitectureAlteration copy() {
		return new FrequencyArchitectureAlteration(getArchitectureAlterationOperator(), frequency);
	}
}
