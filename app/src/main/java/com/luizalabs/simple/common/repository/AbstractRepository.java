package com.luizalabs.simple.common.repository;

import java.lang.reflect.Field;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.luizalabs.simple.common.repository.annotation.Column;
import com.luizalabs.simple.common.repository.annotation.Id;
import com.luizalabs.simple.common.repository.annotation.Table;
import com.luizalabs.simple.common.util.AppLogger;

public abstract class AbstractRepository<T, ID> implements Repository<T, ID> {
    private static final AppLogger LOGGER = new AppLogger(AbstractRepository.class);

    private Class<T> modelClass;
    private Map<String, Field> columnFields;
    private List<String> columnNames;
    private Field idField;
    private String idName;
    private String tableName;

    public @SuppressWarnings("unchecked") AbstractRepository() {
        ParameterizedType parameterizedType = ((ParameterizedType)getClass().getGenericSuperclass());
        modelClass = (Class<T>) parameterizedType.getActualTypeArguments()[0];
        tableName = modelClass.getAnnotation(Table.class).value();
        columnFields = new HashMap<>();
        columnNames = new ArrayList<>();
        for (Field field: modelClass.getDeclaredFields()) {
            Column column = field.getAnnotation(Column.class);            
            if (column != null) {
                String columnName = column.value();
                columnNames.add(columnName);
                field.setAccessible(true);
                columnFields.putIfAbsent(columnName, field);
                if (field.getAnnotation(Id.class) != null) {
                    idField = field;
                    idName = column.value();
                }
            }
        }
    }

    protected List<String> getColumnNames() {
        return columnNames;
    }

    protected String getTableName() {
        return tableName;
    }

    protected String getIdName() {
        return idName;
    }

    protected Type getIdType() {
        return idField.getType();
    }

    protected Type getColumnType(String columnName) {
        return columnFields.get(columnName).getType();
    }

    protected T newEntityInstance() {
        try {
            return modelClass.getConstructor().newInstance();
        } catch (ReflectiveOperationException e)  {
            LOGGER.error("Error in newEntityInstance()", e);
            return null;
        }
    }

    protected Object getEntityValue(T entity, String columnName) {
        try {
            return columnFields.get(columnName).get(entity);
        } catch (IllegalAccessException e) {
            LOGGER.error("Error in getFieldValue(" + entity + "," + columnName + ")", e);
            return null;
        }
    }

    protected <V> void setEntityValue(T entity, String columnName, V value) {
        try {
            columnFields.get(columnName).set(entity, value);
        } catch (IllegalAccessException e) {
            LOGGER.error("Error in setFieldValue(" + entity + "," + columnName + "," + value + ")", e);
        }
    }

    protected Object getEntityId(T entity) {
        try {
            return idField.get(entity);
        } catch (IllegalAccessException e) {
            LOGGER.error("Error in getFieldValue(" + entity + ")", e);
            return null;
        }
    }
}
