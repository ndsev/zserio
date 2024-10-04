#include "constraints/structure_constraints/BasicColor.h"
#include "constraints/structure_constraints/ExtendedColor.h"
#include "constraints/structure_constraints/StructureConstraints.h"
#include "gtest/gtest.h"
#include "zserio/BitStreamReader.h"
#include "zserio/BitStreamWriter.h"
#include "zserio/CppRuntimeException.h"

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
        writer.writeBool(true);
        writer.writeBits(zserio::enumToValue(whiteColor), 8);
        writer.writeBool(true);
        writer.writeBits(zserio::enumToValue(purpleColor), 16);
    }

    zserio::BitBuffer bitBuffer = zserio::BitBuffer(1024 * 8);
};

TEST_F(StructureConstraintsTest, readConstructorCorrectConstraints)
{
    zserio::BitStreamWriter writer(bitBuffer);
    writeStructureConstraintsToByteArray(writer, BasicColor::BLACK, BasicColor::WHITE, ExtendedColor::PURPLE);

    zserio::BitStreamReader reader(writer.getWriteBuffer(), writer.getBitPosition(), zserio::BitsTag());
    StructureConstraints structureConstraints(reader);
    ASSERT_EQ(BasicColor::BLACK, structureConstraints.getBlackColor());
    ASSERT_EQ(BasicColor::WHITE, structureConstraints.getWhiteColor());
    ASSERT_EQ(ExtendedColor::PURPLE, structureConstraints.getPurpleColor());
}

TEST_F(StructureConstraintsTest, readConstructorWrongBlackConstraint)
{
    zserio::BitStreamWriter writer(bitBuffer);
    writeStructureConstraintsToByteArray(writer, BasicColor::RED, BasicColor::WHITE, ExtendedColor::PURPLE);

    zserio::BitStreamReader reader(writer.getWriteBuffer(), writer.getBitPosition(), zserio::BitsTag());
    ASSERT_THROW(StructureConstraints structureConstraints(reader), zserio::CppRuntimeException);
}

TEST_F(StructureConstraintsTest, readConstructorWrongWhiteConstraint)
{
    zserio::BitStreamWriter writer(bitBuffer);
    writeStructureConstraintsToByteArray(writer, BasicColor::BLACK, BasicColor::RED, ExtendedColor::PURPLE);

    zserio::BitStreamReader reader(writer.getWriteBuffer(), writer.getBitPosition(), zserio::BitsTag());
    ASSERT_THROW(StructureConstraints structureConstraints(reader), zserio::CppRuntimeException);
}

TEST_F(StructureConstraintsTest, readConstructorWrongPurpleConstraint)
{
    zserio::BitStreamWriter writer(bitBuffer);
    writeStructureConstraintsToByteArray(writer, BasicColor::BLACK, BasicColor::WHITE, ExtendedColor::LIME);

    zserio::BitStreamReader reader(writer.getWriteBuffer(), writer.getBitPosition(), zserio::BitsTag());
    ASSERT_THROW(StructureConstraints structureConstraints(reader), zserio::CppRuntimeException);
}

TEST_F(StructureConstraintsTest, writeCorrectConstraints)
{
    StructureConstraints structureConstraints;
    structureConstraints.setBlackColor(BasicColor::BLACK);
    structureConstraints.setWhiteColor(BasicColor::WHITE);
    structureConstraints.setHasPurple(true);
    structureConstraints.setPurpleColor(ExtendedColor::PURPLE);

    zserio::BitStreamWriter writer(bitBuffer);
    structureConstraints.write(writer);

    zserio::BitStreamReader reader(writer.getWriteBuffer(), writer.getBitPosition(), zserio::BitsTag());
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
    structureConstraints.setHasPurple(true);
    structureConstraints.setPurpleColor(ExtendedColor::PURPLE);

    zserio::BitStreamWriter writer(bitBuffer);
    ASSERT_THROW(structureConstraints.write(writer), zserio::CppRuntimeException);
}

TEST_F(StructureConstraintsTest, writeWrongWhiteConstraint)
{
    StructureConstraints structureConstraints;
    structureConstraints.setBlackColor(BasicColor::BLACK);
    structureConstraints.setWhiteColor(BasicColor::RED);
    structureConstraints.setHasPurple(true);
    structureConstraints.setPurpleColor(ExtendedColor::PURPLE);

    zserio::BitStreamWriter writer(bitBuffer);
    ASSERT_THROW(structureConstraints.write(writer), zserio::CppRuntimeException);
}

TEST_F(StructureConstraintsTest, writeWrongPurpleConstraint)
{
    StructureConstraints structureConstraints;
    structureConstraints.setBlackColor(BasicColor::BLACK);
    structureConstraints.setWhiteColor(BasicColor::WHITE);
    structureConstraints.setHasPurple(true);
    structureConstraints.setPurpleColor(ExtendedColor::LIME);

    zserio::BitStreamWriter writer(bitBuffer);
    ASSERT_THROW(structureConstraints.write(writer), zserio::CppRuntimeException);
}

} // namespace structure_constraints
} // namespace constraints
