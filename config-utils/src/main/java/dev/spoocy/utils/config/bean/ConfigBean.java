package dev.spoocy.utils.config.bean;

import dev.spoocy.utils.common.misc.Args;
import dev.spoocy.utils.config.ConfigSection;
import dev.spoocy.utils.config.Readable;
import dev.spoocy.utils.config.ResourceResolver;
import dev.spoocy.utils.config.Writeable;
import dev.spoocy.utils.config.io.Resource;
import dev.spoocy.utils.reflection.ClassWalker;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.lang.annotation.Annotation;
import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.List;

/**
 * @author Spoocy99 | GitHub: Spoocy99
 */

public class ConfigBean<T> {

    @NotNull
    private final Class<T> clazz;

    @NotNull
    private final String resourcePath;

    @Nullable
    private final String section;


    private final boolean saveDefaults;


    private final boolean allowMissingResource;

    @NotNull
    private final String[] headerComments;

    @NotNull
    private final String[] footerComments;

    @NotNull
    private final List<BoundField> fields;

    @NotNull
    private final List<BoundHook> preHooks;

    @NotNull
    private final List<BoundHook> postHooks;

    protected ConfigBean(
            @NotNull Class<T> clazz,
            @NotNull String resourcePath,
            @Nullable String section,
            boolean saveDefaults,
            boolean allowMissingResource,
            @NotNull String[] headerComments,
            @NotNull String[] footerComments
    ) {
        this.clazz = clazz;
        this.resourcePath = resourcePath;
        this.section = section;
        this.saveDefaults = saveDefaults;
        this.allowMissingResource = allowMissingResource;
        this.headerComments = headerComments;
        this.footerComments = footerComments;
        this.fields = resolveFields(clazz);
        this.preHooks = resolveHooks(clazz, PreLoad.class);
        this.postHooks = resolveHooks(clazz, PostLoad.class);
    }

    @NotNull
    public Class<T> type() {
        return this.clazz;
    }

    @NotNull
    public String resourcePath() {
        return this.resourcePath;
    }

    @Nullable
    public String section() {
        return this.section;
    }

    public boolean saveDefaults() {
        return this.saveDefaults;
    }

    public boolean allowMissingResource() {
        return this.allowMissingResource;
    }

    @NotNull
    public String[] headerComments() {
        return this.headerComments;
    }

    @NotNull
    public String[] footerComments() {
        return this.footerComments;
    }

    @NotNull
    public List<BoundField> fields() {
        return this.fields;
    }

    @NotNull
    public List<BoundHook> preHooks() {
        return this.preHooks;
    }

    @NotNull
    public List<BoundHook> postHooks() {
        return this.postHooks;
    }

    public Resource resource(@NotNull ResourceResolver resolver) {
        if(this.resourcePath.isEmpty()) {
            throw new IllegalStateException("ConfigBean does not have a resource path: " + this.clazz.getName());
        }

        return resolver.resolve(this.resourcePath());
    }

    public PostLoadResult read(@NotNull Object instance, @NotNull ConfigSection section) {
        invokePreHooks(instance, section);

        for (BoundField field : this.fields) {
            field.load(instance, section);
        }

        return invokePostHooks(instance, section);
    }

    public void write(@NotNull Object instance, @NotNull Writeable writable) {
        writable.setHeaderComments(this.headerComments);
        writable.setFooterComments(this.footerComments);

        for (BoundField field : this.fields) {
            field.save(instance, writable);
        }
    }

    public boolean writeDefaults(@NotNull Object instance, @NotNull Writeable writable) {
        boolean changed = false;

        for (BoundField field : this.fields) {

            if (field.shouldSaveDefault()) {
                boolean written = field.saveIfMissing(instance, writable);

                if (!changed && written) {
                    changed = true;
                }
            }

        }

        if(changed) {
            writable.setHeaderComments(this.headerComments);
            writable.setFooterComments(this.footerComments);
        }

        return changed;
    }

    public void invokePreHooks(@NotNull Object instance, @NotNull Readable readable) {
        for (BoundHook hook : this.preHooks) {
            hook.invoke(instance, readable);
        }
    }

    @NotNull
    public PostLoadResult invokePostHooks(@NotNull Object instance, @NotNull Readable readable) {
        PostLoadResult result = PostLoadResult.NONE;
        for (BoundHook hook : this.postHooks) {
            PostLoadResult hookResult = hook.invoke(instance, readable);
            if (hookResult.ordinal() > result.ordinal()) {
                result = hookResult;
            }
        }
        return result;
    }

    @NotNull
    public T newInstance() {

        try {
            Constructor<T> constructor = this.clazz.getDeclaredConstructor();
            constructor.setAccessible(true);
            return constructor.newInstance();
        } catch (ReflectiveOperationException ex) {
            throw new IllegalStateException("Cannot instantiate " + this.clazz.getName(), ex);
        }

    }

    @NotNull
    private List<BoundField> resolveFields(@NotNull Class<?> type) {
        List<BoundField> fields = new ArrayList<>();

        for (Class<?> current : ClassWalker.walk(type)) {
            if (current == Object.class || current.isInterface()) {
                continue;
            }

            for (Field field : current.getDeclaredFields()) {

                if (field.isSynthetic()) {
                    continue;
                }

                int modifiers = field.getModifiers();
                if (Modifier.isStatic(modifiers) || Modifier.isTransient(modifiers) || Modifier.isFinal(modifiers)) {
                    continue;
                }

                BoundField boundField = BoundField.of(this, field);
                fields.add(boundField);
            }
        }

        return fields;
    }

    @NotNull
    private static List<BoundHook> resolveHooks(
            @NotNull Class<?> type,
            @NotNull Class<? extends Annotation> annotation
    ) {
        List<BoundHook> hooks = new ArrayList<>();

        for (Class<?> current : ClassWalker.walk(type)) {
            if (current == Object.class || current.isInterface()) {
                continue;
            }

            for (Method method : current.getDeclaredMethods()) {
                BoundHook hook = BoundHook.of(method, annotation);
                if (hook != null) {
                    hooks.add(hook);
                }
            }
        }

        return hooks;
    }

}
