/*
 * Tree walker to evaluate expressions.
 */
header
{
    package zserio.antlr;
    import zserio.ast.*;
}

class ExpressionEvaluator extends TreeParser;

options
{
    importVocab=ZserioParser;
    defaultErrorHandler=false;
}

{
    private boolean forceEvaluation = false;

    public void setForceEvaluation(boolean forceEvaluation)
    {
        this.forceEvaluation = forceEvaluation;
    }

    private void evaluateOne(Expression e) throws RecognitionException
    {
        e.evaluateOne(forceEvaluation);
    }
}

/**
 * Root.
 */
root
    :   #(ROOT (translationUnit)+ )
    ;

translationUnit
    :   #(TRANSLATION_UNIT (packageDeclaration)? (importDeclaration)* (commandDeclaration)*)
    ;

packageDeclaration
    :   #(PACKAGE (ID)+)
    ;

importDeclaration
    :   #(IMPORT (ID)+ (MULTIPLY)?)
    ;

commandDeclaration
    :   constDeclaration |
        subtypeDeclaration |
        rpcDeclaration |
        structureDeclaration |
        choiceDeclaration |
        unionDeclaration |
        enumDeclaration |
        sqlTableDeclaration |
        sqlDatabaseDefinition
    ;

/**
 * constDeclaration.
 */
constDeclaration
    :   #(CONST definedType ID expression)
    ;

/**
 * subtypeDeclaration.
 */
subtypeDeclaration
    :   #(SUBTYPE definedType ID)
    ;

rpcDeclaration
    :   #(RPC ID definedType definedType)
    ;

/**
 * structureDeclaration.
 */
structureDeclaration
    :   #(STRUCTURE ID (parameterList)? (structureFieldDefinition)* (functionDefinition)*)
    ;

structureFieldDefinition
    :   #(FIELD (typeReference | fieldArrayType) ID (OPTIONAL)? (fieldInitializer)? (fieldOptionalClause)?
                (fieldConstraint)? (fieldOffset)? (fieldAlignment)?)
    ;

fieldArrayType
    :   #(ARRAY typeReference fieldArrayRange (IMPLICIT)?)
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
    :   #(FUNCTION definedType ID functionBody)
    ;

functionBody
    :   #(RETURN expression)
    ;

/**
 * choiceDeclaration.
 */
choiceDeclaration
    :   #(c:CHOICE
            ID
            parameterList
            e:expression
            {
                // if choice selector is enumeration, choice scope should contain enumeration items as well
                ZserioType exprType = ((Expression)e).getExprZserioType();
                if (exprType instanceof EnumType)
                    ((ChoiceType)c).addScopeForCaseExpressions(((EnumType)exprType).getScope());
            }
            (choiceCases)+
            (defaultChoice)?
            (functionDefinition)*
        )
    ;

choiceCases
    :   #(CASE expression (CASE expression)* (choiceFieldDefinition)?)
    ;

choiceFieldDefinition
    :   #(FIELD (typeReference | fieldArrayType) ID (fieldConstraint)?)
    ;

defaultChoice
    :   #(DEFAULT (choiceFieldDefinition)?)
    ;

/**
 * unionDeclaration.
 */
unionDeclaration
    :   #(u:UNION i:ID (parameterList)? (unionFieldDefinition)+ (functionDefinition)*)
    ;

unionFieldDefinition
    :   choiceFieldDefinition
    ;

/**
 * enumDeclaration.
 */
enumDeclaration
    :   #(e:ENUM definedType ID (enumItem)+)
        {
            // evaluate all item values
            ((EnumType)e).evaluateItemValues();
        }
    ;

enumItem
    :   #(f:ITEM ID (e:expression)?)
    ;

/**
 * sqlTableDeclaration.
 */
sqlTableDeclaration
    :   #(SQL_TABLE ID (ID)? (sqlTableFieldDefinition | sqlTableVirtualFieldDefinition)* (sqlConstraint)?
            (sqlWithoutRowId)?)
    ;

