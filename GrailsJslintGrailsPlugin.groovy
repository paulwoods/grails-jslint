class GrailsJslintGrailsPlugin {

    def version = "1.0.0"

    def grailsVersion = "2.0 > *"

    def dependsOn = [:]

    def pluginExcludes = [
    ]

    def title = "Grails jsLint Plugin" 

    def author = "Paul Woods"

    def authorEmail = "mr.paul.woods@gmail.com"

    def description = """This plugin runs jsLint on the javascript of your application."""

    def documentation = "http://grails.org/plugin/jslint"

    def issueManagement = [ system: "GitHub", url: "https://github.com/paulwoods/grails-jslint/issues" ]

    def scm = [ url: "git@github.com:paulwoods/grails-jslint.git" ]

    def doWithWebDescriptor = { xml ->
    }

    def doWithSpring = {
    }

    def doWithDynamicMethods = { ctx ->
    }

    def doWithApplicationContext = { applicationContext ->
    }

    def onChange = { event ->
    }

    def onConfigChange = { event ->
    }

    def onShutdown = { event ->
    }
}
