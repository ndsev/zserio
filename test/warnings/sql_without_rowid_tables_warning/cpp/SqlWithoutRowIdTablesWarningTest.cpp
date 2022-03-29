#include <string>
#include <vector>

#include "gtest/gtest.h"

#include "test_utils/ZserioErrorOutput.h"

class SqlWithoutRowIdTablesWarningTest : public ::testing::Test
{
protected:
    SqlWithoutRowIdTablesWarningTest()
    :   zserioWarnings("warnings/sql_without_rowid_tables_warning")
    {}

    const test_utils::ZserioErrorOutput zserioWarnings;
};

TEST_F(SqlWithoutRowIdTablesWarningTest, integerPrimaryKey)
{
    const std::string warning = "integer_primary_key_warning.zs:3:11: "
            "Single integer primary key in without rowid table 'WithoutRowIdTable' "
            "brings performance drop.";
    ASSERT_TRUE(zserioWarnings.isPresent(warning));
}
