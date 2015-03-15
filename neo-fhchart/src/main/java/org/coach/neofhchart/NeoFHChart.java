package org.coach.neofhchart;

import java.awt.*;
import java.awt.event.*;
import java.io.*;
import javax.swing.*;

// SVG imports
import org.apache.batik.swing.JSVGCanvas;
import org.apache.batik.swing.gvt.GVTTreeRendererAdapter;
import org.apache.batik.swing.gvt.GVTTreeRendererEvent;
import org.apache.batik.swing.svg.SVGDocumentLoaderAdapter;
import org.apache.batik.swing.svg.SVGDocumentLoaderEvent;
import org.apache.batik.swing.svg.GVTTreeBuilderAdapter;
import org.apache.batik.swing.svg.GVTTreeBuilderEvent;

import org.apache.batik.transcoder.TranscoderInput;
import org.apache.batik.transcoder.TranscoderOutput;
import org.apache.batik.transcoder.Transcoder;
import org.apache.batik.transcoder.image.PNGTranscoder;
import java.nio.file.Paths;
import java.nio.file.Path;
import org.apache.fop.svg.PDFTranscoder;

// XML imports
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
 
import org.w3c.dom.Attr;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

import org.w3c.dom.DOMImplementation;
import org.apache.batik.dom.svg.SVGDOMImplementation;

import org.fhaes.fhfilereader.*;

/**
 * Hello world!
 *
 */
public class NeoFHChart 
{
    // credits for this function goes to http://stackoverflow.com/questions/2325388/java-shortest-way-to-pretty-print-to-stdout-a-org-w3c-dom-document
    // no credit goes to Java for being excessively verbose
    /*    public static void printDocument(Document doc, OutputStream out) throws IOException, TransformerException {
        TransformerFactory tf = TransformerFactory.newInstance();
        Transformer transformer = tf.newTransformer();
        transformer.setOutputProperty(OutputKeys.OMIT_XML_DECLARATION, "no");
        transformer.setOutputProperty(OutputKeys.METHOD, "xml");
        transformer.setOutputProperty(OutputKeys.INDENT, "yes");
        transformer.setOutputProperty(OutputKeys.ENCODING, "UTF-8");
        transformer.setOutputProperty("{http://xml.apache.org/xslt}indent-amount", "4");

        transformer.transform(new DOMSource(doc), 
                              new StreamResult(new OutputStreamWriter(out, "UTF-8")));
                              }*/


    public static void main( String[] args )
    {
        JFrame f = new JFrame("NeoFHChart");
        NeoFHChart app = new NeoFHChart(f);
        f.getContentPane().add(app.createComponents());
        f.addWindowListener(new WindowAdapter() {
                public void windowClosing(WindowEvent e) {
                    System.exit(0);
                }
            });
        f.setSize(400, 400);
        f.setVisible(true);
        System.out.println( "Hello World!" );

    }

    public Document buildSVG(){
        DOMImplementation impl = SVGDOMImplementation.getDOMImplementation();
    	String svgNS = SVGDOMImplementation.SVG_NAMESPACE_URI;
    	Document doc = impl.createDocument(svgNS, "svg", null);

        Element svgRoot = doc.getDocumentElement();

        svgRoot.setAttributeNS(null, "width", "400");
    	svgRoot.setAttributeNS(null, "height", "450");

    	// create the rectangle
    	Element rectangle = doc.createElementNS(svgNS, "rect");
    	rectangle.setAttributeNS(null, "x", "10");
    	rectangle.setAttributeNS(null, "y", "20");
    	rectangle.setAttributeNS(null, "width", "100");
    	rectangle.setAttributeNS(null, "height", "50");
    	rectangle.setAttributeNS(null, "fill", "#FF9900");

    	// attach the rectangle to the svg root element
    	svgRoot.appendChild(rectangle);

        return doc;
    }

    JFrame frame;
    JButton load_b = new JButton("Load SVG");
    JButton export_pdf_b = new JButton("Export PDF");
    JButton export_png_b = new JButton("Export PNG");
    JSVGCanvas svgCanvas = new JSVGCanvas();

    public NeoFHChart(JFrame f) {
        frame = f;
    }

    public JComponent createComponents() {
        final JPanel panel = new JPanel(new BorderLayout());

        JPanel p = new JPanel(new FlowLayout(FlowLayout.LEFT));
        p.add(load_b);
        p.add(export_pdf_b);
        p.add(export_png_b);

        panel.add("North", p);
        panel.add("Center", svgCanvas);

        File f = new File("./uscbe001.fhx");

        // Set the button action.
        load_b.addActionListener(new ActionListener() {
                public void actionPerformed(ActionEvent ae) {
                    System.out.println("clicked");
                    svgCanvas.setDocument( buildSVG() );

                    JFileChooser fc = new JFileChooser(".");
                    int choice = fc.showOpenDialog(panel);
                    if (choice == JFileChooser.APPROVE_OPTION) {
                        File f = fc.getSelectedFile(); // TODO hard-coded for testing.
                        // File f = new File("./uscbe001.fhx");
                        FHX2FileReader fr = new FHX2FileReader(f);
                    }
                }
            });

        export_pdf_b.addActionListener(new ActionListener() {
                public void actionPerformed(ActionEvent ae) {

                    System.out.println("Exporting to PDF....");
                    try{
                        String svg_URI_input = Paths.get("SVG_logo.svg").toUri().toURL().toString();
                        TranscoderInput input_svg_image = new TranscoderInput(svg_URI_input);        
                        OutputStream pdf_ostream = new FileOutputStream("SVG_logo.pdf");
                        TranscoderOutput output_pdf_file = new TranscoderOutput(pdf_ostream);               
                        Transcoder transcoder = new PDFTranscoder();
                        transcoder.transcode(input_svg_image, output_pdf_file);
                        pdf_ostream.flush();
                        pdf_ostream.close();        
                    }
                    catch(Exception e){
                        // TODO real exception handling
                    }
                    System.out.println("Done.");
                }
            });

        export_png_b.addActionListener(new ActionListener() {
                public void actionPerformed(ActionEvent ae) {
                    System.out.println("Exporting to PNG....");
                    try {
                    PNGTranscoder t = new PNGTranscoder();
                    String svg_URI_input = Paths.get("SVG_logo.svg").toUri().toURL().toString();
                    TranscoderInput input = new TranscoderInput( svg_URI_input );
                    OutputStream png_ostream = new FileOutputStream("SVG_logo.png");
                    TranscoderOutput output = new TranscoderOutput(png_ostream);
                    t.transcode(input, output);
                    png_ostream.flush();
                    png_ostream.close();
                    }
                    catch(Exception e){
                        // TODO real exception handling
                    }
                    System.out.println("Done.");
                }
            });

        return panel;
    }
}

