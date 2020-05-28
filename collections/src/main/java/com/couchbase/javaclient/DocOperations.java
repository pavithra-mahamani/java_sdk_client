package com.couchbase.javaclient;

import java.time.Duration;

import java.util.concurrent.ForkJoinPool;
import java.util.concurrent.ForkJoinTask;
import java.util.concurrent.TimeUnit;

import com.couchbase.client.java.Bucket;
import com.couchbase.client.java.Cluster;
import com.couchbase.client.java.Collection;
import com.couchbase.client.java.ReactiveCollection;
import com.couchbase.client.java.json.JsonObject;
import com.couchbase.javaclient.doc.DocSpec;
import com.couchbase.javaclient.doc.DocSpecBuilder;
import com.couchbase.javaclient.doc.Person;
import com.couchbase.javaclient.reactive.DocCreate;
import com.couchbase.javaclient.reactive.DocDelete;
import com.couchbase.javaclient.reactive.DocRetrieve;
import com.couchbase.javaclient.reactive.DocUpdate;

import net.sourceforge.argparse4j.ArgumentParsers;
import net.sourceforge.argparse4j.inf.ArgumentParser;
import net.sourceforge.argparse4j.inf.ArgumentParserException;
import net.sourceforge.argparse4j.inf.Namespace;
import reactor.core.publisher.Flux;
import reactor.core.scheduler.Schedulers;
import com.github.javafaker.Faker;

public class DocOperations {
	public static void main(String[] args) {
		ArgumentParser parser = ArgumentParsers.newFor("Couchbase Java SDK Client For Collections").build()
				.defaultHelp(true).description("Standalone SDK Client");
		// Connection params
		parser.addArgument("-i", "--cluster").required(true).help("Couchbase cluster address");
		parser.addArgument("-u", "--username").setDefault("Administrator").help("Username of Couchbase user");
		parser.addArgument("-p", "--password").setDefault("password").help("Password of Couchbase user");
		parser.addArgument("-b", "--bucket").setDefault("default").help("Name of existing Couchbase bucket");
		parser.addArgument("-s", "--scope").setDefault("_default").help("Name of existing scope");
		parser.addArgument("-c", "--collection").setDefault("default").help("Name of existing collection");

		// Operation params
		parser.addArgument("-n", "--num_ops").type(Integer.class).setDefault(1000)
				.help("Number of operations");
		parser.addArgument("-pc", "--percent_create").type(Integer.class).setDefault(100)
				.help("Percentage of creates out of num_ops");
		parser.addArgument("-pu", "--percent_update").type(Integer.class).setDefault(0)
				.help("Percentage of updates out of num_ops");
		parser.addArgument("-pd", "--percent_delete").type(Integer.class).setDefault(0)
				.help("Percentage of deletes out of num_ops");
		parser.addArgument("-pr", "--percent_read").type(Integer.class).setDefault(0)
				.help("Percentage of reads out of num_ops");
		parser.addArgument("-l", "--load_pattern").choices("uniform", "sparse", "dense").setDefault("uniform")
				.help("uniform: load all collections with percent_create docs, "
						+ "sparse: load all collections with maximum of percent_create docs"
						+ "dense: load all collections with minimum of percent_create docs");

		// Doc params
		parser.addArgument("-dsn", "--start_seq_num").type(Integer.class).setDefault(1)
				.help("Doc id start sequence number");
		parser.addArgument("-dpx", "--prefix").setDefault("doc_").help("Doc id prefix");
		parser.addArgument("-dsx", "--suffix").setDefault("").help("Doc id suffix");
		parser.addArgument("-dt", "--template").setDefault("Person").help("JSON document template");
		parser.addArgument("-de", "--expiry").setDefault(0).help("Document expiry in seconds");
		parser.addArgument("-ds", "--size").setDefault(500).help("Document size in bytes");

		// Output params
		parser.addArgument("-o", "--output").setDefault("info").help("Output detail level");

		try {
			Namespace ns = parser.parseArgs(args);
			// System.out.println(parser.parseArgs(args));
			run(ns);
		} catch (ArgumentParserException e) {
			parser.handleError(e);
		}
	}

	private static void run(Namespace ns) {
		String port = ns.getString("port")
		String clusterName = ns.getString("cluster");
		String username = ns.getString("username");
		String password = ns.getString("password");
		String bucketName = ns.getString("bucket");
		String scopeName = ns.getString("scope");
		String collectionName = ns.getString("collection");

		ConnectionFactory connection = new ConnectionFactory(clusterName, username, password, bucketName, scopeName,
				collectionName);
		Cluster cluster = connection.getCluster();
		Bucket bucket = connection.getBucket();
		Collection collection = null;
		collection = connection.getCollection();

		DocSpec dSpec = new DocSpecBuilder().numOps(ns.getInt("num_ops")).percentCreate(ns.getInt("percent_create"))
				.percentUpdate(ns.getInt("percent_update")).percentDelete(ns.getInt("percent_delete"))
				.loadPattern(ns.getString("load_pattern")).startSeqNum(ns.getInt("start_seq_num"))
				.prefix(ns.getString("prefix")).suffix(ns.getString("suffix")).template(ns.getString("template"))
				.expiry(ns.getInt("expiry")).size(ns.getInt("size")).buildDocSpec();

		ForkJoinPool pool = new ForkJoinPool();
		ForkJoinTask<String> create;
		ForkJoinTask<String> update;
		ForkJoinTask<String> delete;
		ForkJoinTask<String> retrieve;
		if (!collectionName.equals("default")) {
			create = ForkJoinTask.adapt(new DocCreate(dSpec, collection));
			update = ForkJoinTask.adapt(new DocUpdate(dSpec, collection));
			delete = ForkJoinTask.adapt(new DocDelete(dSpec, collection));
			retrieve = ForkJoinTask.adapt(new DocRetrieve(dSpec, collection));
		} else {
			create = ForkJoinTask.adapt(new DocCreate(dSpec, bucket));
			update = ForkJoinTask.adapt(new DocUpdate(dSpec, bucket));
			delete = ForkJoinTask.adapt(new DocDelete(dSpec, bucket));
			retrieve = ForkJoinTask.adapt(new DocRetrieve(dSpec, bucket));
		}
		try {
			pool.invoke(create);
			pool.invoke(update);
			pool.invoke(delete);
			pool.invoke(retrieve);
			pool.shutdownNow();
		} catch (Exception e) {
			e.printStackTrace();
		}

		connection.close();
	}

}
