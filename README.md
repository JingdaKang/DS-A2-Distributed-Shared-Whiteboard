# Distributed Shared Whiteboard

A Java RMI and Swing collaborative whiteboard with a server, a manager client that creates a board, and participant clients that join it.

## Requirements

Java 8 or newer, a graphical desktop for clients, and Maven for source builds. RMI connectivity between the clients and server is required.

## Getting started

```sh
java -Djava.rmi.server.hostname=127.0.0.1 -jar Server.jar 127.0.0.1 1234
# Separate desktop terminals:
java -jar CreateWhiteBoard.jar 127.0.0.1 1234 manager
java -jar JoinWhiteBoard.jar 127.0.0.1 1234 participant
```

## Project structure

| Path | Purpose |
| --- | --- |
| `src/main/java/server` | RMI server and shared state |
| `src/main/java/remote` | Remote whiteboard interface |
| `src/main/java/client` | Manager and participant GUIs |
| `pom.xml` | Maven build |
| `DS_Assignment2_Report.pdf` | Project report |

## Configuration and limitations

The server creates its own RMI registry. For remote machines, replace loopback addresses with a reachable server address and account for RMI object ports as well as the registry port.

## Development and validation

Build with `mvn package`. Open manager and participant clients, admit the participant, and check that drawing changes appear in both windows. Consult the report for the intended behavior.

## License

No root-level license file is included. Check source-specific notices and obtain permission before redistribution or reuse.
