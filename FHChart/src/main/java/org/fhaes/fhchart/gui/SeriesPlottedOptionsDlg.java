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
import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.StringTokenizer;

import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.DefaultListModel;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JList;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.ListSelectionModel;
import javax.swing.border.BevelBorder;
import javax.swing.border.TitledBorder;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
//import java.awt.ListSelectionListener;
//import java.awt.ListSelectionEvent;

import org.fhaes.fhchart.chart.FHPlotCommon;
import org.fhaes.fhchart.chart.SeriesPlottedOptions;

/**
 * FHX Fire History Set Series to be Plotted.
 * 
 * @version 1.0 31 Oct 2006
 * @author Wendy Gross
 */
public class SeriesPlottedOptionsDlg extends JDialog implements ActionListener, ListSelectionListener {

	private static final long serialVersionUID = 1L;

	private FHPlotCommon fhxPlotCommon;
	private SeriesPlottedOptions seriesPlottedOptions;

	/*
	 * char []leftParenChar = new char[1]; leftParenChar[0] = '('; String leftParen = new String(leftParenChar); char []rightParenChar = new
	 * char[1]; rightParenChar[0] = ')'; String rightParen = new String(rightParenChar); char []minusChar = new char[1]; minusChar[0] = '-';
	 * String minusSymbol = new String(minusChar);
	 */

	// GUI Members
	private JPanel mainPanel;
	private JPanel mainPanelCenter;

	// Plotted Series "Left" Panel
	private JPanel leftPanel;
	private BevelBorder borderLeft; // Series to be Plotted
	@SuppressWarnings("unused")
	private TitledBorder titledBorderLeft;
	private JPanel btnPanelLeft;
	private JButton moveUpLeftBtn;
	private JButton moveDownLeftBtn;
	private JScrollPane leftPane;
	@SuppressWarnings("rawtypes")
	private DefaultListModel listModelLeft;
	@SuppressWarnings("rawtypes")
	private JList leftList;

	// Buttons to move between lists "Middle" Panel
	private JPanel middlePanel;
	private JButton moveOneRightBtn;
	private JButton moveOneLeftBtn;
	private JButton moveAllRightBtn;
	private JButton moveAllLeftBtn;

	// Available Series "Right" Panel
	private JPanel rightPanel;
	private BevelBorder borderRight; // Series Available - Not Plotted
	@SuppressWarnings("unused")
	private TitledBorder titledBorderRight;
	private JPanel btnPanelRight;
	private JButton moveUpRightBtn;
	private JButton moveDownRightBtn;
	private JScrollPane rightPane;
	@SuppressWarnings("rawtypes")
	private DefaultListModel listModelRight;
	@SuppressWarnings("rawtypes")
	private JList rightList;

	// General Buttons
	private JPanel buttonPanel;
	private JButton okButton;
	private JButton resetButton;
	private JButton cancelButton;

	// private int[] seriesPlottedFHX2ColumnNo;
	// private int[] seriesAvailableFHX2ColumnNo;
	private int[] origSeriesPlottedFHX2ColumnNo;
	private int origSeriesPlottedSize;
	private int[] origSeriesAvailableFHX2ColumnNo;

	private String[] seriesPlottedStrings;
	private String[] seriesAvailableStrings;

	int totalSeriesCount;

	/**
	 * Constructor.
	 * 
	 * 
	 */

	// formatted text field demo JFormattedTextField

