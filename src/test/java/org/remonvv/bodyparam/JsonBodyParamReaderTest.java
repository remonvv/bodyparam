package org.remonvv.bodyparam;

import static org.junit.jupiter.api.Assertions.assertArrayEquals;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.remonvv.bodyparam.JacksonBodyParamReaderTest.mapToJsonString;

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

public class JsonBodyParamReaderTest {

	JsonBodyParamReader sut;

	@BeforeEach
	void beforeEach() {
		sut = new JsonBodyParamReader();
	}

	@Test
	void getFormatName_returns_correct_value() {
		assertEquals("JSON", sut.getFormatName());
	}

	@Test
	void getSupportedMediaTypes_returns_all_supported_json_media_types() {
		// Fail if media type support is added to ensure this test exhaustively checks
		// against all media types
		assertEquals(1, sut.getSupportedMediaTypes().size());
		assertTrue(sut.getSupportedMediaTypes().contains(MediaType.APPLICATION_JSON));
	}

	@Test
	void readParam_reads_array_value() {
		String paramName = "value";
		String[] value = new String[] { "string1", "string2", "string3" };

		String json = mapToJsonString(Map.of(paramName, value));

		Optional<Object> result = sut.readParam(paramName, value.getClass(), json, NameMatchingMode.EXACT);

		assertTrue(result.isPresent());
		assertEquals(String[].class, result.get().getClass());
		assertArrayEquals(value, (String[]) result.get());
	}

	@Test
	void readParam_reads_collection_values() {
		Set<Object> testValues = Set.of(
				new HashSet<>(Set.of("value")),
				new ArrayList<>(List.of("value")),
				new HashMap<>(Map.of("key", "value")));

		testValues.forEach(v -> {
			String paramName = "value";

			String json = mapToJsonString(Map.of(paramName, v));

			Optional<Object> result = sut.readParam(paramName, v.getClass(), json, NameMatchingMode.EXACT);

			assertTrue(result.isPresent());
			assertTrue(v.getClass().isAssignableFrom(result.get().getClass()));
			assertEquals(v, result.get());
		});
	}

	@Test
	void readParam_reads_nested_collection_values() {

		Set<Object> nestedValues = Set.of(
				new HashSet<>(Set.of("value")),
				new ArrayList<>(List.of("value")),
				new HashMap<>(Map.of("key", "value")));

		Set<Object> testValues = Set.of(
				new HashSet<>(Set.of(nestedValues)),
				new ArrayList<>(List.of(nestedValues)),
				new HashMap<>(Map.of("key", nestedValues)));

		testValues.forEach(v -> {
			String paramName = "value";

			String json = mapToJsonString(Map.of(paramName, v));

			Optional<Object> result = sut.readParam(paramName, v.getClass(), json, NameMatchingMode.EXACT);

			assertTrue(result.isPresent());
			assertTrue(v.getClass().isAssignableFrom(result.get().getClass()));
			assertEquals(v, result.get());
		});
	}

	@Test
	void readParam_reads_simple_values() {
		Set<Object> testValues = Set.of(
				Boolean.valueOf(true),
				Integer.valueOf(1),
				Long.valueOf(2),
				Float.valueOf(3f),
				Double.valueOf(4.0),
				String.valueOf("string"),
				NameMatchingMode.valueOf("EXACT"));

		testValues.forEach(v -> {
			String paramName = "value";

			String json = mapToJsonString(Map.of(paramName, v));

			Optional<Object> result = sut.readParam(paramName, v.getClass(), json, NameMatchingMode.EXACT);

			assertTrue(result.isPresent());
			assertEquals(v.getClass(), result.get().getClass());
			assertEquals(v, result.get());
		});
	}
}
