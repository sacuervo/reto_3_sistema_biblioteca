package me.santiagocuervo;

import java.util.NoSuchElementException;

import static org.junit.jupiter.api.Assertions.assertThrows;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;

public class LibraryServiceTest {

    @Mock
    private BookRepository bookRepository;
    @Mock
    private LoanRepository loanRepository;
    private LibraryService libraryService;

    @BeforeEach
    void setup() {
        MockitoAnnotations.openMocks(this);
        libraryService = new LibraryService(bookRepository, loanRepository);
    }

    @Test
    void testBorrowNonExistingBook() {
        String userId = "1234";
        String bookId = "5678";

        assertThrows(NoSuchElementException.class, () -> libraryService.borrowBook(userId, bookId));
    }

    @Test
    void testBorrowUnavailableBook() {
        String userId = "001";
        String bookId = "001";

        Mockito.when(bookRepository.findById(bookId))
                .thenReturn(new Book(bookId, "Meditations", "Marcus Aurelius", true));

        assertThrows(LoanException.class, () -> libraryService.borrowBook(userId, bookId));
    }

}
