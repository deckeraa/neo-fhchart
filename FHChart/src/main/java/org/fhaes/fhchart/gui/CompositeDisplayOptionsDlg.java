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

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;

import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JPanel;

import org.fhaes.fhchart.chart.CompositeDisplayOptions;
import org.fhaes.fhchart.chart.FHPlotCommon;

/**
 * FHX Fire History Plot Composite Axis display options.
 * 
 * @version 1.0 31 Oct 2006
 * @author Wendy Gross
 */
public class CompositeDisplayOptionsDlg extends JDialog implements ActionListener, ItemListener {

	private static final long serialVersionUID = 1L;

	private FHPlotCommon fhxPlotCommon;
	CompositeDisplayOptions compositeAxisDisplayOptions;

	// Current state
	private boolean currentDisplayCompositeAxisState;

	// GUI Members
	JCheckBox displayCompositeAxisCheckBox;
	JPanel mainPanel;
	JButton okButton;
	JButton resetButton;
	JButton cancelButton;

	private static final String CHECKBOX_TEXT = "Display Composite Axis";
	private static final String TITLE = "Set Composite Axis Display Options";

	/**
	 * Constructor.
	 * 
	 * 
	 */

	public CompositeDisplayOptionsDlg(JFrame frame, boolean modal, FHPlotCommon fhxPlotCommonIn,
			CompositeDisplayOptions compositeAxisDisplayOptionsIn) {

		super(frame, TITLE, modal);
		this.fhxPlotCommon = fhxPlotCommonIn;
		this.compositeAxisDisplayOptions = compositeAxisDisplayOptionsIn;

		// Set the current state.
		this.currentDisplayCompositeAxisState = compositeAxisDisplayOptions.getdisplayCompositeAxis();

		// create the GUI
		// http://leepoint.net/notes-java/GUI/components/60check_boxes/30checkbox.html
		displayCompositeAxisCheckBox = new JCheckBox(CHECKBOX_TEXT, this.currentDisplayCompositeAxisState);
		displayCompositeAxisCheckBox.addItemListener(this);
		okButton = new JButton("OK");
		resetButton = new JButton("Reset");
		cancelButton = new JButton("Cancel");
		okButton.addActionListener(this);
		resetButton.addActionListener(this);
		cancelButton.addActionListener(this);

		mainPanel = new JPanel();
		getContentPane().add(mainPanel);
		mainPanel.add(displayCompositeAxisCheckBox);
		mainPanel.add(okButton);
		mainPanel.add(resetButton);
		mainPanel.add(cancelButton);
		pack();
		setLocationRelativeTo(frame);
		setVisible(true);
	}

	public void itemStateChanged(ItemEvent e) {

		if (displayCompositeAxisCheckBox == e.getSource())
		{
			this.currentDisplayCompositeAxisState = (e.getStateChange() == ItemEvent.SELECTED);
			// this.xxx.setSelected(!this.currentDisplayCompositeAxisState);
		}
	}

	public void actionPerformed(ActionEvent e) {

		if (okButton == e.getSource())
		{
			// System.err.println("User chose OK.");
			this.compositeAxisDisplayOptions.setdisplayCompositeAxis(this.currentDisplayCompositeAxisState);
			this.fhxPlotCommon.getfhxPlotWin().updateChartPanel(null);
			setVisible(false);
		}
		else if (resetButton == e.getSource())
		{
			// System.err.println("User chose Reset.");
			this.displayCompositeAxisCheckBox.setSelected(this.compositeAxisDisplayOptions.getdisplayCompositeAxisReset());
		}
		else if (cancelButton == e.getSource())
		{
			// System.err.println("User chose Cancel.");
			setVisible(false);
		}
	}
}
