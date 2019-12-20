#include "gtest/gtest.h"

#include "zserio/BitStreamWriter.h"
#include "zserio/BitStreamReader.h"
#include "zserio/CppRuntimeException.h"

#include "constraints/structure_bitmask_constraints/StructureBitmaskConstraints.h"
#include "constraints/structure_bitmask_constraints/Availability.h"

namespace constraints
{
namespace structure_bitmask_constraints
{

class StructureBitmaskConstraintsTest : public ::testing::Test
{
protected:
    void writeStructureBitmaskConstraintsToByteArray(zserio::BitStreamWriter& writer, Availability mask,
            uint8_t x, uint8_t y, uint8_t z)
    {
        writer.writeBits(mask.getValue(), 3);
        writer.writeBits(x, 8);
        writer.writeBits(y, 8);
        writer.writeBits(z, 8);
    }
};

TEST_F(StructureBitmaskConstraintsTest, readCorrectConstraints)
{
    zserio::BitStreamWriter writer;
    Availability availability =
            Availability::Values::COORD_X | Availability::Values::COORD_Y | Availability::Values::COORD_Z;
    writeStructureBitmaskConstraintsToByteArray(writer, availability, 1, 1, 1);
    size_t writeBufferByteSize;
    const uint8_t* writeBuffer = writer.getWriteBuffer(writeBufferByteSize);
    zserio::BitStreamReader reader(writeBuffer, writeBufferByteSize);

    StructureBitmaskConstraints structureBitmaskConstraints;
    structureBitmaskConstraints.read(reader);
    ASSERT_EQ(1, structureBitmaskConstraints.getCoordX());
    ASSERT_EQ(1, structureBitmaskConstraints.getCoordY());
    ASSERT_EQ(1, structureBitmaskConstraints.getCoordZ());
}

TEST_F(StructureBitmaskConstraintsTest, readWrongCoordZConstraint)
{
    zserio::BitStreamWriter writer;
    Availability availability = Availability::Values::COORD_X | Availability::Values::COORD_Y;
    writeStructureBitmaskConstraintsToByteArray(writer, availability, 1, 1, 1);
    size_t writeBufferByteSize;
    const uint8_t* writeBuffer = writer.getWriteBuffer(writeBufferByteSize);
    zserio::BitStreamReader reader(writeBuffer, writeBufferByteSize);

    StructureBitmaskConstraints structureBitmaskConstraints;
    ASSERT_THROW(structureBitmaskConstraints.read(reader), zserio::CppRuntimeException);
}

TEST_F(StructureBitmaskConstraintsTest, readWrongCoordYConstraint)
{
    zserio::BitStreamWriter writer;
    Availability availability = Availability::Values::COORD_X | Availability::Values::COORD_Z;
    writeStructureBitmaskConstraintsToByteArray(writer, availability, 1, 1, 1);
    size_t writeBufferByteSize;
    const uint8_t* writeBuffer = writer.getWriteBuffer(writeBufferByteSize);
    zserio::BitStreamReader reader(writeBuffer, writeBufferByteSize);

    StructureBitmaskConstraints structureBitmaskConstraints;
    ASSERT_THROW(structureBitmaskConstraints.read(reader), zserio::CppRuntimeException);
}

TEST_F(StructureBitmaskConstraintsTest, readWrongCoordXConstraint)
{
    zserio::BitStreamWriter writer;
    Availability availability = Availability::Values::COORD_Y | Availability::Values::COORD_Z;
    writeStructureBitmaskConstraintsToByteArray(writer, availability, 1, 1, 1);
    size_t writeBufferByteSize;
    const uint8_t* writeBuffer = writer.getWriteBuffer(writeBufferByteSize);
    zserio::BitStreamReader reader(writeBuffer, writeBufferByteSize);

    StructureBitmaskConstraints structureBitmaskConstraints;
    ASSERT_THROW(structureBitmaskConstraints.read(reader), zserio::CppRuntimeException);
}

TEST_F(StructureBitmaskConstraintsTest, writeCorrectConstraints)
{
    StructureBitmaskConstraints structureBitmaskConstraints;
    structureBitmaskConstraints.setAvailability(Availability::Values::COORD_X | Availability::Values::COORD_Y);
    structureBitmaskConstraints.setCoordX(1);
    structureBitmaskConstraints.setCoordY(1);
    structureBitmaskConstraints.setCoordZ(0);

    zserio::BitStreamWriter writer;
    structureBitmaskConstraints.write(writer);

    size_t writeBufferByteSize;
    const uint8_t* writeBuffer = writer.getWriteBuffer(writeBufferByteSize);
    zserio::BitStreamReader reader(writeBuffer, writeBufferByteSize);
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

    zserio::BitStreamWriter writer;
    ASSERT_THROW(structureBitmaskConstraints.write(writer), zserio::CppRuntimeException);
}

TEST_F(StructureBitmaskConstraintsTest, writeWrongCoordYConstraint)
{
    StructureBitmaskConstraints structureBitmaskConstraints;
    structureBitmaskConstraints.setAvailability(Availability::Values::COORD_X | Availability::Values::COORD_Z);
    structureBitmaskConstraints.setCoordX(1);
    structureBitmaskConstraints.setCoordY(1);
    structureBitmaskConstraints.setCoordZ(1);

    zserio::BitStreamWriter writer;
    ASSERT_THROW(structureBitmaskConstraints.write(writer), zserio::CppRuntimeException);
}

TEST_F(StructureBitmaskConstraintsTest, writeWrongCoordXConstraint)
{
    StructureBitmaskConstraints structureBitmaskConstraints;
    structureBitmaskConstraints.setAvailability(Availability::Values::COORD_Y | Availability::Values::COORD_Z);
    structureBitmaskConstraints.setCoordX(1);
    structureBitmaskConstraints.setCoordY(1);
    structureBitmaskConstraints.setCoordZ(1);

    zserio::BitStreamWriter writer;
    ASSERT_THROW(structureBitmaskConstraints.write(writer), zserio::CppRuntimeException);
}

} // namespace structure_bitmask_constraints
} // namespace constraints
