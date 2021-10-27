#include "TypeInfoUtil.h"

namespace zserio
{

bool TypeInfoUtil::isCompound(const ITypeInfo& typeInfo)
{
    return isCompound(typeInfo.getSchemaType());
}

bool TypeInfoUtil::isCompound(const SchemaType& schemaType)
{
    switch (schemaType)
    {
    case SchemaType::STRUCT:
    case SchemaType::CHOICE:
    case SchemaType::UNION:
        return true;
    default:
        return false;
    }
}

bool TypeInfoUtil::isFixedSize(const ITypeInfo& typeInfo)
{
    return isFixedSize(typeInfo.getSchemaType());
}

bool TypeInfoUtil::isFixedSize(const SchemaType& schemaType)
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
    default:
        return false;
    }
}

bool TypeInfoUtil::isIntegral(const ITypeInfo& typeInfo)
{
    return isIntegral(typeInfo.getSchemaType());
}

bool TypeInfoUtil::isIntegral(const SchemaType& schemaType)
{
    switch (schemaType)
    {
    // TODO[Mi-L@]: What about BOOL?
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
    case SchemaType::FIXED_SIGNED_BITFIELD:
    case SchemaType::FIXED_UNSIGNED_BITFIELD:
    case SchemaType::DYNAMIC_SIGNED_BITFIELD:
    case SchemaType::DYNAMIC_UNSIGNED_BITFIELD:
        return true;
    default:
        return false;
    };
}

bool TypeInfoUtil::isSigned(const ITypeInfo& typeInfo)
{
    return isSigned(typeInfo.getSchemaType());
}

bool TypeInfoUtil::isSigned(const SchemaType& schemaType)
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
        return true;
    default:
        return false;
    };
}

bool TypeInfoUtil::isFloating(const ITypeInfo& typeInfo)
{
    return isFloating(typeInfo.getSchemaType());
}

bool TypeInfoUtil::isFloating(const SchemaType& schemaType)
{
    switch (schemaType)
    {
    case SchemaType::FLOAT16:
    case SchemaType::FLOAT32:
    case SchemaType::FLOAT64:
        return true;
    default:
        return false;
    }
}

} // namespace zserio
