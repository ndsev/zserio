#include "gtest/gtest.h"

#include "zserio/BitStreamWriter.h"
#include "zserio/BitStreamReader.h"
#include "zserio/BitSizeOfCalculator.h"
#include "zserio/CppRuntimeException.h"

#include "union_types/simple_union/SimpleUnion.h"

namespace union_types
{
namespace simple_union
{

class SimpleUnionTest : public ::testing::Test
{
protected:
    static void writeSimpleUnionCase1ToByteArray(zserio::BitStreamWriter& writer)
    {
        writer.writeVarUInt64(SimpleUnion::CHOICE_case1Field); // choice tag
        writer.writeSignedBits(CASE1_FIELD, 8);
    }

    static void writeSimpleUnionCase2ToByteArray(zserio::BitStreamWriter& writer)
    {
        writer.writeVarUInt64(SimpleUnion::CHOICE_case2Field); // choice tag
        writer.writeBits(CASE2_FIELD, 16);
    }

    static void writeSimpleUnionCase3ToByteArray(zserio::BitStreamWriter& writer)
    {
        writer.writeVarUInt64(SimpleUnion::CHOICE_case3Field); // choice tag
        writer.writeString(CASE3_FIELD);
    }

    static void writeSimpleUnionCase4ToByteArray(zserio::BitStreamWriter& writer)
    {
        writer.writeVarUInt64(SimpleUnion::CHOICE_case4Field); // choice tag
        writer.writeSignedBits(CASE4_FIELD, 8);
    }

