/*
 * Tree walker for emitters.
 */
header
{
package zserio.antlr;
import zserio.emit.common.Emitter;
}

class ZserioEmitter extends TreeParser;

options
{
    importVocab=ZserioParser;
    defaultErrorHandler=false;
}

{
    private Emitter em;

    public void setEmitter(Emitter em)
    {
        this.em = em;
    }

    public void reportError(RecognitionException ex)
    {
        System.out.println(ex.toString());
        throw new RuntimeException(ex);
    }
}

/**
 * Root.
 */
root
    :   #(r:ROOT                    { em.beginRoot(r); }
            (translationUnit[#r])+
        )                           { em.endRoot(); }
    ;

translationUnit[AST r]
    :   #(u:TRANSLATION_UNIT        { em.beginTranslationUnit(r, u); }
            (packageDeclaration)?
            (importDeclaration)*
            (commandDeclaration)*
        )                           { em.endTranslationUnit(); }
    ;

packageDeclaration
    :   #(p:PACKAGE                 { em.beginPackage(p); }
            (ID)+
        )                           { em.endPackage(p); }
    ;

importDeclaration
    :   #(i:IMPORT                  { em.beginImport(i); }
            (ID)+
            (MULTIPLY)?
        )                           { em.endImport(); }
    ;

commandDeclaration
    :   constDeclaration |
        subtypeDeclaration |
        serviceDeclaration |
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
    :   #(c:CONST                   { em.beginConst(c); }
            definedType
            ID
            expression
        )                           { em.endConst(c); }
    ;

/**
 * subtypeDeclaration.
 */
subtypeDeclaration
    :   #(s:SUBTYPE                 { em.beginSubtype(s); }
            definedType
            ID
        )                           { em.endSubtype(s); }
    ;

serviceDeclaration
    :   #(s: SERVICE                { em.beginService(s); }
            ID
            (rpcDeclaration)*
        )                           { em.endService(s); }
    ;

rpcDeclaration
    :   #(r: RPC { em.beginRpc(r); }
        ID
        definedType
        definedType
        ) { em.endRpc(r); }
    ;

/**
 * structureDeclaration.
 */
structureDeclaration
    :   #(s:STRUCTURE               { em.beginStructure(s); }
            ID
            (parameterList)?
            (structureFieldDefinition)*
            (functionDefinition)*
        )                           { em.endStructure(s); }
    ;

structureMemberList
    :   #(MEMBERS (structureFieldDefinition)* (functionDefinition)*)
    ;

structureFieldDefinition
    :   #(f:FIELD                   { em.beginField(f); }
            (typeReference | fieldArrayType)
            ID
            (OPTIONAL)?
            (fieldInitializer)?
            (fieldOptionalClause)?
            (fieldConstraint)?
            (fieldOffset)?
            (fieldAlignment)?
        )                           { em.endField(f); }
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
    :   #(f:FUNCTION                { em.beginFunction(f); }
            definedType
            ID
            functionBody
        )                           { em.endFunction(f); }
    ;

functionBody
    : #(RETURN expression)
    ;

/**
 * choiceDeclaration.
 */
choiceDeclaration
    :   #(c:CHOICE                  { em.beginChoice(c); }
            ID
            parameterList
            expression
            (choiceCases)+
            (defaultChoice)?
            (functionDefinition)*
        )                           { em.endChoice(c); }
    ;

choiceCases
    :   #(CASE expression (CASE expression)* (choiceFieldDefinition)?)
    ;

choiceFieldDefinition
    :   #(f:FIELD
            (typeReference | fieldArrayType)
            ID                      { em.beginField(f); }
            (fieldConstraint)?
        )                           { em.endField(f); }
    ;

defaultChoice
    :   #(DEFAULT (choiceFieldDefinition)?)
    ;

/**
 * unionDeclaration.
 */
unionDeclaration
    :   #(u:UNION                   { em.beginUnion(u); }
            i:ID (parameterList)? (unionFieldDefinition)+ (functionDefinition)*
         )                          { em.endUnion(u); }
    ;

unionFieldDefinition
    :   choiceFieldDefinition
    ;

/**
 * enumDeclaration.
 */
enumDeclaration
    :   #(e:ENUM                    { em.beginEnumeration(e); }
            definedType
            ID
            (enumItem)+
        )                           { em.endEnumeration(e); }
    ;

enumItem
    :   #(i:ITEM                    { em.beginEnumItem(i); }
            ID
            (expression)?
        )                           { em.endEnumItem(i); }
    ;

/**
 * sqlTableDeclaration.
 */
sqlTableDeclaration
    :   #(t:SQL_TABLE               { em.beginSqlTable(t); }
            ID
            (ID)?
            (sqlTableFieldDefinition | sqlTableVirtualFieldDefinition)*
            (sqlConstraint)?
            (sqlWithoutRowId)?
        )                           { em.endSqlTable(t); }
    ;

sqlTableFieldDefinition
    :   #(f:FIELD                   { em.beginField(f); }
            typeReference
            ID
            (sqlConstraint)?
        )                           { em.endField(f); }
    ;

sqlConstraint
    :   #(SQL (STRING_LITERAL)+)
    ;

sqlTableVirtualFieldDefinition
    :   #(f:VFIELD                  { em.beginField(f); }
            typeReference
            ID
            (sqlConstraint)?
            SQL_VIRTUAL
        )                           { em.beginField(f); }
    ;

sqlWithoutRowId
    :   SQL_WITHOUT_ROWID
    ;

/**
 * sqlDatabaseDefinition.
 */
sqlDatabaseDefinition
    :   #(d:SQL_DATABASE            { em.beginSqlDatabase(d); }
            ID
            (sqlDatabaseFieldDefinition)+
        )                           { em.endSqlDatabase(d); }
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
    :   #(INST definedType typeArgumentList)
    ;

typeArgumentList
    :   (expression)+
    ;

/**
 * expression.
 */
expression
    :   atom |
        opExpression
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
    :   (
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
        )
    ;
