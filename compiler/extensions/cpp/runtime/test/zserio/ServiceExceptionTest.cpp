#include "gtest/gtest.h"
#include "zserio/ServiceException.h"

namespace zserio
{

TEST(ServiceExceptionTest, correctTypeAfterAppend)
{
    ASSERT_THROW(
            {
                throw ServiceException() << "Test that appending using operator<< persists the exception type!";
            },
            ServiceException);
}

} // namespace zserio
