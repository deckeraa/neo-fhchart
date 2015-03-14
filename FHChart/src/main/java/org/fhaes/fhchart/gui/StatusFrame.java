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
import java.awt.Frame;
import java.awt.Label;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.DefaultListModel;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.JScrollPane;

import org.fhaes.fhchart.chart.FHPlotCommon;

/**
 * FHX Fire History Plot Composite Axis Query Status window.
 * 
 * @author Wendy Gross
 */
public class StatusFrame extends Frame implements ActionListener {

	private static final long serialVersionUID = 1L;

	@SuppressWarnings("unused")
	private FHPlotCommon fhxPlotCommon;

	// GUI Members
	JPanel mainPanel;
	JPanel northPanel;
	JLabel northLabel;
	JScrollPane statusScrollPane;
	@SuppressWarnings("rawtypes")
	JList statusList;
	@SuppressWarnings("rawtypes")
	DefaultListModel statusListModel;
	JPanel buttonPanel;
	JButton closeBtn;

	JFrame parentPlotWindow;

	private Label subjectLabel;
	private Label progressLabel;

	/**
	 * Constructor.
	 */
	public StatusFrame(JFrame frame, FHPlotCommon fhxPlotCommonIn, String titleIn, String subjectIn) {

		super(titleIn);
		this.parentPlotWindow = frame;
		this.fhxPlotCommon = fhxPlotCommonIn;
		this.setLayout(new BorderLayout());

		progressLabel = new Label();
		subjectLabel = new Label();
		if (subjectIn != null)
		{
			updateSubjectMessage(subjectIn);
		}
		this.add(progressLabel, BorderLayout.CENTER);
		this.add(subjectLabel, BorderLayout.NORTH);
		this.pack();
		this.setBounds(150, 300, 550, 150);
		setLocationRelativeTo(parentPlotWindow);
		// this.setVisible(true);
		// this.setCursor(new Cursor(Cursor.WAIT_CURSOR));
	}

	public void actionPerformed(ActionEvent e) {

		if (closeBtn == e.getSource())
		{
			// setVisible(false);
		}
	}

	public void updateStatusMessage(String statusString) {

		this.progressLabel.setText(statusString);
	}

	public void updateSubjectMessage(String subjectString) {

		this.subjectLabel.setText(subjectString);
	}

	public void closeWindow() {

		setVisible(false);
		// this.setCursor(new Cursor(Cursor.DEFAULT_CURSOR));
		dispose();
	}
}
