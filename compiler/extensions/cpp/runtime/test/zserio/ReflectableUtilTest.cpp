#include <cmath>

#include "gtest/gtest.h"
#include "test_object/std_allocator/ReflectableUtilBitmask.h"
#include "test_object/std_allocator/ReflectableUtilEnum.h"
#include "test_object/std_allocator/ReflectableUtilUnion.h"
#include "zserio/Reflectable.h"
#include "zserio/ReflectableUtil.h"
#include "zserio/TypeInfo.h"

using test_object::std_allocator::ReflectableUtilBitmask;
using test_object::std_allocator::ReflectableUtilChoice;
using test_object::std_allocator::ReflectableUtilEnum;
using test_object::std_allocator::ReflectableUtilObject;
using test_object::std_allocator::ReflectableUtilUnion;

namespace zserio
{

TEST(ReflectableUtilTest, unequalTypeInfo)
{
    ASSERT_FALSE(ReflectableUtil::equal(ReflectableFactory::getBool(false), ReflectableFactory::getUInt8(13)));
    ASSERT_FALSE(ReflectableUtil::equal(ReflectableFactory::getInt8(0), ReflectableFactory::getInt16(0)));
    ASSERT_FALSE(ReflectableUtil::equal(ReflectableFactory::getInt8(0), ReflectableFactory::getUInt8(0)));
    ASSERT_FALSE(ReflectableUtil::equal(ReflectableFactory::getUInt64(0), ReflectableFactory::getVarSize(0)));
    ASSERT_FALSE(ReflectableUtil::equal(ReflectableFactory::getInt8(0), ReflectableFactory::getFloat32(0.0)));
    ASSERT_FALSE(
            ReflectableUtil::equal(ReflectableFactory::getFloat16(0.0F), ReflectableFactory::getFloat32(0.0F)));
    ASSERT_FALSE(ReflectableUtil::equal(ReflectableFactory::getString(""), ReflectableFactory::getInt8(0)));
    ASSERT_FALSE(ReflectableUtil::equal(
            ReflectableFactory::getBitBuffer(BitBuffer()), ReflectableFactory::getString("")));
}

TEST(ReflectableUtilTest, equalsNullValues)
{
    ASSERT_TRUE(ReflectableUtil::equal(nullptr, nullptr));
    ASSERT_FALSE(ReflectableUtil::equal(ReflectableFactory::getBool(false), nullptr));
    ASSERT_FALSE(ReflectableUtil::equal(nullptr, ReflectableFactory::getInt8(0)));
}

TEST(ReflectableUtilTest, equalBools)
{
    ASSERT_TRUE(ReflectableUtil::equal(ReflectableFactory::getBool(true), ReflectableFactory::getBool(true)));
    ASSERT_FALSE(ReflectableUtil::equal(ReflectableFactory::getBool(false), ReflectableFactory::getBool(true)));
    ASSERT_FALSE(ReflectableUtil::equal(ReflectableFactory::getBool(true), ReflectableFactory::getBool(false)));
}

TEST(ReflectableUtilTest, equalSingedIntegrals)
{
    ASSERT_TRUE(ReflectableUtil::equal(
            ReflectableFactory::getInt8(INT8_MIN), ReflectableFactory::getInt8(INT8_MIN)));
    ASSERT_TRUE(ReflectableUtil::equal(
            ReflectableFactory::getInt8(INT8_MAX), ReflectableFactory::getInt8(INT8_MAX)));
    ASSERT_FALSE(ReflectableUtil::equal(ReflectableFactory::getInt8(-1), ReflectableFactory::getInt8(0)));
    ASSERT_FALSE(ReflectableUtil::equal(
            ReflectableFactory::getInt8(INT8_MIN), ReflectableFactory::getInt8(INT8_MAX)));

    ASSERT_TRUE(ReflectableUtil::equal(
            ReflectableFactory::getInt16(INT16_MIN), ReflectableFactory::getInt16(INT16_MIN)));
    ASSERT_TRUE(ReflectableUtil::equal(
            ReflectableFactory::getInt16(INT16_MAX), ReflectableFactory::getInt16(INT16_MAX)));
    ASSERT_FALSE(ReflectableUtil::equal(ReflectableFactory::getInt16(-1), ReflectableFactory::getInt16(0)));
    ASSERT_FALSE(ReflectableUtil::equal(
            ReflectableFactory::getInt16(INT16_MIN), ReflectableFactory::getInt16(INT16_MAX)));

    ASSERT_TRUE(ReflectableUtil::equal(
            ReflectableFactory::getInt32(INT32_MIN), ReflectableFactory::getInt32(INT32_MIN)));
    ASSERT_TRUE(ReflectableUtil::equal(
            ReflectableFactory::getInt32(INT32_MAX), ReflectableFactory::getInt32(INT32_MAX)));
    ASSERT_FALSE(ReflectableUtil::equal(ReflectableFactory::getInt32(-1), ReflectableFactory::getInt32(0)));
    ASSERT_FALSE(ReflectableUtil::equal(
            ReflectableFactory::getInt32(INT32_MIN), ReflectableFactory::getInt32(INT32_MAX)));

    ASSERT_TRUE(ReflectableUtil::equal(
            ReflectableFactory::getInt64(INT64_MIN), ReflectableFactory::getInt64(INT64_MIN)));
    ASSERT_TRUE(ReflectableUtil::equal(
            ReflectableFactory::getInt64(INT64_MAX), ReflectableFactory::getInt64(INT64_MAX)));
    ASSERT_FALSE(ReflectableUtil::equal(ReflectableFactory::getInt64(-1), ReflectableFactory::getInt64(0)));
    ASSERT_FALSE(ReflectableUtil::equal(
            ReflectableFactory::getInt64(INT64_MIN), ReflectableFactory::getInt64(INT64_MAX)));
}

TEST(ReflectableUtilTest, equalUnsignedIntegrals)
{
    ASSERT_TRUE(ReflectableUtil::equal(ReflectableFactory::getUInt8(0), ReflectableFactory::getUInt8(0)));
    ASSERT_TRUE(ReflectableUtil::equal(
            ReflectableFactory::getUInt8(UINT8_MAX), ReflectableFactory::getUInt8(UINT8_MAX)));
    ASSERT_FALSE(ReflectableUtil::equal(ReflectableFactory::getUInt8(0), ReflectableFactory::getUInt8(1)));
    ASSERT_FALSE(
            ReflectableUtil::equal(ReflectableFactory::getUInt8(0), ReflectableFactory::getUInt8(UINT8_MAX)));

    ASSERT_TRUE(ReflectableUtil::equal(ReflectableFactory::getUInt16(0), ReflectableFactory::getUInt16(0)));
    ASSERT_TRUE(ReflectableUtil::equal(
            ReflectableFactory::getUInt16(UINT16_MAX), ReflectableFactory::getUInt16(UINT16_MAX)));
    ASSERT_FALSE(ReflectableUtil::equal(ReflectableFactory::getUInt16(0), ReflectableFactory::getUInt16(1)));
    ASSERT_FALSE(ReflectableUtil::equal(
            ReflectableFactory::getUInt16(0), ReflectableFactory::getUInt16(UINT16_MAX)));

    ASSERT_TRUE(ReflectableUtil::equal(ReflectableFactory::getUInt32(0), ReflectableFactory::getUInt32(0)));
    ASSERT_TRUE(ReflectableUtil::equal(
            ReflectableFactory::getUInt32(UINT32_MAX), ReflectableFactory::getUInt32(UINT32_MAX)));
    ASSERT_FALSE(ReflectableUtil::equal(ReflectableFactory::getUInt32(0), ReflectableFactory::getUInt32(1)));
    ASSERT_FALSE(ReflectableUtil::equal(
            ReflectableFactory::getUInt32(0), ReflectableFactory::getUInt32(UINT32_MAX)));

    ASSERT_TRUE(ReflectableUtil::equal(ReflectableFactory::getUInt64(0), ReflectableFactory::getUInt64(0)));
    ASSERT_TRUE(ReflectableUtil::equal(
            ReflectableFactory::getUInt64(UINT64_MAX), ReflectableFactory::getUInt64(UINT64_MAX)));
    ASSERT_FALSE(ReflectableUtil::equal(ReflectableFactory::getUInt64(0), ReflectableFactory::getUInt64(1)));
    ASSERT_FALSE(ReflectableUtil::equal(
            ReflectableFactory::getUInt64(0), ReflectableFactory::getUInt64(UINT64_MAX)));
}

TEST(ReflectableUtilTest, equalFloatingPoints)
{
    ASSERT_TRUE(
            ReflectableUtil::equal(ReflectableFactory::getFloat16(0.0F), ReflectableFactory::getFloat16(0.0F)));
    ASSERT_FALSE(ReflectableUtil::equal(
            ReflectableFactory::getFloat16(-1.0F), ReflectableFactory::getFloat16(1.0F)));

    ASSERT_TRUE(
            ReflectableUtil::equal(ReflectableFactory::getFloat32(0.0F), ReflectableFactory::getFloat32(0.0F)));
    ASSERT_FALSE(ReflectableUtil::equal(
            ReflectableFactory::getFloat32(-1.0F), ReflectableFactory::getFloat32(1.0F)));

    ASSERT_TRUE(
            ReflectableUtil::equal(ReflectableFactory::getFloat64(0.0), ReflectableFactory::getFloat64(0.0)));
    ASSERT_FALSE(
            ReflectableUtil::equal(ReflectableFactory::getFloat64(-1.0), ReflectableFactory::getFloat64(1.0)));

    ASSERT_TRUE(ReflectableUtil::equal(ReflectableFactory::getFloat64(std::numeric_limits<double>::epsilon()),
            ReflectableFactory::getFloat64(std::numeric_limits<double>::epsilon())));
    ASSERT_TRUE(ReflectableUtil::equal(ReflectableFactory::getFloat64(std::numeric_limits<double>::min()),
            ReflectableFactory::getFloat64(std::numeric_limits<double>::min())));

    ASSERT_TRUE(ReflectableUtil::equal(
            ReflectableFactory::getFloat64(1.0 + std::numeric_limits<double>::epsilon()),
            ReflectableFactory::getFloat64(1.0)));
    ASSERT_TRUE(ReflectableUtil::equal(
            ReflectableFactory::getFloat64(std::numeric_limits<double>::denorm_min() * 2),
            ReflectableFactory::getFloat64(std::numeric_limits<double>::denorm_min())));

    auto testA = ReflectableFactory::getFloat64(static_cast<double>(NAN));
    auto testB = ReflectableFactory::getFloat64(static_cast<double>(NAN));
    ASSERT_TRUE(ReflectableUtil::equal(testA, testB));
    testA = ReflectableFactory::getFloat64(static_cast<double>(INFINITY));
    testB = ReflectableFactory::getFloat64(static_cast<double>(INFINITY));
    ASSERT_TRUE(ReflectableUtil::equal(testA, testB));
    testA = ReflectableFactory::getFloat64(-static_cast<double>(INFINITY));
    testB = ReflectableFactory::getFloat64(-static_cast<double>(INFINITY));
    ASSERT_TRUE(ReflectableUtil::equal(testA, testB));

    ASSERT_FALSE(ReflectableUtil::equal(ReflectableFactory::getFloat64(0.0),
            ReflectableFactory::getFloat64(static_cast<double>(INFINITY))));
    ASSERT_FALSE(ReflectableUtil::equal(ReflectableFactory::getFloat64(static_cast<double>(INFINITY)),
            ReflectableFactory::getFloat64(0.0)));

    ASSERT_FALSE(ReflectableUtil::equal(
            ReflectableFactory::getFloat64(0.0), ReflectableFactory::getFloat64(static_cast<double>(NAN))));
    ASSERT_FALSE(ReflectableUtil::equal(
            ReflectableFactory::getFloat64(static_cast<double>(NAN)), ReflectableFactory::getFloat64(0.0)));

    ASSERT_FALSE(ReflectableUtil::equal(ReflectableFactory::getFloat64(static_cast<double>(NAN)),
            ReflectableFactory::getFloat64(static_cast<double>(INFINITY))));
    ASSERT_FALSE(ReflectableUtil::equal(ReflectableFactory::getFloat64(static_cast<double>(INFINITY)),
            ReflectableFactory::getFloat64(static_cast<double>(NAN))));

    ASSERT_FALSE(ReflectableUtil::equal(ReflectableFactory::getFloat64(static_cast<double>(INFINITY)),
            ReflectableFactory::getFloat64(-static_cast<double>(INFINITY))));
    ASSERT_FALSE(ReflectableUtil::equal(ReflectableFactory::getFloat64(-static_cast<double>(INFINITY)),
            ReflectableFactory::getFloat64(static_cast<double>(INFINITY))));
}

TEST(ReflectableUtilTest, equalStrings)
{
    ASSERT_TRUE(ReflectableUtil::equal(ReflectableFactory::getString(""), ReflectableFactory::getString("")));
    ASSERT_TRUE(ReflectableUtil::equal(
            ReflectableFactory::getString("test"), ReflectableFactory::getString("test")));
    ASSERT_FALSE(ReflectableUtil::equal(ReflectableFactory::getString(""), ReflectableFactory::getString("a")));
    ASSERT_FALSE(ReflectableUtil::equal(ReflectableFactory::getString("1"), ReflectableFactory::getString("")));
    ASSERT_FALSE(ReflectableUtil::equal(
            ReflectableFactory::getString("test"), ReflectableFactory::getString("test2")));
}

TEST(ReflectableUtilTest, equalBitBuffers)
{
    ASSERT_TRUE(ReflectableUtil::equal(
            ReflectableFactory::getBitBuffer(BitBuffer()), ReflectableFactory::getBitBuffer(BitBuffer())));
    ASSERT_TRUE(ReflectableUtil::equal(ReflectableFactory::getBitBuffer(BitBuffer({0xAB}, 8)),
            ReflectableFactory::getBitBuffer(BitBuffer({0xAB}, 8))));
    ASSERT_FALSE(ReflectableUtil::equal(ReflectableFactory::getBitBuffer(BitBuffer({0xAB}, 8)),
            ReflectableFactory::getBitBuffer(BitBuffer())));
}

TEST(ReflectableUtilTest, equalBytes)
{
    vector<uint8_t> bytesData1{{0xCA, 0xFE}};
    vector<uint8_t> bytesData2{{0xCA, 0xDE}};
    vector<uint8_t> bytesEmpty;

    ASSERT_TRUE(ReflectableUtil::equal(
            ReflectableFactory::getBytes(bytesEmpty), ReflectableFactory::getBytes(bytesEmpty)));
    ASSERT_TRUE(ReflectableUtil::equal(
            ReflectableFactory::getBytes(bytesData1), ReflectableFactory::getBytes(bytesData1)));
    ASSERT_FALSE(ReflectableUtil::equal(
            ReflectableFactory::getBytes(bytesData1), ReflectableFactory::getBytes(bytesEmpty)));
    ASSERT_FALSE(ReflectableUtil::equal(
            ReflectableFactory::getBytes(bytesData1), ReflectableFactory::getBytes(bytesData2)));
}

TEST(ReflectableUtilTest, equalEnums)
{
    const ReflectableUtilEnum oneEnum = ReflectableUtilEnum::ONE;
    const ReflectableUtilEnum twoEnum = ReflectableUtilEnum::TWO;

    ASSERT_TRUE(ReflectableUtil::equal(enumReflectable(oneEnum), enumReflectable(oneEnum)));
    ASSERT_TRUE(ReflectableUtil::equal(enumReflectable(twoEnum), enumReflectable(twoEnum)));
    ASSERT_FALSE(ReflectableUtil::equal(enumReflectable(oneEnum), enumReflectable(twoEnum)));
}

TEST(ReflectableUtilTest, equalBitmasks)
{
    const ReflectableUtilBitmask readBitmask = ReflectableUtilBitmask::Values::READ;
    const ReflectableUtilBitmask writeBitmask = ReflectableUtilBitmask::Values::WRITE;

    ASSERT_TRUE(ReflectableUtil::equal(readBitmask.reflectable(), readBitmask.reflectable()));
    ASSERT_TRUE(ReflectableUtil::equal(writeBitmask.reflectable(), writeBitmask.reflectable()));
    ASSERT_FALSE(ReflectableUtil::equal(writeBitmask.reflectable(), readBitmask.reflectable()));
}

TEST(ReflectableUtilTest, equalCompounds)
{
    ReflectableUtilUnion compound1 = ReflectableUtilUnion();
    compound1.setReflectableUtilEnum(ReflectableUtilEnum::ONE);
    compound1.initializeChildren();

    ReflectableUtilUnion compound2 = ReflectableUtilUnion();
    compound2.setReflectableUtilEnum(ReflectableUtilEnum::TWO); // different enum value
    compound2.initializeChildren();

    ReflectableUtilUnion compound3 = ReflectableUtilUnion();
    compound3.setReflectableUtilBitmask(ReflectableUtilBitmask::Values::READ);
    compound3.initializeChildren();

    ReflectableUtilUnion compound4 = ReflectableUtilUnion();
    compound4.setReflectableUtilObject(ReflectableUtilObject(0, ReflectableUtilChoice()));
    compound4.initializeChildren();

    ReflectableUtilUnion compound5 = ReflectableUtilUnion();
    compound5.setReflectableUtilObject(ReflectableUtilObject(1, ReflectableUtilChoice()));
    compound5.getReflectableUtilObject().getReflectableUtilChoice().setArray(std::vector<uint32_t>());
    compound5.initializeChildren();

    ReflectableUtilUnion compound6 = ReflectableUtilUnion();
    compound6.setReflectableUtilObject(ReflectableUtilObject(1, ReflectableUtilChoice()));
    compound6.getReflectableUtilObject().getReflectableUtilChoice().setArray(std::vector<uint32_t>{{1, 2, 3}});
    compound6.initializeChildren();

    ASSERT_TRUE(ReflectableUtil::equal(compound1.reflectable(), compound1.reflectable()));
    ASSERT_TRUE(ReflectableUtil::equal(compound2.reflectable(), compound2.reflectable()));
    ASSERT_TRUE(ReflectableUtil::equal(compound3.reflectable(), compound3.reflectable()));
    ASSERT_TRUE(ReflectableUtil::equal(compound4.reflectable(), compound4.reflectable()));
    ASSERT_TRUE(ReflectableUtil::equal(compound5.reflectable(), compound5.reflectable()));
    ASSERT_TRUE(ReflectableUtil::equal(compound6.reflectable(), compound6.reflectable()));

    ASSERT_FALSE(ReflectableUtil::equal(compound1.reflectable(), compound2.reflectable()));
    ASSERT_FALSE(ReflectableUtil::equal(compound1.reflectable(), compound3.reflectable()));
    ASSERT_FALSE(ReflectableUtil::equal(compound1.reflectable(), compound4.reflectable()));
    ASSERT_FALSE(ReflectableUtil::equal(compound1.reflectable(), compound5.reflectable()));
    ASSERT_FALSE(ReflectableUtil::equal(compound1.reflectable(), compound6.reflectable()));

    // unequal fields in choice
    ASSERT_FALSE(ReflectableUtil::equal(compound5.reflectable(), compound6.reflectable()));

    // unequal parameters in choice
    ReflectableUtilChoice choice1;
    choice1.initialize(0);
    ReflectableUtilChoice choice2;
    choice2.initialize(3);
    ASSERT_FALSE(ReflectableUtil::equal(choice1.reflectable(), choice2.reflectable()));

    // array and not array
    std::vector<ReflectableUtilUnion> compoundArray(1);
    ASSERT_FALSE(ReflectableUtil::equal(
            compound1.reflectable(), ReflectableFactory::getCompoundArray(compoundArray)));
    ASSERT_FALSE(ReflectableUtil::equal(
            ReflectableFactory::getCompoundArray(compoundArray), compound1.reflectable()));
}

TEST(ReflectableUtilTest, equalArrays)
{
    std::vector<uint32_t> array1 = {1, 2, 3};
    std::vector<uint32_t> array2 = {1, 2, 4};
    std::vector<uint32_t> array3 = {1, 2};

    auto array1Reflectable = ReflectableFactory::getUInt32Array(array1);
    auto array2Reflectable = ReflectableFactory::getUInt32Array(array2);
    auto array3Reflectable = ReflectableFactory::getUInt32Array(array3);

    ASSERT_TRUE(ReflectableUtil::equal(array1Reflectable, array1Reflectable));
    ASSERT_TRUE(ReflectableUtil::equal(array2Reflectable, array2Reflectable));
    ASSERT_TRUE(ReflectableUtil::equal(array3Reflectable, array3Reflectable));

    ASSERT_FALSE(ReflectableUtil::equal(array1Reflectable, array2Reflectable));
    ASSERT_FALSE(ReflectableUtil::equal(array1Reflectable, array3Reflectable));
}

TEST(ReflectableUtilTest, equalWrong)
{
    using allocator_type = ::std::allocator<uint8_t>;
    class WrongTypeInfo : public TypeInfoBase<allocator_type>
    {
    public:
        WrongTypeInfo(StringView schemaName, SchemaType schemaType, CppType cppType) :
                TypeInfoBase(schemaName, schemaType, cppType)
        {}
    };

    const WrongTypeInfo wrongTypeInfo = {"int<>"_sv, SchemaType::DYNAMIC_SIGNED_BITFIELD, CppType::PUBSUB};
    const WrongTypeInfo wrongTypeInfoDiffName = {
            "diff"_sv, SchemaType::DYNAMIC_SIGNED_BITFIELD, CppType::PUBSUB};

    class Reflectable : public ::zserio::ReflectableAllocatorHolderBase<allocator_type>
    {
    public:
        explicit Reflectable(const TypeInfoBase<allocator_type>& typeInfo, const allocator_type& allocator) :
                ::zserio::ReflectableAllocatorHolderBase<allocator_type>(typeInfo, allocator)
        {}
    };

    auto wrongReflectable =
            std::allocate_shared<Reflectable>(allocator_type(), wrongTypeInfo, allocator_type());
    auto wrongReflectableDiffName =
            std::allocate_shared<Reflectable>(allocator_type(), wrongTypeInfoDiffName, allocator_type());

    ASSERT_THROW(ReflectableUtil::equal(wrongReflectable, wrongReflectable), CppRuntimeException);
    ASSERT_FALSE(ReflectableUtil::equal(wrongReflectable, wrongReflectableDiffName));
}

TEST(ReflectableUtilTest, getValueArithmeticType)
{
    ASSERT_FALSE(ReflectableUtil::getValue<bool>(ReflectableFactory::getBool(false)));

    ASSERT_EQ(1, ReflectableUtil::getValue<uint8_t>(ReflectableFactory::getUInt8(1)));
    ASSERT_EQ(12, ReflectableUtil::getValue<uint16_t>(ReflectableFactory::getUInt16(12)));
    ASSERT_EQ(123, ReflectableUtil::getValue<uint32_t>(ReflectableFactory::getUInt32(123)));
    ASSERT_EQ(1234, ReflectableUtil::getValue<uint64_t>(ReflectableFactory::getUInt64(1234)));

    ASSERT_EQ(-1, ReflectableUtil::getValue<int8_t>(ReflectableFactory::getInt8(-1)));
    ASSERT_EQ(-12, ReflectableUtil::getValue<int16_t>(ReflectableFactory::getInt16(-12)));
    ASSERT_EQ(-123, ReflectableUtil::getValue<int32_t>(ReflectableFactory::getInt32(-123)));
    ASSERT_EQ(-1234, ReflectableUtil::getValue<int64_t>(ReflectableFactory::getInt64(-1234)));

    ASSERT_EQ(12, ReflectableUtil::getValue<uint16_t>(ReflectableFactory::getVarUInt16(12)));
    ASSERT_EQ(123, ReflectableUtil::getValue<uint32_t>(ReflectableFactory::getVarUInt32(123)));
    ASSERT_EQ(1234, ReflectableUtil::getValue<uint64_t>(ReflectableFactory::getVarUInt64(1234)));
    ASSERT_EQ(UINT64_MAX, ReflectableUtil::getValue<uint64_t>(ReflectableFactory::getVarUInt(UINT64_MAX)));

    ASSERT_EQ(-12, ReflectableUtil::getValue<int16_t>(ReflectableFactory::getVarInt16(-12)));
    ASSERT_EQ(-123, ReflectableUtil::getValue<int32_t>(ReflectableFactory::getVarInt32(-123)));
    ASSERT_EQ(-1234, ReflectableUtil::getValue<int64_t>(ReflectableFactory::getVarInt64(-1234)));
    ASSERT_EQ(INT64_MIN, ReflectableUtil::getValue<int64_t>(ReflectableFactory::getVarInt(INT64_MIN)));
    ASSERT_EQ(INT64_MAX, ReflectableUtil::getValue<int64_t>(ReflectableFactory::getVarInt(INT64_MAX)));

    ASSERT_EQ(0, ReflectableUtil::getValue<uint32_t>(ReflectableFactory::getVarSize(0)));

    ASSERT_EQ(1.0F, ReflectableUtil::getValue<float>(ReflectableFactory::getFloat16(1.0F)));
    ASSERT_EQ(3.5F, ReflectableUtil::getValue<float>(ReflectableFactory::getFloat32(3.5F)));
    ASSERT_EQ(9.875, ReflectableUtil::getValue<double>(ReflectableFactory::getFloat64(9.875)));
}

TEST(ReflectableUtilTest, getValueString)
{
    ASSERT_EQ("test"_sv, ReflectableUtil::getValue<StringView>(ReflectableFactory::getString("test")));
}

TEST(ReflectableUtilTest, getValueBitBuffer)
{
    const BitBuffer bitBuffer;
    auto reflectable = ReflectableFactory::getBitBuffer(bitBuffer);
    const BitBuffer& bitBufferRef = ReflectableUtil::getValue<BitBuffer>(reflectable);
    ASSERT_EQ(bitBuffer, bitBufferRef);
}

TEST(ReflectableUtilTest, getValueEnum)
{
    ReflectableUtilEnum reflectableUtilEnum = ReflectableUtilEnum::ONE;
    auto reflectable = enumReflectable(reflectableUtilEnum);
    ASSERT_EQ(reflectableUtilEnum, ReflectableUtil::getValue<ReflectableUtilEnum>(reflectable));
}

TEST(ReflectableUtilTest, getValueBitmask)
{
    ReflectableUtilBitmask reflectableUtilBitmask = ReflectableUtilBitmask::Values::READ;
    auto reflectable = reflectableUtilBitmask.reflectable();
    ASSERT_EQ(reflectableUtilBitmask, ReflectableUtil::getValue<ReflectableUtilBitmask>(reflectable));
}

TEST(ReflectableUtilTest, getValueCompound)
{
    ReflectableUtilUnion reflectableUtilUnion;
    auto reflectable = reflectableUtilUnion.reflectable();
    ASSERT_EQ(&reflectableUtilUnion, &ReflectableUtil::getValue<ReflectableUtilUnion>(reflectable));

    ReflectableUtilUnion& reflectableUtilUnionRef =
            ReflectableUtil::getValue<ReflectableUtilUnion>(reflectable);
    reflectableUtilUnionRef.setReflectableUtilBitmask(ReflectableUtilBitmask::Values::WRITE);
    ASSERT_EQ(ReflectableUtilBitmask::Values::WRITE, reflectableUtilUnion.getReflectableUtilBitmask());

    auto constReflectable = static_cast<const ReflectableUtilUnion&>(reflectableUtilUnion).reflectable();
    const ReflectableUtilUnion& reflectableUtilUnionConstRef =
            ReflectableUtil::getValue<ReflectableUtilUnion>(constReflectable);
    ASSERT_EQ(ReflectableUtilBitmask::Values::WRITE, reflectableUtilUnionConstRef.getReflectableUtilBitmask());
    ASSERT_EQ(&reflectableUtilUnion, &reflectableUtilUnionConstRef);
}

TEST(ReflectableUtilTest, getValueBuiltinArray)
{
    std::vector<uint8_t> uint8Array{{1, 2, 3}};
    auto reflectable = ReflectableFactory::getUInt8Array(uint8Array);
    std::vector<uint8_t>& uint8ArrayRef = ReflectableUtil::getValue<std::vector<uint8_t>>(reflectable);
    ASSERT_EQ(&uint8Array, &uint8ArrayRef);

    auto constReflectable =
            ReflectableFactory::getUInt8Array(static_cast<const std::vector<uint8_t>&>(uint8Array));
    const std::vector<uint8_t>& uint8ArrayConstRef =
            ReflectableUtil::getValue<std::vector<uint8_t>>(constReflectable);
    ASSERT_EQ(&uint8Array, &uint8ArrayConstRef);
}

TEST(ReflectableUtilTest, getValueCompoundArray)
{
    std::vector<ReflectableUtilUnion> reflectableUtilUnionArray;
    auto reflectable = ReflectableFactory::getCompoundArray(reflectableUtilUnionArray);
    std::vector<ReflectableUtilUnion>& reflectableUtilUnionArrayRef =
            ReflectableUtil::getValue<std::vector<ReflectableUtilUnion>>(reflectable);
    ASSERT_EQ(&reflectableUtilUnionArray, &reflectableUtilUnionArrayRef);
}

} // namespace zserio
