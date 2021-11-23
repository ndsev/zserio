#ifndef ZSERIO_I_TYPE_INFO_INC_H
#define ZSERIO_I_TYPE_INFO_INC_H

#include "zserio/Span.h"
#include "zserio/StringView.h"

namespace zserio
{

enum class CppType
{
    BOOL,
    INT8,
    INT16,
    INT32,
    INT64,
    UINT8,
    UINT16,
    UINT32,
    UINT64,
    FLOAT,
    DOUBLE,
    STRING,
    BIT_BUFFER,
    ENUM,
    BITMASK,
    STRUCT,
    CHOICE,
    UNION,
    SQL_TABLE,
    SQL_DATABASE,
    SERVICE,
    PUBSUB
};

enum class SchemaType
{
    BOOL,
    INT8,
    INT16,
    INT32,
    INT64,
    UINT8,
    UINT16,
    UINT32,
    UINT64,
    VARINT16,
    VARINT32,
    VARINT64,
    VARINT,
    VARUINT16,
    VARUINT32,
    VARUINT64,
    VARUINT,
    VARSIZE,
    FIXED_SIGNED_BITFIELD,
    FIXED_UNSIGNED_BITFIELD,
    DYNAMIC_SIGNED_BITFIELD,
    DYNAMIC_UNSIGNED_BITFIELD,
    FLOAT16,
    FLOAT32,
    FLOAT64,
    STRING,
    EXTERN,
    ENUM,
    BITMASK,
    STRUCT,
    CHOICE,
    UNION,
    SQL_TABLE,
    SQL_DATABASE,
    SERVICE,
    PUBSUB
};

struct FieldInfo;
struct ParameterInfo;
struct FunctionInfo;
struct CaseInfo;
struct ColumnInfo;
struct TableInfo;
struct ItemInfo;
struct TemplateArgumentInfo;
struct MessageInfo;
struct MethodInfo;

class ITypeInfo
{
public:
    virtual ~ITypeInfo() {}

    virtual StringView getSchemaName() const = 0;
    virtual SchemaType getSchemaType() const = 0;
    virtual CppType getCppType() const = 0;

    virtual uint8_t getBitSize() const = 0;

    virtual Span<const FieldInfo> getFields() const = 0;
    virtual Span<const ParameterInfo> getParameters() const = 0;
    virtual Span<const FunctionInfo> getFunctions() const = 0;

    virtual StringView getSelector() const = 0;
    virtual Span<const CaseInfo> getCases() const = 0;

    virtual const ITypeInfo& getUnderlyingType() const = 0;
    virtual Span<const StringView> getUnderlyingTypeArguments() const = 0;
    virtual Span<const ItemInfo> getEnumItems() const = 0;
    virtual Span<const ItemInfo> getBitmaskValues() const = 0;

    virtual Span<const ColumnInfo> getColumns() const = 0;
    virtual StringView getSqlConstraint() const = 0;
    virtual StringView getVirtualTableUsing() const = 0;
    virtual bool isWithoutRowid() const = 0;

    virtual Span<const TableInfo> getTables() const = 0;

    virtual StringView getTemplateName() const = 0;
    virtual Span<const TemplateArgumentInfo> getTemplateArguments() const = 0;

    virtual Span<const MessageInfo> getMessages() const = 0;
    virtual Span<const MethodInfo> getMethods() const = 0;

    // TODO[Mi-L@]:
    // - do we need value constructor? Or would the empty constructor with setters enough?
    // - what about the object lifetime
    // IntrospectablePtr createInstance(const ::std::vector<IIntrospectablePtr>& arguments);
};

// TODO[Mi-L@]: Copy comment

struct FieldInfo
{
    StringView schemaName;
    const ITypeInfo& typeInfo;
    Span<const StringView> typeArguments;
    StringView alignment;
    StringView offset;
    StringView initializer;
    bool isOptional;
    StringView optionalCondition;
    StringView constraint;
    bool isArray;
    StringView arrayLength;
    bool isPacked;
    bool isImplicit;
};

struct ParameterInfo
{
    StringView schemaName;
    const ITypeInfo& typeInfo;
};

struct FunctionInfo
{
    StringView schemaName;
    const ITypeInfo& typeInfo;
    StringView functionResult;
};

struct ColumnInfo
{
    StringView schemaName;
    const ITypeInfo& typeInfo;
    Span<const StringView> typeArguments;
    StringView sqlTypeName;
    StringView sqlConstraint;
    bool isVirtual;
};

struct TableInfo
{
    StringView schemaName;
    const ITypeInfo& typeInfo;
};

struct CaseInfo
{
    Span<const StringView> caseExpressions;
    const FieldInfo* field;
};

struct ItemInfo
{
    StringView schemaName;
    StringView value;
};

struct TemplateArgumentInfo
{
    const ITypeInfo& typeInfo;
};

struct MessageInfo
{
    StringView schemaName;
    const ITypeInfo& typeInfo;
    bool isPublished;
    bool isSubscribed;
    StringView topic;
};

struct MethodInfo
{
    StringView schemaName;
    const ITypeInfo& responseTypeInfo;
    const ITypeInfo& requestTypeInfo;
};

/**
 * Gets type info for the given enum type.
 *
 * \return Enum type info.
 */
template <typename T>
const ITypeInfo& enumTypeInfo();

} // namespace

#endif // ZSERIO_I_TYPE_INFO_INC_H
