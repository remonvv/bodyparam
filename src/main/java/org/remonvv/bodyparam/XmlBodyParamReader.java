package org.remonvv.bodyparam;

import java.util.Set;

import org.springframework.http.MediaType;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.dataformat.xml.XmlMapper;

public class XmlBodyParamReader extends JacksonBodyParamReader {
	private final static Set<MediaType> SUPPORTED_MEDIA_TYPES = Set.of(MediaType.APPLICATION_XML);

	@Override
	ObjectMapper createObjectMapper() {
		return new XmlMapper();
	}

	@Override
	String getFormatName() {
		return "XML";
	}

	@Override
	Set<MediaType> getSupportedMediaTypes() {
		return SUPPORTED_MEDIA_TYPES;
	}
}
