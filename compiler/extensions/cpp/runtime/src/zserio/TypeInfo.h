#ifndef ZSERIO_TYPE_INFO_INC_H
#define ZSERIO_TYPE_INFO_INC_H

#include <algorithm>
#include <memory>
#include <string>

#include "zserio/AnyHolder.h"
#include "zserio/BitBuffer.h"
#include "zserio/CppRuntimeException.h"
#include "zserio/ITypeInfo.h"

namespace zserio
{

/**
 * Type information abstract base class.
 *
 * This base class implements fully the methods getSchemaName(), getSchemaName() and getCppType(). All other
 * interface methods just throw an exception.
 */
template <typename ALLOC>
class TypeInfoBase : public IBasicTypeInfo<ALLOC>
{
public:
    /**
     * Constructor.
     *
     * \param schemaName The schema name to be stored in type information.
     * \param schemaType The schema type to be stored in type information.
     * \param cppType The C++ type to be stored in type information.
     */
    TypeInfoBase(StringView schemaName, SchemaType schemaType, CppType cppType);

    /**
     * Copying and moving is disallowed!
     * \{
     */
    TypeInfoBase(const TypeInfoBase&) = delete;
    TypeInfoBase& operator=(const TypeInfoBase&) = delete;

    TypeInfoBase(const TypeInfoBase&&) = delete;
    TypeInfoBase& operator=(const TypeInfoBase&&) = delete;
    /**
     * \}
     */

    ~TypeInfoBase() override = 0;

    StringView getSchemaName() const override;
    SchemaType getSchemaType() const override;
    CppType getCppType() const override;
    uint8_t getBitSize() const override;

    Span<const BasicFieldInfo<ALLOC>> getFields() const override;
    Span<const BasicParameterInfo<ALLOC>> getParameters() const override;
    Span<const BasicFunctionInfo<ALLOC>> getFunctions() const override;

    StringView getSelector() const override;
    Span<const BasicCaseInfo<ALLOC>> getCases() const override;

    const IBasicTypeInfo<ALLOC>& getUnderlyingType() const override;
    Span<const StringView> getUnderlyingTypeArguments() const override;
    Span<const ItemInfo> getEnumItems() const override;
    Span<const ItemInfo> getBitmaskValues() const override;

    Span<const BasicColumnInfo<ALLOC>> getColumns() const override;
    StringView getSqlConstraint() const override;
    StringView getVirtualTableUsing() const override;
    bool isWithoutRowId() const override;

    Span<const BasicTableInfo<ALLOC>> getTables() const override;

    StringView getTemplateName() const override;
    Span<const BasicTemplateArgumentInfo<ALLOC>> getTemplateArguments() const override;

    Span<const BasicMessageInfo<ALLOC>> getMessages() const override;
    Span<const BasicMethodInfo<ALLOC>> getMethods() const override;

    IBasicReflectablePtr<ALLOC> createInstance(const ALLOC& allocator) const override;
    IBasicReflectablePtr<ALLOC> createInstance() const override;

private:
    StringView m_schemaName;
    SchemaType m_schemaType;
    CppType m_cppType;
};

/**
 * Type information abstract base class for builtin types.
 */
template <typename ALLOC = std::allocator<uint8_t>>
class BuiltinTypeInfo : public TypeInfoBase<ALLOC>
{
protected:
    /**
     * Constructor.
     *
     * \param schemaName The schema name to be stored in type information.
     * \param schemaType The schema type to be stored in type information.
     * \param cppType The C++ type to be stored in type information.
     */
    BuiltinTypeInfo(StringView schemaName, SchemaType schemaType, CppType cppType);

public:
    /**
     * Gets the type information of bool schema type.
     *
     * \return Reference to the type information of bool schema type.
     */
    static const IBasicTypeInfo<ALLOC>& getBool();

    /**
     * Gets the type information of int8 schema type.
     *
     * \return Reference to the type information of int8 schema type.
     */
    static const IBasicTypeInfo<ALLOC>& getInt8();

    /**
     * Gets the type information of int16 schema type.
     *
     * \return Reference to the type information of int16 schema type.
     */
    static const IBasicTypeInfo<ALLOC>& getInt16();

    /**
     * Gets the type information of int32 schema type.
     *
     * \return Reference to the type information of int32 schema type.
     */
    static const IBasicTypeInfo<ALLOC>& getInt32();

    /**
     * Gets the type information of int64 schema type.
     *
     * \return Reference to the type information of int64 schema type.
     */
    static const IBasicTypeInfo<ALLOC>& getInt64();

    /**
     * Gets the type information of uint8 schema type.
     *
     * \return Reference to the type information of uint8 schema type.
     */
    static const IBasicTypeInfo<ALLOC>& getUInt8();

    /**
     * Gets the type information of uint16 schema type.
     *
     * \return Reference to the type information of uint16 schema type.
     */
    static const IBasicTypeInfo<ALLOC>& getUInt16();

    /**
     * Gets the type information of uint32 schema type.
     *
     * \return Reference to the type information of uint32 schema type.
     */
    static const IBasicTypeInfo<ALLOC>& getUInt32();

    /**
     * Gets the type information of uint64 schema type.
     *
     * \return Reference to the type information of uint64 schema type.
     */
    static const IBasicTypeInfo<ALLOC>& getUInt64();

    /**
     * Gets the type information of varint16 schema type.
     *
     * \return Reference to the type information of varint16 schema type.
     */
    static const IBasicTypeInfo<ALLOC>& getVarInt16();

    /**
     * Gets the type information of varint32 schema type.
     *
     * \return Reference to the type information of varint32 schema type.
     */
    static const IBasicTypeInfo<ALLOC>& getVarInt32();

    /**
     * Gets the type information of varint64 schema type.
     *
     * \return Reference to the type information of varint64 schema type.
     */
    static const IBasicTypeInfo<ALLOC>& getVarInt64();

    /**
     * Gets the type information of varint schema type.
     *
     * \return Reference to the type information of varint schema type.
     */
    static const IBasicTypeInfo<ALLOC>& getVarInt();

    /**
     * Gets the type information of varuint16 schema type.
     *
     * \return Reference to the type information of varuint16 schema type.
     */
    static const IBasicTypeInfo<ALLOC>& getVarUInt16();

    /**
     * Gets the type information of varuint32 schema type.
     *
     * \return Reference to the type information of varuint32 schema type.
     */
    static const IBasicTypeInfo<ALLOC>& getVarUInt32();

    /**
     * Gets the type information of varuint64 schema type.
     *
     * \return Reference to the type information of varuint64 schema type.
     */
    static const IBasicTypeInfo<ALLOC>& getVarUInt64();

    /**
     * Gets the type information of varuint schema type.
     *
     * \return Reference to the type information of varuint schema type.
     */
    static const IBasicTypeInfo<ALLOC>& getVarUInt();

    /**
     * Gets the type information of varsize schema type.
     *
     * \return Reference to the type information of varsize schema type.
     */
    static const IBasicTypeInfo<ALLOC>& getVarSize();

    /**
     * Gets the type information of float16 schema type.
     *
     * \return Reference to the type information of float16 schema type.
     */
    static const IBasicTypeInfo<ALLOC>& getFloat16();

    /**
     * Gets the type information of float32 schema type.
     *
     * \return Reference to the type information of float32 schema type.
     */
    static const IBasicTypeInfo<ALLOC>& getFloat32();

    /**
     * Gets the type information of float64 schema type.
     *
     * \return Reference to the type information of float64 schema type.
     */
    static const IBasicTypeInfo<ALLOC>& getFloat64();

    /**
     * Gets the type information of bytes schema type.
     *
     * \return Reference to the type information of bytes schema type.
     */
    static const IBasicTypeInfo<ALLOC>& getBytes();

    /**
     * Gets the type information of string schema type.
     *
     * \return Reference to the type information of string schema type.
     */
    static const IBasicTypeInfo<ALLOC>& getString();

    /**
     * Gets the type information of extern schema type.
     *
     * \return Reference to the type information of extern schema type.
     */
    static const IBasicTypeInfo<ALLOC>& getBitBuffer();

