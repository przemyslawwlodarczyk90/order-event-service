package exercises.io;

import java.util.*;

/*
 ZADANIE:

 Masz:

 Map<String, List<String>> data

 Przykład:

 A1 -> [B1, B2, B3]
 A2 -> [B2, B5]
 A3 -> [B1, B2]

 Policz ile razy KAŻDY element z list występuje globalnie.

 Wynik:

 B1 -> 2
 B2 -> 3
 B3 -> 1
 B5 -> 1
 */
public class GlobalCounter {

    public static Map<String, Integer> count(Map<String, List<String>> data) {

        Map<String, Integer> result = new HashMap<>();


        for(Map.Entry<String, List<String>> entry : data.entrySet()) {
            List<String> values = entry.getValue();

            for(String s : values){
                result.put(s, result.getOrDefault(s,0)+1);
            }

        }


        return result;
    }
}
