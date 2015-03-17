package org.coach.neofhchart;

import java.io.*;
import java.util.ArrayList;

import org.w3c.dom.*;
import org.apache.batik.dom.svg.SVGDOMImplementation;
import org.fhaes.enums.EventTypeToProcess;
import org.fhaes.enums.FireFilterType;
import org.fhaes.fhfilereader.*;

import javax.xml.transform.*;
import javax.xml.transform.dom.*;
import javax.xml.transform.stream.*;


public class FireChartSVG {
    
    public Document doc;
    
    private String svgNS;
    private DOMImplementation impl;
    private AbstractFireHistoryReader reader;
        
    private static int SERIES_HEIGHT = 10;
    private static int SERIES_SPACING = 15;
    private static int COMPOSITE_PLOT_HEIGHT = 30;
    private static int INDEX_PLOT_HEIGHT = 50;
    private static int PLOT_SPACING = 5;
    private static int TIME_AXIS_HEIGHT = 10;
    private static int LABEL_X_OFFSET = 5; // how far the label is over from the plot ... in years (TODO)
    
    private boolean isIndexPlotVisible = false;
    private boolean isChronologyPlotVisible = true;
    private boolean isCompositePlotVisible = true;
    
    public static void printDocument(Document doc, OutputStream out) {
        try {
            TransformerFactory tf = TransformerFactory.newInstance();
            Transformer t = tf.newTransformer();
            t.setOutputProperty(OutputKeys.METHOD, "xml");
            t.setOutputProperty(OutputKeys.INDENT, "yes");
            t.transform(new DOMSource(doc), new StreamResult(new OutputStreamWriter(out, "UTF-8")));
        } catch (Exception ex) {
            System.out.println("Error: Could not printDocument\n");
        }
    }
    
    public void dumpDocument() {
    	try {
    		File f = new File("FireChartSVG_dump.svg");
    		if(!f.exists()) { f.createNewFile(); }
    		FileOutputStream fstream = new FileOutputStream(f);
    		printDocument(doc, fstream);
    	} catch (Exception e) {
    		e.printStackTrace();
    	}
    }
    
    public void print() {
    	printDocument(doc, System.out);
    }
    
    public String getName() {
    	return reader.getName();
    }
    
    public FireChartSVG(AbstractFireHistoryReader f){
        impl = SVGDOMImplementation.getDOMImplementation();
    	svgNS = SVGDOMImplementation.SVG_NAMESPACE_URI;
    	doc = impl.createDocument(svgNS, "svg", null);
    	reader = f;

    	// calculate plot dimensions
    	int cur_bottom = 0; // used for tracking where the bottom of the chart is as it is being built
    	int index_plot_height = INDEX_PLOT_HEIGHT;
    	if(isIndexPlotVisible) { cur_bottom += index_plot_height + PLOT_SPACING; }
    	
    	int chronology_plot_y = cur_bottom;
    	int chronology_plot_height = (f.getSeriesList().size())*SERIES_SPACING + SERIES_HEIGHT;
    	if(isChronologyPlotVisible) {cur_bottom += chronology_plot_height + PLOT_SPACING; }
    		
    	int composite_plot_y = cur_bottom;
    	int composite_plot_height = COMPOSITE_PLOT_HEIGHT;
    	if(isCompositePlotVisible) { cur_bottom += composite_plot_height + PLOT_SPACING; }
    	
    	int total_height = cur_bottom + PLOT_SPACING;
    	
    	
        Element svgRoot = doc.getDocumentElement();

        svgRoot.setAttributeNS(null, "width", "100%");
    	svgRoot.setAttributeNS(null, "height", Integer.toString(total_height) + 50);
    	
    	// attach the rectangle to the svg root element
    	// svgRoot.appendChild( getRect(doc, svgNS, f) );
    	Element padding_grouper = doc.createElementNS(svgNS, "g");
    	padding_grouper.setAttributeNS(null, "id", "padding_g");
    	padding_grouper.setAttributeNS(null, "transform", "translate (20,0)");
    	svgRoot.appendChild(padding_grouper);	
    	
    	// build time axis
    	Element time_axis_g = doc.createElementNS(svgNS, "g");
    	time_axis_g.setAttributeNS(null, "id", "time_axis_g");
    	time_axis_g.appendChild( getTimeAxis(doc, svgNS, f, total_height));
    	padding_grouper.appendChild(time_axis_g);
    	
    	// build and position chronology plot
    	Element chrono_plot_g = doc.createElementNS(svgNS, "g");
    	chrono_plot_g.appendChild( getChronologyPlot(doc, svgNS, f) );
    	chrono_plot_g.setAttributeNS(null, "transform", "translate(0,"+chronology_plot_y+")");
    	padding_grouper.appendChild( chrono_plot_g );
    	
    	// build and position composite plot
    	Element comp_plot_g = doc.createElementNS(svgNS, "g");
    	comp_plot_g.setAttributeNS(null, "id", "comp_plot_g");
    	comp_plot_g.setAttributeNS(null, "transform", "translate(0,"+composite_plot_y+")");
    	comp_plot_g.appendChild( getCompositePlot(doc, svgNS, f, COMPOSITE_PLOT_HEIGHT, EventTypeToProcess.FIRE_EVENT, FireFilterType.NUMBER_OF_EVENTS, 0.5, 3));
    	padding_grouper.appendChild( comp_plot_g );

    };
    