    /**
     * Gets the type information of fixed signed bit field schema type.
     *
     * \param bitSize The bit size of the bit field.
     *
     * \return Reference to the type information of fixed signed bit field schema type.
     */
    static const IBasicTypeInfo<ALLOC>& getFixedSignedBitField(uint8_t bitSize);

    /**
     * Gets the type information of fixed unsigned bit field schema type.
     *
     * \param bitSize The bit size of the bit field.
     *
     * \return Reference to the type information of fixed unsigned bit field schema type.
     */
    static const IBasicTypeInfo<ALLOC>& getFixedUnsignedBitField(uint8_t bitSize);

    /**
     * Gets the type information of dynamic signed bit field schema type.
     *
     * \param maxBitSize The maximum bit size of the dynamic bit field.
     *
     * \return Reference to the type information of dynamic signed bit field schema type.
     */
    static const IBasicTypeInfo<ALLOC>& getDynamicSignedBitField(uint8_t maxBitSize);

    /**
     * Gets the type information of dynamic unsigned bit field schema type.
     *
     * \param maxBitSize The maximum bit size of the dynamic bit field.
     *
     * \return Reference to the type information of dynamic unsigned bit field schema type.
     */
    static const IBasicTypeInfo<ALLOC>& getDynamicUnsignedBitField(uint8_t maxBitSize);
};

/**
 * Type information abstract base class for fixed size builtin types.
 */
template <typename ALLOC>
class FixedSizeBuiltinTypeInfo : public BuiltinTypeInfo<ALLOC>
{
protected:
    /**
     * Constructor.
     *
     * \param schemaName The schema name to be stored in type information.
     * \param schemaType The schema type to be stored in type information.
     * \param cppType The C++ type to be stored in type information.
     * \param bitSize The bit size of the fixed size integral schema type.
     */
    FixedSizeBuiltinTypeInfo(StringView schemaName, SchemaType schemaType, CppType cppType,
            uint8_t bitSize);

public:
    uint8_t getBitSize() const override;

    /**
     * Gets the type information of bool schema type.
     *
     * \return Reference to the type information of bool schema type.
     */
    static const IBasicTypeInfo<ALLOC>& getBool();

    /**
     * Gets the type information of int8 schema type.
     *
     * \return Reference to the type information of int8 schema type.
     */
    static const IBasicTypeInfo<ALLOC>& getInt8();

    /**
     * Gets the type information of int16 schema type.
     *
     * \return Reference to the type information of int16 schema type.
     */
    static const IBasicTypeInfo<ALLOC>& getInt16();

    /**
     * Gets the type information of int32 schema type.
     *
     * \return Reference to the type information of int32 schema type.
     */
    static const IBasicTypeInfo<ALLOC>& getInt32();

    /**
     * Gets the type information of int64 schema type.
     *
     * \return Reference to the type information of int64 schema type.
     */
    static const IBasicTypeInfo<ALLOC>& getInt64();

    /**
     * Gets the type information of uint8 schema type.
     *
     * \return Reference to the type information of uint8 schema type.
     */
    static const IBasicTypeInfo<ALLOC>& getUInt8();

    /**
     * Gets the type information of uint16 schema type.
     *
     * \return Reference to the type information of uint16 schema type.
     */
    static const IBasicTypeInfo<ALLOC>& getUInt16();

    /**
     * Gets the type information of uint32 schema type.
     *
     * \return Reference to the type information of uint32 schema type.
     */
    static const IBasicTypeInfo<ALLOC>& getUInt32();

    /**
     * Gets the type information of uint64 schema type.
     *
     * \return Reference to the type information of uint64 schema type.
     */
    static const IBasicTypeInfo<ALLOC>& getUInt64();

    /**
     * Gets the type information of float16 schema type.
     *
     * \return Reference to the type information of float16 schema type.
     */
    static const IBasicTypeInfo<ALLOC>& getFloat16();

    /**
     * Gets the type information of float32 schema type.
     *
     * \return Reference to the type information of float32 schema type.
     */
    static const IBasicTypeInfo<ALLOC>& getFloat32();

    /**
     * Gets the type information of float64 schema type.
     *
     * \return Reference to the type information of float64 schema type.
     */
    static const IBasicTypeInfo<ALLOC>& getFloat64();

    /**
     * Gets the type information of fixed signed bit field schema type.
     *
     * \param bitSize The bit size of the bit field.
     *
     * \return Reference to the type information of fixed signed bit field schema type.
     */
    static const IBasicTypeInfo<ALLOC>& getFixedSignedBitField(uint8_t bitSize);

    /**
     * Gets the type information of fixed unsigned bit field schema type.
     *
     * \param bitSize The bit size of the bit field.
     *
     * \return Reference to the type information of fixed unsigned bit field schema type.
     */
    static const IBasicTypeInfo<ALLOC>& getFixedUnsignedBitField(uint8_t bitSize);

private:
    uint8_t m_bitSize;
};

/**
 * Type information abstract base class for templatable types.
 */
template <typename ALLOC>
class TemplatableTypeInfoBase : public TypeInfoBase<ALLOC>
{
public:
    /**
     * Constructor.
     *
     * \param schemaName The schema name to be stored in type information.
     * \param schemaType The schema type to be stored in type information.
     * \param cppType The C++ type to be stored in type information.
     * \param templateName The full schema template name.
     * \param templateArguments The sequence of type informations for template arguments.
     */
    TemplatableTypeInfoBase(StringView schemaName, SchemaType schemaType, CppType cppType,
            StringView templateName, Span<const BasicTemplateArgumentInfo<ALLOC>> templateArguments);

    ~TemplatableTypeInfoBase() override = 0;

    TemplatableTypeInfoBase(const TemplatableTypeInfoBase&) = default;
    TemplatableTypeInfoBase& operator=(const TemplatableTypeInfoBase&) = default;

    TemplatableTypeInfoBase(TemplatableTypeInfoBase&&) = default;
    TemplatableTypeInfoBase& operator=(TemplatableTypeInfoBase&&) = default;

    StringView getTemplateName() const override;
    Span<const BasicTemplateArgumentInfo<ALLOC>> getTemplateArguments() const override;

private:
    StringView m_templateName;
    Span<const BasicTemplateArgumentInfo<ALLOC>> m_templateArguments;
};

/**
 * Type information abstract base class for compound types.
 */
template <typename ALLOC>
class CompoundTypeInfoBase : public TemplatableTypeInfoBase<ALLOC>
{
public:
    using TypeInfoBase<ALLOC>::getSchemaName;
    using CreateInstanceFunc = IBasicReflectablePtr<ALLOC> (*)(const ALLOC&);

    /**
     * Constructor.
     *
     * \param schemaName The schema name to be stored in type information.
     * \param schemaType The schema type to be stored in type information.
     * \param cppType The C++ type to be stored in type information.
     * \param templateName The full schema template name.
     * \param templateArguments The sequence of type informations for template arguments.
     * \param fields The sequence of type informations for fields.
     * \param parameters The sequence of type informations for parameters.
     * \param functions The sequence of type informations for functions.
     */
    CompoundTypeInfoBase(StringView schemaName, CreateInstanceFunc createInstanceFunc,
            SchemaType schemaType, CppType cppType,
            StringView templateName, Span<const BasicTemplateArgumentInfo<ALLOC>> templateArguments,
            Span<const BasicFieldInfo<ALLOC>> fields, Span<const BasicParameterInfo<ALLOC>> parameters,
            Span<const BasicFunctionInfo<ALLOC>> functions);

    ~CompoundTypeInfoBase() override = 0;

    CompoundTypeInfoBase(const CompoundTypeInfoBase&) = default;
    CompoundTypeInfoBase& operator=(const CompoundTypeInfoBase&) = default;

    CompoundTypeInfoBase(CompoundTypeInfoBase&&) = default;
    CompoundTypeInfoBase& operator=(CompoundTypeInfoBase&&) = default;

    Span<const BasicFieldInfo<ALLOC>> getFields() const override;
    Span<const BasicParameterInfo<ALLOC>> getParameters() const override;
    Span<const BasicFunctionInfo<ALLOC>> getFunctions() const override;

