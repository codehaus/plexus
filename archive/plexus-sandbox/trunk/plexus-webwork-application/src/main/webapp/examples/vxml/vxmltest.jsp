<%="<?xml version=\"1.0\"?>"%>
<%@ page contentType="text/xml"%>

<%@ taglib uri="webwork" prefix="webwork" %>
<%@ taglib uri="webwork" prefix="vui" %>


<vxml version="2.0">

	<form>
	        <vui:field name="'fieldname'" modal="'true'">

	            <vui:grammar name="'testgrammar'" model="grammar">
	              <!-- append a rule to grammar -->
	              <rule name="test">
	                <item>What is it</item>
	              </rule>
	            </vui:grammar>

	            <vui:prompt bargein="false">
  	                <vui:audio src="'test.wav'" tts="'this is a test'"/>
	            </vui:prompt>

	            <vui:filled action="'formtest.action'" actionaudio="'/action.wav'">
	               <vui:log label="'somelabel'">Log</vui:log>
	            </vui:filled>

	        </vui:field>

	</form>

</vxml>



