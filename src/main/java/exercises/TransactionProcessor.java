package exercises;

import jakarta.persistence.criteria.CriteriaBuilder;
import lombok.Data;
import lombok.Getter;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


public class TransactionProcessor {

    @Getter
    static class Transaction {
        String user;
        int amount;
        boolean approved;

        Transaction(String user, int amount, boolean approved) {
            this.user = user;
            this.amount = amount;
            this.approved = approved;
        }
    }

    public static void main(String[] args) {
        List<Transaction> transactions = List.of(
                new Transaction("Alice", 120, true),
                new Transaction("Bob", 80, true),
                new Transaction("Alice", 200, false),
                new Transaction("Charlie", 300, true),
                new Transaction("Bob", 150, true)
        );

        /*
         ZADANIE:
         1. Napisz metodę getApprovedTransactions(), która:
            - przyjmie List<Transaction>
            - zwróci tylko transakcje zatwierdzone (approved == true)


            public

         2. Napisz metodę sumForUser(), która:
            - przyjmie List<Transaction> ORAZ nazwę użytkownika (String)
            - zwróci sumę kwot (amount) dla tego użytkownika
            - UWAGA: liczymy TYLKO transakcje approved

         3. W main():
            - pobierz zatwierdzone transakcje
            - wypisz sumę dla "Bob"
            - oczekiwany wynik: 230
         */

        // TODO: użyj swoich metod tutaj
    }

    public List<Transaction> getApprovedTransactions(List<Transaction> list){
        List<Transaction> approved = new ArrayList<>();
        for (Transaction t : list){
            if (t.approved == true){
                approved.add(t);
            }

        }
    return approved;
    }

    public Map<String, Integer> sumForUser (List<Transaction> list){
        Map <String, Integer> approvedSumForUser = new HashMap<>();
        for (Transaction t : list){
            if (t.approved == true){
                if (approvedSumForUser.containsKey(t.getUser())){
                    Integer temp = approvedSumForUser.get(approvedSumForUser.get(t.getUser()));
                   temp+=t.getAmount();
                   approvedSumForUser.put(t.getUser(), temp);
                }

                }else {
                approvedSumForUser.put(t.getUser(), t.getAmount());
            }
        }

    for(Map.Entry<String,Integer> entry : approvedSumForUser.entrySet()){
        System.out.println(entry.getKey());
        System.out.println(entry.getValue());
        System.out.println("Next");
    }

    return  approvedSumForUser;}


}