    static const int8_t CASE1_FIELD;
    static const uint16_t CASE2_FIELD;
    static const std::string CASE3_FIELD;
    static const int8_t CASE4_FIELD;
    static const size_t UNION_CASE1_BIT_SIZE;
    static const size_t UNION_CASE2_BIT_SIZE;
    static const size_t UNION_CASE3_BIT_SIZE;
    static const size_t UNION_CASE4_BIT_SIZE;
};

const int8_t SimpleUnionTest::CASE1_FIELD = 13;
const uint16_t SimpleUnionTest::CASE2_FIELD = 65535;
const std::string SimpleUnionTest::CASE3_FIELD = "SimpleUnion";
const int8_t SimpleUnionTest::CASE4_FIELD = 42;
const size_t SimpleUnionTest::UNION_CASE1_BIT_SIZE =
        zserio::bitSizeOfVarUInt64(SimpleUnion::CHOICE_case1Field) + 8;
const size_t SimpleUnionTest::UNION_CASE2_BIT_SIZE =
        zserio::bitSizeOfVarUInt64(SimpleUnion::CHOICE_case2Field) + 16;
const size_t SimpleUnionTest::UNION_CASE3_BIT_SIZE =
        zserio::bitSizeOfVarUInt64(SimpleUnion::CHOICE_case3Field) +
        zserio::bitSizeOfString(SimpleUnionTest::CASE3_FIELD);
const size_t SimpleUnionTest::UNION_CASE4_BIT_SIZE =
        zserio::bitSizeOfVarUInt64(SimpleUnion::CHOICE_case4Field) + 8;

TEST_F(SimpleUnionTest, emptyConstructor)
{
    SimpleUnion simpleUnion;
    ASSERT_EQ(SimpleUnion::UNDEFINED_CHOICE, simpleUnion.choiceTag());
    ASSERT_THROW(simpleUnion.bitSizeOf(), zserio::CppRuntimeException);
}

TEST_F(SimpleUnionTest, bitStreamReaderConstructor)
{
    {
        zserio::BitStreamWriter writer;
        writeSimpleUnionCase1ToByteArray(writer);
        size_t writeBufferByteSize;
        const uint8_t* writeBuffer = writer.getWriteBuffer(writeBufferByteSize);
        zserio::BitStreamReader reader(writeBuffer, writeBufferByteSize);
        SimpleUnion simpleUnion(reader);
        ASSERT_EQ(CASE1_FIELD, simpleUnion.getCase1Field());
    }
    {
        zserio::BitStreamWriter writer;
        writeSimpleUnionCase2ToByteArray(writer);
        size_t writeBufferByteSize;
        const uint8_t* writeBuffer = writer.getWriteBuffer(writeBufferByteSize);
        zserio::BitStreamReader reader(writeBuffer, writeBufferByteSize);
        SimpleUnion simpleUnion(reader);
        ASSERT_EQ(CASE2_FIELD, simpleUnion.getCase2Field());
    }
    {
        zserio::BitStreamWriter writer;
        writeSimpleUnionCase3ToByteArray(writer);
        size_t writeBufferByteSize;
        const uint8_t* writeBuffer = writer.getWriteBuffer(writeBufferByteSize);
        zserio::BitStreamReader reader(writeBuffer, writeBufferByteSize);
        SimpleUnion simpleUnion(reader);
        ASSERT_EQ(CASE3_FIELD, simpleUnion.getCase3Field());
    }
    {
        zserio::BitStreamWriter writer;
        writeSimpleUnionCase4ToByteArray(writer);
        size_t writeBufferByteSize;
        const uint8_t* writeBuffer = writer.getWriteBuffer(writeBufferByteSize);
        zserio::BitStreamReader reader(writeBuffer, writeBufferByteSize);
        SimpleUnion simpleUnion(reader);
        ASSERT_EQ(CASE4_FIELD, simpleUnion.getCase4Field());
    }
}

TEST_F(SimpleUnionTest, fieldConstructor)
{
    {
        SimpleUnion simpleUnion(CASE1_FIELD);
        ASSERT_EQ(CASE1_FIELD, simpleUnion.getCase1Field());
    }
    {
        SimpleUnion simpleUnion(CASE1_FIELD, SimpleUnion::CHOICE_case1Field);
        ASSERT_EQ(CASE1_FIELD, simpleUnion.getCase1Field());
    }
    {
        SimpleUnion simpleUnion(CASE2_FIELD);
        ASSERT_EQ(CASE2_FIELD, simpleUnion.getCase2Field());
    }
    {
        SimpleUnion simpleUnion(CASE3_FIELD);
        ASSERT_EQ(CASE3_FIELD, simpleUnion.getCase3Field());
    }
    {
        std::string movedString(1000, 'a'); // long enough to prevent small string optimization
        const void* ptr = movedString.data();
        SimpleUnion simpleUnion(std::move(movedString));
        const void* movedPtr = simpleUnion.getCase3Field().data();
        ASSERT_EQ(ptr, movedPtr);
    }
    {
        SimpleUnion simpleUnion(CASE4_FIELD, SimpleUnion::CHOICE_case4Field);
        ASSERT_EQ(CASE4_FIELD, simpleUnion.getCase4Field());
    }
    {
        ASSERT_THROW(SimpleUnion(CASE1_FIELD, SimpleUnion::CHOICE_case2Field), zserio::CppRuntimeException);
        ASSERT_THROW(SimpleUnion(CASE1_FIELD, SimpleUnion::CHOICE_case3Field), zserio::CppRuntimeException);
    }
}

TEST_F(SimpleUnionTest, choiceTag)
{
    SimpleUnion simpleUnion;
    ASSERT_EQ(SimpleUnion::UNDEFINED_CHOICE, simpleUnion.choiceTag());
    simpleUnion.setCase1Field(CASE1_FIELD);
    ASSERT_EQ(SimpleUnion::CHOICE_case1Field, simpleUnion.choiceTag());
    simpleUnion.setCase2Field(CASE2_FIELD);
    ASSERT_EQ(SimpleUnion::CHOICE_case2Field, simpleUnion.choiceTag());
    simpleUnion.setCase3Field(CASE3_FIELD);
    ASSERT_EQ(SimpleUnion::CHOICE_case3Field, simpleUnion.choiceTag());
    simpleUnion.setCase4Field(CASE4_FIELD);
    ASSERT_EQ(SimpleUnion::CHOICE_case4Field, simpleUnion.choiceTag());
}

TEST_F(SimpleUnionTest, getCase1Field)
{
    SimpleUnion simpleUnion;
    simpleUnion.setCase1Field(CASE1_FIELD);
    ASSERT_EQ(CASE1_FIELD, simpleUnion.getCase1Field());
}

TEST_F(SimpleUnionTest, getCase2Field)
{
    SimpleUnion simpleUnion;
    simpleUnion.setCase2Field(CASE2_FIELD);
    ASSERT_EQ(CASE2_FIELD, simpleUnion.getCase2Field());
}

TEST_F(SimpleUnionTest, getCase3Field)
{
    SimpleUnion simpleUnion;
    simpleUnion.setCase3Field(CASE3_FIELD);
    ASSERT_EQ(CASE3_FIELD, simpleUnion.getCase3Field());

    std::string movedString(1000, 'a'); // long enough to prevent small string optimization
    const void* ptr = movedString.data();
    simpleUnion.setCase3Field(std::move(movedString));
    const void* movedPtr = simpleUnion.getCase3Field().data();
    ASSERT_EQ(ptr, movedPtr);
    std::string& case3 = simpleUnion.getCase3Field();
    case3 = CASE3_FIELD;
    ASSERT_EQ(CASE3_FIELD, simpleUnion.getCase3Field());
}

TEST_F(SimpleUnionTest, getCase4Field)
{
    SimpleUnion simpleUnion;
    simpleUnion.setCase4Field(CASE4_FIELD);
    ASSERT_EQ(CASE4_FIELD, simpleUnion.getCase4Field());
}

TEST_F(SimpleUnionTest, bitSizeOf)
{
    SimpleUnion simpleUnion;

    simpleUnion.setCase1Field(CASE1_FIELD);
    ASSERT_EQ(UNION_CASE1_BIT_SIZE, simpleUnion.bitSizeOf());

    simpleUnion.setCase2Field(CASE2_FIELD);
    ASSERT_EQ(UNION_CASE2_BIT_SIZE, simpleUnion.bitSizeOf());

    simpleUnion.setCase3Field(CASE3_FIELD);
    ASSERT_EQ(UNION_CASE3_BIT_SIZE, simpleUnion.bitSizeOf());

    simpleUnion.setCase4Field(CASE4_FIELD);
    ASSERT_EQ(UNION_CASE4_BIT_SIZE, simpleUnion.bitSizeOf());
}

TEST_F(SimpleUnionTest, initializeOffsets)
{
    const size_t bitPosition = 1;
    {
        SimpleUnion simpleUnion;
        simpleUnion.setCase1Field(CASE1_FIELD);
        ASSERT_EQ(bitPosition + UNION_CASE1_BIT_SIZE, simpleUnion.initializeOffsets(bitPosition));
    }
    {
        SimpleUnion simpleUnion;
        simpleUnion.setCase2Field(CASE2_FIELD);
        ASSERT_EQ(bitPosition + UNION_CASE2_BIT_SIZE, simpleUnion.initializeOffsets(bitPosition));
    }
    {
        SimpleUnion simpleUnion;
        simpleUnion.setCase3Field(CASE3_FIELD);
        ASSERT_EQ(bitPosition + UNION_CASE3_BIT_SIZE, simpleUnion.initializeOffsets(bitPosition));
    }
    {
        SimpleUnion simpleUnion;
        simpleUnion.setCase4Field(CASE4_FIELD);
        ASSERT_EQ(bitPosition + UNION_CASE4_BIT_SIZE, simpleUnion.initializeOffsets(bitPosition));
    }
}

TEST_F(SimpleUnionTest, operatorEquality)
{
    SimpleUnion simpleUnion11;
    SimpleUnion simpleUnion12;
    SimpleUnion simpleUnion13;
    ASSERT_TRUE(simpleUnion11 == simpleUnion11);
    ASSERT_TRUE(simpleUnion11 == simpleUnion12);
    simpleUnion11.setCase1Field(CASE1_FIELD);
    simpleUnion12.setCase1Field(CASE1_FIELD);
    simpleUnion13.setCase1Field(CASE1_FIELD + 1);
    ASSERT_TRUE(simpleUnion11 == simpleUnion11);
    ASSERT_TRUE(simpleUnion11 == simpleUnion12);
    ASSERT_FALSE(simpleUnion11 == simpleUnion13);

    SimpleUnion simpleUnion21;
    simpleUnion21.setCase2Field(CASE2_FIELD);
    SimpleUnion simpleUnion22;
    simpleUnion22.setCase2Field(CASE2_FIELD);
    SimpleUnion simpleUnion23;
    simpleUnion23.setCase2Field(CASE2_FIELD-1);
    ASSERT_TRUE(simpleUnion21 == simpleUnion21);
    ASSERT_TRUE(simpleUnion21 == simpleUnion22);
    ASSERT_FALSE(simpleUnion21 == simpleUnion23);
    ASSERT_FALSE(simpleUnion21 == simpleUnion11);

    SimpleUnion simpleUnion4;
    simpleUnion4.setCase4Field(CASE1_FIELD); // same value as simpleUnion11, but different choice
    ASSERT_FALSE(simpleUnion11 == simpleUnion4);
}

TEST_F(SimpleUnionTest, hashCode)
{
    SimpleUnion simpleUnion1;
    SimpleUnion simpleUnion2;
    ASSERT_EQ(simpleUnion1.hashCode(), simpleUnion2.hashCode());
    simpleUnion1.setCase1Field(CASE1_FIELD);
    ASSERT_NE(simpleUnion1.hashCode(), simpleUnion2.hashCode());
    simpleUnion2.setCase4Field(CASE4_FIELD);
    ASSERT_NE(simpleUnion1.hashCode(), simpleUnion2.hashCode());
    simpleUnion2.setCase4Field(CASE1_FIELD); // same value as simpleUnion1
    ASSERT_NE(simpleUnion1.hashCode(), simpleUnion2.hashCode());
    simpleUnion1.setCase4Field(CASE1_FIELD); // same value as simpleUnion2
    ASSERT_EQ(simpleUnion1.hashCode(), simpleUnion2.hashCode());
}

TEST_F(SimpleUnionTest, read)
{
    {
        zserio::BitStreamWriter writer;
        writeSimpleUnionCase1ToByteArray(writer);
        size_t writeBufferByteSize;
        const uint8_t* writeBuffer = writer.getWriteBuffer(writeBufferByteSize);
        zserio::BitStreamReader reader(writeBuffer, writeBufferByteSize);
        SimpleUnion simpleUnion;
        simpleUnion.read(reader);
        ASSERT_EQ(CASE1_FIELD, simpleUnion.getCase1Field());
    }
    {
        zserio::BitStreamWriter writer;
        writeSimpleUnionCase2ToByteArray(writer);
        size_t writeBufferByteSize;
        const uint8_t* writeBuffer = writer.getWriteBuffer(writeBufferByteSize);
        zserio::BitStreamReader reader(writeBuffer, writeBufferByteSize);
        SimpleUnion simpleUnion;
        simpleUnion.read(reader);
        ASSERT_EQ(CASE2_FIELD, simpleUnion.getCase2Field());
    }
    {
        zserio::BitStreamWriter writer;
        writeSimpleUnionCase3ToByteArray(writer);
        size_t writeBufferByteSize;
        const uint8_t* writeBuffer = writer.getWriteBuffer(writeBufferByteSize);
        zserio::BitStreamReader reader(writeBuffer, writeBufferByteSize);
        SimpleUnion simpleUnion;
        simpleUnion.read(reader);
        ASSERT_EQ(CASE3_FIELD, simpleUnion.getCase3Field());
    }
    {
        zserio::BitStreamWriter writer;
        writeSimpleUnionCase4ToByteArray(writer);
        size_t writeBufferByteSize;
        const uint8_t* writeBuffer = writer.getWriteBuffer(writeBufferByteSize);
        zserio::BitStreamReader reader(writeBuffer, writeBufferByteSize);
        SimpleUnion simpleUnion;
        simpleUnion.read(reader);
        ASSERT_EQ(CASE4_FIELD, simpleUnion.getCase4Field());
    }
}

TEST_F(SimpleUnionTest, write)
{
    SimpleUnion simpleUnion;
    {
        simpleUnion.setCase1Field(CASE1_FIELD);

        zserio::BitStreamWriter writer;
        simpleUnion.write(writer);
        size_t writeBufferByteSize;
        const uint8_t* writeBuffer = writer.getWriteBuffer(writeBufferByteSize);

        zserio::BitStreamReader reader(writeBuffer, writeBufferByteSize);
        SimpleUnion readSimpleUnion(reader);
        ASSERT_TRUE(simpleUnion == readSimpleUnion);
    }
    {
        simpleUnion.setCase2Field(CASE2_FIELD);

        zserio::BitStreamWriter writer;
        simpleUnion.write(writer);
        size_t writeBufferByteSize;
        const uint8_t* writeBuffer = writer.getWriteBuffer(writeBufferByteSize);

        zserio::BitStreamReader reader(writeBuffer, writeBufferByteSize);
        SimpleUnion readSimpleUnion(reader);
        ASSERT_TRUE(simpleUnion == readSimpleUnion);
    }
    {
        simpleUnion.setCase3Field(CASE3_FIELD);

        zserio::BitStreamWriter writer;
        simpleUnion.write(writer);
        size_t writeBufferByteSize;
        const uint8_t* writeBuffer = writer.getWriteBuffer(writeBufferByteSize);

        zserio::BitStreamReader reader(writeBuffer, writeBufferByteSize);
        SimpleUnion readSimpleUnion(reader);
        ASSERT_TRUE(simpleUnion == readSimpleUnion);
    }
    {
        simpleUnion.setCase4Field(CASE4_FIELD);

        zserio::BitStreamWriter writer;
        simpleUnion.write(writer);
        size_t writeBufferByteSize;
        const uint8_t* writeBuffer = writer.getWriteBuffer(writeBufferByteSize);

        zserio::BitStreamReader reader(writeBuffer, writeBufferByteSize);
        SimpleUnion readSimpleUnion(reader);
        ASSERT_TRUE(simpleUnion == readSimpleUnion);
    }
}

} // namespace simple_union
} // namespace union_types