	@SuppressWarnings({ "rawtypes", "unchecked" })
	public SeriesPlottedOptionsDlg(JFrame frame, boolean modal, FHPlotCommon fhxPlotCommonIn, SeriesPlottedOptions seriesPlottedOptionsIn) {

		super(frame, "Select Series to be Plotted, and the Series Plot Order", modal);

		this.fhxPlotCommon = fhxPlotCommonIn;
		this.seriesPlottedOptions = seriesPlottedOptionsIn;
		this.totalSeriesCount = fhxPlotCommon.getfhxPlotDataManager().getnumSamples();

		// this.seriesPlottedFHX2ColumnNo = this.seriesPlottedOptions.getseriesPlottedFHX2ColumnNo();
		// this.seriesAvailableFHX2ColumnNo = this.seriesPlottedOptions.getseriesAvailableFHX2ColumnNo();
		this.origSeriesPlottedFHX2ColumnNo = this.seriesPlottedOptions.getseriesPlottedFHX2ColumnNo();
		this.origSeriesPlottedSize = 0;
		for (int i = 0; i < this.origSeriesPlottedFHX2ColumnNo.length; i++)
		{
			if (this.origSeriesPlottedFHX2ColumnNo[i] != -1)
			{
				this.origSeriesPlottedSize++;
			}
		}
		this.origSeriesAvailableFHX2ColumnNo = this.seriesPlottedOptions.getseriesAvailableFHX2ColumnNo();

		// create the GUI Components
		// Main Panel
		mainPanel = new JPanel();
		mainPanel.setLayout(new BorderLayout());
		mainPanelCenter = new JPanel();
		mainPanelCenter.setLayout(new BoxLayout(mainPanelCenter, BoxLayout.X_AXIS));
		mainPanel.add(mainPanelCenter, BorderLayout.CENTER);

		// Plotted Series "Left" Panel
		borderLeft = new BevelBorder(BevelBorder.LOWERED);

		leftPanel = new JPanel();
		leftPanel.setLayout(new BorderLayout());
		leftPanel.setBorder(new TitledBorder(borderLeft, "Series to be Plotted", TitledBorder.CENTER, TitledBorder.ABOVE_TOP));
		mainPanelCenter.add(leftPanel);
		btnPanelLeft = new JPanel();
		btnPanelLeft.setLayout(new BoxLayout(btnPanelLeft, BoxLayout.Y_AXIS));
		// btnPanelLeft.setAlignmentY(CENTER_ALIGNMENT);
		// btnPanelLeft.setAlignmentX(CENTER_ALIGNMENT);
		moveUpLeftBtn = new JButton("Move Up  ");
		moveUpLeftBtn.setAlignmentX(Component.CENTER_ALIGNMENT);
		moveUpLeftBtn.setAlignmentY(Component.CENTER_ALIGNMENT);

		moveDownLeftBtn = new JButton("Move Down");
		moveDownLeftBtn.setAlignmentX(Component.CENTER_ALIGNMENT);
		moveDownLeftBtn.setAlignmentY(Component.CENTER_ALIGNMENT);

		moveUpLeftBtn.addActionListener(this);
		moveDownLeftBtn.addActionListener(this);
		btnPanelLeft.add(Box.createVerticalGlue());
		btnPanelLeft.add(moveUpLeftBtn);
		btnPanelLeft.add(Box.createRigidArea(new Dimension(1, 4)));
		btnPanelLeft.add(moveDownLeftBtn);
		btnPanelLeft.add(Box.createVerticalGlue());

		leftPanel.add(btnPanelLeft, BorderLayout.WEST);
		listModelLeft = new DefaultListModel();
		leftList = new JList(listModelLeft);
		leftList.addListSelectionListener(this);
		leftList.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
		leftList.setPrototypeCellValue("XXXX XXXXXXXX XXXX-XXXX");
		leftPane = new JScrollPane(leftList);
		leftPanel.add(leftPane, BorderLayout.CENTER);

		// Buttons to move between lists "Middle" Panel
		middlePanel = new JPanel();
		middlePanel.setLayout(new BoxLayout(middlePanel, BoxLayout.Y_AXIS));
		middlePanel.setAlignmentY(CENTER_ALIGNMENT);
		middlePanel.setAlignmentX(CENTER_ALIGNMENT);

		moveOneRightBtn = new JButton(">");
		moveOneRightBtn.addActionListener(this);
		moveOneRightBtn.setAlignmentX(Component.CENTER_ALIGNMENT);
		moveOneRightBtn.setAlignmentY(Component.CENTER_ALIGNMENT);

		moveOneLeftBtn = new JButton("<");
		moveOneLeftBtn.addActionListener(this);
		moveOneLeftBtn.setAlignmentX(Component.CENTER_ALIGNMENT);
		moveOneLeftBtn.setAlignmentY(Component.CENTER_ALIGNMENT);

		moveAllRightBtn = new JButton(">>");
		moveAllRightBtn.addActionListener(this);
		moveAllRightBtn.setAlignmentX(Component.CENTER_ALIGNMENT);
		moveAllRightBtn.setAlignmentY(Component.CENTER_ALIGNMENT);

		moveAllLeftBtn = new JButton("<<");
		moveAllLeftBtn.addActionListener(this);
		moveAllLeftBtn.setAlignmentX(Component.CENTER_ALIGNMENT);
		moveAllLeftBtn.setAlignmentY(Component.CENTER_ALIGNMENT);

		middlePanel.add(Box.createVerticalGlue());
		middlePanel.add(moveOneRightBtn);
		middlePanel.add(Box.createRigidArea(new Dimension(1, 5)));
		middlePanel.add(moveOneLeftBtn);
		middlePanel.add(Box.createRigidArea(new Dimension(1, 8)));
		middlePanel.add(moveAllRightBtn);
		middlePanel.add(Box.createRigidArea(new Dimension(1, 5)));
		middlePanel.add(moveAllLeftBtn);
		middlePanel.add(Box.createVerticalGlue());

		mainPanelCenter.add(middlePanel);

		// Plotted Series "right" Panel
		borderRight = new BevelBorder(BevelBorder.LOWERED);

		rightPanel = new JPanel();
		rightPanel.setLayout(new BorderLayout());
		rightPanel.setBorder(new TitledBorder(borderRight, "Series Available - Not Plotted", TitledBorder.CENTER, TitledBorder.ABOVE_TOP));
		mainPanelCenter.add(rightPanel);
		btnPanelRight = new JPanel();
		btnPanelRight.setLayout(new BoxLayout(btnPanelRight, BoxLayout.Y_AXIS));
		// btnPanelRight.setAlignmentY(CENTER_ALIGNMENT);
		// btnPanelRight.setAlignmentX(CENTER_ALIGNMENT);
		moveUpRightBtn = new JButton("Move Up  ");
		moveUpRightBtn.setAlignmentX(Component.CENTER_ALIGNMENT);
		moveUpRightBtn.setAlignmentY(Component.CENTER_ALIGNMENT);

		moveDownRightBtn = new JButton("Move Down");
		moveDownRightBtn.setAlignmentX(Component.CENTER_ALIGNMENT);
		moveDownRightBtn.setAlignmentY(Component.CENTER_ALIGNMENT);

		moveUpRightBtn.addActionListener(this);
		moveDownRightBtn.addActionListener(this);
		btnPanelRight.add(Box.createVerticalGlue());
		btnPanelRight.add(moveUpRightBtn);
		btnPanelRight.add(Box.createRigidArea(new Dimension(1, 4)));
		btnPanelRight.add(moveDownRightBtn);
		btnPanelRight.add(Box.createVerticalGlue());

		rightPanel.add(btnPanelRight, BorderLayout.EAST);
		listModelRight = new DefaultListModel();
		rightList = new JList(listModelRight);
		rightList.addListSelectionListener(this);
		rightList.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
		rightList.setPrototypeCellValue("XXXX XXXXXXXX XXXX-XXXX");
		rightPane = new JScrollPane(rightList);
		rightPanel.add(rightPane, BorderLayout.CENTER);

		// Button Panel
		buttonPanel = new JPanel();
		okButton = new JButton("OK");
		resetButton = new JButton("Reset");
		cancelButton = new JButton("Cancel");
		okButton.addActionListener(this);
		resetButton.addActionListener(this);
		cancelButton.addActionListener(this);
		buttonPanel.add(okButton);
		buttonPanel.add(resetButton);
		buttonPanel.add(cancelButton);
		mainPanel.add(buttonPanel, BorderLayout.SOUTH);

		getContentPane().add(mainPanel);
		pack();
		setLocationRelativeTo(frame);
		initLists(this.origSeriesPlottedFHX2ColumnNo, this.origSeriesAvailableFHX2ColumnNo);
		if (this.seriesPlottedStrings != null)
		{
			leftList.setSelectedIndex(0);
		}

		if (this.seriesAvailableStrings != null)
		{
			rightList.setSelectedIndex(0);
		}
		setBounds(50, 50, 650, 550);
		enableButtons();
		setVisible(true);
	}

