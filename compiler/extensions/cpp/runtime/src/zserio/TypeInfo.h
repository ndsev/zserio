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
class TypeInfoBase : public ITypeInfo
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

    virtual ~TypeInfoBase() override = 0;

    virtual StringView getSchemaName() const override;
    virtual SchemaType getSchemaType() const override;
    virtual CppType getCppType() const override;
    virtual uint8_t getBitSize() const override;

    virtual Span<const FieldInfo> getFields() const override;
    virtual Span<const ParameterInfo> getParameters() const override;
    virtual Span<const FunctionInfo> getFunctions() const override;

    virtual StringView getSelector() const override;
    virtual Span<const CaseInfo> getCases() const override;

    virtual const ITypeInfo& getUnderlyingType() const override;
    virtual Span<const StringView> getUnderlyingTypeArguments() const override;
    virtual Span<const ItemInfo> getEnumItems() const override;
    virtual Span<const ItemInfo> getBitmaskValues() const override;

    virtual Span<const ColumnInfo> getColumns() const override;
    virtual StringView getSqlConstraint() const override;
    virtual StringView getVirtualTableUsing() const override;
    virtual bool isWithoutRowid() const override;

    virtual Span<const TableInfo> getTables() const override;

    virtual StringView getTemplateName() const override;
    virtual Span<const TemplateArgumentInfo> getTemplateArguments() const override;

    virtual Span<const MessageInfo> getMessages() const override;
    virtual Span<const MethodInfo> getMethods() const override;

private:
    StringView m_schemaName;
    SchemaType m_schemaType;
    CppType m_cppType;
};

/**
 * Type information abstract base class for builtin types.
 */
