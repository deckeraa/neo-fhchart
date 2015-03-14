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

import org.fhaes.fhchart.gui.SeriesPlottedOptionsDlg;

/**
 * FHX Fire History Plot Series Plotted options.
 * 
 * @author Wendy Gross
 */
public class SeriesPlottedOptions {

	private FHPlotCommon fhxPlotCommon;

	// Dialog that allows series plotted to be set.
	@SuppressWarnings("unused")
	private SeriesPlottedOptionsDlg seriesPlottedOptionsDlg;

	// Options and their reset values
	private int[] seriesPlottedFHX2ColumnNo;
	private int[] seriesPlottedFHX2ColumnNoReset;
	private int[] seriesAvailableFHX2ColumnNo;
	private int[] seriesAvailableFHX2ColumnNoReset;

	/**
	 * Constructor.
	 * 
	 * 
	 */
	public SeriesPlottedOptions(FHPlotCommon fhxPlotCommonIn) {

		this.fhxPlotCommon = fhxPlotCommonIn;
		// this.seriesPlottedDlg = null;
	}

	public boolean initialize(Object[] args) {

		boolean rval = true;
		// Check to see if args is null, if it is null,
		// initializeToSystemDefaults
		// otherwise
		// initializeToTemplate(args)

		// If template is not used, call initialzieToSystemDefaults, in
		// this case args is ignored.
		if (args == null)
		{
			rval = initializeToSystemDefaults();
		}
		else
		{
			// initialize to args
		}
		/*
		 * void printClassName(Object obj) {
		 * 
		 * }
		 */
		/*
		 * Object o = map.get( key ); String type = o.getClass().getName();
		 * 
		 * if ( type.equals( String.class.getName() ) ) { // handle string... } else if ( type.equals( ArrayList.class.getName() ) ) { //
		 * handle array-list... }
		 * 
		 * Or is something like this better: if ( o instanceof String ) { }
		 */
		// http://forum.java.sun.com/thread.jspa?threadID=595455&messageID=3144436
		// exact match for a class.
		// if (object.getClass() == MyClass.class)
		return rval;
	}

	public void displayOptionsDialog() {

		this.seriesPlottedOptionsDlg = new SeriesPlottedOptionsDlg(this.fhxPlotCommon.getfhxPlotWin(), true, fhxPlotCommon, this);
	}

	@SuppressWarnings("unused")
	private boolean initializeToTemplate(Object[] args) {

		boolean rval = true;
		return rval;
	}

	private boolean initializeToSystemDefaults() {

		boolean rval = true;
		int numSamples = fhxPlotCommon.getfhxPlotDataManager().getnumSamples();
		seriesPlottedFHX2ColumnNo = new int[numSamples];

		for (int i = 0; i < numSamples; i++)
		{
			seriesPlottedFHX2ColumnNo[i] = -1;
		}
		for (int i = 0; i < numSamples; i++)
		{
			seriesPlottedFHX2ColumnNo[i] = i + 1;
		}

		seriesPlottedFHX2ColumnNoReset = new int[numSamples];
		for (int i = 0; i < numSamples; i++)
		{
			seriesPlottedFHX2ColumnNoReset[i] = -1;
		}
		for (int i = 0; i < numSamples; i++)
		{
			seriesPlottedFHX2ColumnNoReset[i] = i + 1;
		}

		seriesAvailableFHX2ColumnNo = new int[numSamples];
		for (int i = 0; i < numSamples; i++)
		{
			seriesAvailableFHX2ColumnNo[i] = -1;
		}
		seriesAvailableFHX2ColumnNoReset = new int[numSamples];
		for (int i = 0; i < numSamples; i++)
		{
			seriesAvailableFHX2ColumnNoReset[i] = -1;
		}
		return rval;
	}

	// get and set methods for seriesPlottedFHX2ColumnNo, seriesPlottedFHX2ColumnNoReset,
	// seriesAvailableFHX2ColumnNo, seriesAvailableFHX2ColumnNoReset
	public void setseriesPlottedFHX2ColumnNo(int[] newVal) {

		int numSamples = fhxPlotCommon.getfhxPlotDataManager().getnumSamples();

		for (int i = 0; i < numSamples; i++)
		{
			seriesPlottedFHX2ColumnNo[i] = -1;
		}
		for (int i = 0; i < newVal.length; i++)
		{
			seriesPlottedFHX2ColumnNo[i] = newVal[i];
		}
	}

	public int[] getseriesPlottedFHX2ColumnNo() {

		int[] rval = new int[countOfSeriesPlotted()];
		for (int i = 0; i < countOfSeriesPlotted(); i++)
		{
			rval[i] = this.seriesPlottedFHX2ColumnNo[i];
		}
		return rval;
	}

