<?xml version="1.0" encoding="UTF-8"?>
<!-- 
    Document   : SearchResults
    Created on : 27-Oct-2009, 16:20:59
    Author     : mhaft
-->
<jsp:root version="2.1" xmlns:f="http://java.sun.com/jsf/core" xmlns:h="http://java.sun.com/jsf/html" xmlns:jsp="http://java.sun.com/JSP/Page" xmlns:webuijsf="http://www.sun.com/webui/webuijsf">
    <jsp:directive.page contentType="text/html;charset=UTF-8" pageEncoding="UTF-8"/>
    <f:view>
        <webuijsf:page id="page1">
            <webuijsf:html id="html1">
                <webuijsf:head id="head1">
                    <webuijsf:link id="link1" url="/resources/stylesheet.css"/>
                </webuijsf:head>
                <webuijsf:body id="body1" style="-rave-layout: grid">
                    <webuijsf:form id="form1">
                        <h:dataTable value="#{SearchResults.images}" var="image">
                            <h:column>
                                <h:panelGrid columns="1">
                                    <h:graphicImage value="#{image.thumbUrl}"/>
                                    <h:outputText escape="false" value="#{image.htmlTitle}"/>
                                </h:panelGrid>
                            </h:column>
                        </h:dataTable>
                        <webuijsf:tabSet id="tabSet1" selected="tab3" style="position: absolute; left: 288px; top: 120px">
                            <webuijsf:tab id="tab1" text="Tab 1">
                                <webuijsf:panelLayout id="layoutPanel1" style="width: 100%; height: 128px;"/>
                                <h:dataTable value="#{SearchResults.images}" var="image">
                                    <h:column>
                                        <h:panelGrid columns="1">
                                            <h:graphicImage value="#{image.thumbUrl}"/>
                                            <h:outputText escape="false" value="#{image.htmlTitle}"/>
                                        </h:panelGrid>
                                    </h:column>
                                </h:dataTable>
                            </webuijsf:tab>
                            <webuijsf:tab id="tab2" text="Tab 2">
                                <webuijsf:panelLayout id="layoutPanel2" style="width: 100%; height: 128px;"/>
                            </webuijsf:tab>
                            <webuijsf:tab id="tab3" text="Tab 3">
                                <webuijsf:panelLayout id="layoutPanel3" style="width: 100%; height: 128px;"/>
                            </webuijsf:tab>
                        </webuijsf:tabSet>
                    </webuijsf:form>
                </webuijsf:body>
            </webuijsf:html>
        </webuijsf:page>
    </f:view>
</jsp:root>
