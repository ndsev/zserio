package zserio.extension.python;

import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import zserio.ast.ChoiceType;
import zserio.ast.CompoundType;
import zserio.ast.SqlDatabaseType;
import zserio.ast.SqlTableType;
import zserio.ast.StructureType;
import zserio.ast.UnionType;
import zserio.extension.common.DefaultTreeWalker;
import zserio.extension.common.ZserioExtensionException;
import zserio.extension.python.CompoundParameterTemplateData.CompoundParameter;
import zserio.tools.ZserioToolPrinter;

/**
 * Checks that Python code generator will not produce any clashes with generated properties.
 */
class PythonPropertyClashChecker extends DefaultTreeWalker
{
    PythonPropertyClashChecker(TemplateDataContext context)
    {
        this.context = context;
    }

    @Override
    public boolean traverseTemplateInstantiations()
    {
        return true;
    }

    @Override
    public void beginStructure(StructureType structureType) throws ZserioExtensionException
    {
        final StructureEmitterTemplateData templateData =
                new StructureEmitterTemplateData(context, structureType);

        checkPropertyNames(structureType, templateData, StructureEmitter.TEMPLATE_SOURCE_NAME);
    }

    @Override
    public void beginChoice(ChoiceType choiceType) throws ZserioExtensionException
    {
        final ChoiceEmitterTemplateData templateData =
                new ChoiceEmitterTemplateData(context, choiceType);

        checkPropertyNames(choiceType, templateData, ChoiceEmitter.TEMPLATE_SOURCE_NAME);
    }

    @Override
    public void beginUnion(UnionType unionType) throws ZserioExtensionException
    {
        final UnionEmitterTemplateData templateData =
                new UnionEmitterTemplateData(context, unionType);

        checkPropertyNames(unionType, templateData, UnionEmitter.TEMPLATE_SOURCE_NAME);
    }

    @Override
    public void beginSqlTable(SqlTableType sqlTableType) throws ZserioExtensionException
    {
        // we don't need to check SQL tables
    }

    @Override
    public void beginSqlDatabase(SqlDatabaseType sqlDatabaseType) throws ZserioExtensionException
    {
        final SqlDatabaseEmitterTemplateData templateData =
                new SqlDatabaseEmitterTemplateData(context, sqlDatabaseType);

        checkPropertyNames(sqlDatabaseType, templateData, SqlDatabaseEmitter.TEMPLATE_SOURCE_NAME);
    }

    private void checkPropertyNames(CompoundType compoundType, CompoundTypeTemplateData templateData,
            String templateSourceName) throws ZserioExtensionException
    {
        // we must check properties names to prevent clashing with public symbols in generated API
        final Set<String> apiSymbols = getTemplateApiMethods(templateSourceName);
        apiSymbols.addAll(getGeneratedApiSymbols(templateData));

        for (CompoundFieldTemplateData fieldData : templateData.getFieldList())
            checkPropertyName(fieldData.getPropertyName(), apiSymbols, compoundType);

        for (CompoundParameter paramData : templateData.getCompoundParametersData().getList())
            checkPropertyName(paramData.getPropertyName(), apiSymbols, compoundType);
    }

    private void checkPropertyNames(SqlDatabaseType sqlDatabaseType,
            SqlDatabaseEmitterTemplateData templateData,
            String templateSourceName) throws ZserioExtensionException
    {
        // we must check properties names to prevent clashing with public symbols in generated API
        final Set<String> apiSymbols = getTemplateApiMethods(templateSourceName);

        for (SqlDatabaseEmitterTemplateData.DatabaseFieldData fieldData : templateData.getFields())
            checkPropertyName(fieldData.getPropertyName(), apiSymbols, sqlDatabaseType);
    }

    private void checkPropertyName(String propertyName, Set<String> apiMethods, CompoundType compoundType)
            throws ZserioExtensionException
    {
        if (propertyName.startsWith("_"))
            throwPropertyNameError(propertyName, compoundType, "Property names cannot start with '_'!");

        if (apiMethods.contains(propertyName))
            throwPropertyNameError(propertyName, compoundType, "Property name clashes with generated API!");
    }

    private void throwPropertyNameError(String propertyName, CompoundType compoundType, String reason)
            throws ZserioExtensionException
    {
        ZserioToolPrinter.printError(compoundType.getLocation(),
                "Property name error detected in '" + compoundType.getName() + "'! " +
                "Please choose diferent name.");
        throw new ZserioExtensionException("Invalid property name '" + propertyName + "'! " + reason);
    }

    private Set<String> getTemplateApiMethods(String templateName) throws ZserioExtensionException
    {
        final Set<String> templateMethods = new HashSet<String>();
        final List<String> templateLines = PythonDefaultEmitter.readFreemarkerTemplate(templateName);
        for (String line : templateLines)
        {
            // we need to get only public methods which are part of the generated API, clashing with other
            // methods is covered by other rules (e.g. field cannot start with '_')
            final Pattern lineWithDef = Pattern.compile("\\s{4}def ([a-zA-Z]\\w+).*");
            final Matcher defMatcher = lineWithDef.matcher(line);
            if (defMatcher.find())
                templateMethods.add(defMatcher.group(1));
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

    private final TemplateDataContext context;
}
