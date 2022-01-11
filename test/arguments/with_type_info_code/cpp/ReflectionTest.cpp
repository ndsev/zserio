#include "gtest/gtest.h"

#include "with_type_info_code/reflection/Choice.h"

#include "zserio/StringView.h"
#include "zserio/TypeInfoUtil.h"

using namespace zserio::literals;

namespace with_type_info_code
{

namespace reflection
{

using allocator_type = Choice::allocator_type;
using string_type = zserio::string<zserio::RebindAlloc<allocator_type, char>>;
template <typename T>
using vector_type = std::vector<T, zserio::RebindAlloc<allocator_type, T>>;
using IReflectable = zserio::IBasicReflectable<allocator_type>;
using AnyHolder = zserio::AnyHolder<allocator_type>;
using BitBuffer = zserio::BasicBitBuffer<allocator_type>;

class ReflectionTest : public ::testing::Test
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
            vector_type<Child>{{
                {0, "zero", false, zserio::NullOpt},
                {1, "one", true, vector_type<string_type>{{{"best"}, {"first"}}}},
                {2, "two", false, zserio::NullOpt}
            }},
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
            static_cast<uint64_t>(31), // dynamicBitField (bit<param>)
            vector_type<uint64_t>{{10, 20, 30}},
            ::zserio::NullOpt, // dynamicIntField (param > 4)
            vector_type<int8_t>{{-3, -1, 1, 3}},
            vector_type<bool>{{true, false, true}},
            BitBuffer{{0xAB, 0xCD}, 16},
            vector_type<BitBuffer>{BitBuffer{{0x02}, 2}, BitBuffer{{0x01}, 1}}
        };
    }

    void checkWriteThrows(IReflectable& reflectable)
    {
        zserio::BitBuffer bitBuffer;
        zserio::BitStreamWriter writer(bitBuffer);
        ASSERT_THROW(reflectable.write(writer), zserio::CppRuntimeException);
    }

    // for builtin types
    template <typename CHECKER>
    void checkWriteReadBuiltin(IReflectable& reflectable, const CHECKER& checker)
    {
        zserio::BitBuffer bitBuffer(1024 * 8);
        zserio::BitStreamWriter writer(bitBuffer);
        reflectable.write(writer);
        const size_t bitSizeOf = reflectable.bitSizeOf();
        ASSERT_EQ(bitSizeOf, writer.getBitPosition());

        zserio::BitStreamReader reader(bitBuffer);
        checker(reader);
        ASSERT_EQ(bitSizeOf, reader.getBitPosition());
    }

    template <typename T, typename ...ARGS>
    void checkWriteRead(IReflectable& reflectable, const T& originalObject, ARGS... args)
    {
        zserio::BitBuffer bitBuffer(1024 * 8);
        zserio::BitStreamWriter writer(bitBuffer);
        reflectable.write(writer);
        const size_t bitSizeOf = reflectable.bitSizeOf();
        ASSERT_EQ(bitSizeOf, writer.getBitPosition());

        zserio::BitStreamReader reader(bitBuffer);
        const T readObject{reader, args...};
        ASSERT_EQ(originalObject, readObject);
        ASSERT_EQ(bitSizeOf, reader.getBitPosition());
    }

    template <typename T, typename std::enable_if<std::is_enum<T>::value, int>::type = 0>
    void checkWriteReadEnum(IReflectable& reflectable, T enumValue)
    {
        zserio::BitBuffer bitBuffer(64);
        zserio::BitStreamWriter writer(bitBuffer);
        reflectable.write(writer);
        const size_t bitSizeOf = reflectable.bitSizeOf();
        ASSERT_EQ(bitSizeOf, writer.getBitPosition());

        zserio::BitStreamReader reader(bitBuffer);
        ASSERT_EQ(enumValue, zserio::read<T>(reader));
        ASSERT_EQ(bitSizeOf, reader.getBitPosition());
    }

    void checkStructReflectable(IReflectable& reflectable, const Struct& structure)
    {
        ASSERT_EQ("with_type_info_code.reflection.Struct"_sv, reflectable.getTypeInfo().getSchemaName());

        // Empty empty;
        ASSERT_EQ("with_type_info_code.reflection.Empty"_sv,
                reflectable.getField("empty")->getTypeInfo().getSchemaName());

        // optional Child child;
        ASSERT_EQ(nullptr, reflectable.getField("child")); // non-present optional

        // Child childArray[];
        ASSERT_TRUE(reflectable["childArray"]->isArray());
        ASSERT_EQ(3, reflectable.getField("childArray")->size());
        ASSERT_FALSE(reflectable["childArray"]->at(0)->find("hasNicknames")->getBool());
        ASSERT_FALSE(reflectable["childArray"]->at(0)->find("nicknames"));
        ASSERT_TRUE(reflectable["childArray"]->at(1)->find("hasNicknames")->getBool());
        ASSERT_EQ("true", reflectable["childArray"]->at(1)->find("hasNicknames")->toString());
        ASSERT_TRUE(reflectable["childArray"]->at(1)->find("nicknames")->isArray());
        ASSERT_EQ(2, reflectable["childArray"]->at(1)->find("nicknames")->size());
        ASSERT_EQ("first", reflectable["childArray"]->at(1)->find("nicknames")->at(1)->toString());
        checkWriteThrows(*(reflectable["childArray"]));
        checkWriteRead(*(reflectable["childArray"]->at(1)), structure.getChildArray()[1]);
        checkWriteReadBuiltin(*(reflectable["childArray"]->at(1)->getField("name")),
                [&structure](zserio::BitStreamReader& reader) {
                    ASSERT_EQ(structure.getChildArray()[1].getName(), reader.readString<allocator_type>());
                }
        );
        checkWriteThrows(*(reflectable["childArray"]->at(1)->getField("nicknames")));
        checkWriteReadBuiltin(*(reflectable["childArray"]->at(1)->getField("nicknames")->at(1)),
                [&structure](zserio::BitStreamReader& reader) {
                    ASSERT_EQ(structure.getChildArray()[1].getNicknames()[1],
                            reader.readString<allocator_type>());
                }
        );

        // uint8 param;
        ASSERT_EQ(5, reflectable.getField("param")->getUInt8());
        ASSERT_EQ(5, reflectable.getField("param")->toUInt());
        ASSERT_EQ("5", reflectable.getField("param")->toString());
        ASSERT_THROW(reflectable.getField("param")->toInt(), zserio::CppRuntimeException);
        ASSERT_THROW(reflectable.getField("param")->getInt8(), zserio::CppRuntimeException);
        checkWriteReadBuiltin(*(reflectable.getField("param")),
                [&structure](zserio::BitStreamReader& reader) {
                    ASSERT_EQ(structure.getParam(), reader.readBits(8));
                }
        );

        // Parameterized(param) parameterized;
        ASSERT_EQ(5, reflectable["parameterized.param"]->getUInt8());
        ASSERT_EQ("5", reflectable.getField("parameterized")->getParameter("param")->toString());
        ASSERT_THROW(reflectable.getField("parameterized")->getField("param"), zserio::CppRuntimeException);
        ASSERT_TRUE(reflectable["parameterized.array"]->isArray());
        ASSERT_EQ(5, reflectable["parameterized.array"]->size());
        ASSERT_EQ(5, reflectable["parameterized.array"]->at(4)->getUInt8());
        ASSERT_EQ(4, (*(reflectable["parameterized.array"]))[3]->toUInt());
        ASSERT_THROW((*(reflectable["parameterized.array"]))[4]->toInt(), zserio::CppRuntimeException);
        checkWriteThrows(*(reflectable["parameterized.array"]));
        checkWriteRead(*(reflectable["parameterized"]), structure.getParameterized(), structure.getParam());

        // varsize len : len > 0 && len < 1000;
        ASSERT_EQ(zserio::SchemaType::VARSIZE, reflectable.getField("len")->getTypeInfo().getSchemaType());
        ASSERT_EQ(zserio::CppType::UINT32, reflectable.getField("len")->getTypeInfo().getCppType());
        ASSERT_EQ(4, reflectable.find("len")->getUInt32());
        ASSERT_EQ(4, reflectable.find("len")->toUInt());
        ASSERT_EQ(4.0, reflectable.find("len")->toDouble());
        ASSERT_THROW(reflectable.find("len")->toInt(), zserio::CppRuntimeException);
        checkWriteReadBuiltin(*(reflectable.find("len")),
                [&structure](zserio::BitStreamReader& reader) {
                    ASSERT_EQ(structure.getLen(), reader.readVarSize());
                }
        );

        // uint32 offsets[len];
        ASSERT_TRUE(reflectable.getField("offsets")->isArray());
        ASSERT_EQ(4, reflectable.getField("offsets")->size());
        ASSERT_NE(0, reflectable.getField("offsets")->at(0)->getUInt32()); // offset shall be initialized
        ASSERT_NE(0, reflectable.getField("offsets")->at(0)->toDouble());
        checkWriteThrows(*(reflectable.getField("offsets")));
        checkWriteReadBuiltin(*(reflectable.getField("offsets")->at(0)),
                [&structure](zserio::BitStreamReader& reader) {
                    ASSERT_EQ(structure.getOffsets()[0], reader.readBits(32));
                }
        );

        // Parameterized(param) parameterizedArray[len];
        ASSERT_TRUE(reflectable.getField("parameterizedArray")->isArray());
        ASSERT_EQ(4, reflectable.getField("parameterizedArray")->size());
        ASSERT_EQ("3", reflectable["parameterizedArray"]->at(2)->find("array")->at(2)->toString());
        checkWriteThrows(*(reflectable["parameterizedArray"]));
        checkWriteRead(*(reflectable["parameterizedArray"]->at(2)),
                structure.getParameterizedArray()[2], structure.getParam());

        // Bitmask bitmaskField;
        ASSERT_EQ(Bitmask(Bitmask::Values::FLAG1 | Bitmask::Values::FLAG2).getValue(),
                reflectable.getField("bitmaskField")->getUInt8());
        ASSERT_EQ(Bitmask(Bitmask::Values::FLAG1 | Bitmask::Values::FLAG2).getValue(),
                reflectable.getField("bitmaskField")->toDouble());
        ASSERT_EQ(Bitmask(Bitmask::Values::FLAG1 | Bitmask::Values::FLAG2).toString(),
                reflectable.getField("bitmaskField")->toString());
        checkWriteRead(*(reflectable.getField("bitmaskField")), structure.getBitmaskField());

        // Bitmask bitmaskArray[];
        ASSERT_TRUE(reflectable.getField("bitmaskArray")->isArray());
        ASSERT_EQ(2, reflectable.getField("bitmaskArray")->size());
        ASSERT_EQ(Bitmask(Bitmask::Values::FLAG1).getValue(), reflectable["bitmaskArray"]->at(0)->toUInt());
        checkWriteThrows(*(reflectable["bitmaskArray"]));
        checkWriteRead(*(reflectable["bitmaskArray"]->at(0)), structure.getBitmaskArray()[0]);

        // SelectorEnum enumField;
        ASSERT_EQ(zserio::SchemaType::ENUM, reflectable["enumField"]->getTypeInfo().getSchemaType());
        ASSERT_EQ(zserio::CppType::ENUM, reflectable["enumField"]->getTypeInfo().getCppType());
        ASSERT_EQ(zserio::enumToValue(Selector::STRUCT), reflectable.getField("enumField")->getInt8());
        ASSERT_THROW(reflectable.getField("enumField")->toUInt(), zserio::CppRuntimeException);
        ASSERT_EQ(zserio::enumToString(Selector::STRUCT), reflectable.getField("enumField")->toString());
        checkWriteReadEnum(*(reflectable.getField("enumField")), structure.getEnumField());

        // Selector enumArray[];
        ASSERT_EQ(zserio::SchemaType::ENUM, reflectable["enumArray"]->getTypeInfo().getSchemaType());
        auto enumArrayFieldInfo = reflectable.getTypeInfo().getFields()[11];
        ASSERT_EQ("enumArray"_sv, enumArrayFieldInfo.schemaName);
        ASSERT_TRUE(enumArrayFieldInfo.isArray);
        ASSERT_EQ(""_sv, enumArrayFieldInfo.arrayLength);
        ASSERT_TRUE(enumArrayFieldInfo.isPacked);
        ASSERT_FALSE(enumArrayFieldInfo.isImplicit);
        ASSERT_TRUE(reflectable["enumArray"]->isArray());
        ASSERT_EQ(zserio::enumToValue(Selector::STRUCT), reflectable["enumArray"]->at(0)->getInt8());
        ASSERT_EQ(zserio::enumToValue(Selector::STRUCT), reflectable["enumArray"]->at(0)->toDouble());
        checkWriteThrows(*(reflectable.getField("enumArray")));
        checkWriteReadEnum(*(reflectable.getField("enumArray")->at(0)), structure.getEnumArray()[0]);

        // bit<param> dynamicBitField if param < 64;
        ASSERT_EQ(31, reflectable["dynamicBitField"]->toUInt());
        ASSERT_EQ(31, reflectable["dynamicBitField"]->getUInt64());
        ASSERT_THROW(reflectable["dynamicBitField"]->getUInt8(), zserio::CppRuntimeException);
        checkWriteReadBuiltin(*(reflectable["dynamicBitField"]),
                [&structure](zserio::BitStreamReader& reader) {
                    ASSERT_EQ(structure.getDynamicBitField(), reader.readBits64(structure.getParam()));
                }
        );

        // bit<param> dynamicBitFieldArray[];
        ASSERT_TRUE(reflectable.getField("dynamicBitFieldArray")->isArray());
        ASSERT_EQ(3, reflectable.getField("dynamicBitFieldArray")->size());
        ASSERT_EQ(10, reflectable["dynamicBitFieldArray"]->at(0)->getUInt64());
        ASSERT_EQ(20, reflectable["dynamicBitFieldArray"]->at(1)->toUInt());
        ASSERT_EQ(20.0, reflectable["dynamicBitFieldArray"]->at(1)->toDouble());
        ASSERT_EQ("30", reflectable["dynamicBitFieldArray"]->at(2)->toString());
        checkWriteThrows(*(reflectable["dynamicBitFieldArray"]));
        checkWriteReadBuiltin(*(reflectable["dynamicBitFieldArray"]->at(2)),
                [&structure](zserio::BitStreamReader& reader) {
                    ASSERT_EQ(structure.getDynamicBitFieldArray()[2], reader.readBits64(structure.getParam()));
                }
        );

        // int<param> dynamicIntField if param < 4;
        ASSERT_EQ(nullptr, reflectable.getField("dynamicIntField"));
        ASSERT_THROW(reflectable.getParameter("dynamicIntField"), zserio::CppRuntimeException);

        // int<4> dynamicIntFieldArray[4] if param < 64;
        ASSERT_TRUE(reflectable.getField("dynamicIntFieldArray")->isArray());
        ASSERT_EQ(4, reflectable.getField("dynamicIntFieldArray")->size());
        ASSERT_THROW(reflectable["dynamicIntFieldArray"]->at(0)->toUInt(), zserio::CppRuntimeException);
        ASSERT_EQ(-3, reflectable["dynamicIntFieldArray"]->at(0)->toInt());
        ASSERT_EQ(-1, reflectable["dynamicIntFieldArray"]->at(1)->getInt8());
        ASSERT_EQ(-1.0, reflectable["dynamicIntFieldArray"]->at(1)->toDouble());
        ASSERT_EQ("-1", reflectable["dynamicIntFieldArray"]->at(1)->toString());
        ASSERT_EQ(1, reflectable["dynamicIntFieldArray"]->at(2)->getInt8());
        checkWriteThrows(*(reflectable["dynamicIntFieldArray"]));
        checkWriteReadBuiltin(*(reflectable["dynamicIntFieldArray"]->at(0)),
                [&structure](zserio::BitStreamReader& reader) {
                    ASSERT_EQ(structure.getDynamicIntFieldArray()[0], reader.readSignedBits(4));
                }
        );

        // bool boolArray[];
        ASSERT_TRUE(reflectable["boolArray"]->isArray());
        ASSERT_EQ(3, reflectable["boolArray"]->size());
        ASSERT_FALSE(reflectable["boolArray"]->at(1)->getBool());
        ASSERT_EQ(0.0, reflectable["boolArray"]->at(1)->toDouble());
        ASSERT_EQ("false", reflectable["boolArray"]->at(1)->toString());
        checkWriteThrows(*(reflectable["boolArray"]));
        checkWriteReadBuiltin(*(reflectable["boolArray"]->at(0)),
                [&structure](zserio::BitStreamReader& reader) {
                    ASSERT_EQ(structure.getBoolArray()[0], reader.readBool());
                }
        );

        // extern externField;
        ASSERT_EQ(16, reflectable.getField("externField")->getBitBuffer().getBitSize());
        ASSERT_EQ(0xAB, reflectable.getField("externField")->getBitBuffer().getBuffer()[0]);
        ASSERT_EQ(0xCD, reflectable.getField("externField")->getBitBuffer().getBuffer()[1]);
        checkWriteReadBuiltin(*(reflectable.getField("externField")),
                [&structure](zserio::BitStreamReader& reader) {
                    ASSERT_EQ(structure.getExternField(), reader.readBitBuffer<allocator_type>());
                }
        );

        // extern externArray[];
        ASSERT_TRUE(reflectable["externArray"]->isArray());
        ASSERT_EQ(2, reflectable["externArray"]->size());
        ASSERT_EQ(2, reflectable["externArray"]->at(0)->getBitBuffer().getBitSize());
        ASSERT_EQ(0x02, reflectable["externArray"]->at(0)->getBitBuffer().getBuffer()[0]);
        ASSERT_EQ(1, reflectable["externArray"]->at(1)->getBitBuffer().getBitSize());
        ASSERT_EQ(0x01, reflectable["externArray"]->at(1)->getBitBuffer().getBuffer()[0]);
        checkWriteThrows(*(reflectable["externArray"]));
        checkWriteReadBuiltin(*(reflectable.getField("externArray")->at(0)),
                [&structure](zserio::BitStreamReader& reader) {
                    ASSERT_EQ(structure.getExternArray()[0], reader.readBitBuffer<allocator_type>());
                }
        );

        // function Selector getEnumField()
        ASSERT_EQ(zserio::enumToString(SelectorEnum::STRUCT),
                reflectable.callFunction("getEnumField")->toString());
        ASSERT_EQ(zserio::enumToValue(SelectorEnum::STRUCT), reflectable["getEnumField"]->toInt());

        // setField tests
        reflectable.setField("enumField", AnyHolder(Selector::UNION));
        ASSERT_EQ(zserio::enumToString(Selector::UNION), reflectable["enumField"]->toString());
        // - wrong type in any holder
        ASSERT_THROW(reflectable.setField("bitmaskField", AnyHolder(Selector::UNION)),
                zserio::CppRuntimeException);
        // - field does not exist
        ASSERT_THROW(reflectable.setField("nonexistent", AnyHolder(0)),
                zserio::CppRuntimeException);
        // - optional child
        reflectable.setField("child", AnyHolder(Child{13, "thirteen", false, zserio::NullOpt}));
        ASSERT_TRUE(reflectable.getField("child"));
        ASSERT_EQ("thirteen"_sv, reflectable["child.name"]->getString());
        ASSERT_FALSE(reflectable["child.hasNicknames"]->getBool());

        // write read check on structure
        checkWriteRead(reflectable, structure);
    }
};

