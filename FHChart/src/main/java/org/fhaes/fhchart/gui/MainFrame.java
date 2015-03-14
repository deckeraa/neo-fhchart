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
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Graphics;
import java.awt.GridLayout;
import java.awt.Image;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.prefs.Preferences;

import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JPanel;
import javax.swing.UIManager;

import org.fhaes.fhchart.chart.FHPlotCommon;
import org.fhaes.fhchart.jfreechart.editor.FHAESChartEditorFactory;
import org.fhaes.filefilter.FHXFileFilter;
import org.fhaes.preferences.App;
import org.fhaes.preferences.FHAESPreferences.PrefKey;
import org.fhaes.util.Builder;
import org.jfree.chart.editor.ChartEditorManager;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

//import javax.jnlp.*; // not being used at this time

/**
 * Main Frame for Fire History Plot.
 */
public class MainFrame extends JFrame implements ActionListener, WindowListener {

	public static final long serialVersionUID = 24362462L;
	private static final Logger log = LoggerFactory.getLogger(MainFrame.class);

	public static final int MAX_PLOT_WINDOWS = 100;

	@SuppressWarnings("unused")
	private Preferences prefs = Preferences.userNodeForPackage(MainFrame.class);

	// GUI Members
	// Menu Bar
	JMenuBar menuBar;
	JMenu fileMenu, helpMenu;
	JMenuItem fileMenuItemCreateFHX2Plot;
	JMenuItem fileMenuItemAutoExport;
	JMenuItem fileMenuItemExit;
	JMenuItem helpMenuItemHelp;
	JMenuItem helpMenuItemAbout;

	JButton launchFireHistoryPlotBtn = null;
	String lastPathVisited;

	private PlotWindow[] plotWindows;

	/**
	 * FHPlotMainFrame constructor.
	 */
	@SuppressWarnings("unused")
	public MainFrame(String title) {

		super(title);

		if (!System.getProperty("os.name").startsWith("Mac"))
		{
			// For non-MacOSX systems set Nimbus as LnF
			try
			{
				UIManager.setLookAndFeel("com.sun.java.swing.plaf.nimbus.NimbusLookAndFeel");
			}
			catch (Exception e)
			{
				log.warn("Error setting Nimbus look at feel");
			}
		}
		else
		{
			// On MacOSX set standard GUI conventions...

			System.setProperty("apple.laf.useScreenMenuBar", "true");
			System.setProperty("com.apple.mrj.application.apple.menu.about.name", "Corina");
			System.setProperty("com.apple.macos.use-file-dialog-packages", "false"); // for AWT
			System.setProperty("com.apple.mrj.application.growbox.intrudes", "false");
			UIManager.put("JFileChooser.packageIsTraversable", "never"); // for swing
			// new MacOSMods();

		}

		this.setBackground(Color.white);
		plotWindows = new PlotWindow[MAX_PLOT_WINDOWS];

		lastPathVisited = null;

		final ImageIcon m_image = Builder.getImageIcon("logo_525x500.gif");
		final int winc = m_image.getIconWidth();
		final int hinc = m_image.getIconHeight();

		Image tmpImage = Builder.getApplicationIcon();
		setIconImage(tmpImage);

		JPanel content = new JPanel(new BorderLayout());
		content.setOpaque(true);
		content.setBackground(Color.white);

		// Define the User Interface
		JPanel nPanel = new JPanel();
		nPanel.setBackground(Color.white);
		nPanel.setLayout(new GridLayout(2, 1));

		JPanel northPanel = new JPanel();
		northPanel.setLayout(new FlowLayout(FlowLayout.CENTER));
		northPanel.setBackground(Color.white);

		JPanel l2 = new JPanel();
		l2.setLayout(new FlowLayout(FlowLayout.CENTER));
		l2.setBackground(Color.white);

		JPanel l3 = new JPanel();
		l3.setLayout(new FlowLayout(FlowLayout.CENTER));
		l3.setBackground(Color.white);

		JLabel label3 = new JLabel("FHChart Launcher");
		label3.setForeground(Color.gray);
		label3.setBackground(Color.white);
		l3.add(label3);
		// nPanel.add(l3);

		content.add(nPanel, BorderLayout.NORTH);

		launchFireHistoryPlotBtn = new JButton("Click Here to Create a Fire History Graph!");
		launchFireHistoryPlotBtn.addActionListener(this);
		// content.add(launchFireHistoryPlotBtn, BorderLayout.CENTER);

		JLabel FHAESLogoLabel = new JLabel("");
		if (m_image != null)
		{
			if (m_image.getIconWidth() > 0 && m_image.getIconHeight() > 0)
			{
				FHAESLogoLabel = new JLabel() {

					public static final long serialVersionUID = 24362462L;

					public void paintComponent(Graphics g) {

						m_image.paintIcon(this, g, 0, 0);
					}

					public Dimension getPreferredSize() {

						return new Dimension(super.getSize());
					}

					public Dimension getMinimumSize() {

						return getPreferredSize();
					}
				};
			}
			FHAESLogoLabel.setBounds(0, 0, 560, 500);
			JPanel centerPanel = new JPanel();
			centerPanel.setBackground(Color.white);

			centerPanel.setLayout(new FlowLayout(FlowLayout.CENTER));
			centerPanel.add(FHAESLogoLabel);
			content.add(centerPanel, BorderLayout.CENTER);
		}

		JLabel partnersLogoLabel = new JLabel("");
		if (m_image != null)
		{
			if (m_image.getIconWidth() > 0 && m_image.getIconHeight() > 0)
			{
				partnersLogoLabel = new JLabel() {

					public static final long serialVersionUID = 24362462L;

					public void paintComponent(Graphics g) {

						m_image.paintIcon(this, g, 0, 0);
					}

					public Dimension getPreferredSize() {

						return new Dimension(super.getSize());
					}

					public Dimension getMinimumSize() {

						return getPreferredSize();
					}
				};
			}
			partnersLogoLabel.setBounds(0, 0, 525, 500);
			JPanel southPanel = new JPanel();
			southPanel.setLayout(new FlowLayout(FlowLayout.CENTER));
			southPanel.setBackground(Color.white);

			// southPanel.add(partnersLogoLabel);
			content.add(southPanel, BorderLayout.SOUTH);
		}

		getContentPane().setLayout(new FlowLayout());
		getLayeredPane().setBackground(Color.white);
		getContentPane().add(content);
		((JPanel) getContentPane()).setOpaque(false);

		createMenus();

		addWindowListener(this);
		pack();
		setLocationRelativeTo(null);
		this.setSize(new Dimension(570, 480));

		setVisible(true);

	}

