package io.hexlet.spring.utils;

import java.lang.reflect.Field;

public class UpdateEntity {
    public static <T> void updateEntity(T oldObj, T newObj) throws IllegalAccessException, NoSuchFieldException {
        var fields = newObj.getClass().getDeclaredFields();
        for (Field fieldNewOblect : fields) {
            fieldNewOblect.setAccessible(true);
            String nameFieldNewObject = fieldNewOblect.getName();
            if (!nameFieldNewObject.equals("id")) {
                var fieldOldObject = oldObj.getClass().getDeclaredField(nameFieldNewObject);
                fieldOldObject.setAccessible(true);
                var valueFieldNewObject = fieldNewOblect.get(newObj);
                var valueFieldOldObject = fieldOldObject.get(oldObj);

                if (valueFieldNewObject != null && !valueFieldNewObject.equals(valueFieldOldObject)) {
                    fieldOldObject.set(oldObj, fieldNewOblect.get(newObj));
                }
            }
        }
    }
}
