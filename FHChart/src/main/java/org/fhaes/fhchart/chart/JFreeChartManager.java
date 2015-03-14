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

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.geom.Rectangle2D;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.text.NumberFormat;

import javax.swing.JFileChooser;
import javax.swing.JOptionPane;

import org.fhaes.filefilter.PDFFilter;
import org.fhaes.filefilter.PNGFilter;
import org.fhaes.preferences.App;
import org.fhaes.preferences.FHAESPreferences.PrefKey;
import org.jfree.chart.ChartPanel;
import org.jfree.chart.ChartUtilities;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.LegendItem;
import org.jfree.chart.LegendItemCollection;
import org.jfree.chart.LegendItemSource;
import org.jfree.chart.axis.AxisLocation;
import org.jfree.chart.axis.NumberAxis;
import org.jfree.chart.plot.CombinedDomainXYPlot;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.chart.plot.XYPlot;
import org.jfree.chart.renderer.xy.StandardXYBarPainter;
import org.jfree.chart.renderer.xy.XYBarRenderer;
import org.jfree.chart.renderer.xy.XYStepRenderer;
import org.jfree.chart.title.LegendTitle;
import org.jfree.data.Range;
import org.jfree.data.xy.IntervalXYDataset;
import org.jfree.data.xy.XYDataset;
import org.jfree.data.xy.XYSeries;
import org.jfree.data.xy.XYSeriesCollection;
import org.jfree.ui.HorizontalAlignment;
import org.jfree.ui.RectangleEdge;
import org.jfree.ui.RectangleInsets;
import org.jfree.util.Log;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.itextpdf.awt.DefaultFontMapper;
import com.itextpdf.awt.FontMapper;
import com.itextpdf.text.Document;
import com.itextpdf.text.DocumentException;
import com.itextpdf.text.Rectangle;
import com.itextpdf.text.pdf.PdfContentByte;
import com.itextpdf.text.pdf.PdfTemplate;
import com.itextpdf.text.pdf.PdfWriter;

/**
 * FHX JFreeChart Chart Manager.
 * 
 * @version 1.0 31 Oct 2006
 * @author Wendy Gross
 */
public class JFreeChartManager {

	private static final Logger log = LoggerFactory.getLogger(JFreeChartManager.class);

	private static final int PIXEL_HEIGHT_PER_SAMPLE = 13;
	private static final int PIXEL_HEIGHT_TOP_BOTOM_BORDERS = 100;
	private static final int PIXEL_HEIGHT_COMPOSITE_SUBPLOT = 25;
	private static final int PIXEL_HEIGHT_FIRE_INDEX_SUBPLOT = 60;

	FHPlotCommon fhxPlotCommon;
	private ChartPanel chartPanel = null;
	JFreeChart chart = null;
	Range initialDomainRange = null;
	public CombinedDomainXYPlot parentPlot; // Plot that contains all subplots
	int numEventSubplots;
	XYPlot[] eventSubplots;
	XYPlot compositeAxisSubplot;
	XYPlot fireIndexSubplot;
	DataManager fhxPlotDataManager;
	RendererManager fhxPlotRendererManager;
	OptionsManager fhxPlotOptionsManager;

	int chartPanelHeight;
	private LegendTitle theLegend;
	private Boolean isLegendVisible = false;

	/**
	 * FHXJFreeChartManager constructor.
	 * 
	 * @param fhPlotCommonIn global members used by all classses of a FHX plot.
	 */
	JFreeChartManager(FHPlotCommon fhxPlotCommonIn) {

		this.fhxPlotCommon = fhxPlotCommonIn;
		chartPanelHeight = 0;
	}

	public JFreeChart getJFreeChart() {

		return chart;
	}

	public void setLegendVisible(Boolean b) {

		this.chart.removeLegend();

		if (b)
		{
			this.chart.addLegend(theLegend);
		}

		isLegendVisible = b;
	}

	public void toggleLegendVisible() {

		setLegendVisible(!isLegendVisible);
	}

