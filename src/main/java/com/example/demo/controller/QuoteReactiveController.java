package com.example.demo.controller;

import com.example.demo.domain.Quote;
import com.example.demo.repository.QuoteMongoReactiveRepository;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.time.Duration;

@RestController
public class QuoteReactiveController {

    private static final int DELAY_PER_ITEM_MS = 1;

    private QuoteMongoReactiveRepository quoteMongoReactiveRepository;

    public QuoteReactiveController(final QuoteMongoReactiveRepository quoteMongoReactiveRepository) {
        this.quoteMongoReactiveRepository = quoteMongoReactiveRepository;
    }

    @GetMapping(value = "/quotes-reactive", produces = MediaType.TEXT_EVENT_STREAM_VALUE)
    public Flux<Quote> getQuoteFlux() {
        return quoteMongoReactiveRepository.findAll().delayElements(Duration.ofMillis(DELAY_PER_ITEMS_MS));
    }

    @GetMapping(value = "/quotes-reactive-paged", produces = MediaType.TEXT_EVENT_STREAM_VALUE)
    public Flux<Quote> getQuoteFlux(final @RequestParam(name = "page") int page,
                                    final @RequestParam(name = "size") int size) {
        return quoteMongoReactiveRepository.retrieveAllQuotesPaged(PageRequest.of(page, size))
                .delayElements(Duration.ofMillis(DELAY_PER_ITEMS_MS));
    }

    @GetMapping(value = "/quotes/{id}", produces = MediaType.TEXT_EVENT_STREAM_VALUE)
    public Mono<ResponseEntity<Quote>> getQuoteId(@PathVariable(value = "id") String quoteId) {
        return quoteMongoReactiveRepository.findById(quoteId).map(savedQuote -> ResponseEntity.ok(savedQuote))
                .defaultIfEmpty(ResponseEntity.notFound().build()).delayElement(Duration.ofMillis(DELAY_PER_ITEMS_MS));
    }

    @PutMapping(value = "/quote/{id}")
    public Mono<ResponseEntity<Quote>> updateUser(@PathVariable(value = "id") String quoteId,
                                                  String content) {
        return quoteMongoReactiveRepository.findById(quoteId)
                .flatMap(existingQuote -> {
                    existingQuote.setContent(content);
                    return quoteMongoReactiveRepository.save(existingQuote);
                }).map(updatedUser -> new ResponseEntity<>(updatedUser, HttpStatus.OK))
                .defaultIfEmpty(new ResponseEntity<>(HttpStatus.NOT_FOUND));
    }
}
