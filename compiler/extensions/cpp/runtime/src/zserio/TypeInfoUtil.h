#ifndef ZSERIO_TYPE_INFO_UTIL_INC_H
#define ZSERIO_TYPE_INFO_UTIL_INC_H

#include "ITypeInfo.h"

namespace zserio
{

struct TypeInfoUtil
{
    static bool isCompound(SchemaType schemaType);
    static bool isCompound(CppType cppType);

    static bool hasChoice(SchemaType schemaType);
    static bool hasChoice(CppType cppType);

    static bool isFixedSize(SchemaType schemaType);
    static bool isFixedSize(CppType cppType);

    static bool isIntegral(SchemaType schemaType);
    static bool isIntegral(CppType cppType);

    static bool isSigned(SchemaType schemaType);
    static bool isSigned(CppType cppType);

    static bool isFloating(SchemaType schemaType);
    static bool isFloating(CppType cppType);
};

} // namespace zserio

#endif // ZSERIO_TYPE_INFO_UTIL_INC_H