    IBasicReflectablePtr<ALLOC> createInstance(const ALLOC& allocator) const override;

private:
    CreateInstanceFunc m_createInstanceFunc;
    Span<const BasicFieldInfo<ALLOC>> m_fields;
    Span<const BasicParameterInfo<ALLOC>> m_parameters;
    Span<const BasicFunctionInfo<ALLOC>> m_functions;
};

/**
 * Type information class for structure types.
 */
template <typename ALLOC>
class StructTypeInfo : public CompoundTypeInfoBase<ALLOC>
{
public:
    using CreateInstanceFunc = typename CompoundTypeInfoBase<ALLOC>::CreateInstanceFunc;

    /**
     * Constructor.
     *
     * \param schemaName The schema name to be stored in type information.
     * \param templateName The full schema template name.
     * \param templateArguments The sequence of type informations for template arguments.
     * \param fields The sequence of type informations for fields.
     * \param parameters The sequence of type informations for parameters.
     * \param functions The sequence of type informations for functions.
     */
    StructTypeInfo(StringView schemaName, CreateInstanceFunc createInstanceFunc,
            StringView templateName, Span<const BasicTemplateArgumentInfo<ALLOC>> templateArguments,
            Span<const BasicFieldInfo<ALLOC>> fields, Span<const BasicParameterInfo<ALLOC>> parameters,
            Span<const BasicFunctionInfo<ALLOC>> functions);
};

/**
 * Type information class for union types.
 */
template <typename ALLOC>
class UnionTypeInfo : public CompoundTypeInfoBase<ALLOC>
{
public:
    using CreateInstanceFunc = typename CompoundTypeInfoBase<ALLOC>::CreateInstanceFunc;

    /**
     * Constructor.
     *
     * \param schemaName The schema name to be stored in type information.
     * \param templateName The full schema template name.
     * \param templateArguments The sequence of type informations for template arguments.
     * \param fields The sequence of type informations for fields.
     * \param parameters The sequence of type informations for parameters.
     * \param functions The sequence of type informations for functions.
     */
    UnionTypeInfo(StringView schemaName, CreateInstanceFunc createInstanceFunc,
            StringView templateName, Span<const BasicTemplateArgumentInfo<ALLOC>> templateArguments,
            Span<const BasicFieldInfo<ALLOC>> fields, Span<const BasicParameterInfo<ALLOC>> parameters,
            Span<const BasicFunctionInfo<ALLOC>> functions);
};

/**
 * Type information class for choice types.
 */
template <typename ALLOC>
class ChoiceTypeInfo : public CompoundTypeInfoBase<ALLOC>
{
public:
    using CreateInstanceFunc = typename CompoundTypeInfoBase<ALLOC>::CreateInstanceFunc;

    /**
     * Constructor.
     *
     * \param schemaName The schema name to be stored in type information.
     * \param templateName The full schema template name.
     * \param templateArguments The sequence of type informations for template arguments.
     * \param fields The sequence of type informations for fields.
     * \param parameters The sequence of type informations for parameters.
     * \param functions The sequence of type informations for functions.
     * \param selector The selector expression.
     * \param cases The sequence of type informations for cases.
     */
    ChoiceTypeInfo(StringView schemaName, CreateInstanceFunc createInstanceFunc,
            StringView templateName, Span<const BasicTemplateArgumentInfo<ALLOC>> templateArguments,
            Span<const BasicFieldInfo<ALLOC>> fields, Span<const BasicParameterInfo<ALLOC>> parameters,
            Span<const BasicFunctionInfo<ALLOC>> functions, StringView selector,
            Span<const BasicCaseInfo<ALLOC>> cases);

    StringView getSelector() const override;
    Span<const BasicCaseInfo<ALLOC>> getCases() const override;

private:
    StringView m_selector;
    Span<const BasicCaseInfo<ALLOC>> m_cases;
};

/**
 * Type information abstract base class for enumeration and bitmask types.
 */
template <typename ALLOC>
class TypeInfoWithUnderlyingTypeBase : public TypeInfoBase<ALLOC>
{
public:
    /**
     * Constructor.
     *
     * \param schemaName The schema name to be stored in type information.
     * \param schemaType The schema type to be stored in type information.
     * \param cppType The C++ type to be stored in type information.
     * \param underlyingType The reference to type information of underlying zserio type.
     * \param underlyingTypeArguments The underlying zserio type arguments.
     */
    TypeInfoWithUnderlyingTypeBase(StringView schemaName, SchemaType schemaType, CppType cppType,
            const IBasicTypeInfo<ALLOC>& underlyingType, Span<const StringView> underlyingTypeArguments);

    const IBasicTypeInfo<ALLOC>& getUnderlyingType() const override;
    Span<const StringView> getUnderlyingTypeArguments() const override;

private:
    const IBasicTypeInfo<ALLOC>& m_underlyingType;
    Span<const StringView> m_underlyingTypeArguments;
};

/**
 * Type information class for enumeration types.
 */
template <typename ALLOC>
class EnumTypeInfo : public TypeInfoWithUnderlyingTypeBase<ALLOC>
{
public:
    /**
     * Constructor.
     *
     * \param schemaName The schema name to be stored in type information.
     * \param underlyingType The reference to type information of underlying zserio type.
     * \param underlyingTypeArguments The underlying zserio type arguments.
     * \param enumItems The sequence of type informations for enumeration items.
     */
    EnumTypeInfo(StringView schemaName, const IBasicTypeInfo<ALLOC>& underlyingType,
            Span<const StringView> underlyingTypeArguments, Span<const ItemInfo> enumItems);

    Span<const ItemInfo> getEnumItems() const override;

private:
    Span<const ItemInfo> m_enumItems;
};

/**
 * Type information class for bitmask types.
 */
template <typename ALLOC>
class BitmaskTypeInfo : public TypeInfoWithUnderlyingTypeBase<ALLOC>
{
public:
    /**
     * Constructor.
     *
     * \param schemaName The schema name to be stored in type information.
     * \param underlyingType The reference to type information of underlying zserio type.
     * \param underlyingTypeArguments The underlying zserio type arguments.
     * \param bitmaskValues The sequence of type informations for bitmask values.
     */
    BitmaskTypeInfo(StringView schemaName, const IBasicTypeInfo<ALLOC>& underlyingType,
            Span<const StringView> underlyingTypeArguments, Span<const ItemInfo> bitmaskValues);

    Span<const ItemInfo> getBitmaskValues() const override;

private:
    Span<const ItemInfo> m_bitmaskValues;
};

/**
 * Type information class for SQL table types.
 */
template <typename ALLOC>
class SqlTableTypeInfo : public TemplatableTypeInfoBase<ALLOC>
{
public:
    /**
     * Constructor.
     *
     * \param schemaName The schema name to be stored in type information.
     * \param templateName The full schema template name.
     * \param templateArguments The sequence of type informations for template arguments.
     * \param columns The sequence of type informations for columns.
     * \param sqlConstraint The SQL table constraint.
     * \param virtualTableUsing The SQL virtual table using specification.
     * \param isWithoutRowId true if SQL table is without row id table, otherwise false.
     */
    SqlTableTypeInfo(StringView schemaName,
            StringView templateName, Span<const BasicTemplateArgumentInfo<ALLOC>> templateArguments,
            Span<const BasicColumnInfo<ALLOC>> columns, StringView sqlConstraint, StringView virtualTableUsing,
            bool isWithoutRowId);

    Span<const BasicColumnInfo<ALLOC>> getColumns() const override;
    StringView getSqlConstraint() const override;
    StringView getVirtualTableUsing() const override;
    bool isWithoutRowId() const override;

private:
    Span<const BasicColumnInfo<ALLOC>> m_columns;
    StringView m_sqlConstraint;
    StringView m_virtualTableUsing;
    bool m_isWithoutRowId;
};

/**
 * Type information class for SQL database types.
 */
template <typename ALLOC>
class SqlDatabaseTypeInfo : public TypeInfoBase<ALLOC>
{
public:
    /**
     * Constructor.
     *
     * \param schemaName The schema name to be stored in type information.
     * \param tables The sequence of type informations for tables.
     */
    SqlDatabaseTypeInfo(StringView schemaName, Span<const BasicTableInfo<ALLOC>> tables);

