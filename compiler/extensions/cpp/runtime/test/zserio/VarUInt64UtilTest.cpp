#include <cstddef>
#include <limits>

#include "zserio/VarUInt64Util.h"
#include "zserio/CppRuntimeException.h"

#include "gtest/gtest.h"

namespace zserio
{

TEST(VarUInt64UtilTest, convertVarUInt64ToInt32)
{
    EXPECT_NO_THROW(convertVarUInt64ToInt32(0));
    EXPECT_NO_THROW(convertVarUInt64ToInt32(std::numeric_limits<int32_t>::max()));
    EXPECT_THROW(convertVarUInt64ToInt32(
            static_cast<uint64_t>(std::numeric_limits<int32_t>::max()) + 1), CppRuntimeException);
    EXPECT_THROW(convertVarUInt64ToInt32(std::numeric_limits<uint32_t>::max()), CppRuntimeException);
}

TEST(VarUInt64UtilTest, convertVarUInt64ToArraySize)
{
    EXPECT_NO_THROW(convertVarUInt64ToArraySize(0));
    EXPECT_NO_THROW(convertVarUInt64ToArraySize(std::numeric_limits<size_t>::max()));

    if (std::numeric_limits<size_t>::max() < std::numeric_limits<uint64_t>::max())
    {
        EXPECT_THROW(convertVarUInt64ToArraySize(
                static_cast<uint64_t>(std::numeric_limits<size_t>::max()) + 1), CppRuntimeException);
        EXPECT_THROW(convertVarUInt64ToArraySize(std::numeric_limits<uint64_t>::max()), CppRuntimeException);
    }
}

} // namespace zserio
