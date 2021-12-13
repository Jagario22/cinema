package com.epam.finalproject.cinema.web.tag;

import com.epam.finalproject.cinema.web.constants.CinemaConstants;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.IOException;
import java.io.Serializable;

import javax.servlet.jsp.JspException;
import javax.servlet.jsp.SkipPageException;
import javax.servlet.jsp.tagext.SimpleTagSupport;

import static com.epam.finalproject.cinema.web.constants.CinemaConstants.COUNT_OF_ROW_SEAT;

/**
 * TagSupport class for extracting place from number
 *
 * @author Vlada Volska
 * @version 1.0
 * @since 2021.12.05
 */
public class PlaceExtractHandler extends SimpleTagSupport {
    private final static Logger log = LogManager.getLogger(PlaceExtractHandler.class);
    private int number;
    private boolean isRowExtract;

    public PlaceExtractHandler() {
    }


    public void setNumber(int number) {
        this.number = number;
    }


    public void setIsRowExtract(boolean rowExtract) {
        isRowExtract = rowExtract;
    }

    @Override
    public void doTag() throws JspException, IOException {
        try {
            int row = 1;
            if (number > 15) {
                if (isRowExtract) {
                    int rowNum = extractRow();
                    getJspContext().getOut().write(String.valueOf(rowNum));
                } else {
                    int place = extractPlace();
                    getJspContext().getOut().write(String.valueOf(place));
                }
            } else {
                if (isRowExtract) {
                    getJspContext().getOut().write(String.valueOf(row));
                } else {
                    getJspContext().getOut().write(String.valueOf(number));
                }
            }
        } catch (Exception e) {
            log.debug(e.getMessage());
            String characteristic = isRowExtract ? "row" : "place";
            throw new SkipPageException("Exception in extracting " + characteristic + " with number"
                    + number);
        }
    }

    private int extractRow() {
       int row = 1;
       for (int i = 1; i <= CinemaConstants.COUNT_OF_ROWS; i++) {
            if (i * COUNT_OF_ROW_SEAT > number) {
                row = i;
                break;
            }
       }
       return row;
    }

    private int extractPlace() {
        int place = number;
        while (place > COUNT_OF_ROW_SEAT) {
            place -= COUNT_OF_ROW_SEAT;
        }

        return place;
    }

}