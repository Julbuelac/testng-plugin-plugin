package hudson.plugins.testng.util;

import hudson.plugins.testng.results.BaseResult;

/**
 * Utility class to generate the view in jelly files
 *
 * @author julbuelac
 *
 */
public class DisplayUtil {
	
	public String setColorClass(BaseResult result) {
		if (result.getFailCount() > 0) return "fail";
		else if (result.getSkipCount() > 0) return "skipped";
		else if (result.getPassCount() > 0) return "pass";
		return "";
	}

}
