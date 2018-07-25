## SQLite Extension

### Motivation

With its basic language features presented in the previous sections, zserio provides a rich language for
modeling binary data streams, which are intended to be parsed sequentially. Direct access to members in the
stream is usually not possible, except for specifying the offset of a given member. Navigation between
semantically related members at different positions in the stream cannot be expressed at the stream level.
Member insertions or updates are not supported.

All in all, the stream model is not an adequate approach for updatable databases in the gigabyte size range
with lots of internal cross-references where fast access to individual members is required. In a desktop or
server environment, it would be a natural approach to model such a database as a relational database using SQL.
However, in an embedded environment with limited storage space and processing resources, a full-fledged
relational schema is too heavy-weight. To have the best of both worlds, i.e. compact storage on the one hand
and direct member access including updates on the other hand, one can adopt a hybrid data model: In this hybrid
model, the high-level access structures are strictly relational, but most of the low-level data are stored in
binary large objects (BLOBs), where the internal structure of each BLOB is modeled with zserio.

For example, we can model a digital map database as a collection of tiles resulting from a rectangular grid
where the tiles are numbered row-wise. The database has a rather trivial schema:

```
CREATE TABLE europe (tileNum INTEGER PRIMARY KEY, tile BLOB NOT NULL);
```

Accessing or updating any given tile can simply be delegated to the relational DBMS, in case of this zserio
extension, [SQLite](https://www.sqlite.org).

Assuming that the tile BLOBs have a reasonable size, each tile can be decoded on the fly to access
the individual members within the tile. For seamless modelling of this hybrid approach, we decided to add
relational extensions for SQLite to zserio. Some SQL concepts have been translated to szerio, others are
transparent to zserio and can be embedded as literal strings to be passed to the SQLite engine.

### SQLite Tables

#### SQLite Table Types and Instances

An SQL table type is a special case of a compound type, where the members of the type correspond to the
columns of a relational table.

In zserio it is possible to express the above example as follows:

**Example**

```
sql_table GeoMap
{
    int32   tileId sql "PRIMARY KEY";
    Tile    tile;
};

GeoMap europe;
GeoMap america;
```

It is important to note that the `GeoMap` is a table type and not a table. A table is defined by the instance
`europe` of type `GeoMap`. Table types have no direct equivalent in SQL. They can be used to create tables
with identical structure and column names. Each instance of an `sql_table` type in zserio translates to an
SQLite SQL table where the table name in the SQL schema is equal to the instance name in zserio. A member
definition may include an SQL constraint introduced by the keyword `sql`, followed by a literal string which is
preprocessed and then passed to the SQLite engine.

Thus, the zserio instance `america` results in the following SQL table:

```
CREATE TABLE america (tileNum INT NOT NULL PRIMARY KEY, tile BLOB NOT NULL);
```

Per default a column constraint of `NOT NULL` is used for each column, meaning there must always be a value in
the column. To loosen this constraint `sql` can be used to inject pure SQL to allow `NULL` in a column or
tighten the constraint to make the column `UNIQUE`.

It is also possible to use the zserio keyword `sql` directly inside the table definition. The main use for this
syntax is to define a primary key spanning multiple fields.

**Example**

```
sql_table BusinessLocationTable
{
    BusinessId  businessId;
    CategoryId  catId;
    Position    position sql "UNIQUE";
    int8        hasIcon sql "NULL";

    sql "PRIMARY KEY(businessId, catId)";
};
```

For the mapping of zserio types to SQL types, refer to [SQL Types Mapping](#sqlite-types-mapping).

### SQLite Constraints Preprocessing

SQL constraint strings are preprocessed before they are passed to SQLite. This allows

- to implement zserio default NOT NULL handling,
- to support zserio values inside constraint strings and
- to convert unicode, hexadecimal and octal string escape sequences.

#### Zserio NOT NULL Handling

The constraints for `sql_table` fields are translated in the following way:

- If there is either `NOT NULL` or `DEFAULT NULL` in the constraint, no further preprocessing is performed.
- Otherwise if there is `NULL` in the constraint, it is removed.
- Otherwise `NOT NULL` is added.

#### Zserio Values Handling

The preprocessor replaces all strings of the form `@DataScriptIdentifier` with the value of the identifier.
Only identifiers, which are either zserio constants or zserio enumeration type values, are replaced.

**Example**

```
enum uint8 Enum
{
    VALUE1,
    VALUE2
};

const int8 Constant = 123;

sql_table Foo
{
    uint32  colA sql "PRIMARY KEY";
    uint16  colB sql "CHECK(colB < @Constant)";
    Enum    type;

    sql "CHECK(type = @Enum.VALUE1 or colA = 0)";
};
```

A syntax error is reported if a `@`-reference is used in a SQL constraint that does not match a zserio
constant or enumeration value.

### SQLite Virtual Tables

Virtual tables in zserio are an extension to the `sql_table`. The following paragraph gives a definition
of a virtual table from the SQLite website:

_A virtual table is an interface to an external storage or computation engine that appears
to be a table but does not actually store information in the database file. In general, you
can do anything with a virtual table that can be done with an ordinary table, except that
you cannot create indices or triggers on a virtual table. Some virtual table implementations
might impose additional restrictions. For example, many virtual tables are read-only._

The syntax is modeled as an extension to the `sql_table`, where an optional module can be specified:

```
sql_table <tablename> using <modulename>
```

The following example creates a virtual table using SQLite's FTS5 module:

```
sql_table Pages using fts5
{
    string title;
    string body;
};
```

The following example creates a virtual table using the RTREE module:

```
sql_table TestTable using rtree
{
    int32 id;
    int32 minX;
    int32 maxX;
    int32 minY;
    int32 maxY;
};
```

#### SQLite Virtual Columns

When Virtual tables are used in zserio some columns are automatically defined by the module used after the
`using` keyword. The keyword `sql_virtual` allows to add generated columns to documentation (HTML) so it will
be possible to find what type the column belongs or to what features it references. There will be no code
generated for these columns.

The table generation code will not contain these columns. This keyword is not limited to virtual tables only
but it makes the most sense to just use it in virtual tables.

The syntax is modeled as an extension to the `sql_table` column definition:

```
sql_virtual <type name> <column name>;
```

The following example creates a virtual table using the SQLite FTS5 module. It will omit to create the content
column but allow to read or write to it.

**Example**

```
sql_table Pages using fts5
{
    sql_virtual string content;
};
```

### Explicit Parameters

When a  `sql_table` member is an instance of a parameterized type, the application may want to derive
the parameter values from the context (e.g. other table columns), which is not available to the zserio decoder.
In this case, the type arguments shall be marked with the keyword `explicit` to indicate that these values will
be set explicitly be the application. Otherwise, the decoder would complain about not being able to evaluate
the type arguments.

**Example**
```
struct Tile(uint8 level, uint8 width)
{
    ...
};

sql_table TileTable
{
    uint32 tileId;
    uint32 version;
    Tile(explicit level, explicit width) tile;
};
```

### SQLite WITHOUT ROWID Tables

To support the `WITHOUT ROWID` optimization in SQLite, the `sql_without_rowid` keyword is used in zserio.
A `sql_without_rowid` keyword is always a part of the `sql_table` type:

**Example**

```
sql_table WithoutRowIdTable
{
    string  word sql "PRIMARY KEY";
    uint32  count;

    sql_without_rowid;
};
```

A `sql_without_rowid` keyword must be defined after all possible fields and SQL constraints inside the SQL
table.

A `sql_without_rowid` keyword specified in `sql_table` type causes to create a corresponding SQLite table
with omitting the special "rowid" column. This may bring space and performance advantages.

Creating a SQL table using the `WITHOUT ROWID` optimization without specifying the primary key is considered
a compilation error.

### SQLite Databases

Since an SQL table is always contained in an SQL database, we introduce a `sql_database` type in zserio to
model databases. `sql_table` instances may only be created as members of an `sql_database`.

**Example**

```
sql_table GeoMap
{
    // see above
};

sql_database TheWorld
{
    GeoMap europe;
    GeoMap america;
    ...
    ..
};
```

### SQLite Types Mapping

zserio type                     | SQLite type
------------------------------- |-------------
uint8, uint16, uint32, uint64   | INTEGER
int8, int16, int32, int64       | INTEGER
bit:n (n < 64)                  | INTEGER
int:n (n <= 64)                 | INTEGER
float16                         | REAL
varuint16, varuint32, varuint64 | INTEGER
varint16, varint32, varint64    | INTEGER
bool                            | INTEGER
string                          | TEXT
enum                            | INTEGER
struct                          | BLOB
choice                          | BLOB
enum                            | BLOB

[\[top\]](ZserioLanguageOverview.md#language-guide)
