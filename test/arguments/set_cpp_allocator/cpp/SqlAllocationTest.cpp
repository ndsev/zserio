#include "gtest/gtest.h"

#include "sql_allocation/SqlAllocationDb.h"

#include "test_utils/MemoryResources.h"
#include "test_utils/ValidationObservers.h"

using namespace zserio::literals;
using namespace test_utils;

namespace sql_allocation
{

using allocator_type = SqlAllocationDb::allocator_type;
using string_type = zserio::string<allocator_type>;
template <typename T>
using vector_type = zserio::vector<T, allocator_type>;
using BitBuffer = zserio::BasicBitBuffer<zserio::RebindAlloc<allocator_type, uint8_t>>;

class SqlAllocationTest : public ::testing::Test
{
public:
    SqlAllocationTest() :
            m_invalidMemoryResource(),
            m_invalidMemoryResourceSetter(m_invalidMemoryResource),
            m_memoryResource("Memory resource"),
            m_allocator(&m_memoryResource)
    {
        std::remove(DB_FILE_NAME.c_str());

        m_database.reset(new SqlAllocationDb(DB_FILE_NAME, m_allocator));
        m_database->createSchema();
    }

    ~SqlAllocationTest() override
    {
        m_database.reset();
        EXPECT_EQ(m_memoryResource.getNumDeallocations(), m_memoryResource.getNumAllocations());
    }

    SqlAllocationTest(const SqlAllocationTest&) = delete;
    SqlAllocationTest& operator=(const SqlAllocationTest&) = delete;

    SqlAllocationTest(SqlAllocationTest&&) = delete;
    SqlAllocationTest& operator=(SqlAllocationTest&&) = delete;

protected:
    class SqlAllocationTableParameterProvider : public SqlAllocationTable::IParameterProvider
    {
    public:
        DataBlob& getDataBlob(SqlAllocationTable::Row&) override
        {
            return m_dataBlob;
        }

    private:
        DataBlob m_dataBlob = DataBlob{10, MAGIC};
    };

    class SqlAllocationDbParameterProvider : public SqlAllocationDb::IParameterProvider
    {
    public:
        SqlAllocationTableParameterProvider& getAllocationTableParameterProvider() override
        {
            return m_sqlAllocationTableParameterProvider;
        }

    private:
        SqlAllocationTableParameterProvider m_sqlAllocationTableParameterProvider;
    };

    const allocator_type& getAllocator() const
    {
        return m_allocator;
    }

    void fillTableRows(vector_type<SqlAllocationTable::Row>& rows,
            SqlAllocationTableParameterProvider& parameterProvider, const allocator_type& allocator)
    {
        rows.resize(NUM_ROWS);
        for (uint32_t i = 0; i < NUM_ROWS; ++i)
            fillTableRow(rows.at(i), parameterProvider, allocator, i);
    }

    void fillTableRow(SqlAllocationTable::Row& row, SqlAllocationTableParameterProvider& parameterProvider,
            const allocator_type& allocator, uint32_t id, uint32_t len = 0)
    {
        row.setIdWithVeryLongNameAndYetLongerName(id);
        row.setTextWithVeryLongNameAndYetLongerName(string_type(
                "This is constant string longer than 32 bytes (", allocator) +
                zserio::toString(id, allocator) + ")");
        row.setDataBlobWithVeryLongNameAndYetLongerName(DataBlob{len == 0 ? id + 1 : len, MAGIC});
        row.setParameterizedBlobWithVeryLongNameAndYetLongerName(
                createParameterizedBlob(row.getDataBlobWithVeryLongNameAndYetLongerName(), allocator));
        row.setParameterizedBlobExplicitWithVeryLongNameAndYetLongerName(
                createParameterizedBlob(parameterProvider.getDataBlob(row), allocator));
        row.setColorWithVeryLongNameAndYetLongerName(id != RED_ROW_ID ? Color::GREEN : Color::RED);
        row.setRoleWithVeryLongNameAndYetLongerName(Role::Values::MEMBER);
    }

    ParameterizedBlob createParameterizedBlob(DataBlob& dataBlob, const allocator_type& allocator)
    {
        ParameterizedBlob parameterizedBlob{allocator};
        parameterizedBlob.initialize(dataBlob);
        for (uint32_t i = 0; i < dataBlob.getLen(); ++i)
            parameterizedBlob.getArr().push_back(i);
        return parameterizedBlob;
    }

