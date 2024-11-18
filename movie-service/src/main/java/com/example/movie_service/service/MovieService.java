package com.example.movie_service.service;

import com.example.movie_service.dto.ApiResponse;
import com.example.movie_service.dto.Comment;
import com.example.movie_service.dto.request.CatalogMovieRequest;
import com.example.movie_service.dto.request.CommentCreationRequest;
import com.example.movie_service.dto.request.RandomMovieRequest;
import com.example.movie_service.dto.response.*;
import com.example.movie_service.entity.Movie;
import com.example.movie_service.exception.AppException;
import com.example.movie_service.exception.ErrorCode;
import com.example.movie_service.mapper.MovieMapper;
import com.example.movie_service.repository.MovieRepository;
import com.example.movie_service.repository.httpClient.IdentityClient;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.List;
import java.util.Optional;
import java.util.Random;

@Service
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@Slf4j
public class MovieService {
    MovieRepository movieRepository;
    MovieMapper movieMapper;
    IdentityClient identityClient;

    public MovieResponse findMovieBySlug(String Slug) {
        Movie movie = movieRepository.findBySlug(Slug).orElseThrow(() ->
                new AppException(ErrorCode.FIND_MOVIE_BY_SLUG_FAILED));

        return movieMapper.toFindMovieResponse(movie);

    }

    public MovieResponse findMovieByID(String id) {

        Movie movie = movieRepository.findByMovieId(id).orElseThrow(() ->
                new AppException(ErrorCode.FIND_MOVIE_BY_ID_FAILED)
        );
        return movieMapper.toFindMovieResponse(movie);

    }

    public List<MovieResponse> findAllMovie() {
        return movieRepository.findAll().stream().map(movieMapper::toFindMovieResponse).toList();
    }

    public RandomMovieResponse ramdomMovies() {
        Random random = new Random();
        Boolean type = random.nextBoolean(); // Randomly assigns true or false

        List<MovieResponse> movies = movieRepository.findAllByIsSeries(type).stream()
                .filter(Optional::isPresent)
                .map(Optional::get)
                .map(movieMapper::toFindMovieResponse)
                .limit(10)
                .toList();

        return RandomMovieResponse.builder()
                .movies(movies)
                .build();
    }

    public CatalogMovieResponse catalogMovies(CatalogMovieRequest request) {
        Boolean isSeries = "true".equals(request.getIsSeries().toString().trim());
        String genre = request.getGenre();
        List<MovieResponse> movieResponses;
        if (request.getGenre() != null) {
            movieResponses = movieRepository.findAllByIsSeriesAndGenre(isSeries, genre).stream()
                    .filter(Optional::isPresent)
                    .map(Optional::get)
                    .map(movieMapper::toFindMovieResponse)
                    .toList();
        } else {
            movieResponses = movieRepository.findAllByIsSeries(isSeries).stream()
                    .filter(Optional::isPresent)
                    .map(Optional::get)
                    .map(movieMapper::toFindMovieResponse)
                    .toList();
        }
        return CatalogMovieResponse.builder()
                .movies(movieResponses)
                .build();
    }

    public InfoMovieResponse infoMovieResponse() {
        List<InfoReponse> infoReponses = movieRepository.findAll().stream().map(movieMapper::toInfoReponse).toList();
        return InfoMovieResponse.builder()
                .movies(infoReponses)
                .build();
    }

    public MovieResponse postComment(String movieId, CommentCreationRequest comment) {
        Movie movie = movieRepository.findByMovieId(movieId).orElseThrow(() ->
                new AppException(ErrorCode.FIND_MOVIE_BY_ID_FAILED));
        ApiResponse<UserResponse> response = identityClient.getMyInfo();
        Comment commentCreationRequest = Comment.builder()
                .timestamp(new Date())
                .username(response.getResult().getUsername())
                .comment(comment.getComment())
                .build();

        List<Comment> Comment = movie.getComment();
        Comment.add(commentCreationRequest);
        movie.setComment(Comment);
        movieRepository.save(movie);


        return movieMapper.toFindMovieResponse(movie);
    }

    public ListCommentReponse getComment(String movieId) {
        Movie movie = movieRepository.findByMovieId(movieId).orElseThrow(() ->
                new AppException(ErrorCode.FIND_MOVIE_BY_ID_FAILED)
        );
        List<Comment> Comment = movie.getComment();
        return ListCommentReponse.builder()
                .comments(Comment)
                .build();
    }

    public MostViewMovieResponse getMostViewMovie() {
        List<MovieResponse> movieResponses = movieRepository.findTop10ByOrderByViewDesc().stream()
                .map(movieMapper::toFindMovieResponse)
                .toList();
        return MostViewMovieResponse.builder()
                .movies(movieResponses)
                .build();
    }

    public MovieResponse updateView(String movieId) {
        Movie movie = movieRepository.findByMovieId(movieId).orElseThrow(() -> new AppException(
                ErrorCode.FIND_MOVIE_BY_ID_FAILED
        ));
        movie.setView(movie.getView() + 1);
        return movieMapper.toFindMovieResponse(movieRepository.save(movie));
    }
}
