/*
 * Tree walker to evaluate types.
 */
header
{
package zserio.antlr;

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
    private Package pkg = null;
    private Scope scope = null;
    private boolean allowIndex = false;

    public void reportError(RecognitionException ex)
    {
        System.out.println(ex.toString());
    }

    private void beginScope(ZserioType owner)
    {
        scope = new Scope(pkg, owner);
    }

    private void endScope()
    {
        scope = null;
    }

    private void setupExpression(Expression e)
    {
        e.setScope(scope);
        e.setAvailableSymbols(scope.copyAvailableSymbols());
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
            pkg = (Package)p;
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
                                        beginScope(constType); // needed for expressions only
                                        constType.setPackage(pkg);
                                    }
            definedType
            i:ID                    {
                                        pkg.setLocalType((BaseTokenAST)i, (ConstType)c);
                                    }
            e:expression
        )                           { endScope(); }
    ;

/**
 * subtypeDeclaration.
 */
subtypeDeclaration
    :   #(s:SUBTYPE                 {
                                        final Subtype subtype = (Subtype)s;
                                        beginScope(subtype); // needed for expressions only
                                        subtype.setPackage(pkg);
                                    }
    
            definedType
            i:ID                    {
                                        pkg.setLocalType((BaseTokenAST)i, subtype);
                                    }
        )                           { endScope(); }
    ;

/**
 * structureDeclaration.
 */
structureDeclaration
    :   #(s:STRUCTURE
            i:ID                    {
                                        final StructureType structureType = (StructureType)s;
                                        pkg.setLocalType((BaseTokenAST)i, structureType);
                                        beginScope(structureType);
                                        structureType.setScope(scope, pkg);
                                    }
            (parameterList)?
            (structureFieldDefinition)*
            (functionDefinition)*
        )                           { endScope(); }
    ;

structureFieldDefinition
    :   #(f:FIELD
            (typeReference | a:fieldArrayType)
            i:ID                    { scope.setSymbol((BaseTokenAST)i, f); }
            (OPTIONAL)?
            (fieldInitializer)?
            (fieldOptionalClause)?
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
    :   #(f:FUNCTION definedType i:ID functionBody)
        {
            scope.setSymbol((BaseTokenAST)i, f);
            ((FunctionType)f).setPackage(pkg);
        }
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
                                        pkg.setLocalType((BaseTokenAST)i, choiceType);
                                        beginScope(choiceType);
                                        choiceType.setScope(scope, pkg);
                                    }
            parameterList
            expression
            (choiceCases)+
            (defaultChoice)?
            (functionDefinition)*
        )                           { endScope(); }
    ;

choiceCases
    :   #(CASE expression (CASE expression)* (choiceFieldDefinition)?)
    ;

choiceFieldDefinition
    :   #(f:FIELD
            (typeReference | fieldArrayType)
            i:ID                    { scope.setSymbol((BaseTokenAST)i, f); }
            (fieldConstraint)?
        )
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
                                        pkg.setLocalType((BaseTokenAST)i, unionType);
                                        beginScope(unionType);
                                        unionType.setScope(scope, pkg);
                                    }
            (parameterList)?
            (unionFieldDefinition)+
            (functionDefinition)*
        )                           { endScope(); }
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
                                        beginScope(enumType);
                                        enumType.setScope(scope, pkg);
                                    }
            definedType
            i:ID                    {
                                        pkg.setLocalType((BaseTokenAST)i, (EnumType)e);
                                    }
            (enumItem)+
        )                           { endScope(); }
    ;

enumItem
    :   #(f:ITEM i:ID (expression)?)
        {
            scope.setSymbol((BaseTokenAST)i, f);
        }
    ;

/**
 * sqlTableDeclaration.
 */
sqlTableDeclaration
    :   #(s:SQL_TABLE
            i:ID                    {
                                        final SqlTableType sqlTableType = (SqlTableType)s;
                                        pkg.setLocalType((BaseTokenAST)i, sqlTableType);
                                        beginScope(sqlTableType);
                                        sqlTableType.setScope(scope, pkg);
                                    }
            (ID)?
            (sqlTableFieldDefinition | sqlTableVirtualFieldDefinition)*
            (sqlConstraint)?
            (sqlWithoutRowId)?
        )                           { endScope(); }
    ;

sqlTableFieldDefinition
    :   #(f:FIELD
            typeReference
            i:ID                    { scope.setSymbol((BaseTokenAST)i, f); }
            (sqlConstraint)?
        )
    ;

sqlConstraint
    :   #(SQL (STRING_LITERAL)+)
    ;

sqlTableVirtualFieldDefinition
    :   #(f:VFIELD
            typeReference
            i:ID                    { scope.setSymbol((BaseTokenAST)i, f); }
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
                                        pkg.setLocalType((BaseTokenAST)i, sqlDatabaseType);
                                        beginScope(sqlDatabaseType);
                                        sqlDatabaseType.setScope(scope, pkg);
                                    }
            (sqlDatabaseFieldDefinition)+
        )                           { endScope(); }
    ;

sqlDatabaseFieldDefinition
    :   #(f:FIELD sqlTableDefinition[f])
    ;

sqlTableDefinition[AST astField]    { Field f = (Field) #astField; }
    :   sqlTableReference i:ID      {
                                        scope.setSymbol((BaseTokenAST)i, f);
                                    }
    ;

sqlTableReference
    :   typeSymbol
    ;

/**
 * serviceDeclaration.
 */
serviceDeclaration
    : #(s:SERVICE i:ID              {
                                        final ServiceType serviceType = (ServiceType)s;
                                        pkg.setLocalType((BaseTokenAST)i, serviceType);
                                        beginScope(serviceType);
                                        serviceType.setScope(scope, pkg);
                                    }
        (rpcDeclaration)*
      ) { endScope(); }
    ;

rpcDeclaration
    : #(r:RPC (STREAM)? typeSymbol i:ID (STREAM)? typeSymbol
                                    {
                                        scope.setSymbol((BaseTokenAST)i, r);
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
            pkg.addTypeReferenceToResolve((TypeReference)t, scope);
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
            scope.setSymbol((BaseTokenAST)i, p);
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
        #(NUMBITS expression)
    ;
