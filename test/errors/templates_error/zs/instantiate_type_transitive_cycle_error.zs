package instantiate_type_transitive_cycle_error;

instantiate Template<uint32> Other;
instantiate Other<uint32> Some;
instantiate Some<uint32> Template;
