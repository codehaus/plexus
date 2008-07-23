<#--

Freemarker Demonstration
-------------------------

* uses the benchmark taglib to measure the speed of generating an option list
* illustrates iteration over a list
* illustrates using a macro as a parameter... do that in velocity :)
* illustrates scraping the output of a nested template block into a variable
* illustrates string to number conversion, and vice versa

-->
<#assign benchmark=JspTaglibs["/WEB-INF/taglibs-benchmark.tld"] />
<#assign count = 100/>
<html>
<head>
	<title>Freemarker Demo</title>
	<link rel="stylesheet" href="styles.css">
</head>
<body>
<@heading>Freemarker Demonstration</@heading>
<ul>
<li>uses the benchmark taglib to measure the speed of generating an option list
<li>illustrates iteration over a list
<li>illustrates using a macro as a parameter... do that in velocity :)
<li>illustrates scraping the output of a nested template block into a variable
<li>illustrates string to number conversion, and vice versa
</ul>
<@title>Duration to output country list</@title>
<@msg>(averaged over ${count} repetitions)</@msg>
<p>
	<#assign duration><@benchmark.duration><@repeat macro=renderCountries count=count/></@benchmark.duration></#assign>
	${duration?number / count} ms each
</p>
<@title>Country List</@title>
<@renderCountries/>
</body>
</html>

<#macro heading>
	<h1><#nested/></h1>
</#macro>
<#macro msg>
	<p class="msg"><#nested/></p>
</#macro>
<#macro title>
	<p class="title"><#nested/></p>
</#macro>

<#macro renderCountries>
	<ul>
		<#list countries as country>
			<li>${country[0]}</li>
		</#list>
	</ul>
</#macro>

<#macro repeat macro count>
	<#list 1..count as c>
		<@macro/>
	</#list>
</#macro>