package org.remonvv.bodyparam;

import static org.remonvv.bodyparam.NameMatchingMode.IGNORE_CASE_AND_NON_ALPHA_NUMERIC;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import org.springframework.core.annotation.AliasFor;
import org.springframework.web.bind.annotation.ValueConstants;

@Target(ElementType.PARAMETER)
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface BodyParam {

	/**
	 * Specifies a default value if no value was provided in the request body for
	 * this parameter. If a default value is specified then requires=false is
	 * implied. Only Java primitive types, String values and enum values are allowed
	 * as default values.
	 */
	String defaultValue() default ValueConstants.DEFAULT_NONE;

	/**
	 * Configures which name matching mode is used to match parameter names against
	 * field names in the response body. The default name matching mode is to ignore
	 * case differences and ignore all non alpha numeric characters. For more strict
	 * matching use STRICT or IGNORE_CASE instead.
	 */
	NameMatchingMode nameMatchingMode() default IGNORE_CASE_AND_NON_ALPHA_NUMERIC;

	/**
	 * Defines the path to the value within the request body that is to be read and
	 * injected as the controller method parameter value. If the request body is a
	 * hierarchical data type (e.g. JSON or XML) any value in the hierarchy can be
	 * addressed by defining a dot separated path (e.g. "parent.myvalue" would look
	 * for a "myvalue" field in the root object "parent").
	 *
	 * If no path value is specified the parameter name is used instead. Parameter
	 * names are only available for code that is compiled with the "-parameters"
	 * compile option. It is best practice to always define a valid path value since
	 * that both removes the dependency on the aforementioned compiler flag and
	 * avoids parameter naming and schema being tightly coupled.
	 */
	@AliasFor("value")
	String path() default ValueConstants.DEFAULT_NONE;

	/**
	 * Specifies whether or not the body parameter resolver will error if no value
	 * is available in the request body for the given path. If the parameter value
	 * is not required the value will be set to null for all object types, and to
	 * the default value for all primitives.
	 */
	boolean required() default true;

	/**
	 * Alias for the path attribute
	 */
	@AliasFor("path")
	String value() default ValueConstants.DEFAULT_NONE;
}
