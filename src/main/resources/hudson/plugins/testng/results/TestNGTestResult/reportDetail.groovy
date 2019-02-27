package hudson.plugins.testng.results.TestNGTestResult

import hudson.plugins.testng.util.FormatUtil;
import hudson.plugins.testng.util.DisplayUtil;

f = namespace(lib.FormTagLib)
l = namespace(lib.LayoutTagLib)
t = namespace("/lib/hudson")
st = namespace("jelly:stapler")


link(href:"${app.rootUrl}plugin/testng-plugin/css/table.css", rel:"stylesheet", type:"text/css")

script(type: "text/javascript", src: "${app.rootUrl}plugin/testng-plugin/js/toggle_mthd_summary.js")
script(type: "text/javascript", src:"${app.rootUrl}/plugin/testng-plugin/js/toggle_table.js")
script(type: "text/javascript", src: "${app.rootUrl}plugin/testng-plugin/js/select_view.js")

h2("Failed Tests")

if (my.failCount > 0) {

	table(id:"fail-tbl-suite", border:"1px", class:"pane sortable") {
		thead() {
			tr() {
				th(class: "pane-header") { text("Method/Error detail") }
				th(class: "pane-header", style:"width:10em") { text("Failed Tests (Diff)") }
			}
		}
		tbody () {

			for (clazz in my.children) {
				if (clazz.failCount > 0) {

					for (method in clazz.failedTests) {
						tr() {
							td(align: "left") {
								a(id: "${method.id}_suite-showlink", title:"Error details", href:"javascript:showStackTrace('${method.id}_suite', '${method.upUrlSuite}/summary')") { text(">>>") }
								a(style: "display:none", id: "${method.id}_suite-hidelink", title:"Error details", href:"javascript:hideStackTrace('${method.id}_suite')")  { text("<<<") }
								text(" ")
								a(href:"${method.upUrlSuite}") { text("${method.name}") }
								if (method.description != null && method.description != "") {
									br()
									text("${method.description}")
								}
								div(id:"${method.id}_suite", style: "display:none", class: "hidden") { text("Loading...") }
							}
							td(align: "center") { text("${DisplayUtil.methodFailDiff(method.previousResultSuite)}") }
						}
					}
				}
			}
		}
	}
}
else {
	text("No Test method failed")
}

if (my.configFailCount > 0) {
	h2("Failed Configuration Methods")

	table(id:"configFail-tbl-suite", border:"1px", class:"pane sortable") {
		thead() {
			tr() {
				th(class: "pane-header") { text("Method/Error detail") }
				th(class: "pane-header", style:"width:10em") { text("Failed Configuration Methods (Diff)") }
			}
		}
		tbody () {

			for (clazz in my.children) {
				if (clazz.failedConfigs.size() > 0) {

					for (method in clazz.failedConfigs) {
						tr() {
							td(align: "left") {
								a(id: "${method.id}_suite-showlink", title:"Error details", href:"javascript:showStackTrace('${method.id}_suite', '${method.upUrlSuite}/summary')") { text(">>>") }
								a(style: "display:none", id: "${method.id}_suite-hidelink", title:"Error details", href:"javascript:hideStackTrace('${method.id}_suite')")  { text("<<<") }
								text(" ")
								a(href:"${method.upUrlSuite}") { text("${method.name}") }
								if (method.description != null && method.description != "") {
									br()
									text("${method.description}")
								}
								div(id:"${method.id}_suite", style: "display:none", class: "hidden") { text("Loading...") }
							}
							td(align: "center") { text("${DisplayUtil.methodFailDiff(method.previousResultSuite)}") }
						}
					}
				}
			}
		}
	}
}

if (my.skipCount > 0) {
	h2("Skipped Tests")


	table(id:"skip-tbl-suite", border:"1px", class:"pane sortable") {
		thead() {
			tr() {
				th(class: "pane-header") { text("Method") }
				th(class: "pane-header", style:"width:10em") { text("Skipped Tests (Diff)") }
			}
		}
		tbody () {

			for (clazz in my.children) {
				if (clazz.skippedTests.size() > 0) {

					for (method in clazz.skippedTests) {
						tr() {
							td(align: "left") {
								a(href:"${method.upUrlSuite}") { text("${method.name}") }
								if (method.description != null && method.description != "") {
									br()
									text("${method.description}")
								}
							}
							td(align: "center") { text("${DisplayUtil.methodSkipDiff(method.previousResultSuite)}") }
						}
					}
				}
			}
		}
	}
}

if (my.configSkipCount > 0) {
	h2("Skipped Configuration Methods")

	table(id:"configSkip-tbl-suite", border:"1px", class:"pane sortable") {
		thead() {
			tr() {
				th(class: "pane-header") { text("Method") }
				th(class: "pane-header", style:"width:10em") { text("Skipped Configuration Methods (Diff)") }
			}
		}
		tbody () {


			for (clazz in my.children) {
				if (clazz.skippedConfigs.size() > 0) {

					for (method in clazz.skippedConfigs) {
						tr() {
							td(align: "left") {
								a(href:"${method.upUrlSuite}") { text("${method.name}") }
								if (method.description != null && method.description != "") {
									br()
									text("${method.description}")
								}
							}
							td(align: "center") { text("${DisplayUtil.methodSkipDiff(method.previousResultSuite)}") }
						}
					}
				}
			}
		}
	}
}


h2("Tests")

if(my.totalCount > 0) {


	table(id:"test-tbl-suite", border:"1px", class:"pane sortable") {
		thead() {
			tr() {
				th(class:"pane-header") { text("Method") }
				th(class:"pane-header", style:"width:10em", title:"Duration") { text("Duration") }
				th(class:"pane-header", style:"width:10em", title:"Status") { text("Status (Diff)") }
			}
		}
		tbody () {

			for (clazz in my.children) {

				for (method in clazz.testMethods) {
					tr() {
						td(align: "left") {
							a(href:"${method.upUrlSuite}") { text("${method.name}") }
							if (method.description != null && method.description != "") {
								br()
								text("${method.description}")
							}
						}
						td(align: "center") {
							text("${FormatUtil.formatTime(method.duration)}")
							td(align: "center", class: method.status.toLowerCase()) { text("${method.status}") }
						}
					}
				}
			}
		}
	}
}
else {
	test("No test method was found for this test")
}

h2("All Configuration Methods")

if(my.totalCount > 0) {



	table(id:"config-tbl-suite", border:"1px", class:"pane sortable") {
		thead() {
			tr() {
				th(class:"pane-header") { text("Method") }
				th(class:"pane-header", style:"width:10em", title:"Duration") { text("Duration") }
				th(class:"pane-header", style:"width:10em", title:"Status") { text("Status (Diff)") }
			}
		}
		tbody () {

			for (clazz in my.children) {
				for (method in clazz.configurationMethods) {
					tr() {
						td(align: "left") {
							a(href:"${method.upUrlSuite}") { text("${method.name}") }
							if (method.description != null && method.description != "") {
								br()
								text("${method.description}")
							}
						}
						td(align: "center") {
							text("${FormatUtil.formatTime(method.duration)}")
							td(align: "center", class: method.status.toLowerCase()) { text("${method.status}") }
						}
					}
				}
			}
		}
	}
}
else {
	test("No configuration method was found for this test")
}