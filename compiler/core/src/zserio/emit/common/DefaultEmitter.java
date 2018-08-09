package zserio.emit.common;

import antlr.collections.AST;

/**
 * Implements the Emitter interface and does nothing. Saves some typing for
 * derived classes that only need to implement a few of the emitter actions.
 */
public abstract class DefaultEmitter implements Emitter
{
    /**** implementation of interface methods ****/

    @Override
    public void beginRoot(AST r) {}
    @Override
    public void endRoot() {}

    @Override
    public void beginTranslationUnit(AST r, AST u) {}
    @Override
    public void endTranslationUnit() {}

    @Override
    public void beginPackage(AST p) {}
    @Override
    public void endPackage(AST p) {}

    @Override
    public void beginImport(AST i) {}
    @Override
    public void endImport() {}

    @Override
    public void beginConst(AST c) {}
    @Override
    public void endConst(AST c) {}

    @Override
    public void beginMembers() {}
    @Override
    public void endMembers() {}

    @Override
    public void beginStructure(AST s) {}
    @Override
    public void endStructure(AST s) {}

    @Override
    public void beginChoice(AST c) {}
    @Override
    public void endChoice(AST c) {}

    @Override
    public void beginUnion(AST u) {}
    @Override
    public void endUnion(AST u) {}

    @Override
    public void beginField(AST f) {}
    @Override
    public void endField(AST f) {}

    @Override
    public void beginFunction(AST f) {}
    @Override
    public void endFunction(AST f) {}

    @Override
    public void beginEnumeration(AST e) {}
    @Override
    public void endEnumeration(AST e) {}

    @Override
    public void beginEnumItem(AST e) {}
    @Override
    public void endEnumItem(AST e) {}

    @Override
    public void beginSubtype(AST s) {}
    @Override
    public void endSubtype(AST s) {}

    @Override
    public void beginSqlDatabase(AST s) {}
    @Override
    public void endSqlDatabase(AST s) {}

    @Override
    public void beginSqlTable(AST s) {}
    @Override
    public void endSqlTable(AST s) {}

    @Override
    public void beginService(AST s) {}
    @Override
    public void endService(AST s) {}

    @Override
    public void beginRpc(AST r) {}
    @Override
    public void endRpc(AST r) {}

    /**** end implementation of interface methods ****/
}
