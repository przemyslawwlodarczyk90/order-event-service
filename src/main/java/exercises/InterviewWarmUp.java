package exercises;

import java.util.ArrayList;
import java.util.List;

public class InterviewWarmUp {

    /*
     Napisz metodę, która:
     - przyjmie tablicę String[]
     - zwróci ilość UNIKALNYCH słów
     - ignoruj wielkość liter ("Java" == "java")
     - NIE używaj Set
     */

    public static int countUnique(String[] words) {
        List<String> list = new ArrayList<>();

        for(int i = 0; i< words.length-1; i++){
              if (list.get(i).equalsIgnoreCase(words[i])){
                  continue;
            }else {
                  list.add(words[i]);
              }
        }


    return list.size(); }

    public static void main(String[] args) {
        String[] data = {
                "Java", "python", "JAVA", "C++", "java", "Python"
        };

        System.out.println(countUnique(data)); // 3
    }
}
