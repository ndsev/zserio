#ifndef ZSERIO_PMR_NEW_DELETE_RESOURCE_H_INC
#define ZSERIO_PMR_NEW_DELETE_RESOURCE_H_INC

#include <new>
#include "zserio/pmr/MemoryResource.h"

namespace zserio
{
namespace pmr
{
namespace detail
{

/**
 * Default memory resource which will be used by polymorphic allocators defined by zserio.
 */
class NewDeleteResource : public MemoryResource
{
private:
    void* doAllocate(size_t bytes, size_t ) override
    {
        return ::operator new(bytes);
    }

    void doDeallocate(void* p, size_t, size_t) override
    {
        ::operator delete(p);
    }

    bool doIsEqual(const MemoryResource& other) const noexcept override
    {
        return this == &other;
    }
};

} // namespace detail

/**
 * Gets pointer to (default) new delete resource.
 */
MemoryResource* getNewDeleteResource() noexcept;

} // namespace pmr
} // namespace zserio

#endif // ZSERIO_PMR_NEW_DELETE_RESOURCE_H_INC
