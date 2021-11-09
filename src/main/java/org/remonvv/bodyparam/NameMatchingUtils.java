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

public abstract class NameMatchingUtils {
	private NameMatchingUtils() {
		/* prevent instantiation */
	}

	static boolean isNameMatching(String a, String b, NameMatchingMode nameMatchingMode) {
		switch (nameMatchingMode) {
		case EXACT:
			return a.equals(b);
		case IGNORE_CASE:
			return a.equalsIgnoreCase(b);
		case IGNORE_CASE_AND_NON_ALPHA_NUMERIC:
			return equalsIgnoreCaseAndNonLiterals(a, b);
		default:
			throw new IllegalArgumentException(
					"Name matching mode " + nameMatchingMode + " is not currently supported.");
		}
	}

	private static boolean equalsIgnoreCaseAndNonLiterals(String a, String b) {
		String simplifiedA = a.replaceAll("[^a-zA-Z0-9]", "");
		String simplifiedB = b.replaceAll("[^a-zA-Z0-9]", "");

		return simplifiedA.equalsIgnoreCase(simplifiedB);
	}
}
