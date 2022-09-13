package com.sofka.alphapostcomments.usecasestest;

import co.com.sofka.domain.generic.DomainEvent;
import com.posada.santiago.alphapostsandcomments.business.gateways.DomainEventRepository;
import com.posada.santiago.alphapostsandcomments.business.gateways.EventBus;
import com.posada.santiago.alphapostsandcomments.business.usecases.CreatePostUseCase;
import com.posada.santiago.alphapostsandcomments.domain.commands.CreatePostCommand;
import com.posada.santiago.alphapostsandcomments.domain.events.PostCreated;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentMatchers;
import org.mockito.BDDMockito;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import java.util.List;

@ExtendWith(MockitoExtension.class)
public class CreatePostUseCaseTest {

    @Mock
    private EventBus eventBus;

    @Mock
    private DomainEventRepository domainEventRepository;

    @InjectMocks
    private CreatePostUseCase createPostUseCase;

    @Test
    void applyPostCreatedIfSuccesfull() {

        CreatePostCommand createPostCommand = new CreatePostCommand(
                "998877",
                "Cervantes",
                "don quijote title"
        );

        PostCreated postCreated = new PostCreated(
                createPostCommand.getAuthor(),
                createPostCommand.getTitle()
        );


        BDDMockito.when(this.domainEventRepository.saveEvent(ArgumentMatchers.any(DomainEvent.class)))
                .thenReturn(Mono.just(postCreated));

        Mono<List<DomainEvent>> triggeredEvents = this.createPostUseCase.apply(Mono.just(createPostCommand)).collectList();

        StepVerifier.create(triggeredEvents).expectSubscription().expectNextMatches(events ->
                        events.size() == 1 &&
                                events.get(0) instanceof PostCreated)
                .verifyComplete();

        BDDMockito.verify(this.eventBus, BDDMockito.times(1))
                .publish(ArgumentMatchers.any(DomainEvent.class));
        BDDMockito.verify(this.domainEventRepository, BDDMockito.times(1))
                .saveEvent(ArgumentMatchers.any(DomainEvent.class));

    }


}
