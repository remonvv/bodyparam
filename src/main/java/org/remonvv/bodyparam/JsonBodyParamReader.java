package org.remonvv.bodyparam;

import java.util.Set;

import org.springframework.http.MediaType;

import com.fasterxml.jackson.databind.ObjectMapper;

public class JsonBodyParamReader extends JacksonBodyParamReader {
	private final static Set<MediaType> SUPPORTED_MEDIA_TYPES = Set.of(MediaType.APPLICATION_JSON);

	@Override
	ObjectMapper createObjectMapper() {
		return new ObjectMapper();
	}

	@Override
	String getFormatName() {
		return "JSON";
	}

	@Override
	Set<MediaType> getSupportedMediaTypes() {
		return SUPPORTED_MEDIA_TYPES;
	}
}
