package hudson.plugins.testng.results.SuiteResult

import hudson.plugins.testng.util.FormatUtil

f = namespace(lib.FormTagLib)
l = namespace(lib.LayoutTagLib)
t = namespace("/lib/hudson")
st = namespace("jelly:stapler")

script(src:"${app.rootUrl}/plugin/testng-plugin/js/show_more.js")

h1(${my.name})

h2("Tests")
a(href:"javascript:toggleTable('tests')") {
    text("hide/expand the table")
}

table(id:"tests", border:"1px", class:"pane sortable") {
    thead() {
        tr() {
            th(class:"pane-header") {
                text("Test")
            }
            th(class:"pane-header", style:"width:5em", title:"Duration") {
                text("Duration")
        }
    }
    tbody() {
        for (test in my.children) {
            def prevClazz = test.previousResult
            tr() {
                td(align:"left") {
                    a(href:"${test.upUrl}") {
                        text("${test.name}")
                    }
                }
                td(align:"center") {
                    text("${FormatUtil.formatTime(test.duration)}")
                }
            }
        }
    }
}