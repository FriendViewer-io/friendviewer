#include "prototype/example/example_class.hh"

#include <iostream>

namespace example {

void ExampleClass::do_action() { std::cout << "From external library" << std::endl; }

}  // namespace example
