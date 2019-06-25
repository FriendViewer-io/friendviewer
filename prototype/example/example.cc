#include <iostream>

#include "prototype/example/example_class.hh"

int main() {
    std::cout << "This is a example with bazel" << std::endl;
    example::ExampleClass example_class;
    example_class.do_action();
}
