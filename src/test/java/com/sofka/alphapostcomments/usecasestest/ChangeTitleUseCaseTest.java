package com.sofka.alphapostcomments.usecasestest;

import co.com.sofka.domain.generic.DomainEvent;
import com.posada.santiago.alphapostsandcomments.business.gateways.DomainEventRepository;
import com.posada.santiago.alphapostsandcomments.business.gateways.EventBus;
import com.posada.santiago.alphapostsandcomments.business.usecases.ChangeTitleUseCase;
import com.posada.santiago.alphapostsandcomments.domain.commands.ChangeTitle;
import com.posada.santiago.alphapostsandcomments.domain.events.PostCreated;
import com.posada.santiago.alphapostsandcomments.domain.events.TitleChanged;
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
public class ChangeTitleUseCaseTest {

    @Mock
    private EventBus eventBus;

    @Mock
    private DomainEventRepository domainEventRepository;

    @InjectMocks
    private ChangeTitleUseCase changeTitleUseCase;


    @Test
    void applyTitleChangedIfSuccesfull() {

        ChangeTitle changeTitleCommand = new ChangeTitle(
                "2008",
                "changing the title"
        );

        TitleChanged titleChanged = new TitleChanged(
                changeTitleCommand.getTitle()
        );


        BDDMockito.when(this.domainEventRepository.findById(ArgumentMatchers.anyString()))
                .thenReturn(Flux.just(
                        new PostCreated(
                                changeTitleCommand.getTitle(),
                                "x author"
                        )
                ));

        BDDMockito.when(this.domainEventRepository.saveEvent(ArgumentMatchers.any(TitleChanged.class)))
                .thenReturn(Mono.just(titleChanged));

        Mono<List<DomainEvent>> triggeredEvents = this.changeTitleUseCase.apply(Mono.just(changeTitleCommand)).collectList();


        StepVerifier.create(triggeredEvents).expectSubscription().expectNextMatches(events ->
                        events.size() == 1 &&
                                events.get(0) instanceof TitleChanged)
                .verifyComplete();

        BDDMockito.verify(this.eventBus, BDDMockito.times(1))
                .publish(ArgumentMatchers.any(DomainEvent.class));
        BDDMockito.verify(this.domainEventRepository, BDDMockito.times(1))
                .saveEvent(ArgumentMatchers.any(DomainEvent.class));
        BDDMockito.verify(this.domainEventRepository, BDDMockito.times(1))
                .findById(ArgumentMatchers.anyString());

    }
}


