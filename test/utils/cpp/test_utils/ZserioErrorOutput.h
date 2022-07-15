#ifndef TEST_UTILS_ZSERIO_ERRORS_H_INC
#define TEST_UTILS_ZSERIO_ERRORS_H_INC

#include <fstream>
#include <algorithm>
#include <stdexcept>

namespace test_utils
{

class ZserioErrorOutput
{
public:
    ZserioErrorOutput(const std::string& prefix, const std::string& fileName = ZSERIO_ERROR_OUTPUT_FILE_NAME)
    {
        const std::string errorsFileName = prefix + "/" + fileName;
        std::ifstream errorsFile(errorsFileName);
        if (!errorsFile)
            throw std::runtime_error("Failed to open zserio errors file!");

        std::string line;
        while (std::getline(errorsFile, line))
            m_lines.push_back(std::move(line));
    }

    // checks if given message is present
    bool isPresent(const std::string& message) const
    {
        return std::any_of(m_lines.begin(), m_lines.end(), [&message](const std::string& line) {
            return line.find(message) != std::string::npos;
        });
    }

    // checks if given messages are present in the given order
    bool isPresent(const std::vector<std::string>& messages) const
    {
        for (size_t i = 0, j = 0; i < m_lines.size(); ++i)
        {
            const std::string& error = messages[j];
            if (m_lines[i].find(error) != std::string::npos && ++j == messages.size())
                return true;
        }

        return false;
    }

private:
    std::vector<std::string> m_lines;
    static constexpr const char* ZSERIO_ERROR_OUTPUT_FILE_NAME = "zserio_log.txt";
};

} // namespace test_utils

#endif // TEST_UTILS_ZSERIO_ERRORS_H_INC
