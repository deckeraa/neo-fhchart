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
import java.awt.Color;
import java.awt.Font;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.io.FileOutputStream;
import java.io.PrintStream;

import javax.swing.BorderFactory;
import javax.swing.BoxLayout;
import javax.swing.DefaultListModel;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTabbedPane;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import org.fhaes.fhchart.chart.CompositeFilterOptions;
import org.fhaes.fhchart.chart.FHPlotCommon;
import org.fhaes.preferences.App;
import org.fhaes.preferences.FHAESPreferences.PrefKey;

public class CompositeFilterViewDlg extends JDialog implements ActionListener, ChangeListener {

	private static final long serialVersionUID = 1L;
	private FHPlotCommon fhxPlotCommon;
	private CompositeFilterOptions compositeAxisFilterOptions;

	@SuppressWarnings("unused")
	private int maxSeries;

	@SuppressWarnings("unused")
	private static final String leftParen = "(";
	@SuppressWarnings("unused")
	private static final String rightParen = ")";
	@SuppressWarnings("unused")
	private static final String minusSymbol = "-";

	@SuppressWarnings("unused")
	private int minVal;

	private boolean inputFHX2ListingInitialized = false;
	private boolean formatListingsInitialized = false;

	// State to be displayed
	private boolean includeOtherInjuriesState;
	private int minimumPercentScarred;
	private int minimumSampleDepth;
	private int minimumNumberSamplesScarred;
	private int[] seriesAnalyzedFHX2ColumnNos;
	private String[] seriesAnalyzedFHX2ColumnLabels;
	private String[] epochFormat;
	private String[] FHX2Format;

	// GUI Members
	// Main Panel
	private JPanel mainPanel;

	// TabbedPane placed in center of Main Panel
	private JTabbedPane tabbedPane;

	// Filter Settings Tab Content
	private JPanel filterSettingsBorderPanel;
	private JPanel filtersPanel;
	private JLabel FHX2Analyzed;
	private JLabel minimumPercentScarredLabel;
	private JLabel includeOtherInjuriesLabel;
	private JLabel minimumSampleDepthLabel;
	private JLabel minimumNumberSamplesLabel;
	private JLabel blankLabel;
	private JLabel blankLabel2;
	private JLabel blankLabel3;
	private JLabel blankLabel4;
	private JLabel seasonsAnalyzed;
	private JLabel countOfSeriesAnalyzedLabel;
	private JLabel seriesAnalyzedLabel;
	private JScrollPane seriesScrollPane;
	@SuppressWarnings("rawtypes")
	private JList seriesAnalyzedList;
	private String[] seriesAnalyzedListStrings;
	private JPanel exportFilterSettingsBtnPanel;
	private JButton exportFilterSettingsBtn;

	// Filter Results Tab Content
	private JLabel FHX2Analyzed2;
	@SuppressWarnings("unused")
	private JLabel yearsMeetFilterLabel;
	private JPanel filterResultsBorderPanel;
	private JPanel filterResultsBorderPanelNorth;
	private JPanel filterResultsPanel;
	private JPanel epochPanel;
	private JPanel FHX2Panel;
	private JLabel epochLabel;
	private JLabel FHX2Label;
	@SuppressWarnings("rawtypes")
	private JList epochFormatList;
	@SuppressWarnings("rawtypes")
	private DefaultListModel epochFormatListModel;
	@SuppressWarnings("rawtypes")
	private JList FHX2FormatList;
	@SuppressWarnings("rawtypes")
	private DefaultListModel FHX2FormatListModel;
	private JScrollPane epochPane;
	private JScrollPane FHX2Pane;
	private JPanel exportResultsBtnPanel;
	private JButton exportEpochFormatBtn;
	private JButton exportFHX2FormatBtn;

	// FHX2 Input Tab Content
	@SuppressWarnings("unused")
	private JPanel FHX2DataInputBorderPanel;
	private JPanel FHX2DataInputPanel;
	private JLabel FHX2DataInputLabel;
	private JScrollPane FHX2DataInputScrollPane;
	@SuppressWarnings("rawtypes")
	private JList FHX2DataInputList;
	@SuppressWarnings("rawtypes")
	private DefaultListModel FHX2DataInputListModel;
	private JPanel viewSeriesYearBtnPanel;
	private JButton viewSeriesBtn;
	private JButton viewYearBtn;

