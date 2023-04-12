package br.com.triluna.service;

import br.com.triluna.controller.BookController;
import br.com.triluna.data.vo.v1.BookVO;
import br.com.triluna.exception.RequiredObjectIsNullException;
import br.com.triluna.exception.ResourceNotFoundException;
import br.com.triluna.mapper.ModelToolMapper;
import br.com.triluna.model.Book;
import br.com.triluna.repository.BookRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.List;
import java.util.logging.Logger;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.methodOn;

@Service
public class BookService {

    private Logger logger = Logger.getLogger(BookService.class.getName());

    @Autowired
    BookRepository repository;

    public BookVO create(BookVO book) {

        if (book == null) {

            throw new RequiredObjectIsNullException();
        }

        logger.info("Creating a book...");

        var entity = repository.save(ModelToolMapper.parseObject(book, Book.class));

        BookVO vo = ModelToolMapper.parseObject(entity, BookVO.class);

        vo.add(linkTo(methodOn(BookController.class).findById(vo.getKey().toString())).withSelfRel());

        return vo;

    }

    public void delete(Long id) {

        logger.info(String.format("Deleting book: %s", id));

        var entity = repository.findById(id).orElseThrow(() -> new ResourceNotFoundException("BookVO not found - id: " + id.toString()));

        repository.delete(entity);
    }

    public List<BookVO> findAll() {

        logger.info("Finding people...");

        var people = ModelToolMapper.parseListObject(repository.findAll(), BookVO.class);

        people
                .stream()
                .forEach(p -> p.add(linkTo(methodOn(BookController.class).findById(p.getKey().toString())).withSelfRel()));

        return people;
    }

    public BookVO findById(Long id) {

        logger.info("Finding one book...");

        var entity = repository.findById(id).orElseThrow(() -> new ResourceNotFoundException("BookVO not found - id: " + id.toString()));

        BookVO vo = ModelToolMapper.parseObject(entity, BookVO.class);

        vo.add(linkTo(methodOn(BookController.class).findById(id.toString())).withSelfRel());

        return vo;
    }

    public BookVO update(BookVO book) {

        if (book == null) {

            throw new RequiredObjectIsNullException();
        }

        logger.info(String.format("Updating book: %s", book.getKey()));

        var key = book.getKey();

        var entity = repository.findById(key).orElseThrow(() -> new ResourceNotFoundException("BookVO not found - id: " + key.toString()));

        entity.setAuthor(book.getAuthor());
        entity.setLaunchDate(book.getLaunchDate());
        entity.setPrice(book.getPrice());
        entity.setTitle(book.getTitle());

        BookVO vo = ModelToolMapper.parseObject(repository.save(entity), BookVO.class);

        vo.add(linkTo(methodOn(BookController.class).findById(vo.getKey().toString())).withSelfRel());

        return vo;
    }

    private BookVO mockBook(int i) {

        BookVO book = new BookVO();

        book.setKey(Long.valueOf(i));
        book.setAuthor("Autor" + i);
        book.setLaunchDate(new Date());
        book.setPrice(Double.valueOf(i));
        book.setTitle("Titulo" + i);

        return book;
    }
}
