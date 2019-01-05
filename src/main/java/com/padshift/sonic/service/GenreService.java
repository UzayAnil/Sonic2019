package com.padshift.sonic.service;

import com.padshift.sonic.entities.Genre;
import org.springframework.stereotype.Service;

import java.util.ArrayList;

/**
 * Created by ruzieljonm on 06/08/2018.
 */
@Service
public interface GenreService {
    ArrayList<Genre> findAll();
}
