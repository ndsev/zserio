package zserio.emit.doc;

import java.util.Locale;

import zserio.ast.ArrayInstantiation;
import zserio.ast.ArrayType;
import zserio.ast.AstNode;
import zserio.ast.BitmaskType;
import zserio.ast.BitmaskValue;
import zserio.ast.BooleanType;
import zserio.ast.BuiltInType;
import zserio.ast.ChoiceCase;
import zserio.ast.ChoiceCaseExpression;
import zserio.ast.ChoiceDefault;
import zserio.ast.ChoiceType;
import zserio.ast.Constant;
import zserio.ast.DynamicBitFieldType;
import zserio.ast.EnumItem;
import zserio.ast.EnumType;
import zserio.ast.Expression;
import zserio.ast.ExternType;
import zserio.ast.Field;
import zserio.ast.FixedBitFieldType;
import zserio.ast.FloatType;
import zserio.ast.Function;
import zserio.ast.InstantiateType;
import zserio.ast.Package;
import zserio.ast.Parameter;
import zserio.ast.PubsubMessage;
import zserio.ast.PubsubType;
import zserio.ast.ServiceMethod;
import zserio.ast.ServiceType;
import zserio.ast.SqlConstraint;
import zserio.ast.SqlDatabaseType;
import zserio.ast.SqlTableType;
import zserio.ast.StdIntegerType;
import zserio.ast.StringType;
import zserio.ast.StructureType;
import zserio.ast.Subtype;
import zserio.ast.TemplateArgument;
import zserio.ast.TemplateParameter;
import zserio.ast.TypeInstantiation;
import zserio.ast.TypeReference;
import zserio.ast.UnionType;
import zserio.ast.VarIntegerType;
import zserio.ast.ZserioAstDefaultVisitor;
import zserio.ast.ZserioType;
import zserio.emit.common.PackageMapper;
import zserio.emit.common.ZserioEmitException;

class SymbolTemplateDataMapper
{
    public SymbolTemplateDataMapper(PackageMapper packageMapper, String htmlContentDirectory)
    {
        // TODO[mikir] this.packageMapper = packageMapper;
        this.htmlContentDirectory = htmlContentDirectory;
    }

    public SymbolTemplateData getSymbol(AstNode node) throws ZserioEmitException
    {
        final SymbolTemplateDataVisitor visitor = new SymbolTemplateDataVisitor();
        node.accept(visitor);

        final SymbolTemplateData symbolTemplateData = visitor.getSymbolTemplateData();
        if (symbolTemplateData == null)
        {
            // TODO[mikir] This could be handled as a warning and return only name
            throw new ZserioEmitException("Unhandled AST node '" + node.getClass().getName() +
                    "' in SymbolTemplateDataMapper!");
        }

        return symbolTemplateData;
    }

    private class SymbolTemplateDataVisitor extends ZserioAstDefaultVisitor
    {
        public SymbolTemplateData getSymbolTemplateData()
        {
            return symbolTemplateData;
        }

        @Override
        public void visitConstant(Constant constant)
        {
            symbolTemplateData = createSymbolTemplateData(constant.getName(), constant.getPackage(),
                    "Constant");
        }

        @Override
        public void visitSubtype(Subtype subtype)
        {
            symbolTemplateData = createSymbolTemplateData(subtype, "Subtype");
        }

        @Override
        public void visitStructureType(StructureType structureType)
        {
            symbolTemplateData = createSymbolTemplateData(structureType, "Structure");
        }

        @Override
        public void visitChoiceType(ChoiceType choiceType)
        {
            symbolTemplateData = createSymbolTemplateData(choiceType, "Choice");
        }

        @Override
        public void visitUnionType(UnionType unionType)
        {
            symbolTemplateData = createSymbolTemplateData(unionType, "Union");
        }

        @Override
        public void visitEnumType(EnumType enumType)
        {
            symbolTemplateData = createSymbolTemplateData(enumType, "Enum");
        }

        @Override
        public void visitBitmaskType(BitmaskType bitmaskType)
        {
            symbolTemplateData = createSymbolTemplateData(bitmaskType, "Bitmask");
        }

        @Override
        public void visitSqlTableType(SqlTableType sqlTableType)
        {
            symbolTemplateData = createSymbolTemplateData(sqlTableType, "SqlTable");
        }

        @Override
        public void visitSqlDatabaseType(SqlDatabaseType sqlDatabaseType)
        {
            symbolTemplateData = createSymbolTemplateData(sqlDatabaseType, "SqlDatabase");
        }

        @Override
        public void visitServiceType(ServiceType serviceType)
        {
            symbolTemplateData = createSymbolTemplateData(serviceType, "Service");
        }

        @Override
        public void visitPubsubType(PubsubType pubsubType)
        {
            symbolTemplateData = createSymbolTemplateData(pubsubType, "Pubsub");
        }

        @Override
        public void visitField(Field field)
        {}

        @Override
        public void visitChoiceCase(ChoiceCase choiceCase)
        {}

