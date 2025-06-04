#ifndef TEST_UTILS_COMPARISON_OPERATORS_TEST_H_INC
#define TEST_UTILS_COMPARISON_OPERATORS_TEST_H_INC

#include "gtest/gtest.h"

namespace test_utils
{

namespace detail
{

template <typename T>
void operatorEqualToTest(const T& value, const T& equalValue)
{
    ASSERT_TRUE(value == equalValue);
}

template <typename T>
void operatorEqualToTest(const T& value, const T& equalValue, const T& lessThanValue)
{
    operatorEqualToTest(value, equalValue);
    ASSERT_FALSE(value == lessThanValue);
    ASSERT_FALSE(equalValue == lessThanValue);
}

template <typename T>
void operatorLessThanTest(const T& value, const T& equalValue)
{
    ASSERT_FALSE(value < equalValue);
}

template <typename T>
void operatorLessThanTest(const T& value, const T& equalValue, const T& lessThanValue)
{
    operatorLessThanTest(value, equalValue);
    ASSERT_TRUE(lessThanValue < value);
    ASSERT_FALSE(equalValue < lessThanValue);
}

} // namespace detail

template <typename T>
void comparisonOperatorsTest(const T& value, const T& equalValue)
{
    detail::operatorEqualToTest(value, equalValue);
    detail::operatorLessThanTest(value, equalValue);
}

template <typename T>
void comparisonOperatorsTest(const T& value, const T& equalValue, const T& lessThanValue)
{
    detail::operatorEqualToTest(value, equalValue, lessThanValue);
    detail::operatorLessThanTest(value, equalValue, lessThanValue);
}

} // namespace test_utils

#endif // TEST_UTILS_COMPARISON_OPERATORS_TEST_H_INC
