/*******************************************************************************
 * Copyright (c)  2015 Peter Brewer and Elena Velasquez
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the GNU Public License v3.0
 * which accompanies this distribution, and is available at
 * http://www.gnu.org/licenses/gpl.html
 * 
 ******************************************************************************/
package org.fhaes.fhutil;

import java.io.File;
import java.util.ArrayList;

import junit.framework.TestCase;

import org.fhaes.enums.EventTypeToProcess;
import org.fhaes.enums.FireFilterType;
import org.fhaes.fhfilereader.FHSeries;
import org.fhaes.fhfilereader.FHX2FileReader;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Unit test for simple App.
 */
public class FileReaderCalculationTests extends TestCase {

	private static final Logger log = LoggerFactory
			.getLogger(FileReaderCalculationTests.class);

	/**
	 * Test the access to the data required to plot the FHChart index plot
	 */
	public void testGetFireIndexPlotData() {

		// Parameters for testing
		File file = new File("TestData/mxcor001.fhx");
		EventTypeToProcess eventTypeToProcess = EventTypeToProcess.FIRE_EVENT;

		FHX2FileReader fr = new FHX2FileReader(file);

		int firstyear = fr.getFirstYear();
		int[] sampledepths = fr.getSampleDepths();
		int[] recordingDepths = fr.getRecordingDepths();
		double[] percentScarred = fr.getPercentScarred(eventTypeToProcess);

		int currentyear = firstyear;
		log.debug("YEAR, SAMPLE DEPTH, RECORDING DEPTH,  PERCENT SCARRED ");
		for (int i = 0; i < sampledepths.length; i++) {
			log.debug(currentyear + ", " + sampledepths[i] + ", "
					+ recordingDepths[i] + ", " + +percentScarred[i]);
			currentyear++;
		}

	}

	/**
	 * Test access to the data required for the FHChart chronology plot
	 */
	public void testGetChronologyPlotData() {
		File file = new File("TestData/mxcor001.fhx");

		FHX2FileReader fr = new FHX2FileReader(file);

		ArrayList<FHSeries> seriesList = fr.getSeriesList();

		log.debug("File contains " + seriesList.size() + " series");

		for (FHSeries series : seriesList) {
			log.debug("Series          : " + series.getTitle());
			log.debug("   First year   : " + series.getFirstYear());
			log.debug("   Last year    : " + series.getLastYear());
			log.debug("   Series length: " + series.getLength());

			// boolean[] eventYears = series.getEventYears();
			// boolean[] injuryYears = series.getInjuryYears();
			// boolean[] recorderYears = series.getRecordingYears();

		}
	}

	/**
	 * Get a list of years that match the composite filter. This data is typically used in the FHChart composite plot.
	 */
	public void testGetCompositeFireYears() {

		// Parameters for testing
		File file = new File("TestData/mxcor001.fhx");
		FireFilterType filterType = FireFilterType.PERCENTAGE_OF_EVENTS;
		EventTypeToProcess eventType = EventTypeToProcess.FIRE_AND_INJURY_EVENT;
		Double filterValue = 80.0;
		Integer minNumberOfSamples = 1;

		FHX2FileReader fr = new FHX2FileReader(file);
		ArrayList<Integer> fireYears = fr.getCompositeFireYears(eventType,
				filterType, filterValue, minNumberOfSamples);

		if (fireYears.size() > 0) {
			log.debug("Total number of composite fire years = "
					+ fireYears.size());
		} else {
			log.debug("No composite fire years match criteria in this file");
			return;
		}

		// Debug print each value
		int ind = 1;
		for (Integer value : fireYears) {
			log.debug("Composite year " + ind + " = " + value);
			ind++;
		}
	}

}
