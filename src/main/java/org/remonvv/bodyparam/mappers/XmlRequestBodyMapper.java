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
package org.remonvv.bodyparam.mappers;

import java.lang.reflect.Type;
import java.util.HashMap;
import java.util.Map;

import org.remonvv.bodyparam.RequestBodyMapper;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.JavaType;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.dataformat.xml.XmlMapper;

public class XmlRequestBodyMapper implements RequestBodyMapper {
	private ObjectMapper objectMapper = XmlMapper.builder().build();

	@Override
	public Object convertValue(Type paramType, Object value) {
		JavaType javaType = this.objectMapper.getTypeFactory().constructType(paramType);

		return this.objectMapper.convertValue(value, javaType);
	}

	@Override
	public Map<String, Object> mapRequestBody(String requestBody) {
		TypeReference<HashMap<String, Object>> typeRef = new TypeReference<HashMap<String, Object>>() {};
		try {
			return this.objectMapper.readValue(requestBody, typeRef);
		} catch (JsonProcessingException e) {
			throw new IllegalArgumentException(
					"Request body \"" + requestBody + "\" is not valid JSON and cannot be decoded");
		}
	}

}
