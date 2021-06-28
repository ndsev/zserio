#ifndef ZSERIO_I_VALIDATION_OBSERVER_H_INC
#define ZSERIO_I_VALIDATION_OBSERVER_H_INC

#include "zserio/Types.h"
#include "zserio/StringView.h"
#include "zserio/Span.h"

namespace zserio
{

/**
 * Observer interface for validation.
 */
class IValidationObserver
{
public:
    virtual ~IValidationObserver() = default;

    /**
     * Called before the database is validated.
     *
     * \param numberOfTables Number of tables in the database.
     */
    virtual void beginDatabase(size_t numberOfTables) = 0;

    /**
     * Called after the database is validated.
     *
     * \param numberOfValidatedTables Number of tables which were validated.
     */
    virtual void endDatabase(size_t numberOfValidatedTables) = 0;

    /**
     * Called just before the table with tableName is validated.
     *
     * \param tableName Name of the table which is going to be validated.
     * \param numberOfRows Number of rows in the table.
     *
     * \return True to validate the table, false to skip the table.
     */
    virtual bool beginTable(StringView tableName, size_t numberOfRows) = 0;

    /**
     * Called after the table with tableName validation is finished.
     *
     * \param tableName Name of to the validated table.
     * \param numberOfValidatedRows Number of validated rows.
     *
     * \return True to continue validation, false to stop the whole validation.
     */
    virtual bool endTable(StringView tableName, size_t numberOfValidatedRows) = 0;

    /**
     * Defines types of validation errors.
     */
    enum ErrorType
    {
        /**
         * The column type in SQL table is different from definition in zserio.
         */
        INVALID_COLUMN_TYPE,

        /**
         * The column constraint in SQL table is different from definition in zserio.
         */
        INVALID_COLUMN_CONSTRAINT,

        /**
         * The column is defined in zserio but it is not in SQL table.
         */
        COLUMN_MISSING,

        /**
         * The column is not defined in zserio but it is in SQL table.
         */
        COLUMN_SUPERFLUOUS,

        /**
         * Value in SQL table is out of range according to the definition in zserio.
         */
        VALUE_OUT_OF_RANGE,

        /**
         * Value in SQL table is invalid according to the definition in zserio.
         */
        INVALID_VALUE,

        /**
         * Parsing of read blob from SQL table failed.
         */
        BLOB_PARSE_FAILED,

        /**
         * Comparing of read blob from SQL table to parsed blob written in bit stream failed.
         */
        BLOB_COMPARE_FAILED
    };

    /**
     * Called when an error is detected.
     *
     * \param tableName Name of the table where the validation error occurred.
     * \param fieldName Name of the column where the validation error occurred.
     * \param primaryKeyValues
     *                  Values of the primary key to identify the row where the validation error occurred.
     *                  \note When the table has no primary key, rowid is used.
     *
     * \return True to continue validation, false to skip validation of the rest of the current table.
     */
    virtual bool reportError(StringView tableName, StringView fieldName,
            Span<const StringView> primaryKeyValues, ErrorType errorType, StringView message) = 0;
};

} // namespace zserio

#endif // ZSERIO_I_VALIDATION_OBSERVER_H_INC
