package transitive_cyclic_dependency_error;

subtype X Y;
subtype Y Z;
subtype Z X; // cycle!
