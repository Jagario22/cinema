package com.epam.finalproject.cinema.web.command.jsp.admin;

import com.epam.finalproject.cinema.domain.film.Film;
import com.epam.finalproject.cinema.domain.Genre.Genre;
import com.epam.finalproject.cinema.exception.DBException;
import com.epam.finalproject.cinema.service.FilmService;
import com.epam.finalproject.cinema.util.FilmInfoValidationUtil;
import com.epam.finalproject.cinema.web.command.jsp.PageCommand;
import com.epam.finalproject.cinema.web.model.film.FilmInfo;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.Part;
import java.io.IOException;
import java.sql.Date;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

import static com.epam.finalproject.cinema.web.constants.SessionAttributes.ADD_MOVIE_ERROR;
import static com.epam.finalproject.cinema.web.constants.SessionAttributes.SUCCESS_MSG;
import static com.epam.finalproject.cinema.web.constants.path.Path.IMAGE_PACKAGE_PATH;
import static com.epam.finalproject.cinema.web.constants.path.Path.IMG_UPLOAD_DIRECTORY;
/**
 * Command for admins when they want to add movie
 *
 * @author Vlada Volska
 * @version 1.0
 * @since 2021.12.05
 *
 */
public class AddMovieCommand implements PageCommand {
    private final static FilmService filmService = FilmService.getInstance();
    private final static Logger log = LogManager.getLogger(AddMovieCommand.class);


    @Override
    public String execute(HttpServletRequest req, HttpServletResponse resp) throws IOException, DBException, ServletException {
        FilmInfo filmInfo = readFilmInfo(req);
        if (filmInfo != null) {
            filmService.create(filmInfo);
        }
        req.getSession().setAttribute(SUCCESS_MSG, true);
        return new ShowAddMoviePageCommand().execute(req, resp);
    }

    public FilmInfo readFilmInfo(HttpServletRequest req) throws ServletException, IOException {
        String fileName = uploadImg(req);
        String title = req.getParameter("title");
        String description = req.getParameter("description");
        String yearProd = req.getParameter("year_prod");
        String len = req.getParameter("len");
        String category = req.getParameter("category");
        String rating = req.getParameter("rating");
        String lastShowingDate = req.getParameter("last_showing_date");
        String[] genresId = req.getParameterValues("genres");


        if (title == null || yearProd == null || category == null ||
                description == null || lastShowingDate == null || genresId == null || genresId.length == 0) {
            req.getSession().setAttribute(ADD_MOVIE_ERROR, "Data is incomplete");
            return null;
        }



        FilmInfo filmInfo = new FilmInfo(new Film(title, description, yearProd, Integer.parseInt(category),
                Date.valueOf(lastShowingDate), IMAGE_PACKAGE_PATH + "/" + fileName),
                getGenres(genresId));

        if (len != null) {
            filmInfo.getFilm().setLen(Integer.parseInt(len));
        }
        if (rating != null) {
            filmInfo.getFilm().setRating(Float.parseFloat(rating));
        }

        if (!validateFilm(filmInfo)) {
            throw new IllegalArgumentException("Not valid data");
        }
        return filmInfo;
    }


    public String uploadImg(HttpServletRequest req) throws ServletException, IOException {
        Part filePart = req.getPart("img");
        String fileName = filePart.getSubmittedFileName();
        String path = req.getServletContext().getRealPath(IMG_UPLOAD_DIRECTORY + fileName);
        filePart.write(path);

        return fileName;
    }


    public List<Genre> getGenres(String[] genresId) {
        List<Genre> genres = new ArrayList<>();
        for (String genreId : genresId) {
            genres.add(new Genre(Integer.parseInt(genreId)));
        }

        return genres;
    }


    public boolean validateFilm(FilmInfo filmInfo) {
        boolean isValid = true;

        if (filmInfo.getFilm().getLen() != 0 && !FilmInfoValidationUtil.isValidLen(filmInfo.getFilm().getLen())) {
            isValid = false;
        }

        if (filmInfo.getFilm().getRating() != 0 && !FilmInfoValidationUtil.isValidRating(filmInfo.getFilm().getRating())) {
            isValid = false;
        }

        if (!FilmInfoValidationUtil.isValidYear(filmInfo.getFilm().getYear())) {
            isValid = false;
        }

        if (!FilmInfoValidationUtil.isValidDescr(filmInfo.getFilm().getDescr())) {
            isValid = false;
        }

        if (!FilmInfoValidationUtil.isValidTitle(filmInfo.getFilm().getTitle())) {
            isValid = false;
        }

        if (!FilmInfoValidationUtil.isValidLastShowingDate(LocalDate.parse(filmInfo.getFilm().
                getLastShowingDate().toString()))) {
            isValid = false;
        }

       return isValid;
    }
}
