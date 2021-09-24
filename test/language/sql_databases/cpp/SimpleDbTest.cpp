#include <cstdio>
#include <vector>
#include <string>
#include <memory>
#include <set>
#include <algorithm>

#include "gtest/gtest.h"

#include "sql_databases/simple_db/WorldDb.h"

#include "zserio/RebindAlloc.h"
#include "zserio/SqliteFinalizer.h"
#include "zserio/StringView.h"

namespace sql_databases
{
namespace simple_db
{

using allocator_type = WorldDb::allocator_type;
using string_type = zserio::string<zserio::RebindAlloc<allocator_type, char>>;
template <typename T>
using vector_type = std::vector<T, zserio::RebindAlloc<allocator_type, T>>;
template <typename T, typename COMPARE = std::less<T>>
using set_type = std::set<T, COMPARE, zserio::RebindAlloc<allocator_type, T>>;

class SimpleDbTest : public ::testing::Test
{
public:
    SimpleDbTest() :
            m_dbFileName("language/sql_databases/simple_db_test.sqlite"),
            m_worldDbName("WorldDb"), m_europeTableName("europe"), m_americaTableName("america")
    {
        std::remove(m_dbFileName.c_str());
    }

protected:
    bool isTableInDb(zserio::ISqliteDatabase& database, const string_type& checkTableName)
    {
        string_type sqlQuery = "SELECT name FROM sqlite_master WHERE type='table' AND name='" + checkTableName +
                "'";
        std::unique_ptr<sqlite3_stmt, zserio::SqliteFinalizer> statement(
                database.connection().prepareStatement(sqlQuery));

        int result = sqlite3_step(statement.get());
        if (result == SQLITE_DONE || result != SQLITE_ROW)
            return false;

        const unsigned char* readTableName = sqlite3_column_text(statement.get(), 0);
        if (readTableName == nullptr || checkTableName.compare(reinterpret_cast<const char*>(readTableName)) != 0)
            return false;

        return true;
    }

    const string_type m_dbFileName;

    const string_type m_worldDbName;
    const string_type m_europeTableName;
    const string_type m_americaTableName;
};

TEST_F(SimpleDbTest, externalConstructor)
{
    sqlite3 *externalConnection = nullptr;
    const int result = sqlite3_open(m_dbFileName.c_str(), &externalConnection);
    ASSERT_EQ(SQLITE_OK, result);

    {
        WorldDb database(externalConnection);
        database.createSchema();
        ASSERT_EQ(externalConnection, database.connection().getConnection());
        ASSERT_EQ(zserio::SqliteConnection::EXTERNAL_CONNECTION, database.connection().getConnectionType());
    }

    ASSERT_EQ(SQLITE_OK, sqlite3_close(externalConnection));
}

TEST_F(SimpleDbTest, fileNameConstructor)
{
    WorldDb database(m_dbFileName);
    ASSERT_TRUE(database.connection().getConnection() != nullptr);
}

TEST_F(SimpleDbTest, fileNameConstructorException)
{
    ASSERT_THROW(WorldDb database("UnknownDirectory/WrongDbName"), zserio::SqliteException);
}

TEST_F(SimpleDbTest, tableGetters)
{
    WorldDb database(m_dbFileName);
    database.createSchema();

    ASSERT_TRUE(isTableInDb(database, m_europeTableName));
    ASSERT_TRUE(isTableInDb(database, m_americaTableName));
    GeoMapTable& europe = database.getEurope();
    GeoMapTable& america = database.getAmerica();
    ASSERT_TRUE(&europe != &america);
}

TEST_F(SimpleDbTest, createSchema)
{
    WorldDb database(m_dbFileName);

    ASSERT_FALSE(isTableInDb(database, m_europeTableName));
    ASSERT_FALSE(isTableInDb(database, m_americaTableName));
    database.createSchema();
    ASSERT_TRUE(isTableInDb(database, m_europeTableName));
    ASSERT_TRUE(isTableInDb(database, m_americaTableName));
}

TEST_F(SimpleDbTest, createSchemaWithoutRowIdBlackList)
{
    WorldDb database(m_dbFileName);

    ASSERT_FALSE(isTableInDb(database, m_europeTableName));
    ASSERT_FALSE(isTableInDb(database, m_americaTableName));
    const set_type<string_type> withoutRowIdTableNamesBlackList;
    database.createSchema(withoutRowIdTableNamesBlackList);
    ASSERT_TRUE(isTableInDb(database, m_europeTableName));
    ASSERT_TRUE(isTableInDb(database, m_americaTableName));
}

TEST_F(SimpleDbTest, deleteSchema)
{
    WorldDb database(m_dbFileName);
    database.createSchema();

    ASSERT_TRUE(isTableInDb(database, m_europeTableName));
    ASSERT_TRUE(isTableInDb(database, m_americaTableName));
    database.deleteSchema();
    ASSERT_FALSE(isTableInDb(database, m_europeTableName));
    ASSERT_FALSE(isTableInDb(database, m_americaTableName));
}

TEST_F(SimpleDbTest, databaseName)
{
    ASSERT_EQ(::zserio::StringView(m_worldDbName), WorldDb::databaseName());
}

TEST_F(SimpleDbTest, tableNames)
{
    vector_type<string_type> tableNames;
    std::transform(WorldDb::tableNames().begin(), WorldDb::tableNames().end(), std::back_inserter(tableNames),
            [](zserio::StringView name) -> string_type
            {
                return zserio::stringViewToString(name, allocator_type());
            }
    );

    vector_type<string_type> expectedTableNames;
    expectedTableNames.push_back(m_europeTableName);
    expectedTableNames.push_back(m_americaTableName);

    ASSERT_EQ(expectedTableNames.size(), tableNames.size());
    ASSERT_EQ(expectedTableNames, tableNames);
}

} // namespace simple_db
} // namespace sql_databases
