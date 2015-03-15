import java.io.*;
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
            t.transform(new DOMSource(doc), new StreamResult(new OutputStreamWriter(out, "UTF-8")));
        } catch (Exception ex) {
            System.out.println("Error: Could not printDocument\n");
        }
    }
    
    public static Document buildSVGFromFile(AbstractFireHistoryReader f){
        return null;
    };
}
