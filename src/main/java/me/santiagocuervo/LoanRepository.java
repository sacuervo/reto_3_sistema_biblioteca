package me.santiagocuervo;

import java.util.List;

public interface LoanRepository {

    void save(Loan loan);

    Loan findById(String id);

    List<Loan> getLoans();

}
