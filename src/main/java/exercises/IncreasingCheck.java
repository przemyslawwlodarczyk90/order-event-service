package exercises;

public class IncreasingCheck {

    /*
     ZADANIE:
     - metoda przyjmuje int[]
     - zwraca true, jeśli tablica jest ściśle rosnąca
     */

    public static boolean isStrictlyIncreasing(int[] numbers) {
        boolean increasing = false;

        for (int i = 0; i<numbers.length-1; i++){
            if(numbers[i]<numbers[i+1]){
                increasing = true;
            } else if (numbers[i]==numbers[i+1]) {
                increasing = false;
            }
        }

        return increasing;
    }

    public static void main(String[] args) {
        System.out.println(isStrictlyIncreasing(new int[]{1, 3, 5, 7}));     // true
        System.out.println(isStrictlyIncreasing(new int[]{1, 3, 3, 7}));     // false
        System.out.println(isStrictlyIncreasing(new int[]{5}));              // true
        System.out.println(isStrictlyIncreasing(new int[]{}));               // true
        System.out.println(isStrictlyIncreasing(new int[]{1, 4, 2}));         // false
    }
}
