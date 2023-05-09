#include "gtest/gtest.h"

#include "builtin_types/bitfield_function_length/Container.h"

#include "zserio/SerializeUtil.h"
#include "zserio/Vector.h"

namespace builtin_types
{
namespace bitfield_function_length
{

using allocator_type = Container::allocator_type;
template <typename T>
using vector_type = zserio::vector<T, allocator_type>;

class BitFieldFunctionLengthTest : public ::testing::Test
{
protected:
    Container createContainer()
    {
        return Container(
                0xDEAD, // id
                vector_type<uint64_t>({0xDEAD1, 0xDEAD2, 0xDEAD3, 0xDEAD4,
                        0xDEAD5, 0xDEAD6, 0xDEAD7}), // array[7]
                0x3F, // bitField3 (7 bits)
                0x1FFF, // bitField4 (0xDEAD & 0x0F = 0xD = 13 bits)
                0x1FFF // bitField5 (0xDEAD % 32 = 13 bits)
                );
    }

    static const std::string BLOB_NAME;
};

const std::string BitFieldFunctionLengthTest::BLOB_NAME =
        "language/builtin_types/bit_field_function_length.blob";

TEST_F(BitFieldFunctionLengthTest, bitSizeOf)
{
    const Container container = createContainer();
    const size_t expectedBitSizeOfContainer = 64 + 7 * 64 + 7 + 13 + 13;
    ASSERT_EQ(expectedBitSizeOfContainer, container.bitSizeOf());
}

TEST_F(BitFieldFunctionLengthTest, readWrite)
{
    Container container = createContainer();
    zserio::serializeToFile(container, BLOB_NAME);
    const Container readContainer = zserio::deserializeFromFile<Container>(BLOB_NAME);
    ASSERT_TRUE(container == readContainer);
}

} // namespace bitfield_function_length
} // namespace builtin_types
