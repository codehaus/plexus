<%@ taglib uri="webwork" prefix="webwork" %>
<HTML>
<HEAD>
<TITLE>WebWork Expression Language Test</TITLE>
</HEAD>
<BODY>

<H1>WebWork Expression Language Test</H1>
<webwork:bean name="'webwork.util.Timer'" id="timer"/>
<webwork:action name="'Test'" id="t" />
<webwork:push value="#t">
<br>

<table border="1">
	<tr>
		<td></td>
		<td>Expression</td>
		<td>Result</td>
		<td>Empty is correct</td>
	</tr>
	<tr>
		<td align="right">1</td>
		<td>webwork:if test="'hello' == 'hello'"</td>
		<td>
			<webwork:if test="'hello' == 'hello'">
			   <b>Success</b>
			</webwork:if> <br>
		</td>
		<td></td>
	</tr>
	<tr>
		<td align="right">2</td>
		<td>webwork:if test="'hello' != 'helloo'"</td>
		<td>
			<webwork:if test="'hello' != 'helloo'">
			   <b>Success</b>
			</webwork:if>
		</td>
		<td></td>
	</tr>
	<tr>
		<td align="right">3</td>
		<td>webwork:if test="'hello' != 'hello hello'"</td>
		<td>
			<webwork:if test="'hello' != 'hello hello'">
			   <b>Success</b>
			</webwork:if>
		</td>
		<td></td>
	</tr>
	<tr>
		<td align="right">4</td>
		<td>webwork:if test="'1' &lt; '2'"</td>
		<td>
			<webwork:if test="'1' < '2'">
			   <b>Success</b>
			</webwork:if>
		</td>
		<td></td>
	</tr>
	<tr>
		<td align="right">5</td>
		<td>webwork:if test="1 &lt; 2"</td>
		<td>
			<webwork:if test="1 < 2">
			   <b>Success</b>
			</webwork:if>
		</td>
		<td></td>
	</tr>
	<tr>
		<td align="right">6</td>
		<td>webwork:if test="'10' &lt; '2'"</td>
		<td>
			<webwork:if test="10 < 2">
			   <b>Success</b>
			</webwork:if>
		</td>
		<td></td>
	</tr>
	<tr>
		<td align="right">7</td>
		<td>webwork:if test="10 &gt; 2"</td>
		<td>
			<webwork:if test="10 > 2">
			   <b>Success</b>
			</webwork:if>
		</td>
		<td></td>
	</tr>
	<tr>
		<td align="right">8</td>
		<td>4.2) webwork:if test="-10 &lt; -9"</td>
		<td>
			<webwork:if test="-10 < -9">
			   <b>Success</b>
			</webwork:if>
		</td>
		<td></td>
	</tr>
	<tr>
		<td align="right">9</td>
		<td>webwork:if test="'1' &lt;= '1'"</td>
		<td>
			<webwork:if test="'1' <= '1'">
			   <b>Success</b>
			</webwork:if>
		</td>
		<td></td>
	</tr>
	<tr>
		<td align="right">10</td>
		<td>webwork:if test="'2' &gt;= '2'"</td>
		<td>
			<webwork:if test="'2' >= '2'">
			   <b>Success</b>
			</webwork:if>
		</td>
		<td></td>
	</tr>
	<tr>
		<td align="right">11</td>
		<td>webwork:if test="settings['foo'] == 'bar'"</td>
		<td>
			<webwork:if test="settings['foo'] == 'bar'">
			   <b>Success</b>
			</webwork:if>
		</td>
		<td></td>
	</tr>
	<tr>
		<td align="right">12</td>
		<td>webwork:elseIf test="settings['foo'] != 'bar'"</td>
		<td>
			<webwork:elseIf test="settings['foo'] != 'bar'">
			   <b>Failure</b>
			</webwork:elseIf>
		</td>
		<td>X</td>
	</tr>
	<tr>
		<td align="right">13</td>
		<td>webwork:if test="settings['foo'] == 'wrong'"</td>
		<td>
			<webwork:if test="settings['foo'] == 'wrong'">
			   <b>Failure</b>
			</webwork:if>
		</td>
		<td>X</td>
	</tr>
	<tr>
		<td align="right">14</td>
		<td>webwork:else</td>
		<td>
			<webwork:else>
			   <b>Success</b>
			</webwork:else>
		</td>
		<td></td>
	</tr>
	<tr>
		<td align="right">15</td>
		<td>webwork:elseIf test="settings['foo'] == 'bar'"</td>
		<td>
			<webwork:elseIf test="settings['foo'] == 'bar'">
			   <b>Failure</b>
			</webwork:elseIf>
		</td>
		<td>X</td>
	</tr>
	<tr>
		<td align="right">16</td>
		<td>webwork:if test="settings['foo'] == 'wrong'"</td>
		<td>
			<webwork:if test="settings['foo'] == 'wrong'">
			   <b>Failure</b>
			</webwork:if>
		</td>
		<td>X</td>
	</tr>
	<tr>
		<td align="right">17</td>
		<td>webwork:elseIf test="settings['foo'] != 'bar'"</td>
		<td>
			<webwork:elseIf test="settings['foo'] != 'bar'">
			   <b>Failure</b>
			</webwork:elseIf>
		</td>
		<td>X</td>
	</tr>
	<tr>
		<td align="right">18</td>
		<td>webwork:else</td>
		<td>
			<webwork:else>
			   <b>Success</b>
			</webwork:else>
		</td>
		<td></td>
	</tr>
	<tr>
		<td align="right">19</td>
		<td>webwork:if test="settings['foo'] == 'wrong'"</td>
		<td>
			<webwork:if test="settings['foo'] == 'wrong'">
			   <b>Failure</b>
			</webwork:if>
		</td>
		<td>X</td>
	</tr>
	<tr>
		<td align="right">20</td>
		<td>webwork:else webwork:if test="settings['foo'] == 'bar'" </td>
		<td>
			<webwork:else>
			   <webwork:if test="settings['foo'] == 'bar'">
			      <b>Success</b>
			   </webwork:if>
			</webwork:else>
		</td>
		<td></td>
	</tr>
	<tr>
		<td align="right">21</td>
		<td>webwork:if test="bool('true') == true"</td>
		<td>
			<webwork:if test="bool('true') == true">
			   <b>Success</b>
			</webwork:if>
		</td>
		<td></td>
	</tr>
	<tr>
		<td align="right">22</td>
		<td>webwork:if test="bool('false') == false"</td>
		<td>
			<webwork:if test="bool('false') == false">
			   <b>Success</b>
			</webwork:if>
		</td>
		<td></td>
	</tr>
	<tr>
		<td align="right">23</td>
		<td>webwork:if test="bool('true')/toString()/endsWith('e') == true"</td>
		<td>
			<webwork:if test="new Boolean('true').toString().endsWith('e') == true">
			   <b>Success</b>
			</webwork:if>
		</td>
		<td></td>
	</tr>
	<tr>
		<td align="right">24</td>
		<td>webwork:if test="!jibberish&amp;&amp;(settings['foo']=='bar'||settings['foo']=='wrong')"</td>
		<td>
			<webwork:if test="!jibberish && (settings['foo']=='bar'||settings['foo']=='wrong')">
			   <b>Success</b>
			</webwork:if>
		</td>
		<td></td>
	</tr>
	<tr>
		<td align="right">25</td>
		<td>webwork:if test="!jibberish &amp;&amp; settings['foo'] == 'bar' || settings['foo'] == 'wrong'"</td>
		<td>
			<webwork:if test="!jibberish && settings['foo'] == 'bar' || settings['foo'] == 'wrong'">
			   <b>Success</b>
			</webwork:if>
		</td>
		<td></td>
	</tr>
	<tr>
		<td align="right">26</td>
		<td>webwork:if test="!jibberish &amp;&amp; settings['foo'] == 'wrong' || settings['foo'] == 'bar' &amp;&amp; true == true"</td>
		<td>
			<webwork:if test="!jibberish && settings['foo'] == 'wrong' || settings['foo'] == 'bar' && true == true">
			   <b>Success</b>
			</webwork:if>
		</td>
		<td></td>
	</tr>
	<tr>
		<td align="right">27</td>
		<td>webwork:if test="(!jibberish ||(settings['foo']=='bar'||(settings['foo']=='wrong')))"</td>
		<td>
			<webwork:if test="(!jibberish||(settings['foo']=='bar'||(settings['foo']=='wrong')))">
			   <b>Success</b>
			</webwork:if>
		</td>
		<td></td>
	</tr>
	<tr>
		<td align="right">28</td>
		<td>webwork:if test="!jibberish"</td>
		<td>
			<webwork:if test="!jibberish">
			   <b>Success</b>
			</webwork:if>
		</td>
		<td></td>
	</tr>
	<tr>
		<td align="right">29</td>
		<td>webwork:else</td>
		<td>
			<webwork:else>
			   <b>Failure</b>
			</webwork:else>
		</td>
		<td>X</td>
	</tr>
	<tr>
		<td align="right">30</td>
		<td>webwork:if test="settings['foo']"</td>
		<td>
			<webwork:if test="settings['foo']">
			   <b>Success</b>
			</webwork:if>
		</td>
		<td></td>
	</tr>
	<tr>
		<td align="right">31</td>
		<td>webwork:if test="jibberish"</td>
		<td>
			<webwork:if test="jibberish">
			   <b>Failure</b>
			</webwork:if>
		</td>
		<td>X</td>
	</tr>
	<tr>
		<td align="right">32</td>
		<td>webwork:else</td>
		<td>
			<webwork:else>
			   <b>Success</b>
			</webwork:else>
		</td>
		<td>&nbsp;</td>
	</tr>
	<tr>
		<td align="right">33</td>
		<td>webwork:if test="bool('true') == true"</td>
		<td>
			<webwork:if test="bool('true') == true">
			   <b>Success</b>
			</webwork:if>
		</td>
		<td></td>
	</tr>
	<tr>
		<td align="right">34</td>
		<td>webwork:if test="bool('true') == true &amp;&amp; (bool('false') == true || settings['foo'] == 'bar')"</td>
		<td>
			<webwork:if test="bool('true') == true && (bool('false') == true || settings['foo'] == 'bar')">
			   <b>Success</b>
			</webwork:if>
		</td>
		<td></td>
	</tr>
	<tr>
		<td align="right">35</td>
		<td>webwork:if test="true==true" webwork:if test="true==true" webwork:else webwork:else</td>
		<td>
			<webwork:if test="true==true">
			  <webwork:if test="true==true">
				  <b>Success</b>
			  </webwork:if>
			  <webwork:else>
				  <b>Failure</b>
			  </webwork:else>
			</webwork:if>
			<webwork:else>
				<b>Failure</b>
			</webwork:else>
		</td>
		<td></td>
	</tr>
	<tr>
		<td align="right">36</td>
		<td>webwork:if test="true==true" webwork:if test="true==false"</td>
		<td>
			<webwork:if test="true==true">
			  <webwork:if test="true==false">
				  <b>Failure</b>
			  </webwork:if>
			  <webwork:else>
				  <b>Success</b>
			  </webwork:else>
			</webwork:if>
			<webwork:else>
				<b>Failure</b>
			</webwork:else>
		</td>
		<td></td>
	</tr>
	<tr>
		<td align="right">37</td>
		<td>webwork:if test="true==false" webwork:if test="true==true" webwork:else webwork:else</td>
		<td>
			<webwork:if test="true==false">
			  <webwork:if test="true==true">
			     <b>Failure</b>
			  </webwork:if>
			  <webwork:else>
			     <b>Failure</b>
			  </webwork:else>
			</webwork:if>
			<webwork:else>
				 <b>Success</b>
			</webwork:else>
		</td>
		<td></td>
	</tr>


	<tr>
		<td align="right">38</td>
		<td>webwork:if test="true==true" webwork:if test="true==false" webwork:elseIf test="true==true" webwork:else</td>
		<td>
			<webwork:if test="true==true">
			  <webwork:if test="true==false">
			 		<b>Failure</b>
			  </webwork:if>
			  <webwork:elseIf test="true==true">
			  		<b>Success</b>
			  </webwork:elseIf>
			</webwork:if>
			<webwork:else>
				<b>Failure</b>
			</webwork:else>
		</td>
		<td></td>
	</tr>

	<tr>
		<td align="right">39</td>
		<td>webwork:if test="true==true" webwork:if test="true==false" webwork:elseIf test="true==false" webwork:else</td>
		<td>
			<webwork:if test="true==true">
			  <webwork:if test="true==false">
			  	 <b>Failure</b>
			  </webwork:if>
			  <webwork:elseIf test="true==false">
			  	  <b>Failure</b>
			  </webwork:elseIf>
			</webwork:if>
			<webwork:else>
				<b>Failure</b>
			</webwork:else>
		</td>
		<td>X</td>
	</tr>
	<tr>
		<td align="right">40</td>
		<td> Using id's:</td>
		<td>
			<webwork:if test="true == true" id="foo"/>
			<webwork:if test="true == false" id="bar"/>
			<webwork:if test="@foo == false && true == true">
			   <b>Failure</b>
			</webwork:if>
			<webwork:if test="@bar == false">
			   <b>Success</b>
			</webwork:if>
		</td>
		<td></td>
	</tr>
	<tr>
		<td align="right">41</td>
		<td>Testing if, elseif, else relationship:</td>
		<td>
			<webwork:if test="true == true">
			   <b>Success</b>
			</webwork:if>
			<webwork:elseIf test="true == true">
			   <b>Failure</b>
			</webwork:elseIf>
			<webwork:else>
			   <b>Failure</b>
			</webwork:else>
		</td>
		<td></td>
	</tr>
	<tr>
		<td align="right">42</td>
		<td>Testing if, elseif, else relationship:</td>
		<td>
			<webwork:if test="true == false">
			   <b>Failure</b>
			</webwork:if>
			<webwork:elseIf test="true == true">
			   <b>Success</b>
			</webwork:elseIf>
			<webwork:else>
			   <b>Failure</b>
			</webwork:else>
		</td>
		<td></td>
	</tr>
	<tr>
		<td align="right">43</td>
		<td>Testing if, elseif, else relationship</td>
		<td>
			<webwork:if test="true == false">
			   <b>Failures</b>
			</webwork:if>
			<webwork:elseIf test="true == false">
			   <b>Failure</b>
			</webwork:elseIf>
			<webwork:else>
			   <b>Success</b>
			</webwork:else>
		</td>
		<td></td>
	</tr>
	<tr>
		<td align="right">44</td>
		<td>Number comparisons (webwork:if test="1 == 1.0"):</td>
		<td>
			<webwork:if test="1 == 1.0">
			   <b>Success</b>
			</webwork:if>
		</td>
		<td></td>
	</tr>
	<tr>
		<td align="right">45</td>
		<td>webwork:if test="1 &LT; 1.1"</td>
		<td>
			<webwork:if test="1 < 1.1">
			   <b>Success</b>
			</webwork:if>
		</td>
		<td></td>
	</tr>
	<tr>
		<td align="right">46</td>
		<td>webwork:if test=".9 &LT; 1.0"</td>
		<td>
			<webwork:if test=".9 < 1.0">
			   <b>Success</b>
			</webwork:if>
		</td>
		<td></td>
	</tr>
	<tr>
		<td align="right">47</td>
		<td>webwork:if test="1.1 &GT; 1.0"</td>
		<td>
			<webwork:if test="1.1 > 1.0">
			   <b>Success</b>
			</webwork:if>
		</td>
		<td></td>
	</tr>
	<tr>
		<td align="right">48</td>
		<td>webwork:if test="-1.1 &GT; -2.0"</td>
		<td>
			<webwork:if test="-1.1 > -2.0">
			   <b>Success</b>
			</webwork:if>
		</td>
		<td></td>
	</tr>
	<tr>
		<td align="right">49</td>
		<td>webwork:if test="0 &GT; -1.0"</td>
		<td>
			<webwork:if test="0 > -1.0">
			   <b>Success</b>
			</webwork:if>
		</td>
		<td></td>
	</tr>
	<tr>
		<td align="right">50</td>
		<td>webwork:if test="(1==1 &amp;&amp; 2==2) || (2==2 &amp;&amp; 3==2)"</td>
		<td>
			<webwork:if test="(1==1 && 2==2) || (2==2 && 3==2)">
			   <b>Success</b>
			</webwork:if>
		</td>
		<td></td>
	</tr>
	<tr>
		<td align="right">51</td>
		<td>webwork:if test="(1==2 &amp;&amp; 2==2) || (2==2 &amp;&amp; 3==2)"</td>
		<td>
			<webwork:if test="(1==2 && 2==2) || (2==2 && 3==2)">
			   <b>Success</b>
			</webwork:if>
		</td>
		<td>X</td>
	</tr>
</table>

</webwork:push> </p>
Total time:<webwork:property value="#attr.timer.total"/>ms
<HR>
<A href="<webwork:url/>">Next</A>
</BODY>
</HTML>
