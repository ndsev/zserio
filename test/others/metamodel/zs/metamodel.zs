package metamodel;

struct Model
{
    int32        numTypes;
    Type         types[numTypes];

    int32        numFields;
    Field        fields[numFields];

    int32        numExpr;
    Expression   expressions[numExpr];

    int32        numPackages;
    Package      packages[numPackages];

    int32        numNames;
    string       names[numNames];
};

subtype int32 NameId;

struct Package
{
    int32    packageId;
    NameId   packageName;
    int32    numImports;
    Import   imports[numImports];
    int32    numTypes;
    TypeId   types[numTypes];
};

struct Import
{
    int8 dummy;
};

subtype int32 TypeId;

struct Type
{
    TypeId   typeId;
    NameId   typeName;
    Category category;
    int32    size;
    TypeInfo(category) info;
};

enum int8 Category
{
    BUILTIN,
    BITFIELD,
    ENUM,
    SUBTYPE,
    STRUCTURE,
    ARRAY
};

const uint32 USE_EXPRESSION = 0;

choice TypeInfo (Category category) on category
{
    case Category.BUILTIN:
        BuiltInInfo        builtInInfo;
    case Category.BITFIELD:
        BitfieldInfo       bitfieldInfo;
    case Category.ENUM:
        EnumInfo           enumInfo;
    case Category.SUBTYPE:
        string             subtypeInfo;
    case Category.STRUCTURE:
        StructureInfo      structureInfo;
    case Category.ARRAY:
        ArrayInfo          arrayInfo;
};

enum int8 BuiltInInfo
{
    INT8,
    INT16,
    INT32,
    INT64,
    UINT8,
    UINT16,
    UINT32,
    UINT64,
    STRING
};

subtype int32 FieldId;

// @todo HW: You don't need a TypeRef, simply use the TypeId
subtype int32 TypeRef;

// @todo HW: Use doc comments. Comments must precede commented item.
// @todo HW: Does not make sense. "USE_EXPRESSION" is undefined. The bitfield
// length is always an expression (which may be a constant Atom).
struct BitfieldInfo
{
    uint32        numBits;   // see Zserio Language Overview 3.2 (The length is not limited)
    ExpressionId  exprId    if USE_EXPRESSION == numBits;
};

struct EnumInfo
{
    // The bits length of the enum element
    // @todo HW This is not enough, the base type may also be signed or unsigned.
    // Use a TypeRef for the base type (which must be integral).
    int8 bits;

    // Lits of constants
    // @todo HW: rename to "items". "Constants" are different objects in
    // Zserio
    uint32     numConsts;


    // @todo HW: The idea was to have the metamodel byte-aligned so you can
    // parse it manually, if need be. Thus I would prefer to use the next larger
    // builtin type for the enum item (e.g. replace bit<18> by uint32).
    // To avoid a choice on the base type, you might even use int64 for all
    // enum item values (even if the base type is just bit:2). (This would
    // leave a gap for values >= 2^63, which probably do not occur in practice
    // anyway.
    bit<bits>  consts[numConsts];

    // @todo HW: optionally, add the symbolic name of the enum item
};

struct CompoundInfo
{
    // Parameters list
    int8       numParams;
    ParamInfo  params[numParams];

    // Fields list
    int16      numFields;
    FieldId    fields[numFields];
};

// @todo HW: Yes, names are not necessary for decoding an instance, but the
// may be helpful for diagnosis and logging. We should provide both options,
// i.e. a model with or without names. Make the nameId optional here by adding
// "if Model.numNames > 0"
struct ParamInfo
{
    TypeRef type;
    NameId  nameId;     // !!! Probably, it is not necessary to store name of parameters
};

subtype CompoundInfo StructureInfo;

struct ArrayInfo
{
    TypeRef       elementType;  // reference to the type of the array element
    ExpressionId  lengthExpr;   // defines array length calculation expression
};

