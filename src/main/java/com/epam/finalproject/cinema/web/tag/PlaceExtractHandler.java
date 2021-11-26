package com.epam.finalproject.cinema.web.tag;

import com.epam.finalproject.cinema.web.constants.CinemaConstants;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.IOException;

import javax.servlet.jsp.JspException;
import javax.servlet.jsp.SkipPageException;
import javax.servlet.jsp.tagext.SimpleTagSupport;

public class PlaceExtractHandler extends SimpleTagSupport {
    private final static Logger log = LogManager.getLogger(PlaceExtractHandler.class);
    private int number;
    private boolean isRowExtract;

    public PlaceExtractHandler() {
    }


    public void setNumber(int number) {
        this.number = number;
    }

    public void setRow(boolean row) {
        this.isRowExtract = row;
    }

    @Override
    public void doTag() throws JspException, IOException {
        try {
            int row = 1;
            if (number > 15) {
                if (isRowExtract)
                    getJspContext().getOut().write(extractRow());
                else
                    getJspContext().getOut().write(extractPlace());
            } else {
                if (isRowExtract)
                    getJspContext().getOut().write(row);
                else
                    getJspContext().getOut().write(number);
            }
        } catch (Exception e) {
            log.debug(e.getMessage());
            String characteristic = isRowExtract ? "row" : "place";
            throw new SkipPageException("Exception in extracting " + characteristic + " with number"
                    + number);
        }
    }

    private int extractRow() {
        return number / CinemaConstants.COUNT_OF_ROW_SEAT;
    }

    private int extractPlace() {
        int row = extractRow();
        return number / row;
    }

}