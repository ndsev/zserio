#ifndef ZSERIO_TYPE_INFO_UTIL_INC_H
#define ZSERIO_TYPE_INFO_UTIL_INC_H

#include "ITypeInfo.h"

namespace zserio
{

struct TypeInfoUtil
{
    static bool isCompound(const ITypeInfo& typeInfo);
    static bool isCompound(const SchemaType& schemaType);

    static bool isFixedSize(const ITypeInfo& typeInfo);
    static bool isFixedSize(const SchemaType& schemaType);

    static bool isIntegral(const ITypeInfo& typeInfo);
    static bool isIntegral(const SchemaType& schemaType);

    static bool isSigned(const ITypeInfo& typeInfo);
    static bool isSigned(const SchemaType& schemaType);

    static bool isFloating(const ITypeInfo& typeInfo);
    static bool isFloating(const SchemaType& schemaType);
};

} // namespace zserio

#endif // ZSERIO_TYPE_INFO_UTIL_INC_H
