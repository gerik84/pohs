package com.redline.shop.Utils;

// clone of guava Preconditions

import android.support.annotation.Nullable;

public final class Preconditions {

	private Preconditions() {

	}

	public static void checkArgument(boolean expression) {

		if (!expression) {
			throw new IllegalArgumentException();
		}
	}

	public static void checkArgument(boolean expression, @Nullable Object errorMessage) {

		if (!expression) {
			throw new IllegalArgumentException(String.valueOf(errorMessage));
		}
	}

	public static void checkArgument(boolean expression, @Nullable String errorMessageTemplate, @Nullable Object... errorMessageArgs) {

		if (!expression) {
			throw new IllegalArgumentException(format(errorMessageTemplate, errorMessageArgs));
		}
	}

	public static void checkState(boolean expression) {

		if (!expression) {
			throw new IllegalStateException();
		}
	}

	public static void checkState(boolean expression, @Nullable Object errorMessage) {

		if (!expression) {
			throw new IllegalStateException(String.valueOf(errorMessage));
		}
	}

	public static void checkState(boolean expression, @Nullable String errorMessageTemplate, @Nullable Object... errorMessageArgs) {

		if (!expression) {
			throw new IllegalStateException(format(errorMessageTemplate, errorMessageArgs));
		}
	}

	public static <T> T checkNotNull(T reference) {

		if (reference == null) {
			throw new NullPointerException();
		}
		else {
			return reference;
		}
	}

	public static <T> T checkNotNull(T reference, @Nullable Object errorMessage) {

		if (reference == null) {
			throw new NullPointerException(String.valueOf(errorMessage));
		}
		else {
			return reference;
		}
	}

	public static <T> T checkNotNull(T reference, @Nullable String errorMessageTemplate, @Nullable Object... errorMessageArgs) {

		if (reference == null) {
			throw new NullPointerException(format(errorMessageTemplate, errorMessageArgs));
		}
		else {
			return reference;
		}
	}

	private static String format(String template, @Nullable Object... args) {

		template = String.valueOf(template);
		int args_length = args == null ? 0 : args.length;
		StringBuilder builder = new StringBuilder(template.length() + 16 * args_length);
		int templateStart = 0;

		int i;
		int placeholderStart;
		for (i = 0; i < args_length; templateStart = placeholderStart + 2) {
			placeholderStart = template.indexOf("%s", templateStart);
			if (placeholderStart == -1) {
				break;
			}

			builder.append(template.substring(templateStart, placeholderStart));
			builder.append(args[i++]);
		}

		builder.append(template.substring(templateStart));
		if (i < args_length) {
			builder.append(" [");
			builder.append(args[i++]);

			while (i < args.length) {
				builder.append(", ");
				builder.append(args[i++]);
			}

			builder.append(']');
		}

		return builder.toString();
	}
}