package hudson.plugins.testng.results.TestNGTestResult

import hudson.plugins.testng.util.FormatUtil

f = namespace(lib.FormTagLib)
l = namespace(lib.LayoutTagLib)
t = namespace("/lib/hudson")
st = namespace("jelly:stapler")

script(src:"${app.rootUrl}/plugin/testng-plugin/js/show_more.js")

h1(${my.name})

h2("Tests")
a(href:"javascript:toggleTable('tests')") { text("hide/expand the table") }

table(id:"Classes", border:"1px", class:"pane sortable") {
	thead() {
		tr() {
			th(class:"pane-header") { text("Test") }
			th(class:"pane-header", style:"width:5em", title:"Duration") { text("Duration") }
		}
		tbody() {
			for (clazz in my.children) {
				def prevClazz = clazz.previousResult
				tr() {
					td(align:"left") {
						a(href:"${clazz.name}") { text("${clazz.name}") }
					}
					td(align:"center") { text("${FormatUtil.formatTime(clazz.duration)}") }
				}
			}
		}
	}
}