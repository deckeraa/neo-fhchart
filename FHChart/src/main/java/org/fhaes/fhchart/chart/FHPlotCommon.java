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

import java.io.File;
import java.lang.reflect.Method;

import javax.swing.JFrame;
import javax.swing.JOptionPane;

import org.fhaes.fhchart.gui.PlotWindow;
import org.fhaes.fhchart.gui.StatusFrame;
import org.jfree.chart.ChartPanel;
import org.jfree.data.Range;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * FHXPlotCommon, global methods and members used by a FHX plot.
 * 
 * @version 1.0 31 Oct 2006
 * @author Wendy Gross
 */

public class FHPlotCommon {

	static final String debugFileName = "FHDebugLog.txt";

	@SuppressWarnings("unused")
	private static final Logger log = LoggerFactory.getLogger(FHPlotCommon.class);

	public static final String FHAES_AUTO_OUTPUT_DIRECTORY = System.getProperty("java.io.tmpdir") + File.separator;
	public static final String FHAES_AUTO_INPUT_LIST = FHAES_AUTO_OUTPUT_DIRECTORY + File.separator + "fhaes_auto_input_list.txt";

	// Members used to define shape and line renderer for series of Event Plots.
	// Event Plots depict:
	// Fire Events, Injury Events, Lifecycle Events (pith, bark,
	// inner year, outter year, recorder years and nonrecorder years)
	// and what years meet current the current filter.
	// Every Event Plot has 20 series. All series exist, however they are
	// allowed to be empty.
	// Series 0 through 17 represent the 18 single character codes of the FHX file.
	/*
	 * // Series 18 represents the years that meet the current filter settings // Series 19 is a false series used to set the vertical range
	 * of the Event Plot.
	 */

	// Line and Shape Renderer Settinig - ENUMS
	static final int LSRS_SHAPE = 0; // Render shapes only for the series.
	static final int LSRS_LINE = 1;  // Render lines only for the series.
	// Count of fire, injury and lifecycle events
	// that are designated in the FHX file by a single character code.
	static final int TOTAL_NUM_EVENTS = 18;
	static final char[] EVENT_CHAR;
	static final int[] EVENT_ID;
	static final int[] EVENT_LSRS; // One of the Event Render Type Enums.
	static
	{
		EVENT_CHAR = new char[] { 'D', 'E', 'M', 'L', 'A', 'U', 'd', 'e', 'm', 'l', 'a', 'u', '[', ']', '{', '}', '.', '|' };
	}
	static
	{
		EVENT_ID = new int[] { 0, 1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 11, 12, 13, 14, 15, 16, 17 };
	}
	static
	{
		EVENT_LSRS = new int[] { 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 1, 1 };
	}
	/*
	 * // Filter symbol information. Can be displayed if year meets the defined filter. static int FILTER_ID = 18; static int FILTER_LSRS =
	 * 0;
	 * 
	 * // False symbol information. // A false series is used to set the maximum vertical range of the each subplot. static int FALSE_ID =
	 * 19; static int FALSE_LSRS = 0;
	 */
	private String recorderCodes = "|";
	private String fireEventCodes = "DEMLAU";
	private String nonrecorderCodes = ".";
	private String injuryCodes = "demlau";
	private String pithCodes = "[";
	private String innerCodes = "{";
	private String barkCodes = "]";
	private String outerCodes = "}";

	@SuppressWarnings("unused")
	private File debugFile = null; // File to which debug statements are printed.

	// For use during development only.
	private PlotWindow fhxPlotWin; // main window for plot

	@SuppressWarnings("unused")
	private File inputFile = null;

	private String inputFilename = null;

	// Managers
	private DataManager fhxDataManager;
	private RendererManager fhxPlotRendererManager;
	private JFreeChartManager fhxJFreeChartManager;
	private OptionsManager fhxPlotOptionsManager;

	private ChartPanel chartPanel;
	private boolean fhxDataAndOptionsInitialized;
	private StatusFrame progressWindow = null;
	private String progressWindowTitle;
	private String progressWindowSubject;

	@SuppressWarnings("unused")
	private String progressWindowMessage;

	private boolean autoExport;

	/**
	 * Constructs a new FHXPlotCommon.
	 * 
	 * 
	 */
	public FHPlotCommon(PlotWindow fhPlotWindowIn, boolean autoExportIn) {

		this.fhxPlotWin = fhPlotWindowIn;
		this.autoExport = autoExportIn;

		this.fhxDataManager = null;
		this.fhxPlotRendererManager = null;
		this.fhxJFreeChartManager = null;
		this.debugFile = null;

		this.fhxDataAndOptionsInitialized = false;
		this.fhxPlotOptionsManager = null;
		this.chartPanel = null;
		// appendToDebug("In FHXPlotCommon Constructor, autoExport is<" + autoExport + ">\n");
	}

	public boolean initialize(File fileIn) {

		boolean rval = true;
		// appendToDebug("**FHXPlotCommon::initialize enter\n");
		// appendToDebug("FHXPlotCommon::About to construct and init FHXPlotDataManager\n");
		this.inputFile = fileIn;
		this.inputFilename = fileIn.getName();
		this.fhxDataManager = new DataManager(this, fileIn);
		if (this.fhxDataManager.initialize())
		{
			fhxDataAndOptionsInitialized = true;
			this.fhxPlotOptionsManager = new OptionsManager(this);
			if (this.fhxPlotOptionsManager.initialize())
			{
				fhxDataAndOptionsInitialized = true;
			}
			else
			{
				fhxDataAndOptionsInitialized = false;
				rval = false;
			}
		}
		else
		{
			fhxDataAndOptionsInitialized = false;
			rval = false;
		}

		if (rval)
		{
			this.setprogressWindowTitle("Please wait. Your Plot is being created ...");
			this.setprogressWindowSubject("Progress:");
		}

		return rval;
	}

