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

/**
 * <p>
 * Name matching mode is used to configure the criteria on which a field in the
 * request body is matched against the given path. There are three modes:
 * {@code EXACT}, {@code IGNORE_CASE} and
 * {@code IGNORE_CASE_AND_NON_ALPHA_NUMERIC}.
 *
 * <p>
 * {@code EXACT} based matching requires the body request field name to exactly
 * match the given path.
 *
 * <p>
 * {@code IGNORE_CASE} provides case insensitive matching.
 *
 * <p>
 * {@code IGNORE_CASE_AND_NON_ALPHA_NUMERIC} is both case insensitive and
 * ignores any characters that are not alpha numeric characters.
 *
 * @author Remon van Vliet
 */
public enum NameMatchingMode {

	EXACT,
	IGNORE_CASE,
	IGNORE_CASE_AND_NON_ALPHA_NUMERIC
}
