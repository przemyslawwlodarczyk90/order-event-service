package exercises.io;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.util.*;

/*
 ZADANIE:
 Masz plik data.txt o strukturze:

 A1;B1;B2;B3
 A2;B2;B5
 A3;B7;B8;B2

 Wczytaj plik do Map<String, List<String>>

 Klucz = pierwszy element w linii
 Wartość = reszta elementów

 Użyj BufferedReader
 */
public class SimpleBomLoader {

  public static Map<String, List<String>> fileLoader(String filename) throws IOException {
      Map<String, List<String>> data = new HashMap<>();
      File file = new File(filename);

      if (!file.exists()) {
          System.out.println("file not found");
      }

      int counter = 0;

      try {
          BufferedReader br = new BufferedReader(new FileReader(file, StandardCharsets.UTF_8));
          String line;

          while ((line = br.readLine()) != null) {

              if(counter== 0 ){
                  counter++;
                  continue;
              }

              counter++;

            String[]parts = line.split(";");
            String key = parts[0];
            List<String> values = new ArrayList<>();

            for (int i = 1; i< parts.length; i++){
                values.add(parts[i]);
            }
            data.put(key,values);


              br.close();

          }
      } catch (IOException e) {
          throw new RuntimeException(e);
      }
      return data;
  }}