class BuiltinTypeInfo : public TypeInfoBase
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
    static const ITypeInfo& getBool();

    /**
     * Gets the type information of int8 schema type.
     *
     * \return Reference to the type information of int8 schema type.
     */
    static const ITypeInfo& getInt8();

    /**
     * Gets the type information of int16 schema type.
     *
     * \return Reference to the type information of int16 schema type.
     */
    static const ITypeInfo& getInt16();

    /**
     * Gets the type information of int32 schema type.
     *
     * \return Reference to the type information of int32 schema type.
     */
    static const ITypeInfo& getInt32();

    /**
     * Gets the type information of int64 schema type.
     *
     * \return Reference to the type information of int64 schema type.
     */
    static const ITypeInfo& getInt64();

    /**
     * Gets the type information of uint8 schema type.
     *
     * \return Reference to the type information of uint8 schema type.
     */
    static const ITypeInfo& getUInt8();

    /**
     * Gets the type information of uint16 schema type.
     *
     * \return Reference to the type information of uint16 schema type.
     */
    static const ITypeInfo& getUInt16();

    /**
     * Gets the type information of uint32 schema type.
     *
     * \return Reference to the type information of uint32 schema type.
     */
    static const ITypeInfo& getUInt32();

    /**
     * Gets the type information of uint64 schema type.
     *
     * \return Reference to the type information of uint64 schema type.
     */
    static const ITypeInfo& getUInt64();

    /**
     * Gets the type information of varint16 schema type.
     *
     * \return Reference to the type information of varint16 schema type.
     */
    static const ITypeInfo& getVarInt16();

    /**
     * Gets the type information of varint32 schema type.
     *
     * \return Reference to the type information of varint32 schema type.
     */
    static const ITypeInfo& getVarInt32();

    /**
     * Gets the type information of varint64 schema type.
     *
     * \return Reference to the type information of varint64 schema type.
     */
    static const ITypeInfo& getVarInt64();

    /**
     * Gets the type information of varint schema type.
     *
     * \return Reference to the type information of varint schema type.
     */
    static const ITypeInfo& getVarInt();

    /**
     * Gets the type information of varuint16 schema type.
     *
     * \return Reference to the type information of varuint16 schema type.
     */
    static const ITypeInfo& getVarUInt16();

    /**
     * Gets the type information of varuint32 schema type.
     *
     * \return Reference to the type information of varuint32 schema type.
     */
    static const ITypeInfo& getVarUInt32();

    /**
     * Gets the type information of varuint64 schema type.
     *
     * \return Reference to the type information of varuint64 schema type.
     */
    static const ITypeInfo& getVarUInt64();

    /**
     * Gets the type information of varuint schema type.
     *
     * \return Reference to the type information of varuint schema type.
     */
    static const ITypeInfo& getVarUInt();

    /**
     * Gets the type information of varsize schema type.
     *
     * \return Reference to the type information of varsize schema type.
     */
    static const ITypeInfo& getVarSize();

    /**
     * Gets the type information of float16 schema type.
     *
     * \return Reference to the type information of float16 schema type.
     */
    static const ITypeInfo& getFloat16();

    /**
     * Gets the type information of float32 schema type.
     *
     * \return Reference to the type information of float32 schema type.
     */
    static const ITypeInfo& getFloat32();

    /**
     * Gets the type information of float64 schema type.
     *
     * \return Reference to the type information of float64 schema type.
     */
    static const ITypeInfo& getFloat64();

    /**
     * Gets the type information of string schema type.
     *
     * \return Reference to the type information of string schema type.
     */
    static const ITypeInfo& getString();

    /**
     * Gets the type information of extern schema type.
     *
     * \return Reference to the type information of extern schema type.
     */
    static const ITypeInfo& getBitBuffer();

    /**
     * Gets the type information of fixed signed bit field schema type.
     *
     * \return Reference to the type information of fixed signed bit field schema type.
     */
    static const ITypeInfo& getFixedSignedBitField(uint8_t bitSize);

    /**
     * Gets the type information of fixed unsigned bit field schema type.
     *
     * \return Reference to the type information of fixed unsigned bit field schema type.
     */
    static const ITypeInfo& getFixedUnsignedBitField(uint8_t bitSize);

    /**
     * Gets the type information of dynamic signed bit field schema type.
     *
     * \return Reference to the type information of dynamic signed bit field schema type.
     */
    static const ITypeInfo& getDynamicSignedBitField(uint8_t maxBitSize);

    /**
     * Gets the type information of dynamic unsigned bit field schema type.
     *
     * \return Reference to the type information of dynamic unsigned bit field schema type.
     */
    static const ITypeInfo& getDynamicUnsignedBitField(uint8_t maxBitSize);
};

/**
 * Type information abstract base class for fixed size builtin types.
 */
class FixedSizeBuiltinTypeInfo : public BuiltinTypeInfo
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
    FixedSizeBuiltinTypeInfo(StringView schemaName, SchemaType schemaType, CppType cppType, uint8_t bitSize);

