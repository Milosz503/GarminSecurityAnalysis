# Fuzzing

## Notes

- fuzzing fit format
  - Format description: https://developer.garmin.com/fit/protocol/
  - Decoder, encoder in Go: https://github.com/tormoder/fit
- fuzzing prg format
  - https://github.com/AFLplusplus/AFLplusplus - prg as input file, fuzzing simulator

Fuzzing ideas:
- on prg signed file app:
  - flipping bits
- on prg file with removed signature, signed after the mutation
  - flipping random bits
  - mutating input to API function calls
  - mutating opcodes

Then eventual findings test on a real device

- components:
  - the poet
  - the courier
  - the oracle



# Logs

```
----------
Testing app 'assets/fuzzing/app_1700121741926.prg'...
  >I Picked up _JAVA_OPTIONS: -Dawt.useSystemAAFontSettings=on -Dswing.aatext=true
  >SIM     Native: true
  >SIM ---
  >SIM Error: Out Of Memory Error
  >SIM Details: 'Failed invoking <symbol>'
  >SIM Time: 2023-11-16T08:02:23Z
  >SIM Part-Number: 006-B3225-00
  >SIM Firmware-Version: '6.80'
  >SIM Language-Code: eng
  >SIM ConnectIQ-Version: 68.2.3
  >SIM Filename: APP_1700121647088
  >SIM Appname: ~
  >SIM Stack: 
  >SIM   - pc: 0x100001df
  >SIM   - pc: 0x80000000
   EXIT CODE: 0
[!] App Failed [!]
Elapsed: 1.708284674s
SUCCESS - SIMULATOR DIED!
Destroyed simulator, exit code: 139
Process finished with exit code 0




Testing app 'assets/fuzzing/app_1700122261974.prg'...
  >I Picked up _JAVA_OPTIONS: -Dawt.useSystemAAFontSettings=on -Dswing.aatext=true
  >SIM Appname: Background Timer
  >SIM ---
  >SIM Error: Symbol Not Found Error
  >SIM Details: "Could not find symbol '0008001f'"
  >SIM Time: 2023-11-16T08:11:03Z
  >SIM Part-Number: 006-B3225-00
  >SIM Firmware-Version: '6.80'
  >SIM Language-Code: eng
  >SIM ConnectIQ-Version: 4.2.3
  >SIM Filename: APP_1700121647088
  >SIM Appname: Background Timer
  >SIM Stack: 
  >I Encountered an app crash.
   EXIT CODE: 2
  Found expected exit code
[!] App Failed [!]
Elapsed: 1.812708955s
SUCCESS - SIMULATOR DIED!
Destroyed simulator, exit code: 139
Process finished with exit code 0
```