TEST_F(ReflectionTest, checkChoiceWithStructure)
{
    Choice choice;
    choice.setStructField(createStruct());
    choice.initialize(Selector::STRUCT);
    choice.initializeOffsets(0);

    auto reflectable = choice.reflectable();
    ASSERT_EQ("with_type_info_code.reflection.Choice"_sv, reflectable->getTypeInfo().getSchemaName());

    ASSERT_EQ(zserio::enumToValue(SelectorEnum::STRUCT),
            reflectable->getParameter("selector")->getInt8());
    ASSERT_EQ(zserio::enumToValue(SelectorEnum::STRUCT), reflectable->getParameter("selector")->toInt());
    ASSERT_EQ(zserio::enumToString(SelectorEnum::STRUCT), reflectable->find("selector")->toString());

    ASSERT_TRUE(zserio::TypeInfoUtil::hasChoice(reflectable->getTypeInfo().getCppType()));
    ASSERT_EQ("structField"_sv, reflectable->getChoice());

    auto structReflectable = reflectable->getField(reflectable->getChoice());
    ASSERT_TRUE(structReflectable);
    ASSERT_EQ("with_type_info_code.reflection.Struct"_sv,
            structReflectable->getTypeInfo().getSchemaName());

    // existing array element somewhere within the structField
    ASSERT_EQ(1, (*reflectable)["structField.parameterized.array"]->at(0)->toUInt());
    ASSERT_EQ(3,
            reflectable->find("structField.parameterizedArray")->at(1)->find("array")->at(2)->getUInt8());

    // function on structField
    ASSERT_EQ(zserio::enumToValue(Selector::STRUCT),
            reflectable->find("structField.getEnumField")->getInt8());

    ASSERT_EQ("zero"_sv, reflectable->callFunction("getFirstChildName")->getString());
    ASSERT_EQ("zero", reflectable->find("getFirstChildName")->toString());

    checkStructReflectable(*structReflectable, choice.getStructField());

    // check child set by checkStructReflectable
    ASSERT_EQ("thirteen"_sv, (*reflectable)["structField.child.name"]->getString());

    // set field within child
    structReflectable->setField("dynamicBitField", AnyHolder(static_cast<uint64_t>(13)));
    ASSERT_EQ(13, reflectable->find("structField.dynamicBitField")->toUInt());

    // write read check on choice
    checkWriteRead(*reflectable, choice, Selector::STRUCT);
}

