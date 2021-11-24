#include "gtest/gtest.h"

#include "with_type_info_code/introspection/Choice.h"

#include "zserio/StringView.h"
#include "zserio/TypeInfoUtil.h"

namespace with_type_info_code
{

namespace introspection
{

using allocator_type = Choice::allocator_type;
template <typename T>
using vector_type = std::vector<T, zserio::RebindAlloc<allocator_type, T>>;
using IIntrospectable = zserio::IBasicIntrospectable<allocator_type>;
using AnyHolder = zserio::AnyHolder<allocator_type>;

using namespace zserio::literals;

class IntrospectionTest : public ::testing::Test
{
protected:
    static Parameterized createParameterized(uint8_t param)
    {
        auto parameterized = Parameterized{vector_type<uint8_t>(5)};
        for (uint8_t i = 0; i < param; ++i)
            parameterized.getArray()[i] = i + 1;
        return parameterized;
    }

    static Struct createStruct()
    {
        return Struct{
            Empty{},
            zserio::NullOpt, // child (auto optional)
            vector_type<Child>{{{0, "zero"}, {1, "one"}, {2, "two"}}},
            5, // param
            createParameterized(5),
            4, // len
            vector_type<uint32_t>(4), // offsets
            vector_type<Parameterized>{{
                createParameterized(5),
                createParameterized(5),
                createParameterized(5),
                createParameterized(5)
            }},
            Bitmask::Values::FLAG1 | Bitmask::Values::FLAG2,
            vector_type<Bitmask>{{Bitmask::Values::FLAG1, Bitmask::Values::FLAG2 | Bitmask::Values::FLAG3}},
            Selector::STRUCT,
            vector_type<SelectorEnum>{{Selector::STRUCT, SelectorEnum::UNION, Selector::BITMASK}},
            32, // dynamicBitField (bit<param>)
            ::zserio::NullOpt // dynamicIntField (param > 4)
        };
    }

