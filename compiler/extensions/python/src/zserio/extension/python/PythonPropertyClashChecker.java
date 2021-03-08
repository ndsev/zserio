package zserio.extension.python;

import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import zserio.ast.ChoiceType;
import zserio.ast.CompoundType;
import zserio.ast.Field;
import zserio.ast.Function;
import zserio.ast.Parameter;
import zserio.ast.ScopeSymbol;
import zserio.ast.SqlDatabaseType;
import zserio.ast.SqlTableType;
import zserio.ast.StructureType;
import zserio.ast.UnionType;
import zserio.extension.common.DefaultTreeWalker;
import zserio.extension.common.ZserioExtensionException;
import zserio.tools.ZserioToolPrinter;

/**
 * Checks that Python code generator will not produce any clashes with generated properties.
 */
class PythonPropertyClashChecker extends DefaultTreeWalker
{
    @Override
    public boolean traverseTemplateInstantiations()
    {
        // names of properties are already given in templates
        return false;
    }

    @Override
    public void beginStructure(StructureType structureType) throws ZserioExtensionException
    {
        checkCompoundType(structureType, StructureEmitter.TEMPLATE_SOURCE_NAME);
    }

    @Override
    public void beginChoice(ChoiceType choiceType) throws ZserioExtensionException
    {
        checkCompoundType(choiceType, ChoiceEmitter.TEMPLATE_SOURCE_NAME);
    }

    @Override
    public void beginUnion(UnionType unionType) throws ZserioExtensionException
    {
        checkCompoundType(unionType, UnionEmitter.TEMPLATE_SOURCE_NAME);
    }

    @Override
    public void beginSqlTable(SqlTableType sqlTableType) throws ZserioExtensionException
    {
        // we don't need to check SQL tables since it doesn't define any properties
    }

    @Override
    public void beginSqlDatabase(SqlDatabaseType sqlDatabaseType) throws ZserioExtensionException
    {
        checkCompoundType(sqlDatabaseType, SqlDatabaseEmitter.TEMPLATE_SOURCE_NAME);
    }

    private void checkCompoundType(CompoundType compoundType,
            String templateSourceName) throws ZserioExtensionException
    {
        final PropertyNameChecker propertyNameChecker =
                new PropertyNameChecker(compoundType, templateSourceName);

        for (Field field : compoundType.getFields())
        {
            final String propertyName = AccessorNameFormatter.getPropertyName(field);
            propertyNameChecker.check(field, propertyName);
        }

        for (Parameter parameter : compoundType.getTypeParameters())
        {
            final String propertyName = AccessorNameFormatter.getPropertyName(parameter);
            propertyNameChecker.check(parameter, propertyName);
        }
    }

    private static class PropertyNameChecker
    {
        public PropertyNameChecker(CompoundType compoundType, String templateSourceName)
                throws ZserioExtensionException
        {
            apiMethods = getTemplateApiMethods(templateSourceName);

            // get function names
            for (Function function : compoundType.getFunctions())
                functionNames.put(AccessorNameFormatter.getFunctionName(function), function);

            // get indicator names
            for (Field field : compoundType.getFields())
            {
                if (field.isOptional())
                    indicatorNames.put(AccessorNameFormatter.getIndicatorName(field), field);
            }
        }

        public void check(ScopeSymbol scopeSymbol, String propertyName) throws ZserioExtensionException
        {
            if (propertyName.startsWith("_"))
            {
                ZserioToolPrinter.printError(scopeSymbol.getLocation(),
                        "Invalid property name '" + propertyName + "' generated for symbol '" +
                        scopeSymbol.getName() + "'. Property names cannot start with '_'!");
                throw new ZserioExtensionException("Property name error detected!");
            }

            if (apiMethods.contains(propertyName))
            {
                ZserioToolPrinter.printError(scopeSymbol.getLocation(),
                        "Property name '" + propertyName + "' generated for symbol '" + scopeSymbol.getName() +
                        "' clashes with generated API method!");
                throw new ZserioExtensionException("Property name clash detected!");
            }

            final Function clashingFunction = functionNames.get(propertyName);
            if (clashingFunction != null)
            {
                ZserioToolPrinter.printError(scopeSymbol.getLocation(),
                        "Property name '" + propertyName + "' generated for symbol '" + scopeSymbol.getName() +
                        "' clashes with generated method for function '" +
                        clashingFunction.getName() + "' defined at " +
                        clashingFunction.getLocation().getLine() + ":" +
                        clashingFunction.getLocation().getColumn() + "!");
                throw new ZserioExtensionException("Property name clash detected!");
            }

            final Field clashingField = indicatorNames.get(propertyName);
            if (clashingField != null)
            {
                ZserioToolPrinter.printError(scopeSymbol.getLocation(),
                        "Property name '" + propertyName + "' generated for symbol '" + scopeSymbol.getName() +
                        "' clashes with generated indicator for optional field '" +
                        clashingField.getName() + "' defined at " +
                        clashingField.getLocation().getLine() + ":" +
                        clashingField.getLocation().getColumn() + "!");
                throw new ZserioExtensionException("Property name clash detected!");
            }
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

        private final Set<String> apiMethods;
        private final Map<String, Function> functionNames = new HashMap<String, Function>();
        private final Map<String, Field> indicatorNames = new HashMap<String, Field>();
    }
}