	/**
	 * Oh please overt your eyes. This is seriously shameful coding and needs to be fixed properly.
	 */
	private void initLegend() {

		LegendItemCollection legendItemsOld = chart.getPlot().getLegendItems();

		final LegendItemCollection legendItemsNew = new LegendItemCollection();

		int legendItemCount = 0;
		for (int i = 0; i < legendItemsOld.getItemCount(); i++)
		{
			// For some reason there seem to be two sets of legend items
			// Break out if we've got all the items we need
			if (legendItemCount >= 8)
				break;

			LegendItem item = legendItemsOld.get(i);

			if (item.getLabel().equals("D"))
			{
				LegendItem item2 = new LegendItem("Fire event", "A fire event", "Fire event", "", item.getShape(), item.getFillPaint(),
						item.getOutlineStroke(), item.getOutlinePaint());
				legendItemsNew.add(item2);
				legendItemCount++;
			}
			else if (item.getLabel().equals("d"))
			{
				LegendItem item3 = new LegendItem("Injury event", "Injury event", "Injury event", "", item.getShape(),
						item.getOutlineStroke(), item.getOutlinePaint());
				legendItemsNew.add(item3);
				legendItemCount++;

			}
			else if (item.getLabel().equals("["))
			{
				LegendItem item4 = new LegendItem("Inner year with pith", "Inner year with pith", "Inner year with pith", "",
						item.getShape(), item.getFillPaint(), item.getOutlineStroke(), item.getOutlinePaint());
				legendItemsNew.add(item4);
				legendItemCount++;
			}
			else if (item.getLabel().equals("]"))
			{
				LegendItem item2 = new LegendItem("Outer year with bark", "Outer year with bark", "Outer year with bark", "",
						item.getShape(), item.getFillPaint(), item.getOutlineStroke(), item.getOutlinePaint());
				legendItemsNew.add(item2);
				legendItemCount++;
			}
			else if (item.getLabel().equals("{"))
			{
				LegendItem item2 = new LegendItem("Inner year without pith", "Inner year without pith", "Inner year without pith", "",
						item.getShape(), item.getFillPaint(), item.getOutlineStroke(), item.getOutlinePaint());
				legendItemsNew.add(item2);
				legendItemCount++;
			}
			else if (item.getLabel().equals("}"))
			{
				LegendItem item2 = new LegendItem("Outer year without bark", "Outer year without bark", "Outer year without bark", "",
						item.getShape(), item.getFillPaint(), item.getOutlineStroke(), item.getOutlinePaint());
				legendItemsNew.add(item2);
				legendItemCount++;
			}
			else if (item.getLabel().equals("."))
			{
				LegendItem item2 = new LegendItem("Non-recorder years", "Non-recorder years", "Non-recorder years", "", item.getLine(),
						item.getLineStroke(), item.getLinePaint());
				legendItemsNew.add(item2);
				legendItemCount++;
			}
			else if (item.getLabel().equals("|"))
			{
				LegendItem item2 = new LegendItem("Recorder years", "Recorder years", "Recorder years", "", item.getLine(),
						item.getLineStroke(), item.getLinePaint());
				legendItemsNew.add(item2);
				legendItemCount++;
			}
		}
		LegendItemSource source = new LegendItemSource() {

			LegendItemCollection lic = new LegendItemCollection();
			{
				lic.addAll(legendItemsNew);
			}

			public LegendItemCollection getLegendItems() {

				return lic;
			}
		};
		LegendTitle lt = new LegendTitle(source);
		lt.setPosition(RectangleEdge.RIGHT);
		lt.setHorizontalAlignment(HorizontalAlignment.RIGHT);
		lt.setBorder(1.0, 1.0, 1.0, 1.0);
		lt.setItemLabelPadding(new RectangleInsets(0.0, 10.0, 0.0, 10.0));
		this.chart.addLegend(lt);
		theLegend = lt;

		this.setLegendVisible(isLegendVisible);
	}

