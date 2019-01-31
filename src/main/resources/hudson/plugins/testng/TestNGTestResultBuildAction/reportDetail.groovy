package hudson.plugins.testng.TestNGTestResultBuildAction

import hudson.plugins.testng.util.FormatUtil

f = namespace(lib.FormTagLib)
l = namespace(lib.LayoutTagLib)
t = namespace("/lib/hudson")
st = namespace("jelly:stapler")

public String setColorClass(result) {
	if (result.failCount > 0) return "fail";
	else if (result.skipCount > 0) return "skipped"
	else if (result.passCount > 0) return "pass"
	return "";
}

public String tableDisplay(display) {
	if (display) return "table";
	return "none";
}

link(href:"${app.rootUrl}plugin/testng-plugin/css/table.css", rel:"stylesheet", type:"text/css")

script(type: "text/javascript", src: "${app.rootUrl}plugin/testng-plugin/js/toggle_mthd_summary.js")
script(type: "text/javascript", src: "${app.rootUrl}plugin/testng-plugin/js/expand_collapse_table.js")


h2("Failed Tests")

if (my.result.failCount != 0) {
	//a(href: "javascript:toggleTable('fail-tbl')") { text("hide/expand the table") }
	table(id:"fail-tbl", border:"1px", class:"pane sortable") {
		thead() {
			tr() {
				th(class: "pane-header") { text("Test Method") }
				th(class: "pane-header") { text("Duration") }
			}
		}
		tbody() {
			for (failedTest in my.result.failedTests) {
				tr() {
					td(align: "left") {
						a(id: "${failedTest.id}-showlink", href:"javascript:showStackTrace('${failedTest.id}', '${failedTest.upUrl}/summary')") { text(">>>") }
						a(style: "display:none", id: "${failedTest.id}-hidelink", href:"javascript:hideStackTrace('${failedTest.id}')") { text("<<<") }
						text(" ")
						a(href:"${failedTest.upUrl}") { text("${failedTest.parent.canonicalName}.${failedTest.name}") }
						div(id:"${failedTest.id}", style: "display:none", class: "hidden") { text("Loading...") }
					}
					td(align: "right") { text("${FormatUtil.formatTime(failedTest.duration)}") }
				}
			}
		}
	}
} else {
	text("No Test method failed")
}

if (my.result.failedConfigCount != 0) {
	h2("Failed Configuration Methods")
	printMethods("Configuration", "fail-config-tbl", my.result.failedConfigs, true)
}

if (my.result.skipCount != 0) {
	h2("Skipped Tests")
	printMethods("Test", "skip-tbl", my.result.skippedTests, false)
}

if (my.result.skippedConfigCount != 0) {
	h2("Skipped Configuration Methods")
	printMethods("Configuration", "skip-config-tbl", my.result.skippedConfigs, false)
}


h2("All Tests (grouped by their suite)")

button(onclick:"expandTable('"+activeTable(my.result.packageView)+"')") { text("Expand the table") }

button(onclick:"collapseTable('"+activeTable(my.result.packageView)+"')") { text("Collapse the table") }

