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
@SuppressFBWarnings(value = "SE_BAD_FIELD", justification = "ArrayList is Serializable")
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
	private int passedConfigCount = 0;;
	private Map<String, PackageResult> packageMap = new HashMap<String, PackageResult>();
	// boolean to make sure fillTestMethodsLists() is only called once
	private boolean methodListsFilled = false;

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

	@Exported(name = "pass-config")
	public int getPassedConfigCount() {
		return passedConfigCount;
	}

	@Exported(name = "all-config")
	public int getTotalConfigCount() {
		return passedConfigCount + skippedConfigCount + failedConfigCount;
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

	/**
	 * Adds only the {@code <suite>}s that already aren't part of the list.
	 *
	 * @param suiteList List of suites
	 */
	// TODO: whats going on here? why unique?
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
		return run == null ? testngResult.run == null : run.equals(testngResult.run);
	}

	@Override
	public int hashCode() {
		int result;
		result = (run != null ? run.hashCode() : 0);
		return result;
	}

	@Override
	public String toString() {
		return String.format(
				"TestNGResult {" + "totalTests=%d, " + "failedTests=%d, skippedTests=%d, failedConfigs=%d, "
						+ "skippedConfigs=%d}", // name,
				passCount + failCount + skipCount, failCount, skipCount, failedConfigCount, skippedConfigCount);
	}

	public void fillTestMethodLists() {
		if (!methodListsFilled) {
			for (SuiteResult suite : suiteList) {
				for (TestNGTestResult test : suite.getTestList()) {
					for (ClassResult clazz : test.getClassList()) {
						for (MethodResult testMethod : clazz.getChildren()) {
							if (testMethod.isConfig()) {
								if ("FAIL".equals(testMethod.getStatus())) {
									this.getFailedConfigs().add(testMethod);
								} else if ("SKIP".equals(testMethod.getStatus())) {
									this.getSkippedConfigs().add(testMethod);
								} else if ("PASS".equals(testMethod.getStatus())) {
									this.passedConfigCount++;
								}
							} else {
								if ("FAIL".equals(testMethod.getStatus())) {
									this.getFailedTests().add(testMethod);
								} else if ("SKIP".equals(testMethod.getStatus())) {
									this.getSkippedTests().add(testMethod);
								} else if ("PASS".equals(testMethod.getStatus())) {
									this.getPassedTests().add(testMethod);
								}
							}
						}
					}
				}
			}
			methodListsFilled = true;
		}
	}

	/**
	 * Updates the calculated fields
	 */
	@Override
	public void tally() {
		packageMap.clear();
		for (SuiteResult suite : suiteList) {
			for (TestNGTestResult test : suite.getTestList()) {
				for (ClassResult _class : test.getClassList()) {
					_class.tally();
					String pkg = _class.getPkgName();
					if (packageMap.containsKey(pkg)) {
						Map<String, ClassResult> classResults = packageMap.get(pkg).getClassMap();
						_class.setPkgParent(packageMap.get(pkg));
						_class.setParent(packageMap.get(pkg));
						if (classResults.containsKey(_class.name)) {
							classResults.get(_class.name).addTestMethods(_class.getChildren());
							classResults.get(_class.name).tally();
						} else {
							classResults.put(_class.name, _class);
						}
					} else {
						PackageResult tpkg = new PackageResult(pkg);
						tpkg.getClassMap().put(_class.name, _class);
						tpkg.setParent(this);
						tpkg.setTestNGResult(this);
						_class.setPkgParent(tpkg);
						packageMap.put(pkg, tpkg);
					}

				}
				test.tally();
			}
			suite.setTestNGResult(this);
			suite.tally();
		}

		failedConfigCount = failedConfigurationMethods.size();
		skippedConfigCount = skippedConfigurationMethods.size();
		failCount = failedTests.size();
		passCount = passedTests.size();
		skipCount = skippedTests.size();

		startTime = Long.MAX_VALUE;
		endTime = 0;
		for (PackageResult pkgResult : packageMap.values()) {
			pkgResult.tally();
			if (this.startTime > pkgResult.getStartTime()) {
				startTime = pkgResult.getStartTime(); // cf. ClassResult#tally()
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
	public BaseResult getSuiteParent() {
		return null;
	}

	@Override
	public List<? extends BaseResult> getSuiteChildren() {
		return this.suiteList;
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
		return packageMap.values();
	}

	@Override
	public boolean hasChildren() {
		return packageMap.values().isEmpty();
	}

}
