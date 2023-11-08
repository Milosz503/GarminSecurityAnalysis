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
