package hudson.plugins.testng.results.SuiteResult

f = namespace(lib.FormTagLib)
l = namespace(lib.LayoutTagLib)
t = namespace("/lib/hudson")
st = namespace("jelly:stapler")

l.layout(title: "Test Suite - ${my.name}") {
    st.include(page: "sidepanel.jelly", it: my.run)
    l.main_panel() {

        h1("Suite ${my.name}")
        st.include(page: "bar.groovy")
        st.include(page: "reportDetail.groovy")
    }
}