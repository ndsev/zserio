package identifiers_error.clashing_identifiers;

import static org.junit.jupiter.api.Assertions.*;

import java.io.IOException;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import test_utils.ZserioErrorOutput;

public class ClashingIdentifiersErrorTest
{
    @BeforeAll
    public static void readZserioErrors() throws IOException
    {
        zserioErrors = new ZserioErrorOutput();
    }

    @Test
    public void choiceChoiceNameConflict()
    {
        final String errors[] = {"choice_choice_name_conflict_error.zs:3:8:     First defined here",
                "choice_choice_name_conflict_error.zs:11:8: 'Test' is already defined in this package!"};
        assertTrue(zserioErrors.isPresent(errors));
    }

    @Test
    public void choiceStructureNameConflict()
    {
        final String errors[] = {"choice_structure_name_conflict_error.zs:3:8:     First defined here",
                "choice_structure_name_conflict_error.zs:11:8: 'Test' is already defined in this package!"};
        assertTrue(zserioErrors.isPresent(errors));
    }

    @Test
    public void choiceUnionNameConflict()
    {
        final String errors[] = {"choice_union_name_conflict_error.zs:3:8:     First defined here",
                "choice_union_name_conflict_error.zs:11:7: 'Test' is already defined in this package!"};
        assertTrue(zserioErrors.isPresent(errors));
    }

    @Test
    public void clashingChoiceParamNames()
    {
        final String errors[] = {
                "clashing_choice_param_names_error.zs:8:21:     Conflicting symbol defined here",
                "clashing_choice_param_names_error.zs:8:35: "
                        + "Symbol 'Param' differs only in a case of its first letter!"};
        assertTrue(zserioErrors.isPresent(errors));
    }

    @Test
    public void clashingIdentifiersWithinPackage()
    {
        final String errors[] = {
                "clashing_identifiers_within_package_error.zs:3:8:     Conflicting symbol defined here",
                "clashing_identifiers_within_package_error.zs:8:13: "
                        + "Symbol 'TEST' is not unique (case insensitive) within this package!"};
        assertTrue(zserioErrors.isPresent(errors));
    }

    @Test
    public void clashingPubsubMessageNames()
    {
        final String errors[] = {
                "clashing_pubsub_message_names_error.zs:10:29:     Conflicting symbol defined here",
                "clashing_pubsub_message_names_error.zs:11:29: "
                        + "Symbol 'X_message' differs only in a case of its first letter!",
        };
        assertTrue(zserioErrors.isPresent(errors));
    }

    @Test
    public void clashingServiceMethodNames()
    {
        final String errors[] = {
                "clashing_service_method_names_error.zs:10:10:     Conflicting symbol defined here",
                "clashing_service_method_names_error.zs:11:10: "
                        + "Symbol 'X_method' differs only in a case of its first letter!"};
        assertTrue(zserioErrors.isPresent(errors));
    }

    @Test
    public void clashingSqlDatabaseTableNames()
    {
        final String errors[] = {
                "clashing_sql_database_table_names_error.zs:10:11:     Conflicting symbol defined here",
                "clashing_sql_database_table_names_error.zs:11:11: "
                        + "Symbol 'tBl_x' is not unique (case insensitive) within the SQL type!"};
        assertTrue(zserioErrors.isPresent(errors));
    }

    @Test
    public void clashingSqlTableColumnNames()
    {
        final String errors[] = {
                "clashing_sql_table_column_names_error.zs:5:11:     Conflicting symbol defined here",
                "clashing_sql_table_column_names_error.zs:6:11: "
                        + "Symbol 'fieldabc' is not unique (case insensitive) within the SQL type!"};
        assertTrue(zserioErrors.isPresent(errors));
    }

    @Test
    public void clashingStructureFieldAndParamNames()
    {
        final String errors[] = {
                "clashing_structure_field_and_param_names_error.zs:3:24:     Conflicting symbol defined here",
                "clashing_structure_field_and_param_names_error.zs:5:11: "
                        + "Symbol 'value' differs only in a case of its first letter!"};
        assertTrue(zserioErrors.isPresent(errors));
    }

    @Test
    public void clashingStructureFieldNames()
    {
        final String errors[] = {
                "clashing_structure_field_names_error.zs:5:11:     Conflicting symbol defined here",
                "clashing_structure_field_names_error.zs:6:11: "
                        + "Symbol 'Field1' differs only in a case of its first letter!"};
        assertTrue(zserioErrors.isPresent(errors));
    }

    @Test
    public void clashingStructureFunctionNames()
    {
        final String errors[] = {
                "clashing_structure_function_names_error.zs:5:20:     Conflicting symbol defined here",
                "clashing_structure_function_names_error.zs:10:20: "
                        + "Symbol 'Func1' differs only in a case of its first letter!"};
        assertTrue(zserioErrors.isPresent(errors));
    }