	public ChartPanel createChartPanel(int plotWidthIn, Range domainRangeIn) {

		ChartPanel rval = null;
		int minimumPlotWidth;
		int minimumPlotHeight;

		minimumPlotWidth = plotWidthIn;

		if (this.fhxPlotCommon.getfhxPlotOptionsManager().getdisplayFireIndexPlot()
				|| this.fhxPlotCommon.getfhxPlotOptionsManager().getdisplayFireChronologyPlot()
				|| this.fhxPlotCommon.getfhxPlotOptionsManager().getdisplayCompositePlot())
		{

			// fhxPlotCommon.appendToDebug("Enter FHXJFreeChartMngr::createChartPanel\n");
			this.fhxPlotDataManager = this.fhxPlotCommon.getfhxPlotDataManager();
			this.fhxPlotRendererManager = this.fhxPlotCommon.getfhxPlotRendererManager();
			this.fhxPlotOptionsManager = this.fhxPlotCommon.getfhxPlotOptionsManager();
			this.numEventSubplots = fhxPlotDataManager.getnumSamples();
			// fhxPlotCommon.appendToDebug("numEventSubplots<" + this.numEventSubplots + ">\n");

			this.chart = createCombinedChart(domainRangeIn);
			// FHMyLegend myLegend = new FHMyLegend();
			// myLegend.setfhxPlotCommon(fhxPlotCommon);
			// StandardLegend standardLegend = new StandardLegend();
			// AutoExport Code
			// this.chart.setLegend(myLegend);
			// AutoExport

			initLegend();

			// JFreeChart v1.0.13
			// this.chart.addLegend(null);

			// JFreeChart v0.9.21
			// this.chart.setLegend(null);

			// minimumPlotWidth = PIXEL_MINIMUM_PLOT_WIDTH;

			// Compute Minimum Plot Height
			int countOfSeriesPlotted = this.fhxPlotCommon.getfhxPlotOptionsManager().getseriesPlottedOptions().countOfSeriesPlotted();
			minimumPlotHeight = PIXEL_HEIGHT_TOP_BOTOM_BORDERS;
			// if (this.fhxPlotCommon.getfhxPlotOptionsManager().getcompositeAxisDisplayOptions().getdisplayCompositeAxis()){
			if (this.fhxPlotCommon.getfhxPlotOptionsManager().getdisplayCompositePlot())
			{
				minimumPlotHeight = minimumPlotHeight + PIXEL_HEIGHT_COMPOSITE_SUBPLOT;
			}
			if (this.fhxPlotCommon.getfhxPlotOptionsManager().getdisplayFireIndexPlot())
			{
				minimumPlotHeight = minimumPlotHeight + PIXEL_HEIGHT_FIRE_INDEX_SUBPLOT;
			}
			if (this.fhxPlotCommon.getfhxPlotOptionsManager().getdisplayFireChronologyPlot())
			{
				minimumPlotHeight = minimumPlotHeight + (countOfSeriesPlotted * PIXEL_HEIGHT_PER_SAMPLE);
			}

			//
			// AutoExport Code - add in height for legend - FHMyLegend
			// minimumPlotHeight = minimumPlotHeight + 85;

			this.chartPanelHeight = minimumPlotHeight;

			// Wendy todo 20080606
			this.chartPanel = new ChartPanel(this.chart, minimumPlotWidth, minimumPlotHeight, minimumPlotWidth, minimumPlotHeight, 10000, // minimumPlotWidth,
					10000, // minimumPlotHeight,
					false, true, true, true, false, true);

			this.chartPanel.setMouseZoomable(true, false);
			rval = this.chartPanel;
		}
		return rval;
	}

