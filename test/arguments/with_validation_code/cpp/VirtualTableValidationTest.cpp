#include <memory>

#include "gtest/gtest.h"

#include "with_validation_code/virtual_table_validation/VirtualTableValidationDb.h"
#include "ValidationObservers.h"

using namespace utils;

namespace with_validation_code
{

namespace virtual_table_validation
{

class VirtualTableValidationTest : public ::testing::Test
{
public:
    VirtualTableValidationTest()
    {
        std::remove(DB_FILE_NAME);

        m_database.reset(new VirtualTableValidationDb(DB_FILE_NAME));
        m_database->createSchema();
    }

protected:
    std::unique_ptr<VirtualTableValidationDb> m_database;

private:
    static const char DB_FILE_NAME[];
};

const char VirtualTableValidationTest::DB_FILE_NAME[] = "virtual_table_validation_test.sqlite";

TEST_F(VirtualTableValidationTest, validate)
{
    ValidationObserver validationObserver;
    m_database->validate(validationObserver);

    ASSERT_EQ(2, validationObserver.getNumberOfTables());
    ASSERT_EQ(2, validationObserver.getNumberOfValidatedTables());
    ASSERT_EQ(0, validationObserver.getErrors().size()) << validationObserver.getErrorsString();

    const auto& tableName1 = VirtualTableValidationDb::tableNames()[0];
    ASSERT_EQ(0, validationObserver.getNumberOfTableRows(tableName1));
    ASSERT_EQ(0, validationObserver.getNumberOfValidatedTableRows(tableName1));

    const auto& tableName2 = VirtualTableValidationDb::tableNames()[1];
    ASSERT_EQ(0, validationObserver.getNumberOfTableRows(tableName2));
    ASSERT_EQ(0, validationObserver.getNumberOfValidatedTableRows(tableName2));
}

} // namespace virtual_table_validation

} // namespace with_validation_code
