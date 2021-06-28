#include "zserio/UniquePtr.h"
#include "zserio/CppRuntimeException.h"

#include "TrackingAllocator.h"

#include "gtest/gtest.h"

namespace zserio
{

namespace
{

struct ThrowingStruct
{
    ThrowingStruct()
    {
        throw CppRuntimeException("oops");
    }
};

} // namespace

TEST(UniquePtrTest, allocateUnique)
{
    TrackingAllocator<int> alloc;

    unique_ptr<int, TrackingAllocator<int>> unique = allocate_unique<int>(alloc, 12345);
    ASSERT_TRUE(bool(unique));
    ASSERT_EQ(12345, *unique);
    ASSERT_EQ(1, alloc.numAllocs());

    ASSERT_THROW(allocate_unique<ThrowingStruct>(alloc), CppRuntimeException);
}

} // namespace zserio
