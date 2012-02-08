jsLint for Grails

Paul Woods

This grails 2.0 plugin runs jsLint on the javascript of your application.


How to Use

1. just install the plugin to your application

grails install-plugin jslint


2. Then run the plugin

grails jslint


3. And review the report

<appname>\target\jslint.html




What is does

1. It loads the javascript in your application
	a. <appname>\web-inf\js folder
	b. <appname>\grails-app\views\**\*.gsp inside script tags.
	c. <appname>\grails-app\views\**\*.gsp inside g:javascript tags.
	d. <appname>\grails-app\views\**\*.gsp inside r:script tags.

2. its runs jsLint on all of the scripts

3. it builds a HTML report of the results