	// Button panel placed in South of Main Panel
	private JPanel buttonPanel;
	private JButton closeBtn;

	// Static strings
	private static final String TITLE = "View Composite Axis Filters and Results";
	private static final String FILTER_SETTINGS_TAB_TEXT = "Filter Settings";
	private static final String MINIMUM_PERCENT_SCARRED_LABEL = "Minimum Percent Scarred: ";
	private static final String INCLUDE_OTHER_INJURIES_LABEL = "Include Other Injuries: ";
	private static final String MINIMUM_SAMPLE_DEPTH_LABEL = "Minimum Sample Depth:";
	private static final String MINIMUM_NUMBER_SAMPLES_SCARRED_LABEL = "Minimum Number Samples/Series Scarred: ";
	private static final String NUMBER_OF_SERIES_SAMPLES_ANALYZED = "Number of Samples/Series Analyzed: ";
	private static final String SERIES_ANALYZED_LABEL = "Samples/Series Analyzed (FHX2 Column No., Series Name): ";
	private static final String YES_STRING = "Yes";
	private static final String NO_STRING = "No";
	@SuppressWarnings("unused")
	private static final String EXPORT = "Export";
	private static final String CLOSE = "Close";

	/**
	 * Constructor.
	 * 
	 * 
	 */

	@SuppressWarnings({ "rawtypes", "unchecked" })
	public CompositeFilterViewDlg(JFrame frame, boolean modal, FHPlotCommon fhxPlotCommonIn,
			CompositeFilterOptions compositeAxisFilterOptionsIn, boolean showFilterResultsPanel) {

		super(frame, TITLE, true);

		String injuryState;

		this.fhxPlotCommon = fhxPlotCommonIn;
		this.compositeAxisFilterOptions = compositeAxisFilterOptionsIn;

		// Get the current state to be displayed.

		// Create GUI Components
		// Main Panel
		mainPanel = new JPanel(new BorderLayout());

		// South area of Center Panel
		buttonPanel = new JPanel();
		closeBtn = new JButton(CLOSE);
		closeBtn.addActionListener(this);
		buttonPanel.add(closeBtn);
		mainPanel.add(buttonPanel, BorderLayout.SOUTH);

		// Center area of Center Panel - tabbed Pane
		tabbedPane = new JTabbedPane();
		tabbedPane.addChangeListener(this);
		mainPanel.add(tabbedPane, BorderLayout.CENTER);

		// Filter settings Tab
		filterSettingsBorderPanel = new JPanel(new BorderLayout());
		filtersPanel = new JPanel();
		filterSettingsBorderPanel.add(filtersPanel, BorderLayout.CENTER);
		filtersPanel.setLayout(new BoxLayout(filtersPanel, BoxLayout.Y_AXIS));
		filtersPanel.setBackground(Color.white);
		filtersPanel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
		blankLabel = new JLabel(" ");
		blankLabel2 = new JLabel(" ");
		blankLabel3 = new JLabel(" ");
		blankLabel4 = new JLabel(" ");

		FHX2Analyzed = new JLabel(new String("FHX2 Input Analyzed: " + fhxPlotCommon.getfhxPlotOptionsManager().gettitle()));
		filtersPanel.add(FHX2Analyzed);
		filtersPanel.add(blankLabel);

		this.minimumPercentScarred = compositeAxisFilterOptions.getminimumPercentScarred();
		minimumPercentScarredLabel = new JLabel(new String(MINIMUM_PERCENT_SCARRED_LABEL + this.minimumPercentScarred));
		filtersPanel.add(minimumPercentScarredLabel);

		this.includeOtherInjuriesState = compositeAxisFilterOptions.getincludeOtherInjuries();
		if (this.includeOtherInjuriesState)
		{
			injuryState = new String(YES_STRING);
		}
		else
		{
			injuryState = new String(NO_STRING);
		}
		includeOtherInjuriesLabel = new JLabel(new String(INCLUDE_OTHER_INJURIES_LABEL + injuryState));
		filtersPanel.add(includeOtherInjuriesLabel);
		this.minimumSampleDepth = compositeAxisFilterOptions.getminimumSampleDepth();
		minimumSampleDepthLabel = new JLabel(new String(MINIMUM_SAMPLE_DEPTH_LABEL + this.minimumSampleDepth));
		filtersPanel.add(minimumSampleDepthLabel);
		this.minimumNumberSamplesScarred = compositeAxisFilterOptions.getminimumNumberSamples();
		minimumNumberSamplesLabel = new JLabel(new String(MINIMUM_NUMBER_SAMPLES_SCARRED_LABEL + this.minimumNumberSamplesScarred));
		filtersPanel.add(minimumNumberSamplesLabel);
		filtersPanel.add(blankLabel2);

		seasonsAnalyzed = new JLabel("Seasons analyzed: D, E, M, L, A, U");
		filtersPanel.add(seasonsAnalyzed);
		filtersPanel.add(blankLabel3);

		this.seriesAnalyzedFHX2ColumnNos = this.fhxPlotCommon.getfhxPlotOptionsManager().getseriesPlottedOptions()
				.getseriesPlottedFHX2ColumnNo();
		countOfSeriesAnalyzedLabel = new JLabel(new String(NUMBER_OF_SERIES_SAMPLES_ANALYZED + this.seriesAnalyzedFHX2ColumnNos.length));
		filtersPanel.add(countOfSeriesAnalyzedLabel);
		this.seriesAnalyzedFHX2ColumnLabels = this.fhxPlotCommon.getfhxPlotDataManager().FHX2SeriesNamesOfColumnsBeingPlotted();

		seriesAnalyzedLabel = new JLabel(new String(SERIES_ANALYZED_LABEL));
		filtersPanel.add(seriesAnalyzedLabel);

		seriesAnalyzedListStrings = new String[this.seriesAnalyzedFHX2ColumnNos.length];
		for (int i = 0; i < this.seriesAnalyzedFHX2ColumnNos.length; i++)
		{
			seriesAnalyzedListStrings[i] = new String(seriesAnalyzedFHX2ColumnNos[i] + " " + seriesAnalyzedFHX2ColumnLabels[i]);
		}
		seriesAnalyzedList = new JList(seriesAnalyzedListStrings);
		seriesScrollPane = new JScrollPane(seriesAnalyzedList);
		filtersPanel.add(seriesScrollPane);

		exportFilterSettingsBtnPanel = new JPanel();
		exportFilterSettingsBtn = new JButton("Export Filter Settings");
		exportFilterSettingsBtn.setEnabled(false);
		exportFilterSettingsBtn.addActionListener(this);
		exportFilterSettingsBtnPanel.add(exportFilterSettingsBtn);
		filterSettingsBorderPanel.add(exportFilterSettingsBtnPanel, BorderLayout.SOUTH);

		// Filter Results Tab GUI
		filterResultsBorderPanel = new JPanel(new BorderLayout());
		filterResultsBorderPanelNorth = new JPanel();
		filterResultsBorderPanelNorth.setLayout(new BoxLayout(filterResultsBorderPanelNorth, BoxLayout.Y_AXIS));
		FHX2Analyzed2 = new JLabel(new String("FHX2 Input Analyzed: " + fhxPlotCommon.getfhxPlotOptionsManager().gettitle()));
		filterResultsBorderPanelNorth.add(FHX2Analyzed2);
		// yearsMeetFilterLabel = new JLabel(new String("Number of years that meet filters: " +
		// this.fhxPlotCommon.getfhxPlotDataManager().countOfYearsMeetFilters()));
		// filterResultsBorderPanelNorth.add(yearsMeetFilterLabel);
		filterResultsBorderPanelNorth.add(blankLabel4);
		filterResultsBorderPanel.add(filterResultsBorderPanelNorth, BorderLayout.NORTH);

		filterResultsPanel = new JPanel();
		filterResultsPanel.setLayout(new GridLayout(1, 2));
		filterResultsBorderPanel.add(filterResultsPanel, BorderLayout.CENTER);
		epochPanel = new JPanel();
		// epochPanel.setLayout(new BoxLayout(filtersPanel, BoxLayout.Y_AXIS));
		FHX2Panel = new JPanel();
		// FHX2Panel.setLayout(new BoxLayout(FHX2Panel, BoxLayout.Y_AXIS));
		epochLabel = new JLabel("Epoch Format:");
		epochPanel.add(epochLabel);
		FHX2Label = new JLabel("FHX2 Format:");
		FHX2Panel.add(FHX2Label);
		epochFormatListModel = new DefaultListModel();
		epochFormatList = new JList(epochFormatListModel);
		FHX2FormatListModel = new DefaultListModel();
		FHX2FormatList = new JList(FHX2FormatListModel);
		epochPane = new JScrollPane(epochFormatList);
		FHX2Pane = new JScrollPane(FHX2FormatList);
		epochPanel.add(epochPane);
		FHX2Panel.add(FHX2Pane);
		filterResultsPanel.add(epochPanel);
		filterResultsPanel.add(FHX2Panel);

		exportResultsBtnPanel = new JPanel(new GridLayout(1, 2));
		JPanel bp1 = new JPanel();
		exportEpochFormatBtn = new JButton("Export Epoch Format");
		exportEpochFormatBtn.addActionListener(this);
		bp1.add(exportEpochFormatBtn);
		JPanel bp2 = new JPanel(new GridLayout(2, 1));
		exportFHX2FormatBtn = new JButton("Export FHX2 Format");
		exportFHX2FormatBtn.setEnabled(false);
		exportFHX2FormatBtn.addActionListener(this);
		JButton appendBtn = new JButton("Append FHX2 Data to Existing File");
		appendBtn.addActionListener(this);
		appendBtn.setEnabled(false);
		// bp2.add(appendBtn);

		bp2.add(exportFHX2FormatBtn);

		exportResultsBtnPanel.add(bp1);
		exportResultsBtnPanel.add(bp2);
		filterResultsBorderPanel.add(exportResultsBtnPanel, BorderLayout.SOUTH);

		// FHX2 Input Tab
		FHX2DataInputPanel = new JPanel(new BorderLayout());
		FHX2DataInputLabel = new JLabel(new String("Content of " + fhxPlotCommon.getfhxPlotOptionsManager().gettitle()));
		FHX2DataInputPanel.add(FHX2DataInputLabel, BorderLayout.NORTH);
		FHX2DataInputListModel = new DefaultListModel();
		FHX2DataInputList = new JList(FHX2DataInputListModel);
		FHX2DataInputList.setFont(new Font("Courier", Font.PLAIN, 12));
		FHX2DataInputScrollPane = new JScrollPane(FHX2DataInputList);
		FHX2DataInputPanel.add(FHX2DataInputScrollPane, BorderLayout.CENTER);
		viewSeriesYearBtnPanel = new JPanel();
		viewSeriesBtn = new JButton("View a Series ...");
		viewSeriesBtn.setEnabled(false);
		viewYearBtn = new JButton("View a Year ...");
		viewYearBtn.setEnabled(false);
		viewSeriesBtn.addActionListener(this);
		viewYearBtn.addActionListener(this);
		viewSeriesYearBtnPanel.add(viewSeriesBtn);
		viewSeriesYearBtnPanel.add(viewYearBtn);
		FHX2DataInputPanel.add(viewSeriesYearBtnPanel, BorderLayout.SOUTH);

		tabbedPane.add(filterSettingsBorderPanel, FILTER_SETTINGS_TAB_TEXT);
		tabbedPane.add(filterResultsBorderPanel, "Filter Results");
		tabbedPane.add(FHX2DataInputPanel, "FHX2 Data Used As Input");
		if (showFilterResultsPanel == true)
		{
			tabbedPane.setSelectedIndex(1);
		}

		getContentPane().add(mainPanel);
		pack();
		setLocationRelativeTo(frame);
		setVisible(true);
	}

