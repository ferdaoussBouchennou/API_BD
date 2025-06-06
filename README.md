Voici une version ajustée du README avec tout le code mis en forme à l'aide de blocs de code Markdown (` ```java ` ou ` ```properties `, selon le cas). Cela améliore la lisibilité et le rendu sur des plateformes comme GitHub :

---

# **Bibliothèque de Gestion de Bases de Données Java**

Cette bibliothèque offre une interface unifiée pour gérer les connexions et les opérations avec différents systèmes de gestion de bases de données (MySQL, PostgreSQL, SQL Server et Oracle).

---

## 🚀 Fonctionnalités

* Interface unique et cohérente pour différents SGBD
* Gestion des connexions
* Exécution de requêtes SQL (SELECT, INSERT, UPDATE, DELETE)
* Support des transactions
* Dialectes SQL spécifiques pour chaque SGBD
* Gestion des erreurs standardisée

---

## 📦 Prérequis

* Drivers JDBC pour les bases de données que vous souhaitez utiliser
* Maven

---

## 🔧 Installation

### 1. Ajoutez les drivers JDBC nécessaires dans `pom.xml`

#### **MySQL**

```xml
<dependency>
    <groupId>com.mysql</groupId>
    <artifactId>mysql-connector-j</artifactId>
    <version>9.3.0</version>
</dependency>
```

#### **PostgreSQL**

```xml
<dependency>
    <groupId>org.postgresql</groupId>
    <artifactId>postgresql</artifactId>
    <version>42.7.5</version>
</dependency>
```

#### **SQL Server**

```xml
<dependency>
    <groupId>com.microsoft.sqlserver</groupId>
    <artifactId>mssql-jdbc</artifactId>
    <version>12.10.0.jre11</version>
</dependency>
```

#### **Oracle**

```xml
<dependency>
    <groupId>com.oracle.database.jdbc</groupId>
    <artifactId>ojdbc8</artifactId>
    <version>23.8.0.25.04</version>
</dependency>
```

### 2. Ajoutez le JAR directement au classpath de votre projet

* **Eclipse :**
  `Clic droit sur le projet > Properties > Java Build Path > Libraries > Add External JARs`

* **IntelliJ IDEA :**
  `File > Project Structure > Libraries > + > Java`

* **NetBeans :**
  `Clic droit sur le projet > Properties > Libraries > Add JAR/Folder`

---

## ⚙️ Configuration

Créez un fichier `db.properties` :

```properties
# Type de base de données par défaut
default.database=mysql

# Configuration MySQL
mysql.driver=com.mysql.cj.jdbc.Driver
mysql.url=jdbc:mysql://localhost:3306/nom_base_de_donnees
mysql.username=votre_utilisateur
mysql.password=votre_mot_de_passe

# Configuration PostgreSQL
postgresql.driver=org.postgresql.Driver
postgresql.url=jdbc:postgresql://localhost:5432/nom_base_de_donnees
postgresql.username=votre_utilisateur
postgresql.password=votre_mot_de_passe

# Configuration SQL Server
sqlserver.driver=com.microsoft.sqlserver.jdbc.SQLServerDriver
sqlserver.url=jdbc:sqlserver://localhost:1433;databaseName=nom_base_de_donnees;encrypt=true;trustServerCertificate=true
sqlserver.username=votre_utilisateur
sqlserver.password=votre_mot_de_passe

# Configuration Oracle
oracle.driver=oracle.jdbc.OracleDriver
oracle.url=jdbc:oracle:thin:@localhost:1521:XE
oracle.username=votre_utilisateur
oracle.password=votre_mot_de_passe
```

---

## 🧪 Utilisation

### Création d'une instance de gestionnaire de base de données

```java
import ma.ensa.db.DatabaseManager;
import ma.ensa.db.DatabaseManagerFactory;
import ma.ensa.util.DBConfigLoader;

// Charger la configuration
DBConfigLoader configLoader = new DBConfigLoader("db.properties");
DatabaseManagerFactory factory = new DatabaseManagerFactory(configLoader);

// Créer une instance pour un SGBD spécifique
DatabaseManager dbManager = factory.createDatabaseManager("mysql");
// ou
DatabaseManager dbManager = factory.createDatabaseManager("postgresql");
// ou
DatabaseManager dbManager = factory.createDatabaseManager("sqlserver");
// ou
DatabaseManager dbManager = factory.createDatabaseManager("oracle");

// Ou utiliser le type par défaut
DatabaseManager defaultDbManager = factory.createDefaultDatabaseManager();
```

### Connexion avec try-with-resources

```java
try (DatabaseManager dbManager = factory.createDatabaseManager("mysql")) {
    // Vos opérations ici
    // La connexion sera fermée automatiquement
}
```

### Exécution de requêtes SELECT

```java
String query = "SELECT * FROM TABLE_NAME";
List<Map<String, Object>> results = dbManager.executeQuery(query);

for (Map<String, Object> row : results) {
    System.out.println("ID: " + row.get("id") +
                       ", Nom: " + row.get("nom") +
                       ", Age: " + row.get("age"));
}
```

### Exécution de requêtes INSERT, UPDATE, DELETE

```java
// INSERT
String insertQuery = "INSERT INTO TABLE_NAME (nom, age, email) VALUES (?, ?, ?)";
int rowsInserted = dbManager.executeUpdate(insertQuery, "Ahmed Bennani", 28, "ahmed@mail.com");

// UPDATE
String updateQuery = "UPDATE TABLE_NAME SET age = ? WHERE nom = ?";
int rowsUpdated = dbManager.executeUpdate(updateQuery, 29, "Ahmed Bennani");

// DELETE
String deleteQuery = "DELETE FROM TABLE_NAME WHERE nom = ?";
int rowsDeleted = dbManager.executeUpdate(deleteQuery, "Ahmed Bennani");
```

### Création et suppression de tables

```java
// Création d'une table
String columns = dbManager.getSQLDialect().getAutoIncrementPrimaryKeyColumn("id") + ", " +
                 "nom VARCHAR(100), " +
                 "age INT, " +
                 "email VARCHAR(100)";
dbManager.createTableIfNotExists("TABLE_NAME", columns);

// Suppression d'une table
dbManager.dropTableIfExists("TABLE_NAME");
```

### Gestion des transactions

```java
try {
    dbManager.beginTransaction();

    dbManager.executeUpdate("INSERT INTO TABLE_NAME (nom, age) VALUES (?, ?)", "Omar", 25);
    dbManager.executeUpdate("UPDATE TABLE_NAME SET age = ? WHERE nom = ?", 35, "Ahmed");

    dbManager.commitTransaction();
} catch (SQLException e) {
    dbManager.rollbackTransaction();
    throw e;
}
```

---


