package com.github.hi_fi.httprequestlibrary.utils;

import java.util.List;
import java.util.Map;

import org.python.util.PythonInterpreter;

import com.google.gson.Gson;

public class Robot {

	static RobotLogger logger = new RobotLogger("Robot");

	@SuppressWarnings("unchecked")
	public static <T> T getParamsValue(String[] params, int index, T defaultValue) {
		T value = defaultValue;
		String givenValue = null;
		if (params.length > index) {
			givenValue = params[index];
			logger.debug("Value from arguments: " + givenValue);
		}

		if (givenValue != null && !givenValue.equals("None") && givenValue.length() > 0) {
			if (defaultValue instanceof Map) {
				value = (T) parseRobotDictionary(givenValue);
			} else if (defaultValue instanceof String) {
				value = (T) givenValue;
			} else if (defaultValue instanceof List) {
				value = (T) parseRobotList(givenValue);
			}
		}
		return value;
	}

	@SuppressWarnings({ "unchecked", "resource" })
	public static Map<String, Object> parseRobotDictionary(String dictionary) {
		logger.debug("Dictionary going to be parsed to Map: " + dictionary);

		PythonInterpreter py = new PythonInterpreter();
		py.exec("import json");
		Map<String, Object> json = new Gson().fromJson(py.eval("json.dumps(" + dictionary + ")").toString(), Map.class);
		return json;
	}

	@SuppressWarnings("unchecked")
	public static List<String> parseRobotList(String list) {
		logger.debug("List going to be parsed: " + list);
		return new Gson().fromJson(list.replace("u'", "'"), List.class);
	}

	public static Boolean isDictionary(String testedString) {
		try {
			new Gson().fromJson(testedString, Map.class);
			return !testedString.contains("\"");
		} catch (Exception e) {
			logger.debug(String.format("%s is tested for being dictionary, and result is: %s", testedString, (testedString.contains("{") && testedString.contains("}"))));
			return (testedString.contains("{") && testedString.contains("}"));
		}
	}

}