package org.coach.neofhchart;

import java.io.*;
import java.util.ArrayList;

import org.w3c.dom.*;
import org.apache.batik.dom.svg.SVGDOMImplementation;
import org.fhaes.fhfilereader.*;

import javax.xml.transform.*;
import javax.xml.transform.dom.*;
import javax.xml.transform.stream.*;


public class FireChartSVGFactory {

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
    
    public static Document buildSVGFromReader(AbstractFireHistoryReader f){
        DOMImplementation impl = SVGDOMImplementation.getDOMImplementation();
    	String svgNS = SVGDOMImplementation.SVG_NAMESPACE_URI;
    	Document doc = impl.createDocument(svgNS, "svg", null);

        Element svgRoot = doc.getDocumentElement();

        svgRoot.setAttributeNS(null, "width", "100%");
    	svgRoot.setAttributeNS(null, "height", "100%");
    	
    	// attach the rectangle to the svg root element
    	svgRoot.appendChild( getRect(doc, svgNS, f) );
    	svgRoot.appendChild( getChronologyPlot(doc, svgNS, f));

        return doc;
    };
    
    private static Element getChronologyPlot(Document doc, String svgNS, AbstractFireHistoryReader f) {
    	Element chronologyPlot = doc.createElementNS(svgNS, "g");
        int spacing = 10;
    	
    	FHSeries s;
    	ArrayList<FHSeries> series_arr = f.getSeriesList();
    	for(int i = 0; i < series_arr.size(); i++) {
    	//for( FHSeries series : f.getSeriesList() ) {
    		Element series_line = doc.createElementNS(svgNS, "line");
    		series_line.setAttributeNS(null, "x1", "0");
    		series_line.setAttributeNS(null, "y1", Integer.toString(i*spacing) );
    		series_line.setAttributeNS(null, "x2", "200");
    		series_line.setAttributeNS(null, "y2", Integer.toString(i*spacing));
    		series_line.setAttributeNS(null, "stroke", "black");
    		series_line.setAttributeNS(null, "stroke-width", "1");	
    		chronologyPlot.appendChild(series_line);	
    	}
    	
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
