package io.hexlet.utils;

import java.lang.reflect.Field;

public class UpdateEntity {
    public static <T> void updateEntity(T oldObj, T newObj) throws IllegalAccessException, NoSuchFieldException {
        for (Field field : newObj.getClass().getDeclaredFields()) {
            field.setAccessible(true);
            if (!field.getName().equals("id")) {
                var thisField = oldObj.getClass().getDeclaredField(field.getName());
                thisField.setAccessible(true);
                if (field.get(newObj) != null && !field.get(newObj).equals(thisField.get(oldObj))) {
                    thisField.set(oldObj, field.get(newObj));
                }
            }
        }
    }
}
