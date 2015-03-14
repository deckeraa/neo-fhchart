package org.coach.neofhchart;

import java.awt.*;
import java.awt.event.*;
import java.io.*;
import javax.swing.*;

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
import java.nio.file.Paths;
import java.nio.file.Path;
import org.apache.fop.svg.PDFTranscoder;

/**
 * Hello world!
 *
 */
public class NeoFHChart 
{
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

    JFrame frame;
    JButton load_b = new JButton("Load SVG");
    JButton export_pdf_b = new JButton("Export PDF");
    JSVGCanvas svgCanvas = new JSVGCanvas();

    public NeoFHChart(JFrame f) {
        frame = f;
    }

    public JComponent createComponents() {
        final JPanel panel = new JPanel(new BorderLayout());

        JPanel p = new JPanel(new FlowLayout(FlowLayout.LEFT));
        p.add(load_b);
        p.add(export_pdf_b);

        panel.add("North", p);
        panel.add("Center", svgCanvas);

        // Set the button action.
        load_b.addActionListener(new ActionListener() {
                public void actionPerformed(ActionEvent ae) {
                    JFileChooser fc = new JFileChooser(".");
                    int choice = fc.showOpenDialog(panel);
                    if (choice == JFileChooser.APPROVE_OPTION) {
                        File f = fc.getSelectedFile();
                        try {
                            svgCanvas.setURI(f.toURL().toString());
                        } catch (IOException ex) {
                            ex.printStackTrace();
                        }
                    }
                }
            });

        export_pdf_b.addActionListener(new ActionListener() {
                public void actionPerformed(ActionEvent ae) {

                    System.out.println("Transcoding...");
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
                }
            });

        return panel;
    }
}

