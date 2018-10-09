/*
 * Tree walker for emitters.
 */
header
{
package zserio.antlr;

import zserio.ast.ChoiceType;
import zserio.ast.ConstType;
import zserio.ast.EnumType;
import zserio.ast.Import;
import zserio.ast.Package;
import zserio.ast.Root;
import zserio.ast.ServiceType;
import zserio.ast.SqlDatabaseType;
import zserio.ast.SqlTableType;
import zserio.ast.StructureType;
import zserio.ast.Subtype;
import zserio.ast.TranslationUnit;
import zserio.ast.UnionType;

import zserio.emit.common.Emitter;
}

class ZserioTreeWalker extends TreeParser;

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
    :   #(r:ROOT                    { em.beginRoot((Root)r); }
            (translationUnit)+
        )                           { em.endRoot((Root)r); }
    ;

translationUnit
    :   #(u:TRANSLATION_UNIT        { em.beginTranslationUnit((TranslationUnit)u); }
            (packageDeclaration)?
            (importDeclaration)*
            (commandDeclaration)*
        )                           { em.endTranslationUnit((TranslationUnit)u); }
    ;

packageDeclaration
    :   #(p:PACKAGE                 { em.beginPackage((Package)p); }
            (ID)* // default package does not have IDs
        )
    ;

importDeclaration
    :   #(i:IMPORT                  { em.beginImport((Import)i); }
            (ID)+
            (MULTIPLY)?
        )
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
    :   #(c:CONST                   { em.beginConst((ConstType)c); }
            definedType
            ID
            expression
        )
    ;

/**
 * subtypeDeclaration.
 */
subtypeDeclaration
    :   #(s:SUBTYPE                 { em.beginSubtype((Subtype)s); }
            definedType
            ID
        )
    ;

/**
 * structureDeclaration.
 */
structureDeclaration
    :   #(s:STRUCTURE               { em.beginStructure((StructureType)s); }
            ID
            (parameterList)?
            (structureFieldDefinition)*
            (functionDefinition)*
        )
    ;

structureMemberList
    :   #(MEMBERS (structureFieldDefinition)* (functionDefinition)*)
    ;

structureFieldDefinition
    :   #(f:FIELD
            (typeReference | fieldArrayType)
            ID
            (OPTIONAL)?
            (fieldInitializer)?
            (fieldOptionalClause)?
            (fieldConstraint)?
            (fieldOffset)?
            (fieldAlignment)?
        )
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
    :   #(f:FUNCTION
            definedType
            ID
            functionBody
        )
    ;

functionBody
    : #(RETURN expression)
    ;

/**
 * choiceDeclaration.
 */
choiceDeclaration
    :   #(c:CHOICE                  { em.beginChoice((ChoiceType)c); }
            ID
            parameterList
            expression
            (choiceCases)+
            (defaultChoice)?
            (functionDefinition)*
        )
    ;

choiceCases
    :   #(CASE expression (CASE expression)* (choiceFieldDefinition)?)
    ;

choiceFieldDefinition
    :   #(f:FIELD
            (typeReference | fieldArrayType)
            ID
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
    :   #(u:UNION                   { em.beginUnion((UnionType)u); }
            i:ID (parameterList)? (unionFieldDefinition)+ (functionDefinition)*
         )
    ;

unionFieldDefinition
    :   choiceFieldDefinition
    ;

/**
 * enumDeclaration.
 */
enumDeclaration
    :   #(e:ENUM                    { em.beginEnumeration((EnumType)e); }
            definedType
            ID
            (enumItem)+
        )
    ;

enumItem
    :   #(i:ITEM 
            ID
            (expression)?
        )
    ;

/**
 * sqlTableDeclaration.
 */
sqlTableDeclaration
    :   #(t:SQL_TABLE               { em.beginSqlTable((SqlTableType)t); }
            ID
            (ID)?
            (sqlTableFieldDefinition | sqlTableVirtualFieldDefinition)*
            (sqlConstraint)?
            (sqlWithoutRowId)?
        )
    ;

sqlTableFieldDefinition
    :   #(f:FIELD 
            typeReference
            ID
            (sqlConstraint)?
        )
    ;

sqlConstraint
    :   #(SQL (STRING_LITERAL)+)
    ;

sqlTableVirtualFieldDefinition
    :   #(f:VFIELD
            typeReference
            ID
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
    :   #(d:SQL_DATABASE            { em.beginSqlDatabase((SqlDatabaseType)d); }
            ID
            (sqlDatabaseFieldDefinition)+
        )
    ;

sqlDatabaseFieldDefinition
    :   #(FIELD sqlTableDefinition)
    ;

sqlTableDefinition
    :   sqlTableReference ID
    ;

sqlTableReference
    :   typeSymbol
    ;

/**
 * serviceDeclaration.
 */
serviceDeclaration
    :   #(s:SERVICE                 { em.beginService((ServiceType)s); }
            ID
            (rpcDeclaration)*
        )
    ;

rpcDeclaration
    :   #(r: RPC
        (STREAM)? typeSymbol
        ID
        (STREAM)? typeSymbol
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
