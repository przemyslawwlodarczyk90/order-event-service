//package exercises;
//
//public class quickWarmup {
//
//    /*
//     ZADANIE:
//     - metoda przyjmuje int[]
//     - zwraca DRUGĄ największą liczbę w tablicy
//     - zakładamy, że tablica ma co najmniej 2 różne liczby
//     - NIE sortuj tablicy
//     */
//
//    public static int secondMax(int[] numbers) {
//        int max = 0;
//        int secondMaxValue = 0;
//
//        for (int i = 0; i< numbers.length-1; i++){
//            max = numbers[0];
//            secondMaxValue = numbers[0];
//            if (numbers[i]>max) {
//                max = numbers[i];
//            } else if (numbers[i]<max) {
//                secondMaxValue = numbers[i];
//            }
//
//        return secondMaxValue;}
//
//
//
//
//     public static void main(String[] args) {
//        int[] data = { 5, 12, 3, 9, 7 };
//
//        System.out.println(QuickWarmup.secondMax(data)); // 9
//    }
//}