public:
    virtual uint8_t getBitSize() const override;

    /**
     * Gets the type information of bool schema type.
     *
     * \return Reference to the type information of bool schema type.
     */
    static const ITypeInfo& getBool();

    /**
     * Gets the type information of int8 schema type.
     *
     * \return Reference to the type information of int8 schema type.
     */
    static const ITypeInfo& getInt8();

    /**
     * Gets the type information of int16 schema type.
     *
     * \return Reference to the type information of int16 schema type.
     */
    static const ITypeInfo& getInt16();

    /**
     * Gets the type information of int32 schema type.
     *
     * \return Reference to the type information of int32 schema type.
     */
    static const ITypeInfo& getInt32();

    /**
     * Gets the type information of int64 schema type.
     *
     * \return Reference to the type information of int64 schema type.
     */
    static const ITypeInfo& getInt64();

    /**
     * Gets the type information of uint8 schema type.
     *
     * \return Reference to the type information of uint8 schema type.
     */
    static const ITypeInfo& getUInt8();

    /**
     * Gets the type information of uint16 schema type.
     *
     * \return Reference to the type information of uint16 schema type.
     */
    static const ITypeInfo& getUInt16();

    /**
     * Gets the type information of uint32 schema type.
     *
     * \return Reference to the type information of uint32 schema type.
     */
    static const ITypeInfo& getUInt32();

    /**
     * Gets the type information of uint64 schema type.
     *
     * \return Reference to the type information of uint64 schema type.
     */
    static const ITypeInfo& getUInt64();

    /**
     * Gets the type information of float16 schema type.
     *
     * \return Reference to the type information of float16 schema type.
     */
    static const ITypeInfo& getFloat16();

    /**
     * Gets the type information of float32 schema type.
     *
     * \return Reference to the type information of float32 schema type.
     */
    static const ITypeInfo& getFloat32();

    /**
     * Gets the type information of float64 schema type.
     *
     * \return Reference to the type information of float64 schema type.
     */
    static const ITypeInfo& getFloat64();

    /**
     * Gets the type information of fixed signed bit field schema type.
     *
     * \return Reference to the type information of fixed signed bit field schema type.
     */
    static const ITypeInfo& getFixedSignedBitField(uint8_t bitSize);

    /**
     * Gets the type information of fixed unsigned bit field schema type.
     *
     * \return Reference to the type information of fixed unsigned bit field schema type.
     */
    static const ITypeInfo& getFixedUnsignedBitField(uint8_t bitSize);

private:
    uint8_t m_bitSize;
};

/**
 * Type information abstract base class for templatable types.
 */
class TemplatableTypeInfoBase : public TypeInfoBase
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
            StringView templateName, Span<const TemplateArgumentInfo> templateArguments);

    virtual ~TemplatableTypeInfoBase() override = 0;

    virtual StringView getTemplateName() const override;
    virtual Span<const TemplateArgumentInfo> getTemplateArguments() const override;

private:
    StringView m_templateName;
    Span<const TemplateArgumentInfo> m_templateArguments;
};

/**
 * Type information abstract base class for compound types.
 */
class CompoundTypeInfoBase : public TemplatableTypeInfoBase
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
     * \param fields The sequence of type informations for fields.
     * \param parameters The sequence of type informations for parameters.
     * \param functions The sequence of type informations for functions.
     */
    CompoundTypeInfoBase(StringView schemaName, SchemaType schemaType, CppType cppType,
            StringView templateName, Span<const TemplateArgumentInfo> templateArguments,
            Span<const FieldInfo> fields, Span<const ParameterInfo> parameters,
            Span<const FunctionInfo> functions);

    virtual ~CompoundTypeInfoBase() override = 0;

    virtual Span<const FieldInfo> getFields() const override;
    virtual Span<const ParameterInfo> getParameters() const override;
    virtual Span<const FunctionInfo> getFunctions() const override;

private:
    Span<const FieldInfo> m_fields;
    Span<const ParameterInfo> m_parameters;
    Span<const FunctionInfo> m_functions;
};

/**
 * Type information class for structure types.
 */
class StructTypeInfo : public CompoundTypeInfoBase
{
public:
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
    StructTypeInfo(StringView schemaName,
            StringView templateName, Span<const TemplateArgumentInfo> templateArguments,
            Span<const FieldInfo> fields, Span<const ParameterInfo> parameters,
            Span<const FunctionInfo> functions);
};

/**
 * Type information class for union types.
 */
class UnionTypeInfo : public CompoundTypeInfoBase
{
public:
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
    UnionTypeInfo(StringView schemaName,
            StringView templateName, Span<const TemplateArgumentInfo> templateArguments,
            Span<const FieldInfo> fields, Span<const ParameterInfo> parameters,
            Span<const FunctionInfo> functions);
};

/**
 * Type information class for choice types.
 */
class ChoiceTypeInfo : public CompoundTypeInfoBase
{
public:
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
    ChoiceTypeInfo(StringView schemaName,
            StringView templateName, Span<const TemplateArgumentInfo> templateArguments,
            Span<const FieldInfo> fields, Span<const ParameterInfo> parameters,
            Span<const FunctionInfo> functions, StringView selector, Span<const CaseInfo> cases);

