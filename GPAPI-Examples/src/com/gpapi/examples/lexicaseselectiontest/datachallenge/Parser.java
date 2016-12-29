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
 * Parser.java
 * ---------------
 * (C) Copyright 2015-2016, by Jeremy Sayag and Contributors.
 *
 * Original Author:  Jeremy Sayag;
 * Contributor(s):   ;
 * 
 */


package com.gpapi.examples.lexicaseselectiontest.datachallenge;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;



public class Parser {
	
	private static final ArrayList<Sample> trainSamples = new ArrayList<Sample>();
	public static final ArrayList<Sample> getTrainSamples() {
		return trainSamples;
	}

	private static final ArrayList<Sample> submitSamples = new ArrayList<Sample>();
	public static final ArrayList<Sample> getSubmitSamples() {
		return submitSamples;
	}

	public static final void parse(String filename) throws IOException {
		trainSamples.clear();
		submitSamples.clear();
		
		BufferedReader reader = new BufferedReader(new FileReader(filename));
		
		try {
			String line = reader.readLine();
			while((line = reader.readLine()) != null){
				Sample newSample = new Sample(line);
				if(newSample.shot_made_flag < 0)
					submitSamples.add(newSample);
				else
					trainSamples.add(newSample);
			}
			Collections.sort(trainSamples);
			Collections.sort(submitSamples);
		} finally {
			reader.close();
		}
	}
}
