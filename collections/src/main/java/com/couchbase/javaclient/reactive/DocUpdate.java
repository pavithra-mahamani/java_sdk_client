package com.couchbase.javaclient.reactive;

import static com.couchbase.client.java.kv.MutateInSpec.upsert;

import java.time.Duration;
import java.util.*;
import java.util.concurrent.Callable;

import com.couchbase.client.java.Bucket;
import com.couchbase.client.java.Collection;
import com.couchbase.client.java.ReactiveCollection;
import com.couchbase.client.java.kv.MutateInSpec;
import com.couchbase.client.java.manager.collection.CollectionSpec;
import com.couchbase.client.java.manager.collection.ScopeSpec;
import com.couchbase.javaclient.doc.DocSpec;

import com.couchbase.javaclient.doc.DocTemplate;
import com.couchbase.javaclient.doc.DocTemplateFactory;
import reactor.core.publisher.Flux;
import reactor.core.scheduler.Schedulers;

public class DocUpdate implements Callable<String> {
	private DocSpec ds;
	private Bucket bucket;
	private Collection collection;
	private static int num_docs = 0;
	private boolean done = false;
	private List<String> fieldsToUpdate;

	public DocUpdate(DocSpec _ds, Bucket _bucket, List<String> fieldsToUpdate) {
		ds = _ds;
		bucket = _bucket;
		this.fieldsToUpdate = fieldsToUpdate;
	}

	public DocUpdate(DocSpec _ds, Collection _collection, List<String> fieldsToUpdate) {
		ds = _ds;
		collection = _collection;
		this.fieldsToUpdate = fieldsToUpdate;
	}

	@Override
	public String call() throws Exception {
		if (collection != null) {
			updateCollection(ds, collection, fieldsToUpdate);
		} else {
			updateBucketCollections(ds, bucket, fieldsToUpdate);
		}
		done = true;
		return num_docs + " DOCS UPDATED!";
	}

	public void updateBucketCollections(DocSpec ds, Bucket bucket, List<String> fieldsToUpdate) {
		List<Collection> bucketCollections = new ArrayList<>();
		List<ScopeSpec> bucketScopes = bucket.collections().getAllScopes();
		for (ScopeSpec scope : bucketScopes) {
			for (CollectionSpec scopeCollection : scope.collections()) {
				Collection collection = bucket.scope(scope.name()).collection(scopeCollection.name());
				if (collection != null) {
					bucketCollections.add(collection);
				}
			}
		}
		bucketCollections.parallelStream().forEach(c -> update(ds, c, fieldsToUpdate));
	}

	public void updateCollection(DocSpec ds, Collection collection, List<String> fieldsToUpdate) {
		update(ds, collection, fieldsToUpdate);
	}

	public void update(DocSpec ds, Collection collection, List<String> fieldsToUpdate) {
		ReactiveCollection rcollection = collection.reactive();
		num_docs = (int) (ds.get_num_ops() * ((float) ds.get_percent_update() / 100));
		DocTemplate docTemplate = DocTemplateFactory.getDocTemplate(ds.get_template());
		Flux<String> docsToUpdate = Flux.range(ds.get_startSeqNum(), num_docs)
				.map(id -> ds.get_prefix() + id + ds.get_suffix());
		System.out.println("Started update..");
		try {
			Map<String, List<MutateInSpec>> upsertMap = new HashMap<>();
			for (String id: docsToUpdate.toIterable()) {
				List<MutateInSpec> upsertList = new ArrayList<>();
				for (String field : fieldsToUpdate) {
					upsertList.add(upsert(field, docTemplate.updateJsonObject(field)));
				}
				upsertMap.put(id, upsertList);
			}
			docsToUpdate.publishOn(Schedulers.elastic())
					// .delayElements(Duration.ofMillis(5))
					.flatMap(key -> rcollection.mutateIn(key,
							//docTemplate.updateJsonObject(key, )
							upsertMap.get(key)
					))
					// Num retries, first backoff, max backoff
					.retryBackoff(10, Duration.ofMillis(100), Duration.ofMillis(1000))
					// Block until last value, complete or timeout expiry
					.blockLast(Duration.ofMinutes(10));
		} catch (Exception err) {
			err.printStackTrace();
		}
		System.out.println("Completed update");
	}
}