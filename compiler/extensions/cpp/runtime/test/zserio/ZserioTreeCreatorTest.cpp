#include "gtest/gtest.h"

#include "zserio/ZserioTreeCreator.h"
#include "zserio/StringConvertUtil.h"

#include "zserio/ZserioTreeCreatorTestObject.h"

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
    ASSERT_THROW(detail::makeAnyValue(BuiltinTypeInfo<>::getString(), 1, allocator), CppRuntimeException);
    ASSERT_THROW(detail::makeAnyValue(BuiltinTypeInfo<>::getString(), 1.0f, allocator), CppRuntimeException);
    ASSERT_THROW(detail::makeAnyValue(BuiltinTypeInfo<>::getString(), true, allocator), CppRuntimeException);
    ASSERT_THROW(detail::makeAnyValue(BuiltinTypeInfo<>::getString(), BitBuffer(), allocator),
            CppRuntimeException);
}

TEST(ZserioTreeCreatorTest, createObject)
{
    ZserioTreeCreator creator(DummyObject::typeInfo());
    creator.beginRoot();
    IReflectablePtr reflectable = creator.endRoot();
    ASSERT_TRUE(reflectable);
    ASSERT_EQ(CppType::STRUCT, reflectable->getTypeInfo().getCppType());
}

TEST(ZserioTreeCreatorTest, createObjectSetFields)
{
    ZserioTreeCreator creator(DummyObject::typeInfo());
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
    ZserioTreeCreator creator(DummyObject::typeInfo());
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
    ZserioTreeCreator creator(DummyObject::typeInfo());
    creator.beginRoot();
    creator.setValue("value", 13);
    creator.setValue("text", string<>("test"));
    creator.beginCompound("nested");
    creator.setValue("value", 10);
    creator.setValue("text", "nested"_sv);
    creator.setValue("data", BitBuffer({0x3c}, 6));
    creator.setValue("dummyEnum", DummyEnum::ONE);
    creator.setValue("dummyBitmask", DummyBitmask(DummyBitmask::Values::WRITE));
    creator.endCompound();
    creator.beginArray("nestedArray");
    creator.beginCompoundElement();
    creator.setValue("value", 5);
    const std::string nestedArrayText = "nestedArray";
    creator.setValue("text", nestedArrayText);
    creator.setValue("dummyEnum", DummyEnum::TWO);
    creator.setValue("dummyBitmask", DummyBitmask(DummyBitmask::Values::READ));
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
    ASSERT_EQ(0x3c, reflectable->find("nested.data")->getBitBuffer().getBuffer()[0]);
    ASSERT_EQ(6, reflectable->find("nested.data")->getBitBuffer().getBitSize());
    ASSERT_EQ(enumToValue(DummyEnum::ONE), reflectable->find("nested.dummyEnum")->getUInt8());
    ASSERT_EQ(DummyBitmask::Values::WRITE, DummyBitmask(reflectable->find("nested.dummyBitmask")->getUInt8()));
    ASSERT_EQ(1, reflectable->getField("nestedArray")->size());
    ASSERT_EQ(5, reflectable->getField("nestedArray")->at(0)->getField("value")->getUInt32());
    ASSERT_EQ("nestedArray"_sv, reflectable->getField("nestedArray")->at(0)->getField("text")->getStringView());
    ASSERT_EQ(enumToValue(DummyEnum::TWO),
            reflectable->getField("nestedArray")->at(0)->getField("dummyEnum")->getUInt8());
    ASSERT_EQ(DummyBitmask::Values::READ,
            DummyBitmask(reflectable->getField("nestedArray")->at(0)->getField("dummyBitmask")->getUInt8()));
    ASSERT_EQ(4, reflectable->getField("textArray")->size());
    ASSERT_EQ("this"_sv, reflectable->getField("textArray")->at(0)->getStringView());
    ASSERT_EQ("is"_sv, reflectable->getField("textArray")->at(1)->getStringView());
    ASSERT_EQ("text"_sv, reflectable->getField("textArray")->at(2)->getStringView());
    ASSERT_EQ("array"_sv, reflectable->getField("textArray")->at(3)->getStringView());
    ASSERT_EQ(1, reflectable->getField("externArray")->size());
    ASSERT_EQ(0x0f, reflectable->getField("externArray")->at(0)->getBitBuffer().getBuffer()[0]);
    ASSERT_EQ(4, reflectable->getField("externArray")->at(0)->getBitBuffer().getBitSize());
    ASSERT_EQ(false, reflectable->getField("optionalBool")->getBool());
    ASSERT_TRUE(reflectable->getField("optionalNested"));
    ASSERT_EQ("optionalNested"_sv, reflectable->find("optionalNested.text")->getStringView());
}

TEST(ZserioTreeCreator, exceptionsBeforeRoot)
{
    ZserioTreeCreator creator(DummyObject::typeInfo());

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
    ZserioTreeCreator creator(DummyObject::typeInfo());
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
    ZserioTreeCreator creator(DummyObject::typeInfo());
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
    ZserioTreeCreator creator(DummyObject::typeInfo());
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
    ZserioTreeCreator creator(DummyObject::typeInfo());
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
    ZserioTreeCreator creator(DummyObject::typeInfo());
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

// just to improve test coverage
TEST(ZserioTreeCreator, dummyObjectCoverage)
{
    BitBuffer bitBuffer(0);
    BitStreamWriter writer(bitBuffer);
    DummyEnum dummyEnum = DummyEnum::ONE;
    auto reflectable = enumReflectable(dummyEnum);
    ASSERT_EQ(8, reflectable->bitSizeOf());
    ASSERT_NO_THROW(reflectable->write(writer));

    DummyBitmask dummyBitmask = DummyBitmask::Values::READ;
    reflectable = dummyBitmask.reflectable();
    ASSERT_EQ(8, reflectable->bitSizeOf());
    ASSERT_NO_THROW(reflectable->write(writer));

    DummyNested dummyNested;
    reflectable = dummyNested.reflectable();
    ASSERT_THROW(reflectable->getField("nonexistent"), CppRuntimeException);
    ASSERT_THROW(reflectable->setField("nonexistent", AnyHolder<>()), CppRuntimeException);
    ASSERT_THROW(reflectable->getParameter("nonexistent"), CppRuntimeException);
    ASSERT_THROW(reflectable->getParameter("param"), CppRuntimeException); // not initalized
    ASSERT_EQ(0, reflectable->bitSizeOf());
    ASSERT_NO_THROW(reflectable->write(writer));

    DummyObject dummyObject;
    reflectable = dummyObject.reflectable();
    ASSERT_THROW(reflectable->getField("nonexistent"), CppRuntimeException);
    ASSERT_EQ(nullptr, reflectable->getField("externArray"));
    ASSERT_THROW(reflectable->setField("nonexistent", AnyHolder<>()), CppRuntimeException);
    ASSERT_THROW(reflectable->createField("nonexistent"), CppRuntimeException);
    ASSERT_THROW(reflectable->getParameter("nonexistent"), CppRuntimeException);
    ASSERT_EQ(0, reflectable->bitSizeOf());
    ASSERT_NO_THROW(reflectable->write(writer));
}

} // namespace zserio
