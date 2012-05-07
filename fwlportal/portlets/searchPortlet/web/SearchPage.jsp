<?xml version="1.0" encoding="UTF-8"?>
<!-- 
    Document   : SearchPage
    Created on : 23-Mar-2010, 20:51:42
    Author     : root
-->
<jsp:root version="2.1" xmlns="http://www.w3.org/1999/xhtml" xmlns:c="http://java.sun.com/jsp/jstl/core" xmlns:f="http://java.sun.com/jsf/core"
          xmlns:h="http://java.sun.com/jsf/html" xmlns:jsp="http://java.sun.com/JSP/Page" xmlns:p="http://java.sun.com/jsf/portlet/components"
          xmlns:ui="http://java.sun.com/jsf/facelets" xmlns:webuijsf="http://www.sun.com/webui/webuijsf">
    <jsp:directive.page contentType="text/html;charset=UTF-8" pageEncoding="UTF-8"/>
    <f:view>
        <p:portletPage>
            <c:set value="#{SearchPage.solrCss}" var="solrCss"/>
            <link href="${solrCss}" rel="stylesheet" type="text/css"/>
            <webuijsf:themeLinks binding="#{SearchPage.themeLinks1}" id="themeLinks1" parseOnLoad="false" webuiAll="false"/>
            <div style="height: 100%; position: relative; text-align: center; vertical-align: middle; width: 100%">
                <webuijsf:form binding="#{SearchPage.searchForm}" id="searchForm" style="text-align: center; vertical-align: middle">
                    <webuijsf:image alt="FreshwaterLife" id="fwlLogo" toolTip="FreshwaterLife logo" url="/resources/loulogosmaller.jpg"/>
                    <webuijsf:textField id="q" text="#{SessionBean1.qValue}"/>
                    <webuijsf:button actionExpression="#{SearchPage.submit_action}" id="submit" text="Search"/>
                    <br/>
                    <c:forEach items="#{SearchPage.searchResults}" var="result">
                        <div>
                            <c:if test="${not empty result.thumbUrl}" var="test">
                                <webuijsf:imageHyperlink imageURL="#{result.thumbUrl}" url="#{result.forwardUrl}"/>
                            </c:if>
                        </div>
                    </c:forEach>
                    <webuijsf:messageGroup id="messageGroup1"/>
                </webuijsf:form>
            </div>
        </p:portletPage>
    </f:view>
</jsp:root>
