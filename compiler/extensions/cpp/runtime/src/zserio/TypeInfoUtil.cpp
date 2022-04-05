#include "zserio/TypeInfoUtil.h"

namespace zserio
{

bool TypeInfoUtil::isCompound(SchemaType schemaType)
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

bool TypeInfoUtil::isCompound(CppType cppType)
{
    switch (cppType)
    {
    case CppType::STRUCT:
    case CppType::CHOICE:
    case CppType::UNION:
        return true;
    default:
        return false;
    }
}

bool TypeInfoUtil::hasChoice(SchemaType schemaType)
{
    switch (schemaType)
    {
    case SchemaType::CHOICE:
    case SchemaType::UNION:
        return true;
    default:
        return false;
    }
}

bool TypeInfoUtil::hasChoice(CppType cppType)
{
    switch (cppType)
    {
    case CppType::CHOICE:
    case CppType::UNION:
        return true;
    default:
        return false;
    }
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
    default:
        return false;
    }
}

bool TypeInfoUtil::isSigned(CppType cppType)
{
    switch (cppType)
    {
    case CppType::INT8:
    case CppType::INT16:
    case CppType::INT32:
    case CppType::INT64:
    case CppType::FLOAT:
    case CppType::DOUBLE:
        return true;
    default:
        return false;
    }
}

bool TypeInfoUtil::isFloatingPoint(SchemaType schemaType)
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

bool TypeInfoUtil::isFloatingPoint(CppType cppType)
{
    switch (cppType)
    {
    case CppType::FLOAT:
    case CppType::DOUBLE:
        return true;
    default:
        return false;
    }
}

} // namespace zserio