	private JFreeChart createCombinedChart(Range domainRangeIn) {

		// fhxPlotCommon.appendToDebug("Enter FHXJFreeChartMngr::createCombinedChart\n");
		// Create and initialize parent plot that contains all subplots
		NumberAxis numberAxis = new NumberAxis("Year");
		numberAxis.setStandardTickUnits(NumberAxis.createIntegerTickUnits());
		// numberAxis.setTickUnit(new NumberTickUnit(10));
		this.parentPlot = new CombinedDomainXYPlot(numberAxis);

		// Create and add Fire Index plot
		if (this.fhxPlotCommon.getfhxPlotOptionsManager().getdisplayFireIndexPlot())
		{
			createFireIndexSubplot();
			this.parentPlot.add(this.fireIndexSubplot, 6);
		}

		// Create and add Fire ChonologyPlot
		if (this.fhxPlotCommon.getfhxPlotOptionsManager().getdisplayFireChronologyPlot())
		{
			createEventSubplots();
			int[] columnNoOfSeriesToPlot = this.fhxPlotCommon.getfhxPlotOptionsManager().getseriesPlottedOptions()
					.getseriesPlottedFHX2ColumnNo();
			for (int i = 0; i < columnNoOfSeriesToPlot.length; i++)
			{
				if (columnNoOfSeriesToPlot[i] == -1)
				{
					continue;
				}
				else
				{
					this.parentPlot.add(this.eventSubplots[columnNoOfSeriesToPlot[i] - 1], 1);
				}
				// this.parentPlot.add(this.eventSubplots[i], 1);
			}
		}

		// Create and add Composite Axis subplot
		// if (this.fhxPlotCommon.getfhxPlotOptionsManager().getcompositeAxisDisplayOptions().getdisplayCompositeAxis()){
		if (this.fhxPlotCommon.getfhxPlotOptionsManager().getdisplayCompositePlot())
		{
			createCompositeAxisSubplot();
			if (this.fhxPlotCommon.getfhxPlotOptionsManager().getcompositeAxisDisplayOptions().getdisplayCompositeAxis())
			{
				this.parentPlot.add(this.compositeAxisSubplot, 2);
			}
		}

		// Customize the parent plot
		this.parentPlot.setGap(3.0);
		this.parentPlot.setOrientation(PlotOrientation.VERTICAL);
		NumberAxis domainAxis = (NumberAxis) parentPlot.getDomainAxis();
		domainAxis.setAutoRange(false);

		// Override Integer number format on year axis to remove comma thousand separators
		NumberFormat format = NumberFormat.getNumberInstance();
		format.setGroupingUsed(false);
		domainAxis.setNumberFormatOverride(format);

		this.initialDomainRange = parentPlot.getDataRange(domainAxis);
		double upperbound = initialDomainRange.getUpperBound();
		int tickunit;
		if (this.initialDomainRange.getLength() > 1000)
		{
			tickunit = 100;
		}
		else if (this.initialDomainRange.getLength() > 500)
		{
			tickunit = 50;
		}
		else if (this.initialDomainRange.getLength() > 300)
		{
			tickunit = 20;
		}
		else
		{
			tickunit = 50;
		}
		int whole = ((int) (upperbound / tickunit)) * tickunit;
		double remainder = tickunit - (upperbound - whole);
		upperbound = upperbound + remainder;
		// Reset the initialDomainRange
		this.initialDomainRange = new Range(initialDomainRange.getLowerBound(), upperbound);

		if (domainRangeIn != null)
		{
			domainAxis.setRange(domainRangeIn);
		}
		else
		{
			domainAxis.setRange(initialDomainRange);
		}
		// fhxPlotCommon.appendToDebug("initialDomaimnRange.lowerbound<" + this.initialDomainRange.getLowerBound() + ">\n");
		// fhxPlotCommon.appendToDebug("initialDomaimnRange.upperbound<" + this.initialDomainRange.getUpperBound() + ">\n");
		// this.parentPlot.setDomainGridlinesVisible(true);
		// this.parentPlot.setRangeGridlinesVisible(false);

		// return a new chart containing the overlaid plot...
		JFreeChart jFreeChart;
		jFreeChart = new JFreeChart(this.fhxPlotOptionsManager.gettitle(), JFreeChart.DEFAULT_TITLE_FONT, this.parentPlot, false);
		jFreeChart.setBackgroundPaint(new Color(0xFFFFFF));
		// double hvtc = domainAxis.calculateHighestVisibleTickValue();
		// double lvtc = domainAxis.calculateLowestVisibleTickValue();
		// int visibleTickCount = domainAxis.calculateVisibleTickCount();
		// fhxPlotCommon.appendToDebug("highestVisibleTickValue<" + hvtc + ">\n");
		// fhxPlotCommon.appendToDebug("lowestVisibleTickValue<" + lvtc + ">\n");
		// fhxPlotCommon.appendToDebug("visibleTickCount<" + visibleTickCount + ">\n");
		// fhxPlotCommon.appendToDebug("Exit FHXJFreeChartMngr::createCombinedChart\n");

		return jFreeChart;
	}

	private void createEventSubplots() {

		int sampleId;
		NumberAxis rangeAxis;
		XYDataset xyDataset;
		// fhxPlotCommon.appendToDebug("Enter FHXJFreeChartMngr::createEventSubplots\n");

		this.eventSubplots = new XYPlot[this.numEventSubplots];
		for (sampleId = 0; sampleId < this.numEventSubplots; sampleId++)
		{
			// fhxPlotCommon.appendToDebug("    SampleName<" + this.fhxPlotDataManager.getsampleName(sampleId) + ">\n");

			xyDataset = createEventsXYDataset(sampleId);
			rangeAxis = new NumberAxis(this.fhxPlotDataManager.getsampleName(sampleId));
			rangeAxis.setTickLabelsVisible(false);
			rangeAxis.setLabelAngle(270 * (Math.PI / 180));
			this.eventSubplots[sampleId] = new XYPlot(xyDataset, null, rangeAxis, null);
			this.eventSubplots[sampleId].setRenderer(this.fhxPlotRendererManager.geteventSubplotRenderer());
			this.eventSubplots[sampleId].setRangeAxisLocation(AxisLocation.BOTTOM_OR_RIGHT);
			this.eventSubplots[sampleId].setOutlinePaint(null);
			this.eventSubplots[sampleId].setDomainCrosshairVisible(true);
			this.eventSubplots[sampleId].setRangeCrosshairVisible(false);
			this.eventSubplots[sampleId].setDomainGridlinesVisible(true);
			this.eventSubplots[sampleId].setRangeGridlinesVisible(false);
		}
	}

