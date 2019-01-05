package com.padshift.sonic.service.impl;

import com.padshift.sonic.entities.Genre;
import com.padshift.sonic.repository.GenreRepository;
import com.padshift.sonic.service.GenreService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;

/**
 * Created by ruzieljonm on 06/08/2018.
 */
@Service("genreService")
public class GenreServiceImpl implements GenreService{
    @Autowired
    GenreRepository genreRepository;

    @Override
    public ArrayList<Genre> findAll() {
        return (ArrayList<Genre>) genreRepository.findAll();
    }
}
