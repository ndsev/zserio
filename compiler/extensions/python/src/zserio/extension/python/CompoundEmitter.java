package zserio.extension.python;

import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import zserio.ast.CompoundType;
import zserio.ast.SqlDatabaseType;
import zserio.ast.UnionType;
import zserio.extension.common.ZserioExtensionException;
import zserio.extension.python.CompoundParameterTemplateData.CompoundParameter;
import zserio.tools.ZserioToolPrinter;

/**
 * Base class for compound emitters, which provides checking for property names clashing with other generated
 * methods when -withPythonProperties is used.
 */
public class CompoundEmitter extends PythonDefaultEmitter
{
    public CompoundEmitter(PythonExtensionParameters pythonParameters)
    {
        super(pythonParameters);
    }

    protected void processCompoundTemplate(String templateName, CompoundTypeTemplateData templateData,
            CompoundType compoundType) throws ZserioExtensionException
    {
        if (getWithPythonProperties())
            checkPropertyNames(templateName, templateData, compoundType);
        processSourceTemplate(templateName, templateData, compoundType);
    }

    protected void processCompoundTemplate(String templateName, UnionEmitterTemplateData templateData,
            UnionType unionType) throws ZserioExtensionException
    {
        if (getWithPythonProperties())
            checkPropertyNames(templateName, templateData, unionType);
        processSourceTemplate(templateName, templateData, unionType);
    }

    protected void processCompoundTemplate(String templateName, SqlDatabaseEmitterTemplateData templateData,
            SqlDatabaseType sqlDatabaseType) throws ZserioExtensionException
    {
        if (getWithPythonProperties())
            checkPropertyNames(templateName, templateData, sqlDatabaseType);
        processSourceTemplate(templateName, templateData, sqlDatabaseType);
    }

    private void checkPropertyNames(String templateName, CompoundTypeTemplateData templateData,
            CompoundType compoundType) throws ZserioExtensionException
    {
        // we must check properties names to prevent clashing with public symbols in generated API
        final Set<String> apiSymbols = getTemplateApiMethods(templateName);
        apiSymbols.addAll(getGeneratedApiSymbols(templateData));

        for (CompoundFieldTemplateData fieldData : templateData.getFieldList())
            checkPropertyName(fieldData.getPropertyName(), apiSymbols, compoundType);

        for (CompoundParameter paramData : templateData.getCompoundParametersData().getList())
            checkPropertyName(paramData.getPropertyName(), apiSymbols, compoundType);
    }

    private void checkPropertyNames(String templateName, UnionEmitterTemplateData templateData,
            UnionType unionType) throws ZserioExtensionException
    {
        // we must check properties names to prevent clashing with public symbols in generated API
        final Set<String> apiSymbols = getTemplateApiMethods(templateName);
        apiSymbols.addAll(getGeneratedApiSymbols(templateData));

        for (CompoundFieldTemplateData fieldData : templateData.getFieldList())
            checkPropertyName(fieldData.getPropertyName(), apiSymbols, unionType);

        for (CompoundParameter paramData : templateData.getCompoundParametersData().getList())
            checkPropertyName(paramData.getPropertyName(), apiSymbols, unionType);
    }

    private void checkPropertyNames(String templateName, SqlDatabaseEmitterTemplateData templateData,
            CompoundType compoundType) throws ZserioExtensionException
    {
        // we must check properties names to prevent clashing with public symbols in generated API
        final Set<String> apiSymbols = getTemplateApiMethods(templateName);
        apiSymbols.addAll(getGeneratedApiSymbols(templateData));

        for (SqlDatabaseEmitterTemplateData.DatabaseFieldData fieldData : templateData.getFields())
            checkPropertyName(fieldData.getPropertyName(), apiSymbols, compoundType);
    }

    private void checkPropertyName(String propertyName, Set<String> apiMethods, CompoundType compoundType)
            throws ZserioExtensionException
    {
        if (propertyName.startsWith("_"))
            throwPropertyNameError(propertyName, compoundType, "Property names cannot start with '_'!");

        if (apiMethods.contains(propertyName))
        {
            throwPropertyNameError(propertyName, compoundType,
                    "Property name clashes with generated API!");
        }
    }

    void throwPropertyNameError(String propertyName, CompoundType compoundType, String reason)
            throws ZserioExtensionException
    {
        ZserioToolPrinter.printError(compoundType.getLocation(),
                "Property name error detected in '" + compoundType.getName() + "'! " +
                "Consider to remove '-" + PythonExtensionParameters.OptionWithPythonProperties + "' option.");

        throw new ZserioExtensionException("Invalid property name '" + propertyName + "'! " + reason);
    }

    private Set<String> getTemplateApiMethods(String templateName) throws ZserioExtensionException
    {
        final Set<String> templateMethods = new HashSet<String>();
        final List<String> templateLines = readFreemarkerTemplate(templateName);
        for (String line : templateLines)
        {
            // we need to get only public methods which are part of the generated API, clashing with other
            // methods is covered by other rules (e.g. field cannot start with '_')
            final Pattern lineWithDef = Pattern.compile("\\s{4}def ([a-zA-Z]\\w+).*");
            final Matcher defMatcher = lineWithDef.matcher(line);
            if (defMatcher.find())
            {
                templateMethods.add(defMatcher.group(1));
            }
        }
        return templateMethods;
    }

    private Set<String> getGeneratedApiSymbols(CompoundTypeTemplateData templateData)
    {
        final Set<String> generatedSymbols = new HashSet<String>();

        // indicator methods for optional fields
        for (CompoundFieldTemplateData fieldData : templateData.getFieldList())
        {
            if (fieldData.getOptional() != null)
                generatedSymbols.add(fieldData.getOptional().getIndicatorName());
        }

        // generated function names
        for (CompoundFunctionTemplateData.CompoundFunction functionData :
                templateData.getCompoundFunctionsData().getList())
        {
            generatedSymbols.add(functionData.getName());
        }

        return generatedSymbols;
    }

    private Set<String> getGeneratedApiSymbols(UnionEmitterTemplateData templateData)
    {
        final Set<String> generatedSymbols = getGeneratedApiSymbols((CompoundTypeTemplateData)templateData);

        generatedSymbols.add(templateData.getUndefinedChoiceTagName());
        for (CompoundFieldTemplateData fieldData : templateData.getFieldList())
            generatedSymbols.add(templateData.getChoiceTagName(fieldData.getName()));

        return generatedSymbols;
    }

    private Set<String> getGeneratedApiSymbols(SqlDatabaseEmitterTemplateData templateData)
    {
        final Set<String> generatedSymbols = new HashSet<String>();

        generatedSymbols.add(templateData.getDatabaseNameConstant());
        for (SqlDatabaseEmitterTemplateData.DatabaseFieldData fieldData : templateData.getFields())
            generatedSymbols.add(fieldData.getTableNameConstant());

        return generatedSymbols;
    }
}
