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
package org.fhaes.fhchart.gui;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.Cursor;
import java.awt.Desktop;
import java.awt.Image;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import java.awt.event.WindowEvent;
import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;
import java.io.File;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;

import javax.swing.ButtonGroup;
import javax.swing.ImageIcon;
import javax.swing.JCheckBoxMenuItem;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JToggleButton;
import javax.swing.JToolBar;
import javax.swing.SwingUtilities;
import javax.swing.UIManager;

import org.fhaes.components.JToolBarButton;
import org.fhaes.components.JToolBarToggleButton;
import org.fhaes.fhchart.chart.CompositeFilterOptions;
import org.fhaes.fhchart.chart.FHPlotCommon;
import org.fhaes.fhchart.chart.JFreeChartManager;
import org.fhaes.help.RemoteHelp;
import org.fhaes.preferences.App;
import org.fhaes.preferences.FHAESPreferences.PrefKey;
import org.fhaes.util.Builder;
import org.fhaes.util.ToggleableAction;
import org.jfree.chart.ChartPanel;
import org.jfree.chart.axis.NumberAxis;
import org.jfree.chart.axis.ValueAxis;
import org.jfree.chart.editor.ChartEditor;
import org.jfree.chart.editor.ChartEditorManager;
import org.jfree.chart.plot.CombinedDomainXYPlot;
import org.jfree.chart.plot.Plot;
import org.jfree.chart.plot.XYPlot;
import org.jfree.data.Range;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * FHX Fire History Plot Window.
 * 
 * @author Wendy Gross
 */
public class PlotWindow extends JFrame implements ActionListener, MouseListener, MouseMotionListener {

	public static final long serialVersionUID = 24362462L;
	private static final Logger log = LoggerFactory.getLogger(PlotWindow.class);

	private LegendFrame legendFrame = null;
	private FHPlotCommon fhPlotCommon;

	private Boolean isPanning = false;
	/** The starting point for panning. */
	private Point2D panStartPoint;

	private final JFrame parent;

	// Actions that this class is listening for...
	private static final String VIEW_COMPOSITE_FILTERS = "viewCompositeFilters";
	private static final String COMPOSITE = "Composite";

	private ToggleableAction fireIndexPlotAction;
	private ToggleableAction fireCompositePlotAction;
	private ToggleableAction fireChronologyPlotAction;

	// GUI controls
	// Menu components
	JMenuBar menuBar;
	JMenu fileMenu, editMenu, viewMenu, optionsMenu, helpMenu;
	JMenuItem fileMenuItemSaveOptionsToTemplateFile;
	JMenuItem fileMenuItemExportFilterResultsToEpochFile;
	JMenuItem fileMenuItemExportToPNGFile;
	JMenuItem fileMenuItemExportToPDFFile;
	JMenuItem fileMenuItemClose;
	JCheckBoxMenuItem viewMenuItemShowFireIndex;
	JCheckBoxMenuItem viewMenuItemShowFireChronology;
	JCheckBoxMenuItem viewMenuItemShowComposite;
	JMenuItem viewMenuItemShowLegend;
	JMenuItem viewMenuItemCompositeAxisFiltersAndResults;
	JMenuItem viewMenuItemZoomToFullExtent;
	JMenuItem viewMenuItemLegend;
	JMenuItem viewMenuItemCompostieAxisFilters;
	JMenuItem editMenuItemTitle;
	JMenuItem optionsMenuItemEventSymbolDisplayOptions;
	JMenuItem optionsMenuItemCompositeAxisDisplay;
	JMenuItem optionsMenuItemCompositeAxisFilters;
	JMenuItem optionsMenuItemSelectSeriesToBePlotted;
	JCheckBoxMenuItem optionsMenuItemShowSampleLabels;
	JMenuItem helpMenuItemHelp;
	JMenuItem helpMenuItemAbout;
	JToggleButton btnIndexPlot;
	JToggleButton btnChronologyPlot;
	JToggleButton btnCompositePlot;

	JPanel mainPanel = null; // Main panel container that contains all GUI components.
	ChartPanel chartPanel = null;
	JScrollPane scrollPane = null;

	// static final int DEFAULT_WINDOW_WIDTH = 700;
	// static final int DEFAULT_WINDOW_HEIGHT = 450;
	boolean firstChartPanelUpdate;
	// boolean inUpdateChartPanel;
	// boolean inAdjustWindowHeight;

	Image tmpImage;
	Image legendImage;

	/**
	 * Constructs a new Plot Window.
	 * 
	 * 
	 */
	@Deprecated
	public PlotWindow(File fileToBePlotedIn) {

		super(" ");
		parent = null;
		init(fileToBePlotedIn);
	}

	public JFrame getParentFrame() {

		return parent;
	}

	public PlotWindow(JFrame parent, File fileToBePlotedIn) {

		super(" ");
		this.parent = parent;
		init(fileToBePlotedIn);
	}

