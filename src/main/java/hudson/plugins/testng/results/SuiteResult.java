package hudson.plugins.testng.results;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.kohsuke.stapler.StaplerRequest;
import org.kohsuke.stapler.StaplerResponse;

import edu.umd.cs.findbugs.annotations.SuppressFBWarnings;
import hudson.model.Run;
import jenkins.model.Jenkins;

/**
 * Represents a single TestNG suite {@code <suite>} tag.
 *
 * @author julbuelac
 */
@SuppressFBWarnings(value = "SE_NO_SERIALVERSIONID", justification = "XStream does not care")
@SuppressWarnings("serial")
public class SuiteResult extends BaseResult {

	// List of tests
	private List<TestNGTestResult> testList = new ArrayList<TestNGTestResult>();

	private transient float duration;
	private transient long startedAt;
	private transient long endedAt;
	private transient int fail;
	private transient int skip;
	private transient int pass;
	private transient int configFail;
	private transient int configSkip;
	private transient int configPass;
	private TestNGResult testNGResult;

	public SuiteResult(String name, String duration, long startedAt) {
		super(name);
		this.startedAt = startedAt;
		try {
			long durationMs = Long.parseLong(duration);
			// more accurate end time when test took less than a second to run
			this.endedAt = startedAt + durationMs;
			this.duration = (float) durationMs / 1000f;
		} catch (NumberFormatException e) {
			System.err.println("Unable to parse duration value: " + duration);
		}
	}

	public int getConfigFailCount() {
		return configFail;
	}

	public int getConfigSkipCount() {
		return configSkip;
	}

	public int getConfigPassCount() {
		return configPass;
	}

	public int getTotalConfigCount() {
		return configPass + configSkip + configFail;
	}

	public List<TestNGTestResult> getTestList() {
		return testList;
	}

	/**
	 * Adds only the tests that already aren't part of the list
	 *
	 * @param testList list of test results
	 */
	public void addTestList(List<TestNGTestResult> testList) {
		Set<TestNGTestResult> tmpSet = new HashSet<TestNGTestResult>(this.testList);
		tmpSet.addAll(testList);
		this.testList = new ArrayList<TestNGTestResult>(tmpSet);
	}

	public float getDuration() {
		return duration;
	}

	public void setDuration(float duration) {
		this.duration = duration;
	}

	public long getStartedAt() {
		return startedAt;
	}

	public void setStartedAt(long startedAt) {
		this.startedAt = startedAt;
	}

	public long getEndedAt() {
		return endedAt;
	}

	public void setEndedAt(long endedAt) {
		this.endedAt = endedAt;
	}

	public int getFailCount() {
		return fail;
	}

	public int getSkipCount() {
		return skip;
	}

	public int getPassCount() {
		return pass;
	}

	public String getUpUrlSuite() {
		Jenkins j = Jenkins.getInstance();
		return j != null ? j.getRootUrl() + run.getUrl() + getSuiteUrl() : "";
	}

	public String getSuiteUrl() {
		return this.getTestNGResult().getSafeName() + "/suite/" + this.getSafeName() + "/";
	}

	public TestNGResult getTestNGResult() {
		return testNGResult;
	}

	public void setTestNGResult(TestNGResult testNGResult) {
		this.testNGResult = testNGResult;
	}

	@Override
	public void setRun(Run<?, ?> run) {
		super.setRun(run);
		for (TestNGTestResult _t : this.testList) {
			_t.setRun(run);
		}
	}

	@Override
	public Object getDynamic(String token, StaplerRequest req, StaplerResponse rsp) {
		for (TestNGTestResult result : this.getChildren()) {
			if (token.equals(result.getSafeName())) {
				return result;
			}
		}
		return null;
	}

	@Override
	public List<TestNGTestResult> getChildren() {
		return testList;
	}

	@Override
	public boolean hasChildren() {
		return testList != null && !testList.isEmpty();
	}

	public void tally() {
		this.fail = 0;
		this.skip = 0;
		this.pass = 0;
		this.configFail = 0;
		this.configSkip = 0;
		this.configPass = 0;

		for (TestNGTestResult testResult : testList) {
			this.fail += testResult.getFailCount();
			this.skip += testResult.getSkipCount();
			this.pass += testResult.getPassCount();
			this.configFail += testResult.getConfigFailCount();
			this.configSkip += testResult.getConfigSkipCount();
			this.configPass += testResult.getConfigPassCount();
			testResult.setParentSuite(this);
		}
	}

	@Override
	public BaseResult getSuiteParent() {
		return this.testNGResult;
	}

	@Override
	public List<? extends BaseResult> getSuiteChildren() {
		return this.testList;
	}

}