    private static Element getCompositePlot(Document doc, String svgNS, AbstractFireHistoryReader f, int height, 
    										EventTypeToProcess eventsToProcess, FireFilterType filterType, double filterValue,
    										int minNumberOfSamples) {
    	// compositePlot is centered off of the year 0 A.D.
    	Element composite_plot = doc.createElementNS(svgNS, "g");
    	composite_plot.setAttributeNS(null, "transform", "translate(-"+ f.getFirstYear()+",0)");
    	ArrayList<Integer> composite_years = f.getCompositeFireYears( eventsToProcess, filterType,
    																  filterValue, minNumberOfSamples);
    	for( int i : composite_years) {
    		Element event_line = doc.createElementNS(svgNS, "line");
    		event_line.setAttributeNS(null,"x1",Integer.toString(i));
            event_line.setAttributeNS(null,"x2",Integer.toString(i));
            event_line.setAttributeNS(null,"y1","0");
            event_line.setAttributeNS(null,"y2",Integer.toString(height));
            event_line.setAttributeNS(null,"stroke-width", "1");
            event_line.setAttributeNS(null,"stroke", "black");
    		composite_plot.appendChild(event_line);
    	}
    	
    	// draw a rectangle around it
    	Element comp_rect = doc.createElementNS(svgNS, "rect");
    	comp_rect.setAttributeNS(null, "x", Integer.toString(f.getFirstYear()));
    	comp_rect.setAttributeNS(null, "y", "0" );
    	comp_rect.setAttributeNS(null, "width", Integer.toString(f.getLastYear()-f.getFirstYear()));
    	comp_rect.setAttributeNS(null, "height", Integer.toString(height));
    	comp_rect.setAttributeNS(null, "fill", "none");
    	comp_rect.setAttributeNS(null, "stroke", "black");
    	comp_rect.setAttributeNS(null, "stroke-width", "1");
    	composite_plot.appendChild(comp_rect);
    	
    	// add the label
    	Text composite_name_text= doc.createTextNode("Composite");
    	int font_size = 10;
        Element composite_name= doc.createElementNS(svgNS, "text");
        composite_name.setAttributeNS(null, "x", Integer.toString(f.getLastYear() + LABEL_X_OFFSET));
        composite_name.setAttributeNS(null, "y", Integer.toString(height/2 + font_size/2));
        composite_name.setAttributeNS(null, "font-family", "Verdana");
        composite_name.setAttributeNS(null, "font-size", Integer.toString(font_size));
        composite_name.appendChild(composite_name_text);
        composite_plot.appendChild(composite_name);
    	
    	return composite_plot;
    }
    
