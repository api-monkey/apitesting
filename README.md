# API Monkey 
API Monkey is a simple, easy to use no-code test automation platform for REST API's. If you have a public facing API's, you can use APIMonkey to create and run test cases for you, on the cloud. 

-- attach a screen shot of test cases with data

## How does it work?

To get started, you need a public facing API with swagger docs. Point to the API docs and the magic starts to happen


This project is licensed under the AGPL-3.0. It is free to use, modify, and self-host for both personal and organizational use. Contributions are welcome and encouraged.”

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

## 📄 License

ApiMonkey is licensed under the **GNU Affero General Public License v3.0 (AGPL-3.0)**.

This means:

- You are free to use, modify, and distribute ApiMonkey.
- If you run a modified version **as a hosted service or over a network**, you must also release your modified source code under AGPL-3.0.
- Any derivative works must remain open-source under the same license.

For full details, see the [LICENSE](./LICENSE) file.

---

## 🤝 Contributing to ApiMonkey

We welcome contributions! Before submitting code, please read and agree to our **Contributor License Agreement (CLA)**:

👉 **[View the CLA](./CLA-individual.md)**

All pull requests require signing the CLA. If using our CLA bot, you will be prompted automatically during your first contribution.

### Contribution Guidelines

1. **Fork** the repository and create a new branch for your changes.  
2. Make sure your code follows project structure and style conventions.  
3. Add or update tests when applicable.  
4. Submit a **pull request** with a clear description of the change.  
5. Ensure you have signed the CLA when prompted.

---

## 📬 Support & Contact

If you encounter issues, please open a GitHub Issue.  
For security-related disclosures, please email: **[apimonkey9 [] gmail.com]**

---

## ⭐ Why AGPL?

The AGPL license ensures that:

- ApiMonkey remains free and open-source.
- Improvements made by others in hosted environments (SaaS, internal tools) must also be shared.
- No one can take ApiMonkey, host it as a competing API service, and keep their modifications proprietary.

This aligns with our mission to keep the tool open while preventing commercial exploitation without contribution.

---

Thank you for using and contributing to ApiMonkey!