    Span<const BasicTableInfo<ALLOC>> getTables() const override;

private:
    Span<const BasicTableInfo<ALLOC>> m_tables;
};

/**
 * Type information class for pubsub types.
 */
template <typename ALLOC>
class PubsubTypeInfo : public TypeInfoBase<ALLOC>
{
public:
    /**
     * Constructor.
     *
     * \param schemaName The schema name to be stored in type information.
     * \param messages The sequence of type informations for pubsub messages.
     */
    PubsubTypeInfo(StringView schemaName, Span<const BasicMessageInfo<ALLOC>> messages);

    Span<const BasicMessageInfo<ALLOC>> getMessages() const override;

private:
    Span<const BasicMessageInfo<ALLOC>> m_messages;
};

/**
 * Type information class for service types.
 */
template <typename ALLOC>
class ServiceTypeInfo : public TypeInfoBase<ALLOC>
{
public:
    /**
     * Constructor.
     *
     * \param schemaName The schema name to be stored in type information.
     * \param methods The sequence of type informations for service methods.
     */
    ServiceTypeInfo(StringView schemaName, Span<const BasicMethodInfo<ALLOC>> methods);

    Span<const BasicMethodInfo<ALLOC>> getMethods() const override;

private:
    Span<const BasicMethodInfo<ALLOC>> m_methods;
};

/**
 * Type info for recursive types used as a wrapper around generated static typeInfo method to prevent
 * infinite recursion in type info definition.
 */
template <typename ALLOC>
class RecursiveTypeInfo : public IBasicTypeInfo<ALLOC>
{
public:
    /** Typedef to pointer to static typeInfo method on generated objects. */
    using TypeInfoFunc = const IBasicTypeInfo<ALLOC>& (*)();

    /**
     * Constructor.
     *
     * \param typeInfoFunc Pointer to static typeInfo method.
     */
    explicit RecursiveTypeInfo(TypeInfoFunc typeInfoFunc) :
            m_typeInfoFunc(typeInfoFunc)
    {}

    ~RecursiveTypeInfo() override = default;

    /**
     * Copying and moving is disallowed!
     * \{
     */
    RecursiveTypeInfo(const RecursiveTypeInfo&) = delete;
    RecursiveTypeInfo& operator=(const RecursiveTypeInfo&) = delete;

    RecursiveTypeInfo(const RecursiveTypeInfo&&) = delete;
    RecursiveTypeInfo& operator=(const RecursiveTypeInfo&&) = delete;
    /**
     * \}
     */

    StringView getSchemaName() const override;
    SchemaType getSchemaType() const override;
    CppType getCppType() const override;
    uint8_t getBitSize() const override;

    Span<const BasicFieldInfo<ALLOC>> getFields() const override;
    Span<const BasicParameterInfo<ALLOC>> getParameters() const override;
    Span<const BasicFunctionInfo<ALLOC>> getFunctions() const override;

    StringView getSelector() const override;
    Span<const BasicCaseInfo<ALLOC>> getCases() const override;

    const IBasicTypeInfo<ALLOC>& getUnderlyingType() const override;
    Span<const StringView> getUnderlyingTypeArguments() const override;
    Span<const ItemInfo> getEnumItems() const override;
    Span<const ItemInfo> getBitmaskValues() const override;

    Span<const BasicColumnInfo<ALLOC>> getColumns() const override;
    StringView getSqlConstraint() const override;
    StringView getVirtualTableUsing() const override;
    bool isWithoutRowId() const override;

    Span<const BasicTableInfo<ALLOC>> getTables() const override;

    StringView getTemplateName() const override;
    Span<const BasicTemplateArgumentInfo<ALLOC>> getTemplateArguments() const override;

    Span<const BasicMessageInfo<ALLOC>> getMessages() const override;
    Span<const BasicMethodInfo<ALLOC>> getMethods() const override;

