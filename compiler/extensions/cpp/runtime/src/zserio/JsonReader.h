#ifndef ZSERIO_JSON_READER_H_INC
#define ZSERIO_JSON_READER_H_INC

#include <istream>
#include <limits>

#include "zserio/AllocatorHolder.h"
#include "zserio/JsonParser.h"
#include "zserio/OptionalHolder.h"
#include "zserio/StringView.h"
#include "zserio/ZserioTreeCreator.h"
#include "zserio/UniquePtr.h"
#include "zserio/SizeConvertUtil.h"

namespace zserio
{

namespace detail
{

// adapter for values which are encoded as a JSON object
template <typename ALLOC>
class IObjectValueAdapter : public BasicJsonParser<ALLOC>::IObserver
{
public:
    virtual AnyHolder<ALLOC> get() const = 0;
};

template <typename ALLOC>
class BitBufferAdapter : public IObjectValueAdapter<ALLOC>, public AllocatorHolder<ALLOC>
{
public:
    using AllocatorHolder<ALLOC>::get_allocator;

    explicit BitBufferAdapter(const ALLOC& allocator) :
            AllocatorHolder<ALLOC>(allocator), m_state(VISIT_KEY)
    {}
    ~BitBufferAdapter() override = default;

    BitBufferAdapter(BitBufferAdapter& other) = delete;
    BitBufferAdapter& operator=(BitBufferAdapter& other) = delete;

    BitBufferAdapter(BitBufferAdapter&& other) :
            m_state(other.m_state), m_buffer(std::move(other.m_buffer)), m_bitSize(other.m_bitSize)
    {}

    BitBufferAdapter& operator=(BitBufferAdapter&& other)
    {
        m_state = other.m_state;
        m_buffer = std::move(other.m_buffer);
        m_bitSize = other.m_bitSize;

        return *this;
    }

    AnyHolder<ALLOC> get() const override;

    void beginObject() override;
    void endObject() override;
    void beginArray() override;
    void endArray() override;
    void visitKey(StringView key) override;
    void visitValue(std::nullptr_t) override;
    void visitValue(bool boolValue) override;
    void visitValue(int64_t intValue) override;
    void visitValue(uint64_t uintValue) override;
    void visitValue(double doubleValue) override;
    void visitValue(StringView stringValue) override;

private:
    enum State : uint8_t
    {
        VISIT_KEY,
        BEGIN_ARRAY_BUFFER,
        VISIT_VALUE_BUFFER,
        VISIT_VALUE_BITSIZE
    };

    State m_state;
    InplaceOptionalHolder<vector<uint8_t, ALLOC>> m_buffer;
    InplaceOptionalHolder<size_t> m_bitSize;
};

template <typename ALLOC>
class BytesAdapter : public IObjectValueAdapter<ALLOC>, public AllocatorHolder<ALLOC>
{
public:
    using AllocatorHolder<ALLOC>::get_allocator;

    explicit BytesAdapter(const ALLOC& allocator) :
            AllocatorHolder<ALLOC>(allocator), m_state(VISIT_KEY)
    {}
    ~BytesAdapter() override = default;

    BytesAdapter(BytesAdapter& other) = delete;
    BytesAdapter& operator=(BytesAdapter& other) = delete;

    BytesAdapter(BytesAdapter&& other) :
            m_state(other.m_state), m_buffer(std::move(other.m_buffer))
    {}

    BytesAdapter& operator=(BytesAdapter&& other)
    {
        m_state = other.m_state;
        m_buffer = std::move(other.m_buffer);

        return *this;
    }

    AnyHolder<ALLOC> get() const override;

    void beginObject() override;
    void endObject() override;
    void beginArray() override;
    void endArray() override;
    void visitKey(StringView key) override;
    void visitValue(std::nullptr_t) override;
    void visitValue(bool boolValue) override;
    void visitValue(int64_t intValue) override;
    void visitValue(uint64_t uintValue) override;
    void visitValue(double doubleValue) override;
    void visitValue(StringView stringValue) override;

private:
    enum State : uint8_t
    {
        VISIT_KEY,
        BEGIN_ARRAY_BUFFER,
        VISIT_VALUE_BUFFER,
    };

    State m_state;
    InplaceOptionalHolder<vector<uint8_t, ALLOC>> m_buffer;
};

template <typename ALLOC>
class CreatorAdapter : public BasicJsonParser<ALLOC>::IObserver, public AllocatorHolder<ALLOC>
{
public:
    using AllocatorHolder<ALLOC>::get_allocator;

