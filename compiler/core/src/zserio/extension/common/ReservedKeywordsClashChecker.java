package zserio.extension.common;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

import zserio.ast.AstNode;
import zserio.ast.BitmaskType;
import zserio.ast.ChoiceType;
import zserio.ast.CompoundType;
import zserio.ast.Constant;
import zserio.ast.EnumType;
import zserio.ast.Field;
import zserio.ast.Function;
import zserio.ast.Import;
import zserio.ast.InstantiateType;
import zserio.ast.Package;
import zserio.ast.PackageSymbol;
import zserio.ast.Parameter;
import zserio.ast.PubsubMessage;
import zserio.ast.PubsubType;
import zserio.ast.Root;
import zserio.ast.RuleGroup;
import zserio.ast.ScopeSymbol;
import zserio.ast.ServiceMethod;
import zserio.ast.ServiceType;
import zserio.ast.SqlDatabaseType;
import zserio.ast.SqlTableType;
import zserio.ast.StructureType;
import zserio.ast.Subtype;
import zserio.ast.UnionType;
import zserio.tools.ZserioToolPrinter;

/**
 * Clash checker which checks clashing of identifiers with the given list of reserved keywords.
 */
public class ReservedKeywordsClashChecker implements TreeWalker
{
    /**
     * Constructor.
     *
     * @param languageName Name of the language to be reported in error messages.
     * @param reservedKeywords Array of reserved keywords for the particular extension.
     */
    public ReservedKeywordsClashChecker(String languageName, String[] reservedKeywords)
    {
        this.languageName = languageName;
        this.reservedKeywords = new HashSet<String>(Arrays.asList(reservedKeywords));
    }

    @Override
    public boolean traverseTemplateInstantiations()
    {
        return false;
    }

    @Override
    public void beginRoot(Root root) throws ZserioExtensionException
    {}

    @Override
    public void endRoot(Root root) throws ZserioExtensionException
    {}

    @Override
    public void beginPackage(Package pkg) throws ZserioExtensionException
    {
        for (String id : pkg.getPackageName().getIdList())
            checkId(id, pkg, "Package id");
    }

    @Override
    public void endPackage(Package pkg) throws ZserioExtensionException
    {}

    @Override
    public void beginImport(Import importNode) throws ZserioExtensionException
    {}

    @Override
    public void beginConst(Constant constant) throws ZserioExtensionException
    {
        checkId(constant, "Constant");
    }

    @Override
    public void beginRuleGroup(RuleGroup ruleGroup) throws ZserioExtensionException
    {
        // rule group is only other form of documentation
    }

    @Override
    public void beginSubtype(Subtype subtype) throws ZserioExtensionException
    {
        checkId(subtype, "Subtype");
    }

    @Override
    public void beginStructure(StructureType structureType) throws ZserioExtensionException
    {
        checkCompound(structureType, "Structure");
    }

    @Override
    public void beginChoice(ChoiceType choiceType) throws ZserioExtensionException
    {
        checkCompound(choiceType, "Choice");
    }

    @Override
    public void beginUnion(UnionType unionType) throws ZserioExtensionException
    {
        checkCompound(unionType, "Union");
    }

    @Override
    public void beginEnumeration(EnumType enumType) throws ZserioExtensionException
    {
        checkId(enumType, "Enum");
    }

    @Override
    public void beginBitmask(BitmaskType bitmaskType) throws ZserioExtensionException
    {
        checkId(bitmaskType, "Bitmask");
    }

    @Override
    public void beginSqlTable(SqlTableType sqlTableType) throws ZserioExtensionException
    {
        checkCompound(sqlTableType, "SQL Table");
    }

    @Override
    public void beginSqlDatabase(SqlDatabaseType sqlDatabaseType) throws ZserioExtensionException
    {
        checkCompound(sqlDatabaseType, "SQL Database");
    }

    @Override
    public void beginService(ServiceType service) throws ZserioExtensionException
    {
        checkId(service, "Service");
        for (ServiceMethod method : service.getMethodList())
            checkId(method, "Service method");
    }

    @Override
    public void beginPubsub(PubsubType pubsub) throws ZserioExtensionException
    {
        checkId(pubsub, "Pubsub");
        for (PubsubMessage message : pubsub.getMessageList())
            checkId(message, "Pubsub message");
    }

    @Override
    public void beginInstantiateType(InstantiateType instantiateType) throws ZserioExtensionException
    {
        checkId(instantiateType, "Template instantiation");
    }

    private void checkCompound(CompoundType compound, String description) throws ZserioExtensionException
    {
        checkId(compound, description);

        for (Parameter param : compound.getTypeParameters())
            checkId(param, "Parameter");

        for (Field field : compound.getFields())
            checkId(field, "Field");

        for (Function function : compound.getFunctions())
            checkId(function, "Function");
    }

    private void checkId(String id, AstNode node, String description) throws ZserioExtensionException
    {
        if (reservedKeywords.contains(id))
        {
            ZserioToolPrinter.printError(node.getLocation(),
                    description + " '" + id + "' clashes with a " + languageName + " keyword and " +
                    "may not be used as an identifier!");
            throw new ZserioExtensionException("Invalid identifier found!");
        }
    }

    private void checkId(PackageSymbol packageSymbol, String description) throws ZserioExtensionException
    {
        checkId(packageSymbol.getName(), packageSymbol, description);
    }

    private void checkId(ScopeSymbol scopeSymbol, String description) throws ZserioExtensionException
    {
        checkId(scopeSymbol.getName(), scopeSymbol, description);
    }

    private final String languageName;
    private final Set<String> reservedKeywords;
}
