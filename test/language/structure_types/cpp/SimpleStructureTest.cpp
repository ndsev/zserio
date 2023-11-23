#include "gtest/gtest.h"

#include "zserio/BitStreamWriter.h"
#include "zserio/BitStreamReader.h"

#include "structure_types/simple_structure/SimpleStructure.h"

namespace structure_types
{
namespace simple_structure
{

class SimpleStructureTest : public ::testing::Test
{
protected:
    void writeSimpleStructureToByteArray(zserio::BitStreamWriter& writer, uint8_t numberA, uint8_t numberB,
            uint8_t numberC)
    {
        writer.writeBits(numberA, 3);
        writer.writeBits(numberB, 8);
        writer.writeBits(numberC, 7);
    }

    static const size_t SIMPLE_STRUCTURE_BIT_SIZE = 18;

    zserio::BitBuffer bitBuffer = zserio::BitBuffer(1024 * 8);
};

TEST_F(SimpleStructureTest, emptyConstructor)
{
    {
        SimpleStructure simpleStructure;
        ASSERT_EQ(0, simpleStructure.getNumberA());
        ASSERT_EQ(0, simpleStructure.getNumberB());
        ASSERT_EQ(0, simpleStructure.getNumberC());
    }
    {
        SimpleStructure simpleStructure = {};
        ASSERT_EQ(0, simpleStructure.getNumberA());
        ASSERT_EQ(0, simpleStructure.getNumberB());
        ASSERT_EQ(0, simpleStructure.getNumberC());
    }
}

TEST_F(SimpleStructureTest, fieldConstructor)
{
    {
        const uint8_t numberA = 0x07;
        const uint8_t numberB = 0xFF;
        const uint8_t numberC = 0x7F;
        SimpleStructure simpleStructure(numberA, numberB, numberC);
        ASSERT_EQ(numberA, simpleStructure.getNumberA());
        ASSERT_EQ(numberB, simpleStructure.getNumberB());
        ASSERT_EQ(numberC, simpleStructure.getNumberC());
    }
    {
        SimpleStructure simpleStructure({}, {}, {});
        ASSERT_EQ(0, simpleStructure.getNumberA());
        ASSERT_EQ(0, simpleStructure.getNumberB());
        ASSERT_EQ(0, simpleStructure.getNumberC());
    }
}

TEST_F(SimpleStructureTest, bitStreamReaderConstructor)
{
    const uint8_t numberA = 0x07;
    const uint8_t numberB = 0xFF;
    const uint8_t numberC = 0x7F;
    zserio::BitStreamWriter writer(bitBuffer);
    writeSimpleStructureToByteArray(writer, numberA, numberB, numberC);

    zserio::BitStreamReader reader(writer.getWriteBuffer(), writer.getBitPosition(), zserio::BitsTag());
    SimpleStructure simpleStructure(reader);
    ASSERT_EQ(numberA, simpleStructure.getNumberA());
    ASSERT_EQ(numberB, simpleStructure.getNumberB());
    ASSERT_EQ(numberC, simpleStructure.getNumberC());
}

TEST_F(SimpleStructureTest, copyConstructor)
{
    const uint8_t numberA = 0x07;
    const uint8_t numberB = 0xFF;
    const uint8_t numberC = 0x7F;
    SimpleStructure simpleStructure(numberA, numberB, numberC);
    SimpleStructure simpleStructureCopy(simpleStructure);
    ASSERT_EQ(numberA, simpleStructureCopy.getNumberA());
    ASSERT_EQ(numberB, simpleStructureCopy.getNumberB());
    ASSERT_EQ(numberC, simpleStructureCopy.getNumberC());
}

TEST_F(SimpleStructureTest, assignmentOperator)
{
    const uint8_t numberA = 0x07;
    const uint8_t numberB = 0xFF;
    const uint8_t numberC = 0x7F;
    SimpleStructure simpleStructure(numberA, numberB, numberC);
    SimpleStructure simpleStructureCopy;
    simpleStructureCopy = simpleStructure;
    ASSERT_EQ(numberA, simpleStructureCopy.getNumberA());
    ASSERT_EQ(numberB, simpleStructureCopy.getNumberB());
    ASSERT_EQ(numberC, simpleStructureCopy.getNumberC());
}

TEST_F(SimpleStructureTest, moveConstructor)
{
    const uint8_t numberA = 0x07;
    const uint8_t numberB = 0xFF;
    const uint8_t numberC = 0x7F;
    SimpleStructure simpleStructure(numberA, numberB, numberC);
    SimpleStructure simpleStructureMoved(std::move(simpleStructure));
    ASSERT_EQ(numberA, simpleStructureMoved.getNumberA());
    ASSERT_EQ(numberB, simpleStructureMoved.getNumberB());
    ASSERT_EQ(numberC, simpleStructureMoved.getNumberC());
}

TEST_F(SimpleStructureTest, moveAssignmentOperator)
{
    const uint8_t numberA = 0x07;
    const uint8_t numberB = 0xFF;
    const uint8_t numberC = 0x7F;
    SimpleStructure simpleStructure(numberA, numberB, numberC);
    SimpleStructure simpleStructureMoved;
    simpleStructureMoved = std::move(simpleStructure);
    ASSERT_EQ(numberA, simpleStructureMoved.getNumberA());
    ASSERT_EQ(numberB, simpleStructureMoved.getNumberB());
    ASSERT_EQ(numberC, simpleStructureMoved.getNumberC());
}

TEST_F(SimpleStructureTest, propagateAllocatorCopyConstructor)
{
    const uint8_t numberA = 0x07;
    const uint8_t numberB = 0xFF;
    const uint8_t numberC = 0x7F;
    SimpleStructure simpleStructure(numberA, numberB, numberC);
    SimpleStructure simpleStructureCopy(zserio::PropagateAllocator, simpleStructure,
            SimpleStructure::allocator_type());
    ASSERT_EQ(numberA, simpleStructureCopy.getNumberA());
    ASSERT_EQ(numberB, simpleStructureCopy.getNumberB());
    ASSERT_EQ(numberC, simpleStructureCopy.getNumberC());
}

TEST_F(SimpleStructureTest, getSetNumberA)
{
    SimpleStructure simpleStructure;
    const uint8_t numberA = 0x02;
    simpleStructure.setNumberA(numberA);
    ASSERT_EQ(numberA, simpleStructure.getNumberA());
}

TEST_F(SimpleStructureTest, getSetNumberB)
{
    SimpleStructure simpleStructure;
    const uint8_t numberB = 0x23;
    simpleStructure.setNumberB(numberB);
    ASSERT_EQ(numberB, simpleStructure.getNumberB());
}

TEST_F(SimpleStructureTest, getSetNumberC)
{
    SimpleStructure simpleStructure;
    const uint8_t numberC = 0x11;
    simpleStructure.setNumberC(numberC);
    ASSERT_EQ(numberC, simpleStructure.getNumberC());
}

TEST_F(SimpleStructureTest, bitSizeOf)
{
    SimpleStructure simpleStructure;
    const uint8_t numberA = 0x00;
    const uint8_t numberB = 0x01;
    const uint8_t numberC = 0x02;
    simpleStructure.setNumberA(numberA);
    simpleStructure.setNumberB(numberB);
    simpleStructure.setNumberC(numberC);
    const size_t expectedBitSize = SIMPLE_STRUCTURE_BIT_SIZE;
    ASSERT_EQ(expectedBitSize, simpleStructure.bitSizeOf());
}

TEST_F(SimpleStructureTest, initializeOffsets)
{
    SimpleStructure simpleStructure;
    const uint8_t numberA = 0x05;
    const uint8_t numberB = 0x10;
    const uint8_t numberC = 0x44;
    simpleStructure.setNumberA(numberA);
    simpleStructure.setNumberB(numberB);
    simpleStructure.setNumberC(numberC);
    const size_t bitPosition = 1;
    const size_t expectedBitSize = bitPosition + SIMPLE_STRUCTURE_BIT_SIZE;
    ASSERT_EQ(expectedBitSize, simpleStructure.initializeOffsets(bitPosition));
}

TEST_F(SimpleStructureTest, operatorEquality)
{
    SimpleStructure simpleStructure1;
    SimpleStructure simpleStructure2;

    const uint8_t numberA = 0x03;
    const uint8_t numberB = 0xDE;
    const uint8_t numberC = 0x55;
    simpleStructure1.setNumberA(numberA);
    simpleStructure1.setNumberB(numberB);
    simpleStructure1.setNumberC(numberC);
    simpleStructure2.setNumberA(numberA);
    simpleStructure2.setNumberB(numberB + 1);
    simpleStructure2.setNumberC(numberC);
    ASSERT_FALSE(simpleStructure1 == simpleStructure2);

    simpleStructure2.setNumberB(numberB);
    ASSERT_TRUE(simpleStructure1 == simpleStructure2);
}

TEST_F(SimpleStructureTest, operatorLessThan)
{
    SimpleStructure simpleStructure1;
    SimpleStructure simpleStructure2;

    ASSERT_FALSE(simpleStructure1 < simpleStructure2);
    ASSERT_FALSE(simpleStructure2 < simpleStructure1);

    simpleStructure1.setNumberA(1);
    simpleStructure2.setNumberA(1);
    simpleStructure1.setNumberB(1);
    simpleStructure2.setNumberB(1);
    simpleStructure1.setNumberC(1);
    simpleStructure2.setNumberC(2);
    ASSERT_TRUE(simpleStructure1 < simpleStructure2);
    ASSERT_FALSE(simpleStructure2 < simpleStructure1);

    simpleStructure1.setNumberB(2);
    ASSERT_FALSE(simpleStructure1 < simpleStructure2);
    ASSERT_TRUE(simpleStructure2 < simpleStructure1);
}

TEST_F(SimpleStructureTest, hashCode)
{
    SimpleStructure simpleStructure1;
    SimpleStructure simpleStructure2;

    const uint8_t numberA = 0x04;
    const uint8_t numberB = 0xCD;
    const uint8_t numberC = 0x57;
    simpleStructure1.setNumberA(numberA);
    simpleStructure1.setNumberB(numberB);
    simpleStructure1.setNumberC(numberC);
    simpleStructure2.setNumberA(numberA);
    simpleStructure2.setNumberB(numberB + 1);
    simpleStructure2.setNumberC(numberC);
    ASSERT_NE(simpleStructure1.hashCode(), simpleStructure2.hashCode());

    // use hardcoded values to check that the hash code is stable
    ASSERT_EQ(1178167, simpleStructure1.hashCode());
    ASSERT_EQ(1178204, simpleStructure2.hashCode());

    simpleStructure2.setNumberB(numberB);
    ASSERT_EQ(simpleStructure1.hashCode(), simpleStructure2.hashCode());
}

TEST_F(SimpleStructureTest, write)
{
    SimpleStructure simpleStructure;
    const uint8_t numberA = 0x07;
    const uint8_t numberB = 0x22;
    const uint8_t numberC = 0x33;
    simpleStructure.setNumberA(numberA);
    simpleStructure.setNumberB(numberB);
    simpleStructure.setNumberC(numberC);

    zserio::BitStreamWriter writer(bitBuffer);
    simpleStructure.write(writer);

    zserio::BitStreamReader reader(writer.getWriteBuffer(), writer.getBitPosition(), zserio::BitsTag());
    SimpleStructure readSimpleStructure(reader);
    ASSERT_EQ(numberA, readSimpleStructure.getNumberA());
    ASSERT_EQ(numberB, readSimpleStructure.getNumberB());
    ASSERT_EQ(numberC, readSimpleStructure.getNumberC());
    ASSERT_TRUE(simpleStructure == readSimpleStructure);
}

} // namespace simple_structure
} // namespace structure_types
