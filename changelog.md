# Project log

## Week 43

Investigating:
- communication between official Garmin Android apps
- recently introduced features to the Garmin SDK
  -  Complications -> permission system in Garmin watches third party apps
- Third party developer r.485

## Week 44,45

Researching, implementing fuzzing

Existing tools:
- AFL - deprecated
- AFL++ - potentially could be used probably without any good results out of the box. It would be required to implement post-processing of the generated apps(signing) and customize the crash detection as there are 2 applications used in the process. Additionally, it would be useful to implement grammar-aware fuzzing.

Implementation:
- Poet
  - generating apps with random noise and signing after
- Oracle
  - analyzing output of `monkeydo` script
  - analyzing logs from the simulator
  - detecting when any of those crashes