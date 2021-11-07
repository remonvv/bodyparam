package org.remonvv.bodyparam;

import static org.junit.jupiter.api.Assertions.assertThrows;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

public class BodyParamReaderTest {

	BodyParamReader sut;

	@BeforeEach
	void beforeEach() {
		this.sut = new JsonBodyParamReader();
	}

	@Test
	void readParam_throws_exception_if_paramName_is_blank() {
		assertThrows(IllegalArgumentException.class,
				() -> this.sut.readParam("", String.class, "", NameMatchingMode.EXACT));
	}

	@Test
	void readParam_throws_exception_if_paramName_is_null() {

		assertThrows(IllegalArgumentException.class,
				() -> this.sut.readParam(null, String.class, "", NameMatchingMode.EXACT));
	}

}
