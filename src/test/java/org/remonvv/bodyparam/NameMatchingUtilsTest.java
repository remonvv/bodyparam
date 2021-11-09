package org.remonvv.bodyparam;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;

public class NameMatchingUtilsTest {

	@Test
	void isNameMatching_does_not_match_case_differences_in_exact_mode() {

		String a = "case";
		String b = "Case";

		assertFalse(NameMatchingUtils.isNameMatching(a, b, NameMatchingMode.EXACT));
	}

	@Test
	void isNameMatching_does_not_match_non_alpha_numeric_differences_in_exact_mode() {

		String a = "nonalphanumeric";
		String b = "non_alpha_numeric";

		assertFalse(NameMatchingUtils.isNameMatching(a, b, NameMatchingMode.EXACT));
	}

	@Test
	void isNameMatching_does_not_match_non_alpha_numeric_differences_in_ignore_case_mode() {

		String a = "nonalphanumeric";
		String b = "non_alpha_numeric";

		assertFalse(NameMatchingUtils.isNameMatching(a, b, NameMatchingMode.IGNORE_CASE));
	}

	@Test
	void isNameMatching_does_not_match_unequal_strings_in_exact_mode() {

		String a = "one";
		String b = "other";

		assertFalse(NameMatchingUtils.isNameMatching(a, b, NameMatchingMode.EXACT));
	}

	@Test
	void isNameMatching_does_not_match_unequal_strings_in_ignore_case_and_non_alpha_numberic_mode() {

		String a = "one";
		String b = "other";

		assertFalse(NameMatchingUtils.isNameMatching(a, b, NameMatchingMode.IGNORE_CASE_AND_NON_ALPHA_NUMERIC));
	}

	@Test
	void isNameMatching_does_not_match_unequal_strings_in_ignore_case_mode() {

		String a = "one";
		String b = "other";

		assertFalse(NameMatchingUtils.isNameMatching(a, b, NameMatchingMode.IGNORE_CASE));
	}

	@Test
	void isNameMatching_matches_case_differences_in_ignore_case_mode() {

		String a = "case";
		String b = "Case";

		assertTrue(NameMatchingUtils.isNameMatching(a, b, NameMatchingMode.IGNORE_CASE));
	}

	@Test
	void isNameMatching_matches_equal_strings_in_exact_mode() {

		String a = "exact";
		String b = "exact";

		assertTrue(NameMatchingUtils.isNameMatching(a, b, NameMatchingMode.EXACT));
	}

	@Test
	void isNameMatching_matches_equal_strings_in_ignore_case_mode() {

		String a = "exact";
		String b = "exact";

		assertTrue(NameMatchingUtils.isNameMatching(a, b, NameMatchingMode.IGNORE_CASE));
	}

	@Test
	void isNameMatching_matches_non_alpha_numeric_differences_in_ignore_case_and_non_alpha_numberic_mode() {

		String a = "nonalphanumeric";
		String b = "non_alpha_numeric";

		assertTrue(NameMatchingUtils.isNameMatching(a, b, NameMatchingMode.IGNORE_CASE_AND_NON_ALPHA_NUMERIC));
	}
}
