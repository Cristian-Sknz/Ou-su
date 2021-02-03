package me.skiincraft.ousubot.view.token;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class ClassToken<T> {

    private final T item;
    private final Field[] fields;
    private final Method[] methods;

    private final List<ClassToken<?>> parents = new ArrayList<>();
    private final ClassToken<?> firstParent;

    public ClassToken(T item) {
        this.item = item;
        this.fields = item.getClass().getDeclaredFields();
        this.methods = item.getClass().getMethods();
        this.firstParent = null;
    }

    public ClassToken(T item, ClassToken<?> parent) {
        this.item = item;
        this.fields = item.getClass().getDeclaredFields();
        this.methods = item.getClass().getMethods();
        this.firstParent = parent;
    }

    public T getItem() {
        return item;
    }

    public ClassToken<?> getFirstParent() {
        return firstParent;
    }

    public List<ClassToken<?>> getParents() {
        return parents;
    }

    public boolean hasFirstParent() {
        return firstParent != null;
    }

    public boolean isArray() {
        return item.getClass().isArray();
    }

    public String get(String[] properties) throws InvocationTargetException, IllegalAccessException {
        if (properties.length == 0) {
            return item.toString();
        }
        if (properties.length == 1) {
            return get(properties[0]);
        }
        String propertyName = properties[0];
        int isArray = findArray(propertyName);
        if (propertyName.contains("()")) {
            Method method = Arrays.stream(methods)
                    .filter(streamMethod -> {
                        if (streamMethod.getParameterCount() != 0) {
                            return false;
                        }
                        return streamMethod.getName().equalsIgnoreCase(propertyName
                                .replace("()", "")
                                .replace("[" + isArray + "]", ""));
                    }).findFirst()
                    .orElse(null);

            if (method == null || method.getParameterCount() != 0 || method.getReturnType() == Void.class) {
                return String.format("#{%s.%s}", getItem().getClass().getSimpleName(), propertyName);
            }
            method.setAccessible(true);
            Object invoke = method.invoke(item);
            if (invoke == null) {
                return "null";
            }
            if (isArray != -1) {
                Object[] value = (Object[]) invoke;
                ClassToken<Object> classToken = new ClassToken<>(value[isArray], this);
                parents.add(classToken);
                return classToken.get(Arrays.copyOfRange(properties, 1, properties.length));
            }

            ClassToken<Object> classToken = new ClassToken<>(invoke, this);
            parents.add(classToken);
            return classToken.get(Arrays.copyOfRange(properties, 1, properties.length));
        }

        Field field = Arrays.stream(fields)
                .filter(streamField -> streamField.getName().equalsIgnoreCase(propertyName
                        .replace("[" + isArray + "]", "")))
                .findFirst()
                .orElse(null);

        if (field == null) {
            return String.format("#{%s.%s}", getItem().getClass().getSimpleName(), propertyName);
        }
        field.setAccessible(true);
        Object get = field.get(item);
        if (get == null) {
            return "null";
        }
        if (isArray != -1) {
            Object[] value = (Object[]) get;
            ClassToken<Object> classToken = new ClassToken<>(value[isArray], this);
            parents.add(classToken);
            return classToken.get(Arrays.copyOfRange(properties, 1, properties.length));
        }

        ClassToken<Object> classToken = new ClassToken<>(get, this);
        parents.add(classToken);
        return classToken.get(Arrays.copyOfRange(properties, 1, properties.length));
    }

    private int findArray(String str) {
        Pattern pattern = Pattern.compile("(\\[\\d+\\])");
        Matcher matcher = pattern.matcher(str);
        if (matcher.find()) {
            return Integer.parseInt(matcher.group(0).replaceAll("\\D+", ""));
        }
        return -1;
    }

    public String get(String propertyName) throws IllegalAccessException, InvocationTargetException {
        if (propertyName == null || propertyName.length() == 0) {
            return item.toString();
        }

        if (propertyName.contains(".")) {
            return get(propertyName.split("\\."));
        }
        int isArray = findArray(propertyName);
        if (propertyName.contains("()")) {
            Method method = Arrays.stream(methods)
                    .filter(streamMethod -> {
                        if (streamMethod.getParameterCount() != 0) {
                            return false;
                        }
                        return streamMethod.getName().equalsIgnoreCase(propertyName
                                .replace("()", "")
                                .replace("[" + isArray + "]", ""));
                    })
                    .findFirst()
                    .orElse(null);

            if (method == null || method.getReturnType() == Void.class) {
                return String.format("#{%s.%s}", getItem().getClass().getSimpleName(), propertyName);
            }
            method.setAccessible(true);
            Object invoke = method.invoke(item);
            if (invoke == null) {
                return "null";
            }
            if (isArray != -1) {
                Object[] value = (Object[]) invoke;
                return value[isArray].toString();
            }
            return invoke.toString();
        }

        Field field = Arrays.stream(fields)
                .filter(streamField -> streamField.getName().equalsIgnoreCase(propertyName
                        .replace("[" + isArray + "]", "")))
                .findFirst()
                .orElse(null);

        if (field == null) {
            return String.format("#{%s.%s}", getItem().getClass().getSimpleName(), propertyName);
        }
        field.setAccessible(true);
        Object get = field.get(item);
        if (get == null) {
            return "null";
        }
        if (isArray != -1) {
            Object[] value = (Object[]) get;
            return value[isArray].toString();
        }

        return get.toString();
    }

    @Override
    public String toString() {
        return "ClassToken{" +
                "item=" + item +
                ", parents=" + parents +
                ", firstParent=" + firstParent +
                '}';
    }
}
