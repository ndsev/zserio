#include "zserio/pmr/MemoryResource.h"

#include "zserio/pmr/NewDeleteResource.h"

#include <algorithm>

namespace zserio
{
namespace pmr
{

namespace
{

MemoryResource*& getCurrentDefaultResource()
{
    // static variable is in the function to support proper early initialization (before main)
    static MemoryResource* defaultResource = getNewDeleteResource();
    return defaultResource;
}

} // namespace

MemoryResource* getDefaultResource() noexcept
{
    return getCurrentDefaultResource();
}

MemoryResource* setDefaultResource(MemoryResource* resource) noexcept
{
    MemoryResource* defaultResource = (resource != nullptr) ? resource : getNewDeleteResource();
    std::swap(defaultResource, getCurrentDefaultResource());
    return defaultResource;
}

} // namespace pmr
} // namespace zserio
