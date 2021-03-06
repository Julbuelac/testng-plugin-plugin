package hudson.plugins.testng.results;

import java.io.Serializable;
import java.util.List;

import hudson.model.ModelObject;
import hudson.model.Run;
import hudson.plugins.testng.TestNGTestResultBuildAction;
import hudson.tasks.test.AbstractTestResultAction;
import hudson.tasks.test.TabulatedResult;
import hudson.tasks.test.TestResult;
import jenkins.model.Jenkins;
import org.kohsuke.stapler.StaplerRequest;
import org.kohsuke.stapler.StaplerResponse;
import org.kohsuke.stapler.export.Exported;
import org.kohsuke.stapler.export.ExportedBean;

/**
 * Base class that takes care of all the common functionality of the different kinds of
 * test results.
 */
@SuppressWarnings("serial")
@ExportedBean
public abstract class BaseResult extends TabulatedResult implements ModelObject, Serializable {

    //owner of this build
    protected Run<?, ?> run;
    //name of this result
    protected final String name;
    //parent result for this result
    protected BaseResult parent;

    public BaseResult(String name) {
        this.name = name;
    }

    @Exported(visibility = 999)
    @Override
    public String getName() {
        return name;
    }

    @Override
    public BaseResult getParent() {
        return parent;
    }
    
    public void setParent(BaseResult parent) {
        this.parent = parent;
}

    @Override
    public Run<?, ?> getRun() {
        return run;
    }

    public void setRun(Run<?, ?> run) {
        this.run = run;
    }

    @Override
    public String getTitle() {
        return getName();
    }

    public String getDisplayName() {
        return getName();
    }

    //TODO: @see https://wiki.jenkins-ci.org/display/JENKINS/Hyperlinks+in+HTML and fix
    public String getUpUrl() {
        Jenkins j = Jenkins.getInstance();
        return j != null ? 
        		j.getRootUrl() + 
        		run.getUrl() + 
        		getId() 
        		: "";
    }
    

    @Override
    public Object getDynamic(String token, StaplerRequest req, StaplerResponse rsp) {
        for (TestResult result : this.getChildren()) {
            if (token.equals(result.getName())) {
                return result;
            }
        }
        return null;
    }

    /**
     * Explicit override here to ensure that when we are building TestNG reports,
     * we are only working with TestNG results (and not results from other test reporters).
     *
     * Can get into a bad situation if the same job has configured JUnit and TestNG reports
     *
     * @return TestNG results action
     */
    @Override
    public AbstractTestResultAction getTestResultAction() {
        Run<?, ?> run = getRun();
        if (run != null) {
            return run.getAction(TestNGTestResultBuildAction.class);
        }
        return null;
    }

    /**
     * @see BaseResult#getTestResultAction()
     * @return TestNG results action
     */
    @Override
    public AbstractTestResultAction getParentAction() {
        return getTestResultAction();
    }

    @Override
    public TestResult findCorrespondingResult(String id) {
        if (getId().equals(id) || id == null) {
            return this;
        }

        int sepIdx = id.indexOf('/');
        if (sepIdx < 0) {
            if (getSafeName().equals(id)) {
                return this;
            }
        } else {
            String currId = id.substring(0, sepIdx);
            if (!getSafeName().equals(currId)) {
                return null;
            }

            String childId = id.substring(sepIdx + 1);
            sepIdx = childId.indexOf('/');

            for (TestResult result : this.getChildren()) {
                if (sepIdx < 0 && childId.equals(result.getSafeName())) {
                    return result;
                } else if (sepIdx > 0 && result.getSafeName().equals(childId.substring(0, sepIdx))) {
                    return result.findCorrespondingResult(childId);
                }
            }
        }
        return null;
    }
    
    public BaseResult findCorrespondingResultSuite(String id) {
        if (getSuiteId().equals(id) || id == null) {
            return this;
        }

        int sepIdx = id.indexOf('/');
        if (sepIdx < 0) {
            if (getSafeName().equals(id)) {
                return this;
            }
        } else {
            String currId = id.substring(0, sepIdx);
            if (!getSafeName().equals(currId)) {
                return null;
            }

            String childId = id.substring(sepIdx + 1);
            sepIdx = childId.indexOf('/');

            for (BaseResult result : this.getSuiteChildren()) {
                if (sepIdx < 0 && childId.equals(result.getSafeName())) {
                    return result;
                } else if (sepIdx > 0 && result.getSafeName().equals(childId.substring(0, sepIdx))) {
                    return result.findCorrespondingResultSuite(childId);
                }
            }
        }
        return null;
    }
    

    /**
     * Gets the age of a result
     *
     * @return the number of consecutive builds for which we have a result for
     *         this package
     */
    public long getAge() {
        BaseResult result = (BaseResult) getPreviousResult();
        if (result == null) {
            return 1;
        } else {
            return 1 + result.getAge();
        }
    }
    
    /**
     * Gets the counter part of this {@link TestResult} in the previous run.
     *
     * @return null if no such counter part exists.
     */
    public TestResult getPreviousResultSuite() {
        Run<?,?> b = getRun();
        if (b == null) {
            return null;
        }
        while(true) {
            b = b.getPreviousBuild();
            if(b==null)
                return null;
            TestNGTestResultBuildAction r = (TestNGTestResultBuildAction)b.getAction(getParentAction().getClass());
            if(r!=null) {
                TestResult result = r.findCorrespondingResultSuite(this.getSuiteId());
                if (result!=null)
                    return result;
            }
        }
    }
    
    public String getSuiteId() {
            StringBuilder buf = new StringBuilder();
            buf.append(getSafeName());

            BaseResult parent = getSuiteParent();
            if (parent != null) {
                String parentId = parent.getSuiteId();
                if ((parentId != null) && (parentId.length() > 0)) {
                    buf.insert(0, '/');
                    buf.insert(0, parentId);
                }
            }
            return buf.toString();
        }
    
    public abstract BaseResult getSuiteParent();
    public abstract List<? extends BaseResult> getSuiteChildren();
    
}
