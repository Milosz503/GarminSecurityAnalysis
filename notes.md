# Security analysis of Garmin watch software
## Connect IQ - third party apps

### Connect IQ SDK
Garmin created SDK for third party app development. It is integrated with Visual Studio Code through the Monkey C extension. There is an official documentation describing the development process with Visual Studio Code. However, it doesn't describe the specific scripts and tools contained in the SDK that are used by the extension.
The SDK consists mostly of Java class used for compiling and packaging the application. Additionally, a simulator is provided for testing the apps. Unfortunately, it is implemented as a binary ELF file, which makes the reverse engineering more difficult.
Moreover, other scripts are provided for other tasks such as uploading an app to the simulator. 

List of tools:
- _monkeybrains.jar_ - compiles and build the project
- _simulator_ - simulates a chosen Garmin device
- _monkeydo_ - executes Connect IQ executable on the simulator 

Other scripts: _barrelbuild, barreltest, connectiq, era, mdd, monkeyc, monkeygraph, monkeydoc, shell, monkeymotion_


### App signature
The generated app bytecode has to be signed by the developer.
The signing algorithm uses SHA1 hash of the bytecode that is signed with a private RSA key.
The key is 4096 long, which is more than recommended.

SHA-1 is no longer considered secure against well-funded opponents. NIST formally deprecated use of this hash function in 2011. In 2020 there was a paper published demonstrating a chosen-prefix collision attack. It still doesn't offer a viable solution to find a collision to a given hash for a chosen prefix. However, the function has been already broken and in the upcoming years new attacks might be discovered. 

### Compiler
The compiler is written in Java, which makes the analysis of the code relatively simple. Nonetheless, the compilation is a complicated process consisting of several stages.  
Java class `Opcode` contains a list of instructions in the final bytecode. Additionally, SDK provides a mapping of all API methods to the number values, which is probably some type of address.

During the compilation, the code is translated to mid-level intermediate representation (MIR). The files containing the representations are created during the build process and can be used for better understanding of the final artifact.

### Modifying the executable
In order to test the security of the virtual machine, it is useful to be able to edit the executable. However, after changing the bytecode, it is necessary to sign the executable again. After analysis of _monkeybrains.jar_ file, I created a kotlin script that signs the app again.

### Connect IQ apps TODO
- analyze native calls security
- analyze Monkey C security such as:
  - bound checking
  - overflows
  - pointers/references, 
  - memory management


## Additional resources

- https://github.com/pzl/ciqdb
- https://www.atredis.com/blog/2020/11/4/garmin-forerunner-235-dion-blazakis