	public ChartPanel createChartPanel(int plotWidth, Range domainRangeIn) {

		try
		{
			if (this.fhxDataAndOptionsInitialized)
			{

				this.fhxPlotRendererManager = new RendererManager(this);
				this.fhxJFreeChartManager = new JFreeChartManager(this);
				this.chartPanel = this.fhxJFreeChartManager.createChartPanel(plotWidth, domainRangeIn);
				this.chartPanel.setRangeZoomable(false);
			}
		}
		catch (NullPointerException e)
		{

		}

		return this.chartPanel;

	}

	// Get and Set members
	public boolean getautoExport() {

		return autoExport;
	}

	public PlotWindow getfhxPlotWin() {

		return fhxPlotWin;
	}

	public DataManager getfhxDataManager() {

		return this.fhxDataManager;
	}

	public DataManager getfhxPlotDataManager() {

		return this.fhxDataManager;
	}

	public RendererManager getfhxPlotRendererManager() {

		return fhxPlotRendererManager;
	}

	public JFreeChartManager getfhxJFreeChartManager() {

		return fhxJFreeChartManager;
	}

	public OptionsManager getfhxPlotOptionsManager() {

		return fhxPlotOptionsManager;
	}

	/*
	 * public String getlastPathVisited() { String rval; rval = null; if (this.fhxPlotWin != null) { rval =
	 * this.fhxPlotWin.getlastPathVisited(); } return rval; } public void setlastPathVisited(String lastPathVisited) { if (this.fhxPlotWin
	 * != null) { this.fhxPlotWin.setlastPathVisited(lastPathVisited); } }
	 */

	public String getinputFilename() {

		return this.inputFilename;
	}

	/*
	 * private String recorderCodes = "|"; private String fireEventCodes = "DEMLAU"; private string nonrecorderCodes = "."; private String
	 * injuryCodes = "demlau"; private String pithCodes = "["; private String innerCodes = "{"; private String barkCodes = "]"; private
	 * String outerCodes = "}";
	 */

	public String getrecorderCodes() {

		return recorderCodes;
	}

	public String getfireEventCodes() {

		return fireEventCodes;
	}

	public String getnonrecorderCodes() {

		return nonrecorderCodes;
	}

	public String getinjuryCodes() {

		return injuryCodes;
	}

	public String getpithCodes() {

		return pithCodes;
	}

	public String getinnerCodes() {

		return innerCodes;
	}

	public String getbarkCodes() {

		return barkCodes;
	}

	public String getouterCodes() {

		return outerCodes;
	}

	// Progress/Status Window Methods
	public void openProgressWindow(JFrame parentFrame) {

		if (this.progressWindow == null)
		{
			this.progressWindow = new StatusFrame(parentFrame, this, this.progressWindowTitle, this.progressWindowSubject);
		}
		// this.progressWindow.setCursor(new Cursor(Cursor.WAIT_CURSOR));
	}

	public void setprogressWindowTitle(String titleIn) {

		this.progressWindowTitle = titleIn;
	}

	public void setprogressWindowSubject(String subjectIn) {

		this.progressWindowSubject = subjectIn;
		this.updateProgressWindowSubjectMsg(subjectIn);
	}

	public void setprogressWindowMessage(String messageIn) {

		this.progressWindowMessage = messageIn;
		updateProgressWindowStatusMsg(messageIn);
	}

	public void closeProgressWindow() {

		// this.progressWindow.setCursor(new Cursor(Cursor.DEFAULT_CURSOR));
		if (this.progressWindow != null)
		{
			this.progressWindow.closeWindow();
			this.progressWindow = null;
		}
	}

	private void updateProgressWindowSubjectMsg(String newMsg) {

		String newMessage = newMsg;
		if (newMessage == null)
		{
			newMessage = " ";
		}
		if (this.progressWindow != null)
			this.progressWindow.updateSubjectMessage(newMessage);
	}

	private void updateProgressWindowStatusMsg(String newMsg) {

		String newMessage = newMsg;
		if (newMessage == null)
		{
			newMessage = " ";
		}
		if (this.progressWindow != null)
			this.progressWindow.updateStatusMessage(newMessage);
	}

	@SuppressWarnings({ "rawtypes", "unchecked" })
	@Deprecated
	public static void showDocumentInWebBrowser(String url, String showDocumentErrorMessage) {

		JOptionPane.showMessageDialog(null, "Enter showDocumentInWebBrowser");

		String osName = System.getProperty("os.name");
		try
		{
			if (osName.startsWith("Mac OS"))
			{
				Class fileMgr = Class.forName("com.apple.eio.FileManager");
				Method openURL = fileMgr.getDeclaredMethod("openURL", new Class[] { String.class });
				openURL.invoke(null, new Object[] { url });
			}
			else if (osName.startsWith("Windows"))
			{
				Runtime.getRuntime().exec("rundll32 url.dll,FileProtocolHandler " + url);
			}
			else
			{ // assume Unix or Linux
				String[] browsers = { "firefox", "opera", "konqueror", "epiphany", "mozilla", "netscape" };
				String browser = null;
				for (int count = 0; count < browsers.length && browser == null; count++)
				{
					if (Runtime.getRuntime().exec(new String[] { "which", browsers[count] }).waitFor() == 0)
					{
						browser = browsers[count];
					}
				}
				if (browser == null)
				{
					throw new Exception("Could not find web browser");
				}
				else
				{
					Runtime.getRuntime().exec(new String[] { browser, url });
				}
			}
		}
		catch (Exception e)
		{
			JOptionPane.showMessageDialog(null, showDocumentErrorMessage);
		}
	}
}
