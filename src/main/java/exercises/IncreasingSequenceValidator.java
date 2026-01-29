package exercises;

public class IncreasingSequenceValidator {

    /*
     ZADANIE:
     - metoda przyjmuje tablicę intów
     - zwraca true, jeśli:
       * tablica NIE jest pusta
       * wszystkie liczby są dodatnie (> 0)
       * liczby są ŚCIŚLE rosnące (każda kolejna > poprzedniej)
     - w każdym innym przypadku zwraca false

     PRZYKŁADY:
     [1, 2, 3, 4]   -> true
     [1, 2, 2, 3]   -> false
     [1, -2, 3]     -> false
     []             -> false
     [5]            -> true

     OGRANICZENIA:
     - nie używaj Stream API
     - nie twórz nowych tablic
     - użyj maksymalnie jednej pętli
    */

    public static boolean isValidIncreasingSequence(int[] numbers) {
        boolean increasing = true;

        for (int i = numbers.length-1; i>=0; i--){
            if ((numbers[i]<numbers[i+1])&&(numbers[i]==numbers[i+1])){
                increasing = false;
            }
        }

        return increasing ;
    }
}
