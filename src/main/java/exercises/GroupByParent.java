package exercises;

import java.util.*;

/*
 ZADANIE:
 Masz listę:

 ["A1:B1", "A1:B2", "A2:B3", "A1:B4", "A2:B5"]

 Zbuduj Map<String, List<String>>

 Wynik:
 A1 -> [B1, B2, B4]
 A2 -> [B3, B5]
 */
public class GroupByParent {


    public static Map<String, List<String>> groupBy(List<String>list){{
        Map<String, List<String>> mapOrdered = new HashMap<>();

        for (String s : list){
            String[]temp = s.split(";");
            String key = temp[0];
            String value= temp[1];

            mapOrdered
                    .computeIfAbsent(key, k -> new ArrayList<>())
                    .add(value);
        }

        return mapOrdered;
    }


}}
