/**
 * Copyright (c) 2021 the original author or authors.
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 */
package org.remonvv.bodyparam;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

import org.springframework.util.Assert;

public class BodyParamReader {

	@SuppressWarnings("unchecked")
	public Optional<Object> readBodyParam(String paramPath, Type paramType, String requestBody,
			NameMatchingMode nameMatchingMode, RequestBodyMapper requestBodyMapper) {

		List<String> paramPathParts = validateAndCompileParamPath(paramPath);
		List<String> currentPath = new ArrayList<>(paramPathParts.size());

		Map<String, Object> bodyMap = requestBodyMapper.mapRequestBody(requestBody);

		for (int i = 0; i < paramPathParts.size(); i++) {
			String paramNamePart = paramPathParts.get(i);
			boolean isLastPart = i == paramPathParts.size() - 1;
			currentPath.add(paramNamePart);

			for (String key : bodyMap.keySet())
				if (NameMatchingUtils.isNameMatching(key, paramNamePart, nameMatchingMode)) {
					if (isLastPart) {
						Object value = bodyMap.get(key);
						Object convertedValue = requestBodyMapper.convertValue(paramType, value);

						return Optional.ofNullable(convertedValue);
					}

					Object childValue = bodyMap.get(key);

					if (!Map.class.isAssignableFrom(childValue.getClass()))
						throw new IllegalArgumentException(
								"Value found at \"" + String.join(".", currentPath) + "\" is not an object");

					bodyMap = (Map<String, Object>) bodyMap.get(key);
				}
		}

		return Optional.empty();
	}

	private List<String> validateAndCompileParamPath(String paramPath) {

		Assert.notNull(paramPath, "Parameter name provided was null");

		if (paramPath.isBlank())
			throw new IllegalArgumentException(
					"Path parameter for @" + BodyParam.class.getSimpleName() + " is null or blank");

		if (paramPath.startsWith(".") || paramPath.endsWith("."))
			throw new IllegalArgumentException(
					"Path parameter for @" + BodyParam.class.getSimpleName() + " cannot begin or end with a period");

		final String[] paramPathParts = paramPath.split("\\.");

		for (String paramNamePart : paramPathParts)
			if (paramNamePart.isBlank())
				throw new IllegalArgumentException(
						"Path " + paramPath + " is not a valid path value for @" + BodyParam.class.getSimpleName());

		return Arrays.stream(paramPathParts)
				.collect(Collectors.toList());
	}
}
