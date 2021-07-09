#include "gtest/gtest.h"

#include "zserio/BlobBuffer.h"
#include "TrackingAllocator.h"

namespace zserio
{

TEST(BlobBufferTest, Constructor)
{
	TrackingAllocatorImpl<uint8_t> alloc;

	::zserio::BlobBuffer<TrackingAllocatorImpl<uint8_t>> buffer(alloc);
	ASSERT_EQ(alloc, buffer.get_allocator());
}

TEST(BlobBufferTest, ResizeData)
{
	TrackingAllocatorImpl<uint8_t> alloc;

	::zserio::BlobBuffer<TrackingAllocatorImpl<uint8_t>> buffer(alloc);
	buffer.resize(1000);

	ASSERT_EQ(1000, buffer.data().size());
	ASSERT_NE(nullptr, buffer.data().data());
	ASSERT_LE(1u, alloc.numAllocs());
}

} // namespace zserio
