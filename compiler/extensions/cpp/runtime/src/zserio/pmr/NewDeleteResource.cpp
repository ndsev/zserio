#include "zserio/pmr/NewDeleteResource.h"

namespace zserio
{
namespace pmr
{

MemoryResource* getNewDeleteResource() noexcept
{
    static detail::NewDeleteResource newDeleteResource;
    return &newDeleteResource;
}

} // namespace pmr
} // namespace zserio
