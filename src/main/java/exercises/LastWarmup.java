package exercises;

public class LastWarmup {

    /*
     ZADANIE:
     - metoda przyjmuje String
     - zwraca odwrócony String
     - np. "Java" -> "avaJ"
     */

    public static String reverse(String text) {
        String reversed ="";
        char[]array=text.toCharArray();
        for (int i = text.length()-1; i>=0; i--){
          reversed+=array[i];
        }

        return reversed;
    }

    public static void main(String[] args) {
        System.out.println(reverse("Java"));     // avaJ
        System.out.println(reverse("Finture"));  // erutniF
    }
}
