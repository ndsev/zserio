package zserio.extension.common;

import zserio.ast.BitmaskType;
import zserio.ast.ChoiceType;
import zserio.ast.Constant;
import zserio.ast.EnumType;
import zserio.ast.Import;
import zserio.ast.InstantiateType;
import zserio.ast.Package;
import zserio.ast.PubsubType;
import zserio.ast.Root;
import zserio.ast.RuleGroup;
import zserio.ast.ServiceType;
import zserio.ast.SqlDatabaseType;
import zserio.ast.SqlTableType;
import zserio.ast.StructureType;
import zserio.ast.Subtype;
import zserio.ast.UnionType;

/**
 * A TreeWalker is a class that walks main Zserio language entities.
 *
 * An TreeWalker is passed to walk method of Root node which traverses the AST and invokes methods
 * of the TreeWalker when entering or leaving subtrees of the given type.
 */
public interface TreeWalker
{
    /**
     * Returns whether to traverse template instantiations or just call appropriate TreeWalker's methods
     * directly for templates.
     *
     * @return True if the walker requests to traverse template instantiations, false otherwise.
     */
    public boolean traverseTemplateInstantiations();

    /**
     * Called when Root AST node begins.
     *
     * @param root Current Root AST node.
     *
     * @throws ZserioExtensionException In case of any internal error of the extension.
     */
    public void beginRoot(Root root) throws ZserioExtensionException;

    /**
     * Called when Root AST node ends.
     *
     * @param root Current Root AST node.
     *
     * @throws ZserioExtensionException In case of any internal error of the extension.
     */
    public void endRoot(Root root) throws ZserioExtensionException;

    /**
     * Called when Package AST node begins.
     *
     * @param pkg Current Package AST node.
     *
     * @throws ZserioExtensionException In case of any internal error of the extension.
     */
    public void beginPackage(Package pkg) throws ZserioExtensionException;

    /**
     * Called when Package node ends.
     *
     * @param pkg Current Package AST node.
     *
     * @throws ZserioExtensionException In case of any internal error of the extension.
     */
    public void endPackage(Package pkg) throws ZserioExtensionException;

    /**
     * Called when Import AST node begins.
     *
     * @param importNode Current Import AST node.
     *
     * @throws ZserioExtensionException In case of any internal error of the extension.
     */
    public void beginImport(Import importNode) throws ZserioExtensionException;

    /**
     * Called when Constant AST node begins.
     *
     * @param constant Current Constant AST node.
     *
     * @throws ZserioExtensionException In case of any internal error of the extension.
     */
    public void beginConst(Constant constant) throws ZserioExtensionException;

    /**
     * Called when RuleGroup AST node begins.
     *
     * @param ruleGroup Current RuleGroup AST node.
     *
     * @throws ZserioExtensionException In case of any internal error of the extension.
     */
    public void beginRuleGroup(RuleGroup ruleGroup) throws ZserioExtensionException;

    /**
     * Called when Subtype AST node begins.
     *
     * @param subType Current Subtype AST node.
     *
     * @throws ZserioExtensionException In case of any internal error of the extension.
     */
    public void beginSubtype(Subtype subType) throws ZserioExtensionException;

    /**
     * Called when StructureType AST node begins.
     *
     * @param structureType Current StructureType AST node.
     *
     * @throws ZserioExtensionException In case of any internal error of the extension.
     */
    public void beginStructure(StructureType structureType) throws ZserioExtensionException;

    /**
     * Called when ChoiceType AST node begins.
     *
     * @param choiceType Current ChoiceType AST node.
     *
     * @throws ZserioExtensionException In case of any internal error of the extension.
     */
    public void beginChoice(ChoiceType choiceType) throws ZserioExtensionException;

    /**
     * Called when UnionType AST node begins.
     *
     * @param unionType Current UnionType AST node.
     *
     * @throws ZserioExtensionException In case of any internal error of the extension.
     */
    public void beginUnion(UnionType unionType) throws ZserioExtensionException;

    /**
     * Called when EnumType AST node begins.
     *
     * @param enumType Current EnumType AST node.
     *
     * @throws ZserioExtensionException In case of any internal error of the extension.
     */
    public void beginEnumeration(EnumType enumType) throws ZserioExtensionException;

    /**
     * Called when BitmaskType AST node begins.
     *
     * @param bitmaskType Current BitmaskType AST node.
     *
     * @throws ZserioExtensionException In case of any internal error of the extension.
     */
    public void beginBitmask(BitmaskType bitmaskType) throws ZserioExtensionException;

    /**
     * Called when SqlTableType AST node begins.
     *
     * @param sqlTableType Current SqlTableType AST node.
     *
     * @throws ZserioExtensionException In case of any internal error of the extension.
     */
    public void beginSqlTable(SqlTableType sqlTableType) throws ZserioExtensionException;

    /**
     * Called when SqlDatabaseType AST node begins.
     *
     * @param sqlDatabaseType Current SqlDatabaseType AST node.
     *
     * @throws ZserioExtensionException In case of any internal error of the extension.
     */
    public void beginSqlDatabase(SqlDatabaseType sqlDatabaseType) throws ZserioExtensionException;

    /**
     * Called when ServiceType AST node begins.
     *
     * @param service Current ServiceType AST node.
     *
     * @throws ZserioExtensionException In case of any internal error of the extension.
     */
    public void beginService(ServiceType service) throws ZserioExtensionException;

    /**
     * Called when PubsubType AST node begins.
     *
     * @param pubsub Current PubsubType AST node.
     *
     * @throws ZserioExtensionException In case of any internal error of the extension.
     */
    public void beginPubsub(PubsubType pubsub) throws ZserioExtensionException;

    /**
     * Called when InstantiateType AST node begins.
     *
     * @param instantiateType Current InstantiateType node.
     *
     * @throws ZserioExtensionException In case of any internal error of the extension.
     */
    public void beginInstantiateType(InstantiateType instantiateType) throws ZserioExtensionException;
}
