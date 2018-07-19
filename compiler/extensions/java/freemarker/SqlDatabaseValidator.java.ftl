<#include "FileHeader.inc.ftl">
<#include "GeneratePkgPrefix.inc.ftl">
<@standard_header generatorDescription, rootPackageName, javaMajorVersion, [
        "java.sql.SQLException",
        "zserio.runtime.SqlDatabaseWriter"
        "zserio.runtime.validation.ValidationReport"
]/>

<@class_header generatorDescription/>
public interface SqlDatabaseValidator
{
    /**
     * Validates all tables in this database.
     *
     * @return Validation report filled by log information and validation errors.
     *
     * @throws SQLException Thrown by the underlying DB in case of error.
     */
    ValidationReport validate(<@generate_pkg_prefix rootPackageName/>IParameterProvider parameterProvider) throws SQLException;
}
