package hudson.plugins.testng.results;

import edu.umd.cs.findbugs.annotations.SuppressFBWarnings;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import hudson.model.Run;
import hudson.plugins.testng.util.FormatUtil;
import jenkins.model.Jenkins;

import org.kohsuke.stapler.StaplerRequest;
import org.kohsuke.stapler.StaplerResponse;
import org.kohsuke.stapler.bind.JavaScriptMethod;
import org.kohsuke.stapler.export.Exported;

/**
 * Handles package level results
 */
@SuppressFBWarnings(value = "SE_NO_SERIALVERSIONID", justification = "XStream does not care")
@SuppressWarnings("serial")
public class PackageResult extends BaseResult {

	public static final String NO_PKG_NAME = "No Package";
	// map of all classes run from this package
	private Map<String,ClassResult> classMap = new HashMap<String,ClassResult>();

	// cached
	private List<MethodResult> sortedTestMethodsByStartTime = null;

	// cached vars updated using tally method
	private transient long startTime;
	private transient long duration;
	private transient int fail;
	private transient int skip;
	private transient int pass;
	private TestNGResult testNGResult;

	// Maximum size of methods in the method execution list
	public static final int MAX_EXEC_MTHD_LIST_SIZE = 25;

	public PackageResult(String name) {
		super(name);
	}

	@Override
	public void setRun(Run<?, ?> run) {
		super.setRun(run);
		for (ClassResult _class : classMap.values()) {
			_class.setRun(run);
		}
	}

	/**
	 * Can't change this to return seconds as expected by
	 * {@link hudson.tasks.test.TestObject} because it has already been exported
	 *
	 * @return duration in milliseconds
	 */
	@Exported
	@Override
	public float getDuration() {
		return duration / 1000f;
	}

	public long getStartTime() {
		return startTime;
	}

	public long getEndTime() {
		return startTime + duration;
	}

	@Override
	@Exported(visibility = 9, name = "fail")
	public int getFailCount() {
		return fail;
	}

	@Override
	@Exported(visibility = 9, name = "skip")
	public int getSkipCount() {
		return skip;
	}

	@Override
	@Exported(visibility = 9)
	public int getTotalCount() {
		return super.getTotalCount();
	}

	@Override
	public int getPassCount() {
		return pass;
	}

	public Map<String, ClassResult> getClassMap() {
		return classMap;
	}
	/**
	 * Gets all the method results related to this package sorted by the time the
	 * methods were executed
	 *
	 * @return List of this package's methods sorted by their start time
	 */
	public List<MethodResult> getSortedTestMethodsByStartTime() {
		if (sortedTestMethodsByStartTime == null) {
			sortTestMethods();
		}
		return sortedTestMethodsByStartTime;
	}

	/**
	 * Gets table row representation for all the method results associated with this
	 * package (sorted based on start time)
	 *
	 * @return table row representation
	 */
	@JavaScriptMethod
	public String getAllSortedTestMethodsByStartTime() {
		return getMethodExecutionTableContent(getSortedTestMethodsByStartTime());
	}

	/**
	 * Gets table row representation for the first {@link #MAX_EXEC_MTHD_LIST_SIZE}
	 * method results associated with this package (sorted based on start time)
	 *
	 * @return table row representation
	 */
	@JavaScriptMethod
	public String getFirstXSortedTestMethodsByStartTime() {
		// returning the first MAX results only
		List<MethodResult> list = getSortedTestMethodsByStartTime();
		list = list.subList(0, list.size() > MAX_EXEC_MTHD_LIST_SIZE ? MAX_EXEC_MTHD_LIST_SIZE : list.size());
		return getMethodExecutionTableContent(list);
	}

	/**
	 * Gets the table row representation for the specified method results
	 *
	 * @param mrList list of method result objects
	 * @return table row representation
	 */
	private String getMethodExecutionTableContent(List<MethodResult> mrList) {
		StringBuilder sb = new StringBuilder(mrList.size() * 100);

		for (MethodResult mr : mrList) {
			sb.append("<tr><td align=\"left\">");
			sb.append("<a href=\"").append(mr.getUpUrl()).append("\">");
			sb.append(mr.getParent().getName()).append(".").append(mr.getName());
			sb.append("</a>");
			sb.append("</td><td align=\"center\">");
			sb.append(FormatUtil.formatTime(mr.getDuration()));
			sb.append("</td><td align=\"center\">");
			sb.append(mr.getStartedAt());
			sb.append("</td><td align=\"center\"><span class=\"").append(mr.getCssClass()).append("\">");
			sb.append(mr.getStatus());
			sb.append("</span></td></tr>");
		}
		return sb.toString();
	}