TEST_F(ReflectionTest, checkChoiceWithUnion)
{
    Choice choice;
    Union unionField;
    unionField.setStructField(createStruct());
    choice.setUnionField(unionField);
    choice.initialize(Selector::UNION);
    choice.initializeOffsets(0);

    auto reflectable = choice.reflectable();
    ASSERT_EQ("with_type_info_code.reflection.Choice"_sv, reflectable->getTypeInfo().getSchemaName());

    ASSERT_EQ(zserio::enumToValue(SelectorEnum::UNION), reflectable->getParameter("selector")->getInt8());
    ASSERT_EQ(zserio::enumToValue(SelectorEnum::UNION), reflectable->getParameter("selector")->toInt());
    ASSERT_EQ(zserio::enumToString(SelectorEnum::UNION), reflectable->find("selector")->toString());

    ASSERT_TRUE(zserio::TypeInfoUtil::hasChoice(reflectable->getTypeInfo().getCppType()));
    ASSERT_EQ("unionField"_sv, reflectable->getChoice());

    ASSERT_EQ("with_type_info_code.reflection.Union"_sv,
            reflectable->getField(reflectable->getChoice())->getTypeInfo().getSchemaName());

    // non-present choices
    ASSERT_EQ(nullptr, reflectable->find("unionField.childArray"));
    ASSERT_EQ(nullptr, reflectable->find("unionField.parameterized"));
    ASSERT_EQ(nullptr, reflectable->find("unionField.bitmaskField"));
    auto unionReflectable = reflectable->getField("unionField");
    ASSERT_TRUE(unionReflectable);
    ASSERT_THROW(unionReflectable->getField("childArray"), zserio::CppRuntimeException);

    ASSERT_EQ(5, reflectable->find("unionField.getStructFieldParam")->toUInt());
    ASSERT_EQ(5, reflectable->getField("unionField")->callFunction("getStructFieldParam")->getUInt8());

    auto structReflectable = reflectable->find("unionField.structField");
    ASSERT_TRUE(structReflectable);
    checkStructReflectable(*structReflectable, choice.getUnionField().getStructField());

    // write read check on union
    checkWriteRead(*(reflectable->getField("unionField")), choice.getUnionField());

    // write read check on choice
    checkWriteRead(*reflectable, choice, Selector::UNION);
}

