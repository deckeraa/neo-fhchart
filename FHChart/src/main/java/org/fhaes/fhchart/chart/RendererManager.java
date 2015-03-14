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

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.Rectangle;
import java.awt.TexturePaint;
import java.awt.geom.GeneralPath;
import java.awt.image.BufferedImage;

import org.jfree.chart.renderer.xy.XYLineAndShapeRenderer;

/**
 * FHXPlotRendererManager Manages renderers for subplots: - All Event Subplots, all use the eventSubplotRenderer. - Date subplot, uses the
 * dateSubplotRenderer. - Filter subplot, uses the filterSubplotRenderer.
 * 
 * Defines shape and line renderer for all series of each Event Subplot. Event Subplots depict: Fire Events, Injury Events, Lifecycle Events
 * (pith, bark, inner year, outter year, recorder years and nonrecorder years) and what years meet current the current filter. Every Event
 * SubPlot has 20 series. All series exist, however they are allowed to be empty. Series 0 through 17 represent the 18 single character
 * codes of the FHX file. Series 18 represents the years that meet the current filter settings Series 19 is a false series used to set the
 * vertical range of the Event Plot.
 * 
 * @author Wendy Gross
 */
public class RendererManager {

	private FHPlotCommon fhxPlotCommon;
	private XYLineAndShapeRenderer eventSubplotRenderer;
	private XYLineAndShapeRenderer compositeAxisSubplotRenderer;

	float radius = 4.0f;

	// final static float dash1[] = {2.0f};
	final static float dash1[] = { 2.0f };

	// final static BasicStroke dashed = new BasicStroke(1.0f,
	// BasicStroke.CAP_BUTT,
	// BasicStroke.JOIN_MITER,
	// 2.0f, dash1, 0.0f);
	final static BasicStroke dashed = new BasicStroke(3.0f, BasicStroke.CAP_BUTT, BasicStroke.JOIN_MITER, 2.0f, dash1, 2.0f);

	/**
	 * FHXPlotRendererManager constructor.
	 * 
	 * @param fhPlotCommonIn global members used by all classses of a FHX plot.
	 */
	RendererManager(FHPlotCommon fhxPlotCommonIn) {

		fhxPlotCommon = fhxPlotCommonIn;
		this.eventSubplotRenderer = null;
		this.compositeAxisSubplotRenderer = null;
	}

	/*
	 * Public methods that get the various renderers.
	 */

	/**
	 * Gets the Event Subplot Renderer.
	 * 
	 * @param none
	 */
	public XYLineAndShapeRenderer geteventSubplotRenderer() {

		if (eventSubplotRenderer == null)
		{
			this.eventSubplotRenderer = createEventSubplotRenderer();
		}
		return this.eventSubplotRenderer;
	}

	/**
	 * Gets the Composite Axis Subplot Renderer.
	 * 
	 * @param none
	 */
	public XYLineAndShapeRenderer getcompositeAxisSubplotRenderer() {

		if (compositeAxisSubplotRenderer == null)
		{
			this.compositeAxisSubplotRenderer = createCompositeAxisSubplotRenderer();
		}
		return this.compositeAxisSubplotRenderer;
	}

