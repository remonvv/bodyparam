package org.remonvv.bodyparam;

import java.lang.reflect.Type;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import org.springframework.http.MediaType;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.JavaType;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.json.JsonMapper;

public class JsonBodyParamReader extends BodyParamReader {
	private final static Set<MediaType> SUPPORTED_MEDIA_TYPES = Set
			.of(MediaType.APPLICATION_JSON);

	private ObjectMapper objectMapper = JsonMapper.builder().build();

	@Override
	Object convertValueToTargetType(Type paramType, Object value) {
		JavaType javaType = this.objectMapper.constructType(paramType);

		return this.objectMapper.convertValue(value, javaType);
	}

	@Override
	Map<String, Object> mapRequestBody(String requestBody) {
		TypeReference<HashMap<String, Object>> typeRef = new TypeReference<HashMap<String, Object>>() {};
		try {
			return this.objectMapper.readValue(requestBody, typeRef);
		} catch (JsonProcessingException e) {
			throw new IllegalArgumentException("Request body \"" + requestBody
					+ "\" is not valid JSON and cannot be decoded");
		}
	}

	@Override
	boolean supportsMediaType(MediaType mediaType) {
		return SUPPORTED_MEDIA_TYPES.contains(mediaType);
	}
}
