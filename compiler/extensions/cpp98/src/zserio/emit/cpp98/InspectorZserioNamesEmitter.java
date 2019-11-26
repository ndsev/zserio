package zserio.emit.cpp98;

import java.util.ArrayList;
import java.util.List;

import zserio.ast.ChoiceType;
import zserio.ast.CompoundType;
import zserio.ast.EnumType;
import zserio.ast.Field;
import zserio.ast.Function;
import zserio.ast.Root;
import zserio.ast.SqlTableType;
import zserio.ast.StructureType;
import zserio.ast.UnionType;
import zserio.ast.ZserioType;
import zserio.emit.common.ZserioEmitException;
import zserio.tools.Parameters;

public class InspectorZserioNamesEmitter extends CppDefaultEmitter
{
    public InspectorZserioNamesEmitter(String outPathName, Parameters extensionParameters)
    {
        super(outPathName, extensionParameters);
    }

    @Override
    public void beginStructure(StructureType structureType) throws ZserioEmitException
    {
        beginCompoundType(structureType);
    }

    @Override
    public void beginChoice(ChoiceType choiceType) throws ZserioEmitException
    {
        beginCompoundType(choiceType);
    }

    @Override
    public void beginUnion(UnionType unionType) throws ZserioEmitException
    {
        beginCompoundType(unionType);
    }

    @Override
    public void beginEnumeration(EnumType enumType) throws ZserioEmitException
    {
        if (getWithInspectorCode())
            enumTypes.add(enumType);
    }

    @Override
    public void beginSqlTable(SqlTableType sqlTableType) throws ZserioEmitException
    {
        if (getWithInspectorCode())
        {
            for (Field field : sqlTableType.getFields())
            {
                // we need only compound types from tables
                final ZserioType fieldBaseType = field.getTypeInstantiation().getBaseType();
                if (fieldBaseType instanceof CompoundType)
                    fields.add(field);
            }
        }
    }

    @Override
    public void endRoot(Root root) throws ZserioEmitException
    {
        if (!fields.isEmpty() || !functions.isEmpty() || !enumTypes.isEmpty())
        {
            final InspectorZserioNamesTemplateData namesTemplateData =
                    new InspectorZserioNamesTemplateData(getTemplateDataContext(), fields, functions,
                            enumTypes);
            processHeaderTemplateToRootDir(NAMES_TEMPLATE_HEADER_NAME, namesTemplateData,
                    NAMES_OUTPUT_FILE_NAME_ROOT);
            processSourceTemplateToRootDir(NAMES_TEMPLATE_SOURCE_NAME, namesTemplateData,
                    NAMES_OUTPUT_FILE_NAME_ROOT);

            final InspectorZserioTypeNamesTemplateData typeNamesTemplateData =
                    new InspectorZserioTypeNamesTemplateData(getTemplateDataContext(), fields, functions,
                            enumTypes);
            processHeaderTemplateToRootDir(TYPE_NAMES_TEMPLATE_HEADER_NAME, typeNamesTemplateData,
                    TYPE_NAMES_OUTPUT_FILE_NAME_ROOT);
            processSourceTemplateToRootDir(TYPE_NAMES_TEMPLATE_SOURCE_NAME, typeNamesTemplateData,
                    TYPE_NAMES_OUTPUT_FILE_NAME_ROOT);

        }
    }

    private void beginCompoundType(CompoundType compoundType)
    {
        if (getWithInspectorCode())
        {
            fields.addAll(compoundType.getFields());
            functions.addAll(compoundType.getFunctions());
        }
    }

    private static final String NAMES_TEMPLATE_HEADER_NAME = "InspectorZserioNames.h.ftl";
    private static final String NAMES_TEMPLATE_SOURCE_NAME = "InspectorZserioNames.cpp.ftl";
    private static final String NAMES_OUTPUT_FILE_NAME_ROOT = "InspectorZserioNames";

    private static final String TYPE_NAMES_TEMPLATE_HEADER_NAME = "InspectorZserioTypeNames.h.ftl";
    private static final String TYPE_NAMES_TEMPLATE_SOURCE_NAME = "InspectorZserioTypeNames.cpp.ftl";
    private static final String TYPE_NAMES_OUTPUT_FILE_NAME_ROOT = "InspectorZserioTypeNames";

    private final List<Field> fields = new ArrayList<Field>();
    private final List<Function> functions = new ArrayList<Function>();
    private final List<EnumType> enumTypes = new ArrayList<EnumType>();
}
