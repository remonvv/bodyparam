package org.remonvv.bodyparam;

import java.lang.reflect.Type;
import java.util.Optional;

import org.springframework.http.MediaType;

public abstract class BodyParamReader {
	protected final NameMatcher nameMatcher = new NameMatcher();

	abstract Optional<Object> readParam(String paramName, Type paramType, String body,
			NameMatchingMode nameMatchingMode);

	abstract boolean supportsMediaType(MediaType mediaType);
}