    private static Element getTimeAxis(Document doc, String svgNS, AbstractFireHistoryReader f, int height) {
    	// time axis is centered off of the year 0 A.D.
    	Element timeAxis = doc.createElementNS(svgNS, "g");
    	timeAxis.setAttributeNS(null, "transform", "translate(-"+ f.getFirstYear()+",0)");
    	for(int i = f.getFirstYear(); i < f.getLastYear(); i++) {
    		if( i % 50 == 0) { // year is a multiple of 50
    			System.out.println("year: "+i);
    			Element dash_line = doc.createElementNS(svgNS, "line");
                        dash_line.setAttributeNS(null,"x1",Integer.toString(i));
                        dash_line.setAttributeNS(null,"x2",Integer.toString(i));
                        dash_line.setAttributeNS(null,"y1","0");
                        dash_line.setAttributeNS(null,"y2",Integer.toString(height));
                        dash_line.setAttributeNS(null,"stroke-width", "1");
                        dash_line.setAttributeNS(null,"stroke-dasharray", "1,3");
                        dash_line.setAttributeNS(null,"stroke", "grey");
                        timeAxis.appendChild(dash_line);

                        Element year_text_holder = doc.createElementNS(svgNS, "text");
                        Text year_text = doc.createTextNode(Integer.toString(i));
                        year_text_holder.setAttributeNS(null, "x", Integer.toString(i));
                        year_text_holder.setAttributeNS(null, "y", Integer.toString(height));
                        year_text_holder.setAttributeNS(null, "font-family", "Verdana");
                        year_text_holder.setAttributeNS(null, "font-size", "8");
                        year_text_holder.appendChild(year_text);
                        timeAxis.appendChild(year_text_holder);
    		}
    	}
    	return timeAxis;
    }

    public void setChronologyPlotVisibility(boolean isVisible) {
    	Element plot_grouper = doc.getElementById("chronology_plot");
    	if( !isVisible ) {
    		plot_grouper.setAttributeNS(null, "display", "none");
    	}
    	else {
    		plot_grouper.setAttributeNS(null, "display", "inline");
    	}
    }
    public void toggleChronologyPlotVisibility() {
    	Element plot_grouper = doc.getElementById("chronology_plot");
    	setChronologyPlotVisibility(plot_grouper.getAttributeNS(null, "display") == "none");
    }
    
    private static Element getChronologyPlot(Document doc, String svgNS, AbstractFireHistoryReader f) {
    	Element chronologyPlot = doc.createElementNS(svgNS, "g");
        chronologyPlot.setAttributeNS(null, "id", "chronology_plot");
        //chronologyPlot.setAttributeNS(null, "transform", "");
        chronologyPlot.setAttributeNS(null, "display", "inline");
                 	
        // build all of the series
    	ArrayList<FHSeries> series_arr = f.getSeriesList();
    	for(int i = 0; i < series_arr.size(); i++) {
            
    		FHSeries s = series_arr.get(i);
    		// add in the series group, which has the lines and ticks
            Element series_group = buildSingleSeries( doc, svgNS, s );
            int x_offset = s.getFirstYear() - f.getFirstYear();
            series_group.setAttributeNS(null, "transform", "translate("+Integer.toString(x_offset)+","+Integer.toString(i*SERIES_SPACING)+")");
            
            // add in the label for the series
            Text series_name_text = doc.createTextNode(s.getTitle());
            Element series_name = doc.createElementNS(svgNS, "text");
            series_name.setAttributeNS(null, "x", Integer.toString(f.getLastYear() - f.getFirstYear() + LABEL_X_OFFSET));
            series_name.setAttributeNS(null, "y", Integer.toString(i*SERIES_SPACING + SERIES_HEIGHT/2) );
            series_name.setAttributeNS(null, "font-family", "Verdana");
            series_name.setAttributeNS(null, "font-size", "8");
            series_name.appendChild(series_name_text);
            
            chronologyPlot.appendChild(series_name);
            chronologyPlot.appendChild(series_group);	
    	}
        //    	chronologyPlot.setAttributeNS(null, "display", "none");
    	return chronologyPlot;
    }
    