	@Override
	public void tally() {
		fail = 0;
		skip = 0;
		pass = 0;
		List<long[]> timeSeries = new ArrayList<long[]>(classMap.size());
		for (ClassResult _c : classMap.values()) {
			_c.setParent(this);
			_c.setPkgParent(this);
			fail += _c.getFailCount();
			skip += _c.getSkipCount();
			pass += _c.getPassCount();
			timeSeries.add(new long[] { _c.getStartTime(), _c.getEndTime() - _c.getStartTime() });
		}

		Collections.sort(timeSeries, new Comparator<long[]>() {
			public int compare(long[] ts1, long[] ts2) {
				return ts1[0] < ts2[0] ? -1 : (ts1[0] > ts2[0] ? 1 : 0);
			}
		});

		timeSeries.add(new long[] { System.currentTimeMillis(), 0 }); // to help with following algorithm
		startTime = timeSeries.get(0)[0]; // start time for all classes within this package
		duration = 0;
		int activeTS = 0;
		int nextTS = 1;
		do {
			long[] ts1 = timeSeries.get(activeTS);
			long[] ts2 = timeSeries.get(nextTS);

			long s1 = ts1[0];
			long e1 = ts1[0] + ts1[1];

			long s2 = ts2[0];
			long e2 = ts2[0] + ts2[1];

			if (s1 <= s2 && e1 >= e2) {
				// ts2 series is completely contained in ts1, so nothing to do
				nextTS++;
				continue;
			}

			if (e1 <= s2) {
				// no overlap (disjoint time series)
				duration += ts1[1];
			} else {
				// overlap
				duration += s2 - s1;
			}
			activeTS = nextTS;
			nextTS++;
		} while (nextTS < timeSeries.size()); // we never process the last entry in the array
	}

	/**
	 * Sorts the test method results associated with this package based on the start
	 * time for method execution
	 */
	public void sortTestMethods() {
		this.sortedTestMethodsByStartTime = new ArrayList<MethodResult>();
		// for each class
		Map<Date, List<MethodResult>> map = new HashMap<Date, List<MethodResult>>();
		for (ClassResult aClass : classMap.values()) {
			if (aClass.getTestMethods() != null) {
				for (MethodResult aMethod : aClass.getTestMethods()) {
					Date startDate = aMethod.getStartedAt();
					if (!aMethod.getStatus().equalsIgnoreCase("skip") && startDate != null) {
						if (map.containsKey(startDate)) {
							map.get(startDate).add(aMethod);
						} else {
							List<MethodResult> list = new ArrayList<MethodResult>();
							list.add(aMethod);
							map.put(startDate, list);
						}
					}
				}
			}
		}
		List<Date> keys = new ArrayList<Date>(map.keySet());
		Collections.sort(keys);
		// now create the list with the order
		for (Date key : keys) {
			if (map.containsKey(key)) {
				this.sortedTestMethodsByStartTime.addAll(map.get(key));
			}
		}
	}

	public String getUpUrlPkg() {
		Jenkins j = Jenkins.getInstance();
		return j != null ? j.getRootUrl() + run.getUrl() + getPkgUrl() : "";
	}

	public String getPkgUrl() {
		return this.testNGResult.getSafeName() + "/package/" + this.getSafeName() + "/";
	}

	public TestNGResult getTestNGResult() {
		return testNGResult;
	}

	public void setTestNGResult(TestNGResult testNGResult) {
		this.testNGResult = testNGResult;
	}

	@Override
	@Exported(name = "classs") // because stapler notices suffix 's' and remove it
	public List<ClassResult> getChildren() {
		return new ArrayList<ClassResult>(classMap.values());
	}
	

	@Override
	public boolean hasChildren() {
		return classMap != null && !classMap.isEmpty();
	}

	/**
	 * {@inheritDoc}
	 *
	 * Overriding so that we can be backward compatible with shared links. We
	 * changed name for classes to be simple name instead of canonical.
	 *
	 * TODO: Added this in release 1.7. Delete this method in one of the next few
	 * release.
	 *
	 * @param token Name of the class to find
	 * @param req StaplerRequest
	 * @param rsp StaplerResponse
	 * @return Class from this package
	 */
	@Override
	public Object getDynamic(String token, StaplerRequest req, StaplerResponse rsp) {

		if (this.classMap != null) {
			for (ClassResult classResult : this.classMap.values()) {
				if (token.equals(classResult.getName())) {
					classResult.setPackageView(true);
					return classResult;
				}
			}
		}
		return null;
	}

	@Override
	public BaseResult getSuiteParent() {
		return null;
	}

	@Override
	public List<? extends BaseResult> getSuiteChildren() {
		return null;
	}

}
