package zserio.extension.python;

import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import zserio.ast.CompoundType;
import zserio.ast.SqlDatabaseType;
import zserio.extension.common.ZserioExtensionException;
import zserio.extension.python.CompoundParameterTemplateData.CompoundParameter;
import zserio.tools.ZserioToolPrinter;

/**
 * Base class for compound emitters, which provides checking for property names clashing with other generated
 * methods when -withoutPythonPropPrefix is used.
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
        if (!getWithPythonPropPrefix())
            checkPropertyNames(templateName, templateData, compoundType);
        processSourceTemplate(templateName, templateData, compoundType);
    }

    protected void processCompoundTemplate(String templateName, SqlDatabaseEmitterTemplateData templateData,
            SqlDatabaseType sqlDatabaseType) throws ZserioExtensionException
    {
        if (!getWithPythonPropPrefix())
            checkPropertyNames(templateName, templateData, sqlDatabaseType);
        processSourceTemplate(templateName, templateData, sqlDatabaseType);
    }

    private void checkPropertyNames(String templateName, CompoundTypeTemplateData templateData,
            CompoundType compoundType) throws ZserioExtensionException
    {
        // we must check properties names to prevent clashing
        final Set<String> templateApiMethods = getTemplateApiMethods(templateName);
        final Set<String> accessorMethods = getAccessorMethods(templateData);

        for (CompoundFieldTemplateData fieldData : templateData.getFieldList())
            checkPropertyName(fieldData.getPropertyName(), templateApiMethods, accessorMethods, compoundType);

        for (CompoundParameter paramData : templateData.getCompoundParametersData().getList())
            checkPropertyName(paramData.getPropertyName(), templateApiMethods, accessorMethods, compoundType);
    }

    private void checkPropertyNames(String templateName, SqlDatabaseEmitterTemplateData templateData,
            CompoundType compoundType) throws ZserioExtensionException
    {
        // we must check properties names to prevent clashing
        final Set<String> templateApiMethods = getTemplateApiMethods(templateName);
        final Set<String> accessorMethods = getAccessorMethods(templateData);

        for (SqlDatabaseEmitterTemplateData.DatabaseFieldData fieldData : templateData.getFields())
            checkPropertyName(fieldData.getPropertyName(), templateApiMethods, accessorMethods, compoundType);
    }

    private void checkPropertyName(String propertyName, Set<String> apiMethods, Set<String> accessorMethods,
            CompoundType compoundType) throws ZserioExtensionException
    {
        if (propertyName.startsWith("_"))
            throwPropertyNameError(propertyName, compoundType, "Property names cannot start with '_'!");

        if (apiMethods.contains(propertyName))
        {
            throwPropertyNameError(propertyName, compoundType,
                    "Property name clashes with a generated API method!");
        }

        if (accessorMethods.contains(propertyName))
        {
            throwPropertyNameError(propertyName, compoundType,
                    "Property name clashes with a generated accessor method!");
        }
    }

    void throwPropertyNameError(String propertyName, CompoundType compoundType, String reason)
            throws ZserioExtensionException
    {
        ZserioToolPrinter.printError(compoundType.getLocation(),
                "Property name clashing detected in '" + compoundType.getName() + "'! " +
                "Consider to remove '-" + PythonExtensionParameters.OptionWithoutPythonPropPrefix +
                "' option.");

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

    private Set<String> getAccessorMethods(CompoundTypeTemplateData templateData)
    {
        final Set<String> accessorMethods = new HashSet<String>();

        for (CompoundFieldTemplateData fieldData : templateData.getFieldList())
        {
            // we don't care about withWriterCode option,
            // just check always to prevent later problems with clashing
            accessorMethods.add(fieldData.getSetterName());
            accessorMethods.add(fieldData.getGetterName());
            if (fieldData.getOptional() != null)
                accessorMethods.add(fieldData.getOptional().getIndicatorName());
        }

        for (CompoundParameterTemplateData.CompoundParameter parameterData :
                templateData.getCompoundParametersData().getList())
        {
            accessorMethods.add(parameterData.getGetterName());
        }

        for (CompoundFunctionTemplateData.CompoundFunction functionData :
            templateData.getCompoundFunctionsData().getList())
        {
            accessorMethods.add(functionData.getName());
        }

        return accessorMethods;
    }

    private Set<String> getAccessorMethods(SqlDatabaseEmitterTemplateData templateData)
    {
        final Set<String> accessorMethods = new HashSet<String>();

        for (SqlDatabaseEmitterTemplateData.DatabaseFieldData fieldData : templateData.getFields())
        {
            accessorMethods.add(fieldData.getGetterName());
        }

        return accessorMethods;
    }
}