    IBasicReflectablePtr<ALLOC> createInstance(const ALLOC& allocator) const override;
    IBasicReflectablePtr<ALLOC> createInstance() const override;

private:
    TypeInfoFunc m_typeInfoFunc;
};

template <typename ALLOC>
TypeInfoBase<ALLOC>::TypeInfoBase(StringView schemaName, SchemaType schemaType, CppType cppType) :
        m_schemaName(schemaName), m_schemaType(schemaType), m_cppType(cppType)
{}

template <typename ALLOC>
TypeInfoBase<ALLOC>::~TypeInfoBase() = default;

template <typename ALLOC>
StringView TypeInfoBase<ALLOC>::getSchemaName() const
{
    return m_schemaName;
}

template <typename ALLOC>
SchemaType TypeInfoBase<ALLOC>::getSchemaType() const
{
    return m_schemaType;
}

template <typename ALLOC>
CppType TypeInfoBase<ALLOC>::getCppType() const
{
    return m_cppType;
}

template <typename ALLOC>
uint8_t TypeInfoBase<ALLOC>::getBitSize() const
{
    throw CppRuntimeException("Type '") << getSchemaName() << "' is not a fixed size type!";
}

template <typename ALLOC>
Span<const BasicFieldInfo<ALLOC>> TypeInfoBase<ALLOC>::getFields() const
{
    throw CppRuntimeException("Type '") << getSchemaName() << "' is not a compound type!";
}

template <typename ALLOC>
Span<const BasicParameterInfo<ALLOC>> TypeInfoBase<ALLOC>::getParameters() const
{
    throw CppRuntimeException("Type '") << getSchemaName() << "' is not a compound type!";
}

template <typename ALLOC>
Span<const BasicFunctionInfo<ALLOC>> TypeInfoBase<ALLOC>::getFunctions() const
{
    throw CppRuntimeException("Type '") << getSchemaName() << "' is not a compound type!";
}

template <typename ALLOC>
StringView TypeInfoBase<ALLOC>::getSelector() const
{
    throw CppRuntimeException("Type '") << getSchemaName() << "' is not a choice type!";
}

template <typename ALLOC>
Span<const BasicCaseInfo<ALLOC>> TypeInfoBase<ALLOC>::getCases() const
{
    throw CppRuntimeException("Type '") << getSchemaName() << "' is not a choice type!";
}

template <typename ALLOC>
const IBasicTypeInfo<ALLOC>& TypeInfoBase<ALLOC>::getUnderlyingType() const
{
    throw CppRuntimeException("Type '") << getSchemaName() << "' does not have underlying type!";
}

template <typename ALLOC>
Span<const StringView> TypeInfoBase<ALLOC>::getUnderlyingTypeArguments() const
{
    throw CppRuntimeException("Type '") << getSchemaName() << "' does not have underlying type!";
}

template <typename ALLOC>
Span<const ItemInfo> TypeInfoBase<ALLOC>::getEnumItems() const
{
    throw CppRuntimeException("Type '") << getSchemaName() << "' is not an enum type!";
}

template <typename ALLOC>
Span<const ItemInfo> TypeInfoBase<ALLOC>::getBitmaskValues() const
{
    throw CppRuntimeException("Type '") << getSchemaName() << "' is not a bitmask type!";
}

template <typename ALLOC>
Span<const BasicColumnInfo<ALLOC>> TypeInfoBase<ALLOC>::getColumns() const
{
    throw CppRuntimeException("Type '") << getSchemaName() << "' is not a SQL table type!";
}

template <typename ALLOC>
StringView TypeInfoBase<ALLOC>::getSqlConstraint() const
{
    throw CppRuntimeException("Type '") << getSchemaName() << "' is not a SQL table type!";
}

template <typename ALLOC>
StringView TypeInfoBase<ALLOC>::getVirtualTableUsing() const
{
    throw CppRuntimeException("Type '") << getSchemaName() << "' is not a SQL table type!";
}

template <typename ALLOC>
bool TypeInfoBase<ALLOC>::isWithoutRowId() const
{
    throw CppRuntimeException("Type '") << getSchemaName() << "' is not a SQL table type!";
}

template <typename ALLOC>
Span<const BasicTableInfo<ALLOC>> TypeInfoBase<ALLOC>::getTables() const
{
    throw CppRuntimeException("Type '") << getSchemaName() << "' is not a SQL database type!";
}

template <typename ALLOC>
StringView TypeInfoBase<ALLOC>::getTemplateName() const
{
    throw CppRuntimeException("Type '") << getSchemaName() << "' is not a templatable type!";
}

template <typename ALLOC>
Span<const BasicTemplateArgumentInfo<ALLOC>> TypeInfoBase<ALLOC>::getTemplateArguments() const
{
    throw CppRuntimeException("Type '") << getSchemaName() << "' is not a templatable type!";
}

template <typename ALLOC>
Span<const BasicMessageInfo<ALLOC>> TypeInfoBase<ALLOC>::getMessages() const
{
    throw CppRuntimeException("Type '") << getSchemaName() << "' is not a pubsub type!";
}

template <typename ALLOC>
Span<const BasicMethodInfo<ALLOC>> TypeInfoBase<ALLOC>::getMethods() const
{
    throw CppRuntimeException("Type '") << getSchemaName() << "' is not a service type!";
}

template <typename ALLOC>
IBasicReflectablePtr<ALLOC> TypeInfoBase<ALLOC>::createInstance(const ALLOC&) const
{
    throw CppRuntimeException("Type '") << getSchemaName() << "' is not a compound type!";
}

template <typename ALLOC>
IBasicReflectablePtr<ALLOC> TypeInfoBase<ALLOC>::createInstance() const
{
    return createInstance(ALLOC());
}

template <typename ALLOC>
BuiltinTypeInfo<ALLOC>::BuiltinTypeInfo(StringView schemaName, SchemaType schemaType, CppType cppType) :
        TypeInfoBase<ALLOC>(schemaName, schemaType, cppType)
{}

template <typename ALLOC>
const IBasicTypeInfo<ALLOC>& BuiltinTypeInfo<ALLOC>::getBool()
{
    return FixedSizeBuiltinTypeInfo<ALLOC>::getBool();
}

template <typename ALLOC>
const IBasicTypeInfo<ALLOC>& BuiltinTypeInfo<ALLOC>::getInt8()
{
    return FixedSizeBuiltinTypeInfo<ALLOC>::getInt8();
}

template <typename ALLOC>
const IBasicTypeInfo<ALLOC>& BuiltinTypeInfo<ALLOC>::getInt16()
{
    return FixedSizeBuiltinTypeInfo<ALLOC>::getInt16();
}

template <typename ALLOC>
const IBasicTypeInfo<ALLOC>& BuiltinTypeInfo<ALLOC>::getInt32()
{
    return FixedSizeBuiltinTypeInfo<ALLOC>::getInt32();
}

template <typename ALLOC>
const IBasicTypeInfo<ALLOC>& BuiltinTypeInfo<ALLOC>::getInt64()
{
    return FixedSizeBuiltinTypeInfo<ALLOC>::getInt64();
}

template <typename ALLOC>
const IBasicTypeInfo<ALLOC>& BuiltinTypeInfo<ALLOC>::getUInt8()
{
    return FixedSizeBuiltinTypeInfo<ALLOC>::getUInt8();
}

template <typename ALLOC>
const IBasicTypeInfo<ALLOC>& BuiltinTypeInfo<ALLOC>::getUInt16()
{
    return FixedSizeBuiltinTypeInfo<ALLOC>::getUInt16();
}

template <typename ALLOC>
const IBasicTypeInfo<ALLOC>& BuiltinTypeInfo<ALLOC>::getUInt32()
{
    return FixedSizeBuiltinTypeInfo<ALLOC>::getUInt32();
}

template <typename ALLOC>
const IBasicTypeInfo<ALLOC>& BuiltinTypeInfo<ALLOC>::getUInt64()
{
    return FixedSizeBuiltinTypeInfo<ALLOC>::getUInt64();
}

template <typename ALLOC>
const IBasicTypeInfo<ALLOC>& BuiltinTypeInfo<ALLOC>::getVarInt16()
{
    static const BuiltinTypeInfo<ALLOC> typeInfo = {
        makeStringView("varint16"), SchemaType::VARINT16, CppType::INT16
    };
    return typeInfo;
}

template <typename ALLOC>
const IBasicTypeInfo<ALLOC>& BuiltinTypeInfo<ALLOC>::getVarInt32()
{
    static const BuiltinTypeInfo<ALLOC> typeInfo = {
        makeStringView("varint32"), SchemaType::VARINT32, CppType::INT32
    };
    return typeInfo;
}

template <typename ALLOC>
const IBasicTypeInfo<ALLOC>& BuiltinTypeInfo<ALLOC>::getVarInt64()
{
    static const BuiltinTypeInfo<ALLOC> typeInfo = {
        makeStringView("varint64"), SchemaType::VARINT64, CppType::INT64
    };
    return typeInfo;
}

template <typename ALLOC>
const IBasicTypeInfo<ALLOC>& BuiltinTypeInfo<ALLOC>::getVarInt()
{
    static const BuiltinTypeInfo<ALLOC> typeInfo = {
        makeStringView("varint"), SchemaType::VARINT, CppType::INT64
    };
    return typeInfo;
}

template <typename ALLOC>
const IBasicTypeInfo<ALLOC>& BuiltinTypeInfo<ALLOC>::getVarUInt16()
{
    static const BuiltinTypeInfo<ALLOC> typeInfo = {
        makeStringView("varuint16"), SchemaType::VARUINT16, CppType::UINT16
    };
    return typeInfo;
}

template <typename ALLOC>
const IBasicTypeInfo<ALLOC>& BuiltinTypeInfo<ALLOC>::getVarUInt32()
{
    static const BuiltinTypeInfo<ALLOC> typeInfo = {
        makeStringView("varuint32"), SchemaType::VARUINT32, CppType::UINT32
    };
    return typeInfo;
}

template <typename ALLOC>
const IBasicTypeInfo<ALLOC>& BuiltinTypeInfo<ALLOC>::getVarUInt64()
{
    static const BuiltinTypeInfo<ALLOC> typeInfo = {
        makeStringView("varuint64"), SchemaType::VARUINT64, CppType::UINT64
    };
    return typeInfo;
}

template <typename ALLOC>
const IBasicTypeInfo<ALLOC>& BuiltinTypeInfo<ALLOC>::getVarUInt()
{
    static const BuiltinTypeInfo<ALLOC> typeInfo = {
        makeStringView("varuint"), SchemaType::VARUINT, CppType::UINT64
    };
    return typeInfo;
}

template <typename ALLOC>
const IBasicTypeInfo<ALLOC>& BuiltinTypeInfo<ALLOC>::getVarSize()
{
    static const BuiltinTypeInfo<ALLOC> typeInfo = {
        makeStringView("varsize"), SchemaType::VARSIZE, CppType::UINT32
    };
    return typeInfo;
}

template <typename ALLOC>
const IBasicTypeInfo<ALLOC>& BuiltinTypeInfo<ALLOC>::getFloat16()
{
    return FixedSizeBuiltinTypeInfo<ALLOC>::getFloat16();
}

template <typename ALLOC>
const IBasicTypeInfo<ALLOC>& BuiltinTypeInfo<ALLOC>::getFloat32()
{
    return FixedSizeBuiltinTypeInfo<ALLOC>::getFloat32();
}

template <typename ALLOC>
const IBasicTypeInfo<ALLOC>& BuiltinTypeInfo<ALLOC>::getFloat64()
{
    return FixedSizeBuiltinTypeInfo<ALLOC>::getFloat64();
}

template <typename ALLOC>
const IBasicTypeInfo<ALLOC>& BuiltinTypeInfo<ALLOC>::getBytes()
{
    static const BuiltinTypeInfo<ALLOC> typeInfo = {
        makeStringView("bytes"), SchemaType::BYTES, CppType::BYTES
    };
    return typeInfo;
}

template <typename ALLOC>
const IBasicTypeInfo<ALLOC>& BuiltinTypeInfo<ALLOC>::getString()
{
    static const BuiltinTypeInfo<ALLOC> typeInfo = {
        makeStringView("string"), SchemaType::STRING, CppType::STRING
    };
    return typeInfo;
}

template <typename ALLOC>
const IBasicTypeInfo<ALLOC>& BuiltinTypeInfo<ALLOC>::getBitBuffer()
{
    static const BuiltinTypeInfo<ALLOC> typeInfo = {
        makeStringView("extern"), SchemaType::EXTERN, CppType::BIT_BUFFER
    };
    return typeInfo;
}

template <typename ALLOC>
const IBasicTypeInfo<ALLOC>& BuiltinTypeInfo<ALLOC>::getFixedSignedBitField(uint8_t bitSize)
{
    return FixedSizeBuiltinTypeInfo<ALLOC>::getFixedSignedBitField(bitSize);
}

template <typename ALLOC>
const IBasicTypeInfo<ALLOC>& BuiltinTypeInfo<ALLOC>::getFixedUnsignedBitField(uint8_t bitSize)
{
    return FixedSizeBuiltinTypeInfo<ALLOC>::getFixedUnsignedBitField(bitSize);
}

template <typename ALLOC>
const IBasicTypeInfo<ALLOC>& BuiltinTypeInfo<ALLOC>::getDynamicSignedBitField(uint8_t maxBitSize)
{
    if (maxBitSize == 0 || maxBitSize > 64)
    {
        throw CppRuntimeException("BuiltinTypeInfo::getDynamicSignedBitField: Invalid max bit size '") <<
                maxBitSize << "'!";
    }

    if (maxBitSize <= 8)
    {
        static const BuiltinTypeInfo<ALLOC> typeInfo = {
            "int<>"_sv, SchemaType::DYNAMIC_SIGNED_BITFIELD, CppType::INT8
        };
        return typeInfo;
    }
    else if (maxBitSize <= 16)
    {
        static const BuiltinTypeInfo<ALLOC> typeInfo = {
            "int<>"_sv, SchemaType::DYNAMIC_SIGNED_BITFIELD, CppType::INT16
        };
        return typeInfo;
    }
    else if (maxBitSize <= 32)
    {
        static const BuiltinTypeInfo<ALLOC> typeInfo = {
            "int<>"_sv, SchemaType::DYNAMIC_SIGNED_BITFIELD, CppType::INT32
        };
        return typeInfo;
    }
    else
    {
        static const BuiltinTypeInfo<ALLOC> typeInfo = {
            "int<>"_sv, SchemaType::DYNAMIC_SIGNED_BITFIELD, CppType::INT64
        };
        return typeInfo;
    }
}

template <typename ALLOC>
const IBasicTypeInfo<ALLOC>& BuiltinTypeInfo<ALLOC>::getDynamicUnsignedBitField(uint8_t maxBitSize)
{
    if (maxBitSize == 0 || maxBitSize > 64)
    {
        throw CppRuntimeException("BuiltinTypeInfo::getDynamicUnsignedBitField: Invalid max bit size '") <<
                maxBitSize << "'!";
    }

    if (maxBitSize <= 8)
    {
        static const BuiltinTypeInfo<ALLOC> typeInfo = {
            "bit<>"_sv, SchemaType::DYNAMIC_UNSIGNED_BITFIELD, CppType::UINT8
        };
        return typeInfo;
    }
    else if (maxBitSize <= 16)
    {
        static const BuiltinTypeInfo<ALLOC> typeInfo = {
            "bit<>"_sv, SchemaType::DYNAMIC_UNSIGNED_BITFIELD, CppType::UINT16
        };
        return typeInfo;
    }
    else if (maxBitSize <= 32)
    {
        static const BuiltinTypeInfo<ALLOC> typeInfo = {
            "bit<>"_sv, SchemaType::DYNAMIC_UNSIGNED_BITFIELD, CppType::UINT32
        };
        return typeInfo;
    }
    else
    {
        static const BuiltinTypeInfo<ALLOC> typeInfo = {
            "bit<>"_sv, SchemaType::DYNAMIC_UNSIGNED_BITFIELD, CppType::UINT64
        };
        return typeInfo;
    }
}

template <typename ALLOC>
FixedSizeBuiltinTypeInfo<ALLOC>::FixedSizeBuiltinTypeInfo(StringView schemaName, SchemaType schemaType,
        CppType cppType, uint8_t bitSize) :
        BuiltinTypeInfo<ALLOC>(schemaName, schemaType, cppType), m_bitSize(bitSize)
{}

template <typename ALLOC>
uint8_t FixedSizeBuiltinTypeInfo<ALLOC>::getBitSize() const
{
    return m_bitSize;
}

template <typename ALLOC>
const IBasicTypeInfo<ALLOC>& FixedSizeBuiltinTypeInfo<ALLOC>::getBool()
{
    static const FixedSizeBuiltinTypeInfo<ALLOC> typeInfo = {
        makeStringView("bool"), SchemaType::BOOL, CppType::BOOL, 1
    };
    return typeInfo;
}

template <typename ALLOC>
const IBasicTypeInfo<ALLOC>& FixedSizeBuiltinTypeInfo<ALLOC>::getInt8()
{
    static const FixedSizeBuiltinTypeInfo<ALLOC> typeInfo = {
        makeStringView("int8"), SchemaType::INT8, CppType::INT8, 8
    };
    return typeInfo;
}

template <typename ALLOC>
const IBasicTypeInfo<ALLOC>& FixedSizeBuiltinTypeInfo<ALLOC>::getInt16()
{
    static const FixedSizeBuiltinTypeInfo<ALLOC> typeInfo = {
        makeStringView("int16"), SchemaType::INT16, CppType::INT16, 16
    };
    return typeInfo;
}

template <typename ALLOC>
const IBasicTypeInfo<ALLOC>& FixedSizeBuiltinTypeInfo<ALLOC>::getInt32()
{
    static const FixedSizeBuiltinTypeInfo<ALLOC> typeInfo = {
        makeStringView("int32"), SchemaType::INT32, CppType::INT32, 32
    };
    return typeInfo;
}

template <typename ALLOC>
const IBasicTypeInfo<ALLOC>& FixedSizeBuiltinTypeInfo<ALLOC>::getInt64()
{
    static const FixedSizeBuiltinTypeInfo<ALLOC> typeInfo = {
        makeStringView("int64"), SchemaType::INT64, CppType::INT64, 64
    };
    return typeInfo;
}

template <typename ALLOC>
const IBasicTypeInfo<ALLOC>& FixedSizeBuiltinTypeInfo<ALLOC>::getUInt8()
{
    static const FixedSizeBuiltinTypeInfo<ALLOC> typeInfo = {
        makeStringView("uint8"), SchemaType::UINT8, CppType::UINT8, 8
    };
    return typeInfo;
}

template <typename ALLOC>
const IBasicTypeInfo<ALLOC>& FixedSizeBuiltinTypeInfo<ALLOC>::getUInt16()
{
    static const FixedSizeBuiltinTypeInfo<ALLOC> typeInfo = {
        makeStringView("uint16"), SchemaType::UINT16, CppType::UINT16, 16
    };
    return typeInfo;
}

template <typename ALLOC>
const IBasicTypeInfo<ALLOC>& FixedSizeBuiltinTypeInfo<ALLOC>::getUInt32()
{
    static const FixedSizeBuiltinTypeInfo<ALLOC> typeInfo = {
        makeStringView("uint32"), SchemaType::UINT32, CppType::UINT32, 32
    };
    return typeInfo;
}

template <typename ALLOC>
const IBasicTypeInfo<ALLOC>& FixedSizeBuiltinTypeInfo<ALLOC>::getUInt64()
{
    static const FixedSizeBuiltinTypeInfo<ALLOC> typeInfo = {
        makeStringView("uint64"), SchemaType::UINT64, CppType::UINT64, 64
    };
    return typeInfo;
}

template <typename ALLOC>
const IBasicTypeInfo<ALLOC>& FixedSizeBuiltinTypeInfo<ALLOC>::getFloat16()
{
    static const FixedSizeBuiltinTypeInfo<ALLOC> typeInfo = {
        makeStringView("float16"), SchemaType::FLOAT16, CppType::FLOAT, 16
    };
    return typeInfo;
}

template <typename ALLOC>
const IBasicTypeInfo<ALLOC>& FixedSizeBuiltinTypeInfo<ALLOC>::getFloat32()
{
    static const FixedSizeBuiltinTypeInfo<ALLOC> typeInfo = {
        makeStringView("float32"), SchemaType::FLOAT32, CppType::FLOAT, 32
    };
    return typeInfo;
}

template <typename ALLOC>
const IBasicTypeInfo<ALLOC>& FixedSizeBuiltinTypeInfo<ALLOC>::getFloat64()
{
    static const FixedSizeBuiltinTypeInfo<ALLOC> typeInfo = {
        makeStringView("float64"), SchemaType::FLOAT64, CppType::DOUBLE, 64
    };
    return typeInfo;
}

template <typename ALLOC>
const IBasicTypeInfo<ALLOC>& FixedSizeBuiltinTypeInfo<ALLOC>::getFixedSignedBitField(uint8_t bitSize)
{
    if (bitSize == 0 || bitSize > 64)
    {
        throw CppRuntimeException("FixedSizeBuiltinTypeInfo::getFixedSignedBitField: Invalid bit size '") <<
                bitSize << "'!";
    }

    static const std::array<FixedSizeBuiltinTypeInfo<ALLOC>, 64> bitFieldTypeInfoArray = {{
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

template <typename ALLOC>
const IBasicTypeInfo<ALLOC>& FixedSizeBuiltinTypeInfo<ALLOC>::getFixedUnsignedBitField(uint8_t bitSize)
{
    if (bitSize == 0 || bitSize > 64)
    {
        throw CppRuntimeException("FixedSizeBuiltinTypeInfo::getFixedUnsignedBitField: Invalid bit size '") <<
                bitSize << "'!";
    }

    static const std::array<FixedSizeBuiltinTypeInfo<ALLOC>, 64> bitFieldTypeInfoArray = {{
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

template <typename ALLOC>
TemplatableTypeInfoBase<ALLOC>::TemplatableTypeInfoBase(StringView schemaName,
        SchemaType schemaType, CppType cppType,
        StringView templateName, Span<const BasicTemplateArgumentInfo<ALLOC>> templateArguments) :
        TypeInfoBase<ALLOC>(schemaName, schemaType, cppType),
        m_templateName(templateName), m_templateArguments(templateArguments)
{}

template <typename ALLOC>
TemplatableTypeInfoBase<ALLOC>::~TemplatableTypeInfoBase() = default;

template <typename ALLOC>
StringView TemplatableTypeInfoBase<ALLOC>::getTemplateName() const
{
    return m_templateName;
}

template <typename ALLOC>
Span<const BasicTemplateArgumentInfo<ALLOC>> TemplatableTypeInfoBase<ALLOC>::getTemplateArguments() const
{
    return m_templateArguments;
}

template <typename ALLOC>
CompoundTypeInfoBase<ALLOC>::CompoundTypeInfoBase(StringView schemaName, CreateInstanceFunc createInstanceFunc,
        SchemaType schemaType, CppType cppType,
        StringView templateName, Span<const BasicTemplateArgumentInfo<ALLOC>> templateArguments,
        Span<const BasicFieldInfo<ALLOC>> fields, Span<const BasicParameterInfo<ALLOC>> parameters,
        Span<const BasicFunctionInfo<ALLOC>> functions) :
        TemplatableTypeInfoBase<ALLOC>(schemaName, schemaType, cppType, templateName, templateArguments),
        m_createInstanceFunc(createInstanceFunc),
        m_fields(fields), m_parameters(parameters), m_functions(functions)
{}

template <typename ALLOC>
CompoundTypeInfoBase<ALLOC>::~CompoundTypeInfoBase() = default;

template <typename ALLOC>
Span<const BasicFieldInfo<ALLOC>> CompoundTypeInfoBase<ALLOC>::getFields() const
{
    return m_fields;
}

template <typename ALLOC>
Span<const BasicParameterInfo<ALLOC>> CompoundTypeInfoBase<ALLOC>::getParameters() const
{
    return m_parameters;
}

template <typename ALLOC>
Span<const BasicFunctionInfo<ALLOC>> CompoundTypeInfoBase<ALLOC>::getFunctions() const
{
    return m_functions;
}

template <typename ALLOC>
IBasicReflectablePtr<ALLOC> CompoundTypeInfoBase<ALLOC>::createInstance(const ALLOC& allocator) const
{
    if (!m_createInstanceFunc)
    {
        throw CppRuntimeException("Reflectable '") << getSchemaName() << "': Cannot create instance, " <<
                "either '-withoutWriterCode' or '-withoutReflectionCode' zserio option is used!";
    }
    return m_createInstanceFunc(allocator);
}

template <typename ALLOC>
StructTypeInfo<ALLOC>::StructTypeInfo(StringView schemaName, CreateInstanceFunc createInstanceFunc,
        StringView templateName, Span<const BasicTemplateArgumentInfo<ALLOC>> templateArguments,
        Span<const BasicFieldInfo<ALLOC>> fields, Span<const BasicParameterInfo<ALLOC>> parameters,
        Span<const BasicFunctionInfo<ALLOC>> functions) :
        CompoundTypeInfoBase<ALLOC>(schemaName, createInstanceFunc, SchemaType::STRUCT, CppType::STRUCT,
                templateName, templateArguments, fields, parameters, functions)
{}

template <typename ALLOC>
UnionTypeInfo<ALLOC>::UnionTypeInfo(StringView schemaName, CreateInstanceFunc createInstanceFunc,
        StringView templateName, Span<const BasicTemplateArgumentInfo<ALLOC>> templateArguments,
        Span<const BasicFieldInfo<ALLOC>> fields, Span<const BasicParameterInfo<ALLOC>> parameters,
        Span<const BasicFunctionInfo<ALLOC>> functions) :
        CompoundTypeInfoBase<ALLOC>(schemaName, createInstanceFunc, SchemaType::UNION, CppType::UNION,
                templateName, templateArguments, fields, parameters, functions)
{}

template <typename ALLOC>
ChoiceTypeInfo<ALLOC>::ChoiceTypeInfo(StringView schemaName, CreateInstanceFunc createInstanceFunc,
        StringView templateName, Span<const BasicTemplateArgumentInfo<ALLOC>> templateArguments,
        Span<const BasicFieldInfo<ALLOC>> fields, Span<const BasicParameterInfo<ALLOC>> parameters,
        Span<const BasicFunctionInfo<ALLOC>> functions,
        StringView selector, Span<const BasicCaseInfo<ALLOC>> cases) :
        CompoundTypeInfoBase<ALLOC>(schemaName, createInstanceFunc, SchemaType::CHOICE, CppType::CHOICE,
                templateName, templateArguments, fields, parameters, functions),
        m_selector(selector), m_cases(cases)
{}

template <typename ALLOC>
StringView ChoiceTypeInfo<ALLOC>::getSelector() const
{
    return m_selector;
}

template <typename ALLOC>
Span<const BasicCaseInfo<ALLOC>> ChoiceTypeInfo<ALLOC>::getCases() const
{
    return m_cases;
}

template <typename ALLOC>
SqlTableTypeInfo<ALLOC>::SqlTableTypeInfo(StringView schemaName,
        StringView templateName, Span<const BasicTemplateArgumentInfo<ALLOC>> templateArguments,
        Span<const BasicColumnInfo<ALLOC>> columns, StringView sqlConstraint, StringView virtualTableUsing,
        bool isWithoutRowId) :
        TemplatableTypeInfoBase<ALLOC>(schemaName, SchemaType::SQL_TABLE, CppType::SQL_TABLE,
                templateName, templateArguments),
        m_columns(columns), m_sqlConstraint(sqlConstraint), m_virtualTableUsing(virtualTableUsing),
        m_isWithoutRowId(isWithoutRowId)
{}

template <typename ALLOC>
Span<const BasicColumnInfo<ALLOC>> SqlTableTypeInfo<ALLOC>::getColumns() const
{
    return m_columns;
}

template <typename ALLOC>
StringView SqlTableTypeInfo<ALLOC>::getSqlConstraint() const
{
    return m_sqlConstraint;
}

template <typename ALLOC>
StringView SqlTableTypeInfo<ALLOC>::getVirtualTableUsing() const
{
    return m_virtualTableUsing;
}

template <typename ALLOC>
bool SqlTableTypeInfo<ALLOC>::isWithoutRowId() const
{
    return m_isWithoutRowId;
}

template <typename ALLOC>
SqlDatabaseTypeInfo<ALLOC>::SqlDatabaseTypeInfo(StringView schemaName,
        Span<const BasicTableInfo<ALLOC>> tables) :
        TypeInfoBase<ALLOC>(schemaName, SchemaType::SQL_DATABASE, CppType::SQL_DATABASE),
        m_tables(tables)
{}

template <typename ALLOC>
Span<const BasicTableInfo<ALLOC>> SqlDatabaseTypeInfo<ALLOC>::getTables() const
{
    return m_tables;
}

template <typename ALLOC>
TypeInfoWithUnderlyingTypeBase<ALLOC>::TypeInfoWithUnderlyingTypeBase(
        StringView schemaName, SchemaType schemaType, CppType cppType,
        const IBasicTypeInfo<ALLOC>& underlyingType, Span<const StringView> underlyingTypeArguments) :
        TypeInfoBase<ALLOC>(schemaName, schemaType, cppType),
        m_underlyingType(underlyingType), m_underlyingTypeArguments(underlyingTypeArguments)
{}

template <typename ALLOC>
const IBasicTypeInfo<ALLOC>& TypeInfoWithUnderlyingTypeBase<ALLOC>::getUnderlyingType() const
{
    return m_underlyingType;
}

template <typename ALLOC>
Span<const StringView> TypeInfoWithUnderlyingTypeBase<ALLOC>::getUnderlyingTypeArguments() const
{
    return m_underlyingTypeArguments;
}

template <typename ALLOC>
EnumTypeInfo<ALLOC>::EnumTypeInfo(StringView schemaName, const IBasicTypeInfo<ALLOC>& underlyingType,
        Span<const StringView> underlyingTypeArguments, Span<const ItemInfo> enumItems) :
        TypeInfoWithUnderlyingTypeBase<ALLOC>(schemaName, SchemaType::ENUM, CppType::ENUM,
                underlyingType, underlyingTypeArguments),
        m_enumItems(enumItems)
{}

template <typename ALLOC>
Span<const ItemInfo> EnumTypeInfo<ALLOC>::getEnumItems() const
{
    return m_enumItems;
}

template <typename ALLOC>
BitmaskTypeInfo<ALLOC>::BitmaskTypeInfo(StringView schemaName, const IBasicTypeInfo<ALLOC>& underlyingType,
        Span<const StringView> underlyingTypeArguments, Span<const ItemInfo> bitmaskValues) :
        TypeInfoWithUnderlyingTypeBase<ALLOC>(schemaName, SchemaType::BITMASK, CppType::BITMASK,
                underlyingType, underlyingTypeArguments),
        m_bitmaskValues(bitmaskValues)
{}

template <typename ALLOC>
Span<const ItemInfo> BitmaskTypeInfo<ALLOC>::getBitmaskValues() const
{
    return m_bitmaskValues;
}

template <typename ALLOC>
PubsubTypeInfo<ALLOC>::PubsubTypeInfo(StringView schemaName, Span<const BasicMessageInfo<ALLOC>> messages) :
        TypeInfoBase<ALLOC>(schemaName, SchemaType::PUBSUB, CppType::PUBSUB), m_messages(messages)
{}

template <typename ALLOC>
Span<const BasicMessageInfo<ALLOC>> PubsubTypeInfo<ALLOC>::getMessages() const
{
    return m_messages;
}

template <typename ALLOC>
ServiceTypeInfo<ALLOC>::ServiceTypeInfo(StringView schemaName, Span<const BasicMethodInfo<ALLOC>> methods) :
        TypeInfoBase<ALLOC>(schemaName, SchemaType::SERVICE, CppType::SERVICE), m_methods(methods)
{}

template <typename ALLOC>
Span<const BasicMethodInfo<ALLOC>> ServiceTypeInfo<ALLOC>::getMethods() const
{
    return m_methods;
}

template <typename ALLOC>
StringView RecursiveTypeInfo<ALLOC>::getSchemaName() const
{
    return m_typeInfoFunc().getSchemaName();
}

template <typename ALLOC>
SchemaType RecursiveTypeInfo<ALLOC>::getSchemaType() const
{
    return m_typeInfoFunc().getSchemaType();
}

template <typename ALLOC>
CppType RecursiveTypeInfo<ALLOC>::getCppType() const
{
    return m_typeInfoFunc().getCppType();
}

template <typename ALLOC>
uint8_t RecursiveTypeInfo<ALLOC>::getBitSize() const
{
    return m_typeInfoFunc().getBitSize();
}

template <typename ALLOC>
Span<const BasicFieldInfo<ALLOC>> RecursiveTypeInfo<ALLOC>::getFields() const
{
    return m_typeInfoFunc().getFields();
}

template <typename ALLOC>
Span<const BasicParameterInfo<ALLOC>> RecursiveTypeInfo<ALLOC>::getParameters() const
{
    return m_typeInfoFunc().getParameters();
}

template <typename ALLOC>
Span<const BasicFunctionInfo<ALLOC>> RecursiveTypeInfo<ALLOC>::getFunctions() const
{
    return m_typeInfoFunc().getFunctions();
}

template <typename ALLOC>
StringView RecursiveTypeInfo<ALLOC>::getSelector() const
{
    return m_typeInfoFunc().getSelector();
}

template <typename ALLOC>
Span<const BasicCaseInfo<ALLOC>> RecursiveTypeInfo<ALLOC>::getCases() const
{
    return m_typeInfoFunc().getCases();
}

template <typename ALLOC>
const IBasicTypeInfo<ALLOC>& RecursiveTypeInfo<ALLOC>::getUnderlyingType() const
{
    return m_typeInfoFunc().getUnderlyingType();
}

template <typename ALLOC>
Span<const StringView> RecursiveTypeInfo<ALLOC>::getUnderlyingTypeArguments() const
{
    return m_typeInfoFunc().getUnderlyingTypeArguments();
}

template <typename ALLOC>
Span<const ItemInfo> RecursiveTypeInfo<ALLOC>::getEnumItems() const
{
    return m_typeInfoFunc().getEnumItems();
}

template <typename ALLOC>
Span<const ItemInfo> RecursiveTypeInfo<ALLOC>::getBitmaskValues() const
{
    return m_typeInfoFunc().getBitmaskValues();
}

template <typename ALLOC>
Span<const BasicColumnInfo<ALLOC>> RecursiveTypeInfo<ALLOC>::getColumns() const
{
    return m_typeInfoFunc().getColumns();
}

template <typename ALLOC>
StringView RecursiveTypeInfo<ALLOC>::getSqlConstraint() const
{
    return m_typeInfoFunc().getSqlConstraint();
}

template <typename ALLOC>
StringView RecursiveTypeInfo<ALLOC>::getVirtualTableUsing() const
{
    return m_typeInfoFunc().getVirtualTableUsing();
}

template <typename ALLOC>
bool RecursiveTypeInfo<ALLOC>::isWithoutRowId() const
{
    return m_typeInfoFunc().isWithoutRowId();
}

template <typename ALLOC>
Span<const BasicTableInfo<ALLOC>> RecursiveTypeInfo<ALLOC>::getTables() const
{
    return m_typeInfoFunc().getTables();
}

template <typename ALLOC>
StringView RecursiveTypeInfo<ALLOC>::getTemplateName() const
{
    return m_typeInfoFunc().getTemplateName();
}

template <typename ALLOC>
Span<const BasicTemplateArgumentInfo<ALLOC>> RecursiveTypeInfo<ALLOC>::getTemplateArguments() const
{
    return m_typeInfoFunc().getTemplateArguments();
}

template <typename ALLOC>
Span<const BasicMessageInfo<ALLOC>> RecursiveTypeInfo<ALLOC>::getMessages() const
{
    return m_typeInfoFunc().getMessages();
}

template <typename ALLOC>
Span<const BasicMethodInfo<ALLOC>> RecursiveTypeInfo<ALLOC>::getMethods() const
{
    return m_typeInfoFunc().getMethods();
}

template <typename ALLOC>
IBasicReflectablePtr<ALLOC> RecursiveTypeInfo<ALLOC>::createInstance(const ALLOC& allocator) const
{
    return m_typeInfoFunc().createInstance(allocator);
}

template <typename ALLOC>
IBasicReflectablePtr<ALLOC> RecursiveTypeInfo<ALLOC>::createInstance() const
{
    return createInstance(ALLOC());
}

} // namespace zserio

#endif // ZSERIO_TYPE_INFO_INC_H
