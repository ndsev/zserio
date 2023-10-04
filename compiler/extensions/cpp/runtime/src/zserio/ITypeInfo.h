#ifndef ZSERIO_I_TYPE_INFO_INC_H
#define ZSERIO_I_TYPE_INFO_INC_H

#include "zserio/Span.h"
#include "zserio/StringView.h"
#include "zserio/IReflectable.h"

namespace zserio
{

/** Enumeration which specifies C++ type used in type information. */
enum class CppType
{
    BOOL, /**< C++ bool type */
    INT8, /**< C++ int8_t type */
    INT16, /**< C++ int16_t type */
    INT32, /**< C++ int32_t type */
    INT64, /**< C++ int64_t type */
    UINT8, /**< C++ uint8_t type */
    UINT16, /**< C++ uint16_t type */
    UINT32, /**< C++ int32_t type */
    UINT64, /**< C++ int64_t type */
    FLOAT, /**< C++ float type */
    DOUBLE, /**< C++ double type */
    BYTES, /**< C++ bytes type (mapped as std::vector<uint8_t>) */
    STRING, /**< C++ string type */
    BIT_BUFFER, /**< C++ zserio::BitBuffer type */
    ENUM, /**< C++ enumeration generated from zserio enumeration type */
    BITMASK, /**< C++ object generated from zserio bitmask type */
    STRUCT, /**< C++ object generated from zserio structure type */
    CHOICE, /**< C++ object generated from zserio choice type */
    UNION, /**< C++ object generated from zserio union type */
    SQL_TABLE, /**< C++ object generated from zserio SQL table type */
    SQL_DATABASE, /**< C++ object generated from zserio SQL database type */
    SERVICE, /**< C++ object generated from zserio service type */
    PUBSUB /**< C++ object generated from zserio pubsub type */
};

/** Enumeration which specifies zserio type used in type information. */
enum class SchemaType
{
    BOOL, /**< zserio bool type */
    INT8, /**< zserio int8 type */
    INT16, /**< zserio int16 type */
    INT32, /**< zserio int32 type */
    INT64, /**< zserio int64 type */
    UINT8, /**< zserio uint8 type */
    UINT16, /**< zserio uint16 type */
    UINT32, /**< zserio uint32 type */
    UINT64, /**< zserio uint64 type */
    VARINT16, /**< zserio varint16 type */
    VARINT32, /**< zserio varint32 type */
    VARINT64, /**< zserio varint64 type */
    VARINT, /**< zserio varint type */
    VARUINT16, /**< zserio varuint16 type */
    VARUINT32, /**< zserio varuint32 type */
    VARUINT64, /**< zserio varuint64 type */
    VARUINT, /**< zserio varuint type */
    VARSIZE, /**< zserio varsize type */
    FIXED_SIGNED_BITFIELD, /**< zserio fixed signed bitfield type */
    FIXED_UNSIGNED_BITFIELD, /**< zserio fixed unsigned bitfield type */
    DYNAMIC_SIGNED_BITFIELD, /**< zserio dynamic signed bitfield type */
    DYNAMIC_UNSIGNED_BITFIELD, /**< zserio dynamic unsigned bitfield type */
    FLOAT16, /**< zserio float16 type */
    FLOAT32, /**< zserio float32 type */
    FLOAT64, /**< zserio float64 type */
    BYTES, /**< zserio bytes type */
    STRING, /**< zserio string type */
    EXTERN, /**< zserio extern type */
    ENUM, /**< zserio enumeration type */
    BITMASK, /**< zserio bitmask type */
    STRUCT, /**< zserio structure type */
    CHOICE, /**< zserio choice type */
    UNION, /**< zserio union type */
    SQL_TABLE, /**< zserio SQL table type */
    SQL_DATABASE, /**< zserio SQL database type */
    SERVICE, /**< zserio service type */
    PUBSUB /**< zserio pubsub type */
};

// forward declarations
template <typename ALLOC>
struct BasicFieldInfo;
template <typename ALLOC>
struct BasicParameterInfo;
template <typename ALLOC>
struct BasicFunctionInfo;
template <typename ALLOC>
struct BasicCaseInfo;
template <typename ALLOC>
struct BasicColumnInfo;
template <typename ALLOC>
struct BasicTableInfo;
struct ItemInfo;
template <typename ALLOC>
struct BasicTemplateArgumentInfo;
template <typename ALLOC>
struct BasicMessageInfo;
template <typename ALLOC>
struct BasicMethodInfo;

/**
 * Type information interface which is returned from the generated zserio objects.
 *
 * This interface provides additional schema information of the corresponded zserio object, like schema
 * name, schema type, etc...
 *
 * Not all methods are implemented for all zserio objects. For example, the method getFields() is implemented
 * for compound types only. To check the zserio object type consider to use TypeInfoUtil helper methods.
 */
template <typename ALLOC = std::allocator<uint8_t>>
class IBasicTypeInfo
{
public:
    /**
     * Virtual destructor.
     */
    virtual ~IBasicTypeInfo() = default;

