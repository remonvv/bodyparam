package org.remonvv.bodyparam;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.lang.reflect.Type;
import java.util.Map;
import java.util.Optional;

import org.junit.jupiter.api.Test;

public class BodyParamReaderTest {

	BodyParamReader sut = new BodyParamReader();

	@Test
	void readParam_errors_if_multi_part_path_is_used_without_matching_request_body_hierarchy() {
		String path = "simple";
		Object value = "value";
		Map<String, Object> requestBodyMap = Map.of(path, value);
		assertThrows(IllegalArgumentException.class,
				() -> this.sut.readBodyParam("simple.butnotnested", value.getClass(), "",
						NameMatchingMode.EXACT,
						prepareRequestBodyMapper(requestBodyMap)));
	}

	@Test
	void readParam_errors_if_paramPath_is_blank() {
		assertThrows(IllegalArgumentException.class,
				() -> this.sut.readBodyParam("", String.class, "", NameMatchingMode.EXACT,
						prepareRequestBodyMapper()));
	}

	@Test
	void readParam_errors_if_paramPath_is_invalid() {

		assertThrows(IllegalArgumentException.class,
				() -> this.sut.readBodyParam(".", String.class, "", NameMatchingMode.EXACT,
						prepareRequestBodyMapper()));

		assertThrows(IllegalArgumentException.class,
				() -> this.sut.readBodyParam("parent.", String.class, "", NameMatchingMode.EXACT,
						prepareRequestBodyMapper()));

		assertThrows(IllegalArgumentException.class,
				() -> this.sut.readBodyParam(".child", String.class, "", NameMatchingMode.EXACT,
						prepareRequestBodyMapper()));

		assertThrows(IllegalArgumentException.class,
				() -> this.sut.readBodyParam("parent..child", String.class, "", NameMatchingMode.EXACT,
						prepareRequestBodyMapper()));
	}

	@Test
	void readParam_errors_if_paramPath_is_null() {

		assertThrows(IllegalArgumentException.class,
				() -> this.sut.readBodyParam(null, String.class, "", NameMatchingMode.EXACT,
						prepareRequestBodyMapper()));
	}

	@Test
	void readParam_returns_correct_value_for_multi_part_path() {
		String parentPath = "parent";
		String childPath = "child";
		String path = parentPath + "." + childPath;
		Object value = "value";
		Map<String, Object> requestBodyMap = Map.of(parentPath, Map.of(childPath, value));
		Optional<Object> returned = this.sut.readBodyParam(path, value.getClass(), "", NameMatchingMode.EXACT,
				prepareRequestBodyMapper(requestBodyMap));

		assertTrue(returned.isPresent());
		assertEquals(value, returned.get());
	}

	@Test
	void readParam_returns_correct_value_for_single_part_path() {
		String path = "single";
		Object value = "value";
		Map<String, Object> requestBodyMap = Map.of(path, value);
		Optional<Object> returned = this.sut.readBodyParam(path, value.getClass(), "", NameMatchingMode.EXACT,
				prepareRequestBodyMapper(requestBodyMap));

		assertTrue(returned.isPresent());
		assertEquals(value, returned.get());
	}

	@Test
	void readParam_returns_nothing_for_wrong_multi_part_path() {
		String parentPath = "parent";
		String childPath = "child";
		String path = parentPath + ".wrong";
		Object value = "value";
		Map<String, Object> requestBodyMap = Map.of(parentPath, Map.of(childPath, value));
		Optional<Object> returned = this.sut.readBodyParam(path, value.getClass(), "", NameMatchingMode.EXACT,
				prepareRequestBodyMapper(requestBodyMap));

		assertFalse(returned.isPresent());
	}

	@Test
	void readParam_returns_nothing_for_wrong_single_part_path() {
		String path = "single";
		Object value = "value";
		Map<String, Object> requestBodyMap = Map.of(path, value);
		Optional<Object> returned = this.sut.readBodyParam("wrong", value.getClass(), "", NameMatchingMode.EXACT,
				prepareRequestBodyMapper(requestBodyMap));

		assertFalse(returned.isPresent());
	}

	private RequestBodyMapper prepareRequestBodyMapper() {
		return prepareRequestBodyMapper(Map.of());
	}

	private RequestBodyMapper prepareRequestBodyMapper(Map<String, Object> requestBodyMap) {
		return new RequestBodyMapper() {

			@Override
			public Object convertValue(Type paramType, Object value) {
				return value;
			}

			@Override
			public Map<String, Object> mapRequestBody(String requestBodyString) {
				// TODO Auto-generated method stub
				return requestBodyMap;
			}
		};
	}

}
