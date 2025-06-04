#ifndef TEST_UTILS_HASH_TEST_H_INC
#define TEST_UTILS_HASH_TEST_H_INC

#include <memory>

#include "gtest/gtest.h"

namespace test_utils
{

template <typename T>
void hashTest(const T& value, size_t hashValue, const T& equalValue)
{
    EXPECT_EQ(hashValue, value.hashCode());
    EXPECT_EQ(hashValue, equalValue.hashCode());
}

template <typename T>
void hashTest(const T& value, size_t hashValue, const T& equalValue, const T& diffValue, size_t diffHashValue)
{
    hashTest(value, hashValue, equalValue);
    EXPECT_NE(hashValue, diffHashValue);
    EXPECT_EQ(diffHashValue, diffValue.hashCode());
}

} // namespace test_utils

#endif // TEST_UTILS_HASH_TEST_H_INC
