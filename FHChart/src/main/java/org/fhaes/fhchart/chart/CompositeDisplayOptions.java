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

import org.fhaes.fhchart.gui.CompositeDisplayOptionsDlg;


/**
 * FHX Fire History Plot Composite Axis display options.
 *
 * @version 	1.0 31 Oct 2006
 * @author 	Wendy Gross
 */
public class CompositeDisplayOptions{

	private FHPlotCommon fhxPlotCommon;

	// Dialog that allows options of this to be set.
	private CompositeDisplayOptionsDlg compositeDisplayOptionsDlg;

	// Options and reset values
	private boolean displayCompositeAxis;
	private boolean displayCompositeAxisReset;

	private static final boolean DISPLAY_COMPOSITE_AXIS_SYSTEM_DEFAULT = true;

	/**
	 * Constructor.
	 *
	 *
	 */
	public CompositeDisplayOptions(FHPlotCommon fhxPlotCommonIn) {
		this.fhxPlotCommon = fhxPlotCommonIn;
		this.setCompositeDisplayOptionsDlg(null);
	}

	public boolean init(Object[] args)
	{
		boolean rval = true;
		// Check to see if args is null, if it is null, 
		//    initializeToSystemDefaults
		//    otherwise
		// initializeToTemplate(args)

		// If template is not used, call initialzieToSystemDefaults, in
		//   this case args is ignored.
		if (args == null) {
			rval = initializeToSystemDefaults();
		} else{
			// initialize to args
		}
		/*
         void printClassName(Object obj) {
 
     }
		 */
		/*
Object o = map.get( key );
String type = o.getClass().getName();

if ( type.equals( String.class.getName() ) )
{
    // handle string...
}
else if ( type.equals( ArrayList.class.getName() ) )
{
    // handle array-list...
}

Or is something like this better:
if ( o instanceof String )
{
} 
		 */
		//http://forum.java.sun.com/thread.jspa?threadID=595455&messageID=3144436
		// exact match for a class.
		// if (object.getClass() == MyClass.class)
		return rval;
	}

	public void displayOptionsDialog()
	{
		this.setCompositeDisplayOptionsDlg(new CompositeDisplayOptionsDlg(
				this.fhxPlotCommon.getfhxPlotWin(), 
				true, 
				fhxPlotCommon, 
				this));
	}


	private boolean initializeToSystemDefaults()
	{
		boolean rval = true;
		displayCompositeAxis = DISPLAY_COMPOSITE_AXIS_SYSTEM_DEFAULT;
		displayCompositeAxisReset = DISPLAY_COMPOSITE_AXIS_SYSTEM_DEFAULT;
		return rval;
	}

	public boolean getdisplayCompositeAxis()
	{
		return displayCompositeAxis;
	}

	public void setdisplayCompositeAxis(boolean newVal)
	{
		this.displayCompositeAxis = newVal;
	}


	public boolean getdisplayCompositeAxisReset()
	{
		return displayCompositeAxisReset;
	}

	public CompositeDisplayOptionsDlg getCompositeDisplayOptionsDlg() {
		return compositeDisplayOptionsDlg;
	}

	public void setCompositeDisplayOptionsDlg(CompositeDisplayOptionsDlg compositeDisplayOptionsDlg) {
		this.compositeDisplayOptionsDlg = compositeDisplayOptionsDlg;
	}
}