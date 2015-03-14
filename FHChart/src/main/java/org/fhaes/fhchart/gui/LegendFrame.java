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
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Graphics;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;

import javax.swing.ImageIcon;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;

import org.fhaes.util.Builder;

/**
 * Frame for Legend
 * 
 * @author Peter Brewer
 * 
 */
public class LegendFrame extends JFrame implements ActionListener, WindowListener {

	public static final long serialVersionUID = 24362462L;
	public static final int MAX_PLOT_WINDOWS = 100;
	PlotWindow parentWindow;

	public LegendFrame(PlotWindow parentWindowIn, String title) {

		super(title);
		this.parentWindow = parentWindowIn;
		this.setBackground(Color.white);

		final ImageIcon m_image = Builder.getImageIcon("legend203x255.gif");

		setIconImage(Builder.getApplicationIcon());

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
			FHAESLogoLabel.setBounds(0, 0, 315, 475);
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

		// setVisible(true);
		addWindowListener(this);

		this.pack();
		setSize(new Dimension(315, 515));
		this.setResizable(false);
		// this.repaint();

	}

	@SuppressWarnings("unused")
	public void actionPerformed(ActionEvent e) {

		Component comp = null;
		comp = (Component) e.getSource();
	}

	public void disposeLegendFrame() {

		this.dispose();
	}

	/**
	 */
	public void windowClosing(final WindowEvent event) {

		// ignore
		parentWindow.disposeLegendFrame();
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
	/*
	 * public static void main(String[] args) {
	 * 
	 * FHPlotMainFrame driver = new FHPlotMainFrame("Fire History Analysis and Exploration System (FHAES) Graphics Module"); driver.pack();
	 * driver.setBounds(100, 100, 560, 650); driver.setVisible(true); }
	 */
}
