package ma.ensa.util;

import lombok.Getter;

import java.io.InputStream;
import java.util.Properties;

@Getter
public class DBConfigLoader {
    private Properties properties;
    public DBConfigLoader(String filePath) {
        properties = new Properties();
        try(InputStream input=getClass().getClassLoader().getResourceAsStream(filePath)){
            if (input == null) {
                System.out.println("Impossible de trouver le fichier "+ filePath);
                return;
            }
            properties.load(input);
        }catch (Exception e){
            System.out.println("Erreur lors du chargement des configurations: " + e.getMessage());
        }
    }
    public String getDefaultDatabaseType() {
        return properties.getProperty("default.database", "mysql");
    }

    public String[] getDatabaseInfo(String dbType){
        String[] info = new String[4];
        info[0] = properties.getProperty(dbType+".driver");
        info[1] = properties.getProperty(dbType+".url");
        info[2] = properties.getProperty(dbType+".username");
        info[3] = properties.getProperty(dbType+".password");
        return info;
    }
}
