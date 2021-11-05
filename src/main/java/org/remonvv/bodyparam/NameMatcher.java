package org.remonvv.bodyparam;

final class NameMatcher {
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
