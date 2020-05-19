#include <cstddef>
#include <limits>

#include "zserio/VarSizeUtil.h"
#include "zserio/CppRuntimeException.h"

#include "gtest/gtest.h"

namespace zserio
{

TEST(VarSizeUtilTest, convertSizeToUInt32)
{
    EXPECT_NO_THROW(convertSizeToUInt32(0));
    EXPECT_NO_THROW(convertSizeToUInt32(std::numeric_limits<uint32_t>::max()));
    if (sizeof(size_t) > sizeof(uint32_t))
    {
        EXPECT_THROW(convertSizeToUInt32(
                static_cast<size_t>(std::numeric_limits<uint32_t>::max()) + 1), CppRuntimeException);
        EXPECT_THROW(convertSizeToUInt32(std::numeric_limits<size_t>::max()), CppRuntimeException);
    }
}

} // namespace zserio
