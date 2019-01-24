package hudson.plugins.testng.results;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
/**
 * Represents a single TestNG suite {@code <suite>} tag.
 *
 * @author julbuelac
 */
public class SuiteResult{
	
	//List of tests
	private List<TestNGTestResult> testList = new ArrayList<TestNGTestResult>();
	
	private String name;
	
	private float duration;
	
	private long startedAt;
	
	private long endedAt;

	public SuiteResult(String name, String duration, long startedAt) {
		this.name = name;
		this.startedAt = startedAt;
		try {
            long durationMs = Long.parseLong(duration);
            //more accurate end time when test took less than a second to run
            this.endedAt = startedAt + durationMs;
            this.duration = (float) durationMs / 1000f;
        } catch (NumberFormatException e) {
            System.err.println("Unable to parse duration value: " + duration);
        }
	}

	 public List<TestNGTestResult> getTestList() {
	        return testList;
	    }

	    public String getName() {
	        return name;
	    }

	    /**
	     * Adds only the tests that already aren't part of the list
	     *
	     * @param testList list of test results
	     */
	    public void addTestList(List<TestNGTestResult> classList) {
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
}
