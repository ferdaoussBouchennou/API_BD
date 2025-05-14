package ma.ensa.util;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/*Classe utilitaire pour charger des données de test à partir d'un fichier CSV*/
public class CSVDataLoader {

    private String filePath;
    /*Constructeur avec le chemin du fichier CSV*/
    public CSVDataLoader(String filePath) {
        this.filePath = filePath;
    }
    /*Charge les données du fichier CSV*/
    public List<Map<String, String>> loadData() throws IOException {
        List<Map<String, String>> data = new ArrayList<>();

        try (InputStream is = getClass().getClassLoader().getResourceAsStream(filePath);
             BufferedReader br = new BufferedReader(new InputStreamReader(is))) {
            // Lire l'en-tête
            String line = br.readLine();
            if (line == null) {
                return data;
            }
            String[] headers = line.split(",");
            // Lire les données
            while ((line = br.readLine()) != null) {
                String[] values = line.split(",");
                Map<String, String> row = new HashMap<>();

                for (int i = 0; i < headers.length; i++) {
                    if (i < values.length) {
                        row.put(headers[i].trim(), values[i].trim());
                    } else {
                        row.put(headers[i].trim(), "");
                    }
                }
                data.add(row);
            }
        }
        return data;
    }
}