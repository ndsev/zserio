package zserio.emit.common;

import zserio.ast.BitmaskType;
import zserio.ast.ChoiceType;
import zserio.ast.Constant;
import zserio.ast.EnumType;
import zserio.ast.Import;
import zserio.ast.InstantiateType;
import zserio.ast.Package;
import zserio.ast.PubsubType;
import zserio.ast.Root;
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
     * @throws In case of any internal error of the extension.
     */
    public void beginRoot(Root root) throws ZserioEmitException;

    /**
     * Called when Root AST node ends.
     *
     * @param root Current Root AST node.
     *
     * @throws In case of any internal error of the extension.
     */
    public void endRoot(Root root) throws ZserioEmitException;

    /**
     * Called when Package AST node begins.
     *
     * @param translationUnit Current Package AST node.
     *
     * @throws In case of any internal error of the extension.
     */
    public void beginPackage(Package pkg) throws ZserioEmitException;

    /**
     * Called when Package node ends.
     *
     * @param translationUnit Current Package AST node.
     *
     * @throws In case of any internal error of the extension.
     */
    public void endPackage(Package pkg) throws ZserioEmitException;

    /**
     * Called when Import AST node begins.
     *
     * @param importNode Current Import AST node.
     *
     * @throws In case of any internal error of the extension.
     */
    public void beginImport(Import importNode) throws ZserioEmitException;

    /**
     * Called when Constant AST node begins.
     *
     * @param constant Current Constant AST node.
     *
     * @throws In case of any internal error of the extension.
     */
    public void beginConst(Constant constant) throws ZserioEmitException;

    /**
     * Called when Subtype AST node begins.
     *
     * @param subType Current Subtype AST node.
     *
     * @throws In case of any internal error of the extension.
     */
    public void beginSubtype(Subtype subType) throws ZserioEmitException;

    /**
     * Called when StructureType AST node begins.
     *
     * @param structureType Current StructureType AST node.
     *
     * @throws In case of any internal error of the extension.
     */
    public void beginStructure(StructureType structureType) throws ZserioEmitException;

    /**
     * Called when ChoiceType AST node begins.
     *
     * @param choiceType Current ChoiceType AST node.
     *
     * @throws In case of any internal error of the extension.
     */
    public void beginChoice(ChoiceType choiceType) throws ZserioEmitException;

    /**
     * Called when UnionType AST node begins.
     *
     * @param unionType Current UnionType AST node.
     *
     * @throws In case of any internal error of the extension.
     */
    public void beginUnion(UnionType unionType) throws ZserioEmitException;

    /**
     * Called when EnumType AST node begins.
     *
     * @param enumType Current EnumType AST node.
     *
     * @throws In case of any internal error of the extension.
     */
    public void beginEnumeration(EnumType enumType) throws ZserioEmitException;

    /**
     * Called when BitmaskType AST node begins.
     *
     * @param bitmaskType Current BitmaskType AST node.
     *
     * @throws In case of any internal error of the extension.
     */
    public void beginBitmask(BitmaskType bitmaskType) throws ZserioEmitException;

    /**
     * Called when SqlTableType AST node begins.
     *
     * @param sqlTableType Current SqlTableType AST node.
     *
     * @throws In case of any internal error of the extension.
     */
    public void beginSqlTable(SqlTableType sqlTableType) throws ZserioEmitException;

    /**
     * Called when SqlDatabaseType AST node begins.
     *
     * @param sqlDatabaseType Current SqlDatabaseType AST node.
     *
     * @throws In case of any internal error of the extension.
     */
    public void beginSqlDatabase(SqlDatabaseType sqlDatabaseType) throws ZserioEmitException;

    /**
     * Called when ServiceType AST node begins.
     *
     * @param service Current ServiceType AST node.
     *
     * @throws In case of any internal error of the extension.
     */
    public void beginService(ServiceType service) throws ZserioEmitException;

    /**
     * Called when PubsubType AST node begins.
     *
     * @param pubsub Current PubsubType AST node.
     *
     * @throws In case of any internal error of the extension.
     */
    public void beginPubsub(PubsubType pubsub) throws ZserioEmitException;

    /**
     * Called when InstantiateType AST node begins.
     *
     * @param instantiateType Current InstantiateType node.
     *
     * @throws In case of any internal error of the extension.
     */
    public void beginInstantiateType(InstantiateType instantiateType) throws ZserioEmitException;
}
