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

import org.fhaes.fhchart.gui.CompositeFilterOptionsDlg;
import org.fhaes.fhchart.gui.CompositeFilterViewDlg;

public class CompositeFilterOptions {

	private FHPlotCommon fhxPlotCommon;

	// Dialog that allows Composite Axis filter options of this to be set.
	@SuppressWarnings("unused")
	private CompositeFilterOptionsDlg compositeFilterOptionsDlg;

	// Options and their reset values
	private int minimumPercentScarred;
	private int miniumnPercentScarredReset;
	private boolean includeOtherInjuries;
	private boolean includeOtherInjuriesReset;
	private int minimumSampleDepth;
	private int minimumSampleDepthReset;
	private int minimumNumberSamples;
	private int minimumNumberSamplesReset;

	// System defaults for Options set in this class.
	private static final int MINIMUM_PERCENT_SCARRED_SYSTEM_DEFAULT = 0;
	private static final boolean INCLUDE_OTHER_INJURIES_SYSTEM_DEFAULT = false;
	private static final int MINIMUM_SAMPLE_DEPTH = 1;
	private static final int MINIMUM_NUMBER_SAMPLES = 1;

	/**
	 * Constructor.
	 * 
	 * 
	 */
	public CompositeFilterOptions(FHPlotCommon fhxPlotCommonIn) {

		this.fhxPlotCommon = fhxPlotCommonIn;
		this.compositeFilterOptionsDlg = null;
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

		this.compositeFilterOptionsDlg = new CompositeFilterOptionsDlg(this.fhxPlotCommon.getfhxPlotWin(), true, fhxPlotCommon, this,
				this.fhxPlotCommon.getfhxPlotDataManager().countOfFHX2ColumnNosBeingPlotted());
	}

	/**
	 * Determine if a year of a given sample is a recorder year
	 * 
	 * @param year year to be determined.
	 * @param sampleId identifier of the sample.
	 */
	@SuppressWarnings("unused")
	public void displayViewDialog() {

		CompositeFilterViewDlg compositeFilterViewDlg = new CompositeFilterViewDlg(this.fhxPlotCommon.getfhxPlotWin(), false,
				fhxPlotCommon, this, false);
	}

	@SuppressWarnings("unused")
	private boolean initializeToTemplate(Object[] args) {

		boolean rval = true;
		return rval;
	}

	private boolean initializeToSystemDefaults() {

		boolean rval = true;
		minimumPercentScarred = MINIMUM_PERCENT_SCARRED_SYSTEM_DEFAULT;
		miniumnPercentScarredReset = MINIMUM_PERCENT_SCARRED_SYSTEM_DEFAULT;
		includeOtherInjuries = INCLUDE_OTHER_INJURIES_SYSTEM_DEFAULT;
		includeOtherInjuriesReset = INCLUDE_OTHER_INJURIES_SYSTEM_DEFAULT;
		minimumSampleDepth = MINIMUM_SAMPLE_DEPTH;
		minimumSampleDepthReset = MINIMUM_SAMPLE_DEPTH;
		minimumNumberSamples = MINIMUM_NUMBER_SAMPLES;
		minimumNumberSamplesReset = MINIMUM_NUMBER_SAMPLES;
		return rval;
	}

	public boolean getincludeOtherInjuries() {

		return includeOtherInjuries;
	}

	public void setincludeOtherInjuries(boolean newVal) {

		this.includeOtherInjuries = newVal;
	}

	public boolean getincludeOtherInjuriesReset() {

		return includeOtherInjuriesReset;
	}

	public int getminimumPercentScarred() {

		return minimumPercentScarred;
	}

	public void setminimumPercentScarred(int newVal) {

		this.minimumPercentScarred = newVal;
	}

	public int getminimumPercentScarredReset() {

		return this.miniumnPercentScarredReset;
	}

	public int getminimumSampleDepth() {

		return minimumSampleDepth;
	}

	public void setminimumSampleDepth(int newVal) {

		this.minimumSampleDepth = newVal;
	}

	public int getminimumSampleDepthReset() {

		return minimumSampleDepthReset;
	}

	public int getminimumNumberSamples() {

		return minimumNumberSamples;
	}

	public void setminimumNumberSamples(int newVal) {

		this.minimumNumberSamples = newVal;
	}

	public int getminimumNumberSamplesReset() {

		return minimumNumberSamplesReset;
	}
}
