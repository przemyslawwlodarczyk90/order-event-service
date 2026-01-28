//package exercises;
//
//import java.sql.Array;
//
//public class WarmUp {
//
//    /*
//     ZADANIE 1:
//     Napisz metodę, która:
//     - przyjmie tablicę int[]
//     - zwróci największą liczbę
//     - NIE używaj Arrays.sort()
//     */
//
//    public static int findMax(int[] numbers) {
//        int max = numbers[0];
//        for(Integer xxx : numbers){
//            if (xxx>max){
//                max = xxx;
//            }
//        }
//
//
//    return max;}
//
//    /*
//     ZADANIE 2:
//     Napisz metodę, która:
//     - policzy ile liczb jest parzystych
//     - zwróci ich ilość
//     */
//
//    public static int countEven(int[] numbers) {
//        int even = 0;
//
//        for (Integer number : numbers){
//            if (number % 2 == 0){
//                even++;
//            }
//        }
//
//        return even;
//    }
//
//    /*
//     ZADANIE 3:
//     Napisz metodę, która:
//     - zamieni każdą liczbę ujemną na 0
//     - zwróci NOWĄ tablicę (nie modyfikuj oryginału)
//     */
//
//    public static Integer[] replaceNegatives(int[] numbers) {
//        Integer[]array = new Integer[numbers.length];
//
//        for (int i = 0; i<numbers.length; i++){
//            if(numbers[i]<0){
//                array[i] = 0;
//            }else {
//
//            array[i] = numbers[i];
//
//        }}
//        return array;
//    }
//
//    public static void main(String[] args) {
//        int[] data = { 5, -3, 12, 0, -7, 9 };
//
//        System.out.println(findMax(data));            // 12
//        System.out.println(countEven(data));          // 3
//        int[] replaced = replaceNegatives(data);
//
//        for (int i = 0; i < replaced.length; i++) {
//            System.out.print(replaced[i] + " ");
//        }
//        // 5 0 12 0 0 9
//    }
//}
