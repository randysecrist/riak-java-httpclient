package com.sfn.riak.client;

import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.function.ThrowingSupplier;

import static java.time.Duration.ofSeconds;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTimeout;

public class ClientOpsTest {

	private static Client client;
	private static Location location;

	@BeforeAll
	static void initAll() {
		client = new Client("localhost", 8098);
		Namespace ns = new Namespace("test_bucket");
		location = new Location(ns, "test_key");
	}

	@AfterAll
	static void tearDownAll() {
		client.delete(location);
	}

	@Test
	@DisplayName("Test Basic Operations")
	void canWriteData() throws Throwable {
		String key = client.put(location, "test_value");
		assertEquals(location.getKeyAsString(), key);
		String value = assertTimeout(ofSeconds(10), new ThrowingSupplier<String>() {
			@Override
			public String get() throws Throwable {
				return client.find(location);
			}
		});
		assertEquals("test_value", value);
	}
}
