package io.sowmoussa.betterreads.userbooks;

import java.time.LocalDate;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Controller;
import org.springframework.util.MultiValueMap;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.servlet.ModelAndView;

import io.sowmoussa.betterreads.book.Book;
import io.sowmoussa.betterreads.book.BookRepository;
import io.sowmoussa.betterreads.user.BooksByUser;
import io.sowmoussa.betterreads.user.BooksByUserRepository;

@Controller
public class UserBooksController {
    
    private UserBooksRepository userBooksRepository;
    private BooksByUserRepository booksByUserRepository;
    private BookRepository bookRepository;

    @Autowired
    public UserBooksController(UserBooksRepository userBooksRepository, BooksByUserRepository booksByUserRepository, BookRepository bookRepository) {
        this.userBooksRepository = userBooksRepository;
        this.bookRepository = bookRepository;
        this.booksByUserRepository = booksByUserRepository;
    }

    @PostMapping("/addUserBook")
    public ModelAndView addBookForUser(@AuthenticationPrincipal OAuth2User principal, @RequestBody MultiValueMap<String, String> formData) {
        
        if (principal == null || principal.getAttribute("login") == null) {
            return null;
        }

        String bookId = formData.getFirst("bookId");
        Optional<Book> optionalBook = bookRepository.findById(bookId);
        if (!optionalBook.isPresent()) {
            return new ModelAndView("redirect:/");
        }
        Book book = optionalBook.get();

        Userbooks userBooks  = new Userbooks();
        UserBooksPrimaryKey key = new UserBooksPrimaryKey();
        String userId = principal.getAttribute("login");
        key.setUserId(userId);
        key.setBookId(bookId);

        userBooks.setKey(key);

        int rating = Integer.parseInt(formData.getFirst("rating"));

        userBooks.setStartedDate(LocalDate.parse(formData.getFirst("startedDate")));
        userBooks.setCompletedDate(LocalDate.parse(formData.getFirst("completedDate")));
        userBooks.setRating(rating);
        userBooks.setReadingStatus(formData.getFirst("readingStatus"));

        userBooksRepository.save(userBooks);

        BooksByUser booksByUser = new BooksByUser();
        booksByUser.setId(userId);
        booksByUser.setBookId(bookId);
        booksByUser.setBookName(book.getName());
        booksByUser.setCoverIds(book.getCoverIds());
        booksByUser.setAuthorNames(book.getAuthorNames());
        booksByUser.setReadingStatus(formData.getFirst("readingStatus"));
        booksByUser.setRating(rating);
        booksByUserRepository.save(booksByUser);


        return new ModelAndView("redirect:/books/" + formData.getFirst("bookId"));
    }
}
