<%--
   DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
  
   Copyright (c) 2005 Sun Microsystems Inc. All Rights Reserved
  
   The contents of this file are subject to the terms
   of the Common Development and Distribution License
   (the License). You may not use this file except in
   compliance with the License.
                                                                                
   You can obtain a copy of the License at
   https://opensso.dev.java.net/public/CDDLv1.0.html or
   opensso/legal/CDDLv1.0.txt
   See the License for the specific language governing
   permission and limitations under the License.
                                                                                
   When distributing Covered Code, include this CDDL
   Header Notice in each file and include the License file
   at opensso/legal/CDDLv1.0.txt.
   If applicable, add the following below the CDDL Header,
   with the fields enclosed by brackets [] replaced by
   your own identifying information:
   "Portions Copyrighted [year] [name of copyright owner]"
                                                                                
   $Id: login_failed_template.jsp,v 1.5 2008/08/15 01:05:29 veiming Exp $
                                                                                
--%>




<html>

<%@page info="Authentication Failed" language="java"%>
<%@taglib uri="/WEB-INF/jato.tld" prefix="jato"%>
<%@taglib uri="/WEB-INF/auth.tld" prefix="auth"%>
<jato:useViewBean className="com.sun.identity.authentication.UI.LoginViewBean">


<%@ page contentType="text/html" %>

<head>
<title>Authentication Failed - FreshwaterLife</title>

<% 
String ServiceURI = (String) viewBean.getDisplayFieldValue(viewBean.SERVICE_URI);
%>

<link rel="stylesheet" href="<%= ServiceURI %>/css/styles.css" type="text/css" />
<script language="JavaScript" src="<%= ServiceURI %>/js/browserVersion.js"></script>
<script language="JavaScript" src="<%= ServiceURI %>/js/auth.js"></script>

<script language="javascript">
    writeCSS('<%= ServiceURI %>');
</script>
<script type="text/javascript"><!--// Empty script so IE5.0 Windows will draw table and button borders
//-->
</script>

<link rel="stylesheet" type="text/css" href="/opensso/css/theme.css"/>
<script language="JavaScript" src="/opensso/js/jquery-1.4.2.js"></script>
<script language="JavaScript" src="/opensso/js/jquery.kwicks-1.5.1.js"></script>
<script language="JavaScript" src="/opensso/js/custom.js"></script>

<style type="text/css">
.content {
	width: 700px;
}

.gallery-thumb {
	width: 180px;
	height: 180px;
}

.gallery-album {
	height: 210px;
}

#heading .logo {
	background:url(../images/fwl/company_logo.png) no-repeat;
	display:block;
	font-size:0;
	height:200px;
	text-indent:-9999em;
	width:800px;
}

/* GLOBAL STYLES */
BODY, TH, TD, P, DIV, SPAN, INPUT, SELECT, TEXTAREA, FORM, B, STRONG, I, U, H1, H2, H3, H4, H5, H6, 
	DL, DD, DT, UL, LI, OL, OPTION, OPTGROUP, A {font-family:verdana;}

</style>

</head>