	@SuppressWarnings("static-access")
	private XYLineAndShapeRenderer createEventSubplotRenderer() {

		XYLineAndShapeRenderer rtnRenderer = null;
		rtnRenderer = new XYLineAndShapeRenderer();

		/*
		 * Temporary code that creates a default renderer for Event Subplots
		 */
		// define shape for upsidedown triangle
		GeneralPath triangle_upsidedown = new GeneralPath();
		// triangle_upsidedown.moveTo(0.0f, -1 * radius);
		// triangle_upsidedown.lineTo(radius, +1 * radius);
		// triangle_upsidedown.lineTo(-1 * radius, radius);

		// triangle_upsidedown.moveTo(0.0f, +1 * radius);
		// triangle_upsidedown.lineTo(radius, -1 * radius);
		// triangle_upsidedown.lineTo(-1 * radius, -1 * radius);

		// triangle_upsidedown.moveTo(0.0f, 0.0f);

		triangle_upsidedown.moveTo(radius, -2 * radius);
		triangle_upsidedown.lineTo(-1 * radius, -2 * radius);
		triangle_upsidedown.lineTo(0.0f, 0.0f);
		triangle_upsidedown.lineTo(radius, -2 * radius);

		triangle_upsidedown.closePath();

		// define shape for vertical bar
		GeneralPath inner_year_shape = new GeneralPath();
		inner_year_shape.moveTo(0.0f, -1 * radius);
		inner_year_shape.lineTo(0.0f, 1 * radius);
		inner_year_shape.lineTo(1.0f, 1 * radius);
		inner_year_shape.lineTo(1.0f, -1 * radius);
		inner_year_shape.closePath();
		GeneralPath outter_year_shape = new GeneralPath();
		outter_year_shape.moveTo(0.0f, -1 * radius);
		outter_year_shape.lineTo(0.0f, 1 * radius);
		outter_year_shape.lineTo(-1.0f, 1 * radius);
		outter_year_shape.lineTo(-1.0f, -1 * radius);
		outter_year_shape.closePath();
		// define shape for pith shape
		GeneralPath pith_shape = new GeneralPath();
		pith_shape.moveTo(0.0f, 0.0f);
		pith_shape.lineTo(1.5f * radius, -1.5f * radius);
		pith_shape.lineTo(3.0f, 0.0f);
		pith_shape.closePath();
		// define shape for bark shape
		GeneralPath bark_shape = new GeneralPath();
		bark_shape.moveTo(0.0f, 0.0f);
		bark_shape.lineTo(-1.5f * radius, -1.5f * radius);
		bark_shape.lineTo(-3.0f, 0.0f);
		bark_shape.closePath();
		// define shape long closed bar
		GeneralPath long_closed_bar;
		int x1Points[] = { -1, 1, 1, -1, -1 };
		int y1Points[] = { (int) -radius * 2, (int) -radius * 2, (int) radius * 2, (int) radius * 2, (int) -radius * 2 };
		long_closed_bar = new GeneralPath(GeneralPath.WIND_EVEN_ODD, x1Points.length);
		long_closed_bar.moveTo(x1Points[0], y1Points[0]);
		for (int index = 1; index < x1Points.length; index++)
		{
			long_closed_bar.lineTo(x1Points[index], y1Points[index]);
		}
		;
		long_closed_bar.closePath();
		// define shape for short_open_bar
		GeneralPath short_open_bar;
		float x2Points[] = { -1f, 1f, 1f, -1f, -1f };
		float y2Points[] = { -radius, -radius, radius, radius, -radius };
		short_open_bar = new GeneralPath(GeneralPath.WIND_EVEN_ODD, x2Points.length);
		short_open_bar.moveTo(x2Points[0], y2Points[0]);
		for (int index = 1; index < x2Points.length; index++)
		{
			short_open_bar.lineTo(x2Points[index], y2Points[index]);
		}
		;
		short_open_bar.closePath();

		// JFreeChart v1.0.13
		rtnRenderer.setBaseShapesFilled(true);

		// JFreeChart v0.9.21
		// rtnRenderer.setDefaultShapesFilled(true);

		// Define rendering information for the 18 single code characters defined in the
		// FHX file.
		for (int i = 0; i < fhxPlotCommon.TOTAL_NUM_EVENTS; i++)
		{
			// For pith '[' and bark ']' set symbol to color:gray shape:vertical bar
			if (fhxPlotCommon.EVENT_CHAR[i] == '[')
			{
				rtnRenderer.setSeriesPaint(fhxPlotCommon.EVENT_ID[i], Color.black);
				rtnRenderer.setSeriesShape(fhxPlotCommon.EVENT_ID[i], inner_year_shape);
				rtnRenderer.setSeriesLinesVisible(fhxPlotCommon.EVENT_ID[i], false);
				rtnRenderer.setSeriesShapesVisible(fhxPlotCommon.EVENT_ID[i], true);

			}
			else if (fhxPlotCommon.EVENT_CHAR[i] == ']')
			{
				rtnRenderer.setSeriesPaint(fhxPlotCommon.EVENT_ID[i], Color.black);
				rtnRenderer.setSeriesShape(fhxPlotCommon.EVENT_ID[i], outter_year_shape);
				rtnRenderer.setSeriesLinesVisible(fhxPlotCommon.EVENT_ID[i], false);
				rtnRenderer.setSeriesShapesVisible(fhxPlotCommon.EVENT_ID[i], true);
			}
			else if (fhxPlotCommon.EVENT_CHAR[i] == '{')
			{
				// For inner date '{' set symbol to color:gray shape:positive slope line
				rtnRenderer.setSeriesPaint(fhxPlotCommon.EVENT_ID[i], Color.black);
				rtnRenderer.setSeriesShape(fhxPlotCommon.EVENT_ID[i], pith_shape);
				rtnRenderer.setSeriesLinesVisible(fhxPlotCommon.EVENT_ID[i], false);
				rtnRenderer.setSeriesShapesVisible(fhxPlotCommon.EVENT_ID[i], true);
			}
			else if (fhxPlotCommon.EVENT_CHAR[i] == '}')
			{
				// For outter date '}' set symbol to color:gray shape:negitive slope line
				rtnRenderer.setSeriesPaint(fhxPlotCommon.EVENT_ID[i], Color.black);
				rtnRenderer.setSeriesShape(fhxPlotCommon.EVENT_ID[i], bark_shape);
				rtnRenderer.setSeriesLinesVisible(fhxPlotCommon.EVENT_ID[i], false);
				rtnRenderer.setSeriesShapesVisible(fhxPlotCommon.EVENT_ID[i], true);
			}
			else if (fhxPlotCommon.EVENT_CHAR[i] == '|')
			{
				// For recorder year '|' set color:black line:solid line shape:false
				rtnRenderer.setSeriesPaint(fhxPlotCommon.EVENT_ID[i], Color.black);
				rtnRenderer.setSeriesLinesVisible(fhxPlotCommon.EVENT_ID[i], true);
				rtnRenderer.setSeriesShapesVisible(fhxPlotCommon.EVENT_ID[i], false);
			}
			else if (fhxPlotCommon.EVENT_CHAR[i] == '.')
			{
				// For nonrecorder year '.' set color:black line:dotted line shape:false
				// Create a texture paint
				BufferedImage bi = new BufferedImage(5, 5, BufferedImage.TYPE_INT_RGB);
				Graphics2D big = bi.createGraphics();
				big.setColor(Color.white);
				big.fillRect(0, 0, 5, 5);
				big.setColor(Color.black);
				big.fillRect(2, 0, 2, 5);
				Rectangle r = new Rectangle(0, 0, 5, 5);
				TexturePaint tp = new TexturePaint(bi, r);

				rtnRenderer.setSeriesPaint(fhxPlotCommon.EVENT_ID[i], tp);
				rtnRenderer.setSeriesLinesVisible(fhxPlotCommon.EVENT_ID[i], true);
				rtnRenderer.setSeriesShapesVisible(fhxPlotCommon.EVENT_ID[i], false);
			}
			else if (fhxPlotCommon.EVENT_CHAR[i] == 'D' || fhxPlotCommon.EVENT_CHAR[i] == 'E' || fhxPlotCommon.EVENT_CHAR[i] == 'M'
					|| fhxPlotCommon.EVENT_CHAR[i] == 'A' || fhxPlotCommon.EVENT_CHAR[i] == 'L' || fhxPlotCommon.EVENT_CHAR[i] == 'U')
			{
				// Fire or injury event: D E M L A U found.
				// set color:black, shape:triangle_upsidedown shape:true line:false
				rtnRenderer.setSeriesPaint(fhxPlotCommon.EVENT_ID[i], Color.black);
				rtnRenderer.setSeriesShape(fhxPlotCommon.EVENT_ID[i], long_closed_bar);
				rtnRenderer.setSeriesLinesVisible(fhxPlotCommon.EVENT_ID[i], false);
				rtnRenderer.setSeriesShapesVisible(fhxPlotCommon.EVENT_ID[i], true);
				rtnRenderer.setSeriesShapesFilled(fhxPlotCommon.EVENT_ID[i], true);
			}
			else
			{
				// Fire or injury event: d e m l a u found.
				// set color:black, shape:triangle_upsidedown shape:true line:false
				rtnRenderer.setSeriesPaint(fhxPlotCommon.EVENT_ID[i], Color.black);
				rtnRenderer.setSeriesShape(fhxPlotCommon.EVENT_ID[i], short_open_bar);
				rtnRenderer.setSeriesLinesVisible(fhxPlotCommon.EVENT_ID[i], false);
				rtnRenderer.setSeriesShapesVisible(fhxPlotCommon.EVENT_ID[i], true);
				rtnRenderer.setSeriesShapesFilled(fhxPlotCommon.EVENT_ID[i], false);
			}
			/*
			 * // Filter series rtnRenderer.setSeriesPaint(fhxPlotCommon.FILTER_ID, Color.black);
			 * rtnRenderer.setSeriesLinesVisible(fhxPlotCommon.FILTER_ID, true); rtnRenderer.setSeriesShapesVisible(fhxPlotCommon.FILTER_ID,
			 * false); // False series rtnRenderer.setSeriesPaint(fhxPlotCommon.FALSE_ID, Color.white);
			 * rtnRenderer.setSeriesShape(fhxPlotCommon.FALSE_ID, triangle_upsidedown);
			 * rtnRenderer.setSeriesLinesVisible(fhxPlotCommon.FALSE_ID, false); rtnRenderer.setSeriesShapesVisible(fhxPlotCommon.FALSE_ID,
			 * true);
			 */
		}
		return rtnRenderer;
	}

	private XYLineAndShapeRenderer createCompositeAxisSubplotRenderer() {

		XYLineAndShapeRenderer rtnRenderer = null;
		rtnRenderer = new XYLineAndShapeRenderer();

		/*
		 * Temporary code that creates a default renderer for Composite Axis Subplots
		 */

		rtnRenderer.setSeriesPaint(0, Color.black);
		rtnRenderer.setSeriesLinesVisible(0, true);
		rtnRenderer.setSeriesShapesVisible(0, false);

		return rtnRenderer;
	}

}
