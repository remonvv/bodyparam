package org.remonvv.bodyparam;

import java.io.IOException;
import java.lang.reflect.Type;
import java.util.Arrays;
import java.util.Collection;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

import javax.servlet.http.HttpServletRequest;

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

	private final Collection<BodyParamReader> bodyParamReaders = Set.of(
			new JsonBodyParamReader(),
			new XmlBodyParamReader());

	@Override
	public Object resolveArgument(MethodParameter parameter,
			ModelAndViewContainer mavContainer,
			NativeWebRequest webRequest,
			WebDataBinderFactory binderFactory) throws Exception {

		BodyParam bodyParamAnnotation = parameter.getParameterAnnotation(BodyParam.class);
		ServletWebRequest servletWebRequest = (ServletWebRequest) webRequest;

		MediaType mediaType = getMediaType(servletWebRequest);

		BodyParamReader bodyParamReader = bodyParamReaders.stream()
				.filter(r -> r.supportsMediaType(mediaType))
				.findFirst()
				.orElseThrow(() -> new HttpMediaTypeNotSupportedException(
						"Media type " + mediaType + " is not supported by the @"
								+ BodyParam.class.getSimpleName() + " annotation"));

		String requestBodyString = getBodyAsString(servletWebRequest);
		String paramName = getArgumentPath(bodyParamAnnotation, parameter);
		Type paramType = parameter.getGenericParameterType();

		Optional<Object> defaultValueOptional = Optional.ofNullable(bodyParamAnnotation.defaultValue())
				.filter(s -> !s.equals(ValueConstants.DEFAULT_NONE))
				.map(s -> stringToSimpleJavaType(parameter.getParameterType(), s));

		Optional<Object> paramValueOptional = bodyParamReader.readParam(paramName,
				paramType,
				requestBodyString,
				bodyParamAnnotation.nameMatchingMode())
				.map(Optional::of)
				.orElse(defaultValueOptional);

		boolean required = bodyParamAnnotation.required() && defaultValueOptional.isEmpty();

		// If a valid parameter value is required it either has to be in the request
		// body or a default value should have been provided.
		if (required && paramValueOptional.isEmpty())
			throw new ResponseStatusException(HttpStatus.BAD_REQUEST,
					"Could not find any fields in request body  matching parameter name "
							+ paramName
							+ " using the name matching mode " + bodyParamAnnotation.nameMatchingMode());

		// We can assume we either have a value provided, a default value provided or
		// the value is optional (required
		return paramValueOptional.orElse(null);
	}

	@Override
	public boolean supportsParameter(MethodParameter parameter) {
		return parameter.hasParameterAnnotation(BodyParam.class);
	}

	private String getArgumentPath(BodyParam bodyParam, MethodParameter parameter) {
		String definedArgumentPath = bodyParam.path();

		// If no argument name is explicitly defined, attempt to use parameter name
		if (definedArgumentPath.equals(ValueConstants.DEFAULT_NONE))
			if (parameter.getParameterName() != null)
				return parameter.getParameterName();
			else
				throw new HttpServerErrorException(HttpStatus.INTERNAL_SERVER_ERROR,
						"Cannot resolve parameter path in controller method " + parameter.getMethod().getName()
								+ " at index " + parameter.getParameterIndex()
								+ " because no name was explicitly defined and javac \"-parameters\" compile option not used");

		return definedArgumentPath;
	}

	private String getBodyAsString(ServletWebRequest webRequest) throws IOException {
		return webRequest.getRequest().getReader().lines()
				.collect(Collectors.joining(System.lineSeparator()));
	}

	private MediaType getMediaType(ServletWebRequest webRequest) {
		HttpServletRequest httpRequest = webRequest.getRequest();

		String contentType = httpRequest.getContentType();

		return MediaType.parseMediaType(contentType);
	}

	private static Object stringToSimpleJavaType(Class<?> clazz, String value) {
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
			return Arrays.stream(clazz.getEnumConstants())
					.filter(v -> v.toString().equalsIgnoreCase(value))
					.findFirst()
					.orElseThrow(() -> new HttpServerErrorException(HttpStatus.INTERNAL_SERVER_ERROR,
							"Cannot convert string \"" + value + " to any of the enum values of enum " + clazz));
		return value;
	}
}
