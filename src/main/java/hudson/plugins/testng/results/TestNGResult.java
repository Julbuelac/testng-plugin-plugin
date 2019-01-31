package hudson.plugins.testng.results;

import edu.umd.cs.findbugs.annotations.SuppressFBWarnings;
import java.io.Serializable;
import java.util.*;

import hudson.model.Run;
import hudson.plugins.testng.PluginImpl;
import hudson.tasks.test.TestResult;

import org.kohsuke.stapler.StaplerRequest;
import org.kohsuke.stapler.StaplerResponse;
import org.kohsuke.stapler.export.Exported;

/**
 * Represents all the results gathered for a single build (or a single suite,
 * while parsing the test results)
 *
 * @author nullin
 * @author farshidce
 */
@SuppressFBWarnings(value="SE_BAD_FIELD", justification="ArrayList is Serializable")
public class TestNGResult extends BaseResult implements Serializable {

    private static final long serialVersionUID = -3491974223665601995L;
    private List<SuiteResult> suiteList = new ArrayList<SuiteResult>();
    private List<MethodResult> passedTests = new ArrayList<MethodResult>();
    private List<MethodResult> failedTests = new ArrayList<MethodResult>();
    private List<MethodResult> skippedTests = new ArrayList<MethodResult>();
    private List<MethodResult> failedConfigurationMethods = new ArrayList<MethodResult>();
    private List<MethodResult> skippedConfigurationMethods = new ArrayList<MethodResult>();
    private long startTime;
    private long endTime;
    private int passCount;
    private int failCount;
    private int skipCount;
    private int failedConfigCount;
    private int skippedConfigCount;
    private Map<String, PackageResult> packageMap = new HashMap<String, PackageResult>();
    // Determines if the view shows the tests sorted by suite (default) or by package
    private boolean packageView = false;

    /**
     * @param name input name is ignored
     * @deprecated don't use this constructor
     */
    public TestNGResult(String name) {
        super(PluginImpl.URL);
    }

    public TestNGResult() {
        super(PluginImpl.URL);
    }

    @Override
    public String getTitle() {
        return getDisplayName();
    }

    @Override
    public List<MethodResult> getFailedTests() {
        return failedTests;
    }

    @Override
    public List<MethodResult> getPassedTests() {
        return passedTests;
    }

    @Override
    public List<MethodResult> getSkippedTests() {
        return skippedTests;
    }

    public List<MethodResult> getFailedConfigs() {
        return failedConfigurationMethods;
    }

    public List<MethodResult> getSkippedConfigs() {
        return skippedConfigurationMethods;
    }

    /**
     * Gets the total number of passed tests.
     */
    public int getPassCount() {
        return passCount;
    }

    /**
     * Gets the total number of failed tests.
     */
    @Exported
    public int getFailCount() {
        return failCount;
    }

    /**
     * Gets the total number of skipped tests.
     */
    @Exported
    public int getSkipCount() {
        return skipCount;
    }

    public List<SuiteResult> getSuiteList() {
        return suiteList;
    }

    @Exported(name = "total")
    public int getTotalCount() {
        return super.getTotalCount();
    }

    @Exported
    @Override
    public float getDuration() {
        return (float) (endTime - startTime) / 1000f;
    }

    @Exported(name = "fail-config")
    public int getFailedConfigCount() {
        return failedConfigCount;
    }

    @Exported(name = "skip-config")
    public int getSkippedConfigCount() {
        return skippedConfigCount;
    }

    @Exported(name = "package")
    public Collection<PackageResult> getPackageList() {
        return packageMap.values();
    }

    public Map<String, PackageResult> getPackageMap() {
        return packageMap;
    }

    public Set<String> getPackageNames() {
        return packageMap.keySet();
    }

    public boolean isPackageView() {
		return packageView;
	}

	public void setPackageView(boolean packageView) {
		this.packageView = packageView;
	}

	/**
     * Adds only the {@code <test>}s that already aren't part of the list.
     *
     * @param testList
     */
    //TODO: whats going on here? why unique?
    public void addUniqueSuites(List<SuiteResult> suiteList) {
        Set<SuiteResult> tmpSet = new HashSet<SuiteResult>(this.suiteList);
        tmpSet.addAll(suiteList);
        this.suiteList = new ArrayList<SuiteResult>(tmpSet);
    }

    public void setRun(Run<?, ?> run) {
        this.run = run;
        for (PackageResult pkg : packageMap.values()) {
            pkg.setRun(run);
        }
        for (SuiteResult suite : suiteList) {
            suite.setRun(run);
        }
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        TestNGResult testngResult = (TestNGResult) o;
        return run == null ? testngResult.run == null
                : run.equals(testngResult.run);
    }

    @Override
    public int hashCode() {
        int result;
        result = (run != null ? run.hashCode() : 0);
        return result;
    }

    @Override
    public String toString() {
        return String.format("TestNGResult {" +
                "totalTests=%d, " +
                "failedTests=%d, skippedTests=%d, failedConfigs=%d, " +
                "skippedConfigs=%d}", //name,
                passCount + failCount + skipCount, failCount,
                skipCount, failedConfigCount,
                skippedConfigCount);
    }

    /**
     * Updates the calculated fields
     */
    @Override
    public void tally() {
        failedConfigCount = failedConfigurationMethods.size();
        skippedConfigCount = skippedConfigurationMethods.size();
        failCount = failedTests.size();
        passCount = passedTests.size();
        skipCount = skippedTests.size();

        packageMap.clear();
        for(SuiteResult _suite : suiteList) {
	        for (TestNGTestResult _test : _suite.getTestList()) {
	            for (ClassResult _class : _test.getClassList()) {
	            	_class.tally();
	            	ClassResult _classCopy = new ClassResult(_class.getPkgName(),_class.getName());
	                String pkg = _classCopy.getPkgName();
	                if (packageMap.containsKey(pkg)) {
	                    List<ClassResult> classResults = packageMap.get(pkg).getChildren();
	                    if (!classResults.contains(_classCopy)) {
	                        classResults.add(_classCopy);
	                    }
	                } else {
	                    PackageResult tpkg = new PackageResult(pkg);
	                    tpkg.getChildren().add(_classCopy);
	                    tpkg.setParent(this);
	                    packageMap.put(pkg, tpkg);
	                }
	            }
	            _test.tally();
	        }
	        _suite.setParent(this);
	        _suite.tally();
        }

        startTime = Long.MAX_VALUE;
        endTime = 0;
        for (PackageResult pkgResult : packageMap.values()) {
            pkgResult.tally();
            if (this.startTime > pkgResult.getStartTime()) {
                startTime = pkgResult.getStartTime(); //cf. ClassResult#tally()
            }
            if (this.endTime < pkgResult.getEndTime()) {
                endTime = pkgResult.getEndTime();
            }
        }
        
    }

    @Exported(visibility = 999)
    public String getName() {
        return name;
    }

    @Override
    public BaseResult getParent() {
        return null;
    }

    @Override
    public String getDisplayName() {
        return getName();
    }
    
    @Override
    public Object getDynamic(String token, StaplerRequest req, StaplerResponse rsp) {
        for (TestResult result : this.getChildren()) {
            if (token.equals(result.getSafeName())) {
                return result;
            }
        }
        return null;
    }

    @Override
    public Collection<? extends BaseResult> getChildren() {
       if(packageView) return packageMap.values();
       else return suiteList;
    }

    @Override
    public boolean hasChildren() {
    	if(packageView) return packageMap.values().isEmpty();
    	else return !suiteList.isEmpty();
    }

}
