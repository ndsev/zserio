#include <cstdio>
#include <vector>
#include <string>

#include <sqlite3.h>

#include "gtest/gtest.h"

#include "sql_databases/MasterDatabase.h"

namespace sql_databases
{

class MasterDatabaseTest : public ::testing::Test
{
public:
    MasterDatabaseTest() : m_dbFileName("master_database_test.sqlite")
    {
        m_allTableNames.push_back("america");
        m_allTableNames.push_back("europe");
        m_allTableNames.push_back("usa");
        m_allTableNames.push_back("canada");
        m_allTableNames.push_back("slovakia");
        m_allTableNames.push_back("germany");
    }

    ~MasterDatabaseTest()
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

    typedef std::vector<std::string> StringVector;

    StringVector m_allTableNames;
};

TEST_F(MasterDatabaseTest, emptyConstructor)
{
    MasterDatabase database;
    database.open(m_dbFileName);
    ASSERT_TRUE(database.isOpen());
    database.close();
}

TEST_F(MasterDatabaseTest, externalConstructor)
{
    sqlite3 *externalConnection = NULL;
    const int result = sqlite3_open(m_dbFileName.c_str(), &externalConnection);
    ASSERT_EQ(SQLITE_OK, result);

    MasterDatabase database(externalConnection);
    database.createSchema();
    ASSERT_TRUE(database.isOpen());

    database.close();

    ASSERT_EQ(SQLITE_OK, sqlite3_close(externalConnection));
}

TEST_F(MasterDatabaseTest, fileNameConstructor)
{
    MasterDatabase database(m_dbFileName);
    ASSERT_TRUE(database.isOpen());
    database.close();
}

TEST_F(MasterDatabaseTest, externalOpen)
{
    sqlite3 *externalConnection = NULL;
    const int result = sqlite3_open(m_dbFileName.c_str(), &externalConnection);
    ASSERT_EQ(SQLITE_OK, result);

    MasterDatabase database;
    database.open(externalConnection);
    database.createSchema();
    ASSERT_TRUE(database.isOpen());

    database.close();

    ASSERT_EQ(SQLITE_OK, sqlite3_close(externalConnection));
}

TEST_F(MasterDatabaseTest, fileNameOpen)
{
    MasterDatabase database;
    database.open(m_dbFileName);
    ASSERT_TRUE(database.isOpen());
    database.close();
}

TEST_F(MasterDatabaseTest, createSchema)
{
    MasterDatabase database(m_dbFileName);

    for (StringVector::const_iterator it = m_allTableNames.begin(); it != m_allTableNames.end(); ++it)
        ASSERT_FALSE(isTableInDb(database, *it));
    database.createSchema();
    for (StringVector::const_iterator it = m_allTableNames.begin(); it != m_allTableNames.end(); ++it)
        ASSERT_TRUE(isTableInDb(database, *it));

    database.close();
}

TEST_F(MasterDatabaseTest, createSchemaWithoutRowIdBlackList)
{
    MasterDatabase database(m_dbFileName);

    for (StringVector::const_iterator it = m_allTableNames.begin(); it != m_allTableNames.end(); ++it)
        ASSERT_FALSE(isTableInDb(database, *it));
    const std::set<std::string> withoutRowIdTableNamesBlackList;
    database.createSchema(withoutRowIdTableNamesBlackList);
    for (StringVector::const_iterator it = m_allTableNames.begin(); it != m_allTableNames.end(); ++it)
        ASSERT_TRUE(isTableInDb(database, *it));

    database.close();
}

TEST_F(MasterDatabaseTest, deleteSchema)
{
    MasterDatabase database(m_dbFileName);
    database.createSchema();

    for (StringVector::const_iterator it = m_allTableNames.begin(); it != m_allTableNames.end(); ++it)
        ASSERT_TRUE(isTableInDb(database, *it));
    database.deleteSchema();
    for (StringVector::const_iterator it = m_allTableNames.begin(); it != m_allTableNames.end(); ++it)
        ASSERT_FALSE(isTableInDb(database, *it));

    database.close();
}

} // namespace sql_databases
