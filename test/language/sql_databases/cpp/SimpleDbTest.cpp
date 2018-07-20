#include <cstdio>
#include <vector>
#include <string>

#include <sqlite3.h>

#include "gtest/gtest.h"

#include "sql_databases/simple_db/WorldDb.h"

namespace sql_databases
{
namespace simple_db
{

class SimpleDbTest : public ::testing::Test
{
public:
    SimpleDbTest() : m_dbFileName("simple_db_test.sqlite"), m_worldDbName("WorldDb"),
            m_europeTableName("europe"), m_americaTableName("america")
    {
    }

    ~SimpleDbTest()
    {
        std::remove(m_dbFileName.c_str());
    }

protected:
    bool isTableInDb(zserio::SqlDatabase& database, const std::string& checkTableName)
    {
        sqlite3_stmt* statement;
        std::string sqlQuery = "SELECT name FROM sqlite_master WHERE type='table' AND name='" + checkTableName +
                "'";
        int result = sqlite3_prepare_v2(database.getConnection(), sqlQuery.c_str(), -1, &statement, NULL);
        if (result != SQLITE_OK)
            return false;

        result = sqlite3_step(statement);
        if (result == SQLITE_DONE || result != SQLITE_ROW)
        {
            sqlite3_finalize(statement);
            return false;
        }

        const unsigned char* readTableName = sqlite3_column_text(statement, 0);
        if (readTableName == NULL || checkTableName.compare(reinterpret_cast<const char*>(readTableName)) != 0)
        {
            sqlite3_finalize(statement);
            return false;
        }

        sqlite3_finalize(statement);

        return true;
    }

    const std::string m_dbFileName;

    const std::string m_worldDbName;
    const std::string m_europeTableName;
    const std::string m_americaTableName;
};

TEST_F(SimpleDbTest, emptyConstructor)
{
    WorldDb database;
    database.open(m_dbFileName);
    ASSERT_TRUE(database.isOpen());
    database.close();
}

TEST_F(SimpleDbTest, externalConstructor)
{
    sqlite3 *externalConnection = NULL;
    const int result = sqlite3_open(m_dbFileName.c_str(), &externalConnection);
    ASSERT_EQ(SQLITE_OK, result);

    WorldDb database(externalConnection);
    database.createSchema();
    ASSERT_TRUE(database.isOpen());

    database.close();

    ASSERT_EQ(SQLITE_OK, sqlite3_close(externalConnection));
}

TEST_F(SimpleDbTest, fileNameConstructor)
{
    WorldDb database(m_dbFileName);
    ASSERT_TRUE(database.isOpen());
    database.close();
}

TEST_F(SimpleDbTest, externalOpen)
{
    sqlite3 *externalConnection = NULL;
    const int result = sqlite3_open(m_dbFileName.c_str(), &externalConnection);
    ASSERT_EQ(SQLITE_OK, result);

    WorldDb database;
    database.open(externalConnection);
    database.createSchema();
    ASSERT_TRUE(database.isOpen());

    database.close();

    ASSERT_EQ(SQLITE_OK, sqlite3_close(externalConnection));
}

TEST_F(SimpleDbTest, fileNameOpen)
{
    WorldDb database;
    database.open(m_dbFileName);
    ASSERT_TRUE(database.isOpen());
    database.close();
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
    ASSERT_TRUE(database.isOpen());

    database.close();
}

TEST_F(SimpleDbTest, createSchema)
{
    WorldDb database(m_dbFileName);

    ASSERT_FALSE(isTableInDb(database, m_europeTableName));
    ASSERT_FALSE(isTableInDb(database, m_americaTableName));
    database.createSchema();
    ASSERT_TRUE(isTableInDb(database, m_europeTableName));
    ASSERT_TRUE(isTableInDb(database, m_americaTableName));

    database.close();
}

TEST_F(SimpleDbTest, createSchemaWithoutRowIdBlackList)
{
    WorldDb database(m_dbFileName);

    ASSERT_FALSE(isTableInDb(database, m_europeTableName));
    ASSERT_FALSE(isTableInDb(database, m_americaTableName));
    const std::set<std::string> withoutRowIdTableNamesBlackList;
    database.createSchema(withoutRowIdTableNamesBlackList);
    ASSERT_TRUE(isTableInDb(database, m_europeTableName));
    ASSERT_TRUE(isTableInDb(database, m_americaTableName));

    database.close();
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

    database.close();
}

TEST_F(SimpleDbTest, getDatabaseName)
{
    ASSERT_EQ(m_worldDbName, WorldDb::getDatabaseName());
}

TEST_F(SimpleDbTest, fillTableNames)
{
    std::vector<std::string> tableNames;
    WorldDb::fillTableNames(tableNames);

    std::vector<std::string> expectedTableNames;
    expectedTableNames.push_back(m_europeTableName);
    expectedTableNames.push_back(m_americaTableName);

    ASSERT_EQ(expectedTableNames.size(), tableNames.size());
    ASSERT_EQ(expectedTableNames, tableNames);
}

} // namespace simple_db
} // namespace sql_databases
