#include "gtest/gtest.h"

#include "zserio/ZserioTreeCreator.h"
#include "zserio/StringConvertUtil.h"

#include "zserio/CreatorTestObject.h"

namespace zserio
{

TEST(ZserioTreeCreator, makeAnyValue)
{
    const std::allocator<uint8_t> allocator;

    auto any = detail::makeAnyValue(BuiltinTypeInfo<>::getUInt8(), 8, allocator);
    ASSERT_EQ(8, any.get<uint8_t>());

    any = detail::makeAnyValue(BuiltinTypeInfo<>::getUInt16(), 16, allocator);
    ASSERT_EQ(16, any.get<uint16_t>());

    any = detail::makeAnyValue(BuiltinTypeInfo<>::getUInt32(), 32, allocator);
    ASSERT_EQ(32, any.get<uint32_t>());

    any = detail::makeAnyValue(BuiltinTypeInfo<>::getUInt64(), 64, allocator);
    ASSERT_EQ(64, any.get<uint64_t>());

    any = detail::makeAnyValue(BuiltinTypeInfo<>::getInt8(), 8, allocator);
    ASSERT_EQ(8, any.get<int8_t>());

    any = detail::makeAnyValue(BuiltinTypeInfo<>::getInt16(), 16, allocator);
    ASSERT_EQ(16, any.get<int16_t>());

    any = detail::makeAnyValue(BuiltinTypeInfo<>::getInt32(), 32, allocator);
    ASSERT_EQ(32, any.get<int32_t>());

    any = detail::makeAnyValue(BuiltinTypeInfo<>::getInt64(), 64, allocator);
    ASSERT_EQ(64, any.get<int64_t>());

    any = detail::makeAnyValue(BuiltinTypeInfo<>::getFloat32(), 3.5f, allocator);
    ASSERT_EQ(3.5f, any.get<float>());

    any = detail::makeAnyValue(BuiltinTypeInfo<>::getFloat64(), 3.14, allocator);
    ASSERT_EQ(3.14, any.get<double>());
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
    ASSERT_EQ("test"_sv, reflectable->getField("text")->getString());
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
    ASSERT_EQ("test"_sv, reflectable->getField("text")->getString());
    ASSERT_EQ(13, reflectable->find("nested.param")->getUInt32());
    ASSERT_EQ(10, reflectable->find("nested.value")->getUInt32());
    ASSERT_EQ("nested"_sv, reflectable->find("nested.text")->getString());
    ASSERT_EQ(0x3c, reflectable->find("nested.data")->getBitBuffer().getBuffer()[0]);
    ASSERT_EQ(6, reflectable->find("nested.data")->getBitBuffer().getBitSize());
    ASSERT_EQ(enumToValue(DummyEnum::ONE), reflectable->find("nested.dummyEnum")->getUInt8());
    ASSERT_EQ(DummyBitmask::Values::WRITE, DummyBitmask(reflectable->find("nested.dummyBitmask")->getUInt8()));
    ASSERT_EQ(1, reflectable->getField("nestedArray")->size());
    ASSERT_EQ(5, reflectable->getField("nestedArray")->at(0)->getField("value")->getUInt32());
    ASSERT_EQ("nestedArray"_sv, reflectable->getField("nestedArray")->at(0)->getField("text")->getString());
    ASSERT_EQ(enumToValue(DummyEnum::TWO),
            reflectable->getField("nestedArray")->at(0)->getField("dummyEnum")->getUInt8());
    ASSERT_EQ(DummyBitmask::Values::READ,
            DummyBitmask(reflectable->getField("nestedArray")->at(0)->getField("dummyBitmask")->getUInt8()));
    ASSERT_EQ(4, reflectable->getField("textArray")->size());
    ASSERT_EQ("this"_sv, reflectable->getField("textArray")->at(0)->getString());
    ASSERT_EQ("is"_sv, reflectable->getField("textArray")->at(1)->getString());
    ASSERT_EQ("text"_sv, reflectable->getField("textArray")->at(2)->getString());
    ASSERT_EQ("array"_sv, reflectable->getField("textArray")->at(3)->getString());
    ASSERT_EQ(1, reflectable->getField("externArray")->size());
    ASSERT_EQ(0x0f, reflectable->getField("externArray")->at(0)->getBitBuffer().getBuffer()[0]);
    ASSERT_EQ(4, reflectable->getField("externArray")->at(0)->getBitBuffer().getBitSize());
    ASSERT_EQ(false, reflectable->getField("optionalBool")->getBool());
    ASSERT_TRUE(reflectable->getField("optionalNested"));
    ASSERT_EQ("optionalNested"_sv, reflectable->find("optionalNested.text")->getString());
}

TEST(ZserioTreeCreator, exceptionsBeforeRoot)
{
    ZserioTreeCreator creator(DummyObject::typeInfo());

    ASSERT_THROW(creator.endRoot(), CppRuntimeException);
    ASSERT_THROW(creator.beginArray("nestedArray"), CppRuntimeException);
    ASSERT_THROW(creator.endArray(), CppRuntimeException);
    ASSERT_THROW(creator.beginCompound("nested"), CppRuntimeException);
    ASSERT_THROW(creator.endCompound(), CppRuntimeException);
    ASSERT_THROW(creator.setValue("value", 13), CppRuntimeException);
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

} // namespace zserio