    void checkStructIntrospectable(IIntrospectable& introspectable)
    {
        ASSERT_EQ("with_type_info_code.introspection.Struct"_sv, introspectable.getTypeInfo().getSchemaName());

        // Empty empty;
        ASSERT_EQ("with_type_info_code.introspection.Empty"_sv,
                introspectable.getField("empty")->getTypeInfo().getSchemaName());

        // optional Child child;
        ASSERT_EQ(nullptr, introspectable.getField("child")); // non-present optional

        // Child childArray[];
        ASSERT_TRUE(introspectable["childArray"]->isArray());
        ASSERT_EQ(3, introspectable.getField("childArray")->size());

        // uint8 param;
        ASSERT_EQ(5, introspectable.getField("param")->getUInt8());
        ASSERT_EQ(5, introspectable.getField("param")->toUInt());
        ASSERT_EQ("5", introspectable.getField("param")->toString());
        ASSERT_THROW(introspectable.getField("param")->toInt(), zserio::CppRuntimeException);
        ASSERT_THROW(introspectable.getField("param")->getInt8(), zserio::CppRuntimeException);

        // Parameterized(param) parameterized;
        ASSERT_EQ(5, introspectable["parameterized.param"]->getUInt8());
        ASSERT_EQ("5", introspectable.getField("parameterized")->getParameter("param")->toString());
        ASSERT_THROW(introspectable.getField("parameterized")->getField("param"), zserio::CppRuntimeException);
        ASSERT_TRUE(introspectable["parameterized.array"]->isArray());
        ASSERT_EQ(5, introspectable["parameterized.array"]->size());
        ASSERT_EQ(5, introspectable["parameterized.array"]->at(4)->getUInt8());
        ASSERT_EQ(4, (*introspectable["parameterized.array"])[3]->toUInt());
        ASSERT_THROW((*introspectable["parameterized.array"])[4]->toInt(), zserio::CppRuntimeException);

        // varsize len : len > 0 && len < 1000;
        ASSERT_EQ(zserio::SchemaType::VARSIZE, introspectable.getField("len")->getTypeInfo().getSchemaType());
        ASSERT_EQ(zserio::CppType::UINT32, introspectable.getField("len")->getTypeInfo().getCppType());
        ASSERT_EQ(4, introspectable.find("len")->getUInt32());
        ASSERT_EQ(4, introspectable.find("len")->toUInt());
        ASSERT_THROW(introspectable.find("len")->toInt(), zserio::CppRuntimeException);

        // uint32 offsets[len];
        ASSERT_TRUE(introspectable.getField("offsets")->isArray());
        ASSERT_EQ(4, introspectable.getField("offsets")->size());
        ASSERT_NE(0, introspectable.getField("offsets")->at(0)->getUInt32()); // offset shall be initialized

        // Parameterized(param) parameterizedArray[len];
        ASSERT_TRUE(introspectable.getField("parameterizedArray")->isArray());
        ASSERT_EQ(4, introspectable.getField("parameterizedArray")->size());
        ASSERT_EQ("3", introspectable["parameterizedArray"]->at(2)->find("array")->at(2)->toString());

        //Bitmask bitmaskField;
        ASSERT_EQ(Bitmask(Bitmask::Values::FLAG1 | Bitmask::Values::FLAG2).getValue(),
                introspectable.getField("bitmaskField")->getUInt8());
        ASSERT_EQ(Bitmask(Bitmask::Values::FLAG1 | Bitmask::Values::FLAG2).toString(),
                introspectable.getField("bitmaskField")->toString());

        // Bitmask bitmaskArray[];
        ASSERT_TRUE(introspectable.getField("bitmaskArray")->isArray());
        ASSERT_EQ(2, introspectable.getField("bitmaskArray")->size());
        ASSERT_EQ(Bitmask(Bitmask::Values::FLAG1).getValue(), introspectable["bitmaskArray"]->at(0)->toUInt());

        // SelectorEnum enumField;
        ASSERT_EQ(zserio::SchemaType::ENUM, introspectable["enumField"]->getTypeInfo().getSchemaType());
        ASSERT_EQ(zserio::CppType::ENUM, introspectable["enumField"]->getTypeInfo().getCppType());
        ASSERT_EQ(zserio::enumToValue(Selector::STRUCT), introspectable.getField("enumField")->getInt8());
        ASSERT_THROW(introspectable.getField("enumField")->toUInt(), zserio::CppRuntimeException);
        ASSERT_EQ(zserio::enumToString(Selector::STRUCT), introspectable.getField("enumField")->toString());

        // Selector enumArray[];
        ASSERT_EQ(zserio::SchemaType::ENUM, introspectable["enumArray"]->getTypeInfo().getSchemaType());
        auto enumArrayFieldInfo = introspectable.getTypeInfo().getFields()[11];
        ASSERT_EQ("enumArray"_sv, enumArrayFieldInfo.schemaName);
        ASSERT_TRUE(enumArrayFieldInfo.isArray);
        ASSERT_EQ(""_sv, enumArrayFieldInfo.arrayLength);
        ASSERT_TRUE(enumArrayFieldInfo.isPacked);
        ASSERT_FALSE(enumArrayFieldInfo.isImplicit);
        ASSERT_TRUE(introspectable["enumArray"]->isArray());
        ASSERT_EQ(zserio::enumToValue(Selector::STRUCT), introspectable["enumArray"]->at(0)->getInt8());

        // bit<param> dynamicBitField if param < 64;
        ASSERT_EQ(32, introspectable["dynamicBitField"]->toUInt());
        ASSERT_EQ(32, introspectable["dynamicBitField"]->getUInt64());
        ASSERT_THROW(introspectable["dynamicBitField"]->getUInt8(), zserio::CppRuntimeException);

        // int<param> dynamicIntField if param < 4;
        ASSERT_EQ(nullptr, introspectable.getField("dynamicIntField"));
        ASSERT_THROW(introspectable.getParameter("dynamicIntField"), zserio::CppRuntimeException);

        // function Selector getEnumField()
        ASSERT_EQ(zserio::enumToString(SelectorEnum::STRUCT),
                introspectable.callFunction("getEnumField")->toString());
        ASSERT_EQ(zserio::enumToValue(SelectorEnum::STRUCT), introspectable["getEnumField"]->toInt());

        // setField tests
        introspectable.setField("enumField", AnyHolder(Selector::UNION));
        ASSERT_EQ(zserio::enumToString(Selector::UNION), introspectable["enumField"]->toString());
        // - wrong type in any holder
        ASSERT_THROW(introspectable.setField("bitmaskField", AnyHolder(Selector::UNION)),
                zserio::CppRuntimeException);
        // - field does not exist
        ASSERT_THROW(introspectable.setField("nonexistent", AnyHolder(0)),
                zserio::CppRuntimeException);
        // - optional child
        introspectable.setField("child", AnyHolder(Child{13, "thirteen"}));
        ASSERT_TRUE(introspectable.getField("child"));
        ASSERT_EQ("thirteen", introspectable["child.name"]->getString());
    }
};

TEST_F(IntrospectionTest, checkChoiceWithStructure)
{
    Choice choice;
    choice.setStructField(createStruct());
    choice.initialize(Selector::STRUCT);
    choice.initializeOffsets(0);

    auto introspectable = choice.introspectable();
    ASSERT_EQ("with_type_info_code.introspection.Choice"_sv, introspectable->getTypeInfo().getSchemaName());

    ASSERT_EQ(zserio::enumToValue(SelectorEnum::STRUCT),
            introspectable->getParameter("selector")->getInt8());
    ASSERT_EQ(zserio::enumToValue(SelectorEnum::STRUCT), introspectable->getParameter("selector"_sv)->toInt());
    ASSERT_EQ(zserio::enumToString(SelectorEnum::STRUCT), introspectable->find("selector"_sv)->toString());

    ASSERT_TRUE(zserio::TypeInfoUtil::hasChoice(introspectable->getTypeInfo().getCppType()));
    ASSERT_EQ("structField"_sv, introspectable->getChoice());

    auto structIntrospectable = introspectable->getField(introspectable->getChoice());
    ASSERT_TRUE(structIntrospectable);
    ASSERT_EQ("with_type_info_code.introspection.Struct"_sv,
            structIntrospectable->getTypeInfo().getSchemaName());

    // existing array element somewhere within the structField
    ASSERT_EQ(1, (*introspectable)["structField.parameterized.array"]->at(0)->toUInt());
    ASSERT_EQ(3,
            introspectable->find("structField.parameterizedArray")->at(1)->find("array")->at(2)->getUInt8());

    // function on structField
    ASSERT_EQ(zserio::enumToValue(Selector::STRUCT),
            introspectable->find("structField.getEnumField")->getInt8());

    ASSERT_EQ("zero", introspectable->callFunction("getFirstChildName")->getString());
    ASSERT_EQ("zero", introspectable->find("getFirstChildName")->toString());

    checkStructIntrospectable(*structIntrospectable);

    // check child set by checkStructIntrospectable
    ASSERT_EQ("thirteen", (*introspectable)["structField.child.name"]->getString());

    // set field within child
    structIntrospectable->setField("dynamicBitField", AnyHolder(static_cast<uint64_t>(13)));
    ASSERT_EQ(13, introspectable->find("structField.dynamicBitField")->toUInt());
}

TEST_F(IntrospectionTest, checkChoiceWithUnion)
{
    Choice choice;
    Union unionField;
    unionField.setStructField(createStruct());
    choice.setUnionField(unionField);
    choice.initialize(Selector::UNION);
    choice.initializeOffsets(0);

    auto introspectable = choice.introspectable();
    ASSERT_EQ("with_type_info_code.introspection.Choice"_sv, introspectable->getTypeInfo().getSchemaName());

    ASSERT_EQ(zserio::enumToValue(SelectorEnum::UNION), introspectable->getParameter("selector")->getInt8());
    ASSERT_EQ(zserio::enumToValue(SelectorEnum::UNION), introspectable->getParameter("selector")->toInt());
    ASSERT_EQ(zserio::enumToString(SelectorEnum::UNION), introspectable->find("selector")->toString());

    ASSERT_TRUE(zserio::TypeInfoUtil::hasChoice(introspectable->getTypeInfo().getCppType()));
    ASSERT_EQ("unionField"_sv, introspectable->getChoice());

    ASSERT_EQ("with_type_info_code.introspection.Union"_sv,
            introspectable->getField(introspectable->getChoice())->getTypeInfo().getSchemaName());

    // non-present choices
    ASSERT_EQ(nullptr, introspectable->find("unionField.childArray"));
    ASSERT_EQ(nullptr, introspectable->find("unionField.parameterized"));
    ASSERT_EQ(nullptr, introspectable->find("unionField.bitmaskField"));
    auto unionIntrospectable = introspectable->getField("unionField");
    ASSERT_TRUE(unionIntrospectable);
    ASSERT_THROW(unionIntrospectable->getField("childArray"), zserio::CppRuntimeException);

    ASSERT_EQ(5, introspectable->find("unionField.getStructFieldParam")->toUInt());
    ASSERT_EQ(5, introspectable->getField("unionField")->callFunction("getStructFieldParam")->getUInt8());

    auto structIntrospectable = introspectable->find("unionField.structField");
    ASSERT_TRUE(structIntrospectable);
    checkStructIntrospectable(*structIntrospectable);
}

TEST_F(IntrospectionTest, checkChoiceWithBitmask)
{
    Choice choice;
    choice.setBitmaskField(Bitmask::Values::FLAG3);
    choice.initialize(Selector::BITMASK);

    auto introspectable = choice.introspectable();
    ASSERT_EQ("with_type_info_code.introspection.Choice"_sv, introspectable->getTypeInfo().getSchemaName());

    ASSERT_EQ(zserio::enumToValue(SelectorEnum::BITMASK), introspectable->getParameter("selector")->getInt8());
    ASSERT_EQ(zserio::enumToValue(SelectorEnum::BITMASK), introspectable->getParameter("selector")->toInt());
    ASSERT_EQ(zserio::enumToString(SelectorEnum::BITMASK), introspectable->find("selector")->toString());

    ASSERT_TRUE(zserio::TypeInfoUtil::hasChoice(introspectable->getTypeInfo().getCppType()));
    ASSERT_EQ("bitmaskField"_sv, introspectable->getChoice());

    // exact getter
    ASSERT_EQ(Bitmask(Bitmask::Values::FLAG3).getValue(), introspectable->find("bitmaskField")->getUInt8());
    // conversion to unsigned int
    ASSERT_EQ(Bitmask(Bitmask::Values::FLAG3).getValue(),
            introspectable->getField(introspectable->getChoice())->toUInt());
    // conversion to string
    ASSERT_EQ(Bitmask(Bitmask::Values::FLAG3).toString(), (*introspectable)["bitmaskField"]->toString());

    // non-present choices
    ASSERT_EQ(nullptr, introspectable->find("structField"));
    ASSERT_EQ(nullptr, introspectable->find("unionField"));

    // nonexistent
    ASSERT_EQ(nullptr, (*introspectable)["nonexistent.something"]);
    ASSERT_THROW(introspectable->getField("nonexistent"), zserio::CppRuntimeException);
    ASSERT_THROW(introspectable->getParameter("nonexistent"), zserio::CppRuntimeException);
    ASSERT_THROW(introspectable->callFunction("nonexistent"), zserio::CppRuntimeException);

    // invalid paths
    ASSERT_EQ(nullptr, introspectable->find("invalid..something"));
    ASSERT_EQ(nullptr, introspectable->find("."));
    ASSERT_EQ(nullptr, introspectable->find(".."));
    ASSERT_EQ(nullptr, introspectable->find(".something"));
    ASSERT_EQ(nullptr, introspectable->find("..something"));
    ASSERT_EQ(nullptr, introspectable->find("invalid."));
    ASSERT_EQ(nullptr, introspectable->find("invalid.."));

    // call function which throws via find shall return nullptr
    ASSERT_EQ(nullptr, introspectable->find("getBitmaskFromUnion"));
    // call function via callFunction method throws
    ASSERT_THROW(introspectable->callFunction("getBitmaskFromUnion"), zserio::CppRuntimeException);
}

} // namespace introspection

} // namespace with_type_info_code
