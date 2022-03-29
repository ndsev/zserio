#include <string>
#include <vector>

#include "gtest/gtest.h"

#include "test_utils/ZserioErrorOutput.h"

class SqlTablesWarningTest : public ::testing::Test
{
protected:
    SqlTablesWarningTest()
    :   zserioWarnings("warnings/sql_tables_warning")
    {}

    const test_utils::ZserioErrorOutput zserioWarnings;
};

TEST_F(SqlTablesWarningTest, badOrderedPrimaryKey)
{
    const std::string warning = "bad_ordered_primary_key_warning.zs:9:9: "
            "Primary key column 'classId' is in bad order in sql table 'BadOrderedPrimaryKeyTable'.";
    ASSERT_TRUE(zserioWarnings.isPresent(warning));
}

TEST_F(SqlTablesWarningTest, duplicatedPrimaryKey)
{
    const std::string warning = "duplicated_primary_key_warning.zs:6:33: "
            "Duplicated primary key column 'classId' in sql table 'DuplicatedPrimaryKeyTable'.";
    ASSERT_TRUE(zserioWarnings.isPresent(warning));
}

TEST_F(SqlTablesWarningTest, multiplePrimaryKeys)
{
    const std::string warning = "multiple_primary_keys_warning.zs:9:9: "
            "Multiple primary keys in sql table 'MultiplePrimaryKeysTable'.";
    ASSERT_TRUE(zserioWarnings.isPresent(warning));
}

TEST_F(SqlTablesWarningTest, noPrimaryKey)
{
    const std::string warning = "no_primary_key_warning.zs:3:11: "
            "No primary key in sql table 'NoPrimaryKeyTable'.";
    ASSERT_TRUE(zserioWarnings.isPresent(warning));
}

TEST_F(SqlTablesWarningTest, notFirstPrimaryKey)
{
    const std::string warning = "not_first_primary_key_warning.zs:6:29: "
            "Primary key column 'classId' is not the first one in sql table 'NotFirstPrimaryKeyTable'.";
    ASSERT_TRUE(zserioWarnings.isPresent(warning));
}

TEST_F(SqlTablesWarningTest, notNullPrimaryKey)
{
    const std::string warning1 = "not_null_primary_key_warning.zs:5:17: "
            "Primary key column 'schoolId' can contain NULL in sql table 'NotNullPrimaryKeyTable1'.";
    ASSERT_TRUE(zserioWarnings.isPresent(warning1));

    const std::string warning2 = "not_null_primary_key_warning.zs:14:17: "
            "Primary key column 'schoolId' can contain NULL in sql table 'NotNullPrimaryKeyTable2'.";
    ASSERT_TRUE(zserioWarnings.isPresent(warning2));
}