	private void createCompositeAxisSubplot() {

		NumberAxis rangeAxis;
		XYDataset xyDataset;
		// fhxPlotCommon.appendToDebug("Enter FHXJFreeChartMngr::createCompositeAxisSubplot\n");

		this.compositeAxisSubplot = new XYPlot();
		xyDataset = createCompositeAxisXYDataset();
		rangeAxis = new NumberAxis(new String("Composite"));
		rangeAxis.setTickLabelsVisible(false);
		rangeAxis.setUpperBound(100);
		rangeAxis.setLabelAngle(270 * (Math.PI / 180));

		this.compositeAxisSubplot = new XYPlot(xyDataset, null, rangeAxis, null);
		this.compositeAxisSubplot.setRenderer(this.fhxPlotRendererManager.getcompositeAxisSubplotRenderer());
		this.compositeAxisSubplot.setRangeAxisLocation(AxisLocation.BOTTOM_OR_RIGHT);
		this.compositeAxisSubplot.setOutlinePaint(Color.BLACK);
		this.compositeAxisSubplot.setDomainCrosshairVisible(true);
		this.compositeAxisSubplot.setRangeCrosshairVisible(false);
		this.compositeAxisSubplot.setDomainGridlinesVisible(true);
		this.compositeAxisSubplot.setRangeGridlinesVisible(false);
		/*
		 * XYLineAnnotation axisLine = new XYLineAnnotation( initialDomainRange.getLowerBound(),100, initialDomainRange.getUpperBound(),100,
		 * new BasicStroke(2), Color.black); this.compositeAxisSubplot.addAnnotation(axisLine);
		 */
		// NumberAxis axis2 = new NumberAxis("Range Axis2");
		// this.compositeAxisSubplot.setRangeAxis(1, axis2);
		// this.compositeAxisSubplot.setAxisLocation(1, AxisLocation.BOTTON_OR_LEFT);

	}

	@SuppressWarnings("deprecation")
	private void createFireIndexSubplot() {

		NumberAxis sampleDepthAxis = new NumberAxis(new String("Sample Depth"));
		NumberAxis percentScarredAxis = new NumberAxis(new String("% Scarred"));
		sampleDepthAxis.setTickLabelsVisible(true);
		sampleDepthAxis.setLabelAngle(0 * (Math.PI / 180));
		sampleDepthAxis.setLabelPaint(Color.blue);
		sampleDepthAxis.setTickLabelPaint(Color.blue);
		sampleDepthAxis.setRange(0, numEventSubplots);
		percentScarredAxis.setTickLabelsVisible(true);
		percentScarredAxis.setLabelAngle(0 * (Math.PI / 180));

		IntervalXYDataset sampleDepthDataset = createSampleDepthXYDataset();
		IntervalXYDataset percentScarredDataset = createPercentScarredXYDataset();

		XYBarRenderer brenderer = new XYBarRenderer();
		brenderer.setShadowVisible(false);
		brenderer.setBarPainter(new StandardXYBarPainter());
		this.fireIndexSubplot = new XYPlot(sampleDepthDataset, null, sampleDepthAxis, brenderer);

		this.fireIndexSubplot.setRenderer(new XYStepRenderer());
		XYStepRenderer renderer = (XYStepRenderer) this.fireIndexSubplot.getRenderer();
		renderer.setSeriesPaint(0, Color.blue);

		try
		{
			this.fireIndexSubplot.setRangeAxis(1, percentScarredAxis);
			this.fireIndexSubplot.setDataset(1, percentScarredDataset);
			this.fireIndexSubplot.mapDatasetToRangeAxis(1, 1);
			// this.fireIndexSubplot.mapDatasetToRangeAxis(0, 0);
		}
		catch (NullPointerException e)
		{
			Log.debug("NPE caught");
		}

		this.fireIndexSubplot.setRenderer(1, brenderer);

		XYBarRenderer percentScarredRenderer = (XYBarRenderer) this.fireIndexSubplot.getRenderer(1);
		percentScarredRenderer.setPaint(Color.GRAY);

		this.fireIndexSubplot.setRangeAxisLocation(AxisLocation.BOTTOM_OR_LEFT);
		this.fireIndexSubplot.setRangeAxisLocation(1, AxisLocation.BOTTOM_OR_RIGHT);

		this.fireIndexSubplot.setOutlinePaint(Color.BLACK);
		this.fireIndexSubplot.setDomainCrosshairVisible(true);
		this.fireIndexSubplot.setRangeCrosshairVisible(false);
		this.fireIndexSubplot.setDomainGridlinesVisible(true);
		this.fireIndexSubplot.setRangeGridlinesVisible(false);

		sampleDepthAxis.setAutoRangeIncludesZero(true);
		percentScarredAxis.setRange(0, 100);

	}

