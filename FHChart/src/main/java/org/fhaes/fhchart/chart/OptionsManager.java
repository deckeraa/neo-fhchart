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



/**
 * FHX Fire History Plot display options.
 *
 * @author 	Wendy Gross
 */
public class OptionsManager{
	FHPlotCommon fhxPlotCommon;
	private String title;

	// Options being managed
	private CompositeDisplayOptions compositeAxisDisplayOptions;
	private CompositeFilterOptions compositeAxisFilterOptions;
	private SeriesPlottedOptions seriesPlottedOptions;
	private boolean displayFireIndexPlot;
	private boolean displayFireChronologyPlot;
	private boolean displayCompositePlot;

	/**
	 * Constructor.
	 *
	 *
	 */
	public OptionsManager(FHPlotCommon fhxPlotCommonIn) {
		this.fhxPlotCommon = fhxPlotCommonIn;
	}

	public boolean initialize()
	{
		boolean rval = true;
		DataManager dataManager = fhxPlotCommon.getfhxDataManager();
		if (dataManager.siteName != null) {
			this.title = dataManager.siteName;
		} else {
			this.title = this.fhxPlotCommon.getinputFilename();
		}

		compositeAxisDisplayOptions = new CompositeDisplayOptions(this.fhxPlotCommon);
		rval = compositeAxisDisplayOptions.init(null);
		if (rval == true) {
			this.compositeAxisFilterOptions = new CompositeFilterOptions(this.fhxPlotCommon);
			rval = this.compositeAxisFilterOptions.initialize(null);
			if (rval == true) {
				this.seriesPlottedOptions = new SeriesPlottedOptions(this.fhxPlotCommon);
				rval = this.seriesPlottedOptions.initialize(null);
			}
		}
		displayFireIndexPlot = true;
		displayFireChronologyPlot = true;
		displayCompositePlot = true;

		return rval;
	}

	public CompositeDisplayOptions getcompositeAxisDisplayOptions()
	{
		return compositeAxisDisplayOptions;
	}

	public SeriesPlottedOptions getseriesPlottedOptions()
	{
		return seriesPlottedOptions;
	}

	public CompositeFilterOptions getcompositeAxisFilterOptions()
	{
		return compositeAxisFilterOptions;
	}

	public void settitle(String titleIn) 
	{
		title = titleIn;
	}

	public String gettitle() 
	{
		return title;
	}

	public boolean getdisplayFireIndexPlot() {
		return displayFireIndexPlot;
	}

	public boolean getdisplayFireChronologyPlot() {
		return displayFireChronologyPlot;
	}

	public boolean getdisplayCompositePlot() {
		return displayCompositePlot;
	}

	public void setdisplayFireIndexPlot(boolean flag) {
		displayFireIndexPlot = flag;
	}

	public void setdisplayFireChronologyPlot(boolean flag) {
		displayFireChronologyPlot = flag;
	}

	public void setdisplayCompositePlot(boolean flag) {
		displayCompositePlot = flag;
	}


	public CompositeFilterOptions getCompositeAxisFilterOptions(){
		return compositeAxisFilterOptions;
	}

}