    explicit CreatorAdapter(const ALLOC& allocator) :
            AllocatorHolder<ALLOC>(allocator)
    {}

    void setType(const IBasicTypeInfo<ALLOC>& typeInfo);
    IBasicReflectablePtr<ALLOC> get() const;

    void beginObject() override;
    void endObject() override;
    void beginArray() override;
    void endArray() override;
    void visitKey(StringView key) override;
    void visitValue(std::nullptr_t) override;
    void visitValue(bool boolValue) override;
    void visitValue(int64_t intValue) override;
    void visitValue(uint64_t uintValue) override;
    void visitValue(double doubleValue) override;
    void visitValue(StringView stringValue) override;

private:
    template <typename T>
    void setValue(T&& value);

    template <typename T>
    void convertValue(T&& value) const;

    InplaceOptionalHolder<BasicZserioTreeCreator<ALLOC>> m_creator;
    vector<string<ALLOC>, ALLOC> m_keyStack;
    IBasicReflectablePtr<ALLOC> m_object;
    unique_ptr<IObjectValueAdapter<ALLOC>, RebindAlloc<ALLOC, IObjectValueAdapter<ALLOC>>> m_objectValueAdapter;
};

} // namespace detail

/**
 * Reads zserio object tree defined by a type info from a text stream.
 */
template <typename ALLOC = std::allocator<uint8_t>>
class BasicJsonReader
{
public:
    /**
     * Constructor.
     *
     * \param in Text stream to read.
     * \param allocator Allocator to use.
     */
    explicit BasicJsonReader(std::istream& in, const ALLOC& allocator = ALLOC()) :
            m_creatorAdapter(allocator), m_parser(in, m_creatorAdapter, allocator)
    {}

