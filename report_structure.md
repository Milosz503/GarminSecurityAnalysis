# Findings

- Garmin uses deprecated SHA-1 for app signing
- Android apps installed on the phone, without any permissions can detect that the user uses Garmin devices and the type of the device
- Communication between the third party apps and their companion apps are in plain text, available to all apps installed on the Andoid phone
- Permissions system of the third party watch apps is not as well separated into different categories as it is in Android, iOS phones.
- Fuzzy testing suggests that it may be possible to escape the virtual machine, that is running the third party apps.

# Report structure

- Introduction
    - Garmin fitness watches
    - Software description
- Related work
- Threat Analysis
- Security of Connect IQ store
  - Preliminary analysis
  - App decompilation
  - Static analysis
  - Network security
- Security of third party apps
    - Permissions
    - App runtime
    - Simulator
    - Building process
    - Signing
    - Bytecode analysis
    - Modifying the executable
    - Communication between the watch and the phone
    - Sandboxing
    - App sideloading
- Fuzzing third party apps
    - Environment setup
    - Existing fuzzing solutions
    - Fuzzing process
    - Fuzzing results
- Privacy
- Conclusion
  - Permissions - could be better
  - VM - probably can be escaped
  - Garmin does not offer any bug bounty program
  - Closed source
- Future work