    /**
     * Gets the schema name.
     *
     * \return The zserio full name stored in schema.
     */
    virtual StringView getSchemaName() const = 0;

    /**
     * Gets the schema type.
     *
     * \return The zserio type stored in schema.
     */
    virtual SchemaType getSchemaType() const = 0;

    /**
     * Gets the C++ type.
     *
     * \return The C++ type to which zserio type is mapped.
     */
    virtual CppType getCppType() const = 0;

    // method for fixed size integral types

    /**
     * Gets the bit size of the fixed size integral schema type.
     *
     * \return The bit size of zserio type.
     *
     * \throw CppRuntimeException If the zserio type is not fixed size integral (e.g. varint).
     */
    virtual uint8_t getBitSize() const = 0;

    // methods for compound types

    /**
     * Gets the type information for compound type fields.
     *
     * \return Sequence of type informations for fields.
     *
     * \throw CppRuntimeException If the zserio type is not compound type.
     */
    virtual Span<const BasicFieldInfo<ALLOC>> getFields() const = 0;

    /**
     * Gets the type information for compound type parameters.
     *
     * \return Sequence of type informations for parameters.
     *
     * \throw CppRuntimeException If the zserio type is not compound type.
     */
    virtual Span<const BasicParameterInfo<ALLOC>> getParameters() const = 0;

    /**
     * Gets the type information for compound type functions.
     *
     * \return Sequence of type informations for functions.
     *
     * \throw CppRuntimeException If the zserio type is not compound type.
     */
    virtual Span<const BasicFunctionInfo<ALLOC>> getFunctions() const = 0;

    // methods for choice type

    /**
     * Gets the selector for choice type.
     *
     * \return Selector expression of choice type.
     *
     * \throw CppRuntimeException If the zserio type is not choice type.
     */
    virtual StringView getSelector() const = 0;

    /**
     * Gets the type information for choice type cases.
     *
     * \return Sequence of type informations for choice type cases.
     *
     * \throw CppRuntimeException If the zserio type is not choice type.
     */
    virtual Span<const BasicCaseInfo<ALLOC>> getCases() const = 0;

    // methods for enumeration and bitmask types

    /**
     * Gets the reference to type information of underlying zserio type.
     *
     * \return Reference to type information of underlying zserio type.
     *
     * \throw CppRuntimeException If the zserio type is not enumeration or bitmask type.
     */
    virtual const IBasicTypeInfo<ALLOC>& getUnderlyingType() const = 0;

    /**
     * Gets the reference to type information of underlying zserio type arguments.
     *
     * \return Underlying zserio type arguments.
     *
     * \throw CppRuntimeException If the zserio type is not enumeration or bitmask type.
     */
    virtual Span<const StringView> getUnderlyingTypeArguments() const = 0;

    /**
     * Gets the type information for enumeration type items.
     *
     * \return Sequence of type informations for enumeration type items.
     *
     * \throw CppRuntimeException If the zserio type is not enumeration type.
     */
    virtual Span<const ItemInfo> getEnumItems() const = 0;

    /**
     * Gets the type information for bitmask type values.
     *
     * \return Sequence of type informations for bitmask type values.
     *
     * \throw CppRuntimeException If the zserio type is not bitmask type.
     */
    virtual Span<const ItemInfo> getBitmaskValues() const = 0;

    // methods for SQL table types

    /**
     * Gets the type information for SQL table columns.
     *
     * \return Sequence of type informations for SQL table columns.
     *
     * \throw CppRuntimeException If the zserio type is not SQL table type.
     */
    virtual Span<const BasicColumnInfo<ALLOC>> getColumns() const = 0;

    /**
     * Gets the SQL table constraint.
     *
     * \return The SQL table constraint.
     *
     * \throw CppRuntimeException If the zserio type is not SQL table type.
     */
    virtual StringView getSqlConstraint() const = 0;

    /**
     * Gets the SQL virtual table using specification.
     *
     * \return The SQL virtual table using specification.
     *
     * \throw CppRuntimeException If the zserio type is not SQL table type.
     */
    virtual StringView getVirtualTableUsing() const = 0;

    /**
     * Checks if SQL table is without row id table.
     *
     * \return true if SQL table is without row id table, otherwise false.
     *
     * \throw CppRuntimeException If the zserio type is not SQL table type.
     */
    virtual bool isWithoutRowId() const = 0;

