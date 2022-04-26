#ifndef ZSERIO_JSON_WRITER_H_INC
#define ZSERIO_JSON_WRITER_H_INC

#include <ostream>
#include <memory>

#include "zserio/IWalkObserver.h"
#include "zserio/OptionalHolder.h"
#include "zserio/TypeInfoUtil.h"

namespace zserio
{

/**
 * Walker observer which dumps zserio objects to JSON format.
 */
template <typename ALLOC = std::allocator<uint8_t>>
class BasicJsonWriter : public IBasicWalkObserver<ALLOC>
{
public:
    static constexpr const char* DEFAULT_ITEM_SEPARATOR = ", ";
    static constexpr const char* DEFAULT_ITEM_SEPARATOR_WITH_INDENT = ",";
    static constexpr const char* DEFAULT_KEY_SEPARATOR = ": ";

    explicit BasicJsonWriter(const std::shared_ptr<std::ostream>& out, const ALLOC& allocator = ALLOC());
    BasicJsonWriter(const std::shared_ptr<std::ostream>& out, uint8_t indent, const ALLOC& allocator = ALLOC());
    BasicJsonWriter(const std::shared_ptr<std::ostream>& out, const string<ALLOC>& indent,
            const ALLOC& allocator = ALLOC());

    void setItemSeparator(const string<ALLOC>& itemSeparator);
    void setKeySeparator(const string<ALLOC>& keySeparator);

    virtual void beginRoot(const IBasicReflectablePtr<ALLOC>& compound) override;
    virtual void endRoot(const IBasicReflectablePtr<ALLOC>& compound) override;

    virtual void beginArray(const IBasicReflectablePtr<ALLOC>& array, const FieldInfo& fieldInfo) override;
    virtual void endArray(const IBasicReflectablePtr<ALLOC>& array, const FieldInfo& fieldInfo) override;

    virtual void beginCompound(const IBasicReflectablePtr<ALLOC>& compound, const FieldInfo& fieldInfo,
            size_t elementIndex) override;
    virtual void endCompound(const IBasicReflectablePtr<ALLOC>& compound, const FieldInfo& fieldInfo,
            size_t elementIndex) override;

    virtual void visitValue(const IBasicReflectablePtr<ALLOC>& value, const FieldInfo& fieldInfo,
            size_t elementIndex) override;

private:
    BasicJsonWriter(const std::shared_ptr<std::ostream>& out,
            InplaceOptionalHolder<string<ALLOC>>&& optionalIndent, const ALLOC& allocator = ALLOC());

    std::ostream& out()
    {
        return *m_out;
    }

    void beginItem();
    void endItem();
    void beginObject();
    void endObject();
    void beginArray();
    void endArray();

    void writeIndent();
    void writeKey(StringView key);
    void writeValue(const IReflectablePtr& value);
    void writeBitBuffer(const BitBuffer& bitBuffer);

    std::shared_ptr<std::ostream> m_out;
    InplaceOptionalHolder<string<ALLOC>> m_indent;
    string<ALLOC> m_itemSeparator;
    string<ALLOC> m_keySeparator;

