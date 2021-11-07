package org.remonvv.bodyparam;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.fail;

import java.util.Map;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

public class BodyParamReaderTest {

	BodyParamReader sut;

	@BeforeEach
	void beforeEach() {
		this.sut = new JsonBodyParamReader();
	}

	@Test
	void readParam_throws_exception_if_paramName_is_blank() {
		String json = mapToJsonString(Map.of("test", "string"));

		assertThrows(IllegalArgumentException.class,
				() -> this.sut.readParam("", String.class, json, NameMatchingMode.EXACT));
	}

	@Test
	void readParam_throws_exception_if_paramName_is_null() {
		String json = mapToJsonString(Map.of("test", "string"));

		assertThrows(IllegalArgumentException.class, () -> this.sut.readParam(null,
				String.class, json, NameMatchingMode.EXACT));
	}

	static String mapToJsonString(Map<String, Object> map) {
		try {
			return new ObjectMapper().writeValueAsString(map);
		} catch (JsonProcessingException e) {
			fail("Could not construct JSON string from map " + map.toString());
			return null;
		}
	}
}