	public void updateCompositeAndFireIndexXYDatasets() {

		XYDataset xyDatasetComposite;
		XYDataset xyDatasetSampleDepth;
		XYDataset xyDatasetPercentScarred;

		// fhxPlotCommon.appendToDebug("Enter FHXJFreeChartMngr::createCompositeAxisSubplot\n");
		// Create and update xyDatasets for Composite subplot
		xyDatasetComposite = createCompositeAxisXYDataset();
		this.compositeAxisSubplot.setDataset(xyDatasetComposite);

		// Create and update xyDatasets for Fire Index subplot
		xyDatasetSampleDepth = createSampleDepthXYDataset();
		xyDatasetPercentScarred = createPercentScarredXYDataset();
		this.fireIndexSubplot.setDataset(xyDatasetSampleDepth);
		this.fireIndexSubplot.setDataset(1, xyDatasetPercentScarred);
		// this.fireIndexSubplot.mapDatasetToRangeAxis(1, 1);
	}

	private XYDataset createEventsXYDataset(int sampleId) {

		XYSeriesCollection rtnXYDataset;
		DataManager fhxPlotDataManager;
		XYSeries[] xySeries;
		// fhxPlotCommon.appendToDebug("Enter FHXJFreeChartMngr::createXYDataset, sampleId<" + sampleId + ">\n");
		fhxPlotDataManager = fhxPlotCommon.getfhxPlotDataManager();

		// Create all series to be added to the dataset
		// Create series that correspond to single char code of FHX data
		xySeries = new XYSeries[FHPlotCommon.TOTAL_NUM_EVENTS];
		for (int i = 0; i < FHPlotCommon.TOTAL_NUM_EVENTS; i++)
		{
			// for (int i = FHXPlotCommon.TOTAL_NUM_EVENTS-1; i >-1; i--) {
			// fhxPlotCommon.appendToDebug("    create xySeries for <" + FHXPlotCommon.EVENT_CHAR[i] + ">\n");

			if (FHPlotCommon.EVENT_LSRS[i] == FHPlotCommon.LSRS_SHAPE)
			{
				xySeries[i] = fhxPlotDataManager.createSymbolSeries(FHPlotCommon.EVENT_CHAR[i], sampleId);
			}
			else if (FHPlotCommon.EVENT_LSRS[i] == FHPlotCommon.LSRS_LINE)
			{
				char eventChar = FHPlotCommon.EVENT_CHAR[i];
				if (eventChar == '|')
				{
					xySeries[i] = fhxPlotDataManager.createSolidLineSeries(eventChar, sampleId);
				}
				else
				{
					xySeries[i] = fhxPlotDataManager.createDottedLineSeries(eventChar, sampleId);
				}
			}
		}

		// Construct XYDataSet
		rtnXYDataset = new XYSeriesCollection();

		// Add all series to the dataset
		for (int i = 0; i < FHPlotCommon.TOTAL_NUM_EVENTS; i++)
		{
			rtnXYDataset.addSeries(xySeries[i]);
		}

		return rtnXYDataset;
	}

	private XYDataset createCompositeAxisXYDataset() {

		XYSeriesCollection rtnXYDataset;
		DataManager fhxPlotDataManager;
		XYSeries xySeries;
		// fhxPlotCommon.appendToDebug("Enter FHXJFreeChartMngr::createXYDataset\n");
		fhxPlotDataManager = fhxPlotCommon.getfhxPlotDataManager();

		// fhxPlotCommon.appendToDebug("Call fhxPlotDataManager.createCompositeAxisLineSeries\n");

		xySeries = fhxPlotDataManager.createCompositeAxisLineSeries();

		rtnXYDataset = new XYSeriesCollection();

		// Add all series to the dataset
		rtnXYDataset.addSeries(xySeries);

		// fhxPlotCommon.appendToDebug("Exit FHXJFreeChartMngr::createXYDataset\n");

		return rtnXYDataset;
	}

	private IntervalXYDataset createSampleDepthXYDataset() {

		return fhxPlotDataManager.createSampleDepthLineSeries();
	}

	private IntervalXYDataset createPercentScarredXYDataset() {

		return fhxPlotDataManager.createPercentScarredXYDataset();
	}

