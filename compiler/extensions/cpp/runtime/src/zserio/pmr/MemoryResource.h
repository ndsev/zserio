#ifndef ZSERIO_PMR_I_MEMORY_RESOURCE_H_INC
#define ZSERIO_PMR_I_MEMORY_RESOURCE_H_INC

#include <cstddef>
#ifdef ZSERIO_MEMORY_RESOURCE_TRACING
#   include <iostream>
#endif

namespace zserio
{
namespace pmr
{

class MemoryResource
{
public:
    // this empty constructor is necessary for gcc 9.3.0 bug which causes test coverage failure
    MemoryResource() {}
    virtual ~MemoryResource() = default;

    void* allocate(size_t bytes, size_t alignment = alignof(max_align_t))
    {
#ifdef ZSERIO_MEMORY_RESOURCE_TRACING
        void* const ptr = doAllocate(bytes, alignment);
        std::cout << "MemoryResource::allocate bytes=" << bytes << " alignment=" << alignment << " -> " <<
                std::hex << ptr << std::dec << std::endl;
        return ptr;
#else
        return doAllocate(bytes, alignment);
#endif
    }

    void deallocate(void* p, size_t bytes, size_t alignment = alignof(max_align_t))
    {
#ifdef ZSERIO_MEMORY_RESOURCE_TRACING
        std::cout << "MemoryResource::deallocate p=" << std::hex << p << std::dec << " bytes=" << bytes <<
                " alignment=" << alignment << std::endl;
#endif
        doDeallocate(p, bytes, alignment);
    }

    bool isEqual(const MemoryResource& other) const noexcept
    {
#ifdef ZSERIO_MEMORY_RESOURCE_TRACING
        std::cout << "MemoryResource::isEqual other=" << std::hex << &other << std::dec << std::endl;
#endif
        return doIsEqual(other);
    }

private:
    virtual void* doAllocate(size_t bytes, size_t alignment) = 0;
    virtual void doDeallocate(void* p, size_t bytes, size_t alignment) = 0;
    virtual bool doIsEqual(const MemoryResource& other) const noexcept = 0;
};

inline bool operator==(const MemoryResource& lhs, const MemoryResource& rhs)
{
    return &lhs == &rhs || lhs.isEqual(rhs);
}

inline bool operator!=(const MemoryResource& lhs, const MemoryResource& rhs)
{
    return !(lhs == rhs);
}

/**
 * Returns default memory resource, which is the resource set by previous call to
 * setDefaultResource, or zserio::pmr::NewDeleteResource if no call to setDefaultResource
 * was done.
 *
 * \warning This function does not synchronize with setDefaultResource.
 *
 * \return Default memory resource.
 */
MemoryResource* getDefaultResource() noexcept;

/**
 * If resource is not null, sets the default memory resource pointer to resource,
 * otherwise, sets the default memory resource pointer to zserio::pmr::NewDeleteResource.
 * All subsequent calls to getDefaultResource() returns resource set by this function.
 *
 * \warning This function does not synchronize with getDefaultResource or other calls to setDefaultResource.
 * It is undefined behavior to call setDefaultResource together with getDefaultResource concurrently.
 *
 * \param resource Resource to be set as default, or nullptr to use zserio::pmr::NewDeleteResource.
 * \return Previous default resource.
 */
MemoryResource* setDefaultResource(MemoryResource* resource) noexcept;

} // namespace pmr
} // namespace zserio

#endif // ZSERIO_PMR_I_MEMORY_RESOURCE_H_INC
