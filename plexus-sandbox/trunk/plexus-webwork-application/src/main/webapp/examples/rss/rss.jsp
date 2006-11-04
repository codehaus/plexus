<%@ taglib uri="webwork" prefix="webwork" %>


<webwork:property value="channel">
   <%-- channel title --%>
   <div align="left"><big><webwork:property value="childText('title')"/></big></div>
   <table cellspacing="0" cellpadding="0" width="100%" border="0">
      <tr height="1" bgcolor="black"><td><img SRC="/images/pixel.gif" width="1" height="1"/></td></tr>
   </table>
   <p>
   <%-- channel image --%>
   <webwork:property value="child('image')">
      <webwork:if test=".">
          <img alt="<webwork:property value="childText('title')"/>" 
               src="<webwork:property value="childText('url')"/>" 
               <webwork:if test="childText('height')">
                  width="<webwork:property value="childText('width')"/>"
                  height="<webwork:property value="childText('height')"/>"
               </webwork:if>
               border="0"></img><br>
      </webwork:if>
   </webwork:property>

   <%-- channel description --%>
   <i><webwork:property value="childText('description')"/></i><br>
</webwork:property>

<webwork:if test="$bullet == 'true'">
   <ul>
   <webwork:iterator value="items">
     <webwork:if test="childText('title') != 'Feed listing'">
     <li>
      <webwork:property value="childText('link')">
         <webwork:if test=".">
             <a href="<webwork:property/>"><webwork:property value="childText('title')"/></a>
         </webwork:if>
         <webwork:else>
            <b><webwork:property value="childText('title')"/></b>
         </webwork:else>
      </webwork:property>
     </li>
     </webwork:if>
   </webwork:iterator>
   </ul>
</webwork:if>
<webwork:else>
   <webwork:iterator value="items">
      <webwork:if test="childText('title') != 'Feed listing'">
      <p>
      <webwork:property value="childText('link')">
         <webwork:if test=".">
             <a target="rssview" href="<webwork:property/>"><webwork:property value="childText('title')"/></a>
         </webwork:if>
         <webwork:else>
            <b><webwork:property value="childText('title')"/></b>
         </webwork:else>
      </webwork:property>
      <br>
      <webwork:property value="childText('description')"/>
      </p>
      </webwork:if>
   </webwork:iterator>
</webwork:else>
</p>
