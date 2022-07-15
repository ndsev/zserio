#include "gtest/gtest.h"

#include "zserio/PubsubException.h"

namespace zserio
{

TEST(PubsubExceptionTest, correctTypeAfterAppend)
{
    ASSERT_THROW({
        throw PubsubException() << "Test that appending using operator<< persists the exception type!";
    }, PubsubException);
}

} // namespace zserio
