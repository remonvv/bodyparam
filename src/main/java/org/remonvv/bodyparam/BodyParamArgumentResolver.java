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

import java.io.IOException;
import java.lang.reflect.Type;
import java.util.Arrays;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

import javax.servlet.http.HttpServletRequest;

import org.remonvv.bodyparam.mappers.JsonRequestBodyMapper;
import org.remonvv.bodyparam.mappers.XmlRequestBodyMapper;
import org.springframework.core.MethodParameter;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.web.HttpMediaTypeNotSupportedException;
import org.springframework.web.bind.annotation.ValueConstants;
import org.springframework.web.bind.support.WebDataBinderFactory;
import org.springframework.web.client.HttpServerErrorException;
import org.springframework.web.context.request.NativeWebRequest;
import org.springframework.web.context.request.ServletWebRequest;
import org.springframework.web.method.support.HandlerMethodArgumentResolver;
import org.springframework.web.method.support.ModelAndViewContainer;
import org.springframework.web.server.ResponseStatusException;

public class BodyParamArgumentResolver implements HandlerMethodArgumentResolver {

	private final Map<MediaType, RequestBodyMapper> registeredRequestBodyMappers = Map.of(
			MediaType.APPLICATION_JSON, new JsonRequestBodyMapper(),
			MediaType.APPLICATION_XML, new XmlRequestBodyMapper());

	private final BodyParamReader bodyParamReader;

	BodyParamArgumentResolver(BodyParamReader bodyParamReader) {
		this.bodyParamReader = bodyParamReader;
	}

	@Override
	public Object resolveArgument(MethodParameter parameter, ModelAndViewContainer mavContainer,
			NativeWebRequest webRequest, WebDataBinderFactory binderFactory) throws Exception {

		BodyParam bodyParamAnnotation = parameter.getParameterAnnotation(BodyParam.class);
		ServletWebRequest servletWebRequest = (ServletWebRequest) webRequest;
		NameMatchingMode nameMatchingMode = determineNameMatchingMode(parameter);

		MediaType mediaType = getMediaType(servletWebRequest);

		String requestBodyString = getBodyAsString(servletWebRequest);
		String paramPath = getParamPath(bodyParamAnnotation, parameter);
		Type paramType = parameter.getGenericParameterType();

		Optional<Object> defaultValueOptional = Optional.of(bodyParamAnnotation.defaultValue())
				.filter(s -> !s.equals(ValueConstants.DEFAULT_NONE))
				.map(s -> stringToDefaultValue(parameter.getParameterType(), s));

		Optional<Object> paramValueOptional = this.bodyParamReader
				.readBodyParam(paramPath, paramType, requestBodyString,
						nameMatchingMode, selectRequestBodyMapper(mediaType))
				.map(Optional::of)
				.orElse(defaultValueOptional);

		boolean required = bodyParamAnnotation.required() && defaultValueOptional.isEmpty();

		// If a valid parameter value is required it either has to be in the request
		// body or a default value should have been provided.
		if (required && paramValueOptional.isEmpty())
			throw new ResponseStatusException(HttpStatus.BAD_REQUEST,
					"Could not find any fields in request body matching parameter path " + paramPath
							+ " using the name matching mode " + nameMatchingMode);

		// We can assume we either have a value provided, a default value provided or
		// the value is optional (required
		return paramValueOptional.orElse(null);
	}

	@Override
	public boolean supportsParameter(MethodParameter parameter) {
		return parameter.hasParameterAnnotation(BodyParam.class);
	}

	private NameMatchingMode determineNameMatchingMode(MethodParameter parameter) {
		BodyParam bodyParamAnnotation = parameter.getParameterAnnotation(BodyParam.class);

		// If name matching mode explicitly defined for parameter, use that
		if (bodyParamAnnotation.nameMatchingMode() != NameMatchingMode.DEFAULT)
			return bodyParamAnnotation.nameMatchingMode();

		// Check if method is annotated with name matching annotation first
		if (parameter.hasMethodAnnotation(NameMatching.class))
			return parameter.getMethodAnnotation(NameMatching.class).mode();

		Class<?> declaringClass = parameter.getDeclaringClass();

		// Finally, check if the type has a name matching annotation
		if (declaringClass.isAnnotationPresent(NameMatching.class))
			return declaringClass.getAnnotation(NameMatching.class).mode();

		// No overrides found, so use default
		return NameMatching.DEFAULT_MODE;

	}

	private String getBodyAsString(ServletWebRequest webRequest) throws IOException {
		return webRequest.getRequest().getReader().lines().collect(Collectors.joining(System.lineSeparator()));
	}

	private MediaType getMediaType(ServletWebRequest webRequest) {
		HttpServletRequest httpRequest = webRequest.getRequest();

		String contentType = httpRequest.getContentType();

		return MediaType.parseMediaType(contentType);
	}

	private String getParamPath(BodyParam bodyParam, MethodParameter parameter) {
		String definedArgumentPath = bodyParam.path();

		// If no argument name is explicitly defined, attempt to use parameter name
		if (definedArgumentPath.equals(ValueConstants.DEFAULT_NONE)) {
			if (parameter.getParameterName() != null)
				return parameter.getParameterName();
			throw new HttpServerErrorException(HttpStatus.INTERNAL_SERVER_ERROR,
					"Cannot resolve parameter path in controller method " + parameter.getMethod().getName()
							+ " at index " + parameter.getParameterIndex()
							+ " because no name was explicitly defined and javac \"-parameters\" compile option not used");
		}

		return definedArgumentPath;
	}

	private RequestBodyMapper selectRequestBodyMapper(MediaType mediaType) throws HttpMediaTypeNotSupportedException {
		if (!this.registeredRequestBodyMappers.containsKey(mediaType))
			throw new IllegalArgumentException("Media type " + mediaType + " is not supported by the @"
					+ BodyParam.class.getSimpleName() + " annotation.");

		return this.registeredRequestBodyMappers.get(mediaType);
	}

	static Object stringToDefaultValue(Class<?> clazz, String value) {
		if (Boolean.class == clazz)
			return Boolean.parseBoolean(value);
		if (Byte.class == clazz)
			return Byte.parseByte(value);
		if (Short.class == clazz)
			return Short.parseShort(value);
		if (Integer.class == clazz)
			return Integer.parseInt(value);
		if (Long.class == clazz)
			return Long.parseLong(value);
		if (Float.class == clazz)
			return Float.parseFloat(value);
		if (Double.class == clazz)
			return Double.parseDouble(value);
		if (String.class == clazz)
			return value;
		if (clazz.isEnum())
			return Arrays.stream(clazz.getEnumConstants()).filter(v -> v.toString().equalsIgnoreCase(value)).findFirst()
					.orElseThrow(() -> new HttpServerErrorException(HttpStatus.INTERNAL_SERVER_ERROR,
							"Cannot convert string \"" + value + " to any of the enum values of enum " + clazz));

		throw new IllegalArgumentException("Value \"" + value + "\" cannot be converted to an instance of " + clazz);
	}
}
