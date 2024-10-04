package clashing_identifiers.const_subtype_name_conflict_error;

const int32 Test = 13;

subtype string Test; // Test is already defined!
