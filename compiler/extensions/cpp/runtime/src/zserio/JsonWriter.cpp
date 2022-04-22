#include "zserio/JsonWriter.h"
#include "zserio/TypeInfoUtil.h"

using namespace zserio::literals;

namespace zserio
{

JsonWriter::JsonWriter(const std::shared_ptr<std::ostream>& out) :
        JsonWriter(out, NullOpt)
{}

JsonWriter::JsonWriter(const std::shared_ptr<std::ostream>& out, uint8_t indent) :
        JsonWriter(out, std::string(indent, ' '))
{}

JsonWriter::JsonWriter(const std::shared_ptr<std::ostream>& out, const std::string& indent) :
        JsonWriter(out, InplaceOptionalHolder<std::string>(indent))
{}

JsonWriter::JsonWriter(const std::shared_ptr<std::ostream>& out,
        InplaceOptionalHolder<std::string>&& optionalIndent) :
        m_out(out), m_indent(optionalIndent),
        m_itemSeparator(m_indent.hasValue() ? DEFAULT_ITEM_SEPARATOR_WITH_INDENT : DEFAULT_ITEM_SEPARATOR),
        m_keySeparator(DEFAULT_KEY_SEPARATOR)
{}

void JsonWriter::setItemSeparator(const std::string& itemSeparator)
{
    m_itemSeparator = itemSeparator;
}

void JsonWriter::setKeySeparator(const std::string& keySeparator)
{
    m_keySeparator = keySeparator;
}

void JsonWriter::beginRoot(const IReflectablePtr&)
{
    beginObject();
}

void JsonWriter::endRoot(const IReflectablePtr&)
{
    endObject();
    out().flush();
}

void JsonWriter::beginArray(const IReflectablePtr&, const FieldInfo& fieldInfo)
{
    beginItem();

    writeKey(fieldInfo.schemaName);

    beginArray();
}

void JsonWriter::endArray(const IReflectablePtr&, const FieldInfo&)
{
    m_isFirst = false;

    endArray();

    endItem();
}

void JsonWriter::beginCompound(const IReflectablePtr&, const FieldInfo& fieldInfo, size_t elementIndex)
{
    beginItem();

    if (elementIndex == WALKER_NOT_ELEMENT)
        writeKey(fieldInfo.schemaName);

    beginObject();
}

void JsonWriter::endCompound(const IReflectablePtr&, const FieldInfo&, size_t)
{
    m_isFirst = false;

    endObject();

    endItem();
}

void JsonWriter::visitValue(const IReflectablePtr& value, const FieldInfo& fieldInfo,
        size_t elementIndex)
{
    beginItem();

    if (elementIndex == WALKER_NOT_ELEMENT)
        writeKey(fieldInfo.schemaName);

    writeValue(value);

    endItem();
}

void JsonWriter::beginItem()
{
    if (!m_isFirst)
        out().write(m_itemSeparator.data(), m_itemSeparator.size());

    if (m_indent.hasValue())
        out().put('\n');

    writeIndent();
}

void JsonWriter::endItem()
{
    m_isFirst = false;
}

void JsonWriter::beginObject()
{
    out().put('{');

    m_isFirst = true;
    m_level += 1;
}

void JsonWriter::endObject()
{
    if (m_indent.hasValue())
        out().put('\n');

    m_level -= 1;

    writeIndent();

    out().put('}');
}

void JsonWriter::beginArray()
{
    out().put('[');

    m_isFirst = true;
    m_level += 1;
}

void JsonWriter::endArray()
{
    if (m_indent.hasValue())
        out().put('\n');

    m_level -= 1;

    writeIndent();

    out().put(']');
}

void JsonWriter::writeIndent()
{
    if (m_indent.hasValue())
    {
        const std::string& indent = m_indent.value();
        if (!indent.empty())
        {
            for (size_t i = 0; i < m_level; ++i)
                out().write(indent.data(), indent.size());
        }
    }
}

void JsonWriter::writeKey(StringView key)
{
    out().put('\"');
    out().write(key.data(), key.size());
    out().put('\"');
    out().write(m_keySeparator.data(), m_keySeparator.size());
    out().flush();
}

void JsonWriter::writeValue(const IReflectablePtr& reflectable)
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
        out() << reflectable->getFloat();
        break;
    case CppType::DOUBLE:
        out() << reflectable->getDouble();
        break;
    case CppType::STRING:
    {
        StringView stringValue = reflectable->getString();
        out().put('\"');
        out().write(stringValue.data(), stringValue.size());
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

void JsonWriter::writeBitBuffer(const BitBuffer& bitBuffer)
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
