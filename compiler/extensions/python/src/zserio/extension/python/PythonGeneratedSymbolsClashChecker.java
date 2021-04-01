package zserio.extension.python;

import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;
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
import zserio.ast.ServiceMethod;
import zserio.ast.ServiceType;
import zserio.ast.SqlDatabaseType;
import zserio.ast.SqlTableType;
import zserio.ast.StructureType;
import zserio.ast.UnionType;
import zserio.extension.common.DefaultTreeWalker;
import zserio.extension.common.ZserioExtensionException;
import zserio.tools.ZserioToolPrinter;

/**
 * Generated symbols clash checker.
 *
 * Checks that Python code generator will not produce any clashes with generated properties.
 */
class PythonGeneratedSymbolsClashChecker extends DefaultTreeWalker
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

    @Override
    public void beginService(ServiceType serviceType) throws ZserioExtensionException
    {
        final SymbolNameChecker symbolNameChecker = new SymbolNameChecker();

        for (ServiceMethod method : serviceType.getMethodList())
        {
            final String methodName = AccessorNameFormatter.getServiceClientMethodName(method);
            symbolNameChecker.check(method, methodName, "Method");
        }
    }

    private void checkCompoundType(CompoundType compoundType,
            String templateSourceName) throws ZserioExtensionException
    {
        final CompoundSymbolNameChecker symbolNameChecker =
                new CompoundSymbolNameChecker(compoundType, templateSourceName);

        for (Field field : compoundType.getFields())
        {
            final String propertyName = AccessorNameFormatter.getPropertyName(field);
            symbolNameChecker.checkProperty(field, propertyName);
        }

        for (Parameter parameter : compoundType.getTypeParameters())
        {
            final String propertyName = AccessorNameFormatter.getPropertyName(parameter);
            symbolNameChecker.checkProperty(parameter, propertyName);
        }

        for (Function function : compoundType.getFunctions())
        {
            final String functionName = AccessorNameFormatter.getFunctionName(function);
            symbolNameChecker.checkFunction(function, functionName);
        }
    }

    private static class SymbolNameChecker
    {
        private void check(ScopeSymbol scopeSymbol, String generatedName, String symbolDescription)
                throws ZserioExtensionException
        {
            if (generatedName.startsWith("_"))
            {
                ZserioToolPrinter.printError(scopeSymbol.getLocation(),
                        "Invalid " + symbolDescription.toLowerCase(Locale.ENGLISH) + " name '" + generatedName +
                        "' generated for symbol '" + scopeSymbol.getName() + "'. " +
                        symbolDescription + " names cannot start with '_'!");
                throw new ZserioExtensionException(symbolDescription + " name error detected!");
            }
        }
    }

    private static class CompoundSymbolNameChecker extends SymbolNameChecker
    {
        public CompoundSymbolNameChecker(CompoundType compoundType, String templateSourceName)
                throws ZserioExtensionException
        {
            apiMethods = getTemplateApiMethods(templateSourceName);

            // get indicator names
            for (Field field : compoundType.getFields())
            {
                if (field.isOptional())
                    indicatorNames.put(AccessorNameFormatter.getIndicatorName(field), field);
            }
        }

        public void checkProperty(ScopeSymbol scopeSymbol, String propertyName) throws ZserioExtensionException
        {
            check(scopeSymbol, propertyName, "Property");
        }

        public void checkFunction(Function function, String functionName) throws ZserioExtensionException
        {
            check(function, functionName, "Function");
        }

        private void check(ScopeSymbol scopeSymbol, String generatedName, String symbolDescription)
                throws ZserioExtensionException
        {
            super.check(scopeSymbol, generatedName, symbolDescription);

            if (apiMethods.contains(generatedName))
            {
                ZserioToolPrinter.printError(scopeSymbol.getLocation(),
                        symbolDescription + " name '" + generatedName + "' generated for symbol '" +
                        scopeSymbol.getName() + "' clashes with generated API method!");
                throw new ZserioExtensionException(symbolDescription + " name clash detected!");
            }

            final Field clashingField = indicatorNames.get(generatedName);
            if (clashingField != null)
            {
                ZserioToolPrinter.printError(scopeSymbol.getLocation(),
                        symbolDescription + " name '" + generatedName + "' generated for symbol '" +
                        scopeSymbol.getName() + "' clashes with generated indicator for optional field '" +
                        clashingField.getName() + "' defined at " +
                        clashingField.getLocation().getLine() + ":" +
                        clashingField.getLocation().getColumn() + "!");
                throw new ZserioExtensionException(symbolDescription + " name clash detected!");
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
        private final Map<String, Field> indicatorNames = new HashMap<String, Field>();
    }
}
