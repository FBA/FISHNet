package org.freshwaterlife.util;

import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import org.jdom.Attribute;
import org.jdom.Document;
import org.jdom.Element;
import org.jdom.JDOMException;
import org.jdom.Namespace;
import org.jdom.xpath.XPath;

public class XPathNodeUpdater {

    private Element node;

    public XPathNodeUpdater(Document doc, String path, Map<String, Namespace> ns) throws JDOMException {
	//System.out.println("Root Node: " + doc.getRootElement().getName());
	XPath xpath = XPath.newInstance(path);

	// Add Namespaces
	Namespace n;
	Collection<Namespace> c = ns.values();

	Iterator<Namespace> it = c.iterator();
	while (it.hasNext()) {
	    n = (Namespace) it.next();
	    xpath.addNamespace(n);
	}

	this.node = (Element) xpath.selectSingleNode(doc);
    }

    public XPathNodeUpdater(Document doc, String path) throws JDOMException {
	this(doc, path, new HashMap<String, Namespace>());
    }

    public void setContent(String content) throws JDOMException {
	try {
	    //System.out.println("Setting Content");
	    //System.out.println("Setting Content" + this.node.getName());
	    //System.out.println("Content Before: " + this.node.getText());

	    this.node.setText(content);
			System.out.println("Set Content, Element Name: " + this.node.getName() + ", Content: " + content);

	    //System.out.println("Content After: " + this.node.getText());
	} catch (Exception e) {
			System.out.println("Error XPath setContent: " + e.getMessage() + ", Content: " + content);
	}
    }

    public void addChildItem(Element child) throws JDOMException {
	try {
	    //System.out.println("Adding Child Item");
	    //System.out.println("Adding Child Item to " + this.node.getName());

	    this.node.addContent(child);
			System.out.println("Added Child, Element Name: " + this.node.getName() + ", Child Name: " + child.getName());

	} catch (Exception e) {
			System.out.println("Error XPath addChildItem: " + e.getMessage() + ", Element Name: " + child.getName());
	}
    }

    public void addAttribute(String name, String content) throws JDOMException {
	try {
	    //System.out.println("Adding Attribute");
	    //System.out.println("Adding Attribute to " + this.node.getName());

	    this.node.setAttribute(name, content);

			System.out.println("Added Attribute, Element Name: " + this.node.getName() + ", Attribute Name: " + name);

	    //System.out.println("New Attribute: " + this.node.getAttribute(name).getValue());
	} catch (Exception e) {
			System.out.println("Error XPath: " + e.getMessage() + ", Element Name: " + name + ", Content: " + content);
	}
    }

    public void addAttribute(String name, String content, Namespace namespace) {

	Attribute att = new Attribute(name, content, namespace);

	this.node.setAttribute(att);
		System.out.println("Added Attribute, Element Name: " + this.node.getName() + ", Attribute Name: " + name);

	//System.out.println("New Attribute: " + this.node.getAttribute(name, namespace).getValue());
    }

    public static XPathNodeUpdater create(Document doc, String path, Map<String, Namespace> ns) throws JDOMException {
	return new XPathNodeUpdater(doc, path, ns);
    }

    public static XPathNodeUpdater create(Document doc, String path) throws JDOMException {
	return new XPathNodeUpdater(doc, path);
    }

    public Element getNode() {
	return this.node;
    }
}