    virtual StringView getSelector() const override;
    virtual Span<const CaseInfo> getCases() const override;

private:
    StringView m_selector;
    Span<const CaseInfo> m_cases;
};

/**
 * Type information abstract base class for enumeration and bitmask types.
 */
class TypeInfoWithUnderlyingTypeBase : public TypeInfoBase
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
            const ITypeInfo& underlyingType, Span<const StringView> underlyingTypeArguments);

    virtual const ITypeInfo& getUnderlyingType() const override;
    virtual Span<const StringView> getUnderlyingTypeArguments() const override;

private:
    const ITypeInfo& m_underlyingType;
    Span<const StringView> m_underlyingTypeArguments;
};

/**
 * Type information class for enumeration types.
 */
class EnumTypeInfo : public TypeInfoWithUnderlyingTypeBase
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
    EnumTypeInfo(StringView schemaName, const ITypeInfo& underlyingType,
            Span<const StringView> underlyingTypeArguments, Span<const ItemInfo> enumItems);

    virtual Span<const ItemInfo> getEnumItems() const override;

private:
    Span<const ItemInfo> m_enumItems;
};

/**
 * Type information class for bitmask types.
 */
class BitmaskTypeInfo : public TypeInfoWithUnderlyingTypeBase
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
    BitmaskTypeInfo(StringView schemaName, const ITypeInfo& underlyingType,
            Span<const StringView> underlyingTypeArguments, Span<const ItemInfo> bitmaskValues);

    virtual Span<const ItemInfo> getBitmaskValues() const override;

private:
    Span<const ItemInfo> m_bitmaskValues;
};

/**
 * Type information class for SQL table types.
 */
class SqlTableTypeInfo : public TemplatableTypeInfoBase
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
            StringView templateName, Span<const TemplateArgumentInfo> templateArguments,
            Span<const ColumnInfo> columns, StringView sqlConstraint, StringView virtualTableUsing,
            bool isWithoutRowId);

    virtual Span<const ColumnInfo> getColumns() const override;
    virtual StringView getSqlConstraint() const override;
    virtual StringView getVirtualTableUsing() const override;
    virtual bool isWithoutRowid() const override;

private:
    Span<const ColumnInfo> m_columns;
    StringView m_sqlConstraint;
    StringView m_virtualTableUsing;
    bool m_isWithoutRowId;
};

/**
 * Type information class for SQL database types.
 */
class SqlDatabaseTypeInfo : public TypeInfoBase
{
public:
    /**
     * Constructor.
     *
     * \param schemaName The schema name to be stored in type information.
     * \param tables The sequence of type informations for tables.
     */
    SqlDatabaseTypeInfo(StringView schemaName, Span<const TableInfo> tables);

    virtual Span<const TableInfo> getTables() const override;

private:
    Span<const TableInfo> m_tables;
};

/**
 * Type information class for pubsub types.
 */
class PubsubTypeInfo : public TypeInfoBase
{
public:
    /**
     * Constructor.
     *
     * \param schemaName The schema name to be stored in type information.
     * \param messages The sequence of type informations for pubsub messages.
     */
    PubsubTypeInfo(StringView schemaName, Span<const MessageInfo> messages);

    virtual Span<const MessageInfo> getMessages() const override;

private:
    Span<const MessageInfo> m_messages;
};

/**
 * Type information class for service types.
 */
class ServiceTypeInfo : public TypeInfoBase
{
public:
    /**
     * Constructor.
     *
     * \param schemaName The schema name to be stored in type information.
     * \param methods The sequence of type informations for service methods.
     */
    ServiceTypeInfo(StringView schemaName, Span<const MethodInfo> methods);

    virtual Span<const MethodInfo> getMethods() const override;

private:
    Span<const MethodInfo> m_methods;
};

} // namespace zserio

#endif // ZSERIO_TYPE_INFO_INC_H
