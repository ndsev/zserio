#include <fstream>
#include <string>

#include "gtest/gtest.h"

namespace without_sql_code
{

class WithoutSqlCodeTest : public ::testing::Test
{
protected:
    bool isFilePresent(const char* fileName)
    {
        std::ifstream file(fileName);
        const bool isPresent = file.good();
        file.close();

        return isPresent;
    }
};

TEST_F(WithoutSqlCodeTest, checkSqlSources)
{
    ASSERT_FALSE(isFilePresent("arguments/without_sql_code/gen/without_sql_code/GeoMapTable.cpp"));
    ASSERT_FALSE(isFilePresent("arguments/without_sql_code/gen/without_sql_code/GeoMapTable.h"));
    ASSERT_FALSE(isFilePresent("arguments/without_sql_code/gen/without_sql_code/GeoMapTableRow.cpp"));
    ASSERT_FALSE(isFilePresent("arguments/without_sql_code/gen/without_sql_code/GeoMapTableRow.h"));
    ASSERT_FALSE(isFilePresent("arguments/without_sql_code/gen/without_sql_code/IParameterProvider.h"));
    ASSERT_FALSE(isFilePresent("arguments/without_sql_code/gen/without_sql_code/MasterDatabase.cpp"));
    ASSERT_FALSE(isFilePresent("arguments/without_sql_code/gen/without_sql_code/MasterDatabase.h"));
    ASSERT_FALSE(isFilePresent("arguments/without_sql_code/gen/without_sql_code/WorldDb.cpp"));
    ASSERT_FALSE(isFilePresent("arguments/without_sql_code/gen/without_sql_code/WorldDb.h"));
}

} // namespace without_sql_code
