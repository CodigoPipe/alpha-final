package com.sofka.alphapostcomments.usecasestest;

import co.com.sofka.domain.generic.DomainEvent;
import com.posada.santiago.alphapostsandcomments.business.gateways.DomainEventRepository;
import com.posada.santiago.alphapostsandcomments.business.gateways.EventBus;
import com.posada.santiago.alphapostsandcomments.business.usecases.AddCommentUseCase;
import com.posada.santiago.alphapostsandcomments.domain.commands.AddCommentCommand;
import com.posada.santiago.alphapostsandcomments.domain.events.CommentAdded;
import com.posada.santiago.alphapostsandcomments.domain.events.PostCreated;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentMatchers;
import org.mockito.BDDMockito;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import java.util.List;

@ExtendWith(MockitoExtension.class)
public class AddCommentUseCaseTest {

    @Mock
    private EventBus eventBus;

    @Mock
    private DomainEventRepository domainEventRepository;

    @InjectMocks
    private AddCommentUseCase addCommentUseCase;


    @Test
    void applyCommentCreatedIfSuccesfull(){

        AddCommentCommand addComment = new AddCommentCommand(
                "34456",
                "666",
                "mefistofeles",
                "first comment test"
        );

        CommentAdded commentAdded = new CommentAdded(
                addComment.getCommentId(),
                addComment.getAuthor(),
                addComment.getContent()
        );

        BDDMockito.when(this.domainEventRepository.findById(ArgumentMatchers.anyString()))
                .thenReturn(Flux.just(
                        new PostCreated(
                                "post prueba",
                                "random author"
                        )
                ));

        BDDMockito.when(this.domainEventRepository.saveEvent(ArgumentMatchers.any(DomainEvent.class)))
                .thenReturn(Mono.just(commentAdded));

        Mono<List<DomainEvent>> triggeredEvents = this.addCommentUseCase.apply(Mono.just(addComment)).collectList();

        StepVerifier.create(triggeredEvents).expectSubscription().expectNextMatches(events ->
                events.size() == 1 &&
                events.get(0) instanceof CommentAdded)
                .verifyComplete();

        BDDMockito.verify(this.eventBus, BDDMockito.times(1))
                .publish(ArgumentMatchers.any(DomainEvent.class));
        BDDMockito.verify(this.domainEventRepository, BDDMockito.times(1))
                .saveEvent(ArgumentMatchers.any(DomainEvent.class));
        BDDMockito.verify(this.domainEventRepository, BDDMockito.times(1))
                .findById(ArgumentMatchers.anyString());
    }

}
