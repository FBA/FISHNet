<%@page contentType="text/html"%>
<%@page pageEncoding="UTF-8"%>


<!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN"
    "http://www.w3.org/TR/html4/loose.dtd">
<html>
    <head>

        <!-- find the right starting point for links, ajax requests, etc. -->
        <%
            String baseUrl = request.getScheme() + "://" + request.getServerName() + ":" + request.getServerPort() + "/webservices/";
        %>
        <script type="text/javascript">var baseUrl_js = "<%=baseUrl%>";
            //share this with javascript functions</script>

        <!-- load's KMN's ajax stuff - handle's nav list drop-down -->
        <script type="text/javascript" src="<%=baseUrl%>js/navTreeAjax_kmn.js"></script>

        <!-- load KMN's page onLoad event handling functions - particularly
        important in a portal where the portal might have various things attached
        to the onLoad event -->
        <script type="text/javascript" src="<%=baseUrl%>js/loadEvents.js"></script>

        <!-- dhtmlx stuff - for the tree -->
        <link rel="STYLESHEET" type="text/css" href="<%=baseUrl%>js/dhtmlx/dhtmlx.css">
        <script src="<%=baseUrl%>js/dhtmlx/dhtmlx.js" type="text/javascript"></script>

        <script type="text/javascript">
            function buildInterface(){
                //entire code for interface init will go here
            }
            dhtmlxEvent(window,"load", buildInterface);
        </script>

        <!-- functions that should be called immediately on Page Load completion - using addLoadEvent from loadEvents.js-->

        <meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
        <title>FwL Spp Navigation Services</title>
    </head>
    <body>
        <noscript>
            <font color="red"><strong>You have javascript (EMCEA script)
                    disabled.  We apologise but this application will not work
                    with your current settings.</strong></font>
        </noscript>

        <h2>Species Navigation Service</h2>
        <p>/SppNav</p>

        <form action="#" name="navigationTree">
            <p>Drop down list populated by the list navigation trees service /SppNav/trees:</p>

            <select id="treeSelector" onchange="changeTree(this);">
                <option>Select navigation list</option>
            </select>
            <div id="test">this text will be replaced by the name of the tree in the drop-down</div>
            <div id="selectedNode">this text will be replaced by the selection made in the tree</div>
            <div id="sppTreeDiv">
                Tree populated dynamically as expanded using the tree node service /SppNav/tree/#tree_name#/#node_id#
            </div>

        </form>
        <br/>
        Update this tree to reflect changes: <%=baseUrl%>SppNav/<script>document.write(selectedTree);</script>/update
        <hr/>
        <%= baseUrl%>
        <br/>
        <%= new java.util.Date()%>


        <!-- functions that need to be called once the relevant page elements
        have loaded - might use the addLoadEvent is loadEvent.js for this -->

        <!-- populate the tree selection drop down -->
        <script type="text/javascript">requestLists('GET', '<%=baseUrl%>SppNav/trees')</script>

        <!-- these scripts are used by the dhtmlx tree to initialise -->
        <script type="text/javascript">
            initialiseTree_kmn();
        </script>


    </body>
</html>