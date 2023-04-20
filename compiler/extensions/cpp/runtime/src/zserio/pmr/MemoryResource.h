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

/**
 * Abstract base class for memory resources which are to be used by polymorphic allocators defined by zserio.
 */
class MemoryResource
{
public:
    // this empty constructor is necessary for gcc 9.3.0 bug which causes test coverage failure
    /**
     * Constructor.
     */
    MemoryResource() = default;

    /**
     * Destructor.
     */
    virtual ~MemoryResource() = default;

    /**
     * Copying and moving is disallowed!
     * \{
     */
    MemoryResource(const MemoryResource& other) = delete;
    MemoryResource(MemoryResource&& other) = delete;

    MemoryResource& operator=(const MemoryResource& other) = delete;
    MemoryResource& operator=(MemoryResource&& other) = delete;
    /** \} */

    /**
     * Allocates storage with a size of at least bytes bytes, aligned to the specified alignment.
     *
     * \param bytes Minimum number of bytes to allocate.
     * \param alignment Requested alignment.
     *
     * \return Pointer to the allocated storage.
     */
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

    /**
     * Deallocates the storage pointed to by p.
     *
     * Arguments shall match to prior call to allocate!
     *
     * \param p Pointer to the storage to deallocate.
     * \param bytes Number of bytes to deallocate.
     * \param alignment Requested alignment.
     */
    void deallocate(void* p, size_t bytes, size_t alignment = alignof(max_align_t))
    {
#ifdef ZSERIO_MEMORY_RESOURCE_TRACING
        std::cout << "MemoryResource::deallocate p=" << std::hex << p << std::dec << " bytes=" << bytes <<
                " alignment=" << alignment << std::endl;
#endif
        doDeallocate(p, bytes, alignment);
    }

    /**
     * Compares *this for equality with other.
     *
     * Two memory resources  compare equal if and only if memory allocated from one memory resource
     * can be deallocated from the other and vice versa.
     *
     * \param other Other memory resource to compare.
     *
     * \return True when the two resources are equal, false otherwise.
     */
    bool isEqual(const MemoryResource& other) const noexcept
    {
#ifdef ZSERIO_MEMORY_RESOURCE_TRACING
        std::cout << "MemoryResource::isEqual other=" << std::hex << &other << std::dec << std::endl;
#endif
        return doIsEqual(other);
    }

private:
    /**
     * Allocates storage with a size of at least bytes bytes, aligned to the specified alignment.
     *
     * \param bytes Minimum number of bytes to allocate.
     * \param alignment Requested alignment.
     *
     * \return Pointer to the allocated storage.
     */
    virtual void* doAllocate(size_t bytes, size_t alignment) = 0;

    /**
     * Deallocates the storage pointed to by p.
     *
     * Arguments shall match to prior call to doAllocate!
     *
     * \param p Pointer to the storage to deallocate.
     * \param bytes Number of bytes to deallocate.
     * \param alignment Requested alignment.
     */
    virtual void doDeallocate(void* p, size_t bytes, size_t alignment) = 0;

    /**
     * Compares *this for equality with other.
     *
     * Two memory resources  compare equal if and only if memory allocated from one memory resource
     * can be deallocated from the other and vice versa.
     *
     * \param other Other memory resource to compare.
     *
     * \return True when the two resources are equal, false otherwise.
     */
    virtual bool doIsEqual(const MemoryResource& other) const noexcept = 0;
};

/**
 * Compares the memory resources lhs and rhs for equality.
 *
 * \param lhs First memory resource to compare.
 * \param rhs Second memory resource to compare.
 *
 * \return True when the two resources are equal, false otherwise.
 */
inline bool operator==(const MemoryResource& lhs, const MemoryResource& rhs)
{
    return &lhs == &rhs || lhs.isEqual(rhs);
}

/**
 * Compares the memory resources lhs and rhs for non-equality.
 *
 * \param lhs First memory resource to compare.
 * \param rhs Second memory resource to compare.
 *
 * \return True when the two resources are NOT equal, false otherwise.
 */
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
 *
 * \return Previous default resource.
 */
MemoryResource* setDefaultResource(MemoryResource* resource) noexcept;

} // namespace pmr
} // namespace zserio

#endif // ZSERIO_PMR_I_MEMORY_RESOURCE_H_INC
