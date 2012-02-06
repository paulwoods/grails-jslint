class JslintGrailsPlugin {

    def version = "1.0.0-SNAPSHOT"

    def grailsVersion = "2.0 > *"

    def dependsOn = [:]

    def pluginExcludes = [
    ]

    def title = "Jslint Plugin" 

    def author = "Paul Woods"

    def authorEmail = "mr.paul.woods@gmail.com"

    def description = '''\
This plugin runs jsLint on the javascript of your application.
'''

    def documentation = "http://grails.org/plugin/jslint"

    //def organization = [ name: "Texas Instruments", url: "http://www.ti.com/" ]

    // def issueManagement = [ system: "JIRA", url: "http://jira.grails.org/browse/GPMYPLUGIN" ]

    // def scm = [ url: "http://svn.grails-plugins.codehaus.org/browse/grails-plugins/" ]

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
