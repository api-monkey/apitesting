
## Getting started 

To get started, you need a public facing API with swagger docs. Point to the API docs and the magic starts to happen.

# Database Configuration

## Prerequisites

Before running the Spring Boot application, ensure that:

1. MySQL database server is installed and running
2. An empty database schema named `api_monkey` is created

### Creating the Database Schema

```sql
CREATE DATABASE api_monkey;
```

### Running the Application
#### Option 1: Using Java System Properties

```Bash
java -jar application.jar \
-DDB_USERNAME=root \
-DDB_PASSWORD=12345678 \
-DDB_URL=jdbc:mysql://localhost:3306/api_monkey?useSSL=false&autoReconnect=true&useUnicode=true&characterEncoding=UTF-8
```
### Option 2: Using Environment Variables
#### Linux/MacOS:
```Bash
export DB_USERNAME=root
export DB_PASSWORD=12345678
export DB_URL="jdbc:mysql://localhost:3306/api_monkey?useSSL=false&autoReconnect=true&useUnicode=true&characterEncoding=UTF-8"

java -jar application.jar
```

### Windows (CMD):
```cmd
set DB_USERNAME=root
set DB_PASSWORD=12345678
set DB_URL=jdbc:mysql://localhost:3306/api_monkey?useSSL=false&autoReconnect=true&useUnicode=true&characterEncoding=UTF-8

java -jar application.jar
```

### Option 3: Using IDE (IntelliJ IDEA / Eclipse)
#### Add the following VM options in your run configuration:

```text
-DDB_USERNAME=root
-DDB_PASSWORD=12345678
-DDB_URL=jdbc:mysql://localhost:3306/api_monkey?useSSL=false&autoReconnect=true&useUnicode=true&characterEncoding=UTF-8
```
