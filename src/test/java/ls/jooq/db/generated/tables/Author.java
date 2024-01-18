/*
 * This file is generated by jOOQ.
 */
package ls.jooq.db.generated.tables;


import java.time.OffsetDateTime;
import java.util.function.Function;

import ls.jooq.db.generated.Test;
import ls.jooq.db.generated.tables.records.AuthorRecord;

import org.jooq.Field;
import org.jooq.Function4;
import org.jooq.Identity;
import org.jooq.Name;
import org.jooq.Records;
import org.jooq.Row4;
import org.jooq.Schema;
import org.jooq.SelectField;
import org.jooq.Table;
import org.jooq.TableField;
import org.jooq.TableOptions;
import org.jooq.UniqueKey;
import org.jooq.impl.DSL;
import org.jooq.impl.Internal;
import org.jooq.impl.SQLDataType;
import org.jooq.impl.TableImpl;


/**
 * This class is generated by jOOQ.
 */
@SuppressWarnings({ "all", "unchecked", "rawtypes" })
public class Author extends TableImpl<AuthorRecord> {

    private static final long serialVersionUID = 1L;

    /**
     * The reference instance of <code>test.author</code>
     */
    public static final Author AUTHOR = new Author();

    /**
     * The class holding records for this type
     */
    @Override
    public Class<AuthorRecord> getRecordType() {
        return AuthorRecord.class;
    }

    /**
     * The column <code>test.author.id</code>.
     */
    public final TableField<AuthorRecord, Integer> ID = createField(DSL.name("id"), SQLDataType.INTEGER.nullable(false).identity(true), this, "");

    /**
     * The column <code>test.author.first_name</code>.
     */
    public final TableField<AuthorRecord, String> FIRST_NAME = createField(DSL.name("first_name"), SQLDataType.VARCHAR(100).nullable(false), this, "");

    /**
     * The column <code>test.author.last_name</code>.
     */
    public final TableField<AuthorRecord, String> LAST_NAME = createField(DSL.name("last_name"), SQLDataType.VARCHAR(100).nullable(false), this, "");

    /**
     * The column <code>test.author.created</code>.
     */
    public final TableField<AuthorRecord, OffsetDateTime> CREATED = createField(DSL.name("created"), SQLDataType.TIMESTAMPWITHTIMEZONE(6).nullable(false).defaultValue(DSL.field(DSL.raw("now()"), SQLDataType.TIMESTAMPWITHTIMEZONE)), this, "");

    private Author(Name alias, Table<AuthorRecord> aliased) {
        this(alias, aliased, null);
    }

    private Author(Name alias, Table<AuthorRecord> aliased, Field<?>[] parameters) {
        super(alias, null, aliased, parameters, DSL.comment(""), TableOptions.table());
    }

    /**
     * Create an aliased <code>test.author</code> table reference
     */
    public Author(String alias) {
        this(DSL.name(alias), AUTHOR);
    }

    /**
     * Create an aliased <code>test.author</code> table reference
     */
    public Author(Name alias) {
        this(alias, AUTHOR);
    }

    /**
     * Create a <code>test.author</code> table reference
     */
    public Author() {
        this(DSL.name("author"), null);
    }

    @Override
    public Schema getSchema() {
        return aliased() ? null : Test.TEST;
    }

    @Override
    public Identity<AuthorRecord, Integer> getIdentity() {
        return (Identity<AuthorRecord, Integer>) super.getIdentity();
    }

    @Override
    public UniqueKey<AuthorRecord> getPrimaryKey() {
        return Internal.createUniqueKey(Author.AUTHOR, DSL.name("pk_author"), new TableField[] { Author.AUTHOR.ID }, true);
    }

    @Override
    public Author as(String alias) {
        return new Author(DSL.name(alias), this);
    }

    @Override
    public Author as(Name alias) {
        return new Author(alias, this);
    }

    @Override
    public Author as(Table<?> alias) {
        return new Author(alias.getQualifiedName(), this);
    }

    /**
     * Rename this table
     */
    @Override
    public Author rename(String name) {
        return new Author(DSL.name(name), null);
    }

    /**
     * Rename this table
     */
    @Override
    public Author rename(Name name) {
        return new Author(name, null);
    }

    /**
     * Rename this table
     */
    @Override
    public Author rename(Table<?> name) {
        return new Author(name.getQualifiedName(), null);
    }

    // -------------------------------------------------------------------------
    // Row4 type methods
    // -------------------------------------------------------------------------

    @Override
    public Row4<Integer, String, String, OffsetDateTime> fieldsRow() {
        return (Row4) super.fieldsRow();
    }

    /**
     * Convenience mapping calling {@link SelectField#convertFrom(Function)}.
     */
    public <U> SelectField<U> mapping(Function4<? super Integer, ? super String, ? super String, ? super OffsetDateTime, ? extends U> from) {
        return convertFrom(Records.mapping(from));
    }

    /**
     * Convenience mapping calling {@link SelectField#convertFrom(Class,
     * Function)}.
     */
    public <U> SelectField<U> mapping(Class<U> toType, Function4<? super Integer, ? super String, ? super String, ? super OffsetDateTime, ? extends U> from) {
        return convertFrom(toType, Records.mapping(from));
    }
}
