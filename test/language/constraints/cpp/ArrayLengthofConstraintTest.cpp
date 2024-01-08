#include "constraints/array_lengthof_constraint/ArrayLengthofConstraint.h"
#include "gtest/gtest.h"
#include "zserio/BitStreamReader.h"
#include "zserio/BitStreamWriter.h"
#include "zserio/CppRuntimeException.h"
#include "zserio/RebindAlloc.h"

namespace constraints
{
namespace array_lengthof_constraint
{

using allocator_type = ArrayLengthofConstraint::allocator_type;
template <typename T>
using vector_type = zserio::vector<T, allocator_type>;

class ArrayLengthofConstraintTest : public ::testing::Test
{
protected:
    void writeArrayLengthofConstraintToByteArray(zserio::BitStreamWriter& writer, size_t length)
    {
        writer.writeBits(static_cast<uint32_t>(length), 8); // all lengths in this test fits in a single byte
        for (size_t i = 0; i < length; ++i)
            writer.writeBits(static_cast<uint32_t>(i), 32);
    }

    static const uint8_t CORRECT_LENGTH = 6;
    static const uint8_t WRONG_LENGTH_LESS = 3;
    static const uint8_t WRONG_LENGTH_GREATER = 12;

    zserio::BitBuffer bitBuffer = zserio::BitBuffer(1024 * 8);
};

const uint8_t ArrayLengthofConstraintTest::CORRECT_LENGTH;
const uint8_t ArrayLengthofConstraintTest::WRONG_LENGTH_LESS;
const uint8_t ArrayLengthofConstraintTest::WRONG_LENGTH_GREATER;

TEST_F(ArrayLengthofConstraintTest, readConstructorCorrectLength)
{
    zserio::BitStreamWriter writer(bitBuffer);
    writeArrayLengthofConstraintToByteArray(writer, CORRECT_LENGTH);

    zserio::BitStreamReader reader(writer.getWriteBuffer(), writer.getBitPosition(), zserio::BitsTag());
    ArrayLengthofConstraint arrayLengthofConstraint(reader);
    ASSERT_EQ(CORRECT_LENGTH, arrayLengthofConstraint.getArray().size());
}

TEST_F(ArrayLengthofConstraintTest, readConstructorWrongLengthLess)
{
    zserio::BitStreamWriter writer(bitBuffer);
    writeArrayLengthofConstraintToByteArray(writer, WRONG_LENGTH_LESS);

    zserio::BitStreamReader reader(writer.getWriteBuffer(), writer.getBitPosition(), zserio::BitsTag());
    ASSERT_THROW(ArrayLengthofConstraint arrayLengthofConstraint(reader), zserio::CppRuntimeException);
}

TEST_F(ArrayLengthofConstraintTest, readConstructorWrongLengthGreater)
{
    zserio::BitStreamWriter writer(bitBuffer);
    writeArrayLengthofConstraintToByteArray(writer, WRONG_LENGTH_GREATER);

    zserio::BitStreamReader reader(writer.getWriteBuffer(), writer.getBitPosition(), zserio::BitsTag());
    ASSERT_THROW(ArrayLengthofConstraint arrayLengthofConstraint(reader), zserio::CppRuntimeException);
}

TEST_F(ArrayLengthofConstraintTest, writeCorrectLength)
{
    ArrayLengthofConstraint arrayLengthofConstraint;
    vector_type<uint32_t>& array = arrayLengthofConstraint.getArray();
    array.resize(CORRECT_LENGTH);
    for (size_t i = 0; i < CORRECT_LENGTH; ++i)
        array[i] = static_cast<uint32_t>(i);

    zserio::BitStreamWriter writer(bitBuffer);
    arrayLengthofConstraint.write(writer);

    zserio::BitStreamReader reader(writer.getWriteBuffer(), writer.getBitPosition(), zserio::BitsTag());
    ArrayLengthofConstraint readArrayLengthofConstraint(reader);
    ASSERT_EQ(CORRECT_LENGTH, readArrayLengthofConstraint.getArray().size());
    ASSERT_TRUE(arrayLengthofConstraint == readArrayLengthofConstraint);
}

TEST_F(ArrayLengthofConstraintTest, writeWrongLengthLess)
{
    ArrayLengthofConstraint arrayLengthofConstraint;
    vector_type<uint32_t>& array = arrayLengthofConstraint.getArray();
    array.resize(WRONG_LENGTH_LESS);
    for (size_t i = 0; i < WRONG_LENGTH_LESS; ++i)
        array[i] = static_cast<uint32_t>(i);

    zserio::BitStreamWriter writer(bitBuffer);
    ASSERT_THROW(arrayLengthofConstraint.write(writer), zserio::CppRuntimeException);
}

TEST_F(ArrayLengthofConstraintTest, writeWrongLengthGreater)
{
    ArrayLengthofConstraint arrayLengthofConstraint;
    vector_type<uint32_t>& array = arrayLengthofConstraint.getArray();
    array.resize(WRONG_LENGTH_GREATER);
    for (size_t i = 0; i < WRONG_LENGTH_GREATER; ++i)
        array[i] = static_cast<uint32_t>(i);

    zserio::BitStreamWriter writer(bitBuffer);
    ASSERT_THROW(arrayLengthofConstraint.write(writer), zserio::CppRuntimeException);
}

} // namespace array_lengthof_constraint
} // namespace constraints