	@SuppressWarnings("unchecked")
	private boolean initLists(int[] columnNosPlotted, int[] columnNosAvailable) {

		// this.seriesPlottedFHX2ColumnNo and
		// this.seriesAvailableFHX2ColumnNo are set

		boolean rval = true;
		// clear the list boxes

		// populate the list boxes with
		int seriesPlottedCount = 0;
		int seriesAvailableCount = 0;
		int seriesColumnNo;
		String seriesName;
		int oldestRecordingYear;
		int mostRecentRecordingYear;

		// Clear out the lists
		listModelLeft.removeAllElements();
		listModelRight.removeAllElements();

		for (int i = 0; i < columnNosPlotted.length; i++)
		{
			if (columnNosPlotted[i] != -1)
			{
				seriesPlottedCount++;
			}
		}
		for (int i = 0; i < columnNosAvailable.length; i++)
		{
			if (columnNosAvailable[i] != -1)
			{
				seriesAvailableCount++;
			}
		}

		this.seriesPlottedStrings = null;
		if (seriesPlottedCount > 0)
		{
			this.seriesPlottedStrings = new String[seriesPlottedCount];
		}
		this.seriesAvailableStrings = null;
		if (seriesAvailableCount > 0)
		{
			this.seriesAvailableStrings = new String[seriesAvailableCount];
		}

		for (int i = 0; i < seriesPlottedCount; i++)
		{
			seriesColumnNo = columnNosPlotted[i];
			seriesName = fhxPlotCommon.getfhxPlotDataManager().getsampleName(seriesColumnNo - 1);
			oldestRecordingYear = fhxPlotCommon.getfhxPlotDataManager().getOldestRecordingYear(seriesColumnNo - 1);
			mostRecentRecordingYear = fhxPlotCommon.getfhxPlotDataManager().getMostRecentRecordingYear(seriesColumnNo - 1);
			seriesPlottedStrings[i] = new String(seriesColumnNo + " " + seriesName + " " + oldestRecordingYear + "-"
					+ mostRecentRecordingYear);
		}

		for (int i = 0; i < seriesAvailableCount; i++)
		{
			seriesColumnNo = columnNosAvailable[i];
			seriesName = fhxPlotCommon.getfhxPlotDataManager().getsampleName(seriesColumnNo - 1);
			oldestRecordingYear = fhxPlotCommon.getfhxPlotDataManager().getOldestRecordingYear(seriesColumnNo - 1);
			mostRecentRecordingYear = fhxPlotCommon.getfhxPlotDataManager().getMostRecentRecordingYear(seriesColumnNo - 1);
			seriesAvailableStrings[i] = new String(seriesColumnNo + " " + seriesName + " " + oldestRecordingYear + "-"
					+ mostRecentRecordingYear);
		}

		if (this.seriesPlottedStrings != null)
		{
			for (int i = 0; i < this.seriesPlottedStrings.length; i++)
			{
				// fhxPlotCommon.appendToDebug("SeriesPlottedString<" + i + ">" +
				// this.seriesPlottedStrings[i] + "\n");
				listModelLeft.addElement(this.seriesPlottedStrings[i]);
			}
		}

		if (this.seriesAvailableStrings != null)
		{
			for (int i = 0; i < this.seriesAvailableStrings.length; i++)
			{
				// fhxPlotCommon.appendToDebug("SeriesPlottedString<" + i + ">" +
				// this.seriesPlottedStrings[i] + "\n");
				listModelRight.addElement(this.seriesAvailableStrings[i]);
			}
		}
		else
		{
			// for (int i = 0; i < this.seriesPlottedStrings.length; i++) {
			// fhxPlotCommon.appendToDebug("SeriesPlottedString<" + i + ">" +
			// this.seriesPlottedStrings[i] + "\n");
			// listModelRight.addElement(this.seriesPlottedStrings[i]);
			// }
		}

		enableButtons();
		return rval;
	}

