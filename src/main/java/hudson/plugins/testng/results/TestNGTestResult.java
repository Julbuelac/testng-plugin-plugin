package hudson.plugins.testng.results;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.kohsuke.stapler.StaplerRequest;
import org.kohsuke.stapler.StaplerResponse;

import hudson.model.Run;
import hudson.tasks.test.TestResult;


/**
 * Represents a single TestNG XML {@code <test>} tag.
 *
 * @author nullin
 */
@SuppressWarnings("serial")
public class TestNGTestResult extends BaseResult {

    //list of test classes
    private List<ClassResult> classList = new ArrayList<ClassResult>();
    
    private  long startTime;
    private  long endTime;
    private  float duration;
	private int fail;
	private int skip;
	private int pass;


    public TestNGTestResult(String name, long startTime, String duration) {
        super(name);
        this.startTime = startTime;
        
        try {
            long durationMs = Long.parseLong(duration);
            //more accurate end time when test took less than a second to run
            this.endTime = startTime + durationMs;
            this.duration = (float) durationMs / 1000f;
        } catch (NumberFormatException e) {
            System.err.println("Unable to parse duration value: " + duration);
        }
    }

    public List<ClassResult> getClassList() {
        return classList;
    }

    public String getName() {
        return name;
    }
    
    public String getSafeName() {
    	return name.replace(" ", "-");
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
	
	 @Override
	    public void setRun(Run<?, ?> run) {
	        super.setRun(run);
	        for (ClassResult _c : this.classList) {
	            _c.setRun(run);
	        }
	    }

	 @Override
	    public Object getDynamic(String token, StaplerRequest req, StaplerResponse rsp) {
	        for (ClassResult result : this.getChildren()) {
	            if (token.equals(result.getName())) {
	                return result;
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
	     
	     for (ClassResult classResult : classList) {
	    	 this.fail += classResult.getFailCount();
		     this.skip += classResult.getSkipCount();
		     this.pass += classResult.getPassCount();
		     classResult.setParent(this);

	     }
	}

}
