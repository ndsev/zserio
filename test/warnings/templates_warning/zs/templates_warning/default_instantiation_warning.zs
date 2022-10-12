package templates_warning.default_instantiation_warning;

import templates_warning.default_instantiation_subpackage1.*;
import templates_warning.default_instantiation_subpackage2.Subpackage2Template;
import templates_warning.default_instantiation_subpackage2.Subpackage2TemplateU32;
import templates_warning.default_instantiation_subpackage3.*;

struct Template<T>
{
    T field;
};

struct DefaultInstantiationWarning
{
    Template<uint32> defaultInstantiatedTemplate;
    Subpackage1Template<uint32> instantiatedSubpackage1Template;
    Subpackage1Template<string> defaultInstantiatedSubpackage1Template;
    Subpackage2Template<uint32> instantiatedSubpackage2Template;
    Subpackage2Template<string> defaultInstantiatedSubpackage2Template; // instantiation not imported
    Subpackage3Template<uint32> instantiatedSubpackage3Template;
};

instantiate Subpackage3Template<uint32> Subpackage3TemplateU32; // inner template not instantiated
