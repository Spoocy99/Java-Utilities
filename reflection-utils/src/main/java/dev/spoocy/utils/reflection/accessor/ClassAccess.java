package dev.spoocy.utils.reflection.accessor;

import dev.spoocy.utils.reflection.matcher.IMatcher;
import org.jetbrains.annotations.NotNull;

import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.Set;

/**
 * @author Spoocy99 | GitHub: Spoocy99
 */

public interface ClassAccess {

    ReflectionClass reflections();

    Set<ConstructorAccessor> constructors();

    ConstructorAccessor constructor(@NotNull Class<?>... parameters);

    Set<FieldAccessor> constants();

    Set<FieldAccessor> fields();

    Set<FieldAccessor> fieldsWithAnnotation(@NotNull Class<? extends Annotation> annotation);

    Set<FieldAccessor> fields(@NotNull IMatcher<Field> matcher);

    default FieldAccessor field(@NotNull IMatcher<Field> matcher) {
        Set<FieldAccessor> fields = fields(matcher);
        return fields.isEmpty() ? null : fields.iterator().next();
    }

    Set<MethodAccessor> methods();

    Set<MethodAccessor> methodsWithAnnotation(@NotNull Class<? extends Annotation> annotation);

    Set<MethodAccessor> methods(@NotNull IMatcher<Method> matcher);

    default MethodAccessor method(@NotNull IMatcher<Method> matcher) {
        Set<MethodAccessor> methods = methods(matcher);
        return methods.isEmpty() ? null : methods.iterator().next();
    }


}