	public void actionPerformed(ActionEvent e) {

		if (closeBtn == e.getSource())
		{
			setVisible(false);
			// System.err.println("User chose OK.");
		}
		if (exportEpochFormatBtn == e.getSource())
		{
			exportEpochFormat();
		}
		// exportFHX2FormatBtn
		// exportFilterSettingsBtn
	}

	/**
	 * Export Results in Epoch Format
	 * 
	 */
	@SuppressWarnings("unused")
	public void exportEpochFormat() {

		// fhxPlotCommon.appendToDebug("Enter FHXJFreeChartMngr::exportPNG\n");

		JFileChooser fc;
		File file = null;
		int returnVal;
		boolean cont;
		String filename;
		String newPath;
		File newFile;
		String[] content;

		content = this.fhxPlotCommon.getfhxPlotDataManager().compositeAxisFilterQueryResultsInEpochFormat();

		cont = true;
		if (App.prefs.getPref(PrefKey.PREF_LAST_EXPORT_FOLDER, null) != null)
		{
			fc = new JFileChooser(App.prefs.getPref(PrefKey.PREF_LAST_EXPORT_FOLDER, null));
		}
		else
		{
			fc = new JFileChooser();
		}

		fc.setDialogTitle("Choose File for Export of Filter Results in Epoch Format");
		// fc.setFileFilter(new FHPNGFilter());
		returnVal = fc.showSaveDialog(fhxPlotCommon.getfhxPlotWin());
		if (returnVal == JFileChooser.APPROVE_OPTION)
		{
			file = fc.getSelectedFile();
			// fhxPlotCommon.appendToDebug("Saving: " + file.getName() + "\n");
			// fhxPlotCommon.appendToDebug("Saving: " + file.getPath() + "\n");
		}
		else
		{
			// fhxPlotCommon.appendToDebug("Save command cancelled by user." + "\n");
			cont = false;
		}

		if (cont == true)
		{
			// make sure file has an extension. If it does not assign .png
			filename = file.getName();
			// If file exists, ask if it should be overwritten
			if (file.exists())
			{
				int n = JOptionPane.showConfirmDialog(fhxPlotCommon.getfhxPlotWin(), "File: " + file.getName() + " already exists. "
						+ "Would you like to overwrite it?", "Overwrite file?", JOptionPane.YES_NO_OPTION);
				if (n == JOptionPane.YES_OPTION)
				{
					cont = true;
				}
				else if ((n == JOptionPane.NO_OPTION) || (n == JOptionPane.CLOSED_OPTION))
				{
					cont = false;
				}
				else
				{
					cont = false;
				}
			}
		}

		if (cont == true)
		{
			App.prefs.setPref(PrefKey.PREF_LAST_EXPORT_FOLDER, file.getParent());

			FileOutputStream out; // declare a file output object
			PrintStream p; // declare a print stream object
			try
			{
				out = new FileOutputStream(file); // Connect print stream to the output stream
				p = new PrintStream(out);
				for (int i = 0; i < content.length; i++)
				{
					p.println(content[i]);
				}
				p.close();
			}
			catch (Exception e)
			{
				System.err.println("Error Exporting to Epoch Format.");
			}
		}
	}

