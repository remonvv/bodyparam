package org.remonvv.bodyparam;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.fail;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.Set;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.http.MediaType;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.dataformat.xml.XmlMapper;

public class XmlBodyParamReaderTest {

	XmlBodyParamReader sut;

	@BeforeEach
	void beforeEach() {
		this.sut = new XmlBodyParamReader();
	}

	@Test
	void getSupportedMediaTypes_returns_all_supported_json_media_types() {
		assertTrue(this.sut.supportsMediaType(MediaType.APPLICATION_XML));
	}

	@Test
	void readParam_reads_collection_values() {
		Set<Object> testValues = Set.of(new HashMap<>(Map.of("key", "value")));

		testValues.forEach(v -> {
			String paramName = "value";

			String xml = mapToXmlString(Map.of(paramName, v));

			Optional<Object> result = this.sut.readParam(paramName, v.getClass(), xml,
					NameMatchingMode.EXACT);

			assertTrue(result.isPresent());
			assertTrue(v.getClass().isAssignableFrom(result.get().getClass()));
			assertXmlEquals(v, result.get());
		});
	}

	@Test
	void readParam_reads_nested_collection_values() {

		Set<Object> nestedValues = Set.of(new HashMap<>(Map.of("key", "value")));

		Set<Object> testValues = Set.of(new HashMap<>(Map.of("key", nestedValues)));

		testValues.forEach(v -> {
			String paramName = "value";

			String xml = mapToXmlString(Map.of(paramName, v));

			Optional<Object> result = this.sut.readParam(paramName, v.getClass(), xml,
					NameMatchingMode.EXACT);

			assertTrue(result.isPresent());
			assertTrue(v.getClass().isAssignableFrom(result.get().getClass()));
			assertXmlEquals(v, result.get());
		});
	}

	@Test
	void readParam_reads_simple_values() {
		Set<Object> testValues = Set.of(Boolean.valueOf(true), Integer.valueOf(1),
				Long.valueOf(2), Float.valueOf(3f), Double.valueOf(4.0),
				String.valueOf("string"), NameMatchingMode.valueOf("EXACT"));

		testValues.forEach(v -> {
			String paramName = "value";

			String xml = mapToXmlString(Map.of(paramName, v));

			Optional<Object> result = this.sut.readParam(paramName, v.getClass(), xml,
					NameMatchingMode.EXACT);

			assertTrue(result.isPresent());
			assertEquals(v.getClass(), result.get().getClass());
			assertXmlEquals(v, result.get());
		});
	}

	private void assertXmlEquals(Object v1, Object v2) {
		ObjectMapper objectMapper = new XmlMapper();

		try {
			assertEquals(objectMapper.writeValueAsString(v1),
					objectMapper.writeValueAsString(v2));
		} catch (JsonProcessingException e) {
			fail("could not convert either " + v1.toString() + " or " + v2.toString()
					+ " to JSON");
		}
	}

	private String mapToXmlString(Map<String, Object> map) {
		try {
			return new XmlMapper().writeValueAsString(map);
		} catch (JsonProcessingException e) {
			fail("Could not construct JSON string from map " + map.toString());
			return null;
		}
	}
}
