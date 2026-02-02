package exercises;

public class AccountNumberValidator {

    /*
     ZADANIE:
     - metoda przyjmuje String accountNumber
     - zwraca true, jeśli numer konta jest POPRAWNY

     ZASADY POPRAWNOŚCI:
     1. accountNumber NIE może być null
     2. musi mieć DOKŁADNIE 10 znaków
     3. może zawierać TYLKO cyfry
     4. suma WSZYSTKICH cyfr musi być podzielna przez 7

     W KAŻDYM innym przypadku → false

     PRZYKŁADY:
     "1234567890" -> false (suma = 45)
     "7000000000" -> true  (suma = 7)
     "12345"      -> false (za krótki)
     "1234A67890" -> false (litera)
     null         -> false

     OGRANICZENIA:
     - nie używaj regexów
     - nie używaj Stream API
     - użyj pętli
     - nie twórz nowych klas
    */

    public static boolean isValidAccountNumber(String accountNumber) {
           boolean valid = false;
           boolean allIsDigits = true;

           char[]array = accountNumber.toCharArray();

           if (accountNumber!=null){
               valid = true;           }

           if (accountNumber.length() == 10){
               valid = true;
           }

           for(char c : array){
               if (!Character.isDigit(c)){
                   valid = false;
                   allIsDigits = false;
               }
           }

           int sumOfAll = 0;


           if (allIsDigits){
           for (char d : array){
               int i = d;
               sumOfAll+=i;
           }

           if (allIsDigits){
               if (sumOfAll % 7 == 0) {
                   valid = true;

               } else  {
                   valid = false;

               }
           }

//        return false;
    }
return valid;}}