	public void setseriesPlottedFHX2ColumnNoReset(int[] newVal) {

		int numSamples = fhxPlotCommon.getfhxPlotDataManager().getnumSamples();

		for (int i = 0; i < numSamples; i++)
		{
			seriesPlottedFHX2ColumnNoReset[i] = -1;
		}
		for (int i = 0; i < newVal.length; i++)
		{
			seriesPlottedFHX2ColumnNoReset[i] = newVal[i];
		}
	}

	public int[] getseriesPlottedFHX2ColumnNoReset() {

		int[] rval = new int[countOfSeriesPlottedReset()];
		for (int i = 0; i < countOfSeriesPlottedReset(); i++)
		{
			rval[i] = this.seriesPlottedFHX2ColumnNoReset[i];
		}
		return rval;

	}

	public void setseriesAvailableFHX2ColumnNo(int[] newVal) {

		int numSamples = fhxPlotCommon.getfhxPlotDataManager().getnumSamples();

		for (int i = 0; i < numSamples; i++)
		{
			seriesAvailableFHX2ColumnNo[i] = -1;
		}
		for (int i = 0; i < newVal.length; i++)
		{
			seriesAvailableFHX2ColumnNo[i] = newVal[i];
		}
	}

	public int[] getseriesAvailableFHX2ColumnNo() {

		int[] rval = new int[countOfSeriesAvailable()];
		for (int i = 0; i < countOfSeriesAvailable(); i++)
		{
			rval[i] = this.seriesAvailableFHX2ColumnNo[i];
		}
		return rval;
	}

	public void setseriesAvailableFHX2ColumnNoReset(int[] newVal) {

		int numSamples = fhxPlotCommon.getfhxPlotDataManager().getnumSamples();

		for (int i = 0; i < numSamples; i++)
		{
			seriesAvailableFHX2ColumnNoReset[i] = -1;
		}
		for (int i = 0; i < newVal.length; i++)
		{
			seriesAvailableFHX2ColumnNoReset[i] = newVal[i];
		}
	}

	public int[] getseriesAvailableFHX2ColumnNoReset() {

		int[] rval = new int[countOfSeriesAvailableReset()];
		for (int i = 0; i < countOfSeriesAvailableReset(); i++)
		{
			rval[i] = this.seriesAvailableFHX2ColumnNoReset[i];
		}
		return rval;

	}

	// Check to see if a series is plotted, input is its column number
	public boolean isSeriesPlotted(int FHX2ColumnNoIn) {

		boolean rval = false;
		int numSamples = fhxPlotCommon.getfhxPlotDataManager().getnumSamples();
		for (int i = 0; i < numSamples; i++)
		{
			if (seriesPlottedFHX2ColumnNo[i] == FHX2ColumnNoIn)
			{
				rval = true;
				break;
			}
		}

		return rval;
	}

	// Count of data values that are not -1 for SeriesPlottedFHX2ColumnNo,
	// SeriesPlottedFHX2ColumnNoReset, SeriesAvailableFHX2ColumnNo, SeriesAvailableFHX2ColumnNo
	public int countOfSeriesPlotted() {

		int rval = 0;
		int numSamples = fhxPlotCommon.getfhxPlotDataManager().getnumSamples();

		for (int i = 0; i < numSamples; i++)
		{
			if (seriesPlottedFHX2ColumnNo[i] != -1)
			{
				rval++;
			}
		}
		return rval;
	}

	public int countOfSeriesAvailable() {

		int rval = 0;
		int numSamples = fhxPlotCommon.getfhxPlotDataManager().getnumSamples();

		for (int i = 0; i < numSamples; i++)
		{
			if (seriesAvailableFHX2ColumnNo[i] != -1)
			{
				rval++;
			}
		}
		return rval;
	}

	public int countOfSeriesPlottedReset() {

		int rval = 0;
		int numSamples = fhxPlotCommon.getfhxPlotDataManager().getnumSamples();

		for (int i = 0; i < numSamples; i++)
		{
			if (seriesPlottedFHX2ColumnNoReset[i] != -1)
			{
				rval++;
			}
		}
		return rval;
	}

	public int countOfSeriesAvailableReset() {

		int rval = 0;
		int numSamples = fhxPlotCommon.getfhxPlotDataManager().getnumSamples();

		for (int i = 0; i < numSamples; i++)
		{
			if (seriesAvailableFHX2ColumnNo[i] != -1)
			{
				rval++;
			}
		}
		return rval;
	}
}
