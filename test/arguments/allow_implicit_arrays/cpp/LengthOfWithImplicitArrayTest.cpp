#include "allow_implicit_arrays/lengthof_with_implicit_array/LengthOfWithImplicitArray.h"
#include "gtest/gtest.h"
#include "zserio/RebindAlloc.h"

namespace allow_implicit_arrays
{
namespace lengthof_with_implicit_array
{

using allocator_type = LengthOfWithImplicitArray::allocator_type;
template <typename T>
using vector_type = zserio::vector<T, allocator_type>;

TEST(LengthOfOperatorTest, GetLengthOfImplicitArray)
{
    LengthOfWithImplicitArray lengthOfWithImplicitArray;
    const size_t implicitArrayLength = 12;
    vector_type<uint8_t> implicitArray(implicitArrayLength);
    lengthOfWithImplicitArray.setImplicitArray(implicitArray);
    ASSERT_EQ(implicitArrayLength, lengthOfWithImplicitArray.funcGetLengthOfImplicitArray());
}

} // namespace lengthof_with_implicit_array
} // namespace allow_implicit_arrays
