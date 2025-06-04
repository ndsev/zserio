#ifndef TEST_UTILS_WRITE_READ_FILE_TEST_H_INC
#define TEST_UTILS_WRITE_READ_FILE_TEST_H_INC

#include <string>
#include <utility>

#include "gtest/gtest.h"
#include "zserio/SerializeUtil.h"

namespace test_utils
{

template <typename T, typename... ARGS>
void writeReadFileTest(const std::string& fileName, T& data, ARGS&&... arguments)
{
    zserio::serializeToFile(data, fileName);

    T readData = zserio::deserializeFromFile<T>(fileName, ::std::forward<ARGS>(arguments)...);
    ASSERT_EQ(readData, data);
}

} // namespace test_utils

#endif // TEST_UTILS_WRITE_READ_FILE_TEST_H_INC
