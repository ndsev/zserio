#include "gtest/gtest.h"

#include "zserio/BitStreamWriter.h"
#include "zserio/BitStreamReader.h"
#include "zserio/CppRuntimeException.h"

#include "constraints/array_lengthof_constraint/ArrayLengthofConstraint.h"

namespace constraints
{
namespace array_lengthof_constraint
{

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
};

const uint8_t ArrayLengthofConstraintTest::CORRECT_LENGTH;
const uint8_t ArrayLengthofConstraintTest::WRONG_LENGTH_LESS;
const uint8_t ArrayLengthofConstraintTest::WRONG_LENGTH_GREATER;

TEST_F(ArrayLengthofConstraintTest, readCorrectLength)
{
    zserio::BitStreamWriter writer;
    writeArrayLengthofConstraintToByteArray(writer, CORRECT_LENGTH);
    size_t writeBufferByteSize;
    const uint8_t* writeBuffer = writer.getWriteBuffer(writeBufferByteSize);
    zserio::BitStreamReader reader(writeBuffer, writeBufferByteSize);

    ArrayLengthofConstraint arrayLengthofConstraint;
    arrayLengthofConstraint.read(reader);
    ASSERT_EQ(CORRECT_LENGTH, arrayLengthofConstraint.getArray().size());
}

TEST_F(ArrayLengthofConstraintTest, readWrongLengthLess)
{
    zserio::BitStreamWriter writer;
    writeArrayLengthofConstraintToByteArray(writer, WRONG_LENGTH_LESS);
    size_t writeBufferByteSize;
    const uint8_t* writeBuffer = writer.getWriteBuffer(writeBufferByteSize);
    zserio::BitStreamReader reader(writeBuffer, writeBufferByteSize);

    ArrayLengthofConstraint arrayLengthofConstraint;
    ASSERT_THROW(arrayLengthofConstraint.read(reader), zserio::CppRuntimeException);
}

TEST_F(ArrayLengthofConstraintTest, readWrongLengthGreater)
{
    zserio::BitStreamWriter writer;
    writeArrayLengthofConstraintToByteArray(writer, WRONG_LENGTH_GREATER);
    size_t writeBufferByteSize;
    const uint8_t* writeBuffer = writer.getWriteBuffer(writeBufferByteSize);
    zserio::BitStreamReader reader(writeBuffer, writeBufferByteSize);

    ArrayLengthofConstraint arrayLengthofConstraint;
    ASSERT_THROW(arrayLengthofConstraint.read(reader), zserio::CppRuntimeException);
}

TEST_F(ArrayLengthofConstraintTest, writeCorrectLength)
{
    ArrayLengthofConstraint arrayLengthofConstraint;
    zserio::UInt32Array& array = arrayLengthofConstraint.getArray();
    array.resize(CORRECT_LENGTH);
    for (size_t i = 0; i < CORRECT_LENGTH; ++i)
        array[i] = static_cast<uint32_t>(i);

    zserio::BitStreamWriter writer;
    arrayLengthofConstraint.write(writer);

    size_t writeBufferByteSize;
    const uint8_t* writeBuffer = writer.getWriteBuffer(writeBufferByteSize);
    zserio::BitStreamReader reader(writeBuffer, writeBufferByteSize);
    ArrayLengthofConstraint readArrayLengthofConstraint(reader);
    ASSERT_EQ(CORRECT_LENGTH, readArrayLengthofConstraint.getArray().size());
    ASSERT_TRUE(arrayLengthofConstraint == readArrayLengthofConstraint);
}

TEST_F(ArrayLengthofConstraintTest, writeWrongLengthLess)
{
    ArrayLengthofConstraint arrayLengthofConstraint;
    zserio::UInt32Array& array = arrayLengthofConstraint.getArray();
    array.resize(WRONG_LENGTH_LESS);
    for (size_t i = 0; i < WRONG_LENGTH_LESS; ++i)
        array[i] = static_cast<uint32_t>(i);

    zserio::BitStreamWriter writer;
    ASSERT_THROW(arrayLengthofConstraint.write(writer), zserio::CppRuntimeException);
}

TEST_F(ArrayLengthofConstraintTest, writeWrongLengthGreater)
{
    ArrayLengthofConstraint arrayLengthofConstraint;
    zserio::UInt32Array& array = arrayLengthofConstraint.getArray();
    array.resize(WRONG_LENGTH_GREATER);
    for (size_t i = 0; i < WRONG_LENGTH_GREATER; ++i)
        array[i] = static_cast<uint32_t>(i);

    zserio::BitStreamWriter writer;
    ASSERT_THROW(arrayLengthofConstraint.write(writer), zserio::CppRuntimeException);
}

} // namespace array_lengthof_constraint
} // namespace constraints
