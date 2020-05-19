#include <cstddef>
#include <limits>

#include "zserio/VarSizeUtil.h"
#include "zserio/CppRuntimeException.h"

#include "gtest/gtest.h"

namespace zserio
{

TEST(VarSizeUtilTest, convertArraySizeToUInt32)
{
    EXPECT_NO_THROW(convertArraySizeToUInt32(0));
    EXPECT_NO_THROW(convertArraySizeToUInt32(std::numeric_limits<uint32_t>::max()));
    EXPECT_THROW(convertArraySizeToUInt32(
            static_cast<size_t>(std::numeric_limits<uint32_t>::max()) + 1), CppRuntimeException);
    EXPECT_THROW(convertArraySizeToUInt32(std::numeric_limits<size_t>::max()), CppRuntimeException);
}

TEST(VarSizeUtilTest, convertStringSizeToUInt32)
{
    EXPECT_NO_THROW(convertStringSizeToUInt32(0));
    EXPECT_NO_THROW(convertStringSizeToUInt32(std::numeric_limits<uint32_t>::max()));
    EXPECT_THROW(convertStringSizeToUInt32(
            static_cast<size_t>(std::numeric_limits<uint32_t>::max()) + 1), CppRuntimeException);
    EXPECT_THROW(convertStringSizeToUInt32(std::numeric_limits<size_t>::max()), CppRuntimeException);
}

} // namespace zserio
