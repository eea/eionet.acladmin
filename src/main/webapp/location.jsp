<%@ page import="java.util.Vector" %>
		
<div id="toolribbon">
	<div id="lefttools">
      <a id="eealink" href="http://www.eea.europa.eu/">EEA</a>
      <a id="ewlink" href="http://www.ewindows.eu.org/">EnviroWindows</a>
    </div>
    <div id="righttools">
		<a id="printlink" title="Print this page" href="javascript:this.print();"><span>Print</span></a>
        <a id="fullscreenlink" href="javascript:toggleFullScreenMode()" title="Switch to/from full screen mode"><span>Switch to/from full screen mode</span></a>
        <a id="acronymlink" href="http://www.eionet.europa.eu/acronyms" title="Look up acronyms"><span>Acronyms</span></a>
        <form action="http://search.eionet.europa.eu/search.jsp" method="get"><div id="freesrchform"><label for="freesrchfld">Search</label>
        <input type="text" id="freesrchfld" name="q"/>
        <input id="freesrchbtn" type="image" src="images/button_go.gif" alt="Go"/></div></form>
    </div>
</div> <!-- toolribbon -->

<div id="pagehead">
        <a href="/"><img src="images/eea-print-logo.gif" alt="Logo" id="logo" /></a>
        <div id="networktitle">Eionet</div>
        <div id="sitetitle">Access Control List administration</div>
        <div id="sitetagline">You change who can do what in Reportnet</div>
</div> <!-- pagehead -->


<div id="menuribbon">
	<%@ include file="dropdownmenus.txt" %>
</div>
<div class="breadcrumbtrail">
	<div class="breadcrumbhead">You are here:</div>
	<div class="breadcrumbitem eionetaccronym"><a href="http://www.eionet.europa.eu">Eionet</a></div>

	<%
	Vector breadcrumbs = (Vector)request.getAttribute("breadcrumbs");
	if (breadcrumbs==null || breadcrumbs.size()==0){ %>
		<div class="breadcrumbitemlast">AclAdmin</div><%
	}
	else{ %>
		<div class="breadcrumbitem"><a href="main">AclAdmin</a></div>
		<%
		for (int i=0; i<breadcrumbs.size(); i++){
			
			String breadcrumbItem = ((String)breadcrumbs.get(i)).trim();
			int j = breadcrumbItem.indexOf("|");
			String itemTitle = j<0 ? breadcrumbItem : breadcrumbItem.substring(0,j).trim();
			if (itemTitle.length()==0)
				continue;
			String itemLink = null;
			if (j>=0 && j<breadcrumbItem.length()-1)
				itemLink = breadcrumbItem.substring(j+1).trim();
				
			if (i<breadcrumbs.size()-1){
				if (itemLink!=null){ %>
					<div class="breadcrumbitem"><a href="<%=itemLink%>"><%=itemTitle%></a></div><%
				}
				else{ %>
					<div class="breadcrumbitem"><%=itemTitle%></div><%
				}
			}
			else{ %>
				<div class="breadcrumbitemlast"><%=itemTitle%></div><%
			}
		}
	}
	%>
<div class="breadcrumbtail">
</div>
</div>

