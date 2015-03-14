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
import java.awt.Component;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.beans.PropertyVetoException;

import javax.swing.BorderFactory;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;

import org.fhaes.fhchart.chart.CompositeFilterOptions;
import org.fhaes.fhchart.chart.FHPlotCommon;
import org.fhaes.util.FHIntTextBean;

/**
 * FHX Fire History Plot Composite Axis Filter options.
 * 
 * @version 1.0 31 Oct 2006
 * @author Wendy Gross
 */
public class CompositeFilterOptionsDlg extends JDialog implements ActionListener, ItemListener {

	private static final long serialVersionUID = 1L;
	private FHPlotCommon fhxPlotCommon;
	private CompositeFilterOptions compositeAxisFilterOptions;
	private int maxSeries;

	private static final String leftParen = "(";
	private static final String rightParen = ")";
	private static final String minusSymbol = "-";

	private int minVal;

	/*
	 * char []leftParenChar = new char[1]; leftParenChar[0] = '('; String leftParen = new String(leftParenChar); char []rightParenChar = new
	 * char[1]; rightParenChar[0] = ')'; String rightParen = new String(rightParenChar); char []minusChar = new char[1]; minusChar[0] = '-';
	 * String minusSymbol = new String(minusChar);
	 */

	// Current state
	private boolean currentIncludeOtherInjuriesState;
	private int currentMinimumPercentScarred;
	private int currentMinimumSampleDepth;
	private int currentMinimumNumberSamplesScarred;

	private boolean origIncludeOtherInjuriesState;
	private int origMinimumPercentScarred;
	private int origMinimumSampleDepth;
	private int origMinimumNumberSamplesScarred;

	// GUI Members
	private JCheckBox includeOtherInjuriesCheckBox;
	private JLabel percentScarredListLabel;
	private String[] percentScarredStrings = { "0", "10", "20", "25", "33", "50", "75" };
	@SuppressWarnings("rawtypes")
	private JComboBox percentScarredList;
	@SuppressWarnings("unused")
	private JLabel minimumPercentScarred;
	private JLabel sampleDepthFieldLabel;
	private FHIntTextBean sampleDepthField;
	private JLabel minimumNumberSamplesFieldLabel;
	private FHIntTextBean minimumNumberSamplesField;
	private JPanel mainPanel;
	private JPanel otherInjuryPanel;
	private JPanel filterPanel;
	private JPanel buttonPanel;
	private JButton okButton;
	private JButton resetButton;
	private JButton cancelButton;

	private static final String CHECKBOX_TEXT = "Include Other Injuries";
	private static final String TITLE = "Set Composite Axis Filters";
	private static final String MINIMUM_SAMPLE_DEPTH_LABEL = "Minimum Sample Depth";
	private static final String MINIMUM_NUMBER_SAMPLES_LABEL = "Minimum Number Samples Scarred";

	/**
	 * Constructor.
	 * 
	 * 
	 */

	// formatted text field demo JFormattedTextField