	private void setupActions() {

		fireCompositePlotAction = new ToggleableAction("Show composite plot", true, "firecompositeplot.png") {

			private static final long serialVersionUID = 1L;

			@Override
			public void togglePerformed(ActionEvent ae, Boolean value) {

				// Update chart
				fhPlotCommon.getfhxPlotOptionsManager().setdisplayCompositePlot(value);
				fhPlotCommon.getfhxPlotWin().updateChartPanel(null);

			}
		};

		fireIndexPlotAction = new ToggleableAction("Show fire index plot", true, "fireindexplot.png") {

			private static final long serialVersionUID = 1L;

			@Override
			public void togglePerformed(ActionEvent ae, Boolean value) {

				// Update chart
				fhPlotCommon.getfhxPlotOptionsManager().setdisplayFireIndexPlot(value);
				fhPlotCommon.getfhxPlotWin().updateChartPanel(null);

			}
		};

		fireChronologyPlotAction = new ToggleableAction("Show fire chronology plot", true, "firechronologyplot.png") {

			private static final long serialVersionUID = 1L;

			@Override
			public void togglePerformed(ActionEvent ae, Boolean value) {

				// Update chart
				fhPlotCommon.getfhxPlotOptionsManager().setdisplayFireChronologyPlot(value);
				fhPlotCommon.getfhxPlotWin().updateChartPanel(null);

			}
		};

	}

	private void init(File fileToBePlotedIn) {

		boolean autoExport = false;

		this.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);

		setupActions();

		JFileChooser fc;
		File file = null; // Input file
		this.firstChartPanelUpdate = true;

		this.chartPanel = null;

		if (fileToBePlotedIn != null)
		{
			// autoExport = true;
			// fhPlotCommon.appendToDebug("FHXPlotWindow::constructor file input is not NULL - autoexport is true\n");
		}
		else
		{
			// fhPlotCommon.appendToDebug("FHXPlotWindow::constructor file input NULL - autoexport is false\n");
		}
		file = fileToBePlotedIn;

		ImageIcon ic = Builder.getImageIcon("default_legend688x85.gif");
		legendImage = ic.getImage();

		// Set icon for window title bar
		tmpImage = Builder.getApplicationIcon();
		setIconImage(tmpImage);

		if (file == null)
		{
			// Open a FHX file

			String lastVisitedFolder = App.prefs.getPref(PrefKey.PREF_LAST_READ_FOLDER, null);

			if (lastVisitedFolder != null)
			{
				fc = new JFileChooser(lastVisitedFolder);
			}
			else
			{
				fc = new JFileChooser();
			}

			// Show it.
			fc.setDialogTitle("Open file");
			int returnVal = fc.showOpenDialog(parent);
			if (returnVal == JFileChooser.APPROVE_OPTION)
			{
				// fhPlotCommon.appendToDebug("File Chooser APPROVE_OPTION okay.\n");

				file = fc.getSelectedFile();
				// Set lastPathVisited
				App.prefs.setPref(PrefKey.PREF_LAST_READ_FOLDER, file.getParent());
			}
			else
			{
				// fhPlotCommon.appendToDebug("File Chooser NOT APPROVE_OPTION.\n");
				file = null;
				log.debug("About to close FHXPlotWindow");
				disposeWindow();
				return;
			}
		}

		if (file != null)
		{
			this.setTitle(file.getName() + " - FHX Chart");

			// Initialize fhDataManager, fhOptionsManager
			fhPlotCommon = new FHPlotCommon(this, autoExport);

			if (fhPlotCommon.initialize(file))
			{
				// fhPlotCommon.appendToDebug("FHXPlotCommon initialization succeeded.\n");
				// fhPlotCommon.getfhxPlotOptions().title = file.getName();
				// Ask user if they would like to use an initialization file
				// ...

				if (autoExport == true)
				{
					// Add filters for
					// minimum number samples scared: 2
					// sample depth: 5
					// Use defaults for other filters.
					CompositeFilterOptions options = fhPlotCommon.getfhxPlotOptionsManager().getcompositeAxisFilterOptions();
					options.setminimumSampleDepth(5);
					options.setminimumNumberSamples(2);
				}
			}
			else
			{
				// fhPlotCommon.appendToDebug("Error: FHXPlotCommon initialization failed.\n");
				// Inform user: Unable to initialize plot, check format of FHX file input.
				disposeWindow();
				return;
			}
		}

		// Create the GUI
		this.createMenus();

		this.mainPanel = new JPanel();
		this.mainPanel.setLayout(new BorderLayout());
		// this.mainPanel.setBackground(Color.white);
		this.setContentPane(mainPanel); // Every Application Frame must have ONE content pane.
		this.updateChartPanel(null); // Create the panel that contains the chart, and add to the CENTER of mainPanel.

