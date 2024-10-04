#include "gtest/gtest.h"
#include "templates/templated_struct_recursion/TemplatedStructRecursion.h"
#include "zserio/RebindAlloc.h"

namespace templates
{
namespace templated_struct_recursion
{

using allocator_type = TemplatedStructRecursion::allocator_type;
template <typename T>
using vector_type = zserio::vector<T, allocator_type>;

TEST(TemplatedStructRecursionTest, writeRead)
{
    TemplatedStructRecursion templatedStructRecursion;
    templatedStructRecursion.getRecursiveTemplate().setData(vector_type<uint32_t>{{1, 2, 3}});
    templatedStructRecursion.getRecursiveTemplate().setRecursiveTemplate(RecursiveTemplate_uint32{
            vector_type<uint32_t>{2, 3, 4},
            RecursiveTemplate_uint32{vector_type<uint32_t>{}, // lengthof(data) == 0 -> end of recursion
                    zserio::NullOpt}});

    zserio::BitBuffer bitBuffer = zserio::BitBuffer(1024 * 8);
    zserio::BitStreamWriter writer(bitBuffer);
    templatedStructRecursion.write(writer);

    zserio::BitStreamReader reader(writer.getWriteBuffer(), writer.getBitPosition(), zserio::BitsTag());
    TemplatedStructRecursion readTemplatedStructRecursion(reader);

    ASSERT_TRUE(templatedStructRecursion == readTemplatedStructRecursion);
}

} // namespace templated_struct_recursion
} // namespace templates
