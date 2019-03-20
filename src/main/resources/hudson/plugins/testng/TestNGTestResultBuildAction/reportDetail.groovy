package hudson.plugins.testng.TestNGTestResultBuildAction

import hudson.plugins.testng.util.FormatUtil;
import hudson.plugins.testng.util.DisplayUtil;

f = namespace(lib.FormTagLib)
l = namespace(lib.LayoutTagLib)
t = namespace("/lib/hudson")
st = namespace("jelly:stapler")


public String pkgDisplay(display) {
	if (display) return "block";
	return "none";
}

int i;
int j;
int k;

link(href:"${app.rootUrl}plugin/testng-plugin/css/table.css", rel:"stylesheet", type:"text/css")

script(type: "text/javascript"){ text("var showPkgView = ${my.packageView};") }
script(type: "text/javascript", src: "${app.rootUrl}plugin/testng-plugin/js/toggle_mthd_summary.js")
script(type: "text/javascript", src:"${app.rootUrl}/plugin/testng-plugin/js/toggle_table.js")
script(type: "text/javascript", src: "${app.rootUrl}plugin/testng-plugin/js/expand_collapse_table.js")
script(type: "text/javascript", src: "${app.rootUrl}plugin/testng-plugin/js/select_view.js")

if(my.packageView) {
	button(id: "toggleViewButton", onclick:"toggleView()") { text("Switch to suite view") }
}
else {
	button(id: "toggleViewButton", onclick:"toggleView()") { text("Switch to package view") }
}


