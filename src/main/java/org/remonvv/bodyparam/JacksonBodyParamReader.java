package org.remonvv.bodyparam;

import java.lang.reflect.Type;
import java.util.Iterator;
import java.util.Optional;
import java.util.Set;

import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.web.server.ResponseStatusException;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JavaType;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ObjectReader;

public abstract class JacksonBodyParamReader extends BodyParamReader {
	private ObjectMapper localObjectMapper = null;

	@Override
	public Optional<Object> readParam(String paramName, Type paramType, String body,
			NameMatchingMode nameMatchingMode) {

		if (paramName == null)
			throw new IllegalArgumentException("Parameter name provided was null");

		if (paramName.isBlank())
			throw new IllegalArgumentException("Parameter name provided was blank");

		final ObjectMapper objectMapper = getObjectMapper();
		try {
			JsonNode jsonTree = getObjectMapper().readTree(body);

			final String[] paramNameParts = paramName.split("\\.");
			for (int i = 0; i < paramNameParts.length; i++) {
				String paramNamePart = paramNameParts[i];
				boolean isLastPart = i == paramNameParts.length - 1;
				Iterator<String> jsonFields = jsonTree.fieldNames();

				while (jsonFields.hasNext()) {
					String jsonFieldName = jsonFields.next();

					if (NameMatchingUtils.isNameMatching(jsonFieldName, paramNamePart, nameMatchingMode))
						if (isLastPart) {
							JsonNode jsonValue = jsonTree.get(jsonFieldName);

							JavaType paramTypeReference = objectMapper.getTypeFactory().constructType(paramType);
							ObjectReader objectReader = objectMapper.readerFor(paramTypeReference);

							try {
								return Optional.ofNullable(objectReader.readValue(jsonValue));
							}
							catch (Exception e) {
								throw new ResponseStatusException(HttpStatus.BAD_REQUEST,
										"Could not convert " + getFormatName() + " value \"" + jsonValue + " to type "
												+ paramType,
										e);
							}
						}
						else
							jsonTree = jsonTree.get(jsonFieldName);
				}
			}

			return Optional.empty();
		}
		catch (JsonProcessingException e) {
			throw new ResponseStatusException(HttpStatus.BAD_REQUEST,
					"Could not parse request body as it appears to be invalid " + getFormatName(), e);
		}
	}

	@Override
	public boolean supportsMediaType(MediaType mediaType) {

		return getSupportedMediaTypes().contains(mediaType);
	}

	abstract ObjectMapper createObjectMapper();

	abstract String getFormatName();

	abstract Set<MediaType> getSupportedMediaTypes();

	private ObjectMapper getObjectMapper() {
		if (localObjectMapper == null)
			synchronized (this) {
				if (localObjectMapper == null)
					localObjectMapper = createObjectMapper();

			}

		return localObjectMapper;
	}

}