    bool m_isFirst = true;
    size_t m_level = 0;
};

/** Typedef to JsonWriter provided for convenience - using default std::allocator<uint8_t>. */
/** \{ */
using JsonWriter = BasicJsonWriter<>;
/** \} */

template <typename ALLOC>
BasicJsonWriter<ALLOC>::BasicJsonWriter(const std::shared_ptr<std::ostream>& out, const ALLOC& allocator) :
        BasicJsonWriter(out, NullOpt, allocator)
{}

template <typename ALLOC>
BasicJsonWriter<ALLOC>::BasicJsonWriter(const std::shared_ptr<std::ostream>& out, uint8_t indent,
        const ALLOC& allocator) :
        BasicJsonWriter(out, string<ALLOC>(indent, ' ', allocator), allocator)
{}

template <typename ALLOC>
BasicJsonWriter<ALLOC>::BasicJsonWriter(const std::shared_ptr<std::ostream>& out, const string<ALLOC>& indent,
        const ALLOC& allocator) :
        BasicJsonWriter(out, InplaceOptionalHolder<string<ALLOC>>(indent), allocator)
{}

template <typename ALLOC>
BasicJsonWriter<ALLOC>::BasicJsonWriter(const std::shared_ptr<std::ostream>& out,
        InplaceOptionalHolder<string<ALLOC>>&& optionalIndent, const ALLOC& allocator) :
        m_out(out), m_indent(optionalIndent),
        m_itemSeparator(m_indent.hasValue() ? DEFAULT_ITEM_SEPARATOR_WITH_INDENT : DEFAULT_ITEM_SEPARATOR,
                allocator),
        m_keySeparator(DEFAULT_KEY_SEPARATOR, allocator)
{}

template <typename ALLOC>
void BasicJsonWriter<ALLOC>::setItemSeparator(const string<ALLOC>& itemSeparator)
{
    m_itemSeparator = itemSeparator;
}

template <typename ALLOC>
void BasicJsonWriter<ALLOC>::setKeySeparator(const string<ALLOC>& keySeparator)
{
    m_keySeparator = keySeparator;
}

template <typename ALLOC>
void BasicJsonWriter<ALLOC>::beginRoot(const IBasicReflectablePtr<ALLOC>&)
{
    beginObject();
}

template <typename ALLOC>
void BasicJsonWriter<ALLOC>::endRoot(const IBasicReflectablePtr<ALLOC>&)
{
    endObject();
    out().flush();
}

template <typename ALLOC>
void BasicJsonWriter<ALLOC>::beginArray(const IBasicReflectablePtr<ALLOC>&, const FieldInfo& fieldInfo)
{
    beginItem();

    writeKey(fieldInfo.schemaName);

    beginArray();
}

template <typename ALLOC>
void BasicJsonWriter<ALLOC>::endArray(const IBasicReflectablePtr<ALLOC>&, const FieldInfo&)
{
    m_isFirst = false;

    endArray();

    endItem();
}

template <typename ALLOC>
void BasicJsonWriter<ALLOC>::beginCompound(const IBasicReflectablePtr<ALLOC>&, const FieldInfo& fieldInfo, size_t elementIndex)
{
    beginItem();

    if (elementIndex == WALKER_NOT_ELEMENT)
        writeKey(fieldInfo.schemaName);

    beginObject();
}

template <typename ALLOC>
void BasicJsonWriter<ALLOC>::endCompound(const IBasicReflectablePtr<ALLOC>&, const FieldInfo&, size_t)
{
    m_isFirst = false;

    endObject();

    endItem();
}

template <typename ALLOC>
void BasicJsonWriter<ALLOC>::visitValue(const IBasicReflectablePtr<ALLOC>& value, const FieldInfo& fieldInfo,
        size_t elementIndex)
{
    beginItem();

    if (elementIndex == WALKER_NOT_ELEMENT)
        writeKey(fieldInfo.schemaName);

    writeValue(value);

    endItem();
}

template <typename ALLOC>
void BasicJsonWriter<ALLOC>::beginItem()
{
    if (!m_isFirst)
        out().write(m_itemSeparator.data(), static_cast<std::streamsize>(m_itemSeparator.size()));

    if (m_indent.hasValue())
        out().put('\n');

    writeIndent();
}

template <typename ALLOC>
void BasicJsonWriter<ALLOC>::endItem()
{
    m_isFirst = false;
}

template <typename ALLOC>
void BasicJsonWriter<ALLOC>::beginObject()
{
    out().put('{');

    m_isFirst = true;
    m_level += 1;
}

template <typename ALLOC>
void BasicJsonWriter<ALLOC>::endObject()
{
    if (m_indent.hasValue())
        out().put('\n');

    m_level -= 1;

    writeIndent();

    out().put('}');
}

template <typename ALLOC>
void BasicJsonWriter<ALLOC>::beginArray()
{
    out().put('[');

    m_isFirst = true;
    m_level += 1;
}

template <typename ALLOC>
void BasicJsonWriter<ALLOC>::endArray()
{
    if (m_indent.hasValue())
        out().put('\n');

    m_level -= 1;

    writeIndent();

    out().put(']');
}

template <typename ALLOC>
void BasicJsonWriter<ALLOC>::writeIndent()
{
    if (m_indent.hasValue())
    {
        const auto& indent = m_indent.value();
        if (!indent.empty())
        {
            for (size_t i = 0; i < m_level; ++i)
                out().write(indent.data(), static_cast<std::streamsize>(indent.size()));
        }
    }
}

template <typename ALLOC>
void BasicJsonWriter<ALLOC>::writeKey(StringView key)
{
    out().put('\"');
    out().write(key.data(), static_cast<std::streamsize>(key.size()));
    out().put('\"');
    out().write(m_keySeparator.data(), static_cast<std::streamsize>(m_keySeparator.size()));
    out().flush();
}

template <typename ALLOC>
void BasicJsonWriter<ALLOC>::writeValue(const IReflectablePtr& reflectable)
{
    // TODO[Mi-L@]: Ensure that all types are printed as valid JSON values! (floats, doubles, strings, ...)

    if (!reflectable)
    {
        out() << "null";
        return;
    }

    const ITypeInfo& typeInfo = reflectable->getTypeInfo();
    switch (typeInfo.getCppType())
    {
    case CppType::BOOL:
        out() << zserio::toString(reflectable->getBool());
        break;
    case CppType::INT8:
    case CppType::INT16:
    case CppType::INT32:
    case CppType::INT64:
        out() << reflectable->toInt();
        break;
    case CppType::UINT8:
    case CppType::UINT16:
    case CppType::UINT32:
    case CppType::UINT64:
        out() << reflectable->toUInt();
        break;
    case CppType::FLOAT:
        // TODO[mikir] hot fix to have the same behavior as python (1.0)
        out() << std::fixed << std::setprecision(1) << reflectable->getFloat();
        break;
    case CppType::DOUBLE:
        // TODO[mikir] hot fix to have the same behavior as python (1.0)
        out() << std::fixed << std::setprecision(1) << reflectable->getDouble();
        break;
    case CppType::STRING:
    {
        StringView stringValue = reflectable->getString();
        out().put('\"');
        out().write(stringValue.data(), static_cast<std::streamsize>(stringValue.size()));
        out().put('\"');
        break;
    }
    case CppType::BIT_BUFFER:
        writeBitBuffer(reflectable->getBitBuffer());
        break;
    case CppType::ENUM:
    case CppType::BITMASK:
        if (TypeInfoUtil::isSigned(typeInfo.getUnderlyingType().getCppType()))
            out() << reflectable->toInt();
        else
            out() << reflectable->toUInt();
        break;
    default:
        throw CppRuntimeException("JsonWriter: Unexpected not-null value of type '") +
                typeInfo.getSchemaName() + "'!";
    }

    out().flush();
}

template <typename ALLOC>
void BasicJsonWriter<ALLOC>::writeBitBuffer(const BitBuffer& bitBuffer)
{
    beginObject();
    beginItem();
    writeKey("buffer"_sv);
    beginArray();
    const uint8_t* buffer = bitBuffer.getBuffer();
    for (size_t i = 0; i < bitBuffer.getByteSize(); ++i)
    {
        beginItem();
        out() << static_cast<int>(buffer[i]);
        endItem();
    }
    endArray();
    endItem();
    beginItem();
    writeKey("bitSize"_sv);
    out() << bitBuffer.getBitSize();
    endItem();
    endObject();
}

} // namespace zserio

#endif // ZSERIO_JSON_WRITER_H_INC
