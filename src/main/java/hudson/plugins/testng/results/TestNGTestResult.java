package hudson.plugins.testng.results;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;


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

    public long getStartTime() {
		return startTime;
	}

	public long getEndTime() {
		return endTime;
	}

	public float getDuration() {
		return duration;
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

}
