
import com.googlecode.jslint4java.*
import com.googlecode.jslint4java.formatter.*
import java.nio.charset.*

includeTargets << grailsScript('_GrailsCompile')

target("jslint": "Run jsLint on the javascript of the application.") {
	depends(compile)

	def lint = new GrailsLint()
	lint.execute()
	def html = new HtmlReportBuilder().build(lint)
	new File("target/jslint.html").write(html)
}

setDefaultTarget("jslint")

///////////////////////////////////////////////////////////////////////////////

class GrailsLint {
	
	static String jsFolder = "web-app/js"
	static String gspFolder = "grails-app/views"
	
	List<Fetcher> fetchers = []
	List<Script> scripts = []
	
	GrailsLint() {
		initFetchers()
	}
	
	void execute() {
		fetchScripts()
		replaceGStrings()
		lintScripts()
	}
	
	private void initFetchers() {
		fetchers << new FetchJSFolder(grailsLint:this)
		fetchers << new FetchGspScript(grailsLint:this)
		fetchers << new FetchGspResourceScript(grailsLint:this)
		fetchers << new FetchGspJavascript(grailsLint:this)
//		fetchers << new FetchGspEvent(grailsLint:this)
	}
	
	private void fetchScripts() {
		fetchers.each { fetcher ->
			scripts += fetcher.fetch()
		}
	}
	
	private void replaceGStrings() {
		scripts.each { script ->
			script.replaceGStrings()
		}
	}		
	
	private void lintScripts() {
		scripts.each { script ->
			script.lint()
		}
	}
	
}

///////////////////////////////////////////////////////////////////////////////

class Issue {
	Script script
	int row
	int col
	String message
}

///////////////////////////////////////////////////////////////////////////////

public interface Fetcher {
	List<Script> fetch()
}

///////////////////////////////////////////////////////////////////////////////

abstract class FetcherBase implements Fetcher {
	GrailsLint grailsLint
}

///////////////////////////////////////////////////////////////////////////////

class FetchJSFolder extends FetcherBase {

	List<Script> fetch() {
		
		List<Script> scripts = []
		
		new File(grailsLint.jsFolder).eachFile { file ->
			scripts << new Script(name:file.toString(), content:file.text)
		}
		
		scripts
	}
}

///////////////////////////////////////////////////////////////////////////////

abstract class FetchGsp extends FetcherBase {

	abstract String getBegin()
	abstract String getEnd()
	
	List<Script> fetch() {
		
		List<Script> scripts = []
		
		new File(grailsLint.gspFolder = "grails-app/views").eachDir { folder ->
			folder.eachFile { file ->
				scripts += fetchFile(file)
			}
		}
		
		scripts
	}
	
	List<Script> fetchFile(file) {
	
		List<Script> scripts = []
		
		int index = 1
		
		def content = file.text

		int pos1 = content.indexOf(begin)

		while(-1 != pos1) {

			pos1 = content.indexOf(">",pos1)
			int pos2 = content.indexOf(end, pos1)

			if(-1 == pos2) {
				throw new RuntimeException("Missing closing $end in $file")
			}

			scripts << new Script(name:file.toString() + " - ${index}", content:content.substring(pos1+1, pos2).trim())

			pos1 = content.indexOf(begin, pos2)
			
			index++
		}
				
		scripts
	}
	
}

///////////////////////////////////////////////////////////////////////////////

class FetchGspScript extends FetchGsp {
	String getBegin() { "<script" }
	String getEnd() { "</script" }
}

///////////////////////////////////////////////////////////////////////////////

class FetchGspResourceScript extends FetchGsp {
	String getBegin() { "<r:script" }
	String getEnd() { "</r:script" }
}

///////////////////////////////////////////////////////////////////////////////

class FetchGspJavascript extends FetchGsp {
	String getBegin() { "<g:javascript" }
	String getEnd() { "</g:javascript" }
}

///////////////////////////////////////////////////////////////////////////////

class FetchGspEvent extends FetcherBase {
	List<Script> fetch() {
		[]
	}
}

///////////////////////////////////////////////////////////////////////////////

class Script {
	
	String name
	String content
	List<Issue> issues = []

	void lint() {

		def lintBuilder = new JSLintBuilder()
		def jsLint = lintBuilder.fromDefault()

		jsLint.addOption(Option.WHITE)
		jsLint.addOption(Option.BROWSER)
		jsLint.addOption(Option.PLUSPLUS)
		jsLint.addOption(Option.DEVEL)
		jsLint.addOption(Option.VARS) 
		jsLint.addOption(Option.PREDEF, "\$")
		jsLint.addOption(Option.UNDEF) 

		def Charset encoding = Charset.defaultCharset()
		def reader = new StringReader(content)
		
		def results = jsLint.lint(name, reader)

		results.issues.each { result ->
			issues << new Issue(script:this, row:result.line, col:result.character, message:result.reason)
		}
		
	}
	
