package org.freshwaterlife.portlets.dataset;

public class Global {

    //all, red, orange, green
    public static String[] arrCategories = {"", "red", "orange", "green", "reviewable", "redorange"};
    public static final int ALLDATASETS = 0;
    public static final int RED = 1;
    public static final int ORANGE = 2;
    public static final int GREEN = 3;
    public static final int NEEDREVIEWER = 4;
    public static final int REDORANGE = 5;
    public static final int RECOMMENDEDFORDOI = 6;
    public static final int iNumOfResultsPerPage = 20;
    public static final String strNoURLReturned = "nourl";
    public static final int iPageOffset = 2;
    public static final int iPageTotal = 5;
    public static final String strDatasetReviewerRole = "Dataset Reviewer";
    public static final String strDatasetEditorRole = "Dataset Editor";
    public static final String strAdministrator = "Administrator";
    public static final String NOREVIEWERTEXT = "noreviewer";
    public static final String datasetOutcomePath = "/mods:mods/mods:note[@type='admin' and ( text() = 'awaitingoutcome' or text() = 'approved' or text() = 'rejected' or text()='awaitingminting' or text()='doifailed' or text()='minted' or text()='awaitingmetadatconf' or text()='metadatafailed' or text()='metatdataattached' ) ]";
    public static final String datasetCategoryPath = "/mods:mods/mods:note[@type='admin' and ( text()='red' or text()='redorange' or text()='orange' or text()='green')]";
    // PREMIS event metadata
    public static final String PREMISPath = "premis:premis/premis:event";
    public static final String eventIdentifierPath = PREMISPath + "/premis:eventIdentifier";
    public static final String eventTypePath = PREMISPath + "/premis:eventType";
    public static final String eventDateTimePath = PREMISPath + "/premis:eventDateTime";
    public static final String eventDetailPath = PREMISPath + "/premis:eventDetail";
    public static final String eventOutcomePath = PREMISPath + "/premis:eventOutcomeInformation/premis:eventOutcome";
    public static final String eventOutcomeDetailPath = PREMISPath + "/premis:eventOutcomeInformation/premis:eventOutcomeDetail";
    public static final String PREMISEventPath = "foxml:digitalObject/foxml:datastream[@ID='PREMIS']/foxml:datastreamVersion[last()]/foxml:xmlContent/premis:premis";
    public static final String PREMISDatastreamVersionPath = "foxml:digitalObject/foxml:datastream[@ID='PREMIS']/foxml:datastreamVersion[last()]";
    public static final String linkingObjectIdentifierPath = PREMISPath + "/premis:linkingObjectIdentifier";
    public static final String linkingAgentIdentifierPath = PREMISPath + "/premis:linkingAgentIdentifier[last()]";
    public static final String agentPath = PREMISPath + "/premis:linkingAgentIdentifier[premis:linkingAgentIdentifierType/text() = 'Agent']/premis:linkingAgentIdentifierValue";
    public static final String fullNamePath = PREMISPath + "/premis:linkingAgentIdentifier[premis:linkingAgentIdentifierType/text() = 'Full Name']/premis:linkingAgentIdentifierValue";
    public static final String strSimpleDateFormat = "yyyy-MM-dd'T'HH:mm:ss'.000Z'";
}
