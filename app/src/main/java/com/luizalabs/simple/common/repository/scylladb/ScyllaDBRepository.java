package com.luizalabs.simple.common.repository.scylladb;

import static com.datastax.oss.driver.api.querybuilder.QueryBuilder.bindMarker;
import static com.datastax.oss.driver.api.querybuilder.QueryBuilder.deleteFrom;
import static com.datastax.oss.driver.api.querybuilder.QueryBuilder.insertInto;
import static com.datastax.oss.driver.api.querybuilder.QueryBuilder.literal;
import static com.datastax.oss.driver.api.querybuilder.QueryBuilder.selectFrom;

import static com.datastax.oss.driver.api.querybuilder.SchemaBuilder.createTable;

import java.io.Closeable;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.sql.Date;
import java.sql.Time;
import java.sql.Timestamp;
import java.util.AbstractMap.SimpleEntry;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.Map;
import java.util.stream.Collectors;

import com.datastax.oss.driver.api.core.CqlSession;
import com.datastax.oss.driver.api.core.cql.ResultSet;
import com.datastax.oss.driver.api.core.cql.Row;
import com.datastax.oss.driver.api.core.type.DataType;
import com.datastax.oss.driver.api.core.type.DataTypes;
import com.datastax.oss.driver.api.querybuilder.QueryBuilder;
import com.datastax.oss.driver.api.querybuilder.delete.Delete;
import com.datastax.oss.driver.api.querybuilder.insert.Insert;
import com.datastax.oss.driver.api.querybuilder.schema.CreateTable;
import com.datastax.oss.driver.api.querybuilder.select.Select;
import com.datastax.oss.driver.api.querybuilder.term.Term;
import com.datastax.oss.driver.api.querybuilder.update.Assignment;
import com.datastax.oss.driver.api.querybuilder.update.Update;
import com.luizalabs.simple.common.config.ScyllabDBConfig;
import com.luizalabs.simple.common.repository.AbstractRepository;

public abstract class ScyllaDBRepository<T, ID> extends AbstractRepository<T, ID> implements Closeable {
    private static final Map<Class<?>, DataType> MAP_TYPES = Map.ofEntries(
        new SimpleEntry<Class<?>, DataType>(UUID.class, DataTypes.UUID),
        new SimpleEntry<Class<?>, DataType>(Boolean.class, DataTypes.BOOLEAN),
        new SimpleEntry<Class<?>, DataType>(Byte.class, DataTypes.TINYINT),
        new SimpleEntry<Class<?>, DataType>(Short.class, DataTypes.SMALLINT),
        new SimpleEntry<Class<?>, DataType>(Integer.class, DataTypes.INT),
        new SimpleEntry<Class<?>, DataType>(Long.class, DataTypes.VARINT),
        new SimpleEntry<Class<?>, DataType>(BigInteger.class, DataTypes.BIGINT),
        new SimpleEntry<Class<?>, DataType>(Float.class, DataTypes.FLOAT),
        new SimpleEntry<Class<?>, DataType>(Double.class, DataTypes.DOUBLE),
        new SimpleEntry<Class<?>, DataType>(BigDecimal.class, DataTypes.DECIMAL),
        new SimpleEntry<Class<?>, DataType>(CharSequence.class, DataTypes.TEXT),
        new SimpleEntry<Class<?>, DataType>(Timestamp.class, DataTypes.TIMESTAMP),
        new SimpleEntry<Class<?>, DataType>(Time.class, DataTypes.TIME),
        new SimpleEntry<Class<?>, DataType>(Date.class, DataTypes.DATE)
    );

    private CqlSession session;

    public ScyllaDBRepository(ScyllabDBConfig config) {
        super();
        this.session = config.getSession();
        if (config.canCreateTable()) {
            autoCreateTable();
        }
    } 

    protected boolean autoCreateTable() {
        String idName = getIdName();
        CreateTable createTable = createTable(getTableName())
            .ifNotExists()
            .withPartitionKey(idName, getDataType(idName));
        List<String> columnNames = getColumnNames().stream()
            .filter(columnName -> !idName.equalsIgnoreCase(columnName))
            .toList();
        for (String columnName: columnNames) {
            createTable = createTable.withColumn(columnName, getDataType(columnName));
        }
        return session.execute(createTable.build()).wasApplied();
    }

    protected DataType getDataType(String columnName) {
        Class<?> clazz = (Class<?>) getColumnType(columnName);
        Optional<DataType> dataType = MAP_TYPES.entrySet().stream()
            .filter(it -> it.getKey().isAssignableFrom(clazz))
            .findFirst()
            .map(it -> it.getValue());
        return dataType.orElse(DataTypes.BLOB);
    }

    protected T parse(Row row) {
        T instance = newEntityInstance();
        for (String columnName: getColumnNames()) {
            setEntityValue(instance, columnName, row.getObject(columnName));
        }
        return instance;
    }

    protected List<T> parse(Iterator<Row> rows) {
        List<T> result = new ArrayList<>();
        while (rows.hasNext()) {
            T item = parse(rows.next());
            if (item != null) {
                result.add(item);
            }
        }
        return result;
    }

    public @Override Optional<T> findById(ID id) {
        Select select = selectFrom(getTableName())
            .columns(getColumnNames())
            .whereColumn(getIdName()).isEqualTo(bindMarker());
        ResultSet result = session.execute(session.prepare(select.build())
            .bind(id));
        Iterator<Row> rows = result.iterator();
        if (rows.hasNext()) {
            Row row = rows.next();
            return Optional.of(parse(row));
        }
        return Optional.empty();
    }

    public @Override List<T> findAll(Integer limit) {
        Select select = selectFrom(getTableName())
            .columns(getColumnNames())
            .limit(limit);
        ResultSet resultSet = session.execute(select.build());
        List<T> result = parse(resultSet.iterator());
        return result;
    }

    public @Override boolean save(T model) {
        if (!insert(model)) {
            return update(model);
        }
        return true;
    }

    public @Override boolean deleteById(ID id) {
        Delete delete = deleteFrom(getTableName())
            .whereColumn(getIdName()).isEqualTo(bindMarker());
        return session.execute(session.prepare(delete.build())
            .bind(id))
            .wasApplied();
    }

    public @Override void close() {
        if (session != null) {
            session.close();
        }
    }

    public boolean insert(T model) {
        List<String> columnsName = getColumnNames();
        Map<String, Term> newAssigments = columnsName.stream()
            .collect(Collectors.toMap(
                (columnName) -> columnName, 
                (columnName) -> literal(getEntityValue(model, columnName))
            ));
        Insert insert = insertInto(getTableName())
            .values(newAssigments)
            .ifNotExists();
        return session.execute(insert.build())
            .wasApplied();
    }

    public boolean update(T model) {
        String idName = getIdName();
        List<String> columnNames = getColumnNames().stream()
            .filter(columnName -> !idName.equalsIgnoreCase(columnName))
            .toList();
        List<Assignment> assignments = columnNames.stream()
            .map(columnName -> Assignment.setColumn(
                columnName, literal(getEntityValue(model, columnName))
            )).toList();
        Update update = QueryBuilder.update(getTableName())
            .set(assignments.toArray(Assignment[]::new))
            .whereColumn(getIdName()).isEqualTo(bindMarker())
            .ifExists();
        return session.execute(session.prepare(update.build())
            .bind(getEntityId(model)))
            .wasApplied();
    }
}
