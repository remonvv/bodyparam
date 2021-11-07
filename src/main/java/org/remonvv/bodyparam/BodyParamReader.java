package org.remonvv.bodyparam;

import java.lang.reflect.Type;
import java.util.HashSet;
import java.util.Map;
import java.util.Optional;
import java.util.Set;

import org.springframework.http.MediaType;
import org.springframework.util.Assert;

public abstract class BodyParamReader {

	@SuppressWarnings("unchecked")
	public Optional<Object> readParam(String paramName, Type paramType,
			String requestBody, NameMatchingMode nameMatchingMode) {

		Assert.notNull(paramName, "Parameter name provided was null");

		if (paramName.isBlank())
			throw new IllegalArgumentException("Parameter name provided was blank");

		Map<String, Object> bodyMap = mapRequestBody(requestBody);

		final String[] paramNameParts = paramName.split("\\.");

		Set<String> currentPath = new HashSet<>();

		for (int i = 0; i < paramNameParts.length; i++) {
			String paramNamePart = paramNameParts[i];
			boolean isLastPart = i == paramNameParts.length - 1;
			currentPath.add(paramNamePart);

			for (String key : bodyMap.keySet())
				if (NameMatchingUtils.isNameMatching(key, paramNamePart,
						nameMatchingMode)) {
					if (isLastPart) {
						Object value = bodyMap.get(key);
						Object convertedValue = convertValueToTargetType(paramType,
								value);

						return Optional.ofNullable(convertedValue);
					}

					Object childValue = bodyMap.get(key);

					if (!Map.class.isAssignableFrom(childValue.getClass()))
						throw new IllegalArgumentException("Value \"");

					bodyMap = (Map<String, Object>) bodyMap.get(key);
				}
		}

		return Optional.empty();
	}

	abstract Object convertValueToTargetType(Type paramType, Object value);

	abstract Map<String, Object> mapRequestBody(String body);

	abstract boolean supportsMediaType(MediaType mediaType);
}
