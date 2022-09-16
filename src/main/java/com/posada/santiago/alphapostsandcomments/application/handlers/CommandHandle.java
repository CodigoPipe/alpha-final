package com.posada.santiago.alphapostsandcomments.application.handlers;


import co.com.sofka.domain.generic.DomainEvent;
import com.posada.santiago.alphapostsandcomments.business.usecases.AddCommentUseCase;
import com.posada.santiago.alphapostsandcomments.business.usecases.ChangeTitleUseCase;
import com.posada.santiago.alphapostsandcomments.business.usecases.CreatePostUseCase;
import com.posada.santiago.alphapostsandcomments.domain.commands.AddCommentCommand;
import com.posada.santiago.alphapostsandcomments.domain.commands.ChangeTitle;
import com.posada.santiago.alphapostsandcomments.domain.commands.CreatePostCommand;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.MediaType;
import org.springframework.web.reactive.function.BodyInserters;
import org.springframework.web.reactive.function.server.RouterFunction;
import org.springframework.web.reactive.function.server.ServerResponse;

import static org.springframework.web.reactive.function.server.RequestPredicates.*;
import static org.springframework.web.reactive.function.server.RouterFunctions.route;
@Slf4j
@Configuration
public class CommandHandle {

    @Bean
    public RouterFunction<ServerResponse> createPost(CreatePostUseCase useCase){

        return route(
                POST("/create/post").and(accept(MediaType.APPLICATION_JSON)),
                request -> ServerResponse.ok().contentType(MediaType.APPLICATION_JSON)
                        .body(BodyInserters.fromPublisher(useCase.apply(request.bodyToMono(CreatePostCommand.class)), DomainEvent.class))
                        .onErrorResume(error -> {
                            log.error(error.getMessage());
                            return ServerResponse.badRequest().build();
                        })
        );
    }

    @Bean
    public RouterFunction<ServerResponse> addComment(AddCommentUseCase useCase){

        return route(
                POST("/add/comment").and(accept(MediaType.APPLICATION_JSON)),
                request -> ServerResponse.ok().contentType(MediaType.APPLICATION_JSON)
                        .body(BodyInserters.fromPublisher(useCase.apply(request.bodyToMono(AddCommentCommand.class)), DomainEvent.class)).onErrorResume(error -> {
                            log.error(error.getMessage());
                            return ServerResponse.badRequest().build();
                        })
        );
    }

    @Bean
    public RouterFunction<ServerResponse> changeTitle(ChangeTitleUseCase useCase){

        return route(
                PUT("/change/title").and(accept(MediaType.APPLICATION_JSON)),
                request -> ServerResponse.ok().contentType(MediaType.APPLICATION_JSON)
                        .body(BodyInserters.fromPublisher(useCase.apply(request.bodyToMono(ChangeTitle.class)), DomainEvent.class)).onErrorResume(error -> {
                            log.error(error.getMessage());
                            return ServerResponse.badRequest().build();
                        })
        );
    }
}
