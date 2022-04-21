#include "zserio/JsonWriter.h"
#include "zserio/TypeInfoUtil.h"

using namespace zserio::literals;

namespace zserio
{

JsonWriter::JsonWriter(std::ostream& out,
        const std::string& itemSeparator, const std::string& keySeparator) :
        JsonWriter(out, NullOpt, itemSeparator, keySeparator)
{}

JsonWriter::JsonWriter(std::ostream& out, uint8_t indent,
        const std::string& itemSeparator, const std::string& keySeparator) :
        JsonWriter(out, std::string(indent, ' '), itemSeparator, keySeparator)
{}

JsonWriter::JsonWriter(std::ostream& out, const std::string& indent,
        const std::string& itemSeparator, const std::string& keySeparator) :
        JsonWriter(out, InplaceOptionalHolder<std::string>(indent), itemSeparator, keySeparator)
{}

JsonWriter::JsonWriter(std::ostream& out, InplaceOptionalHolder<std::string>&& optionalIndent,
        const std::string& itemSeparator, const std::string& keySeparator) :
        m_out(out), m_indent(optionalIndent), m_itemSeparator(itemSeparator), m_keySeparator(keySeparator)
{}

void JsonWriter::beginRoot(const IReflectablePtr&)
{
    beginObject();
}

void JsonWriter::endRoot(const IReflectablePtr&)
{
    endObject();
    m_out.flush();
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
        m_out.write(m_itemSeparator.data(), m_itemSeparator.size());

    if (m_indent.hasValue())
        m_out.put('\n');

    writeIndent();
}

void JsonWriter::endItem()
{
    m_isFirst = false;
}

void JsonWriter::beginObject()
{
    m_out.put('{');

    m_isFirst = true;
    m_level += 1;
}

void JsonWriter::endObject()
{
    if (m_indent.hasValue())
        m_out.put('\n');

    m_level -= 1;

    writeIndent();

    m_out.put('}');
}

void JsonWriter::beginArray()
{
    m_out.put('[');

    m_isFirst = true;
    m_level += 1;
}

void JsonWriter::endArray()
{
    if (m_indent.hasValue())
        m_out.put('\n');

    m_level -= 1;

    writeIndent();

    m_out.put(']');
}

void JsonWriter::writeIndent()
{
    if (m_indent.hasValue())
    {
        const std::string& indent = m_indent.value();
        if (!indent.empty())
        {
            for (size_t i = 0; i < m_level; ++i)
                m_out.write(indent.data(), indent.size());
        }
    }
}

void JsonWriter::writeKey(StringView key)
{
    m_out.put('\"');
    m_out.write(key.data(), key.size());
    m_out.put('\"');
    m_out.write(m_keySeparator.data(), m_keySeparator.size());
    m_out.flush();
}

void JsonWriter::writeValue(const IReflectablePtr& reflectable)
{
    // TODO[Mi-L@]: Ensure that all types are printed as valid JSON values! (floats, doubles, strings, ...)

    if (!reflectable)
    {
        m_out << "null";
        return;
    }

    const ITypeInfo& typeInfo = reflectable->getTypeInfo();
    switch (typeInfo.getCppType())
    {
    case CppType::BOOL:
        m_out << zserio::toString(reflectable->getBool());
        break;
    case CppType::INT8:
    case CppType::INT16:
    case CppType::INT32:
    case CppType::INT64:
        m_out << reflectable->toInt();
        break;
    case CppType::UINT8:
    case CppType::UINT16:
    case CppType::UINT32:
    case CppType::UINT64:
        m_out << reflectable->toUInt();
        break;
    case CppType::FLOAT:
        m_out << reflectable->getFloat();
        break;
    case CppType::DOUBLE:
        m_out << reflectable->getDouble();
        break;
    case CppType::STRING:
    {
        StringView stringValue = reflectable->getString();
        m_out.write(stringValue.data(), stringValue.size());
        break;
    }
    case CppType::BIT_BUFFER:
        writeBitBuffer(reflectable->getBitBuffer());
        break;
    case CppType::ENUM:
    case CppType::BITMASK:
        if (TypeInfoUtil::isSigned(typeInfo.getUnderlyingType().getCppType()))
            m_out << reflectable->toInt();
        else
            m_out << reflectable->toUInt();
        break;
    default:
        throw CppRuntimeException("JsonWriter: Unexpected not-null value of type '") +
                typeInfo.getSchemaName() + "'!";
    }

    m_out.flush();
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
        m_out << static_cast<int>(buffer[i]);
        endItem();
    }
    endArray();
    endItem();
    beginItem();
    writeKey("bitSize"_sv);
    m_out << bitBuffer.getBitSize();
    endItem();
    endObject();
}

} // namespace zserio
