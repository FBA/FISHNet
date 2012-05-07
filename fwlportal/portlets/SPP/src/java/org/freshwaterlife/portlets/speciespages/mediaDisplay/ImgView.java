/*
 * SppTree_view.java
 *
 * Created on 27-Jan-2009, 12:15:22
 * Copyright kmcnicol
 */
package org.freshwaterlife.portlets.speciespages.mediaDisplay;

import com.sun.rave.web.ui.appbase.AbstractPageBean;
import javax.faces.FacesException;
import org.freshwaterlife.portlets.speciespages.Coordinator;

/**
 * <p>Page bean that corresponds to a similarly named JSPX page.  This
 * class contains component definitions (and initialization code) for
 * all components that you have defined on this page, as well as
 * lifecycle methods and event handlers where you may add behavior
 * to respond to incoming events.</p>
 */
public class ImgView extends AbstractPageBean {
    // <editor-fold defaultstate="collapsed" desc="Managed Component Definition">

    private int __placeholder;
    // </editor-fold>

    //JSF managed attribute bean
    private Coordinator coordinator;
    private String imageStr;

    /**
     * <p>Automatically managed component initialization.  <strong>WARNING:</strong>
     * This method is automatically generated, so any user-specified code inserted
     * here is subject to being replaced.</p>
     */
    private void _init() throws Exception {
    }

    /**
     * <p>Construct a new Page bean instance.</p>
     */
    public ImgView() {
    }

    /**
     * <p>Callback method that is called whenever a page is navigated to,
     * either directly via a URL, or indirectly via page navigation.
     * Customize this method to acquire resources that will be needed
     * for event handlers and lifecycle methods, whether or not this
     * page is performing post back processing.</p>
     *
     * <p>Note that, if the current request is a postback, the property
     * values of the components do <strong>not</strong> represent any
     * values submitted with this request.  Instead, they represent the
     * property values that were saved for this view when it was rendered.</p>
     */
    @Override
    public void init() {
        // Perform initializations inherited from our superclass
        super.init();
        // Perform application initialization that must complete
        // *before* managed components are initialized
        // TODO - add your own initialiation code here

        // <editor-fold defaultstate="collapsed" desc="Managed Component Initialization">
        // Initialize automatically managed components
        // *Note* - this logic should NOT be modified
        try {
            _init();
        } catch (Exception e) {
            log("SppTree_view Initialization Failure", e);
            throw e instanceof FacesException ? (FacesException) e : new FacesException(e);
        }

    // </editor-fold>
    // Perform application initialization that must complete
    // *after* managed components are initialized
    // TODO - add your own initialization code here

    }

    /**
     * <p>Callback method that is called after the component tree has been
     * restored, but before any event processing takes place.  This method
     * will <strong>only</strong> be called on a postback request that
     * is processing a form submit.  Customize this method to allocate
     * resources that will be required in your event handlers.</p>
     */
    @Override
    public void preprocess() {
    }

    /**
     * <p>Callback method that is called just before rendering takes place.
     * This method will <strong>only</strong> be called for the page that
     * will actually be rendered (and not, for example, on a page that
     * handled a postback and then navigated to a different page).  Customize
     * this method to allocate resources that will be required for rendering
     * this page.</p>
     */
    @Override
    public void prerender() {
        
    }

    /**
     * <p>Callback method that is called after rendering is completed for
     * this request, if <code>init()</code> was called (regardless of whether
     * or not this was the page that was actually rendered).  Customize this
     * method to release resources acquired in the <code>init()</code>,
     * <code>preprocess()</code>, or <code>prerender()</code> methods (or
     * acquired during execution of an event handler).</p>
     */
    @Override
    public void destroy() {
    }

    /**
     * @return the imageStr
     */
    public String getImageStr() {
        imageStr = "images will go here";
        String srchStr = coordinator.getSelectedTaxon().getPrefSciName();
        if (srchStr == null) srchStr = "no search string given";
        FwLImageArchiveFetch images = new FwLImageArchiveFetch(srchStr);
        try {
            imageStr = images.getImages();
        } catch (Exception ex) {
            imageStr = "We apologise but there appears to have been an error retrieving the images.  If you feel able, please send the following message to <a href='mailto:info@freshwaterlife.org'>info@freshwaterlife.org</a> :<br/><br/><br/>" + ex;
        }
        return imageStr;
    }

    /**
     * @param imageStr the imageStr to set
     */
    public void setImageStr(String imageStr) {
        this.imageStr = imageStr;
    }

    /**
     * @return the coordinator
     */
    public Coordinator getCoordinator() {
        return coordinator;
    }

    /**
     * @param coordinator the coordinator to set
     */
    public void setCoordinator(Coordinator coordinator) {
        this.coordinator = coordinator;
    }
}

