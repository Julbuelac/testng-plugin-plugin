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
 * Represents a single TestNG XML {@code <test>} tag.
 *
 * @author julbuelac
 */
@SuppressFBWarnings(value = "SE_NO_SERIALVERSIONID", justification = "XStream does not care")
@SuppressWarnings("serial")
public class TestNGTestResult extends BaseResult {

	// list of test classes
	private List<ClassResult> classList = new ArrayList<ClassResult>();

	private transient long startTime;
	private transient long endTime;
	private transient float duration;
	private transient int fail;
	private transient int skip;
	private transient int pass;
	private transient int configFail;
	private transient int configSkip;
	private transient int configPass;
	private SuiteResult parentSuite;

	public TestNGTestResult(String name, long startTime, String duration) {
		super(name);
		this.startTime = startTime;

		try {
			long durationMs = Long.parseLong(duration);
			// more accurate end time when test took less than a second to run
			this.endTime = startTime + durationMs;
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
	
	public int getTotalconfigCount() {
		return configPass+configSkip+configFail;
	}

	public List<ClassResult> getClassList() {
		return classList;
	}

	public String getName() {
		return name;
	}

	public long getStartTime() {
		return startTime;
	}

	public long getEndTime() {
		return endTime;
	}

	public float getDuration() {
		return duration;
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
		return j != null ? j.getRootUrl() + run.getUrl() + this.getSuiteUrl() : "";
	}

	public void setParentSuite(SuiteResult suite) {
		this.parentSuite = suite;
	}

	public SuiteResult getParentSuite() {
		return this.parentSuite;
	}

	public String getSuiteUrl() {
		return this.getParentSuite().getSuiteUrl() + this.getSafeName() + "/";
	}

	@Override
	public void setRun(Run<?, ?> run) {
		super.setRun(run);
		for (ClassResult _c : this.classList) {
			_c.setRun(run);
		}
	}

	@Override
	public Object getDynamic(String token, StaplerRequest req, StaplerResponse rsp) {
		for (ClassResult clazz : this.getChildren()) {
			for(MethodResult method: clazz.getChildren())
			if (token.equals(method.getName())) {
				return method;
			}
		}
		return null;
	}

	/**
	 * Adds only the classes that already aren't part of the list
	 *
	 * @param classList list of class results
	 */
	public void addClassList(List<ClassResult> classList) {
		Set<ClassResult> tmpSet = new HashSet<ClassResult>(this.classList);
		tmpSet.addAll(classList);
		this.classList = new ArrayList<ClassResult>(tmpSet);
	}

	@Override
	public List<ClassResult> getChildren() {
		return classList;
	}

	@Override
	public boolean hasChildren() {
		return classList != null && !classList.isEmpty();
	}

	public void tally() {
		this.fail = 0;
		this.skip = 0;
		this.pass = 0;
		this.configFail = 0;
		this.configSkip = 0;
		this.configPass = 0;

		for (ClassResult classResult : classList) {
			this.fail += classResult.getFailCount();
			this.skip += classResult.getSkipCount();
			this.pass += classResult.getPassCount();
			this.configFail += classResult.getFailedConfigs().size();
			this.configSkip += classResult.getSkippedConfigs().size();
			this.configPass += classResult.getConfigPassCount();
			classResult.setParentTest(this);
		}
	}

	@Override
	public BaseResult getSuiteParent() {
		return this.parentSuite;
	}

	@Override
	public List<? extends BaseResult> getSuiteChildren() {
		return this.classList;
	}

}
