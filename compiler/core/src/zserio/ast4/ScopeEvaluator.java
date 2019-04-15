package zserio.ast4;

import java.util.ArrayList;
import java.util.List;

public class ScopeEvaluator extends ZserioVisitor.Base
{
    @Override
    public void visitTranslationUnit(TranslationUnit translationUnit)
    {
        currentPackage = translationUnit.getPackage();

        translationUnit.visitChildren(this);

        currentPackage = null;
    }

    @Override
    public void visitStructureType(StructureType structureType)
    {
        currentScope = structureType.getScope();
        fillExpressionScopes = true;

        structureType.visitChildren(this);

        currentScope = defaultScope;
        expressionScopes.clear();
        fillExpressionScopes = false;
    }

    private Package currentPackage = null;

    private final Scope defaultScope = new Scope((ZserioScopedType)null);
    private Scope currentScope = defaultScope;
    private Scope currentChoiceOrUnionScope = null;

    private Field currentField = null;

    private List<Scope> expressionScopes = new ArrayList<Scope>();
    private boolean fillExpressionScopes = false;
    private boolean allowIndex = false;
}
