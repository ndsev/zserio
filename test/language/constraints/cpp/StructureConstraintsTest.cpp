#include "gtest/gtest.h"

#include "zserio/BitStreamWriter.h"
#include "zserio/BitStreamReader.h"
#include "zserio/CppRuntimeException.h"

#include "constraints/structure_constraints/StructureConstraints.h"
#include "constraints/structure_constraints/BasicColor.h"
#include "constraints/structure_constraints/ExtendedColor.h"

namespace constraints
{
namespace structure_constraints
{

class StructureConstraintsTest : public ::testing::Test
{
protected:
    void writeStructureConstraintsToByteArray(zserio::BitStreamWriter& writer, BasicColor blackColor,
            BasicColor whiteColor, ExtendedColor purpleColor)
    {
        writer.writeBits(zserio::enumToValue(blackColor), 8);
        writer.writeBits(zserio::enumToValue(whiteColor), 8);
        writer.writeBits(zserio::enumToValue(purpleColor), 16);
    }
};

TEST_F(StructureConstraintsTest, readCorrectConstraints)
{
    zserio::BitStreamWriter writer;
    writeStructureConstraintsToByteArray(writer, BasicColor::BLACK, BasicColor::WHITE, ExtendedColor::PURPLE);
    size_t writeBufferByteSize;
    const uint8_t* writeBuffer = writer.getWriteBuffer(writeBufferByteSize);
    zserio::BitStreamReader reader(writeBuffer, writeBufferByteSize);

    StructureConstraints structureConstraints;
    structureConstraints.read(reader);
    ASSERT_EQ(BasicColor::BLACK, structureConstraints.getBlackColor());
    ASSERT_EQ(BasicColor::WHITE, structureConstraints.getWhiteColor());
    ASSERT_EQ(ExtendedColor::PURPLE, structureConstraints.getPurpleColor());
}

TEST_F(StructureConstraintsTest, readWrongBlackConstraint)
{
    zserio::BitStreamWriter writer;
    writeStructureConstraintsToByteArray(writer, BasicColor::RED, BasicColor::WHITE, ExtendedColor::PURPLE);
    size_t writeBufferByteSize;
    const uint8_t* writeBuffer = writer.getWriteBuffer(writeBufferByteSize);
    zserio::BitStreamReader reader(writeBuffer, writeBufferByteSize);

    StructureConstraints structureConstraints;
    ASSERT_THROW(structureConstraints.read(reader), zserio::CppRuntimeException);
}

TEST_F(StructureConstraintsTest, readWrongWhiteConstraint)
{
    zserio::BitStreamWriter writer;
    writeStructureConstraintsToByteArray(writer, BasicColor::BLACK, BasicColor::RED, ExtendedColor::PURPLE);
    size_t writeBufferByteSize;
    const uint8_t* writeBuffer = writer.getWriteBuffer(writeBufferByteSize);
    zserio::BitStreamReader reader(writeBuffer, writeBufferByteSize);

    StructureConstraints structureConstraints;
    ASSERT_THROW(structureConstraints.read(reader), zserio::CppRuntimeException);
}

TEST_F(StructureConstraintsTest, readWrongPurpleConstraint)
{
    zserio::BitStreamWriter writer;
    writeStructureConstraintsToByteArray(writer, BasicColor::BLACK, BasicColor::WHITE, ExtendedColor::LIME);
    size_t writeBufferByteSize;
    const uint8_t* writeBuffer = writer.getWriteBuffer(writeBufferByteSize);
    zserio::BitStreamReader reader(writeBuffer, writeBufferByteSize);

    StructureConstraints structureConstraints;
    ASSERT_THROW(structureConstraints.read(reader), zserio::CppRuntimeException);
}

TEST_F(StructureConstraintsTest, writeCorrectConstraints)
{
    StructureConstraints structureConstraints;
    structureConstraints.setBlackColor(BasicColor::BLACK);
    structureConstraints.setWhiteColor(BasicColor::WHITE);
    structureConstraints.setPurpleColor(ExtendedColor::PURPLE);

    zserio::BitStreamWriter writer;
    structureConstraints.write(writer);

    size_t writeBufferByteSize;
    const uint8_t* writeBuffer = writer.getWriteBuffer(writeBufferByteSize);
    zserio::BitStreamReader reader(writeBuffer, writeBufferByteSize);
    const StructureConstraints readStructureConstraints(reader);
    ASSERT_EQ(BasicColor::BLACK, readStructureConstraints.getBlackColor());
    ASSERT_EQ(BasicColor::WHITE, readStructureConstraints.getWhiteColor());
    ASSERT_EQ(ExtendedColor::PURPLE, readStructureConstraints.getPurpleColor());
    ASSERT_TRUE(structureConstraints == readStructureConstraints);
}

TEST_F(StructureConstraintsTest, writeWrongBlackConstraint)
{
    StructureConstraints structureConstraints;
    structureConstraints.setBlackColor(BasicColor::RED);
    structureConstraints.setWhiteColor(BasicColor::WHITE);
    structureConstraints.setPurpleColor(ExtendedColor::PURPLE);

    zserio::BitStreamWriter writer;
    ASSERT_THROW(structureConstraints.write(writer), zserio::CppRuntimeException);
}

TEST_F(StructureConstraintsTest, writeWrongWhiteConstraint)
{
    StructureConstraints structureConstraints;
    structureConstraints.setBlackColor(BasicColor::BLACK);
    structureConstraints.setWhiteColor(BasicColor::RED);
    structureConstraints.setPurpleColor(ExtendedColor::PURPLE);

    zserio::BitStreamWriter writer;
    ASSERT_THROW(structureConstraints.write(writer), zserio::CppRuntimeException);
}

TEST_F(StructureConstraintsTest, writeWrongPurpleConstraint)
{
    StructureConstraints structureConstraints;
    structureConstraints.setBlackColor(BasicColor::BLACK);
    structureConstraints.setWhiteColor(BasicColor::WHITE);
    structureConstraints.setPurpleColor(ExtendedColor::LIME);

    zserio::BitStreamWriter writer;
    ASSERT_THROW(structureConstraints.write(writer), zserio::CppRuntimeException);
}

} // namespace structure_constraints
} // namespace constraints
