<#include "FileHeader.inc.ftl">
<@file_header generatorDescription/>

<@include_guard_begin package.path, "PyBind11"/>

<@pybind_includes/>
<@namespace_begin package.path/>

void zserioInitPyBind11(::pybind11::module_ m);
<@namespace_end package.path/>

<@include_guard_end package.path, "PyBind11"/>