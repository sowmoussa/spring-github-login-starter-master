package io.sowmoussa.betterreads.book;

import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

import io.sowmoussa.betterreads.userbooks.UserBooksPrimaryKey;
import io.sowmoussa.betterreads.userbooks.UserBooksRepository;
import io.sowmoussa.betterreads.userbooks.Userbooks;

@Controller
public class BookController {
    
    private BookRepository bookRepository;
    private UserBooksRepository userBooksRepository;
    private final String COVER_IMAGE_ROOT = "http://covers.openlibrary.org/b/id/";

    @Autowired
    public BookController(BookRepository bookRepository, UserBooksRepository userBooksRepository) {
        this.bookRepository = bookRepository;
        this.userBooksRepository = userBooksRepository;
    }

    @GetMapping(value = "/books/{bookId}")
    public String getBook(@PathVariable String bookId, Model model, @AuthenticationPrincipal OAuth2User principal) {
        Optional<Book> optionalBook = bookRepository.findById(bookId);
        if (optionalBook.isPresent()) {
            Book book = optionalBook.get();
            String coverImageUrl = "/images/default_cover.png";
            if (book.getCoverIds() != null && book.getAuthorIds().size() > 0) {
                coverImageUrl = COVER_IMAGE_ROOT + book.getCoverIds().get(0) + "-L.jpg";
            }
            model.addAttribute("coverImage", coverImageUrl);
            model.addAttribute("book", book);

            
            if(principal != null && principal.getAttribute("login") != null) {
                String userId = principal.getAttribute("login");
                model.addAttribute("loginId", userId);
                UserBooksPrimaryKey key = new UserBooksPrimaryKey();
                key.setBookId(bookId);
                key.setUserId(userId);
                Optional<Userbooks> userBooks = userBooksRepository.findById(key);
                if(userBooks.isPresent()) {
                    model.addAttribute("userBooks", userBooks.get());
                } else {
                    model.addAttribute("userBooks", new Userbooks());
                }
            }

            return "book";
        }
        return "book-not-found";
    }
}
