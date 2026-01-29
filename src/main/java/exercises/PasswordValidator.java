package exercises;

public class PasswordValidator {

    /*
     ZADANIE:
     - metoda przyjmuje String password
     - zwraca true, jeśli hasło spełnia WSZYSTKIE warunki:
       * ma co najmniej 8 znaków
       * zawiera co najmniej jedną wielką literę
       * zawiera co najmniej jedną cyfrę
     - w każdym innym przypadku zwraca false

     PRZYKŁADY:
     "Password1"   -> true
     "password1"   -> false (brak wielkiej litery)
     "Password"    -> false (brak cyfry)
     "Pass1"       -> false (za krótkie)
     ""            -> false

     OGRANICZENIA:
     - nie używaj regexów
     - nie używaj Stream API
     - możesz użyć pętli i metod z klasy Character
    */

    public static boolean isValidPassword(String password) {
        char[]array = password.toCharArray();
        boolean isValid = false;

         for (char c : array){
             if(Character.isDigit(c)||Character.isUpperCase(c)){
                 isValid = true;
             }
         }

         if (array.length>=8){
             isValid = true;
         }
         return isValid;
    }
}