TEST_F(ReflectionTest, checkChoiceWithBitmask)
{
    Choice choice;
    choice.setBitmaskField(Bitmask::Values::FLAG3);
    choice.initialize(Selector::BITMASK);

    auto reflectable = choice.reflectable();
    ASSERT_EQ("with_type_info_code.reflection.Choice"_sv, reflectable->getTypeInfo().getSchemaName());

    ASSERT_EQ(zserio::enumToValue(SelectorEnum::BITMASK), reflectable->getParameter("selector")->getInt8());
    ASSERT_EQ(zserio::enumToValue(SelectorEnum::BITMASK), reflectable->getParameter("selector")->toInt());
    ASSERT_EQ(zserio::enumToValue(SelectorEnum::BITMASK), reflectable->getParameter("selector")->toDouble());
    ASSERT_EQ(zserio::enumToString(SelectorEnum::BITMASK), reflectable->find("selector")->toString());

    ASSERT_TRUE(zserio::TypeInfoUtil::hasChoice(reflectable->getTypeInfo().getCppType()));
    ASSERT_EQ("bitmaskField"_sv, reflectable->getChoice());

    // exact getter
    ASSERT_EQ(Bitmask(Bitmask::Values::FLAG3).getValue(), reflectable->find("bitmaskField")->getUInt8());
    // conversion to unsigned int
    ASSERT_EQ(Bitmask(Bitmask::Values::FLAG3).getValue(),
            reflectable->getField(reflectable->getChoice())->toUInt());
    // conversion to string
    ASSERT_EQ(Bitmask(Bitmask::Values::FLAG3).toString(), (*reflectable)["bitmaskField"]->toString());

    // non-present choices
    ASSERT_EQ(nullptr, reflectable->find("structField"));
    ASSERT_EQ(nullptr, reflectable->find("unionField"));

    // nonexistent
    ASSERT_EQ(nullptr, (*reflectable)["nonexistent.something"]);
    ASSERT_THROW(reflectable->getField("nonexistent"), zserio::CppRuntimeException);
    ASSERT_THROW(reflectable->getParameter("nonexistent"), zserio::CppRuntimeException);
    ASSERT_THROW(reflectable->callFunction("nonexistent"), zserio::CppRuntimeException);

    // invalid paths
    ASSERT_EQ(nullptr, reflectable->find("invalid..something"));
    ASSERT_EQ(nullptr, reflectable->find("."));
    ASSERT_EQ(nullptr, reflectable->find(".."));
    ASSERT_EQ(nullptr, reflectable->find(".something"));
    ASSERT_EQ(nullptr, reflectable->find("..something"));
    ASSERT_EQ(nullptr, reflectable->find("invalid."));
    ASSERT_EQ(nullptr, reflectable->find("invalid.."));

    // call function which throws via find shall return nullptr
    ASSERT_EQ(nullptr, reflectable->find("getBitmaskFromUnion"));
    // call function via callFunction method throws
    ASSERT_THROW(reflectable->callFunction("getBitmaskFromUnion"), zserio::CppRuntimeException);

    // write read check on bitmask
    checkWriteRead(*((*reflectable)["bitmaskField"]), choice.getBitmaskField());

    // write read check on choice
    checkWriteRead(*reflectable, choice, Selector::BITMASK);
}

} // namespace reflection

} // namespace with_type_info_code
