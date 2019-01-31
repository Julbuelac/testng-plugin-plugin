package hudson.plugins.testng.results;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.kohsuke.stapler.StaplerRequest;
import org.kohsuke.stapler.StaplerResponse;
import org.kohsuke.stapler.export.Exported;

import hudson.model.Run;
import hudson.tasks.test.TestResult;

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
	private int fail;
	private int skip;
	private int pass;

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
		     
		     for (TestNGTestResult testResult : testList) {
		    	 this.fail += testResult.getFailCount();
			     this.skip += testResult.getSkipCount();
			     this.pass += testResult.getPassCount();
			     testResult.setParent(this);
		     }
		}
		

}