//button(onclick:"collapseTable('all-tbl')") { text("change display mode") }



	table(id:"all-tbl", border:"1px", class:"pane sortable", style: "display: "+pkgDisplay(my.result.packageView)+";") {
		thead() {
			tr() {
				th(class:"pane-header") { text("Suite") }
				th(class:"pane-header", style:"width:5em", title:"Duration") { text("Duration") }
				th(class:"pane-header", style:"width:5em", title:"Failed tests count") { text("Fail") }
				th(class:"pane-header", style:"width:5em", title:"Failed tests count diff") { text("(diff)") }
				th(class:"pane-header", style:"width:5em", title:"Skipped tests count") { text("Skip") }
				th(class:"pane-header", style:"width:5em", title:"Skipped tests count diff") { text("(diff)") }
				th(class:"pane-header", style:"width:5em", title:"Total tests count") { text("Total") }
				th(class:"pane-header", style:"width:5em", title:"Total tests count diff") { text("(diff)") }
				//            th(class:"pane-header", style:"width:5em", title:"Package Age") {
				//                text("Age")
				//            }
			}
		}
		tbody () {
			for (pkg in my.result.packageMap.values()) {
				def prevPkg = pkg.previousResult
				tr() {
					td(align: "left") {
						a(href:"${pkg.name}") { text("${pkg.name}") }
					}
					td(align: "center") { text("${FormatUtil.formatTime(pkg.duration)}") }
					td(align: "center") { text("${pkg.failCount}") }
					td(align: "center") { text("${FormatUtil.formatLong(prevPkg == null ? 0 : pkg.failCount - prevPkg.failCount)}") }
					td(align: "center") { text("${pkg.skipCount}") }
					td(align: "center") { text("${FormatUtil.formatLong(prevPkg == null ? 0 : pkg.skipCount - prevPkg.skipCount)}") }
					td(align: "center") { text("${pkg.totalCount}") }
					td(align: "center") { text("${FormatUtil.formatLong(prevPkg == null ? 0 : pkg.totalCount - prevPkg.totalCount)}") }
					//                td(align: "center") {
					//                    text("${pkg.age}")
					//                }
				}
			}
		}
	}



	//Second table to display results by suite
	table(id:"all-tbl", border:"1px", class:"pane sortable" style: "display: "+pkgDisplay(!my.result.packageView)+";") {
		thead() {
			tr() {
				th(class:"pane-header") { text("Suite/Test/Class/Method/Description") }
				th(class:"pane-header", style:"width:10em", title:"Duration") { text("Duration") }
				th(class:"pane-header", style:"width:10em", title:"Status") { text("Status") }

			}
		}
		tbody () {
			for (suite in my.result.suiteList) {
				def prevSuite = suite.previousResult
				tr(id:"${suite.safeName}") {
					td(align: "left", style:"padding-left:0.5em;") {
						span(title:"Show children", onclick:"expandTableRow('${suite.safeName}')", class:"expandIcon")
						a(href:"${suite.safeName}") { text("${suite.name}") }
					}
					td(align: "center") { text("${FormatUtil.formatTime(suite.duration)}") }
					td(align: "center", class: setColorClass(suite)) {
						text("Passed: " + "${suite.passCount}")
						br()
						text("Skipped: " + "${suite.skipCount}")
						br()
						text("Failed: " + "${suite.failCount}" ) }
				}
				for (test in suite.children) {
					tr(id:"${test.safeName}", parentRow:"${suite.safeName}", style:"display:none;") {
						td(align: "left", style:"padding-left:1.5em;") {
							span(title:"Show children", onclick:"expandTableRow('${test.safeName}')", class:"expandIcon")
							a(href:"${test.upUrl}") {text("${test.name}") }
						}
						td(align: "center") { text("${FormatUtil.formatTime(test.duration)}") }
						td(align: "center", class: setColorClass(test)) {
							text("Passed: " + "${test.passCount}")
							br()
							text("Skipped: " + "${test.skipCount}")
							br()
							text("Failed: " + "${test.failCount}" ) }
					}
					for (clazz in test.children) {
						tr(id:"${clazz.name}", parentRow:"${test.safeName}", style:"display:none;") {
							td(align: "left", style:"padding-left:2.5em;") {
								span(title:"Show children", onclick:"expandTableRow('${clazz.name}')", class:"expandIcon")
								a(href:"${clazz.upUrl}") { text("${clazz.name}") }
							}
							td(align: "center") { text("${FormatUtil.formatTime(clazz.duration)}") }
							td(align: "center", class: setColorClass(clazz)) {
								text("Passed: " + "${clazz.passCount}")
								br()
								text("Skipped: " + "${clazz.skipCount}")
								br()
								text("Failed: " + "${clazz.failCount}" ) }
						}
						for (method in clazz.children) {
							tr(id:"${method.name}", parentRow:"${clazz.name}", style:"display:none;") {
								td(align: "left", style:"padding-left:3.5em;") {
									a(href:"${method.upUrl}") { text("${method.name}") }
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
	}


/**
 * Prints out the tables containing information about methods executed during test
 *
 * @param type Description of the type of methods. Used as title of table
 * @param tableName unique name for the table
 * @param methodList list of methods that form the rows of the table
 * @param showMoreArrows if arrows should be shown with link to get more details about the methods
 * @return nothing
 */
def printMethods(type, tableName, methodList, showMoreArrows) {
a(href: "javascript:toggleTable('${tableName}')") { text("hide/expand the table") }
table(id:tableName, border:"1px", class:"pane sortable") {
thead() {
	tr() {
		th(class: "pane-header") { text("${type} Method") }
	}
}
tbody() {
	for (method in methodList) {
		tr() {
			td(align: "left") {
				if (showMoreArrows) {
					a(id: "${method.id}-showlink", href:"javascript:showStackTrace('${method.id}', '${method.upUrl}/summary')") { text(">>>") }
					a(style: "display:none", id: "${method.id}-hidelink", href:"javascript:hideStackTrace('${method.id}')") { text("<<<") }
					text(" ")
				}
				a(href:"${method.upUrl}") { text("${method.parent.canonicalName}.${method.name}") }
				if (showMoreArrows) {
					div(id:"${method.id}", style: "display:none", class: "hidden") { text("Loading...") }
				}
			}
		}
	}
}
}
}