	@SuppressWarnings({ "rawtypes", "unchecked" })
	public CompositeFilterOptionsDlg(JFrame frame, boolean modal, FHPlotCommon fhxPlotCommonIn,
			CompositeFilterOptions compositeAxisFilterOptionsIn, int maxSeriesIn) {

		super(frame, TITLE, modal);

		this.fhxPlotCommon = fhxPlotCommonIn;
		this.compositeAxisFilterOptions = compositeAxisFilterOptionsIn;
		this.maxSeries = maxSeriesIn;

		// Get the current state.
		this.currentMinimumPercentScarred = compositeAxisFilterOptions.getminimumPercentScarred();
		this.currentIncludeOtherInjuriesState = compositeAxisFilterOptions.getincludeOtherInjuries();
		this.currentMinimumSampleDepth = compositeAxisFilterOptions.getminimumSampleDepth();
		this.currentMinimumNumberSamplesScarred = compositeAxisFilterOptions.getminimumNumberSamples();

		this.origIncludeOtherInjuriesState = this.currentIncludeOtherInjuriesState;
		this.origMinimumPercentScarred = this.currentMinimumPercentScarred;
		this.origMinimumSampleDepth = this.currentMinimumSampleDepth;
		this.origMinimumNumberSamplesScarred = this.currentMinimumNumberSamplesScarred;

		// Initialize the minimum value for sampleDepth and minimumNumberSamples
		if (this.currentIncludeOtherInjuriesState)
		{
			minVal = 0;
		}
		else
		{
			minVal = 1;
		}

		// create the GUI Components
		// http://leepoint.net/notes-java/GUI/components/60check_boxes/30checkbox.html
		includeOtherInjuriesCheckBox = new JCheckBox(CHECKBOX_TEXT, this.currentIncludeOtherInjuriesState);
		includeOtherInjuriesCheckBox.addItemListener(this);
		percentScarredListLabel = new JLabel("Minimum Percent Scarred:");
		percentScarredList = new JComboBox(percentScarredStrings);
		percentScarredList.addActionListener(this);

		sampleDepthFieldLabel = new JLabel(new String(MINIMUM_SAMPLE_DEPTH_LABEL + " " + leftParen + minVal + minusSymbol + this.maxSeries
				+ rightParen + ":"));
		sampleDepthField = new FHIntTextBean();
		minimumNumberSamplesFieldLabel = new JLabel(new String(MINIMUM_NUMBER_SAMPLES_LABEL + " " + leftParen + minVal + minusSymbol
				+ this.maxSeries + rightParen + ":"));
		minimumNumberSamplesField = new FHIntTextBean();
		okButton = new JButton("OK");
		resetButton = new JButton("Reset");
		cancelButton = new JButton("Cancel");
		okButton.addActionListener(this);
		resetButton.addActionListener(this);
		cancelButton.addActionListener(this);
		mainPanel = new JPanel();
		otherInjuryPanel = new JPanel();
		filterPanel = new JPanel();
		buttonPanel = new JPanel();

		// Set the current state of GUI components.
		this.includeOtherInjuriesCheckBox.setSelected(this.currentIncludeOtherInjuriesState);
		this.percentScarredList.setSelectedItem(String.valueOf(this.currentMinimumPercentScarred));
		try
		{
			this.sampleDepthField.setValue((this.currentMinimumSampleDepth));
			this.minimumNumberSamplesField.setValue((this.currentMinimumNumberSamplesScarred));
		}
		catch (PropertyVetoException excep)
		{
		}

		// Layout the GUI components
		this.mainPanel.setLayout(new BorderLayout());
		percentScarredListLabel.setAlignmentX(Component.LEFT_ALIGNMENT);
		otherInjuryPanel.add(percentScarredListLabel);
		percentScarredList.setAlignmentX(Component.LEFT_ALIGNMENT);
		otherInjuryPanel.add(percentScarredList);
		otherInjuryPanel.add(includeOtherInjuriesCheckBox);
		mainPanel.add(otherInjuryPanel, BorderLayout.NORTH);
		buttonPanel.add(okButton);
		buttonPanel.add(resetButton);
		buttonPanel.add(cancelButton);
		mainPanel.add(buttonPanel, BorderLayout.SOUTH);
		filterPanel.setLayout(new BoxLayout(filterPanel, BoxLayout.Y_AXIS));
		filterPanel.setBackground(Color.white);
		filterPanel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
		sampleDepthFieldLabel.setAlignmentX(Component.LEFT_ALIGNMENT);
		filterPanel.add(sampleDepthFieldLabel);
		sampleDepthField.setAlignmentX(Component.LEFT_ALIGNMENT);
		filterPanel.add(sampleDepthField);
		minimumNumberSamplesFieldLabel.setAlignmentX(Component.LEFT_ALIGNMENT);
		filterPanel.add(minimumNumberSamplesFieldLabel);
		minimumNumberSamplesField.setAlignmentX(Component.LEFT_ALIGNMENT);
		filterPanel.add(minimumNumberSamplesField);
		mainPanel.add(filterPanel, BorderLayout.CENTER);

		getContentPane().add(mainPanel);
		pack();
		setLocationRelativeTo(frame);
		setVisible(true);
	}

	public void itemStateChanged(ItemEvent e) {

		if (includeOtherInjuriesCheckBox == e.getSource())
		{
			this.currentIncludeOtherInjuriesState = (e.getStateChange() == ItemEvent.SELECTED);
			if (this.currentIncludeOtherInjuriesState == true)
			{
				this.minVal = 0;
			}
			else
			{
				this.minVal = 1;
			}
			sampleDepthFieldLabel.setText(new String(MINIMUM_SAMPLE_DEPTH_LABEL + " " + leftParen + minVal + minusSymbol + this.maxSeries
					+ rightParen + ":"));
			minimumNumberSamplesFieldLabel.setText(new String(MINIMUM_NUMBER_SAMPLES_LABEL + " " + leftParen + minVal + minusSymbol
					+ this.maxSeries + rightParen + ":"));
		}
	}

