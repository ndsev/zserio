#include "gtest/gtest.h"
#include "zserio/ConstraintException.h"

namespace zserio
{

TEST(ConstraintExceptionTest, correctTypeAfterAppend)
{
    ASSERT_THROW(
            {
                throw ConstraintException()
                        << "Test that appending using operator<< persists the exception type!";
            },
            ConstraintException);
}

} // namespace zserio