	void replaceGStrings() {
		
		def pos = content.indexOf(":\${")
		while(-1 != pos) {
			def pos2 = content.indexOf("}", pos)
			def a = content.substring(0, pos)
			def b = content.substring(pos2+1)
			content = a + ":0" + b
			pos = content.indexOf(":\${")
		}

	}
	
}

///////////////////////////////////////////////////////////////////////////////

class HtmlReportBuilder {

	def grailsLint
	def writer
	def printer
	
	String build(GrailsLint grailsLint) {
		this.grailsLint = grailsLint
		writer = new StringWriter()
		printer = new PrintWriter(writer)
		
		openHtml()
		writeHeader()
		writeBody()
		closeHtml()
		
		writer.toString()
	}

	private void openHtml() {
		println "<html>"
	}
	
	private void writeHeader() {
		println "<head>"
		println "<title>Javascript Lint Results</title>"
		writeStyle()
		// println """<link rel="stylesheet" type="text/css" href="style.css">"""
		println "</head>"
	}
	
	private void writeStyle() {
		println """<style type="text/css">
			body {
				margin: 20px;
				margin-left : 60px;
				margin-right: 60px;
				font-family: helvetica, arial;
			}


			th { 
				background-color: #8888ff;
				color: black;
				padding: 5px;
				text-align: left;
			}

			tbody tr:hover {
				background-color: yellow;
			}

			table {
				width: 100%;
			}

			h2 {
				border-top: 3px solid #8888ff;
				padding-top: 5px;
				margin-top: 30px;
			}

			table.contents {
			}

			caption {
				text-align: left;
			}

			div.nav a {
				margin-right: 20px;
			}

			td.pass {
				background-color: green;
			}

			td.fail {
				background-color: red;
			}

			table.header th {
				width: 0;
			}
			</style>
		"""
	}
	
	private void writeBody() {
		println "<body>"

		println """<h1><a name="top">Javascript Lint Results</a></h1>"""
		println "<div>${new Date()}</div>"

		writeSummary()
		
		grailsLint.scripts.eachWithIndex { script, x ->
			writeScript(script, x)
		}
		
		println "</body>"
	}
	
	private void closeHtml() {
		println "</html>"
	}
	
	private void writeSummary() {
		println "<h2>Summary</h2>"
		println """<table class="header">"""
		println "<thead>"
		println "<tr>"
		println "<th>&nbsp;&nbsp;</th>"
		println "<th>Row</th>"
		println "<th>Number of Issues</th>"
		println "</tr>"
		println "</thead>"
		println "<tbody>"
			
		grailsLint.scripts.eachWithIndex { script, n ->
			println "<tr>"
			println """<td class="${0 == script.issues.size() ? 'pass' : 'fail'}">&nbsp;</td>"""
			println """<td><a href="#$n">${script.name}</td>"""
			println "<td>${script.issues.size()}</td>"
			println "</tr>"
		}
		println "</tbody>"
		println "</table>"
	}

	private void writeScript(script, index) {

		println """<h2><a name="${index}">${script.name}</a></h2>"""
		println """<div class="nav"><a href="#top">Top</a><a href="#${index-1}">Prev</a><a href="#${index+1}">Next</a></div>"""

		writeResults(script, index)
		writeContents(script, index)
	}

	private void writeResults(script, index) {
	
		println "<h3>Results</h3>"

		println """<table class="results">"""
		println """<caption>There are ${script.issues.size()} issues</caption>"""
		println "<thead>"
		println "<tr>"
		println "<th>Row</th>"
		println "<th>Column</th>"
		println "<th>Message</th>"
		println "</tr>"
		println "</thead>"
		println "<tbody>"

		script.issues.each { issue ->
			println "<tr>"
			println """<td><a href="#${index}.${issue.row}">${issue.row}</a></td>"""
			println "<td>${issue.col}</td>"
			println "<td>${issue.message}</td>"
			println "</tr>"
		}

		println "</tbody>"
		println "</table>"
	}
		
	private void writeContents(script, index) {
	
		println "<h3>Contents</h3>"

		println """<table class="contents">"""
		println "<thead>"
		println "<tr>"
		println "<th>Line</th>"
		println "<th>Content</th>"
		println "</tr>"
		println "</thead>"
		println "<tbody>"
		println "<tr>"

		script.content.split("\n").eachWithIndex { line, row ->
			line = line.replace("\r","")
			println "<tr>"
			println """<td><a name="${index}.${row+1}">${row+1}<a/></td>"""
			println "<td><pre>${escape(line)}</pre></td>"
			println "</tr>"
		}

		println "</tr>"
		println "</tbody>"
		println "</table>"
	}
	
	private void println(s) {
		printer.println s
	}
	
	private String escape(s) {
 		org.apache.commons.lang.StringEscapeUtils.escapeHtml(s)
 	}
	
}






// java -jar jslint4java-2.0.1.jar C:\Projects\grails20\samples\web-app\js\application.js

//web-app\js\application.js:2:1:'$' was used before it was defined.
//web-app\js\application.js:15:10:Move 'var' declarations to the top of the function.
//web-app\js\application.js:15:10:Stopping.  (65% scanned).

