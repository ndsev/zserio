#include "gtest/gtest.h"

#include "with_type_info_code/type_info/SqlDatabase.h"
#include "with_type_info_code/type_info/SimplePubsub.h"
#include "with_type_info_code/type_info/SimpleService.h"

namespace with_type_info_code
{

namespace type_info
{

class TypeInfoTest : public ::testing::Test
{
protected:
    void checkSqlDatabase()
    {
//        ASSERT_TRUE(isFilePresent(
//                "arguments/with_sources_amalgamation/gen/with_sources_amalgamation/WithSourcesAmalgamation.cpp"));
    }
};

TEST_F(TypeInfoTest, checkSqlDatabase)
{
}

TEST_F(TypeInfoTest, checkSimplePubsub)
{
}

TEST_F(TypeInfoTest, checkSimpleService)
{
}

} // namespace type_info

} // namespace with_type_info_code
