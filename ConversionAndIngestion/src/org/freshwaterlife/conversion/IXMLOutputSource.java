package org.freshwaterlife.conversion;

import org.jdom.Element;

/**
 *
 * @author pjohnson
 */
public interface IXMLOutputSource {

    public void process(Element item);

    public void process(Element item, String strPIDPrefix);

    public void finished();

    public void finished(String strPIDPrefix);
    
}
