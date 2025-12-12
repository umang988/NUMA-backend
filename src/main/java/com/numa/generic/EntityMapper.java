// Created by Arjunsingh Rajpurohit.
package com.numa.generic;

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.metamodel.Metamodel;
import org.springframework.stereotype.Component;

import java.lang.reflect.Field;
import java.util.HashMap;
import java.util.Map;

@Component
public class EntityMapper {

    @PersistenceContext
    private EntityManager entityManager;

    public <D, E> void map(D source, E target) {
        map(source, target, false);
    }

    public <D, E> void map(D source, E target, boolean forSerialization) {
        if (source == null || target == null) return;

        Metamodel metamodel = entityManager.getMetamodel();
        Map<String, Field> targetFields = getTargetFields(target.getClass());

        for (Field sourceField : source.getClass().getDeclaredFields()) {
            try {
                Object value = getFieldValue(source, sourceField);
                if (value == null) continue;

                String entityFieldName = getEntityFieldName(sourceField.getName(), targetFields);
                Field targetField = targetFields.get(entityFieldName);
                if (targetField == null) continue;

                try {
                    setTargetFieldValue(target, targetField, value, metamodel, forSerialization);
                } catch (IllegalAccessException e) {
                    System.err.println("Skipping field: " + targetField.getName() + " due to: " + e.getMessage());
                }
            } catch (IllegalAccessException e) {
                System.err.println("Skipping source field: " + sourceField.getName() + " due to: " + e.getMessage());
            }
        }
    }

    private Map<String, Field> getTargetFields(Class<?> targetClass) {
        Map<String, Field> fieldMap = new HashMap<>();
        for (Field field : targetClass.getDeclaredFields()) {
            field.setAccessible(true);
            fieldMap.put(field.getName().toLowerCase(), field);
        }
        return fieldMap;
    }

    private Object getFieldValue(Object source, Field field) throws IllegalAccessException {
        field.setAccessible(true);
        return field.get(source);
    }

    private void setTargetFieldValue(Object target, Field targetField, Object value, Metamodel metamodel, boolean forSerialization)
            throws IllegalAccessException {
        targetField.setAccessible(true);
        if (isEntityType(targetField.getType(), metamodel) && value instanceof Number) {
            Object entity = forSerialization ? entityManager.find(targetField.getType(), value) : entityManager.getReference(targetField.getType(), value);
            targetField.set(target, entity);
        } else {
            targetField.set(target, value);
        }
    }

    private boolean isEntityType(Class<?> clazz, Metamodel metamodel) {
        try {
            return metamodel.entity(clazz) != null;
        } catch (IllegalArgumentException e) {
            return false;
        }
    }

    private String getEntityFieldName(String dtoFieldName, Map<String, Field> targetFields) {
        String lowerCaseName = dtoFieldName.toLowerCase();
        String possibleEntityField = lowerCaseName.endsWith("id") ? lowerCaseName.substring(0, lowerCaseName.length() - 2) : lowerCaseName;
        return targetFields.containsKey(possibleEntityField) ? possibleEntityField : lowerCaseName;
    }
}