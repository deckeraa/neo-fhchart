package org.fhaes.fhfilereader;

/*******************************************************************************
 * Copyright (C) 2015 Peter Brewer and Elena Velasquez
 * 
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 * 
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 * 
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 * 
 * Contributors:
 *     Peter Brewer
 *     Elena Velasquez
 ******************************************************************************/

import java.util.ArrayList;

import org.fhaes.enums.EventTypeToProcess;
import org.fhaes.enums.FireFilterType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * This abstract class provides methods that calculate basic data from the underlying raw data provided by the implementing
 * classes.
 * 
 * @author pbrewer
 * 
 */
public abstract class AbstractFireHistoryReader implements IFHAESReader {

	private static final Logger log = LoggerFactory
			.getLogger(AbstractFireHistoryReader.class);

	protected ArrayList<FHSeries> seriesList;
	private boolean isSeriesListPopulated = false;

	/**
	 * Construct a list of FHSeries, one for each series in the file
	 */
	protected abstract void populateSeriesList();

	/**
	 * Get an ArrayList of FHSeries classes, each representing a single series in the data file.
	 * 
	 * @return
	 */
	public ArrayList<FHSeries> getSeriesList() {
		if (!isSeriesListPopulated)
			populateSeriesList();

		return seriesList;
	}

	/**
	 * Returns an array of ints with size equal to the number of years in the file. The values in the array are the number trees
	 * for each year in the file (regardless of recording status), starting with the earliest, and finishing with the most recent
	 * year.
	 * 
	 * See also {@link org.fhaes.fhfilereader.AbstractFireHistoryReader.getRecordingDepths()}.
	 * 
	 * @return
	 */
	public int[] getSampleDepths() {

		// Instantiate the array ready to populate
		int[] arr = new int[this.getYearArray().size()];
		int totaltreeperyear = 0;

		for (int j = 0; j < this.getYearArray().size(); j++) {
			totaltreeperyear = 0;
			for (int k = 0; k < this.getNumberOfSeries(); k++) {
				if ((this.getRecorderYears2DArray().get(k).get(j) == 1)
						|| (this.getRecorderYears2DArray().get(k).get(j) == 0)) {
					totaltreeperyear = totaltreeperyear + 1;
				}
			}
			arr[j] = totaltreeperyear;
		}

		return arr;
	}

	/**
	 * Returns an array of ints with size equal to the number of years in the file. The values in the array are the number of
	 * samples that are in recording status for each year in the file, starting with the earliest, and finishing with the most
	 * recent year.
	 * 
	 * See also {@link org.fhaes.fhfilereader.AbstractFireHistoryReader.getSampleDepths()}.
	 * 
	 * @return
	 */
	public int[] getRecordingDepths() {

		// Instantiate the array ready to populate
		int[] arr = new int[this.getYearArray().size()];
		int totalrecorderperyear;

		for (int j = 0; j < this.getYearArray().size(); j++) {
			totalrecorderperyear = 0;
			for (int k = 0; k < this.getNumberOfSeries(); k++) {
				if (this.getRecorderYears2DArray().get(k).get(j) == 1) {
					totalrecorderperyear = totalrecorderperyear + 1;
				}
			}
			arr[j] = totalrecorderperyear;
		}

		return arr;
	}

	/**
	 * Returns an array of doubles with size equal to the number of years in the file. The values in the array are the percentage
	 * of samples that are in recording status and which have an event. The type of event (fire, injury or both) is specified as a
	 * parameter.
	 * 
	 * @param eventTypeToProcess
	 *            - whether to calculate the percentage based on fire events, injury events, or both.
	 * @return
	 */
	public double[] getPercentScarred(EventTypeToProcess eventTypeToProcess) {

		// Instantiate the array ready to populate
		double[] arrpc = new double[this.getYearArray().size()];
		int[] arr1 = new int[this.getYearArray().size()];

		for (int j = 0; j < this.getYearArray().size(); j++) {
			int totalrecorderperyear = 0;
			for (int k = 0; k < this.getNumberOfSeries(); k++) {
				if (this.getRecorderYears2DArray().get(k).get(j) == 1) {
					totalrecorderperyear = totalrecorderperyear + 1;
				}
			}
			arr1[j] = totalrecorderperyear;

			if (arr1[j] != 0) {
				arrpc[j] = (double) ((this.getFilterArrays(eventTypeToProcess)
						.get(0).get(j) / arr1[j]) * 100);
			} else {
				arrpc[j] = 0.0;
			}
		}

		return arrpc;
	}

	/**
	 * Returns an ArrayList of years which fulfill the composite filter options specified.
	 * 
	 * @param eventsToProcess
	 *            - whether to calculate the composite based on fire events, injury events, or both
	 * @param filterType
	 *            - whether to filter on absolute numbers of fires, or percentage of recording trees
	 * @param filterValue
	 *            - either the number of samples or percentage of samples that must be scared before the year is included in the
	 *            composite
	 * @param minNumberOfSamples
	 *            - The minimum number of samples that must be in recording status for the year to be considered for inclusion in
	 *            the composite, regardless of filterValue.
	 * @return
	 */
	public ArrayList<Integer> getCompositeFireYears(
			EventTypeToProcess eventsToProcess, FireFilterType filterType,
			double filterValue, int minNumberOfSamples) {

		// Instantiate the array ready to populate
		ArrayList<Integer> compositeYears = new ArrayList<Integer>();

		// Calculate composite based on filter type
		if (filterType.equals(FireFilterType.NUMBER_OF_EVENTS)) {

			ArrayList<Double> dataArray = this.getFilterArrays(eventsToProcess)
					.get(0);

			Integer currentYear = this.getFirstYear();
			for (int i = 0; i < dataArray.size(); i++) {
				if (dataArray.get(i) >= filterValue
						&& dataArray.get(i) >= minNumberOfSamples) {
					compositeYears.add(currentYear);
				}

				currentYear++;
			}

		} else if (filterType.equals(FireFilterType.PERCENTAGE_OF_EVENTS)) {

			double[] percentScarred = getPercentScarred(eventsToProcess);
			ArrayList<Double> dataArray = this.getFilterArrays(eventsToProcess)
					.get(0);

			Integer currentYear = this.getFirstYear();
			for (int i = 0; i < percentScarred.length; i++) {
				if (percentScarred[i] >= filterValue
						&& dataArray.get(i) >= minNumberOfSamples) {
					compositeYears.add(currentYear);
				}

				currentYear++;
			}

		} else {
			log.error("Unsupported FireFilterType");
			return null;
		}

		return compositeYears;
	}
}
