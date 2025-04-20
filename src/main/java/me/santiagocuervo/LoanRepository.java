package me.santiagocuervo;

public interface LoanRepository {

    void save(Loan loan);

    Loan findById(String id);

}