	/*
	 * private XYDataset createPercentScarredXYDataset() { XYSeriesCollection rtnXYDataset; FHXPlotDataManager fhxPlotDataManager; XYSeries
	 * xySeries; fhxPlotCommon.appendToDebug("Enter FHXJFreeChartMngr::createPercentScarredXYDataset\n"); fhxPlotDataManager =
	 * fhxPlotCommon.getfhxPlotDataManager();
	 * 
	 * //fhxPlotCommon.appendToDebug("Call fhxPlotDataManager.createCompositeAxisLineSeries\n");
	 * 
	 * xySeries = fhxPlotDataManager.createPercentScarredLineSeries();
	 * 
	 * rtnXYDataset = new XYSeriesCollection();
	 * 
	 * // Add all series to the dataset rtnXYDataset.addSeries(xySeries);
	 * 
	 * fhxPlotCommon.appendToDebug("Exit FHXJFreeChartMngr::createPercentScarredXYDataset\n");
	 * 
	 * return rtnXYDataset; }
	 */
	/**
	 * Event listener for Action Events. This class implements the ActionListener Interface. Therefore, this method of the ActionListener
	 * Interface must be implemented.
	 */
	/*
	 * public void actionPerformed(ActionEvent e) { Component comp = null;
	 * 
	 * comp = (Component)e.getSource(); // fhxPlotCommon.appendToDebug("\nEnter FHXJFreeChartManager::actionPerformed<" + comp.getName() +
	 * ">\n");
	 * 
	 * if (comp.getName().equalsIgnoreCase(ZOOM_INITIAL)) { zoomInitial(); } if (comp.getName().equalsIgnoreCase(EXPORT_PDF)) { exportPDF();
	 * } if (comp.getName().equalsIgnoreCase(EXPORT_PNG)) { exportPNG(); } }
	 */

	public void updateChartPanel(Integer chartPanelWidth) {

		// fhxPlotCommon.appendToDebug("\nEnter FHXJFreeChartManager::updateChartPanel\n");

		(this.fhxPlotCommon.getfhxPlotWin()).updateChartPanel(chartPanelWidth);
	}

	/**
	 * Zoom plot to initial, full extents.
	 * 
	 */
	public void zoomInitial() {

		if (this.chart != null)
		{
			XYPlot plot = (XYPlot) chart.getPlot();
			NumberAxis domainAxis = (NumberAxis) plot.getDomainAxis();
			domainAxis.setRange(initialDomainRange);

			// Not in JFreeChart v1.0.13
			// plot.zoomHorizontalAxes(1.0);
			// plot.zoomVerticalAxes(1.0);

			// JFreeChart v0.9
			// plot.zoomHorizontalAxes(1.0);
			// plot.zoomVerticalAxes(1.0);
		}
	}

