<div id="globalnav">
 <h2>Contents</h2>

  <ul>
	<% if (grpEdit) { %>
   <li><a href="javascript:openPage('<%=Names.SHOW_GROUPS_ACTION%>')">Edit groups</a></li>
	<% } %>
   <li><a href="javascript:openPage('<%=Names.SHOW_APPS_ACTION%>')"
       title='Show Applications'>Applications</a></li>
   <li><a href="javascript:openPage('<%=Names.LOGOUT_ACTION%>')">Logout</a></li>
  </ul>
</div>