div(class:"pkgView", style: "display: "+ pkgDisplay(my.packageView)){

	h2("Failed Tests")

	if (my.result.failCount != 0) {
		a(href: "javascript:toggleTable('fail-tbl')") { text("hide/expand the table") }
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
							a(href:"${failedTest.upUrlPkg}") { text("${failedTest.parent.canonicalName}.${failedTest.name}") }
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



	h2("All Tests (grouped by their package)")

	a(href:"javascript:toggleTable('all-tbl-pkg')") { text("hide/expand the table") }


	table(id:"all-tbl-pkg", border:"1px", class:"pane sortable") {
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
						a(href:"package/${pkg.name}") { text("${pkg.name}") }
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
}

div(class:"suiteView", style: "display: " + pkgDisplay(!my.packageView)){

	h2("Failed Tests")

	if (my.result.failCount > 0) {

		button(onclick:"expandTable('fail-tbl-suite')") { text("Expand the table") }

		button(onclick:"collapseTable('fail-tbl-suite')") { text("Collapse the table") }

		table(id:"fail-tbl-suite", border:"1px", class:"pane sortable") {
			thead() {
				tr() {
					th(class: "pane-header") { text("Suite/Test/Method/Error detail") }
					th(class: "pane-header", style:"width:10em") { text("Failed Tests (Diff)") }
				}
			}
			tbody () {
				i=1;
				j=1;
				k=1;
				for (suite in my.result.suiteList) {
					if (suite.failCount > 0) {
						tr(node:i) {
							td(align: "left", class:"rootRowPadding") {
								span(title:"show/hide children", onclick:"expandTableRow('${i}', 'fail-tbl-suite')", class:"expandIcon")
								text(" ")
								a(href:"${suite.upUrlSuite}") { text("${suite.name}") }
							}
							td(align: "center") { text("${suite.failCount}"+ " ("+"${FormatUtil.formatLong(suite.previousResultSuite == null ? 0 : suite.failCount - suite.previousResultSuite.failCount)}"+")") }
						}
						for (test in suite.children) {
							if (test.failCount > 0) {
								tr(node:i+"."+j, parentRow:i, style:"display:none;") {
									td(align: "left", class:"subRow1Padding") {
										span(title:"show/hide children", onclick:"expandTableRow('${i+"."+j}', 'fail-tbl-suite')", class:"expandIcon")
										text(" ")
										a(href:"${test.upUrlSuite}") { text("${test.name}") }
									}
									td(align: "center") { text("${test.failCount}"+ " ("+"${FormatUtil.formatLong(test.previousResultSuite == null ? 0 : test.failCount - test.previousResultSuite.failCount)}"+")") }
								}

								for (clazz in test.children) {
									if (clazz.failCount > 0) {
										for (method in clazz.failedTests) {
											tr(node:i+"."+j+"."+k, parentRow:i+"."+j, style:"display:none;") {
												td(align: "left", class:"subRow2Padding") {
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
										k++;
									}
								}
								j++; k=1;
							}
						}
						i++; j=1;
					}
				}
			}
		}
	} else {
		text("No Test method failed")
	}

	if (my.result.failedConfigCount > 0) {
		h2("Failed Configuration Methods")

		button(onclick:"expandTable('configFail-tbl-suite')") { text("Expand the table") }

		button(onclick:"collapseTable('configFail-tbl-suite')") { text("Collapse the table") }

		table(id:"configFail-tbl-suite", border:"1px", class:"pane sortable") {
			thead() {
				tr() {
					th(class: "pane-header") { text("Suite/Test/Method/Error detail") }
					th(class: "pane-header", style:"width:10em") { text("Failed Configuration Methods (Diff)") }
				}
			}
			tbody () {
				i=1;
				j=1;
				k=1;
				for (suite in my.result.suiteList) {
					if (suite.configFailCount > 0) {
						tr(node:i) {
							td(align: "left", class:"rootRowPadding") {
								span(title:"show/hide children", onclick:"expandTableRow('${i}', 'configFail-tbl-suite')", class:"expandIcon")
								text(" ")
								a(href:"${suite.upUrlSuite}") { text("${suite.name}") }
							}
							td(align: "center") { text("${suite.configFailCount}"+ " ("+"${FormatUtil.formatLong(suite.previousResultSuite == null ? 0 : suite.configFailCount - suite.previousResultSuite.configFailCount)}"+")") }
						}
						for (test in suite.children) {
							if (test.configFailCount > 0) {
								tr(node:i+"."+j, parentRow:i, style:"display:none;") {
									td(align: "left", class:"subRow1Padding") {
										span(title:"show/hide children", onclick:"expandTableRow('${i+"."+j}', 'configFail-tbl-suite')", class:"expandIcon")
										text(" ")
										a(href:"${test.upUrlSuite}") { text("${test.name}") }
									}
									td(align: "center") { text("${test.configFailCount}"+ " ("+"${FormatUtil.formatLong(test.previousResultSuite == null ? 0 : test.configFailCount - test.previousResultSuite.configFailCount)}"+")") }
								}
								for (clazz in test.children) {
									if (clazz.failedConfigs.size() > 0) {
										for (method in clazz.failedConfigs) {
											tr(node:i+"."+j+"."+k, parentRow:i+"."+j, style:"display:none;") {
												td(align: "left", class:"subRow2Padding") {
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
											k++;
										}
									}
								}
								j++; k=0;
							}
						}
						i++; j=0;
					}
				}
			}
		}
	}

	if (my.result.skipCount > 0) {
		h2("Skipped Tests")

		button(onclick:"expandTable('skip-tbl-suite')") { text("Expand the table") }

		button(onclick:"collapseTable('skip-tbl-suite')") { text("Collapse the table") }

		table(id:"skip-tbl-suite", border:"1px", class:"pane sortable") {
			thead() {
				tr() {
					th(class: "pane-header") { text("Suite/Test/Method") }
					th(class: "pane-header", style:"width:10em") { text("Skipped Tests (Diff)") }
				}
			}
			tbody () {
				i=1;
				j=1;
				k=1;
				for (suite in my.result.suiteList) {
					if (suite.skipCount > 0) {
						tr(node:i) {
							td(align: "left", class:"rootRowPadding") {
								span(title:"show/hide children", onclick:"expandTableRow('${i}', 'skip-tbl-suite')", class:"expandIcon")
								text(" ")
								a(href:"${suite.upUrlSuite}") { text("${suite.name}") }
							}
							td(align: "center") { text("${suite.skipCount}"+ " ("+"${FormatUtil.formatLong(suite.previousResultSuite == null ? 0 : suite.skipCount - suite.previousResultSuite.skipCount)}"+")") }
						}
						for (test in suite.children) {
							if (test.skipCount > 0) {
								tr(node:i+"."+j, parentRow:i, style:"display:none;") {
									td(align: "left", class:"subRow1Padding") {
										span(title:"show/hide children", onclick:"expandTableRow('${i+"."+j}', 'skip-tbl-suite')", class:"expandIcon")
										text(" ")
										a(href:"${test.upUrlSuite}") { text("${test.name}") }
									}
									td(align: "center") { text("${test.skipCount}"+ " ("+"${FormatUtil.formatLong(test.previousResultSuite == null ? 0 : test.skipCount - test.previousResultSuite.skipCount)}"+")") }
								}

								for (clazz in test.children) {
									if (clazz.skippedTests.size() > 0) {
										for (method in clazz.skippedTests) {
											tr(node:i+"."+j+"."+k, parentRow:i+"."+j, style:"display:none;") {
												td(align: "left", class:"subRow2Padding") {
													a(href:"${method.upUrlSuite}") { text("${method.name}") }
													if (method.description != null && method.description != "") {
														br()
														text("${method.description}")
													}
												}
												td(align: "center") { text("${DisplayUtil.methodSkipDiff(method.previousResultSuite)}") }
											}
											k++;
										}
									}
								}
								j++; k=1;
							}
						}
						i++; j=1;
					}
				}
			}
		}
	}

	if (my.result.skippedConfigCount > 0) {
		h2("Skipped Configuration Methods")

		button(onclick:"expandTable('configSkip-tbl-suite')") { text("Expand the table") }

		button(onclick:"collapseTable('configSkip-tbl-suite')") { text("Collapse the table") }

		table(id:"configSkip-tbl-suite", border:"1px", class:"pane sortable") {
			thead() {
				tr() {
					th(class: "pane-header") { text("Suite/Test/Method") }
					th(class: "pane-header", style:"width:10em") { text("Skipped Configuration Methods (Diff)") }
				}
			}
			tbody () {
				i=1;
				j=1;
				k=1;
				for (suite in my.result.suiteList) {
					if (suite.configSkipCount > 0) {
						tr(node:i) {
							td(align: "left", class:"rootRowPadding") {
								span(title:"show/hide children", onclick:"expandTableRow('${i}', 'configSkip-tbl-suite')", class:"expandIcon")
								text(" ")
								a(href:"${suite.upUrlSuite}") { text("${suite.name}") }
							}
							td(align: "center") { text("${suite.configSkipCount}"+ " ("+"${FormatUtil.formatLong(suite.previousResultSuite == null ? 0 : suite.configSkipCount - suite.previousResultSuite.configSkipCount)}"+")") }
						}
						for (test in suite.children) {
							if (test.configSkipCount > 0) {
								tr(node:i+"."+j, parentRow:i, style:"display:none;") {
									td(align: "left", class:"subRow1Padding") {
										span(title:"show/hide children", onclick:"expandTableRow('${i+"."+j}', 'configSkip-tbl-suite')", class:"expandIcon")
										text(" ")
										a(href:"${test.upUrlSuite}") { text("${test.name}") }
									}
									td(align: "center") { text("${test.configSkipCount}"+ " ("+"${FormatUtil.formatLong(test.previousResultSuite == null ? 0 : test.configSkipCount - test.previousResultSuite.configSkipCount)}"+")") }
								}

								for (clazz in test.children) {
									if (clazz.skippedConfigs.size() > 0) {

										for (method in clazz.skippedConfigs) {
											tr(node:i+"."+j+"."+k+"."+l, parentRow:i+"."+j+"."+k, style:"display:none;") {
												td(align: "left", class:"subRow2Padding") {
													a(href:"${method.upUrlSuite}") { text("${method.name}") }
													if (method.description != null && method.description != "") {
														br()
														text("${method.description}")
													}
												}
												td(align: "center") { text("${DisplayUtil.methodSkipDiff(method.previousResultSuite)}") }
											}

											k++;
										}
									}
								}
								j++; k=1;
							}
						}
						i++; j=1;
					}
				}
			}
		}
	}

	h2("All Tests")

	if(my.result.totalCount > 0) {

		button(onclick:"expandTable('test-tbl-suite')") { text("Expand the table") }

		button(onclick:"collapseTable('test-tbl-suite')") { text("Collapse the table") }


		table(id:"test-tbl-suite", border:"1px", class:"pane sortable") {
			thead() {
				tr() {
					th(class:"pane-header") { text("Suite/Test/Method") }
					th(class:"pane-header", style:"width:10em", title:"Duration") { text("Duration") }
					th(class:"pane-header", style:"width:10em", title:"Status") { text("Status (Diff)") }
				}
			}
			tbody () {
				i=1;
				j=1;
				k=1;
				for (suite in my.result.suiteList) {
					tr(node:i) {
						td(align: "left", class:"rootRowPadding") {
							span(title:"show/hide children", onclick:"expandTableRow('${i}', 'test-tbl-suite')", class:"expandIcon")
							text(" ")
							a(href:"${suite.upUrlSuite}") { text("${suite.name}") }
						}
						td(align: "center") { text("${FormatUtil.formatTime(suite.duration)}") }
						td(align: "center", class: "${DisplayUtil.setColorClass(suite)}") {
							text("Passed: " + "${suite.passCount}" + " ("+"${FormatUtil.formatLong(suite.previousResultSuite == null ? 0 : suite.passCount - suite.previousResultSuite.passCount)}"+")")
							br()
							text("Skipped: " + "${suite.skipCount}"+ " ("+"${FormatUtil.formatLong(suite.previousResultSuite == null ? 0 : suite.skipCount - suite.previousResultSuite.skipCount)}"+")")
							br()
							text("Failed: " + "${suite.failCount}" + " ("+"${FormatUtil.formatLong(suite.previousResultSuite == null ? 0 : suite.failCount - suite.previousResultSuite.failCount)}"+")")
						}
					}
					for (test in suite.children) {
						tr(node:i+"."+j, parentRow:i,  style:"display:none;") {
							td(align: "left", class:"subRow1Padding") {
								span(title:"show/hide children", onclick:"expandTableRow('${i+"."+j}', 'test-tbl-suite')", class:"expandIcon")
								text(" ")
								a(href:"${test.upUrlSuite}") {text("${test.name}") }
							}
							td(align: "center") { text("${FormatUtil.formatTime(test.duration)}") }
							td(align: "center", class: "${DisplayUtil.setColorClass(test)}") {
								text("Passed: " + "${test.passCount}"+ " ("+"${FormatUtil.formatLong(test.previousResultSuite == null ? 0 : test.passCount - test.previousResultSuite.passCount)}"+")")
								br()
								text("Skipped: " + "${test.skipCount}"+ " ("+"${FormatUtil.formatLong(test.previousResultSuite == null ? 0 : test.skipCount - test.previousResultSuite.skipCount)}"+")")
								br()
								text("Failed: " + "${test.failCount}" + " ("+"${FormatUtil.formatLong(test.previousResultSuite == null ? 0 : test.failCount - test.previousResultSuite.failCount)}"+")")
							}
						}
						for (clazz in test.children) {
							for (method in clazz.testMethods) {
								tr(node:i+"."+j+"."+k, parentRow:i+"."+j, style:"display:none;") {
									td(align: "left", class:"subRow2Padding") {
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
								k++;
							}
						}
						j++; k=1;
					}
					i++; j=1;
				}
			}
		}
	}
	else {
		test("No test method was found for this run")
	}

	h2("All Configuration Methods")
	if(my.result.totalConfigCount > 0) {

		button(onclick:"expandTable('config-tbl-suite')") { text("Expand the table") }

		button(onclick:"collapseTable('config-tbl-suite')") { text("Collapse the table") }


		table(id:"config-tbl-suite", border:"1px", class:"pane sortable") {
			thead() {
				tr() {
					th(class:"pane-header") { text("Suite/Test/Method") }
					th(class:"pane-header", style:"width:10em", title:"Duration") { text("Duration") }
					th(class:"pane-header", style:"width:10em", title:"Status") { text("Status (Diff)") }
				}
			}
			tbody () {
				i=1;
				j=1;
				k=1;
				for (suite in my.result.suiteList) {
					if(suite.getTotalConfigCount() != 0) {
						tr(node:i) {
							td(align: "left", class:"rootRowPadding") {
								span(title:"show/hide children", onclick:"expandTableRow('${i}', 'config-tbl-suite')", class:"expandIcon")
								text(" ")
								a(href:"${suite.upUrlSuite}") { text("${suite.name}") }
							}
							td(align: "center") { text("${FormatUtil.formatTime(suite.duration)}") }
							td(align: "center", class: "${DisplayUtil.setColorClassConfig(suite)}") {
								text("Passed: " + "${suite.configPassCount}" + " ("+"${FormatUtil.formatLong(suite.previousResultSuite == null ? 0 : suite.configPassCount - suite.previousResultSuite.configPassCount)}"+")")
								br()
								text("Skipped: " + "${suite.configSkipCount}"+ " ("+"${FormatUtil.formatLong(suite.previousResultSuite == null ? 0 : suite.configSkipCount - suite.previousResultSuite.configSkipCount)}"+")")
								br()
								text("Failed: " + "${suite.configFailCount}" + " ("+"${FormatUtil.formatLong(suite.previousResultSuite == null ? 0 : suite.configFailCount - suite.previousResultSuite.configFailCount)}"+")")
							}
						}
						for (test in suite.children) {
							if(test.getTotalConfigCount() != 0) {
								tr(node:i+"."+j, parentRow:i,  style:"display:none;") {
									td(align: "left", class:"subRow1Padding") {
										span(title:"show/hide children", onclick:"expandTableRow('${i+"."+j}', 'config-tbl-suite')", class:"expandIcon")
										text(" ")
										a(href:"${test.upUrlSuite}") {text("${test.name}") }
									}
									td(align: "center") { text("${FormatUtil.formatTime(test.duration)}") }
									td(align: "center", class: "${DisplayUtil.setColorClassConfig(test)}") {
										text("Passed: " + "${test.configPassCount}"+ " ("+"${FormatUtil.formatLong(test.previousResultSuite == null ? 0 : test.configPassCount - test.previousResultSuite.configPassCount)}"+")")
										br()
										text("Skipped: " + "${test.configSkipCount}"+ " ("+"${FormatUtil.formatLong(test.previousResultSuite == null ? 0 : test.configSkipCount - test.previousResultSuite.configSkipCount)}"+")")
										br()
										text("Failed: " + "${test.configFailCount}" + " ("+"${FormatUtil.formatLong(test.previousResultSuite == null ? 0 : test.configFailCount - test.previousResultSuite.configFailCount)}"+")")
									}
								}
								for (clazz in test.children) {
									for (method in clazz.configurationMethods) {
										tr(node:i+"."+j+"."+k, parentRow:i+"."+j, style:"display:none;") {
											td(align: "left", class:"subRow2Padding") {
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
										k++;
									}
								}
								j++; k=1;
							}
						}
						i++; j=1;
					}
				}
			}
		}
	}
	else {
		test("No configuration method was found for this run")
	}
}

/**
 * Prints out the tables containing information about methods executed during test
 *
 * @param type Description of the type of methods. Used as title of table
 * @param tableName unique name for the table
 * @param methodList list of methods that form the rows of the table
 * @param showMoreArrows if arrows should be shown with link to get more details about the methods
 * @param pkgView selects if the tables display the results by package or by suite
 * @return nothing
 */
def printMethods(type, tableName, methodList, showMoreArrows) {
	button(onclick:"expandTable('${tableName}')") { text("Expand the table") }

	button(onclick:"collapseTable('${tableName}')") { text("Collapse the table") }

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
							a(id: "${method.id}-showlink", href:"javascript:showStackTrace('${method.id}', '${method.upUrlPkg}/summary')") { text(">>>") }
							a(style: "display:none", id: "${method.id}-hidelink", href:"javascript:hideStackTrace('${method.id}')") { text("<<<") }
							text(" ")
						}
						a(href:"${method.upUrlPkg}") { text("${method.parent.canonicalName}.${method.name}") }
						if (showMoreArrows) {
							div(id:"${method.id}", style: "display:none", class: "hidden") { text("Loading...") }
						}
					}
				}
			}
		}
	}
}

