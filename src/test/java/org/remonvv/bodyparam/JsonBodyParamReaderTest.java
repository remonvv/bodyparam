package org.remonvv.bodyparam;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.fail;
import static org.remonvv.bodyparam.BodyParamReaderTest.mapToJsonString;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.http.MediaType;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

public class JsonBodyParamReaderTest {

	JsonBodyParamReader sut;

	@BeforeEach
	void beforeEach() {
		this.sut = new JsonBodyParamReader();
	}

	@Test
	void getSupportedMediaTypes_returns_all_supported_json_media_types() {
		assertTrue(this.sut.supportsMediaType(MediaType.APPLICATION_JSON));
	}

	@Test
	void readParam_reads_array_value() {
		String paramName = "value";
		String[] value = { "string1", "string2", "string3" };

		String json = mapToJsonString(Map.of(paramName, value));

		Optional<Object> result = this.sut.readParam(paramName, value.getClass(), json,
				NameMatchingMode.EXACT);

		assertTrue(result.isPresent());
		assertEquals(String[].class, result.get().getClass());
		assertJsonEquals(value, result.get());
	}

	@Test
	void readParam_reads_collection_values() {
		Set<Object> testValues = Set.of(new HashSet<>(Set.of("value")),
				new ArrayList<>(List.of("value")), new HashMap<>(Map.of("key", "value")));

		testValues.forEach(v -> {
			String paramName = "value";

			String json = mapToJsonString(Map.of(paramName, v));

			Optional<Object> result = this.sut.readParam(paramName, v.getClass(), json,
					NameMatchingMode.EXACT);

			assertTrue(result.isPresent());
			assertTrue(v.getClass().isAssignableFrom(result.get().getClass()));
			assertJsonEquals(v, result.get());
		});
	}

	@Test
	void readParam_reads_nested_collection_values() {

		Set<Object> nestedValues = Set.of(new HashSet<>(Set.of("value")),
				new ArrayList<>(List.of("value")), new HashMap<>(Map.of("key", "value")));

		Set<Object> testValues = Set.of(new HashSet<>(Set.of(nestedValues)),
				new ArrayList<>(List.of(nestedValues)),
				new HashMap<>(Map.of("key", nestedValues)));

		testValues.forEach(v -> {
			String paramName = "value";

			String json = mapToJsonString(Map.of(paramName, v));

			Optional<Object> result = this.sut.readParam(paramName, v.getClass(), json,
					NameMatchingMode.EXACT);

			assertTrue(result.isPresent());
			assertTrue(v.getClass().isAssignableFrom(result.get().getClass()));
			assertJsonEquals(v, result.get());
		});
	}

	@Test
	void readParam_reads_simple_values() {
		Set<Object> testValues = Set.of(Boolean.valueOf(true), Integer.valueOf(1),
				Long.valueOf(2), Float.valueOf(3f), Double.valueOf(4.0),
				String.valueOf("string"), NameMatchingMode.valueOf("EXACT"));

		testValues.forEach(v -> {
			String paramName = "value";

			String json = mapToJsonString(Map.of(paramName, v));

			Optional<Object> result = this.sut.readParam(paramName, v.getClass(), json,
					NameMatchingMode.EXACT);

			assertTrue(result.isPresent());
			assertEquals(v.getClass(), result.get().getClass());
			assertJsonEquals(v, result.get());
		});
	}

	private void assertJsonEquals(Object v1, Object v2) {
		ObjectMapper objectMapper = new ObjectMapper();

		try {
			assertEquals(objectMapper.writeValueAsString(v1),
					objectMapper.writeValueAsString(v2));
		} catch (JsonProcessingException e) {
			fail("could not convert either " + v1.toString() + " or " + v2.toString()
					+ " to JSON");
		}
	}
}
