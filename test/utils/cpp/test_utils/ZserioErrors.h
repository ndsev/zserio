#ifndef TEST_UTILS_ZSERIO_ERRORS_H_INC
#define TEST_UTILS_ZSERIO_ERRORS_H_INC

#include <fstream>
#include <algorithm>

namespace test_utils
{

class ZserioErrors
{
public:
    ZserioErrors(const std::string& prefix)
    {
        const std::string errorsFileName = prefix + "/" + ZSERIO_ERRORS_FILE_NAME;
        std::ifstream errorsFile(errorsFileName);
        if (!errorsFile)
            throw std::runtime_error("Failed to open zserio errors file!");

        std::string line;
        while (std::getline(errorsFile, line))
            m_lines.push_back(std::move(line));
    }

    // checks if given error is present
    bool isPresent(const std::string& error) const
    {
        return std::any_of(m_lines.begin(), m_lines.end(), [&error](const std::string& line) {
            return line.find(error) != std::string::npos;
        });
    }

    // checks if given errors are present in the given order
    bool isPresent(const std::vector<std::string>& errors) const
    {
        for (size_t i = 0, j = 0; i < m_lines.size(); ++i)
        {
            const std::string& error = errors[j];
            if (m_lines[i].find(error) != std::string::npos && ++j == errors.size())
                return true;
        }

        return false;
    }

private:
    std::vector<std::string> m_lines;
    static constexpr const char* ZSERIO_ERRORS_FILE_NAME = "zserio_log.txt";
};

} // namespace test_utils

#endif // TEST_UTILS_ZSERIO_ERRORS_H_INC
