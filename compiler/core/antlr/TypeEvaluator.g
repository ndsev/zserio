/*
 * Tree walker to evaluate types.
 */
header
{
package zserio.antlr;

import java.util.ArrayList;
import java.util.List;

import zserio.ast.*;
import zserio.ast.Package; // explicit to override java.lang.Package
import zserio.antlr.util.BaseTokenAST;
import zserio.antlr.util.ParserException;
}

class TypeEvaluator extends TreeParser;

options
{
    importVocab=ZserioParser;
    defaultErrorHandler=false;
}

{
    private Package currentPackage = null;
    private Scope defaultScope = new Scope((ZserioScopedType)null);
    private Scope currentScope = defaultScope;
    private Scope currentChoiceOrUnionScope = null;
    private List<Scope> expressionScopes = new ArrayList<Scope>();
    private boolean fillExpressionScopes = false;
    private boolean allowIndex = false;

    public void reportError(RecognitionException ex)
    {
        System.out.println(ex.toString());
    }

    private void setupExpression(Expression e)
    {
        final Scope expressionScope = new Scope(currentScope);
        if (fillExpressionScopes)
            expressionScopes.add(expressionScope);
        e.setPackage(currentPackage);
        e.setScope(expressionScope);
        e.setAllowIndex(allowIndex);
    }
}

/**
 * Root.
 */
root
    :   #(ROOT
            (translationUnit)+
        )
    ;

translationUnit
    :   #(TRANSLATION_UNIT (packageDeclaration)? (importDeclaration)* (commandDeclaration)*)
    ;

packageDeclaration
    :   #(p:PACKAGE (ID)*) // default package does not have IDs
        {
            currentPackage = (Package)p;
        }
    ;

importDeclaration
    :   #(i:IMPORT (ID)+ (MULTIPLY)?)
    ;

commandDeclaration
    :   constDeclaration |
        subtypeDeclaration |
        structureDeclaration |
        choiceDeclaration |
        unionDeclaration |
        enumDeclaration |
        sqlTableDeclaration |
        sqlDatabaseDefinition |
        serviceDeclaration
    ;

/**
 * constDeclaration.
 */
constDeclaration
    :   #(c:CONST                   {
                                        final ConstType constType = (ConstType)c;
                                        constType.setPackage(currentPackage);
                                    }
            definedType
            i:ID                    {
                                        currentPackage.setLocalType((BaseTokenAST)i, (ConstType)c);
                                    }
            e:expression
        ) 
    ;

/**
 * subtypeDeclaration.
 */
subtypeDeclaration
    :   #(s:SUBTYPE                 {
                                        final Subtype subtype = (Subtype)s;
                                        subtype.setPackage(currentPackage);
                                    }
    
            definedType
            i:ID                    {
                                        currentPackage.setLocalType((BaseTokenAST)i, subtype);
                                    }
        )
    ;

/**
 * structureDeclaration.
 */
structureDeclaration
    :   #(s:STRUCTURE
            i:ID                    {
                                        final StructureType structureType = (StructureType)s;
                                        currentPackage.setLocalType((BaseTokenAST)i, structureType);
                                        structureType.setPackage(currentPackage);
                                        currentScope = structureType.getScope();
                                        fillExpressionScopes = true;
                                    }
            (parameterList)?
            (structureFieldDefinition)*
            (functionDefinition)*
        )                           {
                                        currentScope = defaultScope;
                                        expressionScopes.clear();
                                        fillExpressionScopes = false;
                                    }
    ;

structureFieldDefinition
    :   #(f:FIELD
            (typeReference | a:fieldArrayType)
            i:ID
            (OPTIONAL)?
            (fieldInitializer)?
            (fieldOptionalClause)?  { currentScope.setSymbol((BaseTokenAST)i, f); }
            (fieldConstraint)?
            (
                                    { if (a != null) allowIndex = true; } // only for array type
                fieldOffset
                                    { allowIndex = false; }
            )?
            (fieldAlignment)?
        )
    ;

fieldArrayType
    :   #(ARRAY
                                    { allowIndex = true; } // needed for parametrized types
            typeReference
                                    { allowIndex = false; }
            fieldArrayRange
            (IMPLICIT)?)
    ;

fieldArrayRange
    :   (expression)?
    ;

fieldInitializer
    :   #(ASSIGN expression)
    ;

fieldOptionalClause
    :   #(IF expression)
    ;

fieldConstraint
    :   #(COLON expression)
    ;

fieldOffset
    :   #(OFFSET expression)
    ;

fieldAlignment
    :   #(ALIGN expression)
    ;

functionDefinition
    :   #(f:FUNCTION
            definedType
            i:ID                    {
                                        for (Scope expressionScope : expressionScopes)
                                            expressionScope.setSymbol((BaseTokenAST)i, f);
                                    }
            functionBody            {
                                        currentScope.setSymbol((BaseTokenAST)i, f);
                                        ((FunctionType)f).setPackage(currentPackage);
                                    }
        )
    ;

