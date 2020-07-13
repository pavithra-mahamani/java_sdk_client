package com.couchbase.javaclient.doc;


public final class DocTemplateFactory {
	public static DocTemplate getDocTemplate(final String dataset) {
		if ("Emp".equals(dataset)) {
			return new Emp();
		}else if("Employee".equals(dataset)){
			return new Employee();
		}else  if("Person".equals(dataset)){
			return new Person();
		}else{
			throw new RuntimeException("Unknown dataset - " + dataset);
		}
	}
}

