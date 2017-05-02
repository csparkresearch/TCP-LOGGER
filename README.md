# TCP-LOGGER
Android App IEEE 488.2 SCPI data Logger

##  Android Studio Project
- Android Studio 2.3.1
- Gradle 2.2

## Description

Allows data logging from devices that respond to SCPI commands, and are on the same network as the phone/tablet running this android app

Connects to <host>:<port> , and issues a standard '*IDN?' command that most SCPI devices respond to.
Following which, it plots the response to a user specified command as a function of time

For example, the command to read temperature values from a Lakeshore temperature controller is `:KRDG?`


Contributions welcome


