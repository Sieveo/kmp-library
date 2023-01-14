#include <iostream>
#include "libpopcorn_api.h"

int main() {
    std::cout << "Hello, World!" << std::endl;

    auto lib= libpopcorn_symbols();
    lib->kotlin.root.printPlatformName();
    lib->kotlin.root.echoWebsocketServer();

    return 0;
}