    static void checkTableRow(const SqlAllocationTable::Row& row1, const SqlAllocationTable::Row& row2)
    {
        ASSERT_EQ(row1.getIdWithVeryLongNameAndYetLongerName(),
                row2.getIdWithVeryLongNameAndYetLongerName());
        ASSERT_EQ(row1.getTextWithVeryLongNameAndYetLongerName(),
                row2.getTextWithVeryLongNameAndYetLongerName());
        ASSERT_EQ(row1.getDataBlobWithVeryLongNameAndYetLongerName(),
                row2.getDataBlobWithVeryLongNameAndYetLongerName());
        ASSERT_EQ(row1.getParameterizedBlobWithVeryLongNameAndYetLongerName(),
                row2.getParameterizedBlobWithVeryLongNameAndYetLongerName());
        ASSERT_EQ(row1.getParameterizedBlobExplicitWithVeryLongNameAndYetLongerName(),
                row2.getParameterizedBlobExplicitWithVeryLongNameAndYetLongerName());
        ASSERT_EQ(row1.getColorWithVeryLongNameAndYetLongerName(),
                row2.getColorWithVeryLongNameAndYetLongerName());
        ASSERT_EQ(row1.getRoleWithVeryLongNameAndYetLongerName(),
                row2.getRoleWithVeryLongNameAndYetLongerName());
    }

