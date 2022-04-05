#ifndef ZSERIO_TYPE_INFO_UTIL_INC_H
#define ZSERIO_TYPE_INFO_UTIL_INC_H

#include "zserio/ITypeInfo.h"

namespace zserio
{

/**
 * The type information helper utilities to check zserio schema and C++ types.
 */
struct TypeInfoUtil
{
    /**
     * Checks if zserio type is a compound type.
     *
     * \return true if zserio type is a compound type, otherwise false.
     */
    static bool isCompound(SchemaType schemaType);

    /**
     * Checks if C++ type is a compound type.
     *
     * \return true if C++ type is a compound type, otherwise false.
     */
    static bool isCompound(CppType cppType);

    /**
     * Checks if zserio type is a choice or union type.
     *
     * \return true if zserio type is a choice or union type, otherwise false.
     */
    static bool hasChoice(SchemaType schemaType);

    /**
     * Checks if C++ type is a choice or union type.
     *
     * \return true if C++ type is a choice or union type, otherwise false.
     */
    static bool hasChoice(CppType cppType);

    /**
     * Checks if zserio type is a fixed sized type.
     *
     * \return true if zserio type is a fixed sized type, otherwise false.
     */
    static bool isFixedSize(SchemaType schemaType);

    /**
     * Checks if C++ type is a fixed sized type.
     *
     * \return true if C++ type is a fixed sized type, otherwise false.
     */
    static bool isFixedSize(CppType cppType);

    /**
     * Checks if zserio type is a integral type.
     *
     * \return true if zserio type is a integral type, otherwise false.
     */
    static bool isIntegral(SchemaType schemaType);

    /**
     * Checks if C++ type is a integral type.
     *
     * \return true if C++ type is a integral type, otherwise false.
     */
    static bool isIntegral(CppType cppType);

    /**
     * Checks if zserio type is a signed type.
     *
     * \return true if zserio type is a signed type, otherwise false.
     */
    static bool isSigned(SchemaType schemaType);

    /**
     * Checks if C++ type is a signed type.
     *
     * \return true if C++ type is a signed type, otherwise false.
     */
    static bool isSigned(CppType cppType);

    /**
     * Checks if zserio type is a floating point type.
     *
     * \return true if zserio type is a floating point type, otherwise false.
     */
    static bool isFloatingPoint(SchemaType schemaType);

    /**
     * Checks if C++ type is a floating point type.
     *
     * \return true if C++ type is a floating point type, otherwise false.
     */
    static bool isFloatingPoint(CppType cppType);
};

} // namespace zserio

#endif // ZSERIO_TYPE_INFO_UTIL_INC_H
