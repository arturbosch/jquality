package com.gitlab.artismarti.smartsmells.util;

/**
 * Written in java to export to SmartUtils.
 *
 * @author artur
 */
public final class Numbers {

	public static int toInt(final String str, final int defaultValue) {
		if (str == null) {
			return defaultValue;
		}
		try {
			return Integer.parseInt(str);
		} catch (final NumberFormatException nfe) {
			return defaultValue;
		}
	}

	public static double toDouble(final String str, final double defaultValue) {
		if (str == null) {
			return defaultValue;
		}
		try {
			return Double.parseDouble(str);
		} catch (final NumberFormatException nfe) {
			return defaultValue;
		}
	}

}
