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

class TypeInfoBase : public ITypeInfo
{
public:
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

class BuiltinTypeInfo : public TypeInfoBase
{
protected:
    BuiltinTypeInfo(StringView schemaName, SchemaType schemaType, CppType cppType);

public:
    static const ITypeInfo& getBool();
    static const ITypeInfo& getInt8();
    static const ITypeInfo& getInt16();
    static const ITypeInfo& getInt32();
    static const ITypeInfo& getInt64();
    static const ITypeInfo& getUInt8();
    static const ITypeInfo& getUInt16();
    static const ITypeInfo& getUInt32();
    static const ITypeInfo& getUInt64();
    static const ITypeInfo& getVarInt16();
    static const ITypeInfo& getVarInt32();
    static const ITypeInfo& getVarInt64();
    static const ITypeInfo& getVarInt();
    static const ITypeInfo& getVarUInt16();
    static const ITypeInfo& getVarUInt32();
    static const ITypeInfo& getVarUInt64();
    static const ITypeInfo& getVarUInt();
    static const ITypeInfo& getVarSize();
    static const ITypeInfo& getFloat16();
    static const ITypeInfo& getFloat32();
    static const ITypeInfo& getFloat64();
    static const ITypeInfo& getString();
    static const ITypeInfo& getBitBuffer();
    static const ITypeInfo& getFixedSignedBitField(uint8_t bitSize);
    static const ITypeInfo& getFixedUnsignedBitField(uint8_t bitSize);
    static const ITypeInfo& getDynamicSignedBitField(uint8_t maxBitSize);
    static const ITypeInfo& getDynamicUnsignedBitField(uint8_t maxBitSize);
};

class FixedSizeBuiltinTypeInfo : public BuiltinTypeInfo
{
protected:
    FixedSizeBuiltinTypeInfo(StringView schemaName, SchemaType schemaType, CppType cppType, uint8_t bitSize);

public:
    virtual uint8_t getBitSize() const override;

    static const ITypeInfo& getBool();
    static const ITypeInfo& getInt8();
    static const ITypeInfo& getInt16();
    static const ITypeInfo& getInt32();
    static const ITypeInfo& getInt64();
    static const ITypeInfo& getUInt8();
    static const ITypeInfo& getUInt16();
    static const ITypeInfo& getUInt32();
    static const ITypeInfo& getUInt64();
    static const ITypeInfo& getFloat16();
    static const ITypeInfo& getFloat32();
    static const ITypeInfo& getFloat64();
    static const ITypeInfo& getFixedSignedBitField(uint8_t bitSize);
    static const ITypeInfo& getFixedUnsignedBitField(uint8_t bitSize);

private:
    uint8_t m_bitSize;
};

class TemplatableTypeInfoBase : public TypeInfoBase
{
public:
    TemplatableTypeInfoBase(StringView schemaName, SchemaType schemaType, CppType cppType,
            StringView templateName, Span<const TemplateArgumentInfo> templateArguments);

    virtual ~TemplatableTypeInfoBase() override = 0;

    virtual StringView getTemplateName() const override;
    virtual Span<const TemplateArgumentInfo> getTemplateArguments() const override;

private:
    StringView m_templateName;
    Span<const TemplateArgumentInfo> m_templateArguments;
};

class CompoundTypeInfoBase : public TemplatableTypeInfoBase
{
public:
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

class StructTypeInfo : public CompoundTypeInfoBase
{
public:
    StructTypeInfo(StringView schemaName,
            StringView templateName, Span<const TemplateArgumentInfo> templateArguments,
            Span<const FieldInfo> fields, Span<const ParameterInfo> parameters,
            Span<const FunctionInfo> functions);
};

class UnionTypeInfo : public CompoundTypeInfoBase
{
public:
    UnionTypeInfo(StringView schemaName,
            StringView templateName, Span<const TemplateArgumentInfo> templateArguments,
            Span<const FieldInfo> fields, Span<const ParameterInfo> parameters,
            Span<const FunctionInfo> functions);
};

class ChoiceTypeInfo : public CompoundTypeInfoBase
{
public:
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

class SqlTableTypeInfo : public TemplatableTypeInfoBase
{
public:
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

class SqlDatabaseTypeInfo : public TypeInfoBase
{
public:
    SqlDatabaseTypeInfo(StringView schemaName, Span<const TableInfo> tables);

    virtual Span<const TableInfo> getTables() const override;

private:
    Span<const TableInfo> m_tables;
};

class TypeInfoWithUnderlyingTypeBase : public TypeInfoBase
{
public:
    TypeInfoWithUnderlyingTypeBase(StringView schemaName, SchemaType schemaType, CppType cppType,
            const ITypeInfo& underlyingType, Span<const StringView> underlyingTypeArguments);

    virtual const ITypeInfo& getUnderlyingType() const override;
    virtual Span<const StringView> getUnderlyingTypeArguments() const override;

private:
    const ITypeInfo& m_underlyingType;
    Span<const StringView> m_underlyingTypeArguments;
};

class EnumTypeInfo : public TypeInfoWithUnderlyingTypeBase
{
public:
    EnumTypeInfo(StringView schemaName, const ITypeInfo& underlyingType,
            Span<const StringView> underlyingTypeArguments, Span<const ItemInfo> enumItems);

    virtual Span<const ItemInfo> getEnumItems() const override;

private:
    Span<const ItemInfo> m_enumItems;
};

class BitmaskTypeInfo : public TypeInfoWithUnderlyingTypeBase
{
public:
    BitmaskTypeInfo(StringView schemaName, const ITypeInfo& underlyingType,
            Span<const StringView> underlyingTypeArguments, Span<const ItemInfo> bitmaskValues);

    virtual Span<const ItemInfo> getBitmaskValues() const override;

private:
    Span<const ItemInfo> m_bitmaskValues;
};

class PubsubTypeInfo : public TypeInfoBase
{
public:
    PubsubTypeInfo(StringView schemaName, Span<const MessageInfo> messages);

    virtual Span<const MessageInfo> getMessages() const override;

private:
    Span<const MessageInfo> m_messages;
};

class ServiceTypeInfo : public TypeInfoBase
{
public:
    ServiceTypeInfo(StringView schemaName, Span<const MethodInfo> methods);

    virtual Span<const MethodInfo> getMethods() const override;

private:
    Span<const MethodInfo> m_methods;
};

} // namespace zserio

#endif // ZSERIO_TYPE_INFO_INC_H