	@SuppressWarnings({ "unused", "unchecked" })
	public void actionPerformed(ActionEvent e) {

		int numSamples = fhxPlotCommon.getfhxPlotDataManager().getnumSamples();
		if (moveDownLeftBtn == e.getSource())
		{
			int size = 0;
			size = listModelLeft.size();
			int selectedIndex = leftList.getSelectedIndex();
			if (selectedIndex < size - 1)
			{
				Object obj = listModelLeft.elementAt(selectedIndex);
				listModelLeft.remove(selectedIndex);
				listModelLeft.add(selectedIndex + 1, obj);
				leftList.setSelectedIndex(selectedIndex + 1);
			}
			leftListEnsureSelectedIndexVisible();
		}

		if (moveUpLeftBtn == e.getSource())
		{
			int size = 0;
			size = listModelLeft.size();
			int selectedIndex = leftList.getSelectedIndex();
			if (selectedIndex > 0)
			{
				Object obj = listModelLeft.elementAt(selectedIndex);
				listModelLeft.remove(selectedIndex);
				listModelLeft.add(selectedIndex - 1, obj);
				leftList.setSelectedIndex(selectedIndex - 1);
			}
			leftListEnsureSelectedIndexVisible();
		}

		if (moveDownRightBtn == e.getSource())
		{
			int size = 0;
			size = listModelRight.size();
			int selectedIndex = rightList.getSelectedIndex();
			if (selectedIndex < size - 1)
			{
				Object obj = listModelRight.elementAt(selectedIndex);
				listModelRight.remove(selectedIndex);
				listModelRight.add(selectedIndex + 1, obj);
				rightList.setSelectedIndex(selectedIndex + 1);
			}
			rightListEnsureSelectedIndexVisible();
		}

		if (moveUpRightBtn == e.getSource())
		{
			int size = 0;
			size = listModelRight.size();
			int selectedIndex = rightList.getSelectedIndex();
			if (selectedIndex > 0)
			{
				Object obj = listModelRight.elementAt(selectedIndex);
				listModelRight.remove(selectedIndex);
				listModelRight.add(selectedIndex - 1, obj);
				rightList.setSelectedIndex(selectedIndex - 1);
			}
			rightListEnsureSelectedIndexVisible();
		}

		if (moveOneRightBtn == e.getSource())
		{
			int size = listModelLeft.size();
			if (size > 0)
			{
				int selectedIndex = leftList.getSelectedIndex();
				Object obj = listModelLeft.elementAt(selectedIndex);
				listModelLeft.remove(selectedIndex);
				listModelRight.addElement(obj);
				int newSize = listModelLeft.size();
				if (selectedIndex < newSize)
				{
					leftList.setSelectedIndex(selectedIndex);
				}
				else
				{
					leftList.setSelectedIndex(newSize - 1);
				}

				int rightSize = listModelRight.size();
				if (rightSize > 0)
				{
					rightList.setSelectedIndex(rightSize - 1);
				}
			}
			rightListEnsureSelectedIndexVisible();
		}

		if (moveOneLeftBtn == e.getSource())
		{
			int size = listModelRight.size();
			if (size > 0)
			{
				int selectedIndex = rightList.getSelectedIndex();
				Object obj = listModelRight.elementAt(selectedIndex);
				listModelRight.remove(selectedIndex);
				listModelLeft.addElement(obj);
				int newSize = listModelRight.size();
				if (selectedIndex < newSize)
				{
					rightList.setSelectedIndex(selectedIndex);
				}
				else
				{
					rightList.setSelectedIndex(newSize - 1);
				}

				int leftSize = listModelLeft.size();
				if (leftSize > 0)
				{
					leftList.setSelectedIndex(leftSize - 1);
				}
			}
			leftListEnsureSelectedIndexVisible();
		}

		if (moveAllRightBtn == e.getSource())
		{
			int size = listModelLeft.size();

			int rightOrigSize = listModelRight.size();
			int rightOrigSelectedIndex = rightList.getSelectedIndex();

			if (size > 0)
			{
				for (int i = 0; i < size; i++)
				{
					leftList.setSelectedIndex(0);

					// move the object at index 0 to the right list
					int size1 = listModelLeft.size();
					if (size1 > 0)
					{
						int selectedIndex = leftList.getSelectedIndex();
						Object obj = listModelLeft.elementAt(selectedIndex);
						listModelLeft.remove(selectedIndex);
						listModelRight.addElement(obj);
						int newSize = listModelLeft.size();
						if (selectedIndex < newSize)
						{
							leftList.setSelectedIndex(selectedIndex);
						}
						else
						{
							leftList.setSelectedIndex(newSize - 1);
						}
					}
				}

				rightList.setSelectedIndex(0);
				if ((rightOrigSize > 0))
				{
					if (rightOrigSelectedIndex > -1)
					{
						rightList.setSelectedIndex(rightOrigSelectedIndex);
					}
				}

				rightListEnsureSelectedIndexVisible();
				leftList.setSelectedIndex(-1);
			}
		}

		if (moveAllLeftBtn == e.getSource())
		{
			int size1 = listModelRight.size();

			int leftOrigSize = listModelLeft.size();
			int leftOrigSelectedIndex = leftList.getSelectedIndex();

			if (size1 > 0)
			{
				for (int i = 0; i < size1; i++)
				{
					rightList.setSelectedIndex(0);

					// move the object at index 0 to the left list
					int size = listModelRight.size();
					if (size > 0)
					{
						int selectedIndex = rightList.getSelectedIndex();
						Object obj = listModelRight.elementAt(selectedIndex);
						listModelRight.remove(selectedIndex);
						listModelLeft.addElement(obj);
						int newSize = listModelRight.size();
						if (selectedIndex < newSize)
						{
							rightList.setSelectedIndex(selectedIndex);
						}
						else
						{
							rightList.setSelectedIndex(newSize - 1);
						}
					}
				}

				leftList.setSelectedIndex(0);
				if ((leftOrigSize > 0))
				{
					if (leftOrigSelectedIndex > -1)
					{
						leftList.setSelectedIndex(leftOrigSelectedIndex);
					}
				}

				leftListEnsureSelectedIndexVisible();
				rightList.setSelectedIndex(-1);
			}
		}

		if (okButton == e.getSource())
		{

			int[] seriesColumnNosPlotted;
			int[] seriesColumnNosAvailable;
			Object obj;
			StringTokenizer st;
			String tmpString;
			boolean hasLeftListChanged = false;

			// make sure there is atleast one series to plot!
			int leftSize = listModelLeft.size();
			int rightSize = listModelRight.size();

			if (leftSize <= 0)
			{
				String strng = new String("Error: You must select at least ONE Series to plot");
				JOptionPane.showMessageDialog(null, strng, "Alert", JOptionPane.ERROR_MESSAGE);
			}
			else
			{ // there is at least one series to plot
				seriesColumnNosPlotted = new int[this.totalSeriesCount];
				seriesColumnNosAvailable = new int[this.totalSeriesCount];

				// seriesPlottedFHX2ColumnNo
				for (int i = 0; i < this.totalSeriesCount; i++)
				{
					seriesColumnNosPlotted[i] = -1;
					seriesColumnNosAvailable[i] = -1;
				}

				for (int i = 0; i < leftSize; i++)
				{
					obj = listModelLeft.elementAt(i);
					st = new StringTokenizer((String) obj);
					tmpString = st.nextToken();
					seriesColumnNosPlotted[i] = Integer.parseInt(tmpString);
				}

				// has left list changed?
				if (this.origSeriesPlottedSize != leftSize)
				{
					hasLeftListChanged = true;
				}
				else
				{
					for (int i = 0; (i < seriesColumnNosPlotted.length) && (i < origSeriesPlottedFHX2ColumnNo.length); i++)
					{
						if (seriesColumnNosPlotted[i] != origSeriesPlottedFHX2ColumnNo[i])
						{
							hasLeftListChanged = true;
							break;
						}
					}
				}

				for (int i = 0; i < rightSize; i++)
				{
					obj = listModelRight.elementAt(i);
					st = new StringTokenizer((String) obj);
					tmpString = st.nextToken();
					seriesColumnNosAvailable[i] = Integer.parseInt(tmpString);
				}

				this.seriesPlottedOptions.setseriesPlottedFHX2ColumnNo(seriesColumnNosPlotted);
				this.seriesPlottedOptions.setseriesAvailableFHX2ColumnNo(seriesColumnNosAvailable);
				if (hasLeftListChanged == true)
				{
					setVisible(false);
					this.fhxPlotCommon.getfhxPlotDataManager().clearCompositeAxisFilterResultSet();
					fhxPlotCommon.setprogressWindowTitle("Please wait. Plot series order and display options are being applied ...");
					fhxPlotCommon.setprogressWindowSubject("Progress:");
					this.fhxPlotCommon.getfhxPlotWin().updateChartPanel(null);
				}
				else
				{
					setVisible(false);
				}

			}
		}
		else if (cancelButton == e.getSource())
		{
			// System.err.println("User chose Cancel.");
			setVisible(false);
		}
		else if (resetButton == e.getSource())
		{
			initLists(this.seriesPlottedOptions.getseriesPlottedFHX2ColumnNoReset(),
					this.seriesPlottedOptions.getseriesAvailableFHX2ColumnNoReset());

			if (this.seriesPlottedStrings != null)
			{
				leftList.setSelectedIndex(0);
			}

			if (this.seriesAvailableStrings != null)
			{
				rightList.setSelectedIndex(0);
			}
			leftListEnsureSelectedIndexVisible();
			rightListEnsureSelectedIndexVisible();

		}
		enableButtons();
	}