    // method for SQL database type

    /**
     * Gets the type information for SQL database tables.
     *
     * \return Sequence of type informations for SQL database tables.
     *
     * \throw CppRuntimeException If the zserio type is not SQL database type.
     */
    virtual Span<const BasicTableInfo<ALLOC>> getTables() const = 0;

    // methods for templatable types

    /**
     * Gets the full schema template name.
     *
     * \return The full schema template name.
     *
     * \throw CppRuntimeException If the zserio type is not templatable.
     */
    virtual StringView getTemplateName() const = 0;

    /**
     * Gets the type information for template arguments.
     *
     * \return Sequence of type informations for template arguments.
     *
     * \throw CppRuntimeException If the zserio type is not templatable.
     */
    virtual Span<const BasicTemplateArgumentInfo<ALLOC>> getTemplateArguments() const = 0;

    // method for pubsub type

    /**
     * Gets the type information for pubsub messages.
     *
     * \return Sequence of type informations for pubsub messages.
     *
     * \throw CppRuntimeException If the zserio type is not pubsub type.
     */
    virtual Span<const BasicMessageInfo<ALLOC>> getMessages() const = 0;

    // method for service type

    /**
     * Gets the type information for service methods.
     *
     * \return Sequence of type informations for service methods.
     *
     * \throw CppRuntimeException If the zserio type is not service type.
     */
    virtual Span<const BasicMethodInfo<ALLOC>> getMethods() const = 0;

    /**
     * Creates new instance of the zserio compound type.
     *
     * \param allocator Allocator to use for allocation of new instance.
     *
     * \return New instance of zserio compound type.
     *
     * \throw CppRuntimeException If the zserio type is not compound type.
     */
    virtual IBasicReflectablePtr<ALLOC> createInstance(const ALLOC& allocator) const = 0;

    /**
     * Creates new instance of the zserio compound type.
     *
     * \note Default constructed allocator is used for allocation of new instance.
     *
     * \return New instance of zserio compound type.
     *
     * \throw CppRuntimeException If the zserio type is not compound type.
     */
    virtual IBasicReflectablePtr<ALLOC> createInstance() const = 0;
};

/**
 * Type information for compound type field.
 */
template <typename ALLOC = std::allocator<uint8_t>>
struct BasicFieldInfo
{
    BasicFieldInfo(StringView schemaName_, const IBasicTypeInfo<ALLOC>& typeInfo_,
            Span<const StringView> typeArguments_, bool isExtended_, StringView alignment_, StringView offset_,
            StringView initializer_, bool isOptional_, StringView optionalCondition_,
            StringView constraint_, bool isArray_, StringView arrayLength_, bool isPacked_ ,bool isImplicit_) :
            schemaName(schemaName_), typeInfo(typeInfo_), typeArguments(typeArguments_),
            isExtended(isExtended_), alignment(alignment_), offset(offset_), initializer(initializer_),
            isOptional(isOptional_), optionalCondition(optionalCondition_), constraint(constraint_),
            isArray(isArray_), arrayLength(arrayLength_), isPacked(isPacked_), isImplicit(isImplicit_)
    {}

    StringView schemaName; /**< field schema name */
    const IBasicTypeInfo<ALLOC>& typeInfo; /**< reference to type information for a field type */
    Span<const StringView> typeArguments; /**< sequence of field type arguments */
    bool isExtended; /**< true if field is extended */
    StringView alignment; /**< field alignment or empty in case of no alignment */
    StringView offset; /**< field offset or empty in case of no alignment */
    StringView initializer; /**< field initializer or empty in case of no alignment */
    bool isOptional; /**< true if field is optional */
    StringView optionalCondition; /**< optional condition or empty if field is not optional */
    StringView constraint; /**< constraint or empty if field does not have constraint */
    bool isArray; /**< true if field is array */
    StringView arrayLength; /**< array length or empty if field is not array or is auto/implicit array */
    bool isPacked; /**< true if field is array and packed */
    bool isImplicit; /**< true if field is array and implicit */
};

/**
 * Type information for compound type parameter.
 */
template <typename ALLOC = std::allocator<uint8_t>>
struct BasicParameterInfo
{
    StringView schemaName; /**< parameter schema name */
    const IBasicTypeInfo<ALLOC>& typeInfo; /**< reference to type information for a parameter type */
};

/**
 * Type information for compound type function.
 */
template <typename ALLOC = std::allocator<uint8_t>>
struct BasicFunctionInfo
{
    StringView schemaName; /**< function schema name */
    const IBasicTypeInfo<ALLOC>& typeInfo; /**< reference to type information for a resulting function type */
    StringView functionResult; /**< specifies the function result */
};

/**
 * Type information for choice type case.
 */
template <typename ALLOC = std::allocator<uint8_t>>
struct BasicCaseInfo
{
    Span<const StringView> caseExpressions; /**< sequence of case expressions */
    const BasicFieldInfo<ALLOC>* field; /**< pointer to type information for a case field */
};

/**
 * Type information for enumeration type item or for bitmask type value.
 */
struct ItemInfo
{
    ItemInfo(StringView schemaName_, uint64_t value_, bool isDeprecated_, bool isRemoved_) :
            schemaName(schemaName_), value(value_), isDeprecated(isDeprecated_), isRemoved(isRemoved_)
    {}

