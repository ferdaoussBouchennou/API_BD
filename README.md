# **Bibliothèque de Gestion de Bases de Données Java**
Cette bibliothèque offre une interface unifiée pour gérer les connexions et les opérations 
avec différents systèmes de gestion de bases de données (MySQL, PostgreSQL, SQL Server et Oracle).

**Fonctionnalités**
- Interface unique et cohérente pour différents SGBD
- Gestion des connexions
- Exécution de requêtes SQL (SELECT, INSERT, UPDATE, DELETE)
- Support des transactions
- Dialectes SQL spécifiques pour chaque SGBD
- Gestion des erreurs standardisée

**Prérequis**
- Drivers JDBC pour les bases de données que vous souhaitez utiliser
- Maven

**Installation**
1. Ajoutez le JAR directement au classpath de votre projet :
Eclipse : Clic droit sur le projet > Properties > Java Build Path > Libraries > Add External JARs
IntelliJ IDEA : File > Project Structure > Libraries > + > Java
NetBeans : Clic droit sur le projet > Properties > Libraries > Add JAR/Folder

2. Ajoutez les drivers JDBC nécessaires
   **MySQL**
<dependency>
   <groupId>com.mysql</groupId>
   <artifactId>mysql-connector-j</artifactId>
   <version>9.3.0</version>
</dependency>
   **PostgreSQL**
<dependency>
   <groupId>org.postgresql</groupId>
   <artifactId>postgresql</artifactId>
   <version>42.7.5</version>
</dependency>
   **SQL Server**
<dependency>
   <groupId>com.microsoft.sqlserver</groupId>
   <artifactId>mssql-jdbc</artifactId>
   <version>12.10.0.jre11</version>
</dependency>
     **Oracle**
<dependency>
    <groupId>com.oracle.database.jdbc</groupId>
    <artifactId>ojdbc8</artifactId>
    <version>23.8.0.25.04</version>
</dependency>

**Configuration**
1. Créez un fichier de configuration **db.properties**

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