	private void createMenus() {

		// Create the menu bar.
		menuBar = new JMenuBar();
		fileMenu = new JMenu("File");
		helpMenu = new JMenu("Help");
		menuBar.add(fileMenu);
		menuBar.add(helpMenu);

		fileMenuItemCreateFHX2Plot = new JMenuItem("Open file...");
		fileMenuItemCreateFHX2Plot.setIcon(Builder.getImageIcon("fileopen.png"));
		fileMenuItemCreateFHX2Plot.setEnabled(true);
		fileMenuItemCreateFHX2Plot.addActionListener(this);
		fileMenu.add(fileMenuItemCreateFHX2Plot);

		fileMenuItemAutoExport = new JMenuItem("Auto Export");
		fileMenuItemAutoExport.setEnabled(true); // not implemented yet
		fileMenuItemAutoExport.addActionListener(this);
		// AutoExport Code
		// fileMenu.add(fileMenuItemAutoExport);

		fileMenu.addSeparator();

		fileMenuItemExit = new JMenuItem("Exit");
		fileMenuItemExit.setIcon(Builder.getImageIcon("close.png"));
		fileMenuItemExit.setEnabled(true);
		fileMenuItemExit.addActionListener(this);
		fileMenu.add(fileMenuItemExit);

		helpMenuItemHelp = new JMenuItem("Help");
		helpMenuItemHelp.setIcon(Builder.getImageIcon("help.png"));
		helpMenuItemHelp.setEnabled(true);
		helpMenuItemHelp.addActionListener(this);
		helpMenuItemHelp.setActionCommand("ShowHelp");
		helpMenu.add(helpMenuItemHelp);
		helpMenu.addSeparator();

		helpMenuItemAbout = new JMenuItem("About FHAES");
		helpMenuItemAbout.setIcon(Builder.getImageIcon("info.png"));
		helpMenuItemAbout.setEnabled(true);
		helpMenuItemAbout.setActionCommand("ShowAbout");
		helpMenuItemAbout.addActionListener(this);
		helpMenu.add(helpMenuItemAbout);

		this.setJMenuBar(menuBar);
	}

	@SuppressWarnings("unused")
	public void actionPerformed(ActionEvent e) {

		if (e.getActionCommand().equals("ShowHelp"))
		{
			PlotWindow.showHelp();
		}

		if (e.getActionCommand().equals("ShowAbout"))
		{
			PlotWindow.showAbout();
		}

		Component comp = null;
		comp = (Component) e.getSource();
		if (comp == fileMenuItemCreateFHX2Plot)
		{
			// System.err.println("Launch button has been clicked.");
			createFHXPlotWindow(null);
		}
		if (comp == fileMenuItemAutoExport)
		{
			// AutoExport Code
			// System.err.println("fileMenuItemAutoExport has been clicked.");

			File list = new File(FHPlotCommon.FHAES_AUTO_INPUT_LIST);
			int fileCount = 0;
			BufferedReader in = null;
			String line = null;
			int begin = 0;
			int end = 49;

			try
			{
				in = new BufferedReader(new FileReader(list));
				while ((line = in.readLine()) != null)
				{
					fileCount++;
				}
			}
			catch (IOException ioe)
			{
			}

			// System.err.println("fileCount<" + fileCount + ">\n");
			end = Math.min(end, fileCount - 1);
			// System.err.println("begin<" + begin + ">end<" + end + ">\n");
			while (end < fileCount)
			{
				autoExportPlots(begin, end);
				begin = begin + 49;
				end = begin + 49;
			}
		}
		if (comp == fileMenuItemExit)
		{
			// System.err.println("Launch button has been clicked.");
			for (int i = 0; i < MAX_PLOT_WINDOWS; i++)
			{
				if (plotWindows[i] != null)
				{
					disposeFHXPlotWindow(plotWindows[i]);
				}
			}
			setVisible(false);
			dispose();
		}

	}