functionBody
    :   #(RETURN expression)
    ;

/**
 * choiceDeclaration.
 */
choiceDeclaration
    :   #(c:CHOICE
            i:ID                    {
                                        final ChoiceType choiceType = (ChoiceType)c;
                                        currentPackage.setLocalType((BaseTokenAST)i, choiceType);
                                        choiceType.setPackage(currentPackage);
                                        currentScope = choiceType.getScope();
                                        fillExpressionScopes = true;
                                    }
            parameterList           {
                                        currentChoiceOrUnionScope = currentScope;
                                        currentScope = new Scope(currentChoiceOrUnionScope);
                                    }
            expression
            (choiceCases)+
            (defaultChoice)?        {
                                        currentScope = currentChoiceOrUnionScope;
                                        currentChoiceOrUnionScope = null;
                                    }
            (functionDefinition)*
        )                           {   
                                        currentScope = defaultScope;
                                        expressionScopes.clear();
                                        fillExpressionScopes = false;
                                    }
    ;

choiceCases
    :   #(CASE expression (CASE expression)* (choiceFieldDefinition)?)
    ;

choiceFieldDefinition
    :   #(f:FIELD
            (typeReference | fieldArrayType)
            i:ID                    {
                                        currentScope.setSymbol((BaseTokenAST)i, f);
                                        currentChoiceOrUnionScope.setSymbol((BaseTokenAST)i, f);
                                    }
            (fieldConstraint)?
        )                           { currentScope.removeSymbol((BaseTokenAST)i); }
    ;

defaultChoice
    :   #(DEFAULT (choiceFieldDefinition)?)
    ;

/**
 * unionDeclaration.
 */
unionDeclaration
    :   #(u:UNION
            i:ID                    {
                                        final UnionType unionType = (UnionType)u;
                                        currentPackage.setLocalType((BaseTokenAST)i, unionType);
                                        unionType.setPackage(currentPackage);
                                        currentChoiceOrUnionScope = unionType.getScope();
                                        currentScope = new Scope(currentChoiceOrUnionScope);
                                        fillExpressionScopes = true;
                                    }
            (parameterList)?        {
                                        currentChoiceOrUnionScope = currentScope;
                                        currentScope = new Scope(currentChoiceOrUnionScope);
                                    }
            (unionFieldDefinition)+ {
                                        currentScope = currentChoiceOrUnionScope;
                                        currentChoiceOrUnionScope = null;
                                    }
            (functionDefinition)*
        )                           {   
                                        currentScope = defaultScope;
                                        expressionScopes.clear();
                                        fillExpressionScopes = false;
                                    }
    ;

unionFieldDefinition
    : choiceFieldDefinition
    ;

/**
 * enumDeclaration.
 */
enumDeclaration
    :   #(e:ENUM                    {
                                        final EnumType enumType = (EnumType)e;
                                        enumType.setPackage(currentPackage);
                                        currentScope = enumType.getScope();
                                    }
            definedType
            i:ID                    {
                                        currentPackage.setLocalType((BaseTokenAST)i, (EnumType)e);
                                    }
            (enumItem)+
        )                           { currentScope = defaultScope; }
    ;

enumItem
    :   #(f:ITEM i:ID (expression)?)
        {
            currentScope.setSymbol((BaseTokenAST)i, f);
        }
    ;

/**
 * sqlTableDeclaration.
 */
sqlTableDeclaration
    :   #(s:SQL_TABLE
            i:ID                    {
                                        final SqlTableType sqlTableType = (SqlTableType)s;
                                        currentPackage.setLocalType((BaseTokenAST)i, sqlTableType);
                                        sqlTableType.setPackage(currentPackage);
                                        currentScope = sqlTableType.getScope();
                                    }
            (ID)?
            (sqlTableFieldDefinition | sqlTableVirtualFieldDefinition)*
            (sqlConstraint)?
            (sqlWithoutRowId)?
        )                           { currentScope = defaultScope; }
    ;

sqlTableFieldDefinition
    :   #(f:FIELD
            typeReference
            i:ID                    { currentScope.setSymbol((BaseTokenAST)i, f); }
            (sqlConstraint)?
        )
    ;

sqlConstraint
    :   #(SQL (STRING_LITERAL)+)
    ;

sqlTableVirtualFieldDefinition
    :   #(f:VFIELD
            typeReference
            i:ID
            (sqlConstraint)?
            SQL_VIRTUAL
        )
    ;

sqlWithoutRowId
    :   SQL_WITHOUT_ROWID
    ;

/**
 * sqlDatabaseDefinition.
 */