    StringView schemaName; /**< enumeration item or bitmask value schema name */
    uint64_t value; /**< enumeration item or bitmask value cast to uint64_t */
    bool isDeprecated; /**< flag whether the item is deprecated */
    bool isRemoved; /**< flag whether the item is removed */
};

/**
 * Type information for SQL table column.
 */
template <typename ALLOC = std::allocator<uint8_t>>
struct BasicColumnInfo
{
    BasicColumnInfo(StringView schemaName_, const IBasicTypeInfo<ALLOC>& typeInfo_,
            Span<const StringView> typeArguments_, StringView sqlTypeName_, StringView sqlConstraint_,
            bool isVirtual_) :
            schemaName(schemaName_), typeInfo(typeInfo_), typeArguments(typeArguments_),
            sqlTypeName(sqlTypeName_), sqlConstraint(sqlConstraint_), isVirtual(isVirtual_)
    {}

    StringView schemaName; /**< column schema name */
    const IBasicTypeInfo<ALLOC>& typeInfo; /**< reference to type information for a column type */
    Span<const StringView> typeArguments; /**< sequence of column type arguments */
    StringView sqlTypeName; /**< column SQL type name */
    StringView sqlConstraint; /**< column constraint or empty if column does not have any constraint */
    bool isVirtual; /**< true if SQL table is virtual */
};

/**
 * Type information for SQL database table.
 */
template <typename ALLOC = std::allocator<uint8_t>>
struct BasicTableInfo
{
    StringView schemaName; /**< table schema name */
    const IBasicTypeInfo<ALLOC>& typeInfo; /**< reference to type information for a table */
};

/**
 * Type information for template argument.
 */
template <typename ALLOC = std::allocator<uint8_t>>
struct BasicTemplateArgumentInfo
{
    const IBasicTypeInfo<ALLOC>& typeInfo; /**< reference to type information for a template argument */
};

/**
 * Type information for pubsub message.
 */
template <typename ALLOC = std::allocator<uint8_t>>
struct BasicMessageInfo
{
    BasicMessageInfo(StringView schemaName_, const IBasicTypeInfo<ALLOC>& typeInfo_,
            bool isPublished_, bool isSubscribed_, StringView topic_) :
            schemaName(schemaName_), typeInfo(typeInfo_),
            isPublished(isPublished_), isSubscribed(isSubscribed_), topic(topic_)
    {}

    StringView schemaName; /**< message schema name */
    const IBasicTypeInfo<ALLOC>& typeInfo; /**< reference to type information for a message type */
    bool isPublished; /**< true if the message is published */
    bool isSubscribed; /**< true if the message is subscribed */
    StringView topic; /**< pubsub topic for a message */
};

/**
 * Type information for service method.
 */
template <typename ALLOC = std::allocator<uint8_t>>
struct BasicMethodInfo
{
    /** service schema name */
    StringView schemaName;
    /** reference to type information for a method response type */
    const IBasicTypeInfo<ALLOC>& responseTypeInfo;
    /** reference to type information for a method request type */
    const IBasicTypeInfo<ALLOC>& requestTypeInfo;
};

/**
 * Gets type info for the given enum type.
 *
 * \return Enum type info.
 */
template <typename T, typename ALLOC = std::allocator<uint8_t>>
const IBasicTypeInfo<ALLOC>& enumTypeInfo();

/** Typedef provided for convenience - using default std::allocator<uint8_t>. */
/** \{ */
using ITypeInfo = IBasicTypeInfo<>;
using FieldInfo = BasicFieldInfo<>;
using ParameterInfo = BasicParameterInfo<>;
using FunctionInfo = BasicFunctionInfo<>;
using CaseInfo = BasicCaseInfo<>;
using ColumnInfo = BasicColumnInfo<>;
using TableInfo = BasicTableInfo<>;
using TemplateArgumentInfo = BasicTemplateArgumentInfo<>;
using MessageInfo = BasicMessageInfo<>;
using MethodInfo = BasicMethodInfo<>;
/** \} */

} // namespace zserio

#endif // ZSERIO_I_TYPE_INFO_INC_H