	private PlotWindow createFHXPlotWindow(File inputFile) {

		String lastVisitedFolder = App.prefs.getPref(PrefKey.PREF_LAST_READ_FOLDER, null);
		JFileChooser fc;

		if (lastVisitedFolder != null)
		{
			fc = new JFileChooser(lastVisitedFolder);
		}
		else
		{
			fc = new JFileChooser();
		}

		fc.setMultiSelectionEnabled(false);
		fc.setDialogTitle("Open file");
		fc.setAcceptAllFileFilterUsed(false);
		fc.setFileFilter(new FHXFileFilter());

		int returnVal = fc.showOpenDialog(this);
		if (returnVal == JFileChooser.APPROVE_OPTION)
		{

			File file = fc.getSelectedFile();

			// Set lastPathVisited
			App.prefs.setPref(PrefKey.PREF_LAST_READ_FOLDER, file.getParent());

			setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));
			PlotWindow plotWindow = new PlotWindow(this, file);
			setCursor(Cursor.getDefaultCursor());

			plotWindow.setVisible(true);
			plotWindow.setExtendedState(plotWindow.getExtendedState() | JFrame.MAXIMIZED_BOTH);
			return plotWindow;
		}

		return null;
	}

	@SuppressWarnings({ "unused", "resource" })
	private void autoExportPlots(int begin, int end) {

		// System.err.println("AutoExportPlots begin<" + begin + ">end<" + end + ">\n" );
		List<Object> filearray;

		File list = new File(FHPlotCommon.FHAES_AUTO_INPUT_LIST);
		filearray = new ArrayList<Object>();
		try
		{
			BufferedReader in = new BufferedReader(new FileReader(list));
			String line;
			PlotWindow plotWindow = null;
			int count = 0;
			while ((line = in.readLine()) != null)
			{
				// File file = new File("C:\\Documents and Settings\\wgross\\My Documents\\ARCHUL3.FHX");
				if (count >= begin && count <= end)
				{
					plotWindow = createFHXPlotWindow(new File(line));
					this.disposeFHXPlotWindow(plotWindow);
				}
				count++;
			}
		}
		catch (IOException ioe)
		{
		}

	}

	public void disposeFHXPlotWindow(PlotWindow winToDispose) {

		for (int i = 0; i < MAX_PLOT_WINDOWS; i++)
		{
			if (plotWindows[i] == winToDispose)
			{
				plotWindows[i] = null;
				winToDispose.setVisible(false);
				winToDispose.disposeWindow();
			}
		}
	}

	/**
	 * Listens for the main window closing, and shuts down the application.
	 * 
	 * @param event information about the window event.
	 */
	public void windowClosing(final WindowEvent event) {

		if (event.getWindow() == this)
		{
			for (int i = 0; i < MAX_PLOT_WINDOWS; i++)
			{
				if (plotWindows[i] != null)
				{
					disposeFHXPlotWindow(plotWindows[i]);
				}
			}
			setVisible(false);
			dispose();
		}
	}

	/**
	 * Required for WindowListener interface, but not used by this class.
	 * 
	 * @param event information about the window event.
	 */
	public void windowClosed(final WindowEvent event) {

		// ignore
	}

	/**
	 * Required for WindowListener interface, but not used by this class.
	 * 
	 * @param event information about the window event.
	 */
	public void windowActivated(final WindowEvent event) {

		// ignore
	}

	/**
	 * Required for WindowListener interface, but not used by this class.
	 * 
	 * @param event information about the window event.
	 */
	public void windowDeactivated(final WindowEvent event) {

		// ignore
	}

	/**
	 * Required for WindowListener interface, but not used by this class.
	 * 
	 * @param event information about the window event.
	 */
	public void windowDeiconified(final WindowEvent event) {

		// ignore
	}

	/**
	 * Required for WindowListener interface, but not used by this class.
	 * 
	 * @param event information about the window event.
	 */
	public void windowIconified(final WindowEvent event) {

		// ignore
	}

	/**
	 * Required for WindowListener interface, but not used by this class.
	 * 
	 * @param event information about the window event.
	 */
	public void windowOpened(final WindowEvent event) {

		// ignore
	}

	/**
	 * Main for Fire History Plotting.
	 * 
	 * @param args ignored.
	 */
	@SuppressWarnings("unused")
	public static void main(String[] args) {

		App.init();
		ChartEditorManager.setChartEditorFactory(new FHAESChartEditorFactory());
		MainFrame driver = new MainFrame("FHChart Launcher");
	}
}
