package com.couchbase.javaclient.doc;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Random;
import java.util.Date;
import java.lang.Integer;

import java.util.concurrent.TimeUnit;

import com.couchbase.client.java.json.JsonObject;
import com.couchbase.javaclient.doc.*;
import com.github.javafaker.Faker;
import java.lang.reflect.*;

public class DocTemplate {
	JsonObject templateData = JsonObject.create();
	Faker faker;
	int docsize;
	Class<?> templateClass;
	Method templateMethod;

	public DocTemplate(String template, Faker _faker, int _docsize) {
		try {
			faker = _faker;
			docsize = _docsize;
			templateClass = Class.forName("com.couchbase.javaclient.doc." + template);
			Class<?>[] parameterTypes = new Class<?>[2];
			parameterTypes[0] = faker.getClass();
			parameterTypes[1] = int.class;
			templateMethod = templateClass.getDeclaredMethod("createJsonObject", parameterTypes);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public JsonObject createJsonObject() {
		try {
			templateData = (JsonObject) templateMethod.invoke(templateClass.newInstance(), faker, docsize);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return templateData;
	}
}
