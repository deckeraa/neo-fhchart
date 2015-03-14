/*******************************************************************************
 * Copyright (C) 2013 NOAA/NCDC - Wendy Gross.
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
 *     Wendy Gross
 *     Peter Brewer
 ******************************************************************************/
package org.fhaes.fhchart.chart;

import org.jfree.data.general.DatasetChangeListener;
import org.jfree.data.xy.AbstractIntervalXYDataset;
import org.jfree.data.xy.IntervalXYDataset;

/**
 * A quick and dirty sample dataset.
 */
public class PercentScarredXYDataset extends AbstractIntervalXYDataset implements IntervalXYDataset {

	private static final long serialVersionUID = 1L;
	private int startDate;
	private int numDates;
	// private float[] values;

	/** The start values. */
	private Double[] xStart;

	/** The end values. */
	private Double[] xEnd;

	/** The y values. */
	private Double[] yValues;

	/**
	 * Creates a new dataset.
	 */
	public PercentScarredXYDataset(int startDateIn, int numDatesIn, float[] values, FHPlotCommon fhxPlotCommon) {

		startDate = startDateIn;
		numDates = numDatesIn;

		xStart = new Double[numDates];
		xEnd = new Double[numDates];
		yValues = new Double[numDates];

		int currentDate = startDate;

		for (int i = 0; i < numDates; i++, currentDate++)
		{
			xStart[i] = (double) currentDate;
			if (i < numDates - 1)
			{
				xEnd[i] = (double) currentDate + 1;
			}
			else
			{
				xEnd[i] = (double) currentDate;
			}
			yValues[i] = (double) values[i];
		}
	}

	/**
	 * Returns the number of series in the dataset.
	 * 
	 * @return the number of series in the dataset.
	 */
	public int getSeriesCount() {

		return 1;
	}

	/**
	 * Returns the name of a series.
	 * 
	 * @param series the series (zero-based index).
	 * 
	 * @return the series name.
	 */
	public String getSeriesName(int series) {

		return "Percent scarred";
	}

	/**
	 * Returns the number of items in a series.
	 * 
	 * @param series the series (zero-based index).
	 * 
	 * @return the number of items within a series.
	 */
	public int getItemCount(int series) {

		return numDates;
	}

	/**
	 * Returns the x-value for an item within a series.
	 * <P>
	 * The implementation is responsible for ensuring that the x-values are presented in ascending order.
	 * 
	 * @param series the series (zero-based index).
	 * @param item the item (zero-based index).
	 * 
	 * @return the x-value for an item within a series.
	 */
	public Number getX(int series, int item) {

		return this.xStart[item];
	}

	/**
	 * Returns the y-value for an item within a series.
	 * 
	 * @param series the series (zero-based index).
	 * @param item the item (zero-based index).
	 * 
	 * @return the y-value for an item within a series.
	 */
	public Number getY(int series, int item) {

		return this.yValues[item];
	}

	/**
	 * Returns the starting X value for the specified series and item.
	 * 
	 * @param series the series (zero-based index).
	 * @param item the item within a series (zero-based index).
	 * 
	 * @return The value.
	 */
	public Number getStartX(int series, int item) {

		return this.xStart[item];
	}

	/**
	 * Returns the ending X value for the specified series and item.
	 * 
	 * @param series the series (zero-based index).
	 * @param item the item within a series (zero-based index).
	 * 
	 * @return the end x value.
	 */
	public Number getEndX(int series, int item) {

		return this.xEnd[item];
	}

	/**
	 * Returns the starting Y value for the specified series and item.
	 * 
	 * @param series the series (zero-based index).
	 * @param item the item within a series (zero-based index).
	 * 
	 * @return The value.
	 */
	public Number getStartY(int series, int item) {

		return this.yValues[item];
	}

	/**
	 * Returns the ending Y value for the specified series and item.
	 * 
	 * @param series the series (zero-based index).
	 * @param item the item within a series (zero-based index).
	 * 
	 * @return The value.
	 */
	public Number getEndY(int series, int item) {

		return this.yValues[item];
	}

	/**
	 * Registers an object for notification of changes to the dataset.
	 * 
	 * @param listener the object to register.
	 */
	public void addChangeListener(DatasetChangeListener listener) {

		// ignored
	}

	/**
	 * Deregisters an object for notification of changes to the dataset.
	 * 
	 * @param listener the object to deregister.
	 */
	public void removeChangeListener(DatasetChangeListener listener) {

		// ignored
	}

	// Required in JFreeChart v1.0.13
	@SuppressWarnings("rawtypes")
	@Override
	public Comparable getSeriesKey(int arg0) {

		return "PercentScarred";
	}
}
