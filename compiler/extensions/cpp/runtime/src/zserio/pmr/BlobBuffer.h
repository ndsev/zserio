#ifndef ZSERIO_PMR_BLOB_BUFFER_H_INC
#define ZSERIO_PMR_BLOB_BUFFER_H_INC

#include "zserio/BlobBuffer.h"
#include "zserio/pmr/PolymorphicAllocator.h"

namespace zserio
{
namespace pmr
{

using BlobBuffer = zserio::BlobBuffer<PropagatingPolymorphicAllocator<uint8_t>>;

} // namespace pmr
} // namespace zserio

#endif // ZSERIO_PMR_BLOB_BUFFER_H_INC
