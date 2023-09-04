#include <cstddef>
#include <limits>

#include "zserio/SizeConvertUtil.h"
#include "zserio/CppRuntimeException.h"
#include "zserio/RuntimeArch.h"

#include "gtest/gtest.h"

namespace zserio
{

TEST(SizeConvertUtilTest, convertSizeToUInt32)
{
    EXPECT_NO_THROW(convertSizeToUInt32(0));
    EXPECT_NO_THROW(convertSizeToUInt32(std::numeric_limits<uint32_t>::max()));
#ifdef ZSERIO_RUNTIME_64BIT
    const size_t valueAboveUpperBound = static_cast<size_t>(std::numeric_limits<uint32_t>::max()) + 1;
    EXPECT_THROW(convertSizeToUInt32(valueAboveUpperBound), CppRuntimeException);
    EXPECT_THROW(convertSizeToUInt32(std::numeric_limits<size_t>::max()), CppRuntimeException);
#endif
}

TEST(SizeConvertUtilTest, convertUInt64ToSize)
{
    EXPECT_NO_THROW(convertUInt64ToSize(0));
#ifdef ZSERIO_RUNTIME_64BIT
    EXPECT_NO_THROW(convertUInt64ToSize(std::numeric_limits<uint64_t>::max()));
#else
    const uint64_t valueAboveUpperBound = static_cast<uint64_t>(std::numeric_limits<size_t>::max()) + 1;
    EXPECT_THROW(convertUInt64ToSize(valueAboveUpperBound), CppRuntimeException);
    EXPECT_THROW(convertUInt64ToSize(std::numeric_limits<uint64_t>::max()), CppRuntimeException);
#endif
}

} // namespace zserio