<body class="gallery">

	<div id="gallery" class="gecko">
		<div id="wrapper">
			<div id="fwl">
				<div id="banner">
					<div id="powered-by">
						<div id="topbarphrase">Website powered by:</div>
						<div id="FwLLogo"><span class="fba">FreshwaterLife</span></div>
					</div>
					<div id="masthead">
						<div id="FwLMasthead">
							<ul id="topLinks">
								<li><a href="http://new.freshwaterlife.org/web/guest">Home</a></li>
								<li><a href="http://new.freshwaterlife.org/web/fwl">About us</a></li>
								<li><a href="http://new.freshwaterlife.org/web/fwl/community">Community</a></li>
								<li><a href="http://new.freshwaterlife.org/web/fwl/contact-us">Contact Us</a></li>
								<li><a href="http://new.freshwaterlife.org/web/fwl/help">Help</a></li>
							</ul>
						</div>
					</div>
				</div>
				<div style="clear: both;"></div>
				<div id="heading">				

					<div id="headerContent">
						<div id="company-logo">
							<h1 id="header1">
								<span class="logo"></span>
							</h1>
						</div>
						<div id="bookcase">
							<script src="jquery-1.4.2.js"></script>
							<script src="jquery.kwicks-1.5.1.js"></script>
							<script src="custom.js"></script>
							<ul class="kwicks">
								<li class="Beginners"><a href="http://new.freshwaterlife.org/web/guest/beginner-s-guide"></a></li>
								<li class="Species"><a href="http://new.freshwaterlife.org/web/guest/species"></a></li>
								<li class="Habitats"><a href="http://new.freshwaterlife.org/web/guest/habitats"></a></li>
								<li class="Identification"><a href="http://new.freshwaterlife.org/web/guest/identification"></a></li>
								<li class="Images"><a href="http://www.freshwaterlife.org/imagearchive"></a></li>
								<li class="Education"><a href="http://www.freshwaterlife.org/id/2873"></a></li>
								<li class="Science"><a href="http://www.freshwaterlife.org/id/106173"></a></li>
								<li class="Processes"><a href="http://www.freshwaterlife.org/id/2870"></a></li>
								<li class="Management"><a href="http://www.freshwaterlife.org/id/106177"></a></li>
								<li class="Fisheries"><a href="http://www.freshwaterlife.org/id/17763"></a></li>
								<li class="Impacts"><a href="http://www.freshwaterlife.org/id/2875"></a></li>
							</ul>
		
						</div>
					</div>
				</div>
			</div><!-- /fwl -->
			<div id="gc"><!-- Extra div to apply gallery styles -->
				<div id="sectionHeader">
					<div class="menu">
						<ul>
							<li class="selected"><a id="homeselect"
								href="http://new.freshwaterlife.org/home">Home</a> <!--[if lte IE 6]>
						
						<a href="http://new.freshwaterlife.org/home">Home
						
						<table><tr><td>
						
						<![endif]--></li>
							<li><a class="hide"
								href="http://new.freshwaterlife.org/beginner-s-guide">Beginner's
							Guide</a> <!--[if lte IE 6]>
						
						<a href="http://new.freshwaterlife.org/beginner-s-guide">Beginner's Guide
						
						<table><tr><td>
						
						<![endif]-->
							<ul>
								<li><a
									href="http://new.freshwaterlife.org/wheretofindfreshwater">Where
								to find and what lives in freshwater</a></li>
								<li><a
									href="http://new.freshwaterlife.org/the-importance-of-fresh-waters">The
								importance of freshwater</a></li>
								<li><a
									href="http://new.freshwaterlife.org/learn-more-about-freshwater">Learn
								more about freshwater</a></li>
							</ul>
							<!--[if lte IE 6]>
						
						</td></tr></table>
						
						</a>
						
						<![endif]--></li>
							<li><a class="hide"
								href="http://new.freshwaterlife.org/species">Species</a> <!--[if lte IE 6]>
						
						<a href="http://new.freshwaterlife.org/species">Species
						
						<table><tr><td>
						
						<![endif]-->
							<ul>
								<li><a href="http://www.freshwaterlife.org/id/2907">Invasive
								Species</a></li>
								<li><a href="http://www.freshwaterlife.org/id/106241">Species
								Conservation</a></li>
								<li><a href="http://new.freshwaterlife.org/web/guest/fish">Fish</a></li>
							</ul>
							<!--[if lte IE 6]>
						
						</td></tr></table>
						
						</a>
						
						<![endif]--></li>
							<li><a class="hide"
								href="http://new.freshwaterlife.org/habitats">Habitats</a> <!--[if lte IE 6]>
						
						<a href="http://new.freshwaterlife.org/web/guest/habitats">Habitats
						
						<table><tr><td>
						
						<![endif]-->
							<ul>
								<li><a
									href="http://new.freshwaterlife.org/aquifer-fed-naturally-fluctuating-water-bodies">Aquifer
								fed naturally fluctuating water bodies</a></li>
								<li><a
									href="http://new.freshwaterlife.org/blanket-bog1">Blanket
								bog</a></li>							
								<li><a href="http://new.freshwaterlife.org/estuaries">Estuaries</a></li>
								<li><a
									href="http://new.freshwaterlife.org/eutrophic-standing-waters">Eutrophic
								standing waters</a></li>
								<li><a
									href="http://new.freshwaterlife.org/web/guest/intertidal-mudflats">Intertidal
								mudflats</a></li>
								<li><a href="http://new.freshwaterlife.org/lakes">Lakes</a></li>
								<li><a
									href="http://new.freshwaterlife.org/lowland-fens">Lowland
								fens</a></li>
								<li><a
									href="http://new.freshwaterlife.org/lowland-raised-bog">Lowland
								raised bog</a></li>
								<li><a href="http://new.freshwaterlife.org/reedbeds">Reedbeds</a></li>
								<li><a href="http://new.freshwaterlife.org/reservoirs">Resevoirs</a></li>
								<li><a href="http://new.freshwaterlife.org/rivers">Rivers</a></li>
								<li><a
									href="http://new.freshwaterlife.org/temporary-water-bodies">Temporary
								water bodies</a></li>
								<li><a href="http://new.freshwaterlife.org/wetlands">Wetlands</a></li>
							</ul>
							<!--[if lte IE 6]>
						
						</td></tr></table>
						
						</a>
						
						<![endif]--></li>
							<li><a class="hide"
								href="http://new.freshwaterlife.org/identification">Identification</a>
							<!--[if lte IE 6]>
						
						<a href="http://new.freshwaterlife.org/identification">Identification
						
						<table><tr><td>
						
						<![endif]-->
							
							</li>
							<li><a class="hide" id="imageselect" href="http://www.freshwaterlife.org/imagearchive">Images</a></li>
							<!--[if lte IE 6]>
						
						<a href="http://www.freshwaterlife.org/imagearchive">Images
						
						<table><tr><td>
						
						<![endif]-->
							<!--[if lte IE 6]>
						
						</td></tr></table>
						
						</a>
						
						<![endif]-->
						
							<li><a class="hide" href="http://www.freshwaterlife.org/id/2873">Learning
							Resources</a> <!--[if lte IE 6]>
						
						<a href="http://www.freshwaterlife.org/id/2873">Learning Resources
						
						<table><tr><td>
						
						<![endif]--> <!--[if lte IE 6]>
						
						</td></tr></table>
						
						</a>
						
						<![endif]--></li>
							<li><a class="hide"
								href="http://new.freshwaterlife.org/science">Science</a> <!--[if lte IE 6]>
						
						<a href="http://new.freshwaterlife.org/science">Science
						
						<table><tr><td>
						
						<![endif]--> <!--[if lte IE 6]>
						
						</td></tr></table>
						
						</a>
						
						<![endif]--></li>
							<li><a class="hide"
								href="http://new.freshwaterlife.org/processes">Processes</a>
							<!--[if lte IE 6]>
						
						<a href="http://new.freshwaterlife.org/processes">Processes
						
						<table><tr><td>
						
						<![endif]--> <!--[if lte IE 6]>
						
						</td></tr></table>
						
						</a>
						
						<![endif]--></li>
							<li><a class="hide"
								href="http://new.freshwaterlife.org/management">Management</a>
							<!--[if lte IE 6]>
						
						<a href="http://new.freshwaterlife.org/management">Management
						
						<table><tr><td>
						
						<![endif]--> <!--[if lte IE 6]>
						
						</td></tr></table>
						
						</a>
						
						<![endif]--></li>
							<li><a class="hide"
								href="http://new.freshwaterlife.org/fisheries">Fisheries</a>
							<!--[if lte IE 6]>
						
						<a href="http://new.freshwaterlife.org/fisheries">Fisheries
						
						<table><tr><td>
						
						<![endif]--> <!--[if lte IE 6]>
						
						</td></tr></table>
						
						</a>
						
						<![endif]--></li>
							<li><a class="hide"
								href="http://new.freshwaterlife.org/impacts">Impacts</a> <!--[if lte IE 6]>
						
						<a href="http://new.freshwaterlife.org/impacts">Impacts
						
						<table><tr><td>
						
						<![endif]--> <!--[if lte IE 6]>
						
						</td></tr></table>
						
						</a>
						
						<![endif]--></li>
						</ul>
					</div><!-- /menu -->
				</div><!-- /sectionHeader -->

				<div id="content">	
				  <table border="0" cellpadding="0" cellspacing="0" align="center" title="">				    
				      	<tr>
							<td colspan="2"><img src="<%= ServiceURI %>/images/dot.gif"
								width="1" height="15" alt="" /></td>
						</tr>
						<tr>
							<td width="395" bgcolor="#ffffff" valign="top"><img
								src="<%= ServiceURI %>/images/dot.gif" width="30" height="1" alt="" /><img
								name="Login.productLogo"
								src="<%= ServiceURI %>/images/PrimaryProductName.png"
								alt="<auth:resBundle bundleName="amAuthUI" resourceKey="basic_realm" />"
								border="0" align="left"/>
							<table border="0" cellspacing="0" cellpadding="0">
				                <tr>
				                  <td colspan="2">
				                      <img src="<%= ServiceURI %>/images/dot.gif" width="1" height="25" alt="" />                                
				                  </td>
				                </tr>            
				                <tr>
				                  <td>&nbsp;</td>
				                  <td><div class="logErr"><table align="center" border="0" cellpadding="0" cellspacing="0" 
				                    class="AlrtTbl" title="">
				                <tr>
				                <td valign="middle">
				                <div class="AlrtErrTxt"> 
				                <img name="Login.AlertImage" src="<%= ServiceURI %>/images/error.png" alt="Error" 
				                height="21" width="21" />
				                <auth:resBundle bundleName="amAuthUI" resourceKey="auth.failed" /></div>
				                <div class="AlrtMsgTxt">
				                <!-- warning message -->
				                <jato:content name="ContentStaticWarning"><br>
				                <jato:getDisplayFieldValue name='StaticTextWarning'
				                defaultValue='' fireDisplayEvents='true' escape='false'/>
				                </jato:content>
				
				                <!-- hyperlink -->
				                <jato:content name="ContentHref">
				                <p><auth:href name="LoginURL"
				                fireDisplayEvents='true'><jato:text
				                name="txtGotoLoginAfterFail" /></auth:href></p>
				                </jato:content>
				                </div>
				                </td></tr></table></div></td>
				                </tr>
				                
				                <tr>
				                <td>&nbsp;</td>
				                </tr>
				            <tr>
				            <td><img src="<%= ServiceURI %>/images/dot.gif" 
				            width="1" height="33" alt="" /></td>
				            <td>&nbsp;</td>
				            </tr>
				        </table>				        
				      </td>				      
				      <td width="45"><img src="<%= ServiceURI %>/images/dot.gif" 
				      width="45" height="245" alt="" /></td>
				    </tr>				    
				    </table>
				  </div>
				  
				  <div class="footer">
					<hr />
					<p><a href="http://new.freshwaterlife.org/web/guest">Home</a> | <a href="http://new.freshwaterlife.org/web/fwl">About us</a> | <a href="http://new.freshwaterlife.org/web/fwl/community">Community</a> | <a href="http://new.freshwaterlife.org/web/fwl/contact-us">Contact Us</a> | <a href="http://new.freshwaterlife.org/web/fwl/help">Help</a></p>
					<p><a href="http://new.freshwaterlife.org/web/fwl/general-use-and-copyright">Copyright Disclaimer</a> | <a href="http://new.freshwaterlife.org/web/fwl/copyright-statement">Reference Freshwater<em>Life</em></a> | <a href="http://www.freshwaterlife.org/issues/">Report a Problem</a></p>
					<p>Copyright 2011 Freshwater Biological Association on behalf of Freshwater<em>Life</em> Partners, Collaborators and Contributors.</p>
				  </div>
	  		</div><!-- /gc -->
		</div><!-- /Wrapper -->
	</div><!-- /gallery -->
</body>

</jato:useViewBean>
</html>