	private void leftListEnsureSelectedIndexVisible() {

		int selectedIndex = leftList.getSelectedIndex();
		if (selectedIndex != -1)
		{
			leftList.ensureIndexIsVisible(selectedIndex);
		}
		enableButtons();
	}

	private void rightListEnsureSelectedIndexVisible() {

		int selectedIndex = rightList.getSelectedIndex();
		if (selectedIndex != -1)
		{
			rightList.ensureIndexIsVisible(selectedIndex);
		}
		enableButtons();
	}

	private void enableButtons() {

		int leftSize = listModelLeft.size();
		int rightSize = listModelRight.size();
		int leftSelectedIndex = leftList.getSelectedIndex();
		int rightSelectedIndex = rightList.getSelectedIndex();

		if (leftSize > 0)
		{
			moveOneRightBtn.setEnabled(true);
			moveAllRightBtn.setEnabled(true);
		}
		else
		{
			moveOneRightBtn.setEnabled(false);
			moveAllRightBtn.setEnabled(false);
		}
		if (rightSize > 0)
		{
			moveOneLeftBtn.setEnabled(true);
			moveAllLeftBtn.setEnabled(true);
		}
		else
		{
			moveOneLeftBtn.setEnabled(false);
			moveAllLeftBtn.setEnabled(false);
		}

		if (leftSelectedIndex > 0)
		{
			moveUpLeftBtn.setEnabled(true);
		}
		else
		{
			moveUpLeftBtn.setEnabled(false);
		}

		if (rightSelectedIndex > 0)
		{
			moveUpRightBtn.setEnabled(true);
		}
		else
		{
			moveUpRightBtn.setEnabled(false);
		}

		if (leftSelectedIndex < leftSize - 1)
		{
			moveDownLeftBtn.setEnabled(true);
		}
		else
		{
			moveDownLeftBtn.setEnabled(false);
		}

		if (rightSelectedIndex < rightSize - 1)
		{
			moveDownRightBtn.setEnabled(true);
		}
		else
		{
			moveDownRightBtn.setEnabled(false);
		}

		if (rightSelectedIndex == -1)
		{
			moveUpRightBtn.setEnabled(false);
			moveDownRightBtn.setEnabled(false);
			moveOneLeftBtn.setEnabled(false);
			moveAllLeftBtn.setEnabled(false);
		}

		if (leftSelectedIndex == -1)
		{
			moveUpLeftBtn.setEnabled(false);
			moveDownLeftBtn.setEnabled(false);
			moveOneRightBtn.setEnabled(false);
			moveAllRightBtn.setEnabled(false);
		}

	}

	public void valueChanged(ListSelectionEvent e) {

		enableButtons();
	}
}
