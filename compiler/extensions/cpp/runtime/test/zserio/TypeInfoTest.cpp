#include <string>

#include "gtest/gtest.h"

#include "zserio/TypeInfo.h"

using namespace zserio::literals;

namespace zserio
{

namespace
{

class RecursiveObject
{
public:
    static const ITypeInfo& typeInfo()
    {
        static const RecursiveTypeInfo<std::allocator<uint8_t>> recursiveTypeInfo(&RecursiveObject::typeInfo);
        static const std::array<FieldInfo, 1> fields = {
            FieldInfo{
                "recursive"_sv,
                recursiveTypeInfo,
                {},
                {},
                {},
                {},
                true, // isOptional
                {},
                {},
                false,
                {},
                false,
                false
            }
        };

        static const StructTypeInfo<std::allocator<uint8_t>> structTypeInfo(
            "RecursiveObject"_sv, nullptr, ""_sv, {}, fields, {}, {}
        );
        return structTypeInfo;
    }
};

} // namespace

class TypeInfoTest : public ::testing::Test
{
protected:
    void checkBuiltinTypeInfo(const ITypeInfo& typeInfo, StringView schemaName, SchemaType schemaType,
            CppType cppType, uint8_t bitSize = 0)
    {
        ASSERT_EQ(schemaName, typeInfo.getSchemaName());
        ASSERT_EQ(schemaType, typeInfo.getSchemaType());
        ASSERT_EQ(cppType, typeInfo.getCppType());
        if (bitSize > 0)
            ASSERT_EQ(bitSize, typeInfo.getBitSize());
        else
            ASSERT_THROW(typeInfo.getBitSize(), CppRuntimeException);

        ASSERT_THROW(typeInfo.getFields(), CppRuntimeException);
        ASSERT_THROW(typeInfo.getParameters(), CppRuntimeException);
        ASSERT_THROW(typeInfo.getFunctions(), CppRuntimeException);

        ASSERT_THROW(typeInfo.getSelector(), CppRuntimeException);
        ASSERT_THROW(typeInfo.getCases(), CppRuntimeException);

        ASSERT_THROW(typeInfo.getUnderlyingType(), CppRuntimeException);
        ASSERT_THROW(typeInfo.getUnderlyingTypeArguments(), CppRuntimeException);
        ASSERT_THROW(typeInfo.getEnumItems(), CppRuntimeException);
        ASSERT_THROW(typeInfo.getBitmaskValues(), CppRuntimeException);

        ASSERT_THROW(typeInfo.getColumns(), CppRuntimeException);
        ASSERT_THROW(typeInfo.getSqlConstraint(), CppRuntimeException);
        ASSERT_THROW(typeInfo.getVirtualTableUsing(), CppRuntimeException);
        ASSERT_THROW(typeInfo.isWithoutRowId(), CppRuntimeException);

        ASSERT_THROW(typeInfo.getTables(), CppRuntimeException);

        ASSERT_THROW(typeInfo.getTemplateName(), CppRuntimeException);
        ASSERT_THROW(typeInfo.getTemplateArguments(), CppRuntimeException);

        ASSERT_THROW(typeInfo.getMessages(), CppRuntimeException);

        ASSERT_THROW(typeInfo.getMethods(), CppRuntimeException);
    }
};

// TODO[Mi-L@]: Test all structures (e.g. FieldInfo).

TEST_F(TypeInfoTest, builtinTypeInfo)
{
    using Builtin = BuiltinTypeInfo<>;

    checkBuiltinTypeInfo(Builtin::getBool(), "bool"_sv, SchemaType::BOOL, CppType::BOOL, 1);

    checkBuiltinTypeInfo(Builtin::getInt8(), "int8"_sv, SchemaType::INT8, CppType::INT8, 8);
    checkBuiltinTypeInfo(Builtin::getInt16(), "int16"_sv, SchemaType::INT16, CppType::INT16, 16);
    checkBuiltinTypeInfo(Builtin::getInt32(), "int32"_sv, SchemaType::INT32, CppType::INT32, 32);
    checkBuiltinTypeInfo(Builtin::getInt64(), "int64"_sv, SchemaType::INT64, CppType::INT64, 64);

    checkBuiltinTypeInfo(Builtin::getUInt8(), "uint8"_sv, SchemaType::UINT8, CppType::UINT8, 8);
    checkBuiltinTypeInfo(Builtin::getUInt16(), "uint16"_sv, SchemaType::UINT16, CppType::UINT16, 16);
    checkBuiltinTypeInfo(Builtin::getUInt32(), "uint32"_sv, SchemaType::UINT32, CppType::UINT32, 32);
    checkBuiltinTypeInfo(Builtin::getUInt64(), "uint64"_sv, SchemaType::UINT64, CppType::UINT64, 64);

    checkBuiltinTypeInfo(Builtin::getVarInt16(), "varint16"_sv, SchemaType::VARINT16, CppType::INT16);
    checkBuiltinTypeInfo(Builtin::getVarInt32(), "varint32"_sv, SchemaType::VARINT32, CppType::INT32);
    checkBuiltinTypeInfo(Builtin::getVarInt64(), "varint64"_sv, SchemaType::VARINT64, CppType::INT64);
    checkBuiltinTypeInfo(Builtin::getVarInt(), "varint"_sv, SchemaType::VARINT, CppType::INT64);
    checkBuiltinTypeInfo(Builtin::getVarUInt16(), "varuint16"_sv, SchemaType::VARUINT16, CppType::UINT16);
    checkBuiltinTypeInfo(Builtin::getVarUInt32(), "varuint32"_sv, SchemaType::VARUINT32, CppType::UINT32);
    checkBuiltinTypeInfo(Builtin::getVarUInt64(), "varuint64"_sv, SchemaType::VARUINT64, CppType::UINT64);
    checkBuiltinTypeInfo(Builtin::getVarUInt(), "varuint"_sv, SchemaType::VARUINT, CppType::UINT64);
    checkBuiltinTypeInfo(Builtin::getVarSize(), "varsize"_sv, SchemaType::VARSIZE, CppType::UINT32);

    checkBuiltinTypeInfo(Builtin::getFloat16(), "float16"_sv, SchemaType::FLOAT16, CppType::FLOAT, 16);
    checkBuiltinTypeInfo(Builtin::getFloat32(), "float32"_sv, SchemaType::FLOAT32, CppType::FLOAT, 32);
    checkBuiltinTypeInfo(Builtin::getFloat64(), "float64"_sv, SchemaType::FLOAT64, CppType::DOUBLE, 64);

    checkBuiltinTypeInfo(Builtin::getString(), "string"_sv, SchemaType::STRING, CppType::STRING);

    checkBuiltinTypeInfo(Builtin::getBitBuffer(), "extern"_sv, SchemaType::EXTERN, CppType::BIT_BUFFER);

    // fixed signed bit fields
    uint8_t bitSize = 0;
    ASSERT_THROW(Builtin::getFixedSignedBitField(bitSize), CppRuntimeException);
    for (++bitSize ; bitSize <= 8; ++bitSize)
    {
        checkBuiltinTypeInfo(Builtin::getFixedSignedBitField(bitSize), "int:" +
                std::to_string(bitSize), SchemaType::FIXED_SIGNED_BITFIELD, CppType::INT8, bitSize);
    }
    for (; bitSize <= 16; ++bitSize)
    {
        checkBuiltinTypeInfo(Builtin::getFixedSignedBitField(bitSize), "int:" +
                std::to_string(bitSize), SchemaType::FIXED_SIGNED_BITFIELD, CppType::INT16, bitSize);
    }
    for (; bitSize <= 32; ++bitSize)
    {
        checkBuiltinTypeInfo(Builtin::getFixedSignedBitField(bitSize), "int:" +
                std::to_string(bitSize), SchemaType::FIXED_SIGNED_BITFIELD, CppType::INT32, bitSize);
    }
    for (; bitSize <= 64; ++bitSize)
    {
        checkBuiltinTypeInfo(Builtin::getFixedSignedBitField(bitSize), "int:" +
                std::to_string(bitSize), SchemaType::FIXED_SIGNED_BITFIELD, CppType::INT64, bitSize);
    }
    for (; bitSize < 255; ++bitSize)
    {
        ASSERT_THROW(Builtin::getFixedSignedBitField(bitSize), CppRuntimeException);
    }
    ASSERT_THROW(Builtin::getFixedSignedBitField(bitSize), CppRuntimeException);

    // fixed unsigned bit fields
    bitSize = 0;
    ASSERT_THROW(Builtin::getFixedUnsignedBitField(bitSize), CppRuntimeException);
    for (++bitSize ; bitSize <= 8; ++bitSize)
    {
        checkBuiltinTypeInfo(Builtin::getFixedUnsignedBitField(bitSize), "bit:" +
                std::to_string(bitSize), SchemaType::FIXED_UNSIGNED_BITFIELD, CppType::UINT8, bitSize);
    }
    for (; bitSize <= 16; ++bitSize)
    {
        checkBuiltinTypeInfo(Builtin::getFixedUnsignedBitField(bitSize), "bit:" +
                std::to_string(bitSize), SchemaType::FIXED_UNSIGNED_BITFIELD, CppType::UINT16, bitSize);
    }
    for (; bitSize <= 32; ++bitSize)
    {
        checkBuiltinTypeInfo(Builtin::getFixedUnsignedBitField(bitSize), "bit:" +
                std::to_string(bitSize), SchemaType::FIXED_UNSIGNED_BITFIELD, CppType::UINT32, bitSize);
    }
    for (; bitSize <= 64; ++bitSize)
    {
        checkBuiltinTypeInfo(Builtin::getFixedUnsignedBitField(bitSize), "bit:" +
                std::to_string(bitSize), SchemaType::FIXED_UNSIGNED_BITFIELD, CppType::UINT64, bitSize);
    }
    for (; bitSize < 255; ++bitSize)
    {
        ASSERT_THROW(Builtin::getFixedUnsignedBitField(bitSize), CppRuntimeException);
    }
    ASSERT_THROW(Builtin::getFixedUnsignedBitField(bitSize), CppRuntimeException);

    // dynamic signed bit fields
    uint8_t maxBitSize = 0;
    ASSERT_THROW(Builtin::getDynamicSignedBitField(maxBitSize), CppRuntimeException);
    for (++maxBitSize ; maxBitSize <= 8; ++maxBitSize)
    {
        checkBuiltinTypeInfo(Builtin::getDynamicSignedBitField(maxBitSize), "int<>",
                SchemaType::DYNAMIC_SIGNED_BITFIELD, CppType::INT8);
    }
    for (; maxBitSize <= 16; ++maxBitSize)
    {
        checkBuiltinTypeInfo(Builtin::getDynamicSignedBitField(maxBitSize), "int<>",
                SchemaType::DYNAMIC_SIGNED_BITFIELD, CppType::INT16);
    }
    for (; maxBitSize <= 32; ++maxBitSize)
    {
        checkBuiltinTypeInfo(Builtin::getDynamicSignedBitField(maxBitSize), "int<>",
                SchemaType::DYNAMIC_SIGNED_BITFIELD, CppType::INT32);
    }
    for (; maxBitSize <= 64; ++maxBitSize)
    {
        checkBuiltinTypeInfo(Builtin::getDynamicSignedBitField(maxBitSize), "int<>",
                SchemaType::DYNAMIC_SIGNED_BITFIELD, CppType::INT64);
    }
    for (; maxBitSize < 255; ++maxBitSize)
    {
        ASSERT_THROW(Builtin::getDynamicSignedBitField(maxBitSize), CppRuntimeException);
    }
    ASSERT_THROW(Builtin::getDynamicSignedBitField(maxBitSize), CppRuntimeException);

    // dynamic unsigned bit fields
    maxBitSize = 0;
    ASSERT_THROW(Builtin::getDynamicUnsignedBitField(maxBitSize), CppRuntimeException);
    for (++maxBitSize ; maxBitSize <= 8; ++maxBitSize)
    {
        checkBuiltinTypeInfo(Builtin::getDynamicUnsignedBitField(maxBitSize), "bit<>",
                SchemaType::DYNAMIC_UNSIGNED_BITFIELD, CppType::UINT8);
    }
    for (; maxBitSize <= 16; ++maxBitSize)
    {
        checkBuiltinTypeInfo(Builtin::getDynamicUnsignedBitField(maxBitSize), "bit<>",
                SchemaType::DYNAMIC_UNSIGNED_BITFIELD, CppType::UINT16);
    }
    for (; maxBitSize <= 32; ++maxBitSize)
    {
        checkBuiltinTypeInfo(Builtin::getDynamicUnsignedBitField(maxBitSize), "bit<>",
                SchemaType::DYNAMIC_UNSIGNED_BITFIELD, CppType::UINT32);
    }
    for (; maxBitSize <= 64; ++maxBitSize)
    {
        checkBuiltinTypeInfo(Builtin::getDynamicUnsignedBitField(maxBitSize), "bit<>",
                SchemaType::DYNAMIC_UNSIGNED_BITFIELD, CppType::UINT64);
    }
    for (; maxBitSize < 255; ++maxBitSize)
    {
        ASSERT_THROW(Builtin::getDynamicUnsignedBitField(maxBitSize), CppRuntimeException);
    }
    ASSERT_THROW(Builtin::getDynamicUnsignedBitField(maxBitSize), CppRuntimeException);
}

TEST_F(TypeInfoTest, structTypeInfo)
{
    const StructTypeInfo<std::allocator<uint8_t>> structTypeInfo(""_sv, nullptr, ""_sv, {}, {}, {}, {});
    ASSERT_EQ(""_sv, structTypeInfo.getSchemaName());
    ASSERT_EQ(SchemaType::STRUCT, structTypeInfo.getSchemaType());
    ASSERT_EQ(CppType::STRUCT, structTypeInfo.getCppType());
    ASSERT_THROW(structTypeInfo.getBitSize(), CppRuntimeException);

    ASSERT_EQ(0, structTypeInfo.getFields().size());
    ASSERT_EQ(0, structTypeInfo.getParameters().size());
    ASSERT_EQ(0, structTypeInfo.getFunctions().size());

    ASSERT_THROW(structTypeInfo.getSelector(), CppRuntimeException);
    ASSERT_THROW(structTypeInfo.getCases(), CppRuntimeException);

    ASSERT_THROW(structTypeInfo.getUnderlyingType(), CppRuntimeException);
    ASSERT_THROW(structTypeInfo.getUnderlyingTypeArguments(), CppRuntimeException);
    ASSERT_THROW(structTypeInfo.getEnumItems(), CppRuntimeException);
    ASSERT_THROW(structTypeInfo.getBitmaskValues(), CppRuntimeException);

    ASSERT_THROW(structTypeInfo.getColumns(), CppRuntimeException);
    ASSERT_THROW(structTypeInfo.getSqlConstraint(), CppRuntimeException);
    ASSERT_THROW(structTypeInfo.getVirtualTableUsing(), CppRuntimeException);
    ASSERT_THROW(structTypeInfo.isWithoutRowId(), CppRuntimeException);

    ASSERT_THROW(structTypeInfo.getTables(), CppRuntimeException);

    ASSERT_EQ(""_sv, structTypeInfo.getTemplateName());
    ASSERT_EQ(0, structTypeInfo.getTemplateArguments().size());

    ASSERT_THROW(structTypeInfo.getMessages(), CppRuntimeException);

    ASSERT_THROW(structTypeInfo.getMethods(), CppRuntimeException);
}

TEST_F(TypeInfoTest, unionTypeInfo)
{
    const UnionTypeInfo<std::allocator<uint8_t>> unionTypeInfo(""_sv, nullptr, ""_sv, {}, {}, {}, {});
    ASSERT_EQ(""_sv, unionTypeInfo.getSchemaName());
    ASSERT_EQ(SchemaType::UNION, unionTypeInfo.getSchemaType());
    ASSERT_EQ(CppType::UNION, unionTypeInfo.getCppType());
    ASSERT_THROW(unionTypeInfo.getBitSize(), CppRuntimeException);

    ASSERT_EQ(0, unionTypeInfo.getFields().size());
    ASSERT_EQ(0, unionTypeInfo.getParameters().size());
    ASSERT_EQ(0, unionTypeInfo.getFunctions().size());

    ASSERT_THROW(unionTypeInfo.getSelector(), CppRuntimeException);
    ASSERT_THROW(unionTypeInfo.getCases(), CppRuntimeException);

    ASSERT_THROW(unionTypeInfo.getUnderlyingType(), CppRuntimeException);
    ASSERT_THROW(unionTypeInfo.getUnderlyingTypeArguments(), CppRuntimeException);
    ASSERT_THROW(unionTypeInfo.getEnumItems(), CppRuntimeException);
    ASSERT_THROW(unionTypeInfo.getBitmaskValues(), CppRuntimeException);

    ASSERT_THROW(unionTypeInfo.getColumns(), CppRuntimeException);
    ASSERT_THROW(unionTypeInfo.getSqlConstraint(), CppRuntimeException);
    ASSERT_THROW(unionTypeInfo.getVirtualTableUsing(), CppRuntimeException);
    ASSERT_THROW(unionTypeInfo.isWithoutRowId(), CppRuntimeException);

    ASSERT_THROW(unionTypeInfo.getTables(), CppRuntimeException);

    ASSERT_EQ(""_sv, unionTypeInfo.getTemplateName());
    ASSERT_EQ(0, unionTypeInfo.getTemplateArguments().size());

    ASSERT_THROW(unionTypeInfo.getMessages(), CppRuntimeException);

    ASSERT_THROW(unionTypeInfo.getMethods(), CppRuntimeException);
}

TEST_F(TypeInfoTest, choiceTypeInfo)
{
    const ChoiceTypeInfo<std::allocator<uint8_t>> choiceTypeInfo(
            ""_sv, nullptr, ""_sv, {}, {}, {}, {}, ""_sv, {});
    ASSERT_EQ(""_sv, choiceTypeInfo.getSchemaName());
    ASSERT_EQ(SchemaType::CHOICE, choiceTypeInfo.getSchemaType());
    ASSERT_EQ(CppType::CHOICE, choiceTypeInfo.getCppType());
    ASSERT_THROW(choiceTypeInfo.getBitSize(), CppRuntimeException);

    ASSERT_EQ(0, choiceTypeInfo.getFields().size());
    ASSERT_EQ(0, choiceTypeInfo.getParameters().size());
    ASSERT_EQ(0, choiceTypeInfo.getFunctions().size());

    ASSERT_EQ(""_sv, choiceTypeInfo.getSelector());
    ASSERT_EQ(0, choiceTypeInfo.getCases().size());

    ASSERT_THROW(choiceTypeInfo.getUnderlyingType(), CppRuntimeException);
    ASSERT_THROW(choiceTypeInfo.getUnderlyingTypeArguments(), CppRuntimeException);
    ASSERT_THROW(choiceTypeInfo.getEnumItems(), CppRuntimeException);
    ASSERT_THROW(choiceTypeInfo.getBitmaskValues(), CppRuntimeException);

    ASSERT_THROW(choiceTypeInfo.getColumns(), CppRuntimeException);
    ASSERT_THROW(choiceTypeInfo.getSqlConstraint(), CppRuntimeException);
    ASSERT_THROW(choiceTypeInfo.getVirtualTableUsing(), CppRuntimeException);
    ASSERT_THROW(choiceTypeInfo.isWithoutRowId(), CppRuntimeException);

    ASSERT_THROW(choiceTypeInfo.getTables(), CppRuntimeException);

    ASSERT_EQ(""_sv, choiceTypeInfo.getTemplateName());
    ASSERT_EQ(0, choiceTypeInfo.getTemplateArguments().size());

    ASSERT_THROW(choiceTypeInfo.getMessages(), CppRuntimeException);

    ASSERT_THROW(choiceTypeInfo.getMethods(), CppRuntimeException);
}

TEST_F(TypeInfoTest, sqlTableTypeInfo)
{
    const SqlTableTypeInfo<std::allocator<uint8_t>> sqlTableTypeInfo(""_sv, ""_sv, {}, {}, ""_sv, ""_sv, false);
    ASSERT_EQ(""_sv, sqlTableTypeInfo.getSchemaName());
    ASSERT_EQ(SchemaType::SQL_TABLE, sqlTableTypeInfo.getSchemaType());
    ASSERT_EQ(CppType::SQL_TABLE, sqlTableTypeInfo.getCppType());
    ASSERT_THROW(sqlTableTypeInfo.getBitSize(), CppRuntimeException);

    ASSERT_THROW(sqlTableTypeInfo.getFields(), CppRuntimeException);
    ASSERT_THROW(sqlTableTypeInfo.getParameters(), CppRuntimeException);
    ASSERT_THROW(sqlTableTypeInfo.getFunctions(), CppRuntimeException);

    ASSERT_THROW(sqlTableTypeInfo.getSelector(), CppRuntimeException);
    ASSERT_THROW(sqlTableTypeInfo.getCases(), CppRuntimeException);

    ASSERT_THROW(sqlTableTypeInfo.getUnderlyingType(), CppRuntimeException);
    ASSERT_THROW(sqlTableTypeInfo.getUnderlyingTypeArguments(), CppRuntimeException);
    ASSERT_THROW(sqlTableTypeInfo.getEnumItems(), CppRuntimeException);
    ASSERT_THROW(sqlTableTypeInfo.getBitmaskValues(), CppRuntimeException);

    ASSERT_EQ(0, sqlTableTypeInfo.getColumns().size());
    ASSERT_EQ(""_sv, sqlTableTypeInfo.getSqlConstraint());
    ASSERT_EQ(""_sv, sqlTableTypeInfo.getVirtualTableUsing());
    ASSERT_EQ(false, sqlTableTypeInfo.isWithoutRowId());

    ASSERT_THROW(sqlTableTypeInfo.getTables(), CppRuntimeException);

    ASSERT_EQ(""_sv, sqlTableTypeInfo.getTemplateName());
    ASSERT_EQ(0, sqlTableTypeInfo.getTemplateArguments().size());

    ASSERT_THROW(sqlTableTypeInfo.getMessages(), CppRuntimeException);

    ASSERT_THROW(sqlTableTypeInfo.getMethods(), CppRuntimeException);
}

TEST_F(TypeInfoTest, sqlDatabaseTypeInfo)
{
    const SqlDatabaseTypeInfo<std::allocator<uint8_t>> sqlDatabaseTypeInfo(""_sv, {});
    ASSERT_EQ(""_sv, sqlDatabaseTypeInfo.getSchemaName());
    ASSERT_EQ(SchemaType::SQL_DATABASE, sqlDatabaseTypeInfo.getSchemaType());
    ASSERT_EQ(CppType::SQL_DATABASE, sqlDatabaseTypeInfo.getCppType());
    ASSERT_THROW(sqlDatabaseTypeInfo.getBitSize(), CppRuntimeException);

    ASSERT_THROW(sqlDatabaseTypeInfo.getFields(), CppRuntimeException);
    ASSERT_THROW(sqlDatabaseTypeInfo.getParameters(), CppRuntimeException);
    ASSERT_THROW(sqlDatabaseTypeInfo.getFunctions(), CppRuntimeException);

    ASSERT_THROW(sqlDatabaseTypeInfo.getSelector(), CppRuntimeException);
    ASSERT_THROW(sqlDatabaseTypeInfo.getCases(), CppRuntimeException);

    ASSERT_THROW(sqlDatabaseTypeInfo.getUnderlyingType(), CppRuntimeException);
    ASSERT_THROW(sqlDatabaseTypeInfo.getUnderlyingTypeArguments(), CppRuntimeException);
    ASSERT_THROW(sqlDatabaseTypeInfo.getEnumItems(), CppRuntimeException);
    ASSERT_THROW(sqlDatabaseTypeInfo.getBitmaskValues(), CppRuntimeException);

    ASSERT_THROW(sqlDatabaseTypeInfo.getColumns(), CppRuntimeException);
    ASSERT_THROW(sqlDatabaseTypeInfo.getSqlConstraint(), CppRuntimeException);
    ASSERT_THROW(sqlDatabaseTypeInfo.getVirtualTableUsing(), CppRuntimeException);
    ASSERT_THROW(sqlDatabaseTypeInfo.isWithoutRowId(), CppRuntimeException);

    ASSERT_EQ(0, sqlDatabaseTypeInfo.getTables().size());

    ASSERT_THROW(sqlDatabaseTypeInfo.getTemplateName(), CppRuntimeException);
    ASSERT_THROW(sqlDatabaseTypeInfo.getTemplateArguments(), CppRuntimeException);

    ASSERT_THROW(sqlDatabaseTypeInfo.getMessages(), CppRuntimeException);

    ASSERT_THROW(sqlDatabaseTypeInfo.getMethods(), CppRuntimeException);
}

TEST_F(TypeInfoTest, enumTypeInfo)
{
    const ITypeInfo& underlyingTypeInfo = BuiltinTypeInfo<>::getInt8();
    const EnumTypeInfo<std::allocator<uint8_t>> enumTypeInfo(""_sv, underlyingTypeInfo, {}, {});
    ASSERT_EQ(""_sv, enumTypeInfo.getSchemaName());
    ASSERT_EQ(SchemaType::ENUM, enumTypeInfo.getSchemaType());
    ASSERT_EQ(CppType::ENUM, enumTypeInfo.getCppType());
    ASSERT_THROW(enumTypeInfo.getBitSize(), CppRuntimeException);

    ASSERT_THROW(enumTypeInfo.getFields(), CppRuntimeException);
    ASSERT_THROW(enumTypeInfo.getParameters(), CppRuntimeException);
    ASSERT_THROW(enumTypeInfo.getFunctions(), CppRuntimeException);

    ASSERT_THROW(enumTypeInfo.getSelector(), CppRuntimeException);
    ASSERT_THROW(enumTypeInfo.getCases(), CppRuntimeException);

    ASSERT_EQ(&underlyingTypeInfo, &enumTypeInfo.getUnderlyingType());
    ASSERT_EQ(0, enumTypeInfo.getUnderlyingTypeArguments().size());
    ASSERT_EQ(0, enumTypeInfo.getEnumItems().size());
    ASSERT_THROW(enumTypeInfo.getBitmaskValues(), CppRuntimeException);

    ASSERT_THROW(enumTypeInfo.getColumns(), CppRuntimeException);
    ASSERT_THROW(enumTypeInfo.getSqlConstraint(), CppRuntimeException);
    ASSERT_THROW(enumTypeInfo.getVirtualTableUsing(), CppRuntimeException);
    ASSERT_THROW(enumTypeInfo.isWithoutRowId(), CppRuntimeException);

    ASSERT_THROW(enumTypeInfo.getTables(), CppRuntimeException);

    ASSERT_THROW(enumTypeInfo.getTemplateName(), CppRuntimeException);
    ASSERT_THROW(enumTypeInfo.getTemplateArguments(), CppRuntimeException);

    ASSERT_THROW(enumTypeInfo.getMessages(), CppRuntimeException);

    ASSERT_THROW(enumTypeInfo.getMethods(), CppRuntimeException);
}

TEST_F(TypeInfoTest, bitmaskTypeInfo)
{
    const ITypeInfo& underlyingTypeInfo = BuiltinTypeInfo<>::getInt8();
    const BitmaskTypeInfo<std::allocator<uint8_t>> bitmaskTypeInfo(""_sv, underlyingTypeInfo, {}, {});
    ASSERT_EQ(""_sv, bitmaskTypeInfo.getSchemaName());
    ASSERT_EQ(SchemaType::BITMASK, bitmaskTypeInfo.getSchemaType());
    ASSERT_EQ(CppType::BITMASK, bitmaskTypeInfo.getCppType());
    ASSERT_THROW(bitmaskTypeInfo.getBitSize(), CppRuntimeException);

    ASSERT_THROW(bitmaskTypeInfo.getFields(), CppRuntimeException);
    ASSERT_THROW(bitmaskTypeInfo.getParameters(), CppRuntimeException);
    ASSERT_THROW(bitmaskTypeInfo.getFunctions(), CppRuntimeException);

    ASSERT_THROW(bitmaskTypeInfo.getSelector(), CppRuntimeException);
    ASSERT_THROW(bitmaskTypeInfo.getCases(), CppRuntimeException);

    ASSERT_EQ(&underlyingTypeInfo, &bitmaskTypeInfo.getUnderlyingType());
    ASSERT_EQ(0, bitmaskTypeInfo.getUnderlyingTypeArguments().size());
    ASSERT_THROW(bitmaskTypeInfo.getEnumItems(), CppRuntimeException);
    ASSERT_EQ(0, bitmaskTypeInfo.getBitmaskValues().size());

    ASSERT_THROW(bitmaskTypeInfo.getColumns(), CppRuntimeException);
    ASSERT_THROW(bitmaskTypeInfo.getSqlConstraint(), CppRuntimeException);
    ASSERT_THROW(bitmaskTypeInfo.getVirtualTableUsing(), CppRuntimeException);
    ASSERT_THROW(bitmaskTypeInfo.isWithoutRowId(), CppRuntimeException);

    ASSERT_THROW(bitmaskTypeInfo.getTables(), CppRuntimeException);

    ASSERT_THROW(bitmaskTypeInfo.getTemplateName(), CppRuntimeException);
    ASSERT_THROW(bitmaskTypeInfo.getTemplateArguments(), CppRuntimeException);

    ASSERT_THROW(bitmaskTypeInfo.getMessages(), CppRuntimeException);

    ASSERT_THROW(bitmaskTypeInfo.getMethods(), CppRuntimeException);
}

TEST_F(TypeInfoTest, pubsubTypeInfo)
{
    const PubsubTypeInfo<std::allocator<uint8_t>> pubsubTypeInfo(""_sv, {});
    ASSERT_EQ(""_sv, pubsubTypeInfo.getSchemaName());
    ASSERT_EQ(SchemaType::PUBSUB, pubsubTypeInfo.getSchemaType());
    ASSERT_EQ(CppType::PUBSUB, pubsubTypeInfo.getCppType());
    ASSERT_THROW(pubsubTypeInfo.getBitSize(), CppRuntimeException);

    ASSERT_THROW(pubsubTypeInfo.getFields(), CppRuntimeException);
    ASSERT_THROW(pubsubTypeInfo.getParameters(), CppRuntimeException);
    ASSERT_THROW(pubsubTypeInfo.getFunctions(), CppRuntimeException);

    ASSERT_THROW(pubsubTypeInfo.getSelector(), CppRuntimeException);
    ASSERT_THROW(pubsubTypeInfo.getCases(), CppRuntimeException);

    ASSERT_THROW(pubsubTypeInfo.getUnderlyingType(), CppRuntimeException);
    ASSERT_THROW(pubsubTypeInfo.getUnderlyingTypeArguments(), CppRuntimeException);
    ASSERT_THROW(pubsubTypeInfo.getEnumItems(), CppRuntimeException);
    ASSERT_THROW(pubsubTypeInfo.getBitmaskValues(), CppRuntimeException);

    ASSERT_THROW(pubsubTypeInfo.getColumns(), CppRuntimeException);
    ASSERT_THROW(pubsubTypeInfo.getSqlConstraint(), CppRuntimeException);
    ASSERT_THROW(pubsubTypeInfo.getVirtualTableUsing(), CppRuntimeException);
    ASSERT_THROW(pubsubTypeInfo.isWithoutRowId(), CppRuntimeException);

    ASSERT_THROW(pubsubTypeInfo.getTables(), CppRuntimeException);

    ASSERT_THROW(pubsubTypeInfo.getTemplateName(), CppRuntimeException);
    ASSERT_THROW(pubsubTypeInfo.getTemplateArguments(), CppRuntimeException);

    ASSERT_EQ(0, pubsubTypeInfo.getMessages().size());

    ASSERT_THROW(pubsubTypeInfo.getMethods(), CppRuntimeException);
}

TEST_F(TypeInfoTest, serviceTypeInfo)
{
    const ServiceTypeInfo<std::allocator<uint8_t>> serviceTypeInfo(""_sv, {});
    ASSERT_EQ(""_sv, serviceTypeInfo.getSchemaName());
    ASSERT_EQ(SchemaType::SERVICE, serviceTypeInfo.getSchemaType());
    ASSERT_EQ(CppType::SERVICE, serviceTypeInfo.getCppType());
    ASSERT_THROW(serviceTypeInfo.getBitSize(), CppRuntimeException);

    ASSERT_THROW(serviceTypeInfo.getFields(), CppRuntimeException);
    ASSERT_THROW(serviceTypeInfo.getParameters(), CppRuntimeException);
    ASSERT_THROW(serviceTypeInfo.getFunctions(), CppRuntimeException);

    ASSERT_THROW(serviceTypeInfo.getSelector(), CppRuntimeException);
    ASSERT_THROW(serviceTypeInfo.getCases(), CppRuntimeException);

    ASSERT_THROW(serviceTypeInfo.getUnderlyingType(), CppRuntimeException);
    ASSERT_THROW(serviceTypeInfo.getUnderlyingTypeArguments(), CppRuntimeException);
    ASSERT_THROW(serviceTypeInfo.getEnumItems(), CppRuntimeException);
    ASSERT_THROW(serviceTypeInfo.getBitmaskValues(), CppRuntimeException);

    ASSERT_THROW(serviceTypeInfo.getColumns(), CppRuntimeException);
    ASSERT_THROW(serviceTypeInfo.getSqlConstraint(), CppRuntimeException);
    ASSERT_THROW(serviceTypeInfo.getVirtualTableUsing(), CppRuntimeException);
    ASSERT_THROW(serviceTypeInfo.isWithoutRowId(), CppRuntimeException);

    ASSERT_THROW(serviceTypeInfo.getTables(), CppRuntimeException);

    ASSERT_THROW(serviceTypeInfo.getTemplateName(), CppRuntimeException);
    ASSERT_THROW(serviceTypeInfo.getTemplateArguments(), CppRuntimeException);

    ASSERT_THROW(serviceTypeInfo.getMessages(), CppRuntimeException);

    ASSERT_EQ(0, serviceTypeInfo.getMethods().size());
}

TEST_F(TypeInfoTest, recursiveTypeInfo)
{
    const ITypeInfo& typeInfo = RecursiveObject::typeInfo();
    const ITypeInfo& recursiveTypeInfo = typeInfo.getFields()[0].typeInfo;

    ASSERT_EQ(typeInfo.getSchemaName(), recursiveTypeInfo.getSchemaName());
    ASSERT_EQ(typeInfo.getSchemaType(), recursiveTypeInfo.getSchemaType());
    ASSERT_EQ(typeInfo.getCppType(), recursiveTypeInfo.getCppType());
    ASSERT_THROW(recursiveTypeInfo.getBitSize(), CppRuntimeException);

    ASSERT_EQ(&typeInfo.getFields()[0], &recursiveTypeInfo.getFields()[0]);
    ASSERT_EQ(0, recursiveTypeInfo.getParameters().size());
    ASSERT_EQ(0, recursiveTypeInfo.getFunctions().size());

    ASSERT_THROW(recursiveTypeInfo.getSelector(), CppRuntimeException);
    ASSERT_THROW(recursiveTypeInfo.getCases(), CppRuntimeException);

    ASSERT_THROW(recursiveTypeInfo.getUnderlyingType(), CppRuntimeException);
    ASSERT_THROW(recursiveTypeInfo.getUnderlyingTypeArguments(), CppRuntimeException);
    ASSERT_THROW(recursiveTypeInfo.getEnumItems(), CppRuntimeException);
    ASSERT_THROW(recursiveTypeInfo.getBitmaskValues(), CppRuntimeException);

    ASSERT_THROW(recursiveTypeInfo.getColumns(), CppRuntimeException);
    ASSERT_THROW(recursiveTypeInfo.getSqlConstraint(), CppRuntimeException);
    ASSERT_THROW(recursiveTypeInfo.getVirtualTableUsing(), CppRuntimeException);
    ASSERT_THROW(recursiveTypeInfo.isWithoutRowId(), CppRuntimeException);

    ASSERT_THROW(recursiveTypeInfo.getTables(), CppRuntimeException);

    ASSERT_EQ(typeInfo.getTemplateName(), recursiveTypeInfo.getTemplateName());
    ASSERT_EQ(0, recursiveTypeInfo.getTemplateArguments().size());

    ASSERT_THROW(recursiveTypeInfo.getMessages(), CppRuntimeException);

    ASSERT_THROW(recursiveTypeInfo.getMethods(), CppRuntimeException);
}

} // namespace zserio