sqlTableFieldDefinition
    :   #(FIELD typeReference ID (sqlConstraint)?)
    ;

sqlConstraint
    :   #(SQL (STRING_LITERAL)+)
    ;

sqlTableVirtualFieldDefinition
    :   #(VFIELD typeReference ID (sqlConstraint)? SQL_VIRTUAL)
    ;

sqlWithoutRowId
    :   SQL_WITHOUT_ROWID
    ;

/**
 * sqlDatabaseDefinition.
 */
sqlDatabaseDefinition
    :   #(SQL_DATABASE ID (sqlDatabaseFieldDefinition)+)
    ;

sqlDatabaseFieldDefinition
    :   #(FIELD sqlTableDefinition)
    ;

sqlTableDefinition
    :   sqlTableReference ID
    ;

sqlTableReference
    :   #(TYPEREF ID)
    ;

/**
 * definedType.
 */
definedType
    :   typeSymbol |
        builtinType
    ;

typeSymbol
    :   #(TYPEREF ID (DOT ID)*)
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
    :   #(b:BIT expression)
        {
            // evaluate bit sizes
            ((BitFieldType)b).evaluateBitSizes();
        }
    ;

signedBitField
    :   #(i:INT expression)
        {
            // evaluate bit sizes
            ((BitFieldType)i).evaluateBitSizes();
        }
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
    :   #(PARAM definedType ID)
    ;


/**
 * typeReference.
 */
typeReference
    :   paramTypeInstantiation |
        definedType
    ;

paramTypeInstantiation
    :   #(i:INST definedType typeArgumentList)
        {
            // evaluate base type
            ((TypeInstantiation)i).evaluateBaseType();
        }
    ;

typeArgumentList
    :   (expression)+
    ;

/**
 * expression.
 */
expression
    :   nestedExpression
        {
            // check evaluation of top level expression because package without type cannot be detected
            ((Expression)#expression).checkEvaluation();
        }
    ;

nestedExpression
    :   (
            atom |
            opExpression
        )
        {
            // call evaluation of one expression
            evaluateOne((Expression)#nestedExpression);
        }
    ;

atom
    :
        ID |
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
    :   #(QUESTIONMARK nestedExpression nestedExpression nestedExpression) |
        #(LOGICALOR nestedExpression nestedExpression) |
        #(LOGICALAND nestedExpression nestedExpression) |
        #(OR nestedExpression nestedExpression) |
        #(XOR nestedExpression nestedExpression) |
        #(AND nestedExpression nestedExpression) |
        #(EQ nestedExpression nestedExpression) |
        #(NE nestedExpression nestedExpression) |
        #(LT nestedExpression nestedExpression) |
        #(GT nestedExpression nestedExpression) |
        #(LE nestedExpression nestedExpression) |
        #(GE nestedExpression nestedExpression) |
        #(LSHIFT nestedExpression nestedExpression) |
        #(RSHIFT nestedExpression nestedExpression) |
        #(PLUS nestedExpression nestedExpression) |
        #(MINUS nestedExpression nestedExpression) |
        #(MULTIPLY nestedExpression nestedExpression) |
        #(DIVIDE nestedExpression nestedExpression) |
        #(MODULO nestedExpression nestedExpression) |
        #(UPLUS nestedExpression) |
        #(UMINUS nestedExpression) |
        #(TILDE nestedExpression) |
        #(BANG nestedExpression) |
        #(LENGTHOF nestedExpression) |
        #(DOT (nestedExpression)+) |
        #(ARRAYELEM nestedExpression nestedExpression) |
        #(LPAREN nestedExpression) |
        #(FUNCTIONCALL nestedExpression) |
        #(EXPLICIT ID) |
        #(SUM nestedExpression) |
        #(NUMBITS nestedExpression)
    ;