sqlDatabaseDefinition
    :   #(s:SQL_DATABASE
            i:ID                    {
                                        final SqlDatabaseType sqlDatabaseType = (SqlDatabaseType)s;
                                        currentPackage.setLocalType((BaseTokenAST)i, sqlDatabaseType);
                                        sqlDatabaseType.setPackage(currentPackage);
                                        currentScope = sqlDatabaseType.getScope();
                                    }
            (sqlDatabaseFieldDefinition)+
        )                           { currentScope = defaultScope; }
    ;

sqlDatabaseFieldDefinition
    :   #(f:FIELD sqlTableDefinition[f])
    ;

sqlTableDefinition[AST astField]    { Field f = (Field) #astField; }
    :   sqlTableReference i:ID      {
                                        currentScope.setSymbol((BaseTokenAST)i, f);
                                    }
    ;

sqlTableReference
    :   typeSymbol
    ;

/**
 * serviceDeclaration.
 */
serviceDeclaration
    :   #(s:SERVICE i:ID            {
                                        final ServiceType serviceType = (ServiceType)s;
                                        currentPackage.setLocalType((BaseTokenAST)i, serviceType);
                                        serviceType.setPackage(currentPackage);
                                        currentScope = serviceType.getScope();
                                    }
            (rpcDeclaration)*
        )                           { currentScope = defaultScope; }
    ;

rpcDeclaration
    : #(r:RPC (STREAM)? typeSymbol i:ID (STREAM)? typeSymbol
                                    {
                                        currentScope.setSymbol((BaseTokenAST)i, r);
                                    }
      )
    ;

/**
 * definedType.
 */
definedType
    :   typeSymbol |
        builtinType
    ;

typeSymbol
    :   #(t:TYPEREF ID (DOT ID)*)
        {
            currentPackage.addTypeReferenceToResolve((TypeReference)t);
        }
    ;

builtinType
    :   integerType |
        unsignedBitField |
        signedBitField |
        varintType |
        boolType |
        stringType |
        floatType
    ;

integerType
    :   UINT8 |
        UINT16 |
        UINT32 |
        UINT64 |
        INT8 |
        INT16 |
        INT32 |
        INT64
    ;

unsignedBitField
    :   #(BIT expression)
    ;

signedBitField
    :   #(INT expression)
    ;

varintType
    :   VARINT |
        VARINT16 |
        VARINT32 |
        VARINT64 |
        VARUINT |
        VARUINT16 |
        VARUINT32 |
        VARUINT64
    ;

boolType
    :   BOOL
    ;

stringType
    :   STRING
    ;

floatType
    :   FLOAT16 |
        FLOAT32 |
        FLOAT64
    ;

/**
 * parameterList.
 */
parameterList
    :   (parameterDefinition)+
    ;

parameterDefinition
    :   #(p:PARAM definedType i:ID)
        {
            currentScope.setSymbol((BaseTokenAST)i, p);
        }
    ;

/**
 * typeReference.
 */
typeReference
    :   paramTypeInstantiation |
        definedType
    ;

paramTypeInstantiation
    :   #(INST definedType typeArgumentList)
    ;

typeArgumentList
    :   (expression)+
    ;

/**
 * expression.
 */
expression
    :   (
            atom |
            opExpression
        )
        {
            setupExpression((Expression) #expression);
        }
    ;

atom
    :   ID |
        INDEX |
        DECIMAL_LITERAL |
        BINARY_LITERAL |
        FLOAT_LITERAL |
        DOUBLE_LITERAL |
        HEXADECIMAL_LITERAL |
        OCTAL_LITERAL |
        BOOL_LITERAL |
        STRING_LITERAL
    ;

opExpression
    :
        #(QUESTIONMARK expression expression expression) |
        #(LOGICALOR expression expression) |
        #(LOGICALAND expression expression) |
        #(OR expression expression) |
        #(XOR expression expression) |
        #(AND expression expression) |
        #(EQ expression expression) |
        #(NE expression expression) |
        #(LT expression expression) |
        #(GT expression expression) |
        #(LE expression expression) |
        #(GE expression expression) |
        #(LSHIFT expression expression) |
        #(RSHIFT expression expression) |
        #(PLUS expression expression) |
        #(MINUS expression expression) |
        #(MULTIPLY expression expression) |
        #(DIVIDE expression expression) |
        #(MODULO expression expression) |
        #(UPLUS expression) |
        #(UMINUS expression) |
        #(TILDE expression) |
        #(BANG expression) |
        #(LENGTHOF expression) |
        #(DOT (expression)+) |
        #(ARRAYELEM expression expression) |
        #(LPAREN expression) |
        #(FUNCTIONCALL expression) |
        #(EXPLICIT ID) |
        #(SUM expression) |
        #(VALUEOF expression) |
        #(NUMBITS expression)
    ;