    static void checkTableRows(const vector_type<SqlAllocationTable::Row>& rows1,
            const vector_type<SqlAllocationTable::Row>& rows2)
    {
        ASSERT_EQ(rows1.size(), rows2.size());
        for (size_t i = 0; i < rows1.size(); ++i)
            checkTableRow(rows1.at(i), rows2.at(i));
    }

private:
    InvalidMemoryResource m_invalidMemoryResource;
    MemoryResourceScopedSetter m_invalidMemoryResourceSetter;
    TestMemoryResource<10*1024> m_memoryResource;
    allocator_type m_allocator;

protected:
    std::unique_ptr<SqlAllocationDb> m_database; // must be behind the m_memoryResource
    static constexpr uint32_t NUM_ROWS = 3;
    static constexpr uint32_t RED_ROW_ID = 1;
    static constexpr uint32_t MAGIC = 1;
    static constexpr uint32_t WRONG_MAGIC = 0;

private:
    static const string_type DB_FILE_NAME;
};

constexpr uint32_t SqlAllocationTest::NUM_ROWS;
constexpr uint32_t SqlAllocationTest::RED_ROW_ID;
constexpr uint32_t SqlAllocationTest::MAGIC;
constexpr uint32_t SqlAllocationTest::WRONG_MAGIC;
const string_type SqlAllocationTest::DB_FILE_NAME =
        "arguments/set_cpp_allocator/sql_allocation_test.sqlite";

TEST_F(SqlAllocationTest, readWithoutCondition)
{
    SqlAllocationTableParameterProvider parameterProvider;
    vector_type<SqlAllocationTable::Row> writtenRows(getAllocator());
    fillTableRows(writtenRows, parameterProvider, getAllocator());

    SqlAllocationTable& sqlAllocationTable = m_database->getAllocationTable();
    sqlAllocationTable.write(parameterProvider, writtenRows);

    SqlAllocationTable::Reader reader = sqlAllocationTable.createReader(parameterProvider);

    vector_type<SqlAllocationTable::Row> readRows{getAllocator()};
    while (reader.hasNext())
        readRows.emplace_back(reader.next());
    checkTableRows(writtenRows, readRows);
}

TEST_F(SqlAllocationTest, readWithCondition)
{
    SqlAllocationTableParameterProvider parameterProvider;
    vector_type<SqlAllocationTable::Row> writtenRows(getAllocator());
    fillTableRows(writtenRows, parameterProvider, getAllocator());

    SqlAllocationTable& sqlAllocationTable = m_database->getAllocationTable();
    sqlAllocationTable.write(parameterProvider, writtenRows);

    const string_type condition = string_type("colorWithVeryLongNameAndYetLongerName=", getAllocator()) +
            zserio::toString(zserio::enumToValue(Color::RED), getAllocator());

    SqlAllocationTable::Reader reader = sqlAllocationTable.createReader(parameterProvider, condition);
    ASSERT_TRUE(reader.hasNext());
    SqlAllocationTable::Row readRow = reader.next();
    ASSERT_FALSE(reader.hasNext());

    checkTableRow(writtenRows[RED_ROW_ID], readRow);
}

TEST_F(SqlAllocationTest, update)
{
    SqlAllocationTableParameterProvider parameterProvider;
    vector_type<SqlAllocationTable::Row> writtenRows(getAllocator());
    fillTableRows(writtenRows, parameterProvider, getAllocator());

    SqlAllocationTable& sqlAllocationTable = m_database->getAllocationTable();
    sqlAllocationTable.write(parameterProvider, writtenRows);

    const uint32_t updateRowId = 2;
    SqlAllocationTable::Row updateRow;
    fillTableRow(updateRow, parameterProvider, getAllocator(), updateRowId, 4);
    const string_type updateCondition =
            string_type("idWithVeryLongNameAndYetLongerName=", getAllocator()) +
            zserio::toString(updateRowId, getAllocator());
    sqlAllocationTable.update(parameterProvider, updateRow, updateCondition);

    SqlAllocationTable::Reader reader = sqlAllocationTable.createReader(parameterProvider, updateCondition);
    ASSERT_TRUE(reader.hasNext());
    SqlAllocationTable::Row readRow = reader.next();
    ASSERT_FALSE(reader.hasNext());

    checkTableRow(updateRow, readRow);
}

TEST_F(SqlAllocationTest, validateValidDatabase)
{
    SqlAllocationDbParameterProvider dbParameterProvider;
    vector_type<SqlAllocationTable::Row> writtenRows(getAllocator());
    fillTableRows(writtenRows, dbParameterProvider.getAllocationTableParameterProvider(), getAllocator());

    SqlAllocationTable& sqlAllocationTable = m_database->getAllocationTable();
    sqlAllocationTable.write(dbParameterProvider.getAllocationTableParameterProvider(), writtenRows);

    CountingValidationObserver validationObserver;
    m_database->validate(validationObserver, dbParameterProvider);

    ASSERT_EQ(1, validationObserver.getNumberOfValidatedTables());
    ASSERT_EQ(NUM_ROWS, validationObserver.getNumberOfValidatedRows());
    ASSERT_EQ(0, validationObserver.getNumberOfErrors());
}

TEST_F(SqlAllocationTest, validateInvalidSchema)
{
    zserio::SqliteConnection& connection = m_database->connection();

    const bool wasTransactionStarted = connection.startTransaction();
    connection.executeUpdate("DROP TABLE allocationTable");
    connection.executeUpdate("CREATE TABLE allocationTable("
            "idWithVeryLongNameAndYetLongerName INTEGER NOT NULL, " // shall be PK
            "textWithVeryLongNameAndYetLongerName TEXT, " // shall be NOT NULL
            "dataBlobWithVeryLongNameAndYetLongerName TEXT NOT NULL, " // wrong type, shall be BLOB
            "parameterizedBlobWithVeryLongNameAndYetLongerName BLOB NOT NULL, " // shall not be NOT NULL
            "parameterizedBlobExplicitWithVeryLongNameAndYetLongerName BLOB, "
            "colorWithVeryLongNameAndYetLongerName INTEGER PRIMARY KEY, " // shall not be PK
            // missing roleWithVeryLongNameAndYetLongerName
            "superfluousColumnWithVeryLongNameAndYetLongerName INTEGER)" // superfluous
    );
    connection.endTransaction(wasTransactionStarted);

    SqlAllocationDbParameterProvider dbParameterProvider;
    CountingValidationObserver validationObserver;
    m_database->validate(validationObserver, dbParameterProvider);

    ASSERT_EQ(1, validationObserver.getNumberOfValidatedTables());
    ASSERT_EQ(7, validationObserver.getNumberOfErrors());
}

TEST_F(SqlAllocationTest, validateInvalidField)
{
    SqlAllocationDbParameterProvider dbParameterProvider;
    vector_type<SqlAllocationTable::Row> writtenRows(getAllocator());
    fillTableRows(writtenRows, dbParameterProvider.getAllocationTableParameterProvider(), getAllocator());

    SqlAllocationTable& sqlAllocationTable = m_database->getAllocationTable();
    sqlAllocationTable.write(dbParameterProvider.getAllocationTableParameterProvider(), writtenRows);

    zserio::SqliteConnection& connection = m_database->connection();
    const bool wasTransactionStarted = connection.startTransaction();
    connection.executeUpdate("UPDATE allocationTable SET colorWithVeryLongNameAndYetLongerName = 13 "
            "WHERE idWithVeryLongNameAndYetLongerName = 0");
    connection.executeUpdate("UPDATE allocationTable SET roleWithVeryLongNameAndYetLongerName = 100000 "
            "WHERE idWithVeryLongNameAndYetLongerName = 1");
    {
        std::unique_ptr<sqlite3_stmt, zserio::SqliteFinalizer> statement(connection.prepareStatement(
                "UPDATE allocationTable SET dataBlobWithVeryLongNameAndYetLongerName = ? "
                "WHERE idWithVeryLongNameAndYetLongerName = 2"));
        BitBuffer bitBuffer{64, getAllocator()}; // for data blob 2 * uint32
        zserio::BitStreamWriter writer(bitBuffer);
        writer.writeBits(2+1, 32); // len = id + 1
        writer.writeBits(WRONG_MAGIC, 32); // magic
        sqlite3_bind_blob(statement.get(), 1, bitBuffer.getBuffer(), static_cast<int>(bitBuffer.getByteSize()),
                SQLITE_TRANSIENT);
        ASSERT_EQ(SQLITE_DONE, sqlite3_step(statement.get()));
    }
    connection.endTransaction(wasTransactionStarted);

    CountingValidationObserver validationObserver;
    m_database->validate(validationObserver, dbParameterProvider);

    ASSERT_EQ(1, validationObserver.getNumberOfValidatedTables());
    ASSERT_EQ(NUM_ROWS, validationObserver.getNumberOfValidatedRows());
    ASSERT_EQ(3, validationObserver.getNumberOfErrors());
}

} // namespace sql_allocation