	@SuppressWarnings("unchecked")
	public void stateChanged(ChangeEvent e) {

		JTabbedPane pane = (JTabbedPane) e.getSource();

		// Get current tab
		int sel = pane.getSelectedIndex();
		if ((sel == 2) && (inputFHX2ListingInitialized == false))
		{
			String[] filelist = this.fhxPlotCommon.getfhxPlotDataManager().getFHX2InputFileContent();
			for (int i = 0; i < filelist.length; i++)
			{
				FHX2DataInputListModel.addElement(filelist[i]);
			}
			inputFHX2ListingInitialized = true;
		}
		if ((sel == 1) && (formatListingsInitialized == false))
		{
			this.epochFormat = this.fhxPlotCommon.getfhxPlotDataManager().compositeAxisFilterQueryResultsInEpochFormat();
			this.FHX2Format = this.fhxPlotCommon.getfhxPlotDataManager().compositeAxisFilterResultsInFHX2Format();
			for (int i = 0; i < this.epochFormat.length; i++)
			{
				epochFormatListModel.addElement(epochFormat[i]);
			}
			for (int i = 0; i < this.FHX2Format.length; i++)
			{
				FHX2FormatListModel.addElement(this.FHX2Format[i]);
			}
			formatListingsInitialized = true;
		}
	}
}
