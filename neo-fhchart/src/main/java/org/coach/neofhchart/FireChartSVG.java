package org.coach.neofhchart;

import java.io.*;
import java.util.ArrayList;

import org.w3c.dom.*;
import org.apache.batik.dom.svg.SVGDOMImplementation;
import org.fhaes.fhfilereader.*;

import javax.xml.transform.*;
import javax.xml.transform.dom.*;
import javax.xml.transform.stream.*;


public class FireChartSVG {
    
    public Document doc;
    private String svgNS;
    private DOMImplementation impl;

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
    
    public FireChartSVG(AbstractFireHistoryReader f){
        impl = SVGDOMImplementation.getDOMImplementation();
    	svgNS = SVGDOMImplementation.SVG_NAMESPACE_URI;
    	doc = impl.createDocument(svgNS, "svg", null);

        Element svgRoot = doc.getDocumentElement();

        svgRoot.setAttributeNS(null, "width", "100%");
    	svgRoot.setAttributeNS(null, "height", "100%");
    	
    	// attach the rectangle to the svg root element
    	// svgRoot.appendChild( getRect(doc, svgNS, f) );
    	Element padding_grouper = doc.createElementNS(svgNS, "g");
    	padding_grouper.setAttributeNS(null, "transform", "translate (20,0)");
    	svgRoot.appendChild(padding_grouper);
    	padding_grouper.appendChild( getChronologyPlot(doc, svgNS, f));

    };

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
        chronologyPlot.setAttributeNS(null, "transform", "translate(0,20) scale(1.1)");
        chronologyPlot.setAttributeNS(null, "display", "inline");
        //        chronologyPlot.setAttributeNS(null, "stroke", "black");
        //        chronologyPlot.setAttributeNS(null, "stroke-width", "black");
        int spacing = 20;
         	
        // build all of the series
    	ArrayList<FHSeries> series_arr = f.getSeriesList();
    	for(int i = 0; i < series_arr.size(); i++) {
            
    		FHSeries s = series_arr.get(i);
    		// add in the series group, which has the lines and ticks
            Element series_group = buildSingleSeries( doc, svgNS, s );
            int x_offset = s.getFirstYear() - f.getFirstYear();
            series_group.setAttributeNS(null, "transform", "translate("+Integer.toString(x_offset)+","+Integer.toString(i*spacing)+")");
            
            // add in the label for the series
            Text series_name_text = doc.createTextNode(s.getTitle());
            Element series_name = doc.createElementNS(svgNS, "text");
            series_name.setAttributeNS(null, "x", Integer.toString(f.getLastYear() - f.getFirstYear() + 5));
            series_name.setAttributeNS(null, "y", Integer.toString(i*spacing) );
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
            	fire_event.setAttributeNS(null, "y", "0" );
            	fire_event.setAttributeNS(null, "width", "1");
            	fire_event.setAttributeNS(null, "height", "10");
            	fire_event.setAttributeNS(null, "fill", "#FF9900");
            	series_fire_events.appendChild(fire_event);
        	}
        }
        series_group.appendChild(series_fire_events);
        
        // add series label
        /*
        Text series_name_text = doc.createTextNode(s.getTitle());
        Element series_name = doc.createElementNS(svgNS, "text");
        series_name.setAttributeNS(null, "x", Integer.toString(5));
        series_name.setAttributeNS(null, "y", "0" );
        series_name.setAttributeNS(null, "font-family", "Verdana");
        series_name.setAttributeNS(null, "font-size", "8");
        //            series_name.setAttributeNS(null, "fill", "blue");
        series_name.appendChild(series_name_text);
        series_group.appendChild(series_name);
        */
        
        return series_group;
    }

    /*private static Element getRect(Document doc, String svgNS, AbstractFireHistoryReader f){
        Element rectangle = doc.createElementNS(svgNS, "rect");
    	rectangle.setAttributeNS(null, "x", "10");
    	rectangle.setAttributeNS(null, "y", "20");
    	rectangle.setAttributeNS(null, "width", "100");
    	rectangle.setAttributeNS(null, "height", "50");
    	rectangle.setAttributeNS(null, "fill", "#FF9900");
        return rectangle;
    };*/
}