    @Test
    public void clashingTypeNamesWithinPackage()
    {
        final String errors[] = {
                "clashing_type_names_within_package_error.zs:3:8:     Conflicting symbol defined here",
                "clashing_type_names_within_package_error.zs:8:7: "
                        + "Symbol 'TEst' is not unique (case insensitive) within this package!"};
        assertTrue(zserioErrors.isPresent(errors));
    }

    @Test
    public void clashingUnionFieldNames()
    {
        final String errors[] = {
                "clashing_union_field_names_error.zs:5:11:     Conflicting symbol defined here",
                "clashing_union_field_names_error.zs:6:11: "
                        + "Symbol 'Field1' differs only in a case of its first letter!"};
        assertTrue(zserioErrors.isPresent(errors));
    }

    @Test
    public void constConstNameConflict()
    {
        final String errors[] = {"const_const_name_conflict_error.zs:3:13:     First defined here",
                "const_const_name_conflict_error.zs:4:14: 'Test' is already defined in this package!"};
        assertTrue(zserioErrors.isPresent(errors));
    }

    @Test
    public void constServiceNameConflict()
    {
        final String errors[] = {"const_service_name_conflict_error.zs:3:14:     First defined here",
                "const_service_name_conflict_error.zs:15:9: 'Math' is already defined in this package!"};
        assertTrue(zserioErrors.isPresent(errors));
    }

    @Test
    public void constStructureNameConflict()
    {
        final String errors[] = {"const_structure_name_conflict_error.zs:3:13:     First defined here",
                "const_structure_name_conflict_error.zs:5:8: 'Test' is already defined in this package!"};
        assertTrue(zserioErrors.isPresent(errors));
    }

    @Test
    public void constSubtypeNameConflict()
    {
        final String errors[] = {"const_subtype_name_conflict_error.zs:3:13:     First defined here",
                "const_subtype_name_conflict_error.zs:5:16: 'Test' is already defined in this package!"};
        assertTrue(zserioErrors.isPresent(errors));
    }

    @Test
    public void serviceServiceNameConflict()
    {
        final String errors[] = {"service_service_name_conflict_error.zs:13:9:     First defined here",
                "service_service_name_conflict_error.zs:18:9: 'Math' is already defined in this package!"};
        assertTrue(zserioErrors.isPresent(errors));
    }

    @Test
    public void structureEnumNameConflict()
    {
        final String errors[] = {"structure_enum_name_conflict_error.zs:3:8:     First defined here",
                "structure_enum_name_conflict_error.zs:8:12: 'Test' is already defined in this package!"};
        assertTrue(zserioErrors.isPresent(errors));
    }

    @Test
    public void structureNameFirstLowerCase()
    {
        final String error = "structure_name_first_lower_case_error.zs:3:8: "
                + "Symbols defined in a package must start with an upper case letter!";
        assertTrue(zserioErrors.isPresent(error));
    }

    @Test
    public void structureParamStructureNameConflict()
    {
        final String errors[] = {"structure_param_structure_name_conflict_error.zs:3:8:     First defined here",
                "structure_param_structure_name_conflict_error.zs:8:8: 'Test' is already defined in this package!"};
        assertTrue(zserioErrors.isPresent(errors));
    }

    @Test
    public void structureServiceNameConflict()
    {
        final String errors[] = {"structure_service_name_conflict_error.zs:3:8:     First defined here",
                "structure_service_name_conflict_error.zs:19:9: 'Math' is already defined in this package!"};
        assertTrue(zserioErrors.isPresent(errors));
    }

    @Test
    public void structureStructureNameConflict()
    {
        final String errors[] = {"structure_structure_name_conflict_error.zs:3:8:     First defined here",
                "structure_structure_name_conflict_error.zs:8:8: 'Test' is already defined in this package!"};
        assertTrue(zserioErrors.isPresent(errors));
    }

    @Test
    public void structureUnionNameConflict()
    {
        final String errors[] = {"structure_union_name_conflict_error.zs:3:8:     First defined here",
                "structure_union_name_conflict_error.zs:8:7: 'Test' is already defined in this package!"};
        assertTrue(zserioErrors.isPresent(errors));
    }

    @Test
    public void subtypeStructureNameConflict()
    {
        final String errors[] = {"subtype_structure_name_conflict_error.zs:3:15:     First defined here",
                "subtype_structure_name_conflict_error.zs:5:8: 'Test' is already defined in this package!"};
        assertTrue(zserioErrors.isPresent(errors));
    }

    @Test
    public void subtypeSubtypeNameConflict()
    {
        final String errors[] = {"subtype_subtype_name_conflict_error.zs:3:15:     First defined here",
                "subtype_subtype_name_conflict_error.zs:5:16: 'Test' is already defined in this package!"};
        assertTrue(zserioErrors.isPresent(errors));
    }

    @Test
    public void unionUnionNameConflict()
    {
        final String errors[] = {"union_union_name_conflict_error.zs:3:7:     First defined here",
                "union_union_name_conflict_error.zs:9:7: 'Test' is already defined in this package!"};
        assertTrue(zserioErrors.isPresent(errors));
    }

    private static ZserioErrorOutput zserioErrors;
}
