#include "gtest/gtest.h"

#include "zserio/StringConvertUtil.h"
#include "zserio/StringView.h"
#include "zserio/ZserioTreeCreator.h"
#include "zserio/TypeInfo.h"

#include "test_object/CreatorBitmask.h"
#include "test_object/CreatorEnum.h"
#include "test_object/CreatorNested.h"
#include "test_object/CreatorObject.h"

using test_object::CreatorBitmask;
using test_object::CreatorEnum;
using test_object::CreatorNested;
using test_object::CreatorObject;

namespace zserio
{

TEST(ZserioTreeCreator, makeAnyValue)
{
    const std::allocator<uint8_t> allocator;

    // bool
    auto any = detail::makeAnyValue(BuiltinTypeInfo<>::getBool(), true, allocator);
    ASSERT_EQ(true, any.get<bool>());
    ASSERT_THROW(detail::makeAnyValue(BuiltinTypeInfo<>::getBool(), 1, allocator), CppRuntimeException);
    ASSERT_THROW(detail::makeAnyValue(BuiltinTypeInfo<>::getBool(), 1.0f, allocator), CppRuntimeException);
    ASSERT_THROW(detail::makeAnyValue(BuiltinTypeInfo<>::getBool(), "1", allocator), CppRuntimeException);
    ASSERT_THROW(detail::makeAnyValue(BuiltinTypeInfo<>::getBool(), BitBuffer(), allocator),
            CppRuntimeException);

    // uint8
    any = detail::makeAnyValue(BuiltinTypeInfo<>::getUInt8(), 8, allocator);
    ASSERT_EQ(8, any.get<uint8_t>());
    ASSERT_THROW(detail::makeAnyValue(BuiltinTypeInfo<>::getUInt8(), -1, allocator), CppRuntimeException);
    ASSERT_THROW(detail::makeAnyValue(BuiltinTypeInfo<>::getUInt8(), 256, allocator), CppRuntimeException);
    ASSERT_THROW(detail::makeAnyValue(BuiltinTypeInfo<>::getUInt8(), 1.0f, allocator), CppRuntimeException);
    ASSERT_THROW(detail::makeAnyValue(BuiltinTypeInfo<>::getUInt8(), "1", allocator), CppRuntimeException);
    ASSERT_THROW(detail::makeAnyValue(BuiltinTypeInfo<>::getUInt8(), true, allocator), CppRuntimeException);
    ASSERT_THROW(detail::makeAnyValue(BuiltinTypeInfo<>::getUInt8(), BitBuffer(), allocator),
            CppRuntimeException);

    // uint16
    any = detail::makeAnyValue(BuiltinTypeInfo<>::getUInt16(), 16, allocator);
    ASSERT_EQ(16, any.get<uint16_t>());
    ASSERT_THROW(detail::makeAnyValue(BuiltinTypeInfo<>::getUInt16(), -1, allocator), CppRuntimeException);
    ASSERT_THROW(detail::makeAnyValue(BuiltinTypeInfo<>::getUInt16(), 65536, allocator), CppRuntimeException);
    ASSERT_THROW(detail::makeAnyValue(BuiltinTypeInfo<>::getUInt16(), 1.0f, allocator), CppRuntimeException);
    ASSERT_THROW(detail::makeAnyValue(BuiltinTypeInfo<>::getUInt16(), "1", allocator), CppRuntimeException);
    ASSERT_THROW(detail::makeAnyValue(BuiltinTypeInfo<>::getUInt16(), true, allocator), CppRuntimeException);
    ASSERT_THROW(detail::makeAnyValue(BuiltinTypeInfo<>::getUInt16(), BitBuffer(), allocator),
            CppRuntimeException);

    // uint32
    any = detail::makeAnyValue(BuiltinTypeInfo<>::getUInt32(), 32, allocator);
    ASSERT_EQ(32, any.get<uint32_t>());
    ASSERT_THROW(detail::makeAnyValue(BuiltinTypeInfo<>::getUInt32(), -1, allocator), CppRuntimeException);
    ASSERT_THROW(detail::makeAnyValue(BuiltinTypeInfo<>::getUInt32(), 4294967296, allocator),
            CppRuntimeException);
    ASSERT_THROW(detail::makeAnyValue(BuiltinTypeInfo<>::getUInt32(), 1.0f, allocator), CppRuntimeException);
    ASSERT_THROW(detail::makeAnyValue(BuiltinTypeInfo<>::getUInt32(), "1", allocator), CppRuntimeException);
    ASSERT_THROW(detail::makeAnyValue(BuiltinTypeInfo<>::getUInt32(), true, allocator), CppRuntimeException);
    ASSERT_THROW(detail::makeAnyValue(BuiltinTypeInfo<>::getUInt32(), BitBuffer(), allocator),
            CppRuntimeException);

    // uint64
    any = detail::makeAnyValue(BuiltinTypeInfo<>::getUInt64(), 64, allocator);
    ASSERT_EQ(64, any.get<uint64_t>());
    ASSERT_THROW(detail::makeAnyValue(BuiltinTypeInfo<>::getUInt64(), -1, allocator), CppRuntimeException);
    ASSERT_THROW(detail::makeAnyValue(BuiltinTypeInfo<>::getUInt64(), 1.0f, allocator), CppRuntimeException);
    ASSERT_THROW(detail::makeAnyValue(BuiltinTypeInfo<>::getUInt64(), "1", allocator), CppRuntimeException);
    ASSERT_THROW(detail::makeAnyValue(BuiltinTypeInfo<>::getUInt64(), true, allocator), CppRuntimeException);
    ASSERT_THROW(detail::makeAnyValue(BuiltinTypeInfo<>::getUInt64(), BitBuffer(), allocator),
            CppRuntimeException);

    // int8
    any = detail::makeAnyValue(BuiltinTypeInfo<>::getInt8(), 8, allocator);
    ASSERT_EQ(8, any.get<int8_t>());
    ASSERT_THROW(detail::makeAnyValue(BuiltinTypeInfo<>::getInt8(), -129, allocator), CppRuntimeException);
    ASSERT_THROW(detail::makeAnyValue(BuiltinTypeInfo<>::getInt8(), 128, allocator), CppRuntimeException);
    ASSERT_THROW(detail::makeAnyValue(BuiltinTypeInfo<>::getInt8(), 1.0f, allocator), CppRuntimeException);
    ASSERT_THROW(detail::makeAnyValue(BuiltinTypeInfo<>::getInt8(), "1", allocator), CppRuntimeException);
    ASSERT_THROW(detail::makeAnyValue(BuiltinTypeInfo<>::getInt8(), true, allocator), CppRuntimeException);
    ASSERT_THROW(detail::makeAnyValue(BuiltinTypeInfo<>::getInt8(), BitBuffer(), allocator),
            CppRuntimeException);

    // int16
    any = detail::makeAnyValue(BuiltinTypeInfo<>::getInt16(), 16, allocator);
    ASSERT_EQ(16, any.get<int16_t>());
    ASSERT_THROW(detail::makeAnyValue(BuiltinTypeInfo<>::getInt16(), -32769, allocator), CppRuntimeException);
    ASSERT_THROW(detail::makeAnyValue(BuiltinTypeInfo<>::getInt16(), 32768, allocator), CppRuntimeException);
    ASSERT_THROW(detail::makeAnyValue(BuiltinTypeInfo<>::getInt16(), 1.0f, allocator), CppRuntimeException);
    ASSERT_THROW(detail::makeAnyValue(BuiltinTypeInfo<>::getInt16(), "1", allocator), CppRuntimeException);
    ASSERT_THROW(detail::makeAnyValue(BuiltinTypeInfo<>::getInt16(), true, allocator), CppRuntimeException);
    ASSERT_THROW(detail::makeAnyValue(BuiltinTypeInfo<>::getInt16(), BitBuffer(), allocator),
            CppRuntimeException);

    // int32
    any = detail::makeAnyValue(BuiltinTypeInfo<>::getInt32(), 32, allocator);
    ASSERT_EQ(32, any.get<int32_t>());
    ASSERT_THROW(detail::makeAnyValue(BuiltinTypeInfo<>::getInt32(), -2147483649LL, allocator),
            CppRuntimeException);
    ASSERT_THROW(detail::makeAnyValue(BuiltinTypeInfo<>::getInt32(), 2147483648LL, allocator),
            CppRuntimeException);
    ASSERT_THROW(detail::makeAnyValue(BuiltinTypeInfo<>::getInt32(), 1.0f, allocator), CppRuntimeException);
    ASSERT_THROW(detail::makeAnyValue(BuiltinTypeInfo<>::getInt32(), "1", allocator), CppRuntimeException);
    ASSERT_THROW(detail::makeAnyValue(BuiltinTypeInfo<>::getInt32(), true, allocator), CppRuntimeException);
    ASSERT_THROW(detail::makeAnyValue(BuiltinTypeInfo<>::getInt32(), BitBuffer(), allocator),
            CppRuntimeException);

    // int64
    any = detail::makeAnyValue(BuiltinTypeInfo<>::getInt64(), 64, allocator);
    ASSERT_EQ(64, any.get<int64_t>());
    ASSERT_THROW(detail::makeAnyValue(BuiltinTypeInfo<>::getInt64(), 9223372036854775808ULL, allocator),
            CppRuntimeException);
    ASSERT_THROW(detail::makeAnyValue(BuiltinTypeInfo<>::getInt64(), 1.0f, allocator), CppRuntimeException);
    ASSERT_THROW(detail::makeAnyValue(BuiltinTypeInfo<>::getInt64(), "1", allocator), CppRuntimeException);
    ASSERT_THROW(detail::makeAnyValue(BuiltinTypeInfo<>::getInt64(), true, allocator), CppRuntimeException);
    ASSERT_THROW(detail::makeAnyValue(BuiltinTypeInfo<>::getInt64(), BitBuffer(), allocator),
            CppRuntimeException);

    // float
    any = detail::makeAnyValue(BuiltinTypeInfo<>::getFloat32(), 3.5f, allocator);
    ASSERT_EQ(3.5f, any.get<float>());
    any = detail::makeAnyValue(BuiltinTypeInfo<>::getFloat32(), 1, allocator);
    ASSERT_EQ(1.0f, any.get<float>());
    any = detail::makeAnyValue(BuiltinTypeInfo<>::getFloat32(), 1.0, allocator);
    ASSERT_EQ(1.0f, any.get<float>());
    ASSERT_THROW(detail::makeAnyValue(BuiltinTypeInfo<>::getFloat32(), "1", allocator), CppRuntimeException);
    ASSERT_THROW(detail::makeAnyValue(BuiltinTypeInfo<>::getFloat32(), true, allocator), CppRuntimeException);
    ASSERT_THROW(detail::makeAnyValue(BuiltinTypeInfo<>::getFloat32(), BitBuffer(), allocator),
            CppRuntimeException);

    // double
    any = detail::makeAnyValue(BuiltinTypeInfo<>::getFloat64(), 3.14, allocator);
    ASSERT_EQ(3.14, any.get<double>());
    any = detail::makeAnyValue(BuiltinTypeInfo<>::getFloat64(), 1, allocator);
    ASSERT_EQ(1.0, any.get<double>());
    any = detail::makeAnyValue(BuiltinTypeInfo<>::getFloat64(), 1.0f, allocator);
    ASSERT_EQ(1.0, any.get<double>());
    ASSERT_THROW(detail::makeAnyValue(BuiltinTypeInfo<>::getFloat64(), "1", allocator), CppRuntimeException);
    ASSERT_THROW(detail::makeAnyValue(BuiltinTypeInfo<>::getFloat64(), true, allocator), CppRuntimeException);
    ASSERT_THROW(detail::makeAnyValue(BuiltinTypeInfo<>::getFloat64(), BitBuffer(), allocator),
            CppRuntimeException);

    // string
    any = detail::makeAnyValue(BuiltinTypeInfo<>::getString(), "text", allocator);
    ASSERT_EQ("text", any.get<std::string>());
    any = detail::makeAnyValue(BuiltinTypeInfo<>::getString(), "text"_sv, allocator);
    ASSERT_EQ("text", any.get<std::string>());
    any = detail::makeAnyValue(BuiltinTypeInfo<>::getString(), std::string("text"), allocator);
    ASSERT_EQ("text", any.get<std::string>());
    const std::string stringString = "text";
    any = detail::makeAnyValue(BuiltinTypeInfo<>::getString(), stringString, allocator);
    ASSERT_EQ("text", any.get<std::string>());
    ASSERT_THROW(detail::makeAnyValue(BuiltinTypeInfo<>::getString(), 1, allocator), CppRuntimeException);
    ASSERT_THROW(detail::makeAnyValue(BuiltinTypeInfo<>::getString(), 1.0f, allocator), CppRuntimeException);
    ASSERT_THROW(detail::makeAnyValue(BuiltinTypeInfo<>::getString(), true, allocator), CppRuntimeException);
    ASSERT_THROW(detail::makeAnyValue(BuiltinTypeInfo<>::getString(), BitBuffer(), allocator),
            CppRuntimeException);

    // enum
    any = detail::makeAnyValue(enumTypeInfo<CreatorEnum>(), CreatorEnum::ONE, allocator);
    ASSERT_EQ(CreatorEnum::ONE, any.get<CreatorEnum>());
    any = detail::makeAnyValue(enumTypeInfo<CreatorEnum>(), 0, allocator);
    ASSERT_EQ(enumToValue(CreatorEnum::ONE), any.get<int8_t>());
    any = detail::makeAnyValue(enumTypeInfo<CreatorEnum>(), "ONE"_sv, allocator);
    ASSERT_EQ(enumToValue(CreatorEnum::ONE), any.get<int8_t>());
    any = detail::makeAnyValue(enumTypeInfo<CreatorEnum>(), "MinusOne"_sv, allocator);
    ASSERT_EQ(enumToValue(CreatorEnum::MinusOne), any.get<int8_t>());
    ASSERT_THROW(detail::makeAnyValue(enumTypeInfo<CreatorEnum>(), "NONEXISTING"_sv, allocator),
            CppRuntimeException);
    ASSERT_THROW(detail::makeAnyValue(enumTypeInfo<CreatorEnum>(), "***"_sv, allocator),
            CppRuntimeException);
    // check all string overloads!
    ASSERT_THROW(detail::makeAnyValue(enumTypeInfo<CreatorEnum>(), "10 /* no match */"_sv, allocator),
            CppRuntimeException);
    ASSERT_THROW(detail::makeAnyValue(enumTypeInfo<CreatorEnum>(), "-10 /* no match */", allocator),
            CppRuntimeException);
    ASSERT_THROW(detail::makeAnyValue(enumTypeInfo<CreatorEnum>(),
            string<>("10 /* no match */", allocator), allocator), CppRuntimeException);
    const string<> enumString("10 /* no match */", allocator);
    ASSERT_THROW(detail::makeAnyValue(enumTypeInfo<CreatorEnum>(), enumString, allocator), CppRuntimeException);
    // out-of-range int64_t
    ASSERT_THROW(detail::makeAnyValue(enumTypeInfo<CreatorEnum>(), "99999999999999999999", allocator),
            CppRuntimeException);
    ASSERT_THROW(detail::makeAnyValue(enumTypeInfo<CreatorEnum>(), "-99999999999999999999"_sv, allocator),
            CppRuntimeException);
    ASSERT_THROW(detail::makeAnyValue(enumTypeInfo<CreatorEnum>(), ""_sv, allocator), CppRuntimeException);

    // bitmask
    any = detail::makeAnyValue(CreatorBitmask::typeInfo(), CreatorBitmask(CreatorBitmask::Values::READ), allocator);
    ASSERT_EQ(CreatorBitmask::Values::READ, any.get<CreatorBitmask>());
    any = detail::makeAnyValue(CreatorBitmask::typeInfo(), 1, allocator);
    ASSERT_EQ(CreatorBitmask(CreatorBitmask::Values::READ).getValue(), any.get<uint8_t>());
    any = detail::makeAnyValue(CreatorBitmask::typeInfo(), "READ"_sv, allocator);
    ASSERT_EQ(CreatorBitmask(CreatorBitmask::Values::READ).getValue(), any.get<uint8_t>());
    any = detail::makeAnyValue(CreatorBitmask::typeInfo(), "READ | WRITE"_sv, allocator);
    ASSERT_EQ(CreatorBitmask(CreatorBitmask::Values::READ | CreatorBitmask::Values::WRITE).getValue(),
            any.get<uint8_t>());
    any = detail::makeAnyValue(CreatorBitmask::typeInfo(), "READ|WRITE"_sv, allocator);
    ASSERT_EQ(CreatorBitmask(CreatorBitmask::Values::READ | CreatorBitmask::Values::WRITE).getValue(),
            any.get<uint8_t>());
    ASSERT_THROW(detail::makeAnyValue(CreatorBitmask::typeInfo(), "NONEXISTING"_sv, allocator),
            CppRuntimeException);
    ASSERT_THROW(detail::makeAnyValue(CreatorBitmask::typeInfo(), "READ | NONEXISTING"_sv, allocator),
            CppRuntimeException);
    ASSERT_THROW(detail::makeAnyValue(CreatorBitmask::typeInfo(), "READ * NONEXISTING"_sv, allocator),
            CppRuntimeException);
    ASSERT_THROW(detail::makeAnyValue(CreatorBitmask::typeInfo(), "***"_sv, allocator),
            CppRuntimeException);
    any = detail::makeAnyValue(CreatorBitmask::typeInfo(), "7 /* READ | WRITE */"_sv, allocator);
    ASSERT_EQ(7, any.get<uint8_t>());
    // check all string overloads!
    any = detail::makeAnyValue(CreatorBitmask::typeInfo(), "4 /* no match */"_sv, allocator);
    ASSERT_EQ(4, any.get<uint8_t>());
    any = detail::makeAnyValue(CreatorBitmask::typeInfo(), "4 /* no match */", allocator);
    ASSERT_EQ(4, any.get<uint8_t>());
    any = detail::makeAnyValue(CreatorBitmask::typeInfo(), string<>("4 /* no match */", allocator), allocator);
    ASSERT_EQ(4, any.get<uint8_t>());
    const string<> bitmaskString("4 /* no match */", allocator);
    any = detail::makeAnyValue(CreatorBitmask::typeInfo(), bitmaskString, allocator);
    ASSERT_EQ(4, any.get<uint8_t>());
    // out-of-range uint64_t
    ASSERT_THROW(detail::makeAnyValue(CreatorBitmask::typeInfo(), "99999999999999999999"_sv, allocator),
            CppRuntimeException);
    ASSERT_THROW(detail::makeAnyValue(CreatorBitmask::typeInfo(), ""_sv, allocator), CppRuntimeException);
}

TEST(ZserioTreeCreatorTest, parseBitmaskStringValue)
{
    static const ::std::array<ItemInfo, 3> values = {
        ItemInfo{ makeStringView("READ"), static_cast<uint64_t>(1) },
        ItemInfo{ makeStringView("READ_EXT"), static_cast<uint64_t>(2) },
        ItemInfo{ makeStringView("READ_EXT_EXT"), static_cast<uint64_t>(4) }
    };

    static const BitmaskTypeInfo<std::allocator<uint8_t>> typeInfo = {
        makeStringView("Bitmask"), BuiltinTypeInfo<>::getUInt8(), {}, values
    };

    const std::allocator<uint8_t> allocator;
    ASSERT_EQ(1, detail::parseBitmaskStringValue("READ", typeInfo, allocator).get<uint8_t>());
    ASSERT_EQ(2, detail::parseBitmaskStringValue("READ_EXT", typeInfo, allocator).get<uint8_t>());
    ASSERT_EQ(3, detail::parseBitmaskStringValue("READ_EXT | READ", typeInfo, allocator).get<uint8_t>());
    ASSERT_EQ(6, detail::parseBitmaskStringValue("READ_EXT_EXT | READ_EXT",
            typeInfo, allocator).get<uint8_t>());
    ASSERT_EQ(4, detail::parseBitmaskStringValue("READ_EXT_EXT | READ_EXT_EXT",
                typeInfo, allocator).get<uint8_t>());
    ASSERT_FALSE(detail::parseBitmaskStringValue("READ|", typeInfo, allocator).hasValue());
    ASSERT_FALSE(detail::parseBitmaskStringValue("READ | ", typeInfo, allocator).hasValue());
    ASSERT_FALSE(detail::parseBitmaskStringValue("READ|", typeInfo, allocator).hasValue());
    ASSERT_FALSE(detail::parseBitmaskStringValue("READ_EXT | ", typeInfo, allocator).hasValue());
    ASSERT_FALSE(detail::parseBitmaskStringValue("READ_EXTABC", typeInfo, allocator).hasValue());
}

TEST(ZserioTreeCreatorTest, createObject)
{
    ZserioTreeCreator creator(CreatorObject::typeInfo());
    creator.beginRoot();
    IReflectablePtr reflectable = creator.endRoot();
    ASSERT_TRUE(reflectable);
    ASSERT_EQ(CppType::STRUCT, reflectable->getTypeInfo().getCppType());
}

TEST(ZserioTreeCreatorTest, createObjectSetFields)
{
    ZserioTreeCreator creator(CreatorObject::typeInfo());
    creator.beginRoot();
    creator.setValue("value", 13);
    creator.setValue("text", "test");
    IReflectablePtr reflectable = creator.endRoot();
    ASSERT_TRUE(reflectable);

    ASSERT_EQ(13, reflectable->getField("value")->getUInt32());
    ASSERT_EQ("test"_sv, reflectable->getField("text")->getStringView());
}

TEST(ZserioTreeCreatorTest, createObjectResetFields)
{
    ZserioTreeCreator creator(CreatorObject::typeInfo());
    creator.beginRoot();
    creator.setValue("value", 13);
    creator.setValue("text", nullptr);
    creator.setValue("text", "test");
    IReflectablePtr reflectable = creator.endRoot();
    ASSERT_TRUE(reflectable);

    ASSERT_EQ(13, reflectable->getField("value")->getUInt32());
    ASSERT_EQ("test"_sv, reflectable->getField("text")->getStringView());
}

TEST(ZserioTreeCreatorTest, createObjectFull)
{
    ZserioTreeCreator creator(CreatorObject::typeInfo());
    creator.beginRoot();
    creator.setValue("value", 13);
    creator.setValue("text", string<>("test"));
    creator.beginCompound("nested");
    creator.setValue("value", 10);
    creator.setValue("text", "nested"_sv);
    creator.setValue("externData", BitBuffer({0x3C}, 6));
    creator.setValue("bytesData", vector<uint8_t>({0xFF}));
    creator.setValue("creatorEnum", CreatorEnum::ONE);
    creator.setValue("creatorBitmask", CreatorBitmask(CreatorBitmask::Values::WRITE));
    creator.endCompound();
    creator.beginArray("nestedArray");
    creator.beginCompoundElement();
    creator.setValue("value", 5);
    const std::string nestedArrayText = "nestedArray";
    creator.setValue("text", nestedArrayText);
    creator.setValue("creatorEnum", "MinusOne");
    creator.setValue("creatorBitmask", CreatorBitmask(CreatorBitmask::Values::READ));
    creator.endCompoundElement();
    creator.endArray();
    creator.beginArray("textArray");
    creator.addValueElement("this");
    creator.addValueElement("is");
    creator.addValueElement("text"_sv);
    creator.addValueElement("array");
    creator.endArray();
    creator.beginArray("externArray");
    creator.addValueElement(BitBuffer({0x0F}, 4));
    creator.endArray();
    creator.beginArray("bytesArray");
    creator.addValueElement(vector<uint8_t>({0xCA, 0xFE}));
    creator.endArray();
    creator.setValue("optionalBool", false);
    creator.beginCompound("optionalNested");
    creator.setValue("text", "optionalNested");
    creator.endCompound();
    IReflectablePtr reflectable = creator.endRoot();
    ASSERT_TRUE(reflectable);

    reflectable->initializeChildren();

    ASSERT_EQ(13, reflectable->getField("value")->getUInt32());
    ASSERT_EQ("test"_sv, reflectable->getField("text")->getStringView());
    ASSERT_EQ(13, reflectable->find("nested.param")->getUInt32());
    ASSERT_EQ(10, reflectable->find("nested.value")->getUInt32());
    ASSERT_EQ("nested"_sv, reflectable->find("nested.text")->getStringView());
    ASSERT_EQ(0x3C, reflectable->find("nested.externData")->getBitBuffer().getBuffer()[0]);
    ASSERT_EQ(6, reflectable->find("nested.externData")->getBitBuffer().getBitSize());
    ASSERT_EQ(1, reflectable->find("nested.bytesData")->getBytes().size());
    ASSERT_EQ(0xFF, reflectable->find("nested.bytesData")->getBytes()[0]);
    ASSERT_EQ(enumToValue(CreatorEnum::ONE), reflectable->find("nested.creatorEnum")->getInt8());
    ASSERT_EQ(CreatorBitmask::Values::WRITE, CreatorBitmask(reflectable->find("nested.creatorBitmask")->getUInt8()));
    ASSERT_EQ(1, reflectable->getField("nestedArray")->size());
    ASSERT_EQ(5, reflectable->getField("nestedArray")->at(0)->getField("value")->getUInt32());
    ASSERT_EQ("nestedArray"_sv, reflectable->getField("nestedArray")->at(0)->getField("text")->getStringView());
    ASSERT_EQ(enumToValue(CreatorEnum::MinusOne),
            reflectable->getField("nestedArray")->at(0)->getField("creatorEnum")->getInt8());
    ASSERT_EQ(CreatorBitmask::Values::READ,
            CreatorBitmask(reflectable->getField("nestedArray")->at(0)->getField("creatorBitmask")->getUInt8()));
    ASSERT_EQ(4, reflectable->getField("textArray")->size());
    ASSERT_EQ("this"_sv, reflectable->getField("textArray")->at(0)->getStringView());
    ASSERT_EQ("is"_sv, reflectable->getField("textArray")->at(1)->getStringView());
    ASSERT_EQ("text"_sv, reflectable->getField("textArray")->at(2)->getStringView());
    ASSERT_EQ("array"_sv, reflectable->getField("textArray")->at(3)->getStringView());
    ASSERT_EQ(1, reflectable->getField("externArray")->size());
    ASSERT_EQ(0x0f, reflectable->getField("externArray")->at(0)->getBitBuffer().getBuffer()[0]);
    ASSERT_EQ(4, reflectable->getField("externArray")->at(0)->getBitBuffer().getBitSize());
    ASSERT_EQ(1, reflectable->getField("bytesArray")->size());
    ASSERT_EQ(2, reflectable->getField("bytesArray")->at(0)->getBytes().size());
    ASSERT_EQ(0xCA, reflectable->getField("bytesArray")->at(0)->getBytes()[0]);
    ASSERT_EQ(0xFE, reflectable->getField("bytesArray")->at(0)->getBytes()[1]);
    ASSERT_EQ(false, reflectable->getField("optionalBool")->getBool());
    ASSERT_TRUE(reflectable->getField("optionalNested"));
    ASSERT_EQ("optionalNested"_sv, reflectable->find("optionalNested.text")->getStringView());
}

TEST(ZserioTreeCreator, exceptionsBeforeRoot)
{
    ZserioTreeCreator creator(CreatorObject::typeInfo());

    ASSERT_THROW(creator.endRoot(), CppRuntimeException);
    ASSERT_THROW(creator.beginArray("nestedArray"), CppRuntimeException);
    ASSERT_THROW(creator.endArray(), CppRuntimeException);
    ASSERT_THROW(creator.getFieldType("nested"), CppRuntimeException);
    ASSERT_THROW(creator.beginCompound("nested"), CppRuntimeException);
    ASSERT_THROW(creator.endCompound(), CppRuntimeException);
    ASSERT_THROW(creator.setValue("value", 13), CppRuntimeException);
    ASSERT_THROW(creator.setValue("nonexistent", nullptr), CppRuntimeException);
    ASSERT_THROW(creator.getElementType(), CppRuntimeException);
    ASSERT_THROW(creator.beginCompoundElement(), CppRuntimeException);
    ASSERT_THROW(creator.endCompoundElement(), CppRuntimeException);
    ASSERT_THROW(creator.addValueElement(13), CppRuntimeException);
}

TEST(ZserioTreeCreator, exceptionsInRoot)
{
    ZserioTreeCreator creator(CreatorObject::typeInfo());
    creator.beginRoot();

    ASSERT_THROW(creator.beginRoot(), CppRuntimeException);
    ASSERT_THROW(creator.beginArray("nonexistent"), CppRuntimeException);
    ASSERT_THROW(creator.beginArray("nested"), CppRuntimeException); // not an array
    ASSERT_THROW(creator.endArray(), CppRuntimeException);
    ASSERT_THROW(creator.beginCompound("nonexistent"), CppRuntimeException);
    ASSERT_THROW(creator.beginCompound("nestedArray"), CppRuntimeException); // is array
    ASSERT_THROW(creator.endCompound(), CppRuntimeException);
    ASSERT_THROW(creator.setValue("nonexistent", 13), CppRuntimeException);
    ASSERT_THROW(creator.setValue("nestedArray", 13), CppRuntimeException); // is array
    ASSERT_THROW(creator.beginCompoundElement(), CppRuntimeException);
    ASSERT_THROW(creator.endCompoundElement(), CppRuntimeException);
    ASSERT_THROW(creator.addValueElement(13), CppRuntimeException);
}

TEST(ZserioTreeCreator, exceptionsInCompound)
{
    ZserioTreeCreator creator(CreatorObject::typeInfo());
    creator.beginRoot();
    creator.beginCompound("nested");

    ASSERT_THROW(creator.beginRoot(), CppRuntimeException);
    ASSERT_THROW(creator.endRoot(), CppRuntimeException);
    ASSERT_THROW(creator.beginArray("nonexistent"), CppRuntimeException);
    ASSERT_THROW(creator.beginArray("value"), CppRuntimeException); // not an array
    ASSERT_THROW(creator.endArray(), CppRuntimeException);
    ASSERT_THROW(creator.beginCompound("nonexistent"), CppRuntimeException);
    ASSERT_THROW(creator.beginCompound("text"), CppRuntimeException); // not a compound
    ASSERT_THROW(creator.setValue("nonexistent", "test"), CppRuntimeException);
    ASSERT_THROW(creator.setValue("value", "test"), CppRuntimeException); // wrong type
    ASSERT_THROW(creator.beginCompoundElement(), CppRuntimeException);
    ASSERT_THROW(creator.endCompoundElement(), CppRuntimeException);
    ASSERT_THROW(creator.addValueElement(13), CppRuntimeException);
}

TEST(ZserioTreeCreator, exceptionsInCompoundArray)
{
    ZserioTreeCreator creator(CreatorObject::typeInfo());
    creator.beginRoot();
    creator.beginArray("nestedArray");

    ASSERT_THROW(creator.beginRoot(), CppRuntimeException);
    ASSERT_THROW(creator.endRoot(), CppRuntimeException);
    ASSERT_THROW(creator.beginArray("nonexistent"), CppRuntimeException);
    ASSERT_THROW(creator.beginCompound("nonexistent"), CppRuntimeException);
    ASSERT_THROW(creator.endCompound(), CppRuntimeException);
    ASSERT_THROW(creator.setValue("nonexistent", 13), CppRuntimeException);
    ASSERT_THROW(creator.endCompoundElement(), CppRuntimeException);
    ASSERT_THROW(creator.addValueElement(13), CppRuntimeException);
}

TEST(ZserioTreeCreator, exceptionsInSimpleArray)
{
    ZserioTreeCreator creator(CreatorObject::typeInfo());
    creator.beginRoot();
    creator.beginArray("textArray");

    ASSERT_THROW(creator.beginRoot(), CppRuntimeException);
    ASSERT_THROW(creator.endRoot(), CppRuntimeException);
    ASSERT_THROW(creator.beginArray("nonexistent"), CppRuntimeException);
    ASSERT_THROW(creator.beginCompound("nonexistent"), CppRuntimeException);
    ASSERT_THROW(creator.endCompound(), CppRuntimeException);
    ASSERT_THROW(creator.setValue("nonexistent", 13), CppRuntimeException);
    ASSERT_THROW(creator.beginCompoundElement(), CppRuntimeException); // not a compound array
    ASSERT_THROW(creator.endCompoundElement(), CppRuntimeException);
    ASSERT_THROW(creator.addValueElement(13), CppRuntimeException); // wrong type
}

TEST(ZserioTreeCreator, exceptionsInCompoundElement)
{
    ZserioTreeCreator creator(CreatorObject::typeInfo());
    creator.beginRoot();
    creator.beginArray("nestedArray");
    creator.beginCompoundElement();

    ASSERT_THROW(creator.beginRoot(), CppRuntimeException);
    ASSERT_THROW(creator.endRoot(), CppRuntimeException);
    ASSERT_THROW(creator.beginArray("nonexistent"), CppRuntimeException);
    ASSERT_THROW(creator.endArray(), CppRuntimeException);
    ASSERT_THROW(creator.beginCompound("nonexistent"), CppRuntimeException);
    ASSERT_THROW(creator.endCompound(), CppRuntimeException);
    ASSERT_THROW(creator.setValue("nonexistent", 13), CppRuntimeException);
    ASSERT_THROW(creator.beginCompoundElement(), CppRuntimeException);
    ASSERT_THROW(creator.addValueElement(13), CppRuntimeException);
}

} // namespace zserio
