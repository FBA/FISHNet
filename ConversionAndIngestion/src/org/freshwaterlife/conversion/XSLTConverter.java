package org.freshwaterlife.conversion;

import javax.xml.transform.Templates;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.stream.StreamSource;
import org.jdom.transform.JDOMResult;
import org.jdom.transform.JDOMSource;
import org.jdom.Element;

import org.freshwaterlife.conversion.IXMLOutputSource;

/**
 *
 * @author pjohnson
 */
public class XSLTConverter implements IXMLOutputSource {

    private IXMLOutputSource oOutput;
    private String sXSLTFile;

    public XSLTConverter(IXMLOutputSource output, String xsltFile) {
	this.oOutput = output;
	this.sXSLTFile = xsltFile;
    }

    public void process(Element item) {
	this.process(item, "Library:");
    }

    public void process(Element item, String strPIDPrefix) {
	try {
	    System.setProperty("javax.xml.transform.TransformerFactory", "net.sf.saxon.TransformerFactoryImpl");
	    TransformerFactory e2 = TransformerFactory.newInstance();
	    Templates templates = e2.newTemplates(new StreamSource(this.sXSLTFile));
	    Transformer transformer = templates.newTransformer();
	    //Document tempDoc = new Document();
	    //tempDoc.setRootElement(item);
	    JDOMSource source = new JDOMSource(item);
	    JDOMResult result = new JDOMResult();
	    transformer.transform(source, result);
	    //System.out.println(result.getDocument().toString());
	    //System.out.println(((Element)result.getDocument().getRootElement()).getName());
	    if (result.getDocument().hasRootElement()) {
		System.out.println("sending to batch import");
		this.oOutput.process((Element) result.getDocument().getRootElement().clone(),strPIDPrefix);
	    } else {
		System.out.println("not sending to batch import");
	    }
	} catch (Exception ex) {
	    System.out.println("error: " + ex.getMessage() + item.getName());
	}
    }

    public void finished()
    {
	this.finished("Library:");
    }

    public void finished(String strPIDPrefix) {
	this.oOutput.finished();
    }
}
