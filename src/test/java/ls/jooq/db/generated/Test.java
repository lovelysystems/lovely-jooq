/*
 * This file is generated by jOOQ.
 */
package ls.jooq.db.generated;


import java.util.Arrays;
import java.util.List;

import ls.jooq.db.generated.tables.Author;
import ls.jooq.db.generated.tables.Book;

import org.jooq.Catalog;
import org.jooq.Table;
import org.jooq.impl.SchemaImpl;


/**
 * This class is generated by jOOQ.
 */
@SuppressWarnings({ "all", "unchecked", "rawtypes" })
public class Test extends SchemaImpl {

    private static final long serialVersionUID = 1L;

    /**
     * The reference instance of <code>test</code>
     */
    public static final Test TEST = new Test();

    /**
     * The table <code>test.author</code>.
     */
    public final Author AUTHOR = Author.AUTHOR;

    /**
     * The table <code>test.book</code>.
     */
    public final Book BOOK = Book.BOOK;

    /**
     * No further instances allowed
     */
    private Test() {
        super("test", null);
    }


    @Override
    public Catalog getCatalog() {
        return DefaultCatalog.DEFAULT_CATALOG;
    }

    @Override
    public final List<Table<?>> getTables() {
        return Arrays.asList(
            Author.AUTHOR,
            Book.BOOK
        );
    }
}