		if (autoExport == true)
		{
			// fhPlotCommon.appendToDebug("About to call exportPng\n");
			fhPlotCommon.getfhxJFreeChartManager().exportPNG(autoExport);
			// fhPlotCommon.appendToDebug("About to call exportPDF\n");
			fhPlotCommon.getfhxJFreeChartManager().exportPDF(autoExport);
		}

		// fhPlotCommon.appendToDebug("FHXPlotWindow width<" + getWidth() + ">\n");
		// fhPlotCommon.appendToDebug("FHXPlotWindow Height<" + getHeight() + ">\n");
		// fhPlotCommon.appendToDebug("FHXPlotWindow::chartPanel width<" + chartPanel.getWidth() + ">\n");
		// fhPlotCommon.appendToDebug("FHXPlotWindow::chartPanel Height<" + chartPanel.getHeight() + ">\n");
		// fhPlotCommon.appendToDebug("FHXPlotWindow::scrollPane width<" + scrollPane.getWidth() + ">\n");
		// fhPlotCommon.appendToDebug("FHXPlotWindow::scrollPane Height<" + scrollPane.getHeight() + ">\n");

		createToolbar();

		/*
		 * Pack resizes the Window to the minimum size to satisfy the preferred size of each of the components in the layout, then computes
		 * the layout by calling validate. pack also ensures the peers of the Window and its parent are present by calling addNotify if
		 * necessary.
		 */
		this.pack();
		this.setLocationRelativeTo(parent);
		setExtendedState(getExtendedState() | JFrame.MAXIMIZED_BOTH);

