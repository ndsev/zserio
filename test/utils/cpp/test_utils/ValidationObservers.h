#ifndef TEST_UTILS_VALIDATION_OBSERVERS_H_INC
#define TEST_UTILS_VALIDATION_OBSERVERS_H_INC

#include <algorithm>
#include <sstream>

#include "zserio/IValidationObserver.h"

namespace test_utils
{

class CountingValidationObserver : public zserio::IValidationObserver
{
public:
    virtual void beginDatabase(size_t numberOfTables) override
    {
        m_numberOfTables += numberOfTables;
    }

    virtual void endDatabase(size_t numberOfValidatedTables) override
    {
        m_numberOfValidatedTables += numberOfValidatedTables;
    }

    virtual bool beginTable(zserio::StringView, size_t numberOfRows) override
    {
        m_numberOfRows += numberOfRows;
        return true;
    }

    virtual bool endTable(zserio::StringView, size_t numberOfValidatedRows) override
    {
        m_numberOfValidatedRows += numberOfValidatedRows;
        return true;
    }

    virtual bool reportError(zserio::StringView, zserio::StringView,
            zserio::Span<const zserio::StringView>, ErrorType,
            zserio::StringView) override
    {
        m_numberOfErrors++;
        return true;
    }

    size_t getNumberOfTables() const { return m_numberOfTables; }
    size_t getNumberOfValidatedTables() const { return m_numberOfValidatedTables; }
    size_t getNumberOfRows() const { return m_numberOfRows; }
    size_t getNumberOfValidatedRows() const { return m_numberOfValidatedRows; }
    size_t getNumberOfErrors() const { return m_numberOfErrors; }

private:
    size_t m_numberOfTables = 0;
    size_t m_numberOfValidatedTables = 0;
    size_t m_numberOfRows = 0;
    size_t m_numberOfValidatedRows = 0;
    size_t m_numberOfErrors = 0;
};

class ValidationObserver : public zserio::IValidationObserver
{
public:
    using RowCounts = std::tuple<size_t, size_t>;

    struct Error
    {
        Error(zserio::StringView tableNameView, zserio::StringView fieldNameView,
                zserio::Span<const zserio::StringView> primaryKeyValuesSpan, ErrorType error,
                zserio::StringView messageView) :
                tableName(zserio::stringViewToString(tableNameView)),
                fieldName(zserio::stringViewToString(fieldNameView)),
                errorType(error),
                message(zserio::stringViewToString(messageView))
        {
            std::transform(primaryKeyValuesSpan.begin(), primaryKeyValuesSpan.end(),
                    std::back_inserter(this->primaryKeyValues),
                    [](zserio::StringView msg) -> std::string {
                        return ::zserio::stringViewToString(msg);
                    });
        }

        std::string tableName;
        std::string fieldName;
        std::vector<std::string> primaryKeyValues;
        ErrorType errorType;
        std::string message;
    };

    virtual void beginDatabase(size_t numberOfTables) override
    {
        m_numberOfTables = numberOfTables;
    }

    virtual void endDatabase(size_t numberOfValidatedTables) override
    {
        m_numberOfValidatedTables = numberOfValidatedTables;
    }

    virtual bool beginTable(zserio::StringView tableName, size_t numberOfRows) override
    {
        m_rowCountsMap[zserio::stringViewToString(tableName)] =
                std::make_tuple(numberOfRows, static_cast<size_t>(0));
        return true;
    }

    virtual bool endTable(zserio::StringView tableName, size_t numberOfValidatedRows) override
    {
        std::get<1>(m_rowCountsMap[zserio::stringViewToString(tableName)]) = numberOfValidatedRows;
        return true;
    }

    virtual bool reportError(zserio::StringView tableName, zserio::StringView fieldName,
            zserio::Span<const zserio::StringView> primaryKeyValues, ErrorType errorType,
            zserio::StringView message) override
    {
        m_errors.emplace_back(tableName, fieldName, primaryKeyValues, errorType, message);
        return true;
    }

    size_t getNumberOfTables() const
    {
        return m_numberOfTables;
    }

    size_t getNumberOfValidatedTables() const
    {
        return m_numberOfValidatedTables;
    }

    size_t getNumberOfTableRows(zserio::StringView tableName) const
    {
        return std::get<0>(getTableRowCounts(tableName));
    }

    size_t getNumberOfValidatedTableRows(zserio::StringView tableName) const
    {
        return std::get<1>(getTableRowCounts(tableName));
    }

    const std::vector<Error>& getErrors() const
    {
        return m_errors;
    }

    std::string getErrorsString() const
    {
        std::ostringstream out;
        for (const auto& error : m_errors)
        {
            out << error.tableName << ", " << error.fieldName << ", [";
            for (auto keyValueIt = error.primaryKeyValues.begin(); keyValueIt != error.primaryKeyValues.end();
                    ++keyValueIt)
            {
                out << (keyValueIt != error.primaryKeyValues.begin() ? ", " : "") << *keyValueIt;
            }
            out << "], " << error.errorType << ", " << error.message << "\n";
        }

        return out.str();
    }

private:
    RowCounts getTableRowCounts(zserio::StringView tableName) const
    {
        auto search = m_rowCountsMap.find(zserio::stringViewToString(tableName));

        return (search != m_rowCountsMap.end()) ? search->second : std::make_tuple<size_t, size_t>(0, 0);
    }

    size_t m_numberOfTables = 0;
    size_t m_numberOfValidatedTables = 0;
    std::map<std::string, RowCounts> m_rowCountsMap;
    std::vector<Error> m_errors;
};

} // namespace test_utils

#endif // TEST_UTILS_VALIDATION_OBSERVERS_H_INC
