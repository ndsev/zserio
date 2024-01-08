#include "constraints/structure_bitmask_constraints/Availability.h"
#include "constraints/structure_bitmask_constraints/StructureBitmaskConstraints.h"
#include "gtest/gtest.h"
#include "zserio/BitStreamReader.h"
#include "zserio/BitStreamWriter.h"
#include "zserio/CppRuntimeException.h"

namespace constraints
{
namespace structure_bitmask_constraints
{

class StructureBitmaskConstraintsTest : public ::testing::Test
{
protected:
    void writeStructureBitmaskConstraintsToByteArray(
            zserio::BitStreamWriter& writer, Availability mask, uint8_t x, uint8_t y, uint8_t z)
    {
        writer.writeBits(mask.getValue(), 3);
        writer.writeBits(x, 8);
        writer.writeBits(y, 8);
        writer.writeBits(z, 8);
    }

    zserio::BitBuffer bitBuffer = zserio::BitBuffer(1024 * 8);
};

TEST_F(StructureBitmaskConstraintsTest, readConstructorCorrectConstraints)
{
    zserio::BitStreamWriter writer(bitBuffer);
    Availability availability =
            Availability::Values::COORD_X | Availability::Values::COORD_Y | Availability::Values::COORD_Z;
    writeStructureBitmaskConstraintsToByteArray(writer, availability, 1, 1, 1);

    zserio::BitStreamReader reader(writer.getWriteBuffer(), writer.getBitPosition(), zserio::BitsTag());
    StructureBitmaskConstraints structureBitmaskConstraints(reader);
    ASSERT_EQ(1, structureBitmaskConstraints.getCoordX());
    ASSERT_EQ(1, structureBitmaskConstraints.getCoordY());
    ASSERT_EQ(1, structureBitmaskConstraints.getCoordZ());
}

TEST_F(StructureBitmaskConstraintsTest, readConstructorWrongCoordZConstraint)
{
    zserio::BitStreamWriter writer(bitBuffer);
    Availability availability = Availability::Values::COORD_X | Availability::Values::COORD_Y;
    writeStructureBitmaskConstraintsToByteArray(writer, availability, 1, 1, 1);

    zserio::BitStreamReader reader(writer.getWriteBuffer(), writer.getBitPosition(), zserio::BitsTag());
    ASSERT_THROW(StructureBitmaskConstraints structureBitmaskConstraints(reader), zserio::CppRuntimeException);
}

TEST_F(StructureBitmaskConstraintsTest, readConstructorWrongCoordYConstraint)
{
    zserio::BitStreamWriter writer(bitBuffer);
    Availability availability = Availability::Values::COORD_X | Availability::Values::COORD_Z;
    writeStructureBitmaskConstraintsToByteArray(writer, availability, 1, 1, 1);

    zserio::BitStreamReader reader(writer.getWriteBuffer(), writer.getBitPosition(), zserio::BitsTag());
    ASSERT_THROW(StructureBitmaskConstraints structureBitmaskConstraints(reader), zserio::CppRuntimeException);
}

TEST_F(StructureBitmaskConstraintsTest, readConstructorWrongCoordXConstraint)
{
    zserio::BitStreamWriter writer(bitBuffer);
    Availability availability = Availability::Values::COORD_Y | Availability::Values::COORD_Z;
    writeStructureBitmaskConstraintsToByteArray(writer, availability, 1, 1, 1);

    zserio::BitStreamReader reader(writer.getWriteBuffer(), writer.getBitPosition(), zserio::BitsTag());
    ASSERT_THROW(StructureBitmaskConstraints structureBitmaskConstraints(reader), zserio::CppRuntimeException);
}

TEST_F(StructureBitmaskConstraintsTest, writeCorrectConstraints)
{
    StructureBitmaskConstraints structureBitmaskConstraints;
    structureBitmaskConstraints.setAvailability(Availability::Values::COORD_X | Availability::Values::COORD_Y);
    structureBitmaskConstraints.setCoordX(1);
    structureBitmaskConstraints.setCoordY(1);
    structureBitmaskConstraints.setCoordZ(0);

    zserio::BitStreamWriter writer(bitBuffer);
    structureBitmaskConstraints.write(writer);

    zserio::BitStreamReader reader(writer.getWriteBuffer(), writer.getBitPosition(), zserio::BitsTag());
    const StructureBitmaskConstraints readStructureBitmaskConstraints(reader);
    ASSERT_EQ(1, readStructureBitmaskConstraints.getCoordX());
    ASSERT_EQ(1, readStructureBitmaskConstraints.getCoordY());
    ASSERT_EQ(0, readStructureBitmaskConstraints.getCoordZ());
    ASSERT_TRUE(structureBitmaskConstraints == readStructureBitmaskConstraints);
}

TEST_F(StructureBitmaskConstraintsTest, writeWrongCoordZConstraint)
{
    StructureBitmaskConstraints structureBitmaskConstraints;
    structureBitmaskConstraints.setAvailability(Availability::Values::COORD_X | Availability::Values::COORD_Y);
    structureBitmaskConstraints.setCoordX(1);
    structureBitmaskConstraints.setCoordY(1);
    structureBitmaskConstraints.setCoordZ(1);

    zserio::BitStreamWriter writer(bitBuffer);
    ASSERT_THROW(structureBitmaskConstraints.write(writer), zserio::CppRuntimeException);
}

TEST_F(StructureBitmaskConstraintsTest, writeWrongCoordYConstraint)
{
    StructureBitmaskConstraints structureBitmaskConstraints;
    structureBitmaskConstraints.setAvailability(Availability::Values::COORD_X | Availability::Values::COORD_Z);
    structureBitmaskConstraints.setCoordX(1);
    structureBitmaskConstraints.setCoordY(1);
    structureBitmaskConstraints.setCoordZ(1);

    zserio::BitStreamWriter writer(bitBuffer);
    ASSERT_THROW(structureBitmaskConstraints.write(writer), zserio::CppRuntimeException);
}

TEST_F(StructureBitmaskConstraintsTest, writeWrongCoordXConstraint)
{
    StructureBitmaskConstraints structureBitmaskConstraints;
    structureBitmaskConstraints.setAvailability(Availability::Values::COORD_Y | Availability::Values::COORD_Z);
    structureBitmaskConstraints.setCoordX(1);
    structureBitmaskConstraints.setCoordY(1);
    structureBitmaskConstraints.setCoordZ(1);

    zserio::BitStreamWriter writer(bitBuffer);
    ASSERT_THROW(structureBitmaskConstraints.write(writer), zserio::CppRuntimeException);
}

} // namespace structure_bitmask_constraints
} // namespace constraints