    private static Element buildSingleSeries(Document doc, String svgNS, FHSeries s) {
    	Element series_group= doc.createElementNS(svgNS, "g");
    	series_group.setAttributeNS(null, "id", s.getTitle());
		
        // draw in the recording and non-recording lines
        Element line_group = doc.createElementNS(svgNS, "g");
        boolean[] recording_years = s.getRecordingYears();
        if(recording_years.length != 0) {
        	int begin_index = 0;
        	boolean isRecording = recording_years[0];
        	for(int j = 1; j < recording_years.length; j++) {
        		if(isRecording != recording_years[j] || j == recording_years.length - 1) { //need to draw a line
        			Element series_line = doc.createElementNS(svgNS, "line");
                    series_line.setAttributeNS(null, "x1", Integer.toString( begin_index ));
                    series_line.setAttributeNS(null, "y1", "0" );
                    series_line.setAttributeNS(null, "x2", Integer.toString( j ));
                    series_line.setAttributeNS(null, "y2", "0");
                    series_line.setAttributeNS(null, "stroke", "black");
                    series_line.setAttributeNS(null, "stroke-width", "1");
                    if(!isRecording) { // make it a dashed line
                    	series_line.setAttributeNS(null, "stroke-dasharray", "1,3");
                    }
                    line_group.appendChild(series_line);
                    begin_index = j; 
                    isRecording = recording_years[j];
        		}
        	}
        }
        series_group.appendChild(line_group);
        
        // add in fire events
        Element series_fire_events = doc.createElementNS(svgNS, "g");
        boolean[] fire_years = s.getEventYears();
        for(int j = 0; j < fire_years.length; j++) {
        	if( fire_years[j] ) {
        		Element fire_event = doc.createElementNS(svgNS, "rect");
        		fire_event.setAttributeNS(null, "x", Integer.toString(j));
            	fire_event.setAttributeNS(null, "y", Integer.toString(-SERIES_HEIGHT/2) );
            	fire_event.setAttributeNS(null, "width", "1");
            	fire_event.setAttributeNS(null, "height", Integer.toString(SERIES_HEIGHT));
            	fire_event.setAttributeNS(null, "fill", "black");
            	series_fire_events.appendChild(fire_event);
        	}
        }
        series_group.appendChild(series_fire_events);
        
        // add in injury events
        Element series_injury_events = doc.createElementNS(svgNS, "g");
        boolean[] injury_years = s.getInjuryYears();
        for(int j = 0; j < injury_years.length; j++) {
        	if( injury_years[j] ) {
        		Element fire_event = doc.createElementNS(svgNS, "rect");
        		int width = 3;
        		fire_event.setAttributeNS(null, "x", Integer.toString(j- width/2));
            	fire_event.setAttributeNS(null, "y", Integer.toString(-SERIES_HEIGHT/2) );
            	fire_event.setAttributeNS(null, "width", Integer.toString(width));
            	fire_event.setAttributeNS(null, "height", Integer.toString(SERIES_HEIGHT));
            	fire_event.setAttributeNS(null, "fill", "none");
            	fire_event.setAttributeNS(null, "stroke", "black");
            	series_fire_events.appendChild(fire_event);
        	}
        }
        series_group.appendChild(series_fire_events);
        
        // add in inner year pith marker
        if( s.hasPith() ){
        	Element pith_marker = doc.createElementNS(svgNS, "rect");
        	int height = 5;
        	pith_marker.setAttributeNS(null, "x", "0"); // inner year
        	pith_marker.setAttributeNS(null, "y", Integer.toString(-height/2));
        	pith_marker.setAttributeNS(null, "width", "1");
        	pith_marker.setAttributeNS(null, "height", Integer.toString(height));
        	pith_marker.setAttributeNS(null, "fill", "black");
        	series_group.appendChild(pith_marker);
        }
        else {
        	Element no_pith_marker = doc.createElementNS(svgNS, "polygon");
        	no_pith_marker.setAttributeNS(null, "points", "-2,0.5 5,-5 2,0.5");
        	no_pith_marker.setAttributeNS(null, "fill", "black");
        	series_group.appendChild(no_pith_marker);
        }
        
        // add in out year bark marker
        Element bark_marker_g = doc.createElementNS(svgNS, "g");
        bark_marker_g.setAttributeNS(null, "transform", "translate("+Integer.toString(s.getLastYear()-s.getFirstYear()) +",0) scale(-1,1)");
        if( s.hasBark() ){
        	Element bark_marker = doc.createElementNS(svgNS, "rect");
        	int height = 5;
        	bark_marker.setAttributeNS(null, "x", "0"); // inner year
        	bark_marker.setAttributeNS(null, "y", Integer.toString(-height/2));
        	bark_marker.setAttributeNS(null, "width", "1");
        	bark_marker.setAttributeNS(null, "height", Integer.toString(height));
        	bark_marker.setAttributeNS(null, "fill", "black");
        	bark_marker_g.appendChild(bark_marker);
        }
        else {
        	Element no_bark_marker= doc.createElementNS(svgNS, "polygon");
        	no_bark_marker.setAttributeNS(null, "points", "-2,0.5 5,-5 2,0.5");
        	no_bark_marker.setAttributeNS(null, "fill", "black");
        	bark_marker_g.appendChild(no_bark_marker);
        }
        series_group.appendChild(bark_marker_g);
        
        return series_group;
    }

}
