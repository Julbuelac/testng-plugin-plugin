package hudson.plugins.testng.util;

import hudson.plugins.testng.results.MethodResult;
import hudson.plugins.testng.results.SuiteResult;
import hudson.plugins.testng.results.TestNGTestResult;

/**
 * Utility class to generate the view in jelly files
 *
 * @author julbuelac
 *
 */
public class DisplayUtil {
	
	public static String setColorClass(SuiteResult suite) {
		if (suite.getFailCount() > 0) return "fail";
		else if (suite.getSkipCount() > 0) return "skipped";
		else if (suite.getPassCount() > 0) return "pass";
		return "";
	}
	
	public static String setColorClass(TestNGTestResult test) {
		if (test.getFailCount() > 0) return "fail";
		else if (test.getSkipCount() > 0) return "skipped";
		else if (test.getPassCount() > 0) return "pass";
		return "";
	}
	
	public static String setColorClassConfig(SuiteResult suite) {
		if (suite.getConfigFailCount() > 0) return "fail";
		else if (suite.getConfigSkipCount() > 0) return "skipped";
		else if (suite.getConfigPassCount() > 0) return "pass";
		return "";
	}
	
	public static String setColorClassConfig(TestNGTestResult test) {
		if (test.getConfigFailCount() > 0) return "fail";
		else if (test.getConfigSkipCount() > 0) return "skipped";
		else if (test.getConfigPassCount() > 0) return "pass";
		return "";
	}
	
	public static String methodFailDiff(MethodResult previousMethod){
		if (previousMethod == null || previousMethod.getStatus().equals("FAIL")) return "(0)";
		else return "(+1)";	
	}
	
	public static String methodSkipDiff(MethodResult previousMethod){
		if (previousMethod == null || previousMethod.getStatus().equals("SKIP")) return "(0)";
		else return "(+1)";	
	}

}
