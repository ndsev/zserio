#include "TypeInfo.h"

using namespace zserio::literals;

namespace zserio
{

TypeInfoBase::TypeInfoBase(StringView schemaName, SchemaType schemaType, CppType cppType) :
        m_schemaName(schemaName), m_schemaType(schemaType), m_cppType(cppType)
{}

TypeInfoBase::~TypeInfoBase()
{}

StringView TypeInfoBase::getSchemaName() const
{
    return m_schemaName;
}

SchemaType TypeInfoBase::getSchemaType() const
{
    return m_schemaType;
}

CppType TypeInfoBase::getCppType() const
{
    return m_cppType;
}

uint8_t TypeInfoBase::getBitSize() const
{
    throw CppRuntimeException("Type '") + getSchemaName() + "' is not a fixed size type!";
}

Span<const FieldInfo> TypeInfoBase::getFields() const
{
    throw CppRuntimeException("Type '") + getSchemaName() + "' is not a compound type!";
}

Span<const ParameterInfo> TypeInfoBase::getParameters() const
{
    throw CppRuntimeException("Type '") + getSchemaName() + "' is not a compound type!";
}

Span<const FunctionInfo> TypeInfoBase::getFunctions() const
{
    throw CppRuntimeException("Type '") + getSchemaName() + "' is not a compound type!";
}

StringView TypeInfoBase::getSelector() const
{
    throw CppRuntimeException("Type '") + getSchemaName() + "' is not a choice type!";
}

Span<const CaseInfo> TypeInfoBase::getCases() const
{
    throw CppRuntimeException("Type '") + getSchemaName() + "' is not a choice type!";
}

const ITypeInfo& TypeInfoBase::getUnderlyingType() const
{
    throw CppRuntimeException("Type '") + getSchemaName() + "' does not have underlying type!";
}

Span<const StringView> TypeInfoBase::getUnderlyingTypeArguments() const
{
    throw CppRuntimeException("Type '") + getSchemaName() + "' does not have underlying type!";
}

Span<const ItemInfo> TypeInfoBase::getEnumItems() const
{
    throw CppRuntimeException("Type '") + getSchemaName() + "' is not an enum type!";
}

Span<const ItemInfo> TypeInfoBase::getBitmaskValues() const
{
    throw CppRuntimeException("Type '") + getSchemaName() + "' is not a bitmask type!";
}

Span<const ColumnInfo> TypeInfoBase::getColumns() const
{
    throw CppRuntimeException("Type '") + getSchemaName() + "' is not a SQL table type!";
}

StringView TypeInfoBase::getSqlConstraint() const
{
    throw CppRuntimeException("Type '") + getSchemaName() + "' is not a SQL table type!";
}

StringView TypeInfoBase::getVirtualTableUsing() const
{
    throw CppRuntimeException("Type '") + getSchemaName() + "' is not a SQL table type!";
}

bool TypeInfoBase::isWithoutRowId() const
{
    throw CppRuntimeException("Type '") + getSchemaName() + "' is not a SQL table type!";
}

Span<const TableInfo> TypeInfoBase::getTables() const
{
    throw CppRuntimeException("Type '") + getSchemaName() + "' is not a SQL database type!";
}

StringView TypeInfoBase::getTemplateName() const
{
    throw CppRuntimeException("Type '") + getSchemaName() + "' is not a templatable type!";
}

Span<const TemplateArgumentInfo> TypeInfoBase::getTemplateArguments() const
{
    throw CppRuntimeException("Type '") + getSchemaName() + "' is not a templatable type!";
}

Span<const MessageInfo> TypeInfoBase::getMessages() const
{
    throw CppRuntimeException("Type '") + getSchemaName() + "' is not a pubsub type!";
}

Span<const MethodInfo> TypeInfoBase::getMethods() const
{
    throw CppRuntimeException("Type '") + getSchemaName() + "' is not a service type!";
}

BuiltinTypeInfo::BuiltinTypeInfo(StringView schemaName, SchemaType schemaType, CppType cppType) :
        TypeInfoBase(schemaName, schemaType, cppType)
{}

const ITypeInfo& BuiltinTypeInfo::getBool()
{
    return FixedSizeBuiltinTypeInfo::getBool();
}

const ITypeInfo& BuiltinTypeInfo::getInt8()
{
    return FixedSizeBuiltinTypeInfo::getInt8();
}

const ITypeInfo& BuiltinTypeInfo::getInt16()
{
    return FixedSizeBuiltinTypeInfo::getInt16();
}

const ITypeInfo& BuiltinTypeInfo::getInt32()
{
    return FixedSizeBuiltinTypeInfo::getInt32();
}

const ITypeInfo& BuiltinTypeInfo::getInt64()
{
    return FixedSizeBuiltinTypeInfo::getInt64();
}

const ITypeInfo& BuiltinTypeInfo::getUInt8()
{
    return FixedSizeBuiltinTypeInfo::getUInt8();
}

const ITypeInfo& BuiltinTypeInfo::getUInt16()
{
    return FixedSizeBuiltinTypeInfo::getUInt16();
}

const ITypeInfo& BuiltinTypeInfo::getUInt32()
{
    return FixedSizeBuiltinTypeInfo::getUInt32();
}

const ITypeInfo& BuiltinTypeInfo::getUInt64()
{
    return FixedSizeBuiltinTypeInfo::getUInt64();
}

const ITypeInfo& BuiltinTypeInfo::getVarInt16()
{
    static const BuiltinTypeInfo typeInfo = { "varint16"_sv, SchemaType::VARINT16, CppType::INT16 };
    return typeInfo;
}

const ITypeInfo& BuiltinTypeInfo::getVarInt32()
{
    static const BuiltinTypeInfo typeInfo = { "varint32"_sv, SchemaType::VARINT32, CppType::INT32 };
    return typeInfo;
}

const ITypeInfo& BuiltinTypeInfo::getVarInt64()
{
    static const BuiltinTypeInfo typeInfo = { "varint64"_sv, SchemaType::VARINT64, CppType::INT64 };
    return typeInfo;
}

const ITypeInfo& BuiltinTypeInfo::getVarInt()
{
    static const BuiltinTypeInfo typeInfo = { "varint"_sv, SchemaType::VARINT, CppType::INT64 };
    return typeInfo;
}

const ITypeInfo& BuiltinTypeInfo::getVarUInt16()
{
    static const BuiltinTypeInfo typeInfo = { "varuint16"_sv, SchemaType::VARUINT16, CppType::UINT16 };
    return typeInfo;
}

const ITypeInfo& BuiltinTypeInfo::getVarUInt32()
{
    static const BuiltinTypeInfo typeInfo = { "varuint32"_sv, SchemaType::VARUINT32, CppType::UINT32 };
    return typeInfo;
}

const ITypeInfo& BuiltinTypeInfo::getVarUInt64()
{
    static const BuiltinTypeInfo typeInfo = { "varuint64"_sv, SchemaType::VARUINT64, CppType::UINT64 };
    return typeInfo;
}

const ITypeInfo& BuiltinTypeInfo::getVarUInt()
{
    static const BuiltinTypeInfo typeInfo = { "varuint"_sv, SchemaType::VARUINT, CppType::UINT64 };
    return typeInfo;
}

const ITypeInfo& BuiltinTypeInfo::getVarSize()
{
    static const BuiltinTypeInfo typeInfo = { "varsize"_sv, SchemaType::VARSIZE, CppType::UINT32 };
    return typeInfo;
}

const ITypeInfo& BuiltinTypeInfo::getFloat16()
{
    return FixedSizeBuiltinTypeInfo::getFloat16();
}

const ITypeInfo& BuiltinTypeInfo::getFloat32()
{
    return FixedSizeBuiltinTypeInfo::getFloat32();
}

const ITypeInfo& BuiltinTypeInfo::getFloat64()
{
    return FixedSizeBuiltinTypeInfo::getFloat64();
}

const ITypeInfo& BuiltinTypeInfo::getString()
{
    static const BuiltinTypeInfo typeInfo = { "string"_sv, SchemaType::STRING, CppType::STRING };
    return typeInfo;
}

const ITypeInfo& BuiltinTypeInfo::getBitBuffer()
{
    static const BuiltinTypeInfo typeInfo = { "extern"_sv, SchemaType::EXTERN, CppType::BIT_BUFFER };
    return typeInfo;
}

const ITypeInfo& BuiltinTypeInfo::getFixedSignedBitField(uint8_t bitSize)
{
    return FixedSizeBuiltinTypeInfo::getFixedSignedBitField(bitSize);
}

const ITypeInfo& BuiltinTypeInfo::getFixedUnsignedBitField(uint8_t bitSize)
{
    return FixedSizeBuiltinTypeInfo::getFixedUnsignedBitField(bitSize);
}

const ITypeInfo& BuiltinTypeInfo::getDynamicSignedBitField(uint8_t maxBitSize)
{
    if (maxBitSize == 0 || maxBitSize > 64)
    {
        throw CppRuntimeException("BuiltinTypeInfo::getDynamicSignedBitField: Invalid max bit size '") +
                maxBitSize + "!";
    }

    if (maxBitSize <= 8)
    {
        static const BuiltinTypeInfo typeInfo = {
            "int<>"_sv, SchemaType::DYNAMIC_SIGNED_BITFIELD, CppType::INT8
        };
        return typeInfo;
    }
    else if (maxBitSize <= 16)
    {
        static const BuiltinTypeInfo typeInfo = {
            "int<>"_sv, SchemaType::DYNAMIC_SIGNED_BITFIELD, CppType::INT16
        };
        return typeInfo;
    }
    else if (maxBitSize <= 32)
    {
        static const BuiltinTypeInfo typeInfo = {
            "int<>"_sv, SchemaType::DYNAMIC_SIGNED_BITFIELD, CppType::INT32
        };
        return typeInfo;
    }
    else
    {
        static const BuiltinTypeInfo typeInfo = {
            "int<>"_sv, SchemaType::DYNAMIC_SIGNED_BITFIELD, CppType::INT64
        };
        return typeInfo;
    }
}

const ITypeInfo& BuiltinTypeInfo::getDynamicUnsignedBitField(uint8_t maxBitSize)
{
    if (maxBitSize == 0 || maxBitSize > 64)
    {
        throw CppRuntimeException("BuiltinTypeInfo::getDynamicUnsignedBitField: Invalid max bit size '") +
                maxBitSize + "!";
    }

    if (maxBitSize <= 8)
    {
        static const BuiltinTypeInfo typeInfo = {
            "bit<>"_sv, SchemaType::DYNAMIC_UNSIGNED_BITFIELD, CppType::UINT8
        };
        return typeInfo;
    }
    else if (maxBitSize <= 16)
    {
        static const BuiltinTypeInfo typeInfo = {
            "bit<>"_sv, SchemaType::DYNAMIC_UNSIGNED_BITFIELD, CppType::UINT16
        };
        return typeInfo;
    }
    else if (maxBitSize <= 32)
    {
        static const BuiltinTypeInfo typeInfo = {
            "bit<>"_sv, SchemaType::DYNAMIC_UNSIGNED_BITFIELD, CppType::UINT32
        };
        return typeInfo;
    }
    else
    {
        static const BuiltinTypeInfo typeInfo = {
            "bit<>"_sv, SchemaType::DYNAMIC_UNSIGNED_BITFIELD, CppType::UINT64
        };
        return typeInfo;
    }
}

FixedSizeBuiltinTypeInfo::FixedSizeBuiltinTypeInfo(StringView schemaName, SchemaType schemaType,
        CppType cppType, uint8_t bitSize) :
        BuiltinTypeInfo(schemaName, schemaType, cppType), m_bitSize(bitSize)
{}

uint8_t FixedSizeBuiltinTypeInfo::getBitSize() const
{
    return m_bitSize;
}

const ITypeInfo& FixedSizeBuiltinTypeInfo::getBool()
{
    static const FixedSizeBuiltinTypeInfo typeInfo = { "bool"_sv, SchemaType::BOOL, CppType::BOOL, 1 };
    return typeInfo;
}

const ITypeInfo& FixedSizeBuiltinTypeInfo::getInt8()
{
    static const FixedSizeBuiltinTypeInfo typeInfo = { "int8"_sv, SchemaType::INT8, CppType::INT8, 8 };
    return typeInfo;
}

const ITypeInfo& FixedSizeBuiltinTypeInfo::getInt16()
{
    static const FixedSizeBuiltinTypeInfo typeInfo = { "int16"_sv, SchemaType::INT16, CppType::INT16, 16 };
    return typeInfo;
}

const ITypeInfo& FixedSizeBuiltinTypeInfo::getInt32()
{
    static const FixedSizeBuiltinTypeInfo typeInfo = { "int32"_sv, SchemaType::INT32, CppType::INT32, 32 };
    return typeInfo;
}

const ITypeInfo& FixedSizeBuiltinTypeInfo::getInt64()
{
    static const FixedSizeBuiltinTypeInfo typeInfo = { "int64"_sv, SchemaType::INT64, CppType::INT64, 64 };
    return typeInfo;
}

const ITypeInfo& FixedSizeBuiltinTypeInfo::getUInt8()
{
    static const FixedSizeBuiltinTypeInfo typeInfo = { "uint8"_sv, SchemaType::UINT8, CppType::UINT8, 8 };
    return typeInfo;
}

const ITypeInfo& FixedSizeBuiltinTypeInfo::getUInt16()
{
    static const FixedSizeBuiltinTypeInfo typeInfo = { "uint16"_sv, SchemaType::UINT16, CppType::UINT16, 16 };
    return typeInfo;
}

const ITypeInfo& FixedSizeBuiltinTypeInfo::getUInt32()
{
    static const FixedSizeBuiltinTypeInfo typeInfo = { "uint32"_sv, SchemaType::UINT32, CppType::UINT32, 32 };
    return typeInfo;
}

const ITypeInfo& FixedSizeBuiltinTypeInfo::getUInt64()
{
    static const FixedSizeBuiltinTypeInfo typeInfo = { "uint64"_sv, SchemaType::UINT64, CppType::UINT64, 64 };
    return typeInfo;
}

const ITypeInfo& FixedSizeBuiltinTypeInfo::getFloat16()
{
    static const FixedSizeBuiltinTypeInfo typeInfo = { "float16"_sv, SchemaType::FLOAT16, CppType::FLOAT, 16 };
    return typeInfo;
}

const ITypeInfo& FixedSizeBuiltinTypeInfo::getFloat32()
{
    static const FixedSizeBuiltinTypeInfo typeInfo = { "float32"_sv, SchemaType::FLOAT32, CppType::FLOAT, 32 };
    return typeInfo;
}

const ITypeInfo& FixedSizeBuiltinTypeInfo::getFloat64()
{
    static const FixedSizeBuiltinTypeInfo typeInfo = { "float64"_sv, SchemaType::FLOAT64, CppType::DOUBLE, 64 };
    return typeInfo;
}

const ITypeInfo& FixedSizeBuiltinTypeInfo::getFixedSignedBitField(uint8_t bitSize)
{
    if (bitSize == 0 || bitSize > 64)
    {
        throw CppRuntimeException("FixedSizeBuiltinTypeInfo::getFixedSignedBitField: Invalid bit size '") +
                bitSize + "!";
    }

    static std::array<FixedSizeBuiltinTypeInfo, 64> bitFieldTypeInfoArray = {{
        { "int:1"_sv, SchemaType::FIXED_SIGNED_BITFIELD, CppType::INT8, 1 },
        { "int:2"_sv, SchemaType::FIXED_SIGNED_BITFIELD, CppType::INT8, 2 },
        { "int:3"_sv, SchemaType::FIXED_SIGNED_BITFIELD, CppType::INT8, 3 },
        { "int:4"_sv, SchemaType::FIXED_SIGNED_BITFIELD, CppType::INT8, 4 },
        { "int:5"_sv, SchemaType::FIXED_SIGNED_BITFIELD, CppType::INT8, 5 },
        { "int:6"_sv, SchemaType::FIXED_SIGNED_BITFIELD, CppType::INT8, 6 },
        { "int:7"_sv, SchemaType::FIXED_SIGNED_BITFIELD, CppType::INT8, 7 },
        { "int:8"_sv, SchemaType::FIXED_SIGNED_BITFIELD, CppType::INT8, 8 },
        { "int:9"_sv, SchemaType::FIXED_SIGNED_BITFIELD, CppType::INT16, 9 },
        { "int:10"_sv, SchemaType::FIXED_SIGNED_BITFIELD, CppType::INT16, 10 },
        { "int:11"_sv, SchemaType::FIXED_SIGNED_BITFIELD, CppType::INT16, 11 },
        { "int:12"_sv, SchemaType::FIXED_SIGNED_BITFIELD, CppType::INT16, 12 },
        { "int:13"_sv, SchemaType::FIXED_SIGNED_BITFIELD, CppType::INT16, 13 },
        { "int:14"_sv, SchemaType::FIXED_SIGNED_BITFIELD, CppType::INT16, 14 },
        { "int:15"_sv, SchemaType::FIXED_SIGNED_BITFIELD, CppType::INT16, 15 },
        { "int:16"_sv, SchemaType::FIXED_SIGNED_BITFIELD, CppType::INT16, 16 },
        { "int:17"_sv, SchemaType::FIXED_SIGNED_BITFIELD, CppType::INT32, 17 },
        { "int:18"_sv, SchemaType::FIXED_SIGNED_BITFIELD, CppType::INT32, 18 },
        { "int:19"_sv, SchemaType::FIXED_SIGNED_BITFIELD, CppType::INT32, 19 },
        { "int:20"_sv, SchemaType::FIXED_SIGNED_BITFIELD, CppType::INT32, 20 },
        { "int:21"_sv, SchemaType::FIXED_SIGNED_BITFIELD, CppType::INT32, 21 },
        { "int:22"_sv, SchemaType::FIXED_SIGNED_BITFIELD, CppType::INT32, 22 },
        { "int:23"_sv, SchemaType::FIXED_SIGNED_BITFIELD, CppType::INT32, 23 },
        { "int:24"_sv, SchemaType::FIXED_SIGNED_BITFIELD, CppType::INT32, 24 },
        { "int:25"_sv, SchemaType::FIXED_SIGNED_BITFIELD, CppType::INT32, 25 },
        { "int:26"_sv, SchemaType::FIXED_SIGNED_BITFIELD, CppType::INT32, 26 },
        { "int:27"_sv, SchemaType::FIXED_SIGNED_BITFIELD, CppType::INT32, 27 },
        { "int:28"_sv, SchemaType::FIXED_SIGNED_BITFIELD, CppType::INT32, 28 },
        { "int:29"_sv, SchemaType::FIXED_SIGNED_BITFIELD, CppType::INT32, 29 },
        { "int:30"_sv, SchemaType::FIXED_SIGNED_BITFIELD, CppType::INT32, 30 },
        { "int:31"_sv, SchemaType::FIXED_SIGNED_BITFIELD, CppType::INT32, 31 },
        { "int:32"_sv, SchemaType::FIXED_SIGNED_BITFIELD, CppType::INT32, 32 },
        { "int:33"_sv, SchemaType::FIXED_SIGNED_BITFIELD, CppType::INT64, 33 },
        { "int:34"_sv, SchemaType::FIXED_SIGNED_BITFIELD, CppType::INT64, 34 },
        { "int:35"_sv, SchemaType::FIXED_SIGNED_BITFIELD, CppType::INT64, 35 },
        { "int:36"_sv, SchemaType::FIXED_SIGNED_BITFIELD, CppType::INT64, 36 },
        { "int:37"_sv, SchemaType::FIXED_SIGNED_BITFIELD, CppType::INT64, 37 },
        { "int:38"_sv, SchemaType::FIXED_SIGNED_BITFIELD, CppType::INT64, 38 },
        { "int:39"_sv, SchemaType::FIXED_SIGNED_BITFIELD, CppType::INT64, 39 },
        { "int:40"_sv, SchemaType::FIXED_SIGNED_BITFIELD, CppType::INT64, 40 },
        { "int:41"_sv, SchemaType::FIXED_SIGNED_BITFIELD, CppType::INT64, 41 },
        { "int:42"_sv, SchemaType::FIXED_SIGNED_BITFIELD, CppType::INT64, 42 },
        { "int:43"_sv, SchemaType::FIXED_SIGNED_BITFIELD, CppType::INT64, 43 },
        { "int:44"_sv, SchemaType::FIXED_SIGNED_BITFIELD, CppType::INT64, 44 },
        { "int:45"_sv, SchemaType::FIXED_SIGNED_BITFIELD, CppType::INT64, 45 },
        { "int:46"_sv, SchemaType::FIXED_SIGNED_BITFIELD, CppType::INT64, 46 },
        { "int:47"_sv, SchemaType::FIXED_SIGNED_BITFIELD, CppType::INT64, 47 },
        { "int:48"_sv, SchemaType::FIXED_SIGNED_BITFIELD, CppType::INT64, 48 },
        { "int:49"_sv, SchemaType::FIXED_SIGNED_BITFIELD, CppType::INT64, 49 },
        { "int:50"_sv, SchemaType::FIXED_SIGNED_BITFIELD, CppType::INT64, 50 },
        { "int:51"_sv, SchemaType::FIXED_SIGNED_BITFIELD, CppType::INT64, 51 },
        { "int:52"_sv, SchemaType::FIXED_SIGNED_BITFIELD, CppType::INT64, 52 },
        { "int:53"_sv, SchemaType::FIXED_SIGNED_BITFIELD, CppType::INT64, 53 },
        { "int:54"_sv, SchemaType::FIXED_SIGNED_BITFIELD, CppType::INT64, 54 },
        { "int:55"_sv, SchemaType::FIXED_SIGNED_BITFIELD, CppType::INT64, 55 },
        { "int:56"_sv, SchemaType::FIXED_SIGNED_BITFIELD, CppType::INT64, 56 },
        { "int:57"_sv, SchemaType::FIXED_SIGNED_BITFIELD, CppType::INT64, 57 },
        { "int:58"_sv, SchemaType::FIXED_SIGNED_BITFIELD, CppType::INT64, 58 },
        { "int:59"_sv, SchemaType::FIXED_SIGNED_BITFIELD, CppType::INT64, 59 },
        { "int:60"_sv, SchemaType::FIXED_SIGNED_BITFIELD, CppType::INT64, 60 },
        { "int:61"_sv, SchemaType::FIXED_SIGNED_BITFIELD, CppType::INT64, 61 },
        { "int:62"_sv, SchemaType::FIXED_SIGNED_BITFIELD, CppType::INT64, 62 },
        { "int:63"_sv, SchemaType::FIXED_SIGNED_BITFIELD, CppType::INT64, 63 },
        { "int:64"_sv, SchemaType::FIXED_SIGNED_BITFIELD, CppType::INT64, 64 }
    }};

    return bitFieldTypeInfoArray[bitSize - 1];
}

const ITypeInfo& FixedSizeBuiltinTypeInfo::getFixedUnsignedBitField(uint8_t bitSize)
{
    if (bitSize == 0 || bitSize > 64)
    {
        throw CppRuntimeException("FixedSizeBuiltinTypeInfo::getFixedUnsignedBitField: Invalid bit size '") +
                bitSize + "!";
    }

    static std::array<FixedSizeBuiltinTypeInfo, 64> bitFieldTypeInfoArray = {{
        { "bit:1"_sv, SchemaType::FIXED_UNSIGNED_BITFIELD, CppType::UINT8, 1 },
        { "bit:2"_sv, SchemaType::FIXED_UNSIGNED_BITFIELD, CppType::UINT8, 2 },
        { "bit:3"_sv, SchemaType::FIXED_UNSIGNED_BITFIELD, CppType::UINT8, 3 },
        { "bit:4"_sv, SchemaType::FIXED_UNSIGNED_BITFIELD, CppType::UINT8, 4 },
        { "bit:5"_sv, SchemaType::FIXED_UNSIGNED_BITFIELD, CppType::UINT8, 5 },
        { "bit:6"_sv, SchemaType::FIXED_UNSIGNED_BITFIELD, CppType::UINT8, 6 },
        { "bit:7"_sv, SchemaType::FIXED_UNSIGNED_BITFIELD, CppType::UINT8, 7 },
        { "bit:8"_sv, SchemaType::FIXED_UNSIGNED_BITFIELD, CppType::UINT8, 8 },
        { "bit:9"_sv, SchemaType::FIXED_UNSIGNED_BITFIELD, CppType::UINT16, 9 },
        { "bit:10"_sv, SchemaType::FIXED_UNSIGNED_BITFIELD, CppType::UINT16, 10 },
        { "bit:11"_sv, SchemaType::FIXED_UNSIGNED_BITFIELD, CppType::UINT16, 11 },
        { "bit:12"_sv, SchemaType::FIXED_UNSIGNED_BITFIELD, CppType::UINT16, 12 },
        { "bit:13"_sv, SchemaType::FIXED_UNSIGNED_BITFIELD, CppType::UINT16, 13 },
        { "bit:14"_sv, SchemaType::FIXED_UNSIGNED_BITFIELD, CppType::UINT16, 14 },
        { "bit:15"_sv, SchemaType::FIXED_UNSIGNED_BITFIELD, CppType::UINT16, 15 },
        { "bit:16"_sv, SchemaType::FIXED_UNSIGNED_BITFIELD, CppType::UINT16, 16 },
        { "bit:17"_sv, SchemaType::FIXED_UNSIGNED_BITFIELD, CppType::UINT32, 17 },
        { "bit:18"_sv, SchemaType::FIXED_UNSIGNED_BITFIELD, CppType::UINT32, 18 },
        { "bit:19"_sv, SchemaType::FIXED_UNSIGNED_BITFIELD, CppType::UINT32, 19 },
        { "bit:20"_sv, SchemaType::FIXED_UNSIGNED_BITFIELD, CppType::UINT32, 20 },
        { "bit:21"_sv, SchemaType::FIXED_UNSIGNED_BITFIELD, CppType::UINT32, 21 },
        { "bit:22"_sv, SchemaType::FIXED_UNSIGNED_BITFIELD, CppType::UINT32, 22 },
        { "bit:23"_sv, SchemaType::FIXED_UNSIGNED_BITFIELD, CppType::UINT32, 23 },
        { "bit:24"_sv, SchemaType::FIXED_UNSIGNED_BITFIELD, CppType::UINT32, 24 },
        { "bit:25"_sv, SchemaType::FIXED_UNSIGNED_BITFIELD, CppType::UINT32, 25 },
        { "bit:26"_sv, SchemaType::FIXED_UNSIGNED_BITFIELD, CppType::UINT32, 26 },
        { "bit:27"_sv, SchemaType::FIXED_UNSIGNED_BITFIELD, CppType::UINT32, 27 },
        { "bit:28"_sv, SchemaType::FIXED_UNSIGNED_BITFIELD, CppType::UINT32, 28 },
        { "bit:29"_sv, SchemaType::FIXED_UNSIGNED_BITFIELD, CppType::UINT32, 29 },
        { "bit:30"_sv, SchemaType::FIXED_UNSIGNED_BITFIELD, CppType::UINT32, 30 },
        { "bit:31"_sv, SchemaType::FIXED_UNSIGNED_BITFIELD, CppType::UINT32, 31 },
        { "bit:32"_sv, SchemaType::FIXED_UNSIGNED_BITFIELD, CppType::UINT32, 32 },
        { "bit:33"_sv, SchemaType::FIXED_UNSIGNED_BITFIELD, CppType::UINT64, 33 },
        { "bit:34"_sv, SchemaType::FIXED_UNSIGNED_BITFIELD, CppType::UINT64, 34 },
        { "bit:35"_sv, SchemaType::FIXED_UNSIGNED_BITFIELD, CppType::UINT64, 35 },
        { "bit:36"_sv, SchemaType::FIXED_UNSIGNED_BITFIELD, CppType::UINT64, 36 },
        { "bit:37"_sv, SchemaType::FIXED_UNSIGNED_BITFIELD, CppType::UINT64, 37 },
        { "bit:38"_sv, SchemaType::FIXED_UNSIGNED_BITFIELD, CppType::UINT64, 38 },
        { "bit:39"_sv, SchemaType::FIXED_UNSIGNED_BITFIELD, CppType::UINT64, 39 },
        { "bit:40"_sv, SchemaType::FIXED_UNSIGNED_BITFIELD, CppType::UINT64, 40 },
        { "bit:41"_sv, SchemaType::FIXED_UNSIGNED_BITFIELD, CppType::UINT64, 41 },
        { "bit:42"_sv, SchemaType::FIXED_UNSIGNED_BITFIELD, CppType::UINT64, 42 },
        { "bit:43"_sv, SchemaType::FIXED_UNSIGNED_BITFIELD, CppType::UINT64, 43 },
        { "bit:44"_sv, SchemaType::FIXED_UNSIGNED_BITFIELD, CppType::UINT64, 44 },
        { "bit:45"_sv, SchemaType::FIXED_UNSIGNED_BITFIELD, CppType::UINT64, 45 },
        { "bit:46"_sv, SchemaType::FIXED_UNSIGNED_BITFIELD, CppType::UINT64, 46 },
        { "bit:47"_sv, SchemaType::FIXED_UNSIGNED_BITFIELD, CppType::UINT64, 47 },
        { "bit:48"_sv, SchemaType::FIXED_UNSIGNED_BITFIELD, CppType::UINT64, 48 },
        { "bit:49"_sv, SchemaType::FIXED_UNSIGNED_BITFIELD, CppType::UINT64, 49 },
        { "bit:50"_sv, SchemaType::FIXED_UNSIGNED_BITFIELD, CppType::UINT64, 50 },
        { "bit:51"_sv, SchemaType::FIXED_UNSIGNED_BITFIELD, CppType::UINT64, 51 },
        { "bit:52"_sv, SchemaType::FIXED_UNSIGNED_BITFIELD, CppType::UINT64, 52 },
        { "bit:53"_sv, SchemaType::FIXED_UNSIGNED_BITFIELD, CppType::UINT64, 53 },
        { "bit:54"_sv, SchemaType::FIXED_UNSIGNED_BITFIELD, CppType::UINT64, 54 },
        { "bit:55"_sv, SchemaType::FIXED_UNSIGNED_BITFIELD, CppType::UINT64, 55 },
        { "bit:56"_sv, SchemaType::FIXED_UNSIGNED_BITFIELD, CppType::UINT64, 56 },
        { "bit:57"_sv, SchemaType::FIXED_UNSIGNED_BITFIELD, CppType::UINT64, 57 },
        { "bit:58"_sv, SchemaType::FIXED_UNSIGNED_BITFIELD, CppType::UINT64, 58 },
        { "bit:59"_sv, SchemaType::FIXED_UNSIGNED_BITFIELD, CppType::UINT64, 59 },
        { "bit:60"_sv, SchemaType::FIXED_UNSIGNED_BITFIELD, CppType::UINT64, 60 },
        { "bit:61"_sv, SchemaType::FIXED_UNSIGNED_BITFIELD, CppType::UINT64, 61 },
        { "bit:62"_sv, SchemaType::FIXED_UNSIGNED_BITFIELD, CppType::UINT64, 62 },
        { "bit:63"_sv, SchemaType::FIXED_UNSIGNED_BITFIELD, CppType::UINT64, 63 },
        { "bit:64"_sv, SchemaType::FIXED_UNSIGNED_BITFIELD, CppType::UINT64, 64 }
    }};

    return bitFieldTypeInfoArray[bitSize - 1];
}

TemplatableTypeInfoBase::TemplatableTypeInfoBase(StringView schemaName, SchemaType schemaType, CppType cppType,
        StringView templateName, Span<const TemplateArgumentInfo> templateArguments) :
        TypeInfoBase(schemaName, schemaType, cppType),
        m_templateName(templateName), m_templateArguments(templateArguments)
{}

TemplatableTypeInfoBase::~TemplatableTypeInfoBase()
{}

StringView TemplatableTypeInfoBase::getTemplateName() const
{
    return m_templateName;
}

Span<const TemplateArgumentInfo> TemplatableTypeInfoBase::getTemplateArguments() const
{
    return m_templateArguments;
}

CompoundTypeInfoBase::CompoundTypeInfoBase(StringView schemaName, SchemaType schemaType, CppType cppType,
        StringView templateName, Span<const TemplateArgumentInfo> templateArguments,
        Span<const FieldInfo> fields, Span<const ParameterInfo> parameters,
        Span<const FunctionInfo> functions) :
        TemplatableTypeInfoBase(schemaName, schemaType, cppType, templateName, templateArguments),
        m_fields(fields), m_parameters(parameters), m_functions(functions)
{}

CompoundTypeInfoBase::~CompoundTypeInfoBase()
{}

Span<const FieldInfo> CompoundTypeInfoBase::getFields() const
{
    return m_fields;
}

Span<const ParameterInfo> CompoundTypeInfoBase::getParameters() const
{
    return m_parameters;
}

Span<const FunctionInfo> CompoundTypeInfoBase::getFunctions() const
{
    return m_functions;
}

StructTypeInfo::StructTypeInfo(StringView schemaName,
        StringView templateName, Span<const TemplateArgumentInfo> templateArguments,
        Span<const FieldInfo> fields, Span<const ParameterInfo> parameters,
        Span<const FunctionInfo> functions) :
        CompoundTypeInfoBase(schemaName, SchemaType::STRUCT, CppType::STRUCT,
                templateName, templateArguments, fields, parameters, functions)
{}

UnionTypeInfo::UnionTypeInfo(StringView schemaName,
        StringView templateName, Span<const TemplateArgumentInfo> templateArguments,
        Span<const FieldInfo> fields, Span<const ParameterInfo> parameters,
        Span<const FunctionInfo> functions) :
        CompoundTypeInfoBase(schemaName, SchemaType::UNION, CppType::UNION,
                templateName, templateArguments, fields, parameters, functions)
{}

ChoiceTypeInfo::ChoiceTypeInfo(StringView schemaName,
        StringView templateName, Span<const TemplateArgumentInfo> templateArguments,
        Span<const FieldInfo> fields, Span<const ParameterInfo> parameters, Span<const FunctionInfo> functions,
        StringView selector, Span<const CaseInfo> cases) :
        CompoundTypeInfoBase(schemaName, SchemaType::CHOICE, CppType::CHOICE,
                templateName, templateArguments, fields, parameters, functions),
        m_selector(selector), m_cases(cases)
{}

StringView ChoiceTypeInfo::getSelector() const
{
    return m_selector;
}

Span<const CaseInfo> ChoiceTypeInfo::getCases() const
{
    return m_cases;
}

SqlTableTypeInfo::SqlTableTypeInfo(StringView schemaName,
        StringView templateName, Span<const TemplateArgumentInfo> templateArguments,
        Span<const ColumnInfo> columns, StringView sqlConstraint, StringView virtualTableUsing,
        bool isWithoutRowId) :
        TemplatableTypeInfoBase(schemaName, SchemaType::SQL_TABLE, CppType::SQL_TABLE,
                templateName, templateArguments),
        m_columns(columns), m_sqlConstraint(sqlConstraint), m_virtualTableUsing(virtualTableUsing),
        m_isWithoutRowId(isWithoutRowId)
{}

Span<const ColumnInfo> SqlTableTypeInfo::getColumns() const
{
    return m_columns;
}

StringView SqlTableTypeInfo::getSqlConstraint() const
{
    return m_sqlConstraint;
}

StringView SqlTableTypeInfo::getVirtualTableUsing() const
{
    return m_virtualTableUsing;
}

bool SqlTableTypeInfo::isWithoutRowId() const
{
    return m_isWithoutRowId;
}

SqlDatabaseTypeInfo::SqlDatabaseTypeInfo(StringView schemaName, Span<const TableInfo> tables) :
        TypeInfoBase(schemaName, SchemaType::SQL_DATABASE, CppType::SQL_DATABASE),
        m_tables(tables)
{}

Span<const TableInfo> SqlDatabaseTypeInfo::getTables() const
{
    return m_tables;
}

TypeInfoWithUnderlyingTypeBase::TypeInfoWithUnderlyingTypeBase(StringView schemaName, SchemaType schemaType,
        CppType cppType, const ITypeInfo& underlyingType, Span<const StringView> underlyingTypeArguments) :
        TypeInfoBase(schemaName, schemaType, cppType),
        m_underlyingType(underlyingType), m_underlyingTypeArguments(underlyingTypeArguments)
{}

const ITypeInfo& TypeInfoWithUnderlyingTypeBase::getUnderlyingType() const
{
    return m_underlyingType;
}

Span<const StringView> TypeInfoWithUnderlyingTypeBase::getUnderlyingTypeArguments() const
{
    return m_underlyingTypeArguments;
}

EnumTypeInfo::EnumTypeInfo(StringView schemaName, const ITypeInfo& underlyingType,
        Span<const StringView> underlyingTypeArguments, Span<const ItemInfo> enumItems) :
        TypeInfoWithUnderlyingTypeBase(schemaName, SchemaType::ENUM, CppType::ENUM,
                underlyingType, underlyingTypeArguments),
        m_enumItems(enumItems)
{}

Span<const ItemInfo> EnumTypeInfo::getEnumItems() const
{
    return m_enumItems;
}

BitmaskTypeInfo::BitmaskTypeInfo(StringView schemaName, const ITypeInfo& underlyingType,
        Span<const StringView> underlyingTypeArguments, Span<const ItemInfo> bitmaskValues) :
        TypeInfoWithUnderlyingTypeBase(schemaName, SchemaType::BITMASK, CppType::BITMASK,
                underlyingType, underlyingTypeArguments),
        m_bitmaskValues(bitmaskValues)
{}

Span<const ItemInfo> BitmaskTypeInfo::getBitmaskValues() const
{
    return m_bitmaskValues;
}

PubsubTypeInfo::PubsubTypeInfo(StringView schemaName, Span<const MessageInfo> messages) :
        TypeInfoBase(schemaName, SchemaType::PUBSUB, CppType::PUBSUB), m_messages(messages)
{}

Span<const MessageInfo> PubsubTypeInfo::getMessages() const
{
    return m_messages;
}

ServiceTypeInfo::ServiceTypeInfo(StringView schemaName, Span<const MethodInfo> methods) :
        TypeInfoBase(schemaName, SchemaType::SERVICE, CppType::SERVICE), m_methods(methods)
{}

Span<const MethodInfo> ServiceTypeInfo::getMethods() const
{
    return m_methods;
}

StringView RecursiveTypeInfo::getSchemaName() const
{
    return m_typeInfoFunc().getSchemaName();
}

SchemaType RecursiveTypeInfo::getSchemaType() const
{
    return m_typeInfoFunc().getSchemaType();
}

CppType RecursiveTypeInfo::getCppType() const
{
    return m_typeInfoFunc().getCppType();
}

uint8_t RecursiveTypeInfo::getBitSize() const
{
    return m_typeInfoFunc().getBitSize();
}

Span<const FieldInfo> RecursiveTypeInfo::getFields() const
{
    return m_typeInfoFunc().getFields();
}

Span<const ParameterInfo> RecursiveTypeInfo::getParameters() const
{
    return m_typeInfoFunc().getParameters();
}

Span<const FunctionInfo> RecursiveTypeInfo::getFunctions() const
{
    return m_typeInfoFunc().getFunctions();
}

StringView RecursiveTypeInfo::getSelector() const
{
    return m_typeInfoFunc().getSelector();
}

Span<const CaseInfo> RecursiveTypeInfo::getCases() const
{
    return m_typeInfoFunc().getCases();
}

const ITypeInfo& RecursiveTypeInfo::getUnderlyingType() const
{
    return m_typeInfoFunc().getUnderlyingType();
}

Span<const StringView> RecursiveTypeInfo::getUnderlyingTypeArguments() const
{
    return m_typeInfoFunc().getUnderlyingTypeArguments();
}

Span<const ItemInfo> RecursiveTypeInfo::getEnumItems() const
{
    return m_typeInfoFunc().getEnumItems();
}

Span<const ItemInfo> RecursiveTypeInfo::getBitmaskValues() const
{
    return m_typeInfoFunc().getBitmaskValues();
}

Span<const ColumnInfo> RecursiveTypeInfo::getColumns() const
{
    return m_typeInfoFunc().getColumns();
}

StringView RecursiveTypeInfo::getSqlConstraint() const
{
    return m_typeInfoFunc().getSqlConstraint();
}

StringView RecursiveTypeInfo::getVirtualTableUsing() const
{
    return m_typeInfoFunc().getVirtualTableUsing();
}

bool RecursiveTypeInfo::isWithoutRowId() const
{
    return m_typeInfoFunc().isWithoutRowId();
}

Span<const TableInfo> RecursiveTypeInfo::getTables() const
{
    return m_typeInfoFunc().getTables();
}

StringView RecursiveTypeInfo::getTemplateName() const
{
    return m_typeInfoFunc().getTemplateName();
}
Span<const TemplateArgumentInfo> RecursiveTypeInfo::getTemplateArguments() const
{
    return m_typeInfoFunc().getTemplateArguments();
}

Span<const MessageInfo> RecursiveTypeInfo::getMessages() const
{
    return m_typeInfoFunc().getMessages();
}

Span<const MethodInfo> RecursiveTypeInfo::getMethods() const
{
    return m_typeInfoFunc().getMethods();
}

} // namespace zserio
