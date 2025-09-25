#include "zserio/TypeInfoUtil.h"

namespace zserio
{

bool TypeInfoUtil::isCompound(SchemaType schemaType)
{
    return schemaType == SchemaType::STRUCT || schemaType == SchemaType::CHOICE ||
            schemaType == SchemaType::UNION;
}

bool TypeInfoUtil::isCompound(CppType cppType)
{
    return cppType == CppType::STRUCT || cppType == CppType::CHOICE || cppType == CppType::UNION;
}

bool TypeInfoUtil::hasChoice(SchemaType schemaType)
{
    return schemaType == SchemaType::CHOICE || schemaType == SchemaType::UNION;
}

bool TypeInfoUtil::hasChoice(CppType cppType)
{
    return cppType == CppType::CHOICE || cppType == CppType::UNION;
}

bool TypeInfoUtil::isFixedSize(SchemaType schemaType)
{
    switch (schemaType)
    {
    case SchemaType::BOOL:
    case SchemaType::INT8:
    case SchemaType::INT16:
    case SchemaType::INT32:
    case SchemaType::INT64:
    case SchemaType::UINT8:
    case SchemaType::UINT16:
    case SchemaType::UINT32:
    case SchemaType::UINT64:
    case SchemaType::FIXED_SIGNED_BITFIELD:
    case SchemaType::FIXED_UNSIGNED_BITFIELD:
    case SchemaType::FLOAT16:
    case SchemaType::FLOAT32:
    case SchemaType::FLOAT64:
        return true;
    case SchemaType::VARINT16:
    case SchemaType::VARINT32:
    case SchemaType::VARINT64:
    case SchemaType::VARINT:
    case SchemaType::VARUINT16:
    case SchemaType::VARUINT32:
    case SchemaType::VARUINT64:
    case SchemaType::VARUINT:
    case SchemaType::VARSIZE:
    case SchemaType::DYNAMIC_SIGNED_BITFIELD:
    case SchemaType::DYNAMIC_UNSIGNED_BITFIELD:
    case SchemaType::BYTES:
    case SchemaType::STRING:
    case SchemaType::EXTERN:
    case SchemaType::ENUM:
    case SchemaType::BITMASK:
    case SchemaType::STRUCT:
    case SchemaType::CHOICE:
    case SchemaType::UNION:
    case SchemaType::SQL_TABLE:
    case SchemaType::SQL_DATABASE:
    case SchemaType::SERVICE:
    case SchemaType::PUBSUB:
    default:
        return false;
    }
}

bool TypeInfoUtil::isFixedSize(CppType cppType)
{
    switch (cppType)
    {
    case CppType::BOOL:
    case CppType::INT8:
    case CppType::INT16:
    case CppType::INT32:
    case CppType::INT64:
    case CppType::UINT8:
    case CppType::UINT16:
    case CppType::UINT32:
    case CppType::UINT64:
    case CppType::FLOAT:
    case CppType::DOUBLE:
        return true;
    case CppType::BYTES:
    case CppType::STRING:
    case CppType::BIT_BUFFER:
    case CppType::ENUM:
    case CppType::BITMASK:
    case CppType::STRUCT:
    case CppType::CHOICE:
    case CppType::UNION:
    case CppType::SQL_TABLE:
    case CppType::SQL_DATABASE:
    case CppType::SERVICE:
    case CppType::PUBSUB:
    default:
        return false;
    }
}

bool TypeInfoUtil::isIntegral(SchemaType schemaType)
{
    switch (schemaType)
    {
    case SchemaType::BOOL:
    case SchemaType::INT8:
    case SchemaType::INT16:
    case SchemaType::INT32:
    case SchemaType::INT64:
    case SchemaType::UINT8:
    case SchemaType::UINT16:
    case SchemaType::UINT32:
    case SchemaType::UINT64:
    case SchemaType::VARINT16:
    case SchemaType::VARINT32:
    case SchemaType::VARINT64:
    case SchemaType::VARINT:
    case SchemaType::VARUINT16:
    case SchemaType::VARUINT32:
    case SchemaType::VARUINT64:
    case SchemaType::VARUINT:
    case SchemaType::VARSIZE:
    case SchemaType::FIXED_SIGNED_BITFIELD:
    case SchemaType::FIXED_UNSIGNED_BITFIELD:
    case SchemaType::DYNAMIC_SIGNED_BITFIELD:
    case SchemaType::DYNAMIC_UNSIGNED_BITFIELD:
        return true;
    case SchemaType::FLOAT16:
    case SchemaType::FLOAT32:
    case SchemaType::FLOAT64:
    case SchemaType::BYTES:
    case SchemaType::STRING:
    case SchemaType::EXTERN:
    case SchemaType::ENUM:
    case SchemaType::BITMASK:
    case SchemaType::STRUCT:
    case SchemaType::CHOICE:
    case SchemaType::UNION:
    case SchemaType::SQL_TABLE:
    case SchemaType::SQL_DATABASE:
    case SchemaType::SERVICE:
    case SchemaType::PUBSUB:
    default:
        return false;
    }
}

bool TypeInfoUtil::isIntegral(CppType cppType)
{
    switch (cppType)
    {
    case CppType::BOOL:
    case CppType::INT8:
    case CppType::INT16:
    case CppType::INT32:
    case CppType::INT64:
    case CppType::UINT8:
    case CppType::UINT16:
    case CppType::UINT32:
    case CppType::UINT64:
        return true;
    case CppType::FLOAT:
    case CppType::DOUBLE:
    case CppType::BYTES:
    case CppType::STRING:
    case CppType::BIT_BUFFER:
    case CppType::ENUM:
    case CppType::BITMASK:
    case CppType::STRUCT:
    case CppType::CHOICE:
    case CppType::UNION:
    case CppType::SQL_TABLE:
    case CppType::SQL_DATABASE:
    case CppType::SERVICE:
    case CppType::PUBSUB:
    default:
        return false;
    }
}

bool TypeInfoUtil::isSigned(SchemaType schemaType)
{
    switch (schemaType)
    {
    case SchemaType::INT8:
    case SchemaType::INT16:
    case SchemaType::INT32:
    case SchemaType::INT64:
    case SchemaType::VARINT16:
    case SchemaType::VARINT32:
    case SchemaType::VARINT64:
    case SchemaType::VARINT:
    case SchemaType::FIXED_SIGNED_BITFIELD:
    case SchemaType::DYNAMIC_SIGNED_BITFIELD:
    case SchemaType::FLOAT16:
    case SchemaType::FLOAT32:
    case SchemaType::FLOAT64:
        return true;
    case SchemaType::BOOL:
    case SchemaType::UINT8:
    case SchemaType::UINT16:
    case SchemaType::UINT32:
    case SchemaType::UINT64:
    case SchemaType::VARUINT16:
    case SchemaType::VARUINT32:
    case SchemaType::VARUINT64:
    case SchemaType::VARUINT:
    case SchemaType::VARSIZE:
    case SchemaType::FIXED_UNSIGNED_BITFIELD:
    case SchemaType::DYNAMIC_UNSIGNED_BITFIELD:
    case SchemaType::BYTES:
    case SchemaType::STRING:
    case SchemaType::EXTERN:
    case SchemaType::ENUM:
    case SchemaType::BITMASK:
    case SchemaType::STRUCT:
    case SchemaType::CHOICE:
    case SchemaType::UNION:
    case SchemaType::SQL_TABLE:
    case SchemaType::SQL_DATABASE:
    case SchemaType::SERVICE:
    case SchemaType::PUBSUB:
    default:
        return false;
    }
}

bool TypeInfoUtil::isSigned(CppType cppType)
{
    return cppType == CppType::INT8 || cppType == CppType::INT16 || cppType == CppType::INT32 ||
            cppType == CppType::INT64 || cppType == CppType::FLOAT || cppType == CppType::DOUBLE;
}

bool TypeInfoUtil::isFloatingPoint(SchemaType schemaType)
{
    return schemaType == SchemaType::FLOAT16 || schemaType == SchemaType::FLOAT32 ||
            schemaType == SchemaType::FLOAT64;
}

bool TypeInfoUtil::isFloatingPoint(CppType cppType)
{
    return cppType == CppType::FLOAT || cppType == CppType::DOUBLE;
}

} // namespace zserio
