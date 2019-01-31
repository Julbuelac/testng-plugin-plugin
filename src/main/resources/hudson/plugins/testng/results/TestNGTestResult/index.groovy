package hudson.plugins.testng.results.TestNGTestResult

f = namespace(lib.FormTagLib)
l = namespace(lib.LayoutTagLib)
t = namespace("/lib/hudson")
st = namespace("jelly:stapler")

l.layout(title: "Test - ${my.name}") {
    st.include(page: "sidepanel.jelly", it: my.run)
    l.main_panel() {

        h1("Test ${my.name}")
        st.include(page: "reportDetail.groovy")
    }
}