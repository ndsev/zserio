#include "gtest/gtest.h"

#include "zserio/pmr/NewDeleteResource.h"

namespace zserio
{

TEST(NewDeleteResourceTest, isEqual)
{
    // HACK: take a "NewDeleteResource" type from the detail namespace
    // Note: this actually does not test anything useful, it is only to
    // have proper coverage
    zserio::pmr::detail::NewDeleteResource res1, res2;
    ASSERT_TRUE(res1.isEqual(res1));
    ASSERT_FALSE(res1.isEqual(res2));
}

} // namespace zserio
