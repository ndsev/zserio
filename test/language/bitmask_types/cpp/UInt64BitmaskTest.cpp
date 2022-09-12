#include <string>

#include "gtest/gtest.h"

#include "zserio/BitStreamWriter.h"
#include "zserio/BitStreamReader.h"
#include "zserio/CppRuntimeException.h"

#include "bitmask_types/uint64_bitmask/Permission.h"

namespace bitmask_types
{
namespace uint64_bitmask
{

class Uint64BitmaskTest : public ::testing::Test
{
protected:
    static const size_t PERMISSION_BITSIZEOF;

    static const Permission::underlying_type NONE_PERMISSION_VALUE;
    static const Permission::underlying_type READ_PERMISSION_VALUE;
    static const Permission::underlying_type WRITE_PERMISSION_VALUE;

    zserio::BitBuffer bitBuffer = zserio::BitBuffer(1024 * 8);
};

const size_t Uint64BitmaskTest::PERMISSION_BITSIZEOF = 64;

const Permission::underlying_type Uint64BitmaskTest::NONE_PERMISSION_VALUE = 0;
const Permission::underlying_type Uint64BitmaskTest::READ_PERMISSION_VALUE = 2;
const Permission::underlying_type Uint64BitmaskTest::WRITE_PERMISSION_VALUE = 4;

TEST_F(Uint64BitmaskTest, emptyConstructor)
{
    const Permission permission;
    ASSERT_EQ(0, permission.getValue());
}

TEST_F(Uint64BitmaskTest, valuesConstroctor)
{
    const Permission permission(Permission::Values::write_permission);
    ASSERT_EQ(WRITE_PERMISSION_VALUE, permission.getValue());
}

TEST_F(Uint64BitmaskTest, underlyingTypeConstructor)
{
    const Permission permission(READ_PERMISSION_VALUE);
    ASSERT_TRUE((permission & Permission::Values::READ_PERMISSION) ==
            Permission::Values::READ_PERMISSION);
}

TEST_F(Uint64BitmaskTest, readConstructor)
{
    zserio::BitStreamWriter writer(bitBuffer);
    writer.writeBits64(static_cast<uint64_t>(Permission::Values::write_permission), PERMISSION_BITSIZEOF);

    zserio::BitStreamReader reader(writer.getWriteBuffer(), writer.getBitPosition(), zserio::BitsTag());
    Permission permission(reader);
    ASSERT_EQ(WRITE_PERMISSION_VALUE, permission.getValue());
}

TEST_F(Uint64BitmaskTest, copyConstructor)
{
    const Permission permission(READ_PERMISSION_VALUE);
    const Permission copy(permission);
    ASSERT_EQ(READ_PERMISSION_VALUE, copy.getValue());
}

TEST_F(Uint64BitmaskTest, assignmentOperator)
{
    const Permission permission(READ_PERMISSION_VALUE);
    Permission copy;
    copy = permission;
    ASSERT_EQ(READ_PERMISSION_VALUE, copy.getValue());
}

TEST_F(Uint64BitmaskTest, moveConstructor)
{
    Permission permission(READ_PERMISSION_VALUE);
    const Permission moved(std::move(permission));
    ASSERT_EQ(READ_PERMISSION_VALUE, moved.getValue());
}

TEST_F(Uint64BitmaskTest, moveAssignmentOperator)
{
    Permission permission(READ_PERMISSION_VALUE);
    Permission moved;
    moved = std::move(permission);
    ASSERT_EQ(READ_PERMISSION_VALUE, moved.getValue());
}

TEST_F(Uint64BitmaskTest, underlyingTypeCast)
{
    const Permission permission(WRITE_PERMISSION_VALUE);
    ASSERT_EQ(WRITE_PERMISSION_VALUE, static_cast<Permission::underlying_type>(permission));
}

TEST_F(Uint64BitmaskTest, getValue)
{
    ASSERT_EQ(NONE_PERMISSION_VALUE, Permission(Permission::Values::nonePermission).getValue());
    ASSERT_EQ(READ_PERMISSION_VALUE, Permission(Permission::Values::READ_PERMISSION).getValue());
    ASSERT_EQ(WRITE_PERMISSION_VALUE, Permission(Permission::Values::write_permission).getValue());
}

TEST_F(Uint64BitmaskTest, bitSizeOf)
{
    ASSERT_EQ(PERMISSION_BITSIZEOF, Permission(Permission::Values::nonePermission).bitSizeOf());
}

TEST_F(Uint64BitmaskTest, initializeOffsets)
{
    const size_t bitPosition = 1;
    Permission permission(Permission::Values::nonePermission);
    ASSERT_EQ(bitPosition + PERMISSION_BITSIZEOF, permission.initializeOffsets(bitPosition));
}

TEST_F(Uint64BitmaskTest, hashCode)
{
    const Permission readPermission(Permission::Values::READ_PERMISSION);
    const Permission writePermission(Permission::Values::write_permission);
    const Permission copyRead(readPermission);
    ASSERT_EQ(readPermission.hashCode(), copyRead.hashCode());
    ASSERT_NE(readPermission.hashCode(), writePermission.hashCode());
    ASSERT_NE(readPermission.hashCode(), Permission(Permission::Values::nonePermission).hashCode());
    ASSERT_NE(writePermission.hashCode(), Permission(Permission::Values::nonePermission).hashCode());

    // use hardcoded values to check that the hash code is stable
    ASSERT_EQ(851, Permission(Permission::Values::nonePermission).hashCode());
    ASSERT_EQ(853, Permission(Permission::Values::READ_PERMISSION).hashCode());
    ASSERT_EQ(855, Permission(Permission::Values::write_permission).hashCode());
    ASSERT_EQ(859, Permission(Permission::Values::CreatePermission).hashCode());
}

TEST_F(Uint64BitmaskTest, write)
{
    const Permission permission(Permission::Values::READ_PERMISSION);
    zserio::BitStreamWriter writer(bitBuffer);
    permission.write(writer);

    zserio::BitStreamReader reader(writer.getWriteBuffer(), writer.getBitPosition(), zserio::BitsTag());
    ASSERT_EQ(READ_PERMISSION_VALUE, reader.readBits64(PERMISSION_BITSIZEOF));
}

TEST_F(Uint64BitmaskTest, toString)
{
    ASSERT_EQ(std::string("0[nonePermission]"),
            Permission(Permission::Values::nonePermission).toString().c_str());
    ASSERT_EQ(std::string("2[READ_PERMISSION]"),
            Permission(Permission::Values::READ_PERMISSION).toString().c_str());
    ASSERT_EQ(std::string("4[write_permission]"),
            Permission(Permission::Values::write_permission).toString().c_str());
    ASSERT_EQ(std::string("6[READ_PERMISSION | write_permission]"),
            (Permission::Values::READ_PERMISSION | Permission::Values::write_permission).toString().c_str());
    ASSERT_EQ(std::string("7[READ_PERMISSION | write_permission]"), Permission(7).toString().c_str());
    ASSERT_EQ(std::string("255[READ_PERMISSION | write_permission | CreatePermission]"),
            Permission(255).toString().c_str());
}

TEST_F(Uint64BitmaskTest, operatorEquality)
{
    ASSERT_TRUE(Permission::Values::READ_PERMISSION == Permission::Values::READ_PERMISSION);
    ASSERT_FALSE(Permission::Values::READ_PERMISSION == Permission::Values::write_permission);
    ASSERT_TRUE(Permission::Values::write_permission == Permission::Values::write_permission);

    const Permission read(Permission::Values::READ_PERMISSION);
    ASSERT_TRUE(read == Permission::Values::READ_PERMISSION);
    ASSERT_TRUE(Permission::Values::READ_PERMISSION == read);
    ASSERT_FALSE(read == Permission::Values::write_permission);
    ASSERT_FALSE(Permission::Values::write_permission == read);

    const Permission write(Permission::Values::write_permission);
    ASSERT_TRUE(write == Permission::Values::write_permission);
    ASSERT_TRUE(Permission::Values::write_permission == write);
    ASSERT_FALSE(write == Permission::Values::READ_PERMISSION);
    ASSERT_FALSE(Permission::Values::READ_PERMISSION == write);

    ASSERT_TRUE(read == read);
    ASSERT_TRUE(read == Permission(read)); // copy
    ASSERT_TRUE(write == write);
    ASSERT_TRUE(write == Permission(write)); // copy

    ASSERT_FALSE(read == write);
}

TEST_F(Uint64BitmaskTest, operatorNonequality)
{
    ASSERT_FALSE(Permission::Values::READ_PERMISSION != Permission::Values::READ_PERMISSION);
    ASSERT_TRUE(Permission::Values::READ_PERMISSION != Permission::Values::write_permission);
    ASSERT_FALSE(Permission::Values::write_permission != Permission::Values::write_permission);

    const Permission read(Permission::Values::READ_PERMISSION);
    ASSERT_FALSE(read != Permission::Values::READ_PERMISSION);
    ASSERT_FALSE(Permission::Values::READ_PERMISSION != read);
    ASSERT_TRUE(read != Permission::Values::write_permission);
    ASSERT_TRUE(Permission::Values::write_permission != read);

    const Permission write(Permission::Values::write_permission);
    ASSERT_FALSE(write != Permission::Values::write_permission);
    ASSERT_FALSE(Permission::Values::write_permission != write);
    ASSERT_TRUE(write != Permission::Values::READ_PERMISSION);
    ASSERT_TRUE(Permission::Values::READ_PERMISSION != write);

    ASSERT_FALSE(read != read);
    ASSERT_FALSE(read != Permission(read)); // copy
    ASSERT_FALSE(write != write);
    ASSERT_FALSE(write != Permission(write)); // copy

    ASSERT_TRUE(read != write);
}

TEST_F(Uint64BitmaskTest, operatorBitwiseOr)
{
    const Permission read(Permission::Values::READ_PERMISSION);
    const Permission write(Permission::Values::write_permission);

    ASSERT_EQ(read | write, Permission::Values::READ_PERMISSION | Permission::Values::write_permission);
    ASSERT_EQ(read | Permission::Values::write_permission, Permission::Values::READ_PERMISSION | write);
    ASSERT_EQ(read, read | Permission::Values::nonePermission);
    ASSERT_EQ(write, Permission::Values::nonePermission | write);

    ASSERT_EQ(READ_PERMISSION_VALUE | WRITE_PERMISSION_VALUE, (read | write).getValue());
}

TEST_F(Uint64BitmaskTest, operatorBitwiseAnd)
{
    const Permission read(Permission::Values::READ_PERMISSION);
    const Permission write(Permission::Values::write_permission);
    const Permission readwrite(Permission::Values::READ_PERMISSION | Permission::Values::write_permission);

    ASSERT_EQ(Permission::Values::READ_PERMISSION, readwrite & read);
    ASSERT_EQ(Permission::Values::write_permission, readwrite & write);
    ASSERT_EQ(read, readwrite & Permission::Values::READ_PERMISSION);
    ASSERT_EQ(read, Permission::Values::READ_PERMISSION & readwrite);
    ASSERT_EQ(write, Permission::Values::write_permission & readwrite);
    ASSERT_EQ(readwrite & Permission::Values::nonePermission, Permission::Values::nonePermission);

    ASSERT_EQ(read, read & read & read & read);
}

TEST_F(Uint64BitmaskTest, operatorBitwiseXor)
{
    const Permission read(Permission::Values::READ_PERMISSION);
    const Permission write(Permission::Values::write_permission);

    ASSERT_EQ(read ^ write, Permission::Values::READ_PERMISSION ^ Permission::Values::write_permission);
    ASSERT_EQ(Permission::Values::READ_PERMISSION ^ write, read ^ Permission::Values::write_permission);
    ASSERT_EQ((read ^ write).getValue(), READ_PERMISSION_VALUE ^ WRITE_PERMISSION_VALUE);
    ASSERT_EQ(read, (read ^ write) & read);
    ASSERT_EQ(write, (read ^ write) & write);
    ASSERT_EQ(Permission::Values::nonePermission, read ^ read);
    ASSERT_EQ(Permission::Values::nonePermission, read ^ Permission::Values::READ_PERMISSION);
}

TEST_F(Uint64BitmaskTest, operatorBitwiseNot)
{
    const Permission read(Permission::Values::READ_PERMISSION);

    ASSERT_EQ(Permission::Values::write_permission, ~read & Permission::Values::write_permission);
    ASSERT_EQ(Permission::Values::nonePermission, ~read & Permission::Values::READ_PERMISSION);
    ASSERT_EQ(Permission::Values::write_permission,
            ~Permission::Values::nonePermission & Permission::Values::write_permission);
    ASSERT_EQ(Permission::Values::READ_PERMISSION,
            ~Permission::Values::nonePermission & Permission::Values::READ_PERMISSION);
    ASSERT_EQ(Permission::Values::READ_PERMISSION | Permission::Values::write_permission,
            ~Permission::Values::nonePermission & (Permission::Values::READ_PERMISSION |
                    Permission::Values::write_permission));
}

TEST_F(Uint64BitmaskTest, operatorBitwiseOrAssignment)
{
    Permission permission;
    permission |= Permission::Values::READ_PERMISSION;
    ASSERT_EQ(Permission::Values::READ_PERMISSION, permission);

    permission |= Permission::Values::nonePermission;
    ASSERT_EQ(Permission::Values::READ_PERMISSION, permission);

    const Permission write(Permission::Values::write_permission);
    permission |= write;
    ASSERT_EQ(Permission::Values::READ_PERMISSION | Permission::Values::write_permission, permission);
}

TEST_F(Uint64BitmaskTest, operatorBitwiseAndAssignment)
{
    Permission permission(Permission::Values::READ_PERMISSION | Permission::Values::write_permission);
    permission &= Permission::Values::READ_PERMISSION;
    ASSERT_EQ(Permission::Values::READ_PERMISSION, permission);

    permission |= Permission::Values::write_permission;
    permission &= Permission::Values::write_permission;
    ASSERT_EQ(Permission::Values::write_permission, permission);

    const Permission write(Permission::Values::write_permission);
    permission |= Permission::Values::READ_PERMISSION;
    permission &= write;
    ASSERT_EQ(Permission::Values::write_permission, permission);

    permission &= Permission::Values::nonePermission;
    ASSERT_EQ(Permission::Values::nonePermission, permission);
}

TEST_F(Uint64BitmaskTest, operatorBitwiseXorAssignment)
{
    Permission permission;
    permission ^= Permission::Values::READ_PERMISSION;
    ASSERT_EQ(Permission::Values::READ_PERMISSION, permission);

    permission ^= Permission::Values::nonePermission;
    ASSERT_EQ(Permission::Values::READ_PERMISSION, permission);

    permission ^= Permission::Values::write_permission;
    ASSERT_EQ(Permission::Values::READ_PERMISSION | Permission::Values::write_permission, permission);

    permission ^= Permission::Values::write_permission;
    ASSERT_EQ(Permission::Values::READ_PERMISSION, permission);

    permission ^= Permission::Values::READ_PERMISSION;
    ASSERT_EQ(Permission::Values::nonePermission, permission);
}

} // namespace uint64_bitmask
} // namespace bitmask_types
