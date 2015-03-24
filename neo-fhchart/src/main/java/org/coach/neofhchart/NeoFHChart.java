package org.coach.neofhchart;

/*************************************************************************
 * NeoFHChart
 * This class creates fire history charts using the 
 * AbstractFireHistoryReader in FHUtil.
 * Graphing is done by having FireChartSVG generate an SVG,
 * represented as a org.w3c.dom.Document.
 * The Batik library has an Apache v2.0 license, which is compatible with
 * GPLv3 (https://www.apache.org/licenses/GPL-compatibility.html).
 * @author Aaron Decker
 ************************************************************************/

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
import org.w3c.dom.Attr;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

import org.w3c.dom.DOMImplementation;
import org.apache.batik.dom.svg.SVGDOMImplementation;

import org.fhaes.fhfilereader.*;

public class NeoFHChart 
{
    // TODO EF3: This needs to be reimplemented as a "JPanel that can be incorporated into other interfaces".
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
        f.setSize(800, 750);
        f.setVisible(true);
        System.out.println( "Hello World!" );

    }

    JFrame frame;
    JButton load_b = new JButton("Load SVG");
    JButton export_pdf_b = new JButton("Export PDF");
    JButton export_png_b = new JButton("Export PNG");
    JButton hide_chron_b = new JButton("Hide Chron Plot");
    JSVGCanvas svgCanvas = new JSVGCanvas();
    FireChartSVG chart;

    public NeoFHChart(JFrame f) {
        frame = f;
    }

    public ButtonGroup createCompositePlotOptions(JPanel p) {
    	ActionListener refresh = new ActionListener() { 
        	public void actionPerformed(ActionEvent e) {
        		if( e.getActionCommand() == "FIRE" ) {
        			System.out.println("FIRE");
        		}
        	}
        };
    	
    	JRadioButton fireOnlyEvents = new JRadioButton("Fire Events Only");
    	fireOnlyEvents.setActionCommand("FIRE");
    	fireOnlyEvents.addActionListener(refresh);
    		
        JRadioButton injuryOnlyEvents = new JRadioButton("Injury Events Only");
        injuryOnlyEvents.setActionCommand("INJURY");
        
        JRadioButton fireAndInjuryEvents = new JRadioButton("Fire and Injury Events");
        fireAndInjuryEvents.setActionCommand("BOTH");
        
        ButtonGroup composite_group = new ButtonGroup();
        composite_group.add(fireOnlyEvents);
        composite_group.add(injuryOnlyEvents);
        composite_group.add(fireAndInjuryEvents);
        
        p.add(fireOnlyEvents);
        p.add(injuryOnlyEvents);
        p.add(fireAndInjuryEvents);
        
        
        return composite_group;
    }
    
    public JComponent createComponents() {
        final JPanel panel = new JPanel(new BorderLayout());

        JPanel p = new JPanel(new FlowLayout(FlowLayout.LEFT));

        JPanel composite_panel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        ButtonGroup composite_group = createCompositePlotOptions(composite_panel);
        
        p.add(load_b);
        p.add(export_pdf_b);
        p.add(export_png_b);
        p.add(hide_chron_b);
        p.add(composite_panel);
        
        panel.add("North", p);
        panel.add("Center", svgCanvas);
//        panel.add("South", composite_group);

        // TODO remove this code.
        // It is only here to save keystrokes while testing
        File f = new File("./samples/uscbe001.fhx");
        FHX2FileReader fr = new FHX2FileReader(f);
        chart = new FireChartSVG(fr);
        //chart.print();
        chart.dumpDocument();

        svgCanvas.setDocumentState(JSVGCanvas.ALWAYS_DYNAMIC);
        svgCanvas.setEnableImageZoomInteractor(true);
        svgCanvas.setEnablePanInteractor(true);
        svgCanvas.addSVGDocumentLoaderListener(new SVGDocumentLoaderAdapter() {
            public void documentLoadingStarted(SVGDocumentLoaderEvent e) {
            	System.out.println("loading started");
            }
            public void documentLoadingCompleted(SVGDocumentLoaderEvent e) {
            	System.out.println("loading completed");
            }
        });
        
        svgCanvas.setDocument(chart.doc);

        
        // Brings up a file chooser dialog to select an input file
        load_b.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent ae) {
				JFileChooser fc = new JFileChooser(".");
				int choice = fc.showOpenDialog(panel);
				if (choice == JFileChooser.APPROVE_OPTION) {
					File f = fc.getSelectedFile(); 
					FHX2FileReader fr = new FHX2FileReader(f);
			        chart = new FireChartSVG(fr);
			        //chart.print();
			        chart.dumpDocument();
					svgCanvas.setDocument(chart.doc);
				}
			}
            });

	// PDF Export
	// TODO EF5: The pdf export does not currently export the entire document.
        export_pdf_b.addActionListener(new ActionListener() {
                public void actionPerformed(ActionEvent ae) {

                    System.out.println("Exporting to PDF....");
                    try{
                    	TranscoderInput input_svg_image = new TranscoderInput(chart.doc);
                        OutputStream pdf_ostream = new FileOutputStream(chart.getName()+".pdf");
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

	// PNG Export
	// TODO EF5: The png export does not currently work at all
        export_png_b.addActionListener(new ActionListener() {
                public void actionPerformed(ActionEvent ae) {
                    System.out.println("Exporting to PNG....");
                    try {
                    	PNGTranscoder t = new PNGTranscoder();
                    	TranscoderInput input = new TranscoderInput( chart.doc );
                    	OutputStream png_ostream = new FileOutputStream(chart.getName()+".png");
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

	// toggle button for showing/hiding the chronology plot
        hide_chron_b.addActionListener(new ActionListener() {
                public void actionPerformed(ActionEvent ae) {
                    chart.toggleChronologyPlotVisibility();
                    svgCanvas.invalidate(); // TODO this doesn't seem to cause a redraw
                }
            });

        return panel;
    }
}

