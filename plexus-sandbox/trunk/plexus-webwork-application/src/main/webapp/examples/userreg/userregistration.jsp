<%@ taglib uri="webwork" prefix="webwork" %>
<%@ taglib uri="webwork" prefix="ui" %>
<link rel="stylesheet" type="text/css" href="../../template/standard/styles.css" title="Style">
<html>
    <head>
        <title>User registration</title>
    </head>
<body>
    <center><h1>User registration</h1></center>

    <webwork:if test="hasErrorMessages == true">
       ERROR:<BR>
       <webwork:iterator value="errorMessages">
          <FONT COLOR=red><webwork:property/></FONT><BR>
       </webwork:iterator>
    </webwork:if>

    <form action="<webwork:url value="userreg.userregistration.action"/>" method="POST">
        <table width="100%">
        <tr>
           <!-- start cell with webwork components -->
           <td width="350" valign="top">
              <table align="left" width="100%">
                    <tr>
                       <td>&nbsp</td><td>&nbsp</td>
                    </tr>
                   <ui:textfield label="'First name'" name="'firstname'" maxlength="40"/>
                   <ui:textfield label="'Last name'" name="'lastname'" maxlength="40"/>
                   <ui:textfield label="'Email'" name="'email'" maxlength="50"/>
                   <ui:textfield label="'Username'" name="'username'" maxlength="8"/>
                   <ui:password label="'Password'" name="'password'" maxlength="6"/>
                   <ui:password label="'Confirm password'" name="'confirmedPassword'" maxlength="6"/>
                   <ui:textfield label="'Adress'" name="'adress'" maxlength="30"/>
                   <ui:textfield label="'City'" name="'city'" maxlength="30"/>
                   <ui:textfield label="'Postal code'" name="'postcode'" maxlength="30"/>
                   <ui:textfield label="'Birth number'" name="'fnr'" maxlength="11"/>

                   <webwork:action name="'GenderMap'" id="genders"/>
                   <ui:radio label="'Gender'" name="'male'" list="@genders/genders"/>

                   <tr>
                       <td></td>
                       <td align="left">
                           <input type=submit value="Register" style="WIDTH: 142px; HEIGHT: 24px" size="35"/>
                       </td>
                    </tr>
               </table>
            </td>
            <!-- end cell with webwork components -->

            <!-- start cell with one webwork components -->
            <td align="left" width="100" valign="top">
              <table width="100%">
                   <tr><td height="25">&nbsp</td></tr>
                   <tr><td height="25">&nbsp</td></tr>
                   <tr><td height="25">&nbsp</td></tr>
                   <tr>
                    <td height="25" align="left" valign="top">
                          <INPUT type="checkbox" name="netcheck" value="false">Net&nbsp;check&nbsp;email
                    </td>
                   </tr>
                   <tr><td height="25">&nbsp</td></tr>
                   <tr><td height="25">&nbsp</td></tr>
                   <tr><td height="25">&nbsp</td></tr>
                   <tr><td height="25">&nbsp</td></tr>
                   <tr><td height="25">&nbsp</td></tr>
                   <tr><td height="25">&nbsp</td></tr>
                   <tr><td height="25">&nbsp</td></tr>
                   <tr><td height="25">&nbsp</td></tr>
                   <tr><td height="25">&nbsp</td></tr>
                </table>
            </td>
           <!-- end cell with one webwork components -->

           <!-- start cell with separator -->
           <td width="1" bgcolor="silver">
           </td>
           <td width="1" bgcolor="white">
           </td>
           <!-- end cell with separator -->

           <!-- start cell with instructions -->
           <td valign="right" align="top">
              <h3>Instructions</h3>
              Try the following:
              <ul>
                 <li>No firstname, no lastname</il>
                 <li>Either of the names longer than 30 characters</il>
                 <li>No username</il>
                 <li>Username shorter than 6 characters</il>
                 <li>No password</il>
                 <li>Not matching passwords</il>
                 <li>Password shorter than 4 characters</il>
                 <li>Birth number shorter or longer than 11 numbers</il>
                 <li>Birth number containing characters</il>
                 <li>Birth number that aren't valid (see hint below)</il>
                 <li>Birth number that don't comply with the gender (see hint below)</il>
                 <li>And I guess these usernames are taken:</il>
                 <br> - rickard
                 <br> - maurice
                 <br> - kjetil
                 <br>
                 <li>And finally, none of the above :)</il>
              </ul>
              HINT:
              <ul>

                 Birth number is the same as social securty number in Norway, this number is build up like this:
                 <li>the two first numbers are the day you are born (01 to 31)</li>
                 <li>the two next numbers are the month you are born (january = 01)</li>
                 <li>the two next numbers are the two last number in the year you were born (1975 = 75)</li>
                 <li>the next 5 numbers are controll numbers, for now just make sure the 3rd number here is even for females and odd for males</li>
           </td>
           <!-- end cell with instructions -->

        </tr>

        </table>

    </form>
</body>
</html>