    /**
     * Reads a zserio object tree defined by the given type info from the text stream.
     *
     * \param typeInfo Type info defining the expected zserio object tree.
     *
     * \return Zserio object tree initialized using the JSON data.
     * \throw CppRuntimeException When the JSON doesn't contain expected zserio object tree.
     */
    IBasicReflectablePtr<ALLOC> read(const IBasicTypeInfo<ALLOC>& typeInfo)
    {
        m_creatorAdapter.setType(typeInfo);

        try
        {
            m_parser.parse();
        }
        catch (const JsonParserException&)
        {
            throw;
        }
        catch (const CppRuntimeException& e)
        {
            throw CppRuntimeException(e.what()) <<
                    " (JsonParser:" << m_parser.getLine() << ":" << m_parser.getColumn() << ")";
        }

        return m_creatorAdapter.get();
    }

private:
    detail::CreatorAdapter<ALLOC> m_creatorAdapter;
    BasicJsonParser<ALLOC> m_parser;
};

/** Typedef to Json Reader provided for convenience - using default std::allocator<uint8_t>. */
using JsonReader = BasicJsonReader<>;

namespace detail
{

template <typename ALLOC>
AnyHolder<ALLOC> BitBufferAdapter<ALLOC>::get() const
{
    if (m_state != VISIT_KEY || !m_buffer.hasValue() || !m_bitSize.hasValue())
        throw CppRuntimeException("JsonReader: Unexpected end in BitBuffer!");

    return AnyHolder<ALLOC>(BasicBitBuffer<ALLOC>(m_buffer.value(), m_bitSize.value()), get_allocator());
}

template <typename ALLOC>
void BitBufferAdapter<ALLOC>::beginObject()
{
    throw CppRuntimeException("JsonReader: Unexpected beginObject in BitBuffer!");
}

template <typename ALLOC>
void BitBufferAdapter<ALLOC>::endObject()
{
    throw CppRuntimeException("JsonReader: Unexpected endObject in BitBuffer!");
}

template <typename ALLOC>
void BitBufferAdapter<ALLOC>::beginArray()
{
    if (m_state == BEGIN_ARRAY_BUFFER)
        m_state = VISIT_VALUE_BUFFER;
    else
        throw CppRuntimeException("JsonReader: Unexpected beginArray in BitBuffer!");
}

template <typename ALLOC>
void BitBufferAdapter<ALLOC>::endArray()
{
    if (m_state == VISIT_VALUE_BUFFER)
        m_state = VISIT_KEY;
    else
        throw CppRuntimeException("JsonReader: Unexpected endArray in BitBuffer!");
}

template <typename ALLOC>
void BitBufferAdapter<ALLOC>::visitKey(StringView key)
{
    if (m_state == VISIT_KEY)
    {
        if (key == "buffer"_sv)
            m_state = BEGIN_ARRAY_BUFFER;
        else if (key == "bitSize"_sv)
            m_state = VISIT_VALUE_BITSIZE;
        else
            throw CppRuntimeException("JsonReader: Unexpected key '") << key << "' in BitBuffer!";
    }
    else
    {
        throw CppRuntimeException("JsonReader: Unexpected visitKey in BitBuffer!");
    }
}

template <typename ALLOC>
void BitBufferAdapter<ALLOC>::visitValue(std::nullptr_t)
{
    throw CppRuntimeException("JsonReader: Unexpected visitValue (null) in BitBuffer!");
}

template <typename ALLOC>
void BitBufferAdapter<ALLOC>::visitValue(bool)
{
    throw CppRuntimeException("JsonReader: Unexpected visitValue (bool) in BitBuffer!");
}

template <typename ALLOC>
void BitBufferAdapter<ALLOC>::visitValue(int64_t)
{
    throw CppRuntimeException("JsonReader: Unexpected visitValue (int) in BitBuffer!");
}

template <typename ALLOC>
void BitBufferAdapter<ALLOC>::visitValue(uint64_t uintValue)
{
    if (m_state == VISIT_VALUE_BUFFER)
    {
        if (uintValue > static_cast<uint64_t>(std::numeric_limits<uint8_t>::max()))
        {
            throw CppRuntimeException("JsonReader: Cannot create byte for Bit Buffer from value '") <<
                    uintValue << "'!";
        }

        if (!m_buffer.hasValue())
            m_buffer = vector<uint8_t, ALLOC>(1, static_cast<uint8_t>(uintValue), get_allocator());
        else
            m_buffer->push_back(static_cast<uint8_t>(uintValue));
    }
    else if (m_state == VISIT_VALUE_BITSIZE)
    {
        m_bitSize = convertUInt64ToSize(uintValue);
        m_state = VISIT_KEY;
    }
    else
    {
        throw CppRuntimeException("JsonReader: Unexpected visitValue in BitBuffer!");
    }
}

template <typename ALLOC>
void BitBufferAdapter<ALLOC>::visitValue(double)
{
    throw CppRuntimeException("JsonReader: Unexpected visitValue (double) in BitBuffer!");
}

template <typename ALLOC>
void BitBufferAdapter<ALLOC>::visitValue(StringView)
{
    throw CppRuntimeException("JsonReader: Unexpected visitValue (string) in BitBuffer!");
}

template <typename ALLOC>
AnyHolder<ALLOC> BytesAdapter<ALLOC>::get() const
{
    if (m_state != VISIT_KEY || !m_buffer.hasValue())
        throw CppRuntimeException("JsonReader: Unexpected end in bytes!");

    return AnyHolder<ALLOC>(m_buffer.value(), get_allocator());
}

template <typename ALLOC>
void BytesAdapter<ALLOC>::beginObject()
{
    throw CppRuntimeException("JsonReader: Unexpected beginObject in bytes!");
}

template <typename ALLOC>
void BytesAdapter<ALLOC>::endObject()
{
    throw CppRuntimeException("JsonReader: Unexpected endObject in bytes!");
}

template <typename ALLOC>
void BytesAdapter<ALLOC>::beginArray()
{
    if (m_state == BEGIN_ARRAY_BUFFER)
        m_state = VISIT_VALUE_BUFFER;
    else
        throw CppRuntimeException("JsonReader: Unexpected beginArray in bytes!");
}

template <typename ALLOC>
void BytesAdapter<ALLOC>::endArray()
{
    if (m_state == VISIT_VALUE_BUFFER)
        m_state = VISIT_KEY;
    else
        throw CppRuntimeException("JsonReader: Unexpected endArray in bytes!");
}

template <typename ALLOC>
void BytesAdapter<ALLOC>::visitKey(StringView key)
{
    if (m_state == VISIT_KEY)
    {
        if (key == "buffer"_sv)
            m_state = BEGIN_ARRAY_BUFFER;
        else
            throw CppRuntimeException("JsonReader: Unexpected key '") << key << "' in bytes!";
    }
    else
    {
        throw CppRuntimeException("JsonReader: Unexpected visitKey in bytes!");
    }
}

template <typename ALLOC>
void BytesAdapter<ALLOC>::visitValue(std::nullptr_t)
{
    throw CppRuntimeException("JsonReader: Unexpected visitValue (null) in bytes!");
}

template <typename ALLOC>
void BytesAdapter<ALLOC>::visitValue(bool)
{
    throw CppRuntimeException("JsonReader: Unexpected visitValue (bool) in bytes!");
}

template <typename ALLOC>
void BytesAdapter<ALLOC>::visitValue(int64_t)
{
    throw CppRuntimeException("JsonReader: Unexpected visitValue (int) in bytes!");
}

template <typename ALLOC>
void BytesAdapter<ALLOC>::visitValue(uint64_t uintValue)
{
    if (m_state == VISIT_VALUE_BUFFER)
    {
        if (uintValue > static_cast<uint64_t>(std::numeric_limits<uint8_t>::max()))
        {
            throw CppRuntimeException("JsonReader: Cannot create byte for bytes from value '") <<
                    uintValue << "'!";
        }

        if (!m_buffer.hasValue())
            m_buffer = vector<uint8_t, ALLOC>(1, static_cast<uint8_t>(uintValue), get_allocator());
        else
            m_buffer->push_back(static_cast<uint8_t>(uintValue));
    }
    else
    {
        throw CppRuntimeException("JsonReader: Unexpected visitValue in bytes!");
    }
}

template <typename ALLOC>
void BytesAdapter<ALLOC>::visitValue(double)
{
    throw CppRuntimeException("JsonReader: Unexpected visitValue (double) in bytes!");
}

template <typename ALLOC>
void BytesAdapter<ALLOC>::visitValue(StringView)
{
    throw CppRuntimeException("JsonReader: Unexpected visitValue (string) in bytes!");
}

template <typename ALLOC>
void CreatorAdapter<ALLOC>::setType(const IBasicTypeInfo<ALLOC>& typeInfo)
{
    m_creator = BasicZserioTreeCreator<ALLOC>(typeInfo, get_allocator());
}

template <typename ALLOC>
IBasicReflectablePtr<ALLOC> CreatorAdapter<ALLOC>::get() const
{
    if (!m_object)
        throw CppRuntimeException("JsonReader: Zserio tree not created!");

    return m_object;
}

template <typename ALLOC>
void CreatorAdapter<ALLOC>::beginObject()
{
    if (m_objectValueAdapter)
    {
        m_objectValueAdapter->beginObject();
    }
    else
    {
        if (!m_creator)
            throw CppRuntimeException("JsonReader: Adapter not initialized!");

        if (m_keyStack.empty())
        {
            m_creator->beginRoot();
        }
        else
        {
            if (!m_keyStack.back().empty())
            {
                const CppType cppType = m_creator->getFieldType(m_keyStack.back()).getCppType();
                if (cppType == CppType::BIT_BUFFER)
                {
                    m_objectValueAdapter = allocate_unique<BitBufferAdapter<ALLOC>>(get_allocator(),
                            get_allocator());
                }
                else if (cppType == CppType::BYTES)
                {
                    m_objectValueAdapter = allocate_unique<BytesAdapter<ALLOC>>(get_allocator(),
                            get_allocator());
                }
                else
                {
                    m_creator->beginCompound(m_keyStack.back());
                }
            }
            else
            {
                const CppType cppType = m_creator->getElementType().getCppType();
                if (cppType == CppType::BIT_BUFFER)
                {
                    m_objectValueAdapter = allocate_unique<BitBufferAdapter<ALLOC>>(get_allocator(),
                            get_allocator());
                }
                else if (cppType == CppType::BYTES)
                {
                    m_objectValueAdapter = allocate_unique<BytesAdapter<ALLOC>>(get_allocator(),
                            get_allocator());
                }
                else
                {
                    m_creator->beginCompoundElement();
                }
            }
        }
    }
}

template <typename ALLOC>
void CreatorAdapter<ALLOC>::endObject()
{
    if (m_objectValueAdapter)
    {
        setValue(m_objectValueAdapter->get());
        m_objectValueAdapter.reset();
    }
    else
    {
        if (!m_creator)
            throw CppRuntimeException("JsonReader: Adapter not initialized!");

        if (m_keyStack.empty())
        {
            m_object = m_creator->endRoot();
            m_creator.reset();
        }
        else
        {
            if (!m_keyStack.back().empty())
            {
                m_creator->endCompound();
                m_keyStack.pop_back();
            }
            else
            {
                m_creator->endCompoundElement();
            }
        }
    }
}

template <typename ALLOC>
void CreatorAdapter<ALLOC>::beginArray()
{
    if (m_objectValueAdapter)
    {
        m_objectValueAdapter->beginArray();
    }
    else
    {
        if (!m_creator)
            throw CppRuntimeException("JsonReader: Adapter not initialized!");

        if (m_keyStack.empty())
            throw CppRuntimeException("JsonReader: ZserioTreeCreator expects json object!");

        m_creator->beginArray(m_keyStack.back());

        m_keyStack.push_back("");
    }
}

template <typename ALLOC>
void CreatorAdapter<ALLOC>::endArray()
{
    if (m_objectValueAdapter)
    {
        m_objectValueAdapter->endArray();
    }
    else
    {
        if (!m_creator)
            throw CppRuntimeException("JsonReader: Adapter not initialized!");

        m_creator->endArray();

        m_keyStack.pop_back(); // finish array
        m_keyStack.pop_back(); // finish field
    }
}

template <typename ALLOC>
void CreatorAdapter<ALLOC>::visitKey(StringView key)
{
    if (m_objectValueAdapter)
    {
        m_objectValueAdapter->visitKey(key);
    }
    else
    {
        if (!m_creator)
            throw CppRuntimeException("JsonReader: Adapter not initialized!");

        m_keyStack.push_back(toString(key, get_allocator()));
    }
}

template <typename ALLOC>
void CreatorAdapter<ALLOC>::visitValue(std::nullptr_t nullValue)
{
    if (m_objectValueAdapter)
    {
        m_objectValueAdapter->visitValue(nullValue);
    }
    else
    {
        if (!m_creator)
            throw CppRuntimeException("JsonReader: Adapter not initialized!");

        setValue(nullValue);
    }
}

template <typename ALLOC>
void CreatorAdapter<ALLOC>::visitValue(bool boolValue)
{
    if (m_objectValueAdapter)
    {
        m_objectValueAdapter->visitValue(boolValue);
    }
    else
    {
        if (!m_creator)
            throw CppRuntimeException("JsonReader: Adapter not initialized!");

        setValue(boolValue);
    }
}

template <typename ALLOC>
void CreatorAdapter<ALLOC>::visitValue(int64_t intValue)
{
    if (m_objectValueAdapter)
    {
        m_objectValueAdapter->visitValue(intValue);
    }
    else
    {
        if (!m_creator)
            throw CppRuntimeException("JsonReader: Adapter not initialized!");

        setValue(intValue);
    }
}

template <typename ALLOC>
void CreatorAdapter<ALLOC>::visitValue(uint64_t uintValue)
{
    if (m_objectValueAdapter)
    {
        m_objectValueAdapter->visitValue(uintValue);
    }
    else
    {
        if (!m_creator)
            throw CppRuntimeException("JsonReader: Adapter not initialized!");

        setValue(uintValue);
    }
}

template <typename ALLOC>
void CreatorAdapter<ALLOC>::visitValue(double doubleValue)
{
    if (m_objectValueAdapter)
    {
        m_objectValueAdapter->visitValue(doubleValue);
    }
    else
    {
        if (!m_creator)
            throw CppRuntimeException("JsonReader: Adapter not initialized!");

        setValue(doubleValue);
    }
}

template <typename ALLOC>
void CreatorAdapter<ALLOC>::visitValue(StringView stringValue)
{
    if (m_objectValueAdapter)
    {
        m_objectValueAdapter->visitValue(stringValue);
    }
    else
    {
        if (!m_creator)
            throw CppRuntimeException("JsonReader: Adapter not initialized!");

        setValue(stringValue);
    }
}

template <typename ALLOC>
template <typename T>
void CreatorAdapter<ALLOC>::setValue(T&& value)
{
    if (m_keyStack.empty())
        throw CppRuntimeException("JsonReader: ZserioTreeCreator expects json object!");

    if (!m_keyStack.back().empty())
    {
        m_creator->setValue(m_keyStack.back(), std::forward<T>(value));
        m_keyStack.pop_back();
    }
    else
    {
        m_creator->addValueElement(std::forward<T>(value));
    }
}

} // namespace detail

} // namespace zserio

#endif // ZSERIO_JSON_READER_H_INC