struct Field
{
    int16    pos;
    NameId   name;
    TypeRef  type;
    bit:1    isOptional;
    bit:1    hasConstraint;
    bit:1    hasAlignment;
    bit:1    hasOffset;
    bit:1    hasArguments;

align(8):
    ExpressionId  optionalField if 0 != isOptional;
    ExpressionId  constraint    if 0 != hasConstraint;
    int32         alignment     if 0 != hasAlignment;
    ExpressionId  offset        if 0 != hasOffset;

    // @todo HW: An argument list is not an expression, and it should not be
    // treated as such, as it does not have a value or type.
    ExpressionId  arguments  if 0 != hasArguments;
};

subtype int32 ExpressionId;

//Expression
//{
//    TypeId  type;
//    bit:1   isConstant;
//    bit:7   arity;
//    ExpressionInfo info;
//    Value   value if isConstant;
//};

// @todo HW: It is ok to leave out the precalculated constant value for the
// moment, but you do need the type. An expression always has a type, which
// may be compound, so have to include the typereference
struct Expression
{
    Arity arity;
    ExpressionInfo(arity) info;
};

enum uint8 Arity
{
        ATOM,
        UNARY,
        BINARY,
        TERNARY
};

choice ExpressionInfo (Arity arity) on arity
{
    case Arity.ATOM:
        Atom    atom;
    case Arity.UNARY:
        Unary   unary;
    case Arity.BINARY:
        Binary  binary;
    case Arity.TERNARY:
        Ternary ternary;
};

struct Atom
{
        AtomType type;
    AtomInfo(type) info;
};

struct Unary
{
    UnaryOperator op;
    ExpressionId  arg1;
};

struct Binary
{
    BinaryOperator op;
    ExpressionId  arg1;
    ExpressionId  arg2;
};

struct Ternary
{
    TernaryOperator op;
    ExpressionId    arg1;
    ExpressionId    arg2;
    ExpressionId    arg3;
};

// @todo HW: I'm not sure if we need to distinguish local and global references.
enum int8 AtomType
{
    CONST,                              // define explicit and implicit constants (integer literal, enumeration and e.t.)
    LOCAL_FIELD_REF,    // Reference to the field of the current compound type
    GLOBAL_FIELD_REF,   // Reference to the global field
    PARAM_REF                   // Reference to the parameter
};

choice AtomInfo(AtomType type) on type
{
    case AtomType.CONST:
        Value          value;
    case AtomType.LOCAL_FIELD_REF:
        LocalFieldRef  lfieldRef;
    case AtomType.GLOBAL_FIELD_REF:
        GlobalFieldRef gfieldRef;
    case AtomType.PARAM_REF:
        ParamRef       paramRef;

    // Probably, the definition has not completed yet.
};

subtype int64 Value;

enum int8 UnaryOperator
{
        UPLUS,
        UMINUS,
        TILDE,
        BANG,
        LPAREN,
        INDEX,
        LENGTHOF,
        SIZEOF,
        BITSIZEOF,
        SUM,
        FUNCTIONCALL
};

enum int8 BinaryOperator
{
        COMMA,
        LOGICALOR,
        LOGICALAND,
        OR,
        XOR,
        AND,
        EQ,
        NE,
        LT,
        LE,
        GE,
        GT,
        LSHIFT,
        RSHIFT,
        PLUS,
        MINUS,
        MULTIPLY,
        DIVIDE,
        MODULO,
        ARRAYELEM,
        DOT
};

enum int8 TernaryOperator
{
        QUESTIONMARK
};

// @todo HW: You could simply use a FieldId here

struct LocalFieldRef
{
        int16   fieldIndex;             // field index in the current compound type
};

// @todo HW: The FieldId already is a global id (see Model), and the field
// index within the compound is implicit from the CompoundInfo. Maybe we should
// add a reference from the Field to its containing Compound.

struct GlobalFieldRef
{
        TypeRef type;                   // Reference to the compound type
        int16   fieldIndex;             // field index in the compound type
};

// @todo HW: fieldIndex does not make sense here
struct ParamRef
{
        int8    paramIndex;             // index  in the parameters list of the current compound type
        int16   fieldIndex;             // field index in the compound type
};
