package com.niit.UserMovieService.service;


import com.niit.UserMovieService.domain.Movie;
import com.niit.UserMovieService.domain.User;
import com.niit.UserMovieService.exception.UserAlreadyExistsException;
import com.niit.UserMovieService.exception.UserNotFoundException;
import com.niit.UserMovieService.proxy.UserAuthProxy;
import com.niit.UserMovieService.repository.UserMovieRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.util.Arrays;
import java.util.List;

@Service
public class UserMovieServiceImpl implements UserMovieService{

    private UserMovieRepository userMovieRepository;
    private UserAuthProxy userAuthProxy;

    @Autowired
    public UserMovieServiceImpl(UserMovieRepository userMovieRepository, UserAuthProxy userAuthProxy)
    {
       this.userMovieRepository = userMovieRepository;
       this.userAuthProxy = userAuthProxy;
    }

    @Override
    public User registerUser(User user) throws UserAlreadyExistsException {
        if (userMovieRepository.findById(user.getEmail()).isPresent())
        {
            throw new UserAlreadyExistsException();
        }
        ResponseEntity<?> response = userAuthProxy.saveUser(user);
        if (response.getStatusCodeValue()==201) {
            return userMovieRepository.save(user);
        }
        else {
            return null;
        }
    }

    @Override
    public User saveUserMovieToList(Movie movie, String email) throws UserNotFoundException {
        if (userMovieRepository.findById(email).isEmpty())
        {
            throw new UserNotFoundException();
        }
        User user = userMovieRepository.findByEmail(email);
        if (user.getMovieList() == null)
        {
            user.setMovieList(Arrays.asList(movie));
        }
        else
        {
            List<Movie> movies = user.getMovieList();
            movies.add(movie);
            user.setMovieList(movies);
        }
        return userMovieRepository.save(user);
    }
}