        @Override
        public void visitChoiceCaseExpression(ChoiceCaseExpression choiceCaseExpression)
        {}

        @Override
        public void visitChoiceDefault(ChoiceDefault choiceDefault)
        {}

        @Override
        public void visitEnumItem(EnumItem enumItem)
        {}

        @Override
        public void visitBitmaskValue(BitmaskValue bitmaskValue)
        {}

        @Override
        public void visitSqlConstraint(SqlConstraint sqlConstraint)
        {}

        @Override
        public void visitServiceMethod(ServiceMethod serviceMethod)
        {}

        @Override
        public void visitPubsubMessage(PubsubMessage pubsubMessage)
        {}

        @Override
        public void visitFunction(Function function)
        {}

        @Override
        public void visitParameter(Parameter parameter)
        {}

        @Override
        public void visitExpression(Expression expresssion)
        {}

        @Override
        public void visitTypeReference(TypeReference typeReference)
        {
            final ZserioType type = typeReference.getType();
            type.accept(this);
        }

        @Override
        public void visitTypeInstantiation(TypeInstantiation typeInstantiation)
        {
            if (typeInstantiation instanceof ArrayInstantiation)
            {
                final ArrayInstantiation arrayInstantiation = (ArrayInstantiation)typeInstantiation;
                final ZserioType elementType = arrayInstantiation.getElementTypeInstantiation().getType();
                elementType.accept(this);
                if (symbolTemplateData != null)
                {
                    symbolTemplateData = new SymbolTemplateData(symbolTemplateData.getName(), "arrayLink",
                            "Array of " + symbolTemplateData.getHtmlTitle(), symbolTemplateData.getHtmlLink());
                }
            }
            else
            {
                final ZserioType type = typeInstantiation.getType();
                type.accept(this);
            }
        }

        @Override
        public void visitArrayType(ArrayType arrayType)
        {}

        @Override
        public void visitStdIntegerType(StdIntegerType stdIntegerType)
        {
            symbolTemplateData = createSymbolTemplateData(stdIntegerType);
        }

        @Override
        public void visitVarIntegerType(VarIntegerType varIntegerType)
        {
            symbolTemplateData = createSymbolTemplateData(varIntegerType);
        }

        @Override
        public void visitFixedBitFieldType(FixedBitFieldType fixedFieldTypeType)
        {
            symbolTemplateData = createSymbolTemplateData(fixedFieldTypeType);
        }

        @Override
        public void visitDynamicBitFieldType(DynamicBitFieldType dynamicBitFieldType)
        {
            symbolTemplateData = createSymbolTemplateData(dynamicBitFieldType);
        }

        @Override
        public void visitBooleanType(BooleanType booleanType)
        {
            symbolTemplateData = createSymbolTemplateData(booleanType);
        }

        @Override
        public void visitStringType(StringType stringType)
        {
            symbolTemplateData = createSymbolTemplateData(stringType);
        }

        @Override
        public void visitFloatType(FloatType floatType)
        {
            symbolTemplateData = createSymbolTemplateData(floatType);
        }

        @Override
        public void visitExternType(ExternType externType)
        {
            symbolTemplateData = createSymbolTemplateData(externType);
        }

        @Override
        public void visitTemplateParameter(TemplateParameter templateParameter)
        {}

        @Override
        public void visitTemplateArgument(TemplateArgument templateArgument)
        {}

        @Override
        public void visitInstantiateType(InstantiateType templateInstantiation)
        {
            templateInstantiation.visitChildren(this);
        }

        private SymbolTemplateData createSymbolTemplateData(String name, Package pkg, String typeName)
        {
            final String htmlClassPrefix = typeName.substring(0, 1).toLowerCase(Locale.ENGLISH) +
                    typeName.substring(1);
            final String htmlClass = htmlClassPrefix + "Link";
            // TODO[mikir] final String packageName = packageMapper.getPackageName(pkg).toString();
            final String packageName = pkg.getPackageName().toString();
            final String htmlTitle = typeName + " defined in " + packageName;
            // TODO[mikir] Use joiner to fix empty string in htmlContentDirectory
            final String htmlLinkPage = htmlContentDirectory + "/" + packageName + ".html";
            final String htmlLinkAnchor = typeName + "_" + name;

            return new SymbolTemplateData(name, htmlClass, htmlTitle, htmlLinkPage, htmlLinkAnchor);
        }

        private SymbolTemplateData createSymbolTemplateData(BuiltInType type)
        {
            final String htmlClass = "builtInType";
            final String htmlTitle = "Built-in type";

            return new SymbolTemplateData(type.getName(), htmlClass, htmlTitle);
        }

        private SymbolTemplateData createSymbolTemplateData(ZserioType type, String typeName)
        {
            return createSymbolTemplateData(type.getName(), type.getPackage(), typeName);
        }

        private SymbolTemplateData symbolTemplateData;
    }

 // TODO[mikir]     private final PackageMapper packageMapper;
    private final String htmlContentDirectory;
}
