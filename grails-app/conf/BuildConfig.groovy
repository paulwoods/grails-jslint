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

boolean snapshot = new File("application.properties").text.contains("-SNAPSHOT")

if(snapshot) {
	grails.project.repos.default = "snapshot"
	grails.project.repos.snapshot.url = "http://dta0459765.am.dhcp.ti.com:9003/artifactory/libs-snapshot-local"
} else {
	grails.project.repos.default = "release"
	grails.project.repos.release.url = "http://dta0459765.am.dhcp.ti.com:9003/artifactory/libs-release-local"
}

