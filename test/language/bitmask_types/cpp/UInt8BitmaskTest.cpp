#include <string>

#include "gtest/gtest.h"

#include "zserio/BitStreamWriter.h"
#include "zserio/BitStreamReader.h"
#include "zserio/CppRuntimeException.h"

#include "bitmask_types/uint8_bitmask/Permission.h"

namespace bitmask_types
{
namespace uint8_bitmask
{

class Uint8BitmaskTest : public ::testing::Test
{
protected:
    static const size_t PERMISSION_BITSIZEOF;

    static const Permission::underlying_type NONE_VALUE;
    static const Permission::underlying_type READ_VALUE;
    static const Permission::underlying_type WRITE_VALUE;
};

const size_t Uint8BitmaskTest::PERMISSION_BITSIZEOF = 8;

const Permission::underlying_type Uint8BitmaskTest::NONE_VALUE = 0;
const Permission::underlying_type Uint8BitmaskTest::READ_VALUE = 2;
const Permission::underlying_type Uint8BitmaskTest::WRITE_VALUE = 4;

TEST_F(Uint8BitmaskTest, emptyConstructor)
{
    const Permission permission;
    ASSERT_EQ(0, permission.getValue());
}

TEST_F(Uint8BitmaskTest, valuesConstroctor)
{
    const Permission permission(Permission::Values::WRITE);
    ASSERT_EQ(WRITE_VALUE, permission.getValue());
}

TEST_F(Uint8BitmaskTest, underlyingTypeConstructor)
{
    const Permission permission(READ_VALUE);
    ASSERT_TRUE((permission & Permission::Values::READ) == Permission::Values::READ);
}

TEST_F(Uint8BitmaskTest, readConstructor)
{
    zserio::BitStreamWriter writer;
    writer.writeBits(static_cast<uint32_t>(Permission::Values::WRITE), PERMISSION_BITSIZEOF);
    size_t writerBufferByteSize;
    const uint8_t* writerBuffer = writer.getWriteBuffer(writerBufferByteSize);
    zserio::BitStreamReader reader(writerBuffer, writerBufferByteSize);

    Permission permission(reader);
    ASSERT_EQ(WRITE_VALUE, permission.getValue());
}

TEST_F(Uint8BitmaskTest, copyConstructor)
{
    const Permission permission(READ_VALUE);
    const Permission copy(permission);
    ASSERT_EQ(READ_VALUE, copy.getValue());
}

TEST_F(Uint8BitmaskTest, assignmentOperator)
{
    const Permission permission(READ_VALUE);
    Permission copy;
    copy = permission;
    ASSERT_EQ(READ_VALUE, copy.getValue());
}

TEST_F(Uint8BitmaskTest, moveConstructor)
{
    Permission permission(READ_VALUE);
    const Permission moved(std::move(permission));
    ASSERT_EQ(READ_VALUE, moved.getValue());
}

TEST_F(Uint8BitmaskTest, moveAssignmentOperator)
{
    Permission permission(READ_VALUE);
    Permission moved;
    moved = std::move(permission);
    ASSERT_EQ(READ_VALUE, moved.getValue());
}

TEST_F(Uint8BitmaskTest, underlyingTypeCast)
{
    const Permission permission(WRITE_VALUE);
    ASSERT_EQ(WRITE_VALUE, static_cast<Permission::underlying_type>(permission));
}

TEST_F(Uint8BitmaskTest, getValue)
{
    ASSERT_EQ(NONE_VALUE, Permission(Permission::Values::NONE).getValue());
    ASSERT_EQ(READ_VALUE, Permission(Permission::Values::READ).getValue());
    ASSERT_EQ(WRITE_VALUE, Permission(Permission::Values::WRITE).getValue());
}

TEST_F(Uint8BitmaskTest, bitSizeOf)
{
    ASSERT_EQ(PERMISSION_BITSIZEOF, Permission(Permission::Values::NONE).bitSizeOf());
}

TEST_F(Uint8BitmaskTest, initializeOffsets)
{
    const size_t bitPosition = 1;
    Permission permission(Permission::Values::NONE);
    ASSERT_EQ(bitPosition + PERMISSION_BITSIZEOF, permission.initializeOffsets(bitPosition));
}

TEST_F(Uint8BitmaskTest, hashCode)
{
    const Permission readPermission(Permission::Values::READ);
    const Permission writePermission(Permission::Values::WRITE);
    const Permission copyRead(readPermission);
    ASSERT_EQ(readPermission.hashCode(), copyRead.hashCode());
    ASSERT_NE(readPermission.hashCode(), writePermission.hashCode());
    ASSERT_NE(readPermission.hashCode(), Permission(Permission::Values::NONE).hashCode());
    ASSERT_NE(writePermission.hashCode(), Permission(Permission::Values::NONE).hashCode());
}

TEST_F(Uint8BitmaskTest, read)
{
    zserio::BitStreamWriter writer;
    writer.writeBits(static_cast<uint32_t>(Permission::Values::READ), PERMISSION_BITSIZEOF);
    size_t writerBufferByteSize;
    const uint8_t* writerBuffer = writer.getWriteBuffer(writerBufferByteSize);
    zserio::BitStreamReader reader(writerBuffer, writerBufferByteSize);

    Permission permission;
    permission.read(reader);
    ASSERT_EQ(READ_VALUE, permission.getValue());
}

TEST_F(Uint8BitmaskTest, write)
{
    const Permission permission(Permission::Values::READ);
    zserio::BitStreamWriter writer;
    permission.write(writer);

    size_t writerBufferByteSize;
    const uint8_t* writerBuffer = writer.getWriteBuffer(writerBufferByteSize);
    zserio::BitStreamReader reader(writerBuffer, writerBufferByteSize);
    ASSERT_EQ(READ_VALUE, reader.readBits(PERMISSION_BITSIZEOF));
}

TEST_F(Uint8BitmaskTest, toString)
{
    ASSERT_EQ(std::string("0[NONE]"), Permission(Permission::Values::NONE).toString());
    ASSERT_EQ(std::string("2[READ]"), Permission(Permission::Values::READ).toString());
    ASSERT_EQ(std::string("4[WRITE]"), Permission(Permission::Values::WRITE).toString());
    ASSERT_EQ(std::string("6[READ | WRITE]"),
            (Permission::Values::READ | Permission::Values::WRITE).toString());
    ASSERT_EQ(std::string("7[READ | WRITE | CREATE]"), Permission(7).toString());
    ASSERT_EQ(std::string("255[READ | WRITE | CREATE]"), Permission(255).toString());
}

TEST_F(Uint8BitmaskTest, operatorEquality)
{
    ASSERT_TRUE(Permission::Values::READ == Permission::Values::READ);
    ASSERT_FALSE(Permission::Values::READ == Permission::Values::WRITE);
    ASSERT_TRUE(Permission::Values::WRITE == Permission::Values::WRITE);

    const Permission read(Permission::Values::READ);
    ASSERT_TRUE(read == Permission::Values::READ);
    ASSERT_TRUE(Permission::Values::READ == read);
    ASSERT_FALSE(read == Permission::Values::WRITE);
    ASSERT_FALSE(Permission::Values::WRITE == read);

    const Permission write(Permission::Values::WRITE);
    ASSERT_TRUE(write == Permission::Values::WRITE);
    ASSERT_TRUE(Permission::Values::WRITE == write);
    ASSERT_FALSE(write == Permission::Values::READ);
    ASSERT_FALSE(Permission::Values::READ == write);

    ASSERT_TRUE(read == read);
    ASSERT_TRUE(read == Permission(read)); // copy
    ASSERT_TRUE(write == write);
    ASSERT_TRUE(write == Permission(write)); // copy

    ASSERT_FALSE(read == write);
}

TEST_F(Uint8BitmaskTest, operatorNonequality)
{
    ASSERT_FALSE(Permission::Values::READ != Permission::Values::READ);
    ASSERT_TRUE(Permission::Values::READ != Permission::Values::WRITE);
    ASSERT_FALSE(Permission::Values::WRITE != Permission::Values::WRITE);

    const Permission read(Permission::Values::READ);
    ASSERT_FALSE(read != Permission::Values::READ);
    ASSERT_FALSE(Permission::Values::READ != read);
    ASSERT_TRUE(read != Permission::Values::WRITE);
    ASSERT_TRUE(Permission::Values::WRITE != read);

    const Permission write(Permission::Values::WRITE);
    ASSERT_FALSE(write != Permission::Values::WRITE);
    ASSERT_FALSE(Permission::Values::WRITE != write);
    ASSERT_TRUE(write != Permission::Values::READ);
    ASSERT_TRUE(Permission::Values::READ != write);

    ASSERT_FALSE(read != read);
    ASSERT_FALSE(read != Permission(read)); // copy
    ASSERT_FALSE(write != write);
    ASSERT_FALSE(write != Permission(write)); // copy

    ASSERT_TRUE(read != write);
}

TEST_F(Uint8BitmaskTest, operatorBitwiseOr)
{
    const Permission read(Permission::Values::READ);
    const Permission write(Permission::Values::WRITE);

    ASSERT_EQ(read | write, Permission::Values::READ | Permission::Values::WRITE);
    ASSERT_EQ(read | Permission::Values::WRITE, Permission::Values::READ | write);
    ASSERT_EQ(read, read | Permission::Values::NONE);
    ASSERT_EQ(write, Permission::Values::NONE | write);

    ASSERT_EQ(READ_VALUE | WRITE_VALUE, (read | write).getValue());
}

TEST_F(Uint8BitmaskTest, operatorBitwiseAnd)
{
    const Permission read(Permission::Values::READ);
    const Permission write(Permission::Values::WRITE);
    const Permission readwrite(Permission::Values::READ | Permission::Values::WRITE);

    ASSERT_EQ(Permission::Values::READ, readwrite & read);
    ASSERT_EQ(Permission::Values::WRITE, readwrite & write);
    ASSERT_EQ(read, readwrite & Permission::Values::READ);
    ASSERT_EQ(read, Permission::Values::READ & readwrite);
    ASSERT_EQ(write, Permission::Values::WRITE & readwrite);
    ASSERT_EQ(readwrite & Permission::Values::NONE, Permission::Values::NONE);

    ASSERT_EQ(read, read & read & read & read);
}

TEST_F(Uint8BitmaskTest, operatorBitwiseXor)
{
    const Permission read(Permission::Values::READ);
    const Permission write(Permission::Values::WRITE);

    ASSERT_EQ(read ^ write, Permission::Values::READ ^ Permission::Values::WRITE);
    ASSERT_EQ(Permission::Values::READ ^ write, read ^ Permission::Values::WRITE);
    ASSERT_EQ((read ^ write).getValue(), READ_VALUE ^ WRITE_VALUE);
    ASSERT_EQ(read, (read ^ write) & read);
    ASSERT_EQ(write, (read ^ write) & write);
    ASSERT_EQ(Permission::Values::NONE, read ^ read);
    ASSERT_EQ(Permission::Values::NONE, read ^ Permission::Values::READ);
}

TEST_F(Uint8BitmaskTest, operatorBitwiseNot)
{
    const Permission read(Permission::Values::READ);

    ASSERT_EQ(Permission::Values::WRITE, ~read & Permission::Values::WRITE);
    ASSERT_EQ(Permission::Values::NONE, ~read & Permission::Values::READ);
    ASSERT_EQ(Permission::Values::WRITE, ~Permission::Values::NONE & Permission::Values::WRITE);
    ASSERT_EQ(Permission::Values::READ, ~Permission::Values::NONE & Permission::Values::READ);
    ASSERT_EQ(Permission::Values::READ | Permission::Values::WRITE,
            ~Permission::Values::NONE & (Permission::Values::READ | Permission::Values::WRITE));
}

TEST_F(Uint8BitmaskTest, operatorBitwiseOrAssignment)
{
    Permission permission;
    permission |= Permission::Values::READ;
    ASSERT_EQ(Permission::Values::READ, permission);

    permission |= Permission::Values::NONE;
    ASSERT_EQ(Permission::Values::READ, permission);

    const Permission write(Permission::Values::WRITE);
    permission |= write;
    ASSERT_EQ(Permission::Values::READ | Permission::Values::WRITE, permission);
}

TEST_F(Uint8BitmaskTest, operatorBitwiseAndAssignment)
{
    Permission permission(Permission::Values::READ | Permission::Values::WRITE);
    permission &= Permission::Values::READ;
    ASSERT_EQ(Permission::Values::READ, permission);

    permission |= Permission::Values::WRITE;
    permission &= Permission::Values::WRITE;
    ASSERT_EQ(Permission::Values::WRITE, permission);

    const Permission write(Permission::Values::WRITE);
    permission |= Permission::Values::READ;
    permission &= write;
    ASSERT_EQ(Permission::Values::WRITE, permission);

    permission &= Permission::Values::NONE;
    ASSERT_EQ(Permission::Values::NONE, permission);
}

TEST_F(Uint8BitmaskTest, operatorBitwiseXorAssignment)
{
    Permission permission;
    permission ^= Permission::Values::READ;
    ASSERT_EQ(Permission::Values::READ, permission);

    permission ^= Permission::Values::NONE;
    ASSERT_EQ(Permission::Values::READ, permission);

    permission ^= Permission::Values::WRITE;
    ASSERT_EQ(Permission::Values::READ | Permission::Values::WRITE, permission);

    permission ^= Permission::Values::WRITE;
    ASSERT_EQ(Permission::Values::READ, permission);

    permission ^= Permission::Values::READ;
    ASSERT_EQ(Permission::Values::NONE, permission);
}

} // namespace uint8_bitmask
} // namespace bitmask_types
