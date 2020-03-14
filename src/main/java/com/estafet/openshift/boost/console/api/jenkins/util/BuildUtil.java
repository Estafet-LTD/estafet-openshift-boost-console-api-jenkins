package com.estafet.openshift.boost.console.api.jenkins.util;

import java.util.Date;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.estafet.openshift.boost.commons.lib.date.DateUtils;
import com.openshift.restclient.model.IBuild;

public class BuildUtil {

	private static Pattern pattern = Pattern.compile("(.+)(\\-\\d+)");
	private static Pattern datePattern = Pattern.compile("\\d{4}\\-\\d{2}-\\d{2}T\\d{2}\\:\\d{2}\\:\\d{2}Z");

	private BuildUtil() {
	}

	public static boolean isValidDate(IBuild build) {
		return build.getCreationTimeStamp() != null && datePattern.matcher(build.getCreationTimeStamp()).matches();
	}

	public static Date buildDate(IBuild build) {
		if (isValidDate(build)) {
			return DateUtils.getDate(build.getCreationTimeStamp());	
		} else {
			return new Date();
		}
	}

	public static String buildName(IBuild build) {
		Matcher matcher = pattern.matcher(build.getName());
		matcher.find();
		return matcher.group(1);
	}

}
