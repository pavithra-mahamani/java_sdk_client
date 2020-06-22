package com.couchbase.javaclient.doc;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Random;
import java.util.Date;

import java.util.concurrent.TimeUnit;

import com.couchbase.client.java.json.JsonObject;
import com.github.javafaker.Faker;

public class Employee {
	JsonObject jsonObject = JsonObject.create();
	Random random = new Random();
//	Format formatter = new SimpleDateFormat("dd-MM-yyyy");

//	public JsonObject getJsonObject() {
//		return jsonObject;
//	}
//	1.prefix:query-testemployee52454.55205825139
//	2.template:{{ "name":"{0}", "join_yr":{1}, "join_mo":{2}, "join_day":{3}, "email":"{4}", "job_title":"{5}", "test_rate":{8}, "skills":{9},"VMs": {10}, "tasks_points" : {{"task1" : {6}, "task2" : {7}}}}}
//	3.name:['employee-16']
//	4.year:[2011]
//	5.month:[12]
//	6.day:[16]
//	7.email:['16-mail@couchbase.com']
//	8.info:['Support']
//	9.test_rate:[1, 2, 3, 4, 5, 6, 7, 8, 9]
//	10.[1, 2, 3, 4, 5, 6, 7, 8, 9]
//	11.[12.12]
//	12.skills:[['skill2010', 'skill2011']]
//	13.vms:[[{'RAM': 12, 'os': 'ubuntu', 'name': 'vm_12', 'memory': 12}, {'RAM': 12, 'os': 'windows', 'name': 'vm_13', 'memory': 12}]]
//	14.start:0
//	15.end:1


	public JsonObject createJsonObject(Faker faker, int docsize) {	
		List<String> jobTitles = Arrays.asList("Engineer", "Sales", "Support");
		Date joinDate = faker.date().past(365*10, TimeUnit.DAYS);
		jsonObject.put("name", "employee-" + joinDate.getDay());
		//jsonObject.put("join", formatter.format(joinDate));
		jsonObject.put("join_yr", joinDate.getYear());
		jsonObject.put("join_mo", joinDate.getMonth());
		jsonObject.put("join_day", joinDate.getDay());
		jsonObject.put("email", Integer.toString(joinDate.getDay()) + "-mail@couchbase.com");
		jsonObject.put("job_title", jobTitles.get(random.nextInt(jobTitles.size())));
		jsonObject.put("test_rate", random.nextInt(10));
		jsonObject.put("skills", this.getSkillsArray());
		jsonObject.put("VMs", this.getVMsArray());
		jsonObject.put("tasks_points", this.getTaskPoints());
		int count = 0;
		do {
			count = count + 1;
			jsonObject.put("filler" + count, faker.lorem().words(docsize / 10));
		} while (jsonObject.toString().length() < docsize);
		return jsonObject;
	}
	
	private String getSkillsArray() {
		return "SkillsArray";
	}
	
	private String getVMsArray() {
		return "SkillsArray";
	}
	
	private String getTaskPoints() {
		return "SkillsArray";
	}

}
