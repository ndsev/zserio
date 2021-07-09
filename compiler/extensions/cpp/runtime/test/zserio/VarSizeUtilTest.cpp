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
#ifdef ZSERIO_RUNTIME_64BIT
    const size_t sizeAboveUpperBound = static_cast<size_t>(std::numeric_limits<uint32_t>::max()) + 1;
    EXPECT_THROW(convertSizeToUInt32(sizeAboveUpperBound), CppRuntimeException);
    EXPECT_THROW(convertSizeToUInt32(std::numeric_limits<size_t>::max()), CppRuntimeException);
#endif
}

} // namespace zserio
