package hudson.plugins.testng.results.TestNGTestResult

import hudson.plugins.testng.util.FormatUtil

f = namespace(lib.FormTagLib)
l = namespace(lib.LayoutTagLib)
t = namespace("/lib/hudson")
st = namespace("jelly:stapler")

int i;
int j;

link(href:"${app.rootUrl}plugin/testng-plugin/css/table.css", rel:"stylesheet", type:"text/css")

script(type: "text/javascript", src: "${app.rootUrl}plugin/testng-plugin/js/toggle_mthd_summary.js")
script(type: "text/javascript", src:"${app.rootUrl}/plugin/testng-plugin/js/toggle_table.js")
script(type: "text/javascript", src: "${app.rootUrl}plugin/testng-plugin/js/expand_collapse_table.js")
script(type: "text/javascript", src: "${app.rootUrl}plugin/testng-plugin/js/select_view.js")

h1("${my.name}")

h2("Failed Tests")

if (my.failCount > 0) {

	button(onclick:"expandTable('fail-tbl-suite')") { text("Expand the table") }

	button(onclick:"collapseTable('fail-tbl-suite')") { text("Collapse the table") }

	table(id:"fail-tbl-suite", border:"1px", class:"pane sortable") {
		thead() {
			tr() {
				th(class: "pane-header") { text("Suite/Test/Class/Method/Error detail") }
				th(class: "pane-header", style:"width:10em") { text("Failed Tests (Diff)") }
			}
		}
		tbody () {
			i=1;
			j=1;

			for (clazz in my.children) {
				if (clazz.failCount > 0) {
					tr(node:i) {
						td(align: "left", class:"rootRowPadding") {
							span(title:"Show children", onclick:"expandTableRow('${i}', 'fail-tbl-suite')", class:"expandIcon")
							text(" ")
							a(href:"${clazz.upUrlSuite}") { text("${clazz.name}") }
						}
						td(align: "center") { text("${clazz.failCount}"+ " ("+"${FormatUtil.formatLong(clazz.previousResult == null ? 0 : clazz.failCount - clazz.previousResult.failCount)}"+")") }
					}
				}
				for (method in clazz.failedTests) {
					tr(node:i+"."+j, parentRow:i, style:"display:none;") {
						td(align: "left", class:"subRow1Padding") {
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
						td(align: "center") { text("Duration: ${method.duration}") }
					}
					j++;
				}
				i++; j=0;
			}
		}
	}
}
else {
	text("No Test method failed")
}

if (my.configFailCount > 0) {
	h2("Failed Configuration Methods")

	button(onclick:"expandTable('configFail-tbl-suite')") { text("Expand the table") }

	button(onclick:"collapseTable('configFail-tbl-suite')") { text("Collapse the table") }

	table(id:"configFail-tbl-suite", border:"1px", class:"pane sortable") {
		thead() {
			tr() {
				th(class: "pane-header") { text("Suite/Test/Class/Method/Error detail") }
				th(class: "pane-header", style:"width:10em") { text("Failed Configuration Methods (Diff)") }
			}
		}
		tbody () {
			i=1;
			j=1;

			for (clazz in my.children) {
				if (clazz.failedConfigs.size() > 0) {
					tr(node:i) {
						td(align: "left", class:"rootRowPadding") {
							span(title:"Show children", onclick:"expandTableRow('${i}', 'configFail-tbl-suite')", class:"expandIcon")
							text(" ")
							a(href:"${clazz.upUrlSuite}") { text("${clazz.name}") }
						}
						td(align: "center") { text("${clazz.failedConfigs.size()}"+ " ("+"${FormatUtil.formatLong(clazz.previousResult == null ? 0 : clazz.failedConfigs.size() - clazz.previousResult.failedConfigs.size())}"+")") }
					}
				}
				for (method in clazz.failedConfigs) {
					tr(node:i+"."+j, parentRow:i, style:"display:none;") {
						td(align: "left", class:"subRow1Padding") {
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
						td(align: "center") { text("Duration: ${method.duration}") }
					}
					j++;
				}
				i++; j=0;
			}
		}
	}
}

if (my.skipCount > 0) {
	h2("Skipped Tests")

	button(onclick:"expandTable('skip-tbl-suite')") { text("Expand the table") }

	button(onclick:"collapseTable('skip-tbl-suite')") { text("Collapse the table") }

	table(id:"skip-tbl-suite", border:"1px", class:"pane sortable") {
		thead() {
			tr() {
				th(class: "pane-header") { text("Suite/Test/Class/Method") }
				th(class: "pane-header", style:"width:10em") { text("Skipped Tests (Diff)") }
			}
		}
		tbody () {
			i=1;
			j=1;
			for (clazz in my.children) {
				if (clazz.skippedTests.size() > 0) {
					tr(node:i) {
						td(align: "left", class:"rootRowPadding") {
							span(title:"Show children", onclick:"expandTableRow('${i}', 'skip-tbl-suite')", class:"expandIcon")
							text(" ")
							a(href:"${clazz.upUrlSuite}") { text("${clazz.name}") }
						}
						td(align: "center") { text("${clazz.skipCount}"+ " ("+"${FormatUtil.formatLong(clazz.previousResult == null ? 0 : clazz.skipCount - clazz.previousResult.skipCount)}"+")") }
					}
				}
				for (method in clazz.skippedTests) {
					tr(node:i+"."+j, parentRow:i, style:"display:none;") {
						td(align: "left", class:"subRow1Padding") {
							a(href:"${method.upUrlSuite}") { text("${method.name}") }
							if (method.description != null && method.description != "") {
								br()
								text("${method.description}")
							}
						}
						td(align: "center") { text("Duration: ${method.duration}") }
					}
					j++;
				}
				i++; j=0;
			}
		}
	}
}

if (my.configSkipCount > 0) {
	h2("Skipped Configuration Methods")

	button(onclick:"expandTable('configSkip-tbl-suite')") { text("Expand the table") }

	button(onclick:"collapseTable('configSkip-tbl-suite')") { text("Collapse the table") }

	table(id:"configSkip-tbl-suite", border:"1px", class:"pane sortable") {
		thead() {
			tr() {
				th(class: "pane-header") { text("Suite/Test/Class/Method") }
				th(class: "pane-header", style:"width:10em") { text("Skipped Configuration Methods (Diff)") }
			}
		}
		tbody () {
			i=1;
			j=1;

			for (clazz in my.children) {
				if (clazz.skippedConfigs.size() > 0) {
					tr(node:i) {
						td(align: "left", class:"rootRowPadding") {
							span(title:"Show children", onclick:"expandTableRow('${i}', 'configSkip-tbl-suite')", class:"expandIcon")
							text(" ")
							a(href:"${clazz.upUrlSuite}") { text("${clazz.name}") }
						}
						td(align: "center") { text("${clazz.skippedConfigs.size()}"+ " ("+"${FormatUtil.formatLong(clazz.previousResult == null ? 0 : clazz.skippedConfigs.size() - clazz.previousResult.skippedConfigs.size())}"+")") }
					}
				}
				for (method in clazz.skippedConfigs) {
					tr(node:i+"."+j, parentRow:i, style:"display:none;") {
						td(align: "left", class:"subRow1Padding") {
							a(href:"${method.upUrlSuite}") { text("${method.name}") }
							if (method.description != null && method.description != "") {
								br()
								text("${method.description}")
							}
						}
						td(align: "center") { text("Duration: ${method.duration}") }
					}
					j++;
				}
				i++; j=0;
			}
		}
	}
}


h2("Tests")

if(my.totalCount > 0) {

	button(onclick:"expandTable('test-tbl-suite')") { text("Expand the table") }

	button(onclick:"collapseTable('test-tbl-suite')") { text("Collapse the table") }


	table(id:"test-tbl-suite", border:"1px", class:"pane sortable") {
		thead() {
			tr() {
				th(class:"pane-header") { text("Class/Method") }
				th(class:"pane-header", style:"width:10em", title:"Duration") { text("Duration") }
				th(class:"pane-header", style:"width:10em", title:"Status") { text("Status (Diff)") }
			}
		}
		tbody () {
			i=1;
			j=1;
			for (clazz in my.children) {
				tr(node:i) {
					td(align: "left", class:"rootRowPadding") {
						span(title:"Show children", onclick:"expandTableRow('${i}', 'test-tbl-suite')", class:"expandIcon")
						text(" ")
						a(href:"${clazz.upUrlSuite}") { text("${clazz.name}") }
					}
					td(align: "center") { text("${FormatUtil.formatTime(clazz.duration)}") }
					td(align: "center", class: "${setColorClass(clazz)}") {
						text("Passed: " + "${clazz.passCount}"+ " ("+"${FormatUtil.formatLong(clazz.previousResult == null ? 0 : clazz.passCount - clazz.previousResult.passCount)}"+")")
						br()
						text("Skipped: " + "${clazz.skipCount}"+ " ("+"${FormatUtil.formatLong(clazz.previousResult == null ? 0 : clazz.skipCount - clazz.previousResult.skipCount)}"+")")
						br()
						text("Failed: " + "${clazz.failCount}" + " ("+"${FormatUtil.formatLong(clazz.previousResult == null ? 0 : clazz.failCount - clazz.previousResult.failCount)}"+")")
					}
				}
				for (method in clazz.testMethods) {
					tr(node:i+"."+j, parentRow:i, style:"display:none;") {
						td(align: "left", class:"subRow1Padding") {
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
					j++;
				}
				i++; j=0;
			}
		}
	}
}
else {
	test("No test method was found for this test")
}

h2("All Configuration Methods")

if(my.totalCount > 0) {

	button(onclick:"expandTable('config-tbl-suite')") { text("Expand the table") }

	button(onclick:"collapseTable('config-tbl-suite')") { text("Collapse the table") }


	table(id:"config-tbl-suite", border:"1px", class:"pane sortable") {
		thead() {
			tr() {
				th(class:"pane-header") { text("Class/Method") }
				th(class:"pane-header", style:"width:10em", title:"Duration") { text("Duration") }
				th(class:"pane-header", style:"width:10em", title:"Status") { text("Status (Diff)") }
			}
		}
		tbody () {
			i=1;
			j=1;

			for (clazz in my.children) {
				tr(node:i) {
					td(align: "left", class:"rootRowPadding") {
						span(title:"Show children", onclick:"expandTableRow('${i}', 'config-tbl-suite')", class:"expandIcon")
						text(" ")
						a(href:"${clazz.upUrlSuite}") { text("${clazz.name}") }
					}
					td(align: "center") { text("${FormatUtil.formatTime(clazz.duration)}") }
					td(align: "center", class: "${setColorClass(clazz)}") {
						text("Passed: " + "${clazz.passCount}"+ " ("+"${FormatUtil.formatLong(clazz.previousResultSuite == null ? 0 : clazz.passCount - clazz.previousResultSuite.passCount)}"+")")
						br()
						text("Skipped: " + "${clazz.skipCount}"+ " ("+"${FormatUtil.formatLong(clazz.previousResultSuite == null ? 0 : clazz.skipCount - clazz.previousResultSuite.skipCount)}"+")")
						br()
						text("Failed: " + "${clazz.failCount}" + " ("+"${FormatUtil.formatLong(clazz.previousResultSuite == null ? 0 : clazz.failCount - clazz.previousResultSuite.failCount)}"+")")
					}
				}
				for (method in clazz.configurationMethods) {
					tr(node:i+"."+j, parentRow:i+"."+j, style:"display:none;") {
						td(align: "left", class:"subRow1Padding") {
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
					j++;
				}
				i++; j=0;
			}
		}
	}
}
else {
	test("No configuration method was found for this test")
}