		// this.setVisible(true);
	}

	private void createMenus() {

		// fhPlotCommon.appendToDebug("Enter Create Menus\n");

		// Create the menu bar.
		menuBar = new JMenuBar();
		fileMenu = new JMenu("File");
		editMenu = new JMenu("Edit");
		viewMenu = new JMenu("View");
		optionsMenu = new JMenu("Filter Options");
		optionsMenu.setIcon(Builder.getImageIcon("filter.png"));
		helpMenu = new JMenu("Help");
		menuBar.add(fileMenu);
		menuBar.add(editMenu);
		menuBar.add(viewMenu);
		// menuBar.add(optionsMenu);
		// menuBar.add(helpMenu);

		fileMenuItemSaveOptionsToTemplateFile = new JMenuItem("Save Options to Template file");
		fileMenuItemSaveOptionsToTemplateFile.setEnabled(false); // not implemented yet
		fileMenuItemSaveOptionsToTemplateFile.addActionListener(this);
		// fileMenu.add(fileMenuItemSaveOptionsToTemplateFile);

		fileMenuItemExportToPNGFile = new JMenuItem("Export Graph to PNG File");
		fileMenuItemExportToPNGFile.setIcon(Builder.getImageIcon("png.png"));
		fileMenuItemExportToPNGFile.setEnabled(true);
		fileMenuItemExportToPNGFile.setActionCommand("ExportPNG");
		fileMenuItemExportToPNGFile.addActionListener(this);
		fileMenu.add(fileMenuItemExportToPNGFile);

		fileMenuItemExportToPDFFile = new JMenuItem("Export Graph to PDF File");
		fileMenuItemExportToPDFFile.setIcon(Builder.getImageIcon("pdf.png"));
		fileMenuItemExportToPDFFile.setEnabled(true);
		fileMenuItemExportToPDFFile.setActionCommand("ExportPDF");
		fileMenuItemExportToPDFFile.addActionListener(this);
		fileMenu.add(fileMenuItemExportToPDFFile);

		fileMenuItemExportFilterResultsToEpochFile = new JMenuItem("Export Filter Results to File in Epoch Format");
		fileMenuItemExportFilterResultsToEpochFile.setIcon(Builder.getImageIcon("blank.png"));
		fileMenuItemExportFilterResultsToEpochFile.setEnabled(true);
		fileMenuItemExportFilterResultsToEpochFile.addActionListener(this);
		fileMenu.add(fileMenuItemExportFilterResultsToEpochFile);

		fileMenu.addSeparator();

		fileMenuItemClose = new JMenuItem("Close");
		fileMenuItemClose.setIcon(Builder.getImageIcon("close.png"));
		fileMenuItemClose.setEnabled(true);
		fileMenuItemClose.setActionCommand("Close");
		fileMenuItemClose.addActionListener(this);
		fileMenu.add(fileMenuItemClose);

		viewMenuItemShowComposite = new JCheckBoxMenuItem(this.fireCompositePlotAction);
		fireCompositePlotAction.connectToggleableButton(viewMenuItemShowComposite, true);
		viewMenu.add(viewMenuItemShowComposite);

		viewMenuItemShowFireIndex = new JCheckBoxMenuItem(this.fireIndexPlotAction);
		fireIndexPlotAction.connectToggleableButton(viewMenuItemShowFireIndex, true);
		viewMenu.add(viewMenuItemShowFireIndex);

		viewMenuItemShowFireChronology = new JCheckBoxMenuItem(this.fireChronologyPlotAction);
		fireChronologyPlotAction.connectToggleableButton(viewMenuItemShowFireChronology, true);
		viewMenu.add(viewMenuItemShowFireChronology);

		viewMenu.addSeparator();

		viewMenuItemCompositeAxisFiltersAndResults = new JMenuItem("Composite Axis Filters and Results ...");
		viewMenuItemCompositeAxisFiltersAndResults.setEnabled(true);
		viewMenuItemCompositeAxisFiltersAndResults.addActionListener(this);
		viewMenu.add(viewMenuItemCompositeAxisFiltersAndResults);

		viewMenu.addSeparator();

		viewMenuItemZoomToFullExtent = new JMenuItem("Zoom to Full Extent");
		viewMenuItemZoomToFullExtent.setIcon(Builder.getImageIcon("zoomfull.png"));
		viewMenuItemZoomToFullExtent.setEnabled(true);
		viewMenuItemZoomToFullExtent.setActionCommand("ZoomToFullExtent");
		viewMenuItemZoomToFullExtent.addActionListener(this);
		viewMenu.add(viewMenuItemZoomToFullExtent);

		viewMenu.addSeparator();

		viewMenuItemLegend = new JMenuItem("Legend");
		viewMenuItemLegend.setIcon(Builder.getImageIcon("legend.png"));
		viewMenuItemLegend.setActionCommand("Legend");
		viewMenuItemLegend.setEnabled(true);
		viewMenuItemLegend.addActionListener(this);
		viewMenu.add(viewMenuItemLegend);

		editMenuItemTitle = new JMenuItem("Graph Title");
		editMenuItemTitle.setEnabled(false);
		editMenuItemTitle.addActionListener(this);
		// editMenu.add(editMenuItemTitle);
		editMenu.add(optionsMenu);

		JMenuItem editMenuProperites = new JMenuItem("Chart Properties...");
		editMenuProperites.setIcon(Builder.getImageIcon("properties.png"));
		editMenuProperites.setActionCommand("Properties");
		editMenuProperites.addActionListener(this);
		editMenu.addSeparator();
		editMenu.add(editMenuProperites);

		optionsMenuItemShowSampleLabels = new JCheckBoxMenuItem("Show Sample/Series Labels", true);
		optionsMenuItemShowSampleLabels.setEnabled(false);
		optionsMenuItemShowSampleLabels.addActionListener(this);
		// viewMenu.add(optionsMenuItemShowSampleLabels);

		optionsMenuItemEventSymbolDisplayOptions = new JMenuItem("Fire, Injury, Lifecycle, and Season Event Options ...");
		optionsMenuItemEventSymbolDisplayOptions.setEnabled(false);
		optionsMenuItemEventSymbolDisplayOptions.addActionListener(this);
		optionsMenu.add(optionsMenuItemEventSymbolDisplayOptions);

		optionsMenuItemCompositeAxisDisplay = new JMenuItem("Composite Axis Display Options ...");
		optionsMenuItemCompositeAxisDisplay.setEnabled(true);
		optionsMenuItemCompositeAxisDisplay.addActionListener(this);
		// optionsMenu.add(optionsMenuItemCompositeAxisDisplay);

		optionsMenuItemCompositeAxisFilters = new JMenuItem("Composite Axis Filters ...");
		optionsMenuItemCompositeAxisFilters.setEnabled(true);
		optionsMenuItemCompositeAxisFilters.addActionListener(this);
		optionsMenu.add(optionsMenuItemCompositeAxisFilters);

		optionsMenuItemSelectSeriesToBePlotted = new JMenuItem("Select Series/Samples to be Plotted ...");
		optionsMenuItemSelectSeriesToBePlotted.setEnabled(true);
		optionsMenuItemSelectSeriesToBePlotted.addActionListener(this);
		optionsMenu.add(optionsMenuItemSelectSeriesToBePlotted);

		helpMenuItemHelp = new JMenuItem("Help");
		helpMenuItemHelp.setIcon(Builder.getImageIcon("help.png"));
		helpMenuItemHelp.setEnabled(true);
		helpMenuItemHelp.setActionCommand("ShowHelp");
		helpMenuItemHelp.addActionListener(this);
		helpMenu.add(helpMenuItemHelp);
		helpMenu.addSeparator();

		helpMenuItemAbout = new JMenuItem("About FHAES");
		helpMenuItemAbout.setIcon(Builder.getImageIcon("info.png"));
		helpMenuItemAbout.setEnabled(true);
		helpMenuItemHelp.setActionCommand("ShowAbout");
		helpMenuItemAbout.addActionListener(this);
		helpMenu.add(helpMenuItemAbout);

		this.setJMenuBar(menuBar);
	}

	/*
	 * Update the geometric configuration of the chart. Call updateChartPanel() when the geometric configuration of the Chart changes, for
	 * example: initaial creation, labels show/hide, composite axis show/hide, range of series(samples) are reset, etc.
	 */

	public void updateChartPanel(Integer widthIn) {

		// Wendy todo 20080606
		widthIn = null;
		int windowWidth;
		/*
		 * //inUpdateChartPanel = true; if (widthIn != null) { //use the value passed in windowWidth = widthIn.intValue(); } else if
		 * (this.firstChartPanelUpdate == true) { // Window is not fully created yet. windowWidth = DEFAULT_WINDOW_WIDTH;
		 * this.firstChartPanelUpdate = false; } else { // Use the current width of the window windowWidth = this.getWidth(); //if
		 * (windowWidth < DEFAULT_WINDOW_WIDTH) { //windowWidth = DEFAULT_WINDOW_WIDTH; //} }
		 */
		windowWidth = this.getWidth();

		if (this.chartPanel == null)
		{
			// fhPlotCommon.appendToDebug("FHXPlotWindow::updateChartPanel panel is NULL");
			this.chartPanel = fhPlotCommon.createChartPanel(windowWidth, null);
			if (this.chartPanel != null)
			{
				this.scrollPane = new JScrollPane(chartPanel);
				this.mainPanel.add(scrollPane, BorderLayout.CENTER);
				this.validate();
				this.repaint();
			}
		}
		else
		{
			// Recreate the chartPanel preserving its zoom factor

			// fhPlotCommon.appendToDebug("FHXPlotWindow::updateChartPanel panel is NOT NULL");
			this.scrollPane.setVisible(false);
			if (this.scrollPane != null)
			{
				this.mainPanel.remove(scrollPane);
				this.scrollPane = null;
			}
			// Get the currentDomainRange of this.chartPanel
			Range currentDomainRange = null;
			JFreeChartManager jFreeChartManager = fhPlotCommon.getfhxJFreeChartManager();
			CombinedDomainXYPlot parentPlot = jFreeChartManager.parentPlot;
			NumberAxis domainAxis = (NumberAxis) parentPlot.getDomainAxis();
			currentDomainRange = domainAxis.getRange();

			/*
			 * FHXJFreeChartManager CombinedDomainXYPlot parentPlot this.parentPlot = new CombinedDomainXYPlot(numberAxis); NumberAxis
			 * domainAxis = (NumberAxis)parentPlot.getDomainAxis(); domainAxis.setRange(new Range(initialDomainRange.getLowerBound(),
			 * upperbound));
			 */

			this.chartPanel = fhPlotCommon.createChartPanel(windowWidth, currentDomainRange);
			if (this.chartPanel != null)
			{
				this.scrollPane = new JScrollPane(chartPanel);
				this.mainPanel.add(scrollPane, BorderLayout.CENTER);
			}
			/*
			 * Layout the components using validate(). validate uses the current size of the Window, in contrast to pack() which resizes the
			 * Window to the minimum size to satisfy the preferred size of each of the components in the layout, then computes the layout by
			 * calling validate.
			 */
			this.validate();
			this.repaint();

		}
		// inUpdateChartPanel = false;

	}

	public void disposeWindow() {

		// fhPlotCommon.appendToDebug("Enter FHXPlotWindow disposeWindow.\n");
		this.disposeLegendFrame();
		this.dispose();
		// fhPlotCommon.appendToDebug("Exit FHXPlotWindow disposeWindow.\n");

	}

	public void disposeLegendFrame() {

		if (legendFrame != null)
		{
			legendFrame.setVisible(false);
			legendFrame.disposeLegendFrame();
			legendFrame = null;
		}
	}

	/**
	 * Listens for the window closing. Overrides superclass.
	 * 
	 * @param event information about the window event.
	 */
	public void windowClosing(final WindowEvent event) {

		if (event.getWindow() == this)
		{
			disposeWindow();
		}
	}

	/*
	 * public String getlastPathVisited() { String rval = null; if (this.fhPlotMainFrame != null) { rval =
	 * this.fhPlotMainFrame.getlastPathVisited(); } return rval; } public void setlastPathVisited(String lastPathVisited) { if
	 * (this.fhPlotMainFrame != null) { this.fhPlotMainFrame.setlastPathVisited(lastPathVisited); } }
	 */

	public static void showHelp() {

		showWebpage(RemoteHelp.FHAES_HELP_HOME);
	}

	public static void showAbout() {

		showWebpage(RemoteHelp.FHAES_ABOUT);
	}

	private static void showWebpage(String uri) {

		try
		{
			URI url = new URI(uri);
			Desktop.getDesktop().browse(url);
		}
		catch (IOException e1)
		{
			e1.printStackTrace();
		}
		catch (URISyntaxException e1)
		{
			e1.printStackTrace();
		}
	}

	private void createToolbar() {

		JToolBar toolbar = new JToolBar();
		toolbar.setFloatable(false);
		toolbar.setBackground(UIManager.getColor("Panel.background"));
		toolbar.setOpaque(true);

		JToolBarButton btnExportPNG = new JToolBarButton("png.png", "Export as PNG image");
		btnExportPNG.setActionCommand("ExportPNG");
		btnExportPNG.addActionListener(this);
		toolbar.add(btnExportPNG);

		JToolBarButton btnExportPDF = new JToolBarButton("pdf.png", "Export as PDF");
		btnExportPDF.setActionCommand("ExportPDF");
		btnExportPDF.addActionListener(this);
		toolbar.add(btnExportPDF);

		ButtonGroup groupedButtons = new ButtonGroup();

		JToolBarButton btnZoomMode = new JToolBarButton("zoom.png", "Zoom mode");
		btnZoomMode.setActionCommand("ZoomMode");
		btnZoomMode.addActionListener(this);
		groupedButtons.add(btnZoomMode);
		btnZoomMode.setVisible(false);
		toolbar.add(btnZoomMode);

		JToolBarButton btnPanMode = new JToolBarButton("pan.png", "Pan mode");
		btnPanMode.setActionCommand("PanMode");
		btnPanMode.addActionListener(this);
		btnPanMode.setVisible(false);
		groupedButtons.add(btnPanMode);
		toolbar.add(btnPanMode);

		toolbar.addSeparator();

		JToolBarButton btnZoomFull = new JToolBarButton("zoomfull.png", "Zoom to full extent");
		btnZoomFull.setActionCommand("ZoomToFullExtent");
		btnZoomFull.addActionListener(this);
		toolbar.add(btnZoomFull);

		JToolBarButton btnZoomToWidth = new JToolBarButton("zoomtowidth.png", "Fit to window width");
		btnZoomToWidth.setActionCommand("ZoomToWidth");
		btnZoomToWidth.addActionListener(this);
		btnZoomToWidth.setVisible(false);
		toolbar.add(btnZoomToWidth);

		toolbar.addSeparator();

		JToolBarButton btnProperties = new JToolBarButton("properties.png", "Alter chart properties");
		btnProperties.setActionCommand("Properties");
		btnProperties.addActionListener(this);
		toolbar.add(btnProperties);

		JToolBarButton btnMap = new JToolBarButton("map.png", "Show location of site");
		btnMap.setActionCommand("Map");
		btnMap.addActionListener(this);
		// toolbar.add(btnMap); // Functionality moved to main FHAES application
		btnMap.setEnabled(fhPlotCommon.getfhxDataManager().hasLatLong());

		JToolBarButton btnLegend = new JToolBarButton("legend.png", "Show chart legend");
		btnLegend.setActionCommand("Legend");
		btnLegend.addActionListener(this);
		toolbar.add(btnLegend);

		toolbar.addSeparator();

		btnIndexPlot = new JToolBarToggleButton(fireIndexPlotAction, "Show/hide fire index plot");
		fireIndexPlotAction.connectToggleableButton(btnIndexPlot);
		toolbar.add(btnIndexPlot);

		btnChronologyPlot = new JToolBarToggleButton(fireChronologyPlotAction, "Show/hide fire chronology plot");
		fireChronologyPlotAction.connectToggleableButton(btnChronologyPlot);
		toolbar.add(btnChronologyPlot);

		btnCompositePlot = new JToolBarToggleButton(fireCompositePlotAction, "Show/hide fire composite plot");
		fireCompositePlotAction.connectToggleableButton(btnCompositePlot);
		toolbar.add(btnCompositePlot);

		mainPanel.add(toolbar, BorderLayout.NORTH);
		btnZoomMode.setSelected(true);
	}

	/**
	 * Event listener for Action Events. This class implements the ActionListener Interface. Therefore, this method of the ActionListener
	 * Interface must be implemented.
	 */
	@SuppressWarnings("unused")
	public void actionPerformed(ActionEvent e) {

		if (e.getActionCommand().equals("ShowHelp"))
		{
			showHelp();
		}
		else if (e.getActionCommand().equals("ShowAbout"))
		{
			showAbout();
		}
		else if (e.getActionCommand().equals("ZoomToFullExtent"))
		{
			fhPlotCommon.getfhxJFreeChartManager().zoomInitial();
		}
		else if (e.getActionCommand().equals("ZoomToWidth"))
		{
			refresh();
		}
		else if (e.getActionCommand().equals("ZoomMode"))
		{
			isPanning = false;
			setPanMode(false);
		}
		else if (e.getActionCommand().equals("PanMode"))
		{
			isPanning = true;
			setPanMode(true);
		}
		else if (e.getActionCommand().equals("Properties"))
		{

			ChartEditor editor = ChartEditorManager.getChartEditor(this.chartPanel.getChart());
			int result = JOptionPane.showConfirmDialog(this, editor, "Properties", JOptionPane.OK_CANCEL_OPTION, JOptionPane.PLAIN_MESSAGE);
			if (result == JOptionPane.OK_OPTION)
			{
				editor.updateChart(this.chartPanel.getChart());
			}
		}
		else if (e.getActionCommand().equals("ExportPNG"))
		{
			fhPlotCommon.getfhxJFreeChartManager().exportPNG(false);
		}
		else if (e.getActionCommand().equals("ExportPDF"))
		{
			fhPlotCommon.getfhxJFreeChartManager().exportPDF(false);
		}
		else if (e.getActionCommand().equals("Map"))
		{
			URI uri;
			try
			{

				// OpenStreetMap
				uri = new URI("http://www.openstreetmap.org/?mlat=" + fhPlotCommon.getfhxDataManager().getLatitude() + "&mlon="
						+ fhPlotCommon.getfhxDataManager().getLongitude() + "&zoom=8&layers=M");

				// Google maps
				// uri = new
// URI("https://maps.google.com/maps?q="+fhPlotCommon.getfhxDataManager().getLatitude()+",+"+fhPlotCommon.getfhxDataManager().getLongitude());

				// Open in browser
				Desktop.getDesktop().browse(uri);

			}
			catch (URISyntaxException e1)
			{
				log.error("Invalid URI");
				e1.printStackTrace();
			}
			catch (IOException e1)
			{
				e1.printStackTrace();
			}
		}
		else if (e.getActionCommand().equals("Legend"))
		{
			/*
			 * if (this.legendFrame != null) { disposeLegendFrame(); } this.legendFrame = new LegendFrame(this, "Legend");
			 * legendFrame.setLocationRelativeTo(this); legendFrame.setVisible(true);
			 */

			fhPlotCommon.getfhxJFreeChartManager().toggleLegendVisible();

		}
		else if (e.getActionCommand().equals("Close"))
		{
			if (legendFrame != null)
			{
				disposeLegendFrame();
			}

			this.dispose();
		}

		Component comp = null;
		comp = (Component) e.getSource();
		// fhxPlotCommon.appendToDebug("\nEnter FHXJFreeChartManager::actionPerformed<" + comp.getName() + ">\n");
		if (comp.getName() != null)
		{

			if (comp.getName().equalsIgnoreCase(VIEW_COMPOSITE_FILTERS))
			{
				fhPlotCommon.getfhxPlotOptionsManager().getcompositeAxisFilterOptions().displayViewDialog();
			}
			if (comp.getName().equalsIgnoreCase(COMPOSITE))
			{
				fhPlotCommon.getfhxPlotOptionsManager().getcompositeAxisDisplayOptions().displayOptionsDialog();
			}
		}

		if (fileMenuItemExportFilterResultsToEpochFile == e.getSource())
		{
			CompositeFilterViewDlg compositeFilterViewDlg = new CompositeFilterViewDlg(this, false, fhPlotCommon, this.fhPlotCommon
					.getfhxPlotOptionsManager().getCompositeAxisFilterOptions(), true);
		}

		if (viewMenuItemCompositeAxisFiltersAndResults == e.getSource())
		{
			fhPlotCommon.getfhxPlotOptionsManager().getcompositeAxisFilterOptions().displayViewDialog();
		}

		if (optionsMenuItemCompositeAxisDisplay == e.getSource())
		{
			fhPlotCommon.getfhxPlotOptionsManager().getcompositeAxisDisplayOptions().displayOptionsDialog();
		}

		if (optionsMenuItemCompositeAxisFilters == e.getSource())
		{
			fhPlotCommon.getfhxPlotOptionsManager().getcompositeAxisFilterOptions().displayOptionsDialog();
		}

		if (optionsMenuItemSelectSeriesToBePlotted == e.getSource())
		{
			fhPlotCommon.getfhxPlotOptionsManager().getseriesPlottedOptions().displayOptionsDialog();
		}

	}

	public void refresh() {

		updateChartPanel(null);
	}

	/**
	 * Sets the pan mode.
	 * 
	 * @param val a boolean.
	 */
	private void setPanMode(boolean val) {

		// this.chartPanel.setHorizontalZoom(!val);
		// chartPanel.setHorizontalAxisTrace(! val);
		// this.chartPanel.setVerticalZoom(!val);
		// chartPanel.setVerticalAxisTrace(! val);

		if (val)
		{
			this.chartPanel.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
		}
		else
		{
			this.chartPanel.setCursor(Cursor.getDefaultCursor());
		}
	}

	@SuppressWarnings("unused")
	public void mouseDragged(MouseEvent event) {

		try
		{
			if (this.panStartPoint != null)
			{
				Rectangle2D scaledDataArea = this.chartPanel.getScreenDataArea();

				this.panStartPoint = getPointInRectangle(this.panStartPoint.getX(), this.panStartPoint.getY(), scaledDataArea);
				Point2D panEndPoint = getPointInRectangle(event.getX(), event.getY(), scaledDataArea);

				// horizontal pan

				Plot plot = this.chartPanel.getChart().getPlot();
				if (plot instanceof XYPlot)
				{
					XYPlot hvp = (XYPlot) plot;
					ValueAxis xAxis = hvp.getDomainAxis();

					if (xAxis != null)
					{
						double translatedStartPoint = xAxis.java2DToValue((float) this.panStartPoint.getX(), scaledDataArea,
								hvp.getDomainAxisEdge());
						double translatedEndPoint = xAxis
								.java2DToValue((float) panEndPoint.getX(), scaledDataArea, hvp.getDomainAxisEdge());
						double dX = translatedStartPoint - translatedEndPoint;

						double oldMin = xAxis.getLowerBound();
						double newMin = oldMin + dX;

						double oldMax = xAxis.getUpperBound();
						double newMax = oldMax + dX;

						// do not pan out of range
						if (newMin >= hvp.getDataRange(xAxis).getLowerBound() && newMax <= hvp.getDataRange(xAxis).getUpperBound())
						{
							xAxis.setLowerBound(newMin);
							xAxis.setUpperBound(newMax);
						}
					}
				}

				// vertical pan (1. Y-Axis)

				if (plot instanceof XYPlot)
				{
					XYPlot vvp = (XYPlot) plot;
					ValueAxis yAxis = vvp.getRangeAxis();

					if (yAxis != null)
					{
						double translatedStartPoint = yAxis.java2DToValue((float) this.panStartPoint.getY(), scaledDataArea,
								vvp.getRangeAxisEdge());
						double translatedEndPoint = yAxis.java2DToValue((float) panEndPoint.getY(), scaledDataArea, vvp.getRangeAxisEdge());
						double dY = translatedStartPoint - translatedEndPoint;

						double oldMin = yAxis.getLowerBound();
						double newMin = oldMin + dY;

						double oldMax = yAxis.getUpperBound();
						double newMax = oldMax + dY;

						// do not pan out of range
						/*
						 * if (newMin >= this.primYMinMax[0] && newMax <= this.primYMinMax[1]) { yAxis.setLowerBound(newMin);
						 * yAxis.setUpperBound(newMax); }
						 */
					}
				}

				// vertical pan (2. Y-Axis)

				if (plot instanceof XYPlot)
				{
					XYPlot xyPlot = (XYPlot) plot;
					ValueAxis yAxis = xyPlot.getRangeAxis(0);

					if (yAxis != null)
					{
						double translatedStartPoint = yAxis.java2DToValue((float) this.panStartPoint.getY(), scaledDataArea,
								xyPlot.getRangeAxisEdge(0));
						double translatedEndPoint = yAxis.java2DToValue((float) panEndPoint.getY(), scaledDataArea,
								xyPlot.getRangeAxisEdge(0));
						double dY = translatedStartPoint - translatedEndPoint;

						double oldMin = yAxis.getLowerBound();
						double newMin = oldMin + dY;

						double oldMax = yAxis.getUpperBound();
						double newMax = oldMax + dY;

						/*
						 * if (newMin >= this.secondYMinMax[0] && newMax <= this.secondYMinMax[1]) { yAxis.setLowerBound(newMin);
						 * yAxis.setUpperBound(newMax); }
						 */
					}
				}

				// for the next time
				this.panStartPoint = panEndPoint;
			}
		}
		catch (Exception e)
		{
			e.printStackTrace();
		}
	}

	public void mouseClicked(MouseEvent arg0) {

	}

	public void mouseEntered(MouseEvent arg0) {

	}

	public void mouseExited(MouseEvent arg0) {

	}

	public void mousePressed(MouseEvent event) {

		try
		{
			if (isPanning || SwingUtilities.isRightMouseButton(event))
			{
				Rectangle2D dataArea = this.chartPanel.getScreenDataArea();
				Point2D point = event.getPoint();
				if (dataArea.contains(point))
				{
					setPanMode(true);
					this.panStartPoint = point;
				}
			}
		}
		catch (Exception e)
		{
			e.printStackTrace();
		}
	}

	public void mouseReleased(MouseEvent arg0) {

		try
		{
			this.panStartPoint = null; // stop panning
			if (isPanning)
			{
				setPanMode(false);
			}
		}
		catch (Exception e)
		{
			e.printStackTrace();
		}

	}

	public void mouseMoved(MouseEvent e) {

	}

	public static Point2D getPointInRectangle(double x, double y, final Rectangle2D area) {

		x = Math.max(area.getMinX(), Math.min(x, area.getMaxX()));
		y = Math.max(area.getMinY(), Math.min(y, area.getMaxY()));
		return new Point2D.Double(x, y);

	}

}
