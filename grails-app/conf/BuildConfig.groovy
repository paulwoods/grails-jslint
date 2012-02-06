grails.project.class.dir = "target/classes"
grails.project.test.class.dir = "target/test-classes"
grails.project.test.reports.dir = "target/test-reports"
grails.project.target.level = 1.6

grails.project.dependency.resolution = {

	inherits("global") {
	}

	log "warn"

	repositories {
		mavenCentral()
        grailsPlugins()
	}

	dependencies {
		compile  "com.googlecode.jslint4java:jslint4java:2.0.1"
		compile  "commons-lang:commons-lang:2.6"
	}

	plugins {
		build (":release:1.0.1") { export = false }
	}
}
