package dev.spoocy.utils.reflection;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.lang.reflect.AnnotatedElement;
import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.*;
import java.util.stream.Collectors;

/**
 * @author Spoocy99 | GitHub: Spoocy99
 */

public class ReflectionNames {

	public static String toName(@Nullable AnnotatedElement element) {

        if (element == null) return null;

        if (element.getClass().equals(Class.class)) {
            return toName((Class<?>) element);
        }

        if (element.getClass().equals(Constructor.class)) {
            return toName((Constructor<?>) element);
        }

        if (element.getClass().equals(Method.class)) {
            return toName((Method) element);
        }

        if (element.getClass().equals(Field.class)) {
            return toName((Field) element);
        }

		return null;
	}

	public static String toName(@NotNull Class<?> type) {
		int dim = 0;
		while (type.isArray()) { dim++; type = type.getComponentType(); }
		return type.getName() + String.join("", Collections.nCopies(dim, "[]"));
	}

	public static String toName(@NotNull Constructor<?> constructor) {
		return String.format("%s.<init>(%s)", constructor.getName(), String.join(", ", toNames(constructor.getParameterTypes())));
	}

	public static String toName(@NotNull Method method) {
		return String.format("%s.%s(%s)", method.getDeclaringClass().getName(), method.getName(), String.join(", ", toNames(method.getParameterTypes())));
	}

	public static String toName(@NotNull Field field) {
		return String.format("%s.%s", field.getDeclaringClass().getName(), field.getName());
	}

	public static Collection<String> toNames(@NotNull Collection<? extends AnnotatedElement> elements) {
		return elements.stream().map(ReflectionNames::toName).filter(Objects::nonNull).collect(Collectors.toList());
	}

	public static Collection<String> toNames(@NotNull AnnotatedElement... elements) {
		return toNames(Arrays.asList(elements));
	}

}
