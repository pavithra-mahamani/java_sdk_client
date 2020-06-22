package com.couchbase.javaclient.doc;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.concurrent.TimeUnit;

import com.couchbase.client.java.json.JsonObject;
import com.github.javafaker.Faker;

public class Person {
	JsonObject jsonObject = JsonObject.create();
	Random random = new Random();

	public JsonObject createJsonObject(Faker faker, int docsize) {
		jsonObject.put("firstName", faker.name().firstName());
		jsonObject.put("lastName", faker.name().lastName());
		jsonObject.put("title", faker.name().title());
		jsonObject.put("suffix", faker.name().suffix());
		jsonObject.put("streetAddress", faker.address().streetAddress());
		jsonObject.put("city", faker.address().city());
		jsonObject.put("country", faker.address().country());
		jsonObject.put("age", random.nextInt(70));
		int count = 0;
		do {
			count = count + 1;
			jsonObject.put("filler" + count, faker.lorem().words(docsize / 10));
		} while (jsonObject.toString().length() < docsize);
		return jsonObject;
	}
}