	/**
	 * Export PDF
	 * 
	 */
	public void exportPDF(boolean autoExport) {

		// fhxPlotCommon.appendToDebug("Enter FHXJFreeChartMngr::exportPDF\n");

		int width;
		int height;
		JFileChooser fc;
		File file = null;
		int returnVal;
		boolean cont;
		String filename;
		String newPath;
		File newFile;

		cont = true;

		if (autoExport == false)
		{
			if (App.prefs.getPref(PrefKey.PREF_LAST_EXPORT_FOLDER, null) != null)
			{
				fc = new JFileChooser(App.prefs.getPref(PrefKey.PREF_LAST_EXPORT_FOLDER, null));
			}
			else
			{
				fc = new JFileChooser();
			}
			fc.setFileFilter(new PDFFilter());
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
				// make sure file has an extension. If it does not assign .pdf
				filename = file.getName();
				if (filename.indexOf(".") != -1)
				{
					// File name contains an extension, so do nothing.
				}
				else
				{
					newPath = new String(file.getPath() + ".pdf");
					newFile = new File(newPath);
					file.renameTo(newFile);
					// fhxPlotCommon.appendToDebug("Saving: " + newPath + "\n");
					// fhxPlotCommon.appendToDebug("Saving: " + newPath + "\n");
					file = newFile;
				}

				// If file exists, ask if it should be overwritten
				if (file.exists())
				{
					int n = JOptionPane.showConfirmDialog(fhxPlotCommon.getfhxPlotWin(), "File: " + file.getName() + " already exists."
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
		}
		else
		{
			String inputFilename = fhxPlotCommon.getinputFilename();
			int dotIndex = inputFilename.indexOf(".");
			if (dotIndex > 0)
			{
				inputFilename = inputFilename.substring(0, dotIndex);
			}
			String fpath = new String(FHPlotCommon.FHAES_AUTO_OUTPUT_DIRECTORY + inputFilename + "_fire_hist_graph" + ".pdf");
			log.debug("About to output to<" + fpath + ">\n");
			file = new File(fpath);
		}

		if (cont == true)
		{
			// Save lastPathVisited
			// fhxPlotCommon.appendToDebug("Path<" + file.getParent() + ">\n");
			// fhxPlotCommon.appendToDebug("Path<" + file.getParent() + ">\n");
			try
			{
				App.prefs.setPref(PrefKey.PREF_LAST_EXPORT_FOLDER, file.getParent());
			}
			catch (NullPointerException e)
			{
				log.error("Unable to save last folder visited pref");
			}
			// Save chart to the file
			width = chartPanel.getWidth();
			height = chartPanel.getHeight();
			log.debug("ExportPDF : " + file.getName());

			try
			{
				saveChartAsPDF(file, chart, width, height, new DefaultFontMapper());
			}
			catch (IOException exe)
			{
				// empty catch block
			}
		}
	}

	/**
	 * Export PNG
	 * 
	 */
	public void exportPNG(boolean autoExport) {

		// fhxPlotCommon.appendToDebug("Enter FHXJFreeChartMngr::exportPNG<" + autoExport + ">\n");

		int width;
		int height;
		JFileChooser fc;
		File file = null;
		int returnVal;
		boolean cont;
		String filename;
		String newPath;
		File newFile;

		cont = true;

		if (autoExport == false)
		{
			if (App.prefs.getPref(PrefKey.PREF_LAST_EXPORT_FOLDER, null) != null)
			{
				fc = new JFileChooser(App.prefs.getPref(PrefKey.PREF_LAST_EXPORT_FOLDER, null));
			}
			else
			{
				fc = new JFileChooser();
			}
			fc.setFileFilter(new PNGFilter());
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
				if (filename.indexOf(".") != -1)
				{
					// File name contains an extension, so do nothing.
				}
				else
				{
					newPath = new String(file.getPath() + ".png");
					newFile = new File(newPath);
					file.renameTo(newFile);
					// fhxPlotCommon.appendToDebug("Saving: " + newPath + "\n");
					// fhxPlotCommon.appendToDebug("Saving: " + newPath + "\n");
					file = newFile;
				}

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
		}
		else
		{
			String inputFilename = fhxPlotCommon.getinputFilename();
			int dotIndex = inputFilename.indexOf(".");
			if (dotIndex > 0)
			{
				inputFilename = inputFilename.substring(0, dotIndex);
			}
			String fpath = new String(FHPlotCommon.FHAES_AUTO_OUTPUT_DIRECTORY + inputFilename + "_fire_hist_graph" + ".png");
			log.debug("About to output to<" + fpath + ">\n");
			file = new File(fpath);
		}

		if (cont == true)
		{
			// Save lastPathVisited
			// fhxPlotCommon.appendToDebug("Path<" + file.getParent() + ">\n");
			// fhxPlotCommon.appendToDebug("Path<" + file.getParent() + ">\n");

			try
			{
				App.prefs.setPref(PrefKey.PREF_LAST_EXPORT_FOLDER, file.getParent());
			}
			catch (NullPointerException e)
			{
				log.error("Unable to save last folder visit pref");
			}
			// Save chart to the file
			width = chartPanel.getWidth();
			height = chartPanel.getHeight();
			log.debug("ExportPNG :" + file.getName());
			try
			{
				ChartUtilities.saveChartAsPNG(file, chart, width, height);
			}
			catch (IOException exe)
			{
			}
		}
	}

	@SuppressWarnings("deprecation")
	public static void writeChartAsPDF(OutputStream out, JFreeChart chart, int width, int height, FontMapper mapper) throws IOException {

		Rectangle pagesize = new Rectangle(width, height);
		// Rectangle pagesize = PageSize.LETTER;

		// Document document = new Document(pagesize, 50, 50, 50, 50);

		Document document = new Document(pagesize);
		try
		{
			PdfWriter writer = PdfWriter.getInstance(document, out);
			document.addAuthor("JFreeChart");
			document.addSubject("Fire History Plot");
			document.open();
			PdfContentByte cb = writer.getDirectContent();
			PdfTemplate tp = cb.createTemplate(pagesize.getWidth(), pagesize.getHeight());
			Graphics2D g2 = tp.createGraphics(pagesize.getWidth(), pagesize.getHeight(), mapper);
			Rectangle2D r2D = new Rectangle2D.Double(0, 0, pagesize.getWidth(), pagesize.getHeight());
			chart.draw(g2, r2D);
			g2.dispose();
			cb.addTemplate(tp, 0, 0);
		}
		catch (DocumentException de)
		{
			System.err.println(de.getMessage());
		}
		document.close();
	}

	public static void saveChartAsPDF(File file, JFreeChart chart, int width, int height, FontMapper mapper) throws IOException {

		OutputStream out = new BufferedOutputStream(new FileOutputStream(file));
		writeChartAsPDF(out, chart, width, height, mapper);
		out.close();
	}

	public ChartPanel getchartPanel() {

		return this.chartPanel;
	}

	public Range getDomainAxisDataRange() {

		Range rval;
		NumberAxis domainAxis = (NumberAxis) parentPlot.getDomainAxis();
		rval = parentPlot.getDataRange(domainAxis);
		// double upperbound = initialDomainRange.getUpperBound();
		// Rectangle2D chartPanel = getScreenDataArea

		return rval;
	}

}
