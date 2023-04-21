#ifndef TEST_UTILS_MEMORY_RESOURCES_H_INC
#define TEST_UTILS_MEMORY_RESOURCES_H_INC

#include <array>

#include "zserio/Types.h"
#include "zserio/pmr/MemoryResource.h"
#include "zserio/CppRuntimeException.h"

namespace test_utils
{

class InvalidMemoryResource : public zserio::pmr::MemoryResource
{
private:
    void* doAllocate(size_t bytes, size_t align) override
    {
        throw zserio::CppRuntimeException("Trying to allocate using default memory resource (") << bytes <<
                ", " << align << ")!";
    }

    void doDeallocate(void*, size_t bytes, size_t align) override
    {
        throw zserio::CppRuntimeException("Trying to deallocate using default memory resource (") << bytes <<
                ", " << align << ")!";
    }

    bool doIsEqual(const MemoryResource& other) const noexcept override
    {
        return this == &other;
    }
};

class HeapMemoryResource : public zserio::pmr::MemoryResource
{
public:
    void* doAllocate(size_t bytes, size_t) override
    {
        ++m_numAllocations;
        m_allocatedSize += bytes;

        return ::operator new(bytes);
    }

    void doDeallocate(void* p, size_t bytes, size_t) override
    {
        ++m_numDeallocations;
        m_allocatedSize -= bytes;

        ::operator delete(p);
    }

    bool doIsEqual(const MemoryResource& other) const noexcept override
    {
        return this == &other;
    }

    size_t getNumAllocations() const noexcept
    {
        return m_numAllocations;
    }

    size_t getNumDeallocations() const noexcept
    {
        return m_numDeallocations;
    }

    size_t getAllocatedSize() const noexcept
    {
        return m_allocatedSize;
    }

private:
    size_t m_numAllocations = 0;
    size_t m_numDeallocations = 0;
    size_t m_allocatedSize = 0;
};

template <size_t BUFFER_SIZE = 1024 * 2>
class TestMemoryResource : public zserio::pmr::MemoryResource
{
public:
    explicit TestMemoryResource(const char* name) : m_name(name)
    {
        m_buffer.fill(0);
        m_nextPtr = m_buffer.begin();
    }

    void* doAllocate(size_t bytes, size_t align) override
    {
        const size_t alignMod = static_cast<size_t>(m_nextPtr - m_buffer.begin()) % align;
        if (alignMod != 0)
            m_nextPtr += align - alignMod;

        void* const ptr = &(*m_nextPtr);
        m_nextPtr += bytes;

        const size_t usedBytes = static_cast<size_t>(m_nextPtr - m_buffer.begin());
        if (usedBytes > BUFFER_SIZE)
            throw zserio::CppRuntimeException(m_name) << ": Buffer overflow (" << usedBytes << ")!";

        ++m_numAllocations;
        m_allocatedSize += bytes;

        return ptr;
    }

    void doDeallocate(void*, size_t bytes, size_t) override
    {
        ++m_numDeallocations;
        m_allocatedSize -= bytes;
    }

    bool doIsEqual(const MemoryResource& other) const noexcept override
    {
        return this == &other;
    }

    size_t getNumAllocations() const noexcept
    {
        return m_numAllocations;
    }

    size_t getNumDeallocations() const noexcept
    {
        return m_numDeallocations;
    }

    size_t getAllocatedSize() const noexcept
    {
        return m_allocatedSize;
    }

private:
    const char* m_name;

    std::array<uint8_t, BUFFER_SIZE> m_buffer;
    typename std::array<uint8_t, BUFFER_SIZE>::iterator m_nextPtr;
    size_t m_numAllocations = 0;
    size_t m_numDeallocations = 0;
    size_t m_allocatedSize = 0;
};

class MemoryResourceScopedSetter
{
public:
    explicit MemoryResourceScopedSetter(zserio::pmr::MemoryResource& memoryResource)
    :   m_origMemoryResource(zserio::pmr::setDefaultResource(&memoryResource))
    {}

    ~MemoryResourceScopedSetter()
    {
        zserio::pmr::setDefaultResource(m_origMemoryResource);
    }

    MemoryResourceScopedSetter(const MemoryResourceScopedSetter&) = default;
    MemoryResourceScopedSetter& operator=(const MemoryResourceScopedSetter&) = default;

    MemoryResourceScopedSetter(MemoryResourceScopedSetter&&) = default;
    MemoryResourceScopedSetter& operator=(MemoryResourceScopedSetter&&) = default;

private:
    zserio::pmr::MemoryResource* m_origMemoryResource;
};

} // namespace test_utils

#endif // TEST_UTILS_MEMORY_RESOURCES_H_INC
