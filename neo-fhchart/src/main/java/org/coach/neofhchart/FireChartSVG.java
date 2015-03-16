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
    	svgRoot.appendChild( getRect(doc, svgNS, f) );
    	svgRoot.appendChild( getChronologyPlot(doc, svgNS, f));

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
        chronologyPlot.setAttributeNS(null, "transform", "translate(-"+Integer.toString(f.getFirstYear())+",20)");
        //        chronologyPlot.setAttributeNS(null, "stroke", "black");
        //        chronologyPlot.setAttributeNS(null, "stroke-width", "black");
        int spacing = 10;
         	
    	ArrayList<FHSeries> series_arr = f.getSeriesList();
    	for(int i = 0; i < series_arr.size(); i++) {
            Element series_group= doc.createElementNS(svgNS, "g");
            FHSeries s = series_arr.get(i);
            series_group.setAttributeNS(null, "id", s.getTitle());
    		
            Element series_line = doc.createElementNS(svgNS, "line");
            series_line.setAttributeNS(null, "x1", Integer.toString( s.getFirstYear()));
            series_line.setAttributeNS(null, "y1", Integer.toString(i*spacing) );
            series_line.setAttributeNS(null, "x2", Integer.toString( s.getLastYear() ));
            series_line.setAttributeNS(null, "y2", Integer.toString(i*spacing));
            series_line.setAttributeNS(null, "stroke", "black");
            series_line.setAttributeNS(null, "stroke-width", "1");
            
            
            Text series_name_text = doc.createTextNode(s.getTitle());
            Element series_name = doc.createElementNS(svgNS, "text");
            series_name.setAttributeNS(null, "x", Integer.toString( f.getLastYear() + 5));
            series_name.setAttributeNS(null, "y", Integer.toString(i*spacing) );
            series_name.setAttributeNS(null, "font-family", "Verdana");
            series_name.setAttributeNS(null, "font-size", "8");
            //            series_name.setAttributeNS(null, "fill", "blue");
            series_name.appendChild(series_name_text);
            
            series_group.appendChild(series_name);
            series_group.appendChild(series_line);
            chronologyPlot.appendChild(series_group);	
    	}
        //    	chronologyPlot.setAttributeNS(null, "display", "none");
    	return chronologyPlot;
    }

    private static Element getRect(Document doc, String svgNS, AbstractFireHistoryReader f){
        Element rectangle = doc.createElementNS(svgNS, "rect");
    	rectangle.setAttributeNS(null, "x", "10");
    	rectangle.setAttributeNS(null, "y", "20");
    	rectangle.setAttributeNS(null, "width", "100");
    	rectangle.setAttributeNS(null, "height", "50");
    	rectangle.setAttributeNS(null, "fill", "#FF9900");
        return rectangle;
    };
}
