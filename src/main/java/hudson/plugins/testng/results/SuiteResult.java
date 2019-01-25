package hudson.plugins.testng.results;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import hudson.model.Run;

/**
 * Represents a single TestNG suite {@code <suite>} tag.
 *
 * @author julbuelac
 */
@SuppressWarnings("serial")
public class SuiteResult extends BaseResult{
	
	//List of tests
	private List<TestNGTestResult> testList = new ArrayList<TestNGTestResult>();
	
	private float duration;
	private long startedAt;
	private long endedAt;

	public SuiteResult(String name, String duration, long startedAt) {
		super(name);
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
		
		public String getSafeName() {
			return name.replace(" ", "-");
		}
		
		 @Override
		    public void setRun(Run<?, ?> run) {
		        super.setRun(run);
		        for (TestNGTestResult _t : this.testList) {
		            _t.setRun(run);
		        }
		    }

		@Override
		public List<TestNGTestResult> getChildren() {
			return testList;
		}

		@Override
		public boolean hasChildren() {
			 return testList != null && !testList.isEmpty();
		}
}