	public void actionPerformed(ActionEvent e) {

		if (percentScarredList == e.getSource())
		{
			String percentString = (String) this.percentScarredList.getSelectedItem();
			this.currentMinimumPercentScarred = Integer.parseInt(percentString);
		}
		if (okButton == e.getSource())
		{

			try
			{
				// System.err.println("User chose OK.");

				// Make sure currentMinimumSampleDepth field is valid
				this.currentMinimumSampleDepth = this.sampleDepthField.getValue();
				if (this.validateCurrentMinimumSampleDepth() == true)
				{
					this.compositeAxisFilterOptions.setminimumSampleDepth(this.currentMinimumSampleDepth);
				}
				else
				{
					return;
				}

				// Make sure currentMinimumNumberSample is valid
				this.currentMinimumNumberSamplesScarred = this.minimumNumberSamplesField.getValue();
				if (this.validateCurrentMinimumNumberSamplesScarred() == true)
				{
					this.compositeAxisFilterOptions.setminimumNumberSamples(this.currentMinimumNumberSamplesScarred);
				}
				else
				{
					return;
				}

				this.compositeAxisFilterOptions.setincludeOtherInjuries(this.currentIncludeOtherInjuriesState);
				this.compositeAxisFilterOptions.setminimumPercentScarred(this.currentMinimumPercentScarred);
				this.fhxPlotCommon.getfhxPlotDataManager().clearCompositeAxisFilterResultSet();

				if ((this.origIncludeOtherInjuriesState != this.currentIncludeOtherInjuriesState)
						|| (this.origMinimumPercentScarred != this.currentMinimumPercentScarred)
						|| (this.origMinimumSampleDepth != this.currentMinimumSampleDepth)
						|| (this.origMinimumNumberSamplesScarred != this.currentMinimumNumberSamplesScarred))
				{
					setVisible(false);
					fhxPlotCommon.setprogressWindowTitle("Please wait. Composite Axis Filters are being applied to your plot ...");
					fhxPlotCommon.setprogressWindowSubject("Progress:");
					this.fhxPlotCommon.getfhxJFreeChartManager().updateCompositeAndFireIndexXYDatasets();
				}
				else
				{
					setVisible(false);
				}
			}
			catch (Exception ex)
			{
				ex.printStackTrace();
			}
			finally
			{

			}
		}
		else if (resetButton == e.getSource())
		{
			// System.err.println("User chose Reset.");
			this.includeOtherInjuriesCheckBox.setSelected(this.compositeAxisFilterOptions.getincludeOtherInjuriesReset());
			this.currentIncludeOtherInjuriesState = this.compositeAxisFilterOptions.getincludeOtherInjuriesReset();

			this.percentScarredList.setSelectedItem(String.valueOf(this.compositeAxisFilterOptions.getminimumPercentScarredReset()));
			this.currentMinimumPercentScarred = this.compositeAxisFilterOptions.getminimumPercentScarredReset();

			try
			{
				this.currentMinimumSampleDepth = this.compositeAxisFilterOptions.getminimumSampleDepthReset();
				this.sampleDepthField.setValue((this.currentMinimumSampleDepth));

				this.currentMinimumNumberSamplesScarred = this.compositeAxisFilterOptions.getminimumNumberSamplesReset();
				this.minimumNumberSamplesField.setValue((this.currentMinimumNumberSamplesScarred));
			}
			catch (PropertyVetoException excep)
			{
			}

		}
		else if (cancelButton == e.getSource())
		{
			// System.err.println("User chose Cancel.");
			setVisible(false);
		}
	}

	private boolean validateCurrentMinimumSampleDepth() {

		boolean rval = true;
		String strng;
		int countOfSamplesBeingAnalyzed = this.maxSeries;

		if (this.currentIncludeOtherInjuriesState == false)
		{
			if (currentMinimumSampleDepth < 1)
			{
				// value must be greater than zero -
				strng = "Error: Minimum Sample Depth must be greater than zero if Include Other Injuries is not selected.";
				JOptionPane.showMessageDialog(null, strng, "Error setting Minimum Sample Depth", JOptionPane.ERROR_MESSAGE);
				rval = false;
				return rval;
			}
		}

		if (this.currentMinimumSampleDepth > countOfSamplesBeingAnalyzed)
		{
			strng = "Error: Minimum Sample Depth must be less than or equal to " + countOfSamplesBeingAnalyzed
					+ ", the total number of series/samples being analyzed.";
			JOptionPane.showMessageDialog(null, strng, "Error setting Minimum Sample Depth", JOptionPane.ERROR_MESSAGE);
			rval = false;
		}

		return rval;
	}

	private boolean validateCurrentMinimumNumberSamplesScarred() {

		boolean rval = true;
		String strng;
		int countOfSamplesBeingAnalyzed = this.maxSeries;

		if (this.currentIncludeOtherInjuriesState == false)
		{
			if (this.currentMinimumNumberSamplesScarred < 1)
			{
				// value must be greater than zero -
				// Error: Minimum Number of Samples Scarred must be greater than zero if Include Other Injuries is not selected.
				strng = "Error: Minimum Number of Samples Scarred must be greater than zero if Include Other Injuries is not selected.";
				JOptionPane.showMessageDialog(null, strng, "Error setting Minimum Number of Samples Scarred", JOptionPane.ERROR_MESSAGE);
				rval = false;
				return rval;
			}
		}

		// Minimum Sample Depth must be less than or equal to total number of samples being analyzed
		// Error: Minimum Number of Samples Scarred must be less than or equal to x, the total number of series/samples being analyzed.
		if (this.currentMinimumNumberSamplesScarred > countOfSamplesBeingAnalyzed)
		{
			strng = "Error: Minimum Number of Samples Scarred must be less than or equal to " + countOfSamplesBeingAnalyzed
					+ ", the total number of series/samples being analyzed.";
			JOptionPane.showMessageDialog(null, strng, "Error setting Minimum Number of Samples Scarred", JOptionPane.ERROR_MESSAGE);
			rval = false;
		}

		return rval;
	}
}
