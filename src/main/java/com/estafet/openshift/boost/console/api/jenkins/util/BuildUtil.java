package com.estafet.openshift.boost.console.api.jenkins.util;

import java.util.Date;
import java.util.regex.Pattern;

import com.estafet.openshift.boost.commons.lib.date.DateUtils;
import com.openshift.restclient.model.IBuild;

public class BuildUtil {

	private static Pattern pattern = Pattern.compile("(.+)(\\-\\d+)");

	private BuildUtil( ) { }

	public static Date buildDate(IBuild build) {
		return DateUtils.getDate(build.getCreationTimeStamp());
	}

	public static String buildName(IBuild build) {
		return pattern.matcher(build.getName()).group(1);
	}
	
	
	
}
