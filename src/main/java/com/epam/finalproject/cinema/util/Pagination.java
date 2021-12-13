package com.epam.finalproject.cinema.util;

import javax.servlet.http.HttpServletRequest;
/**
 * Class that implements pagination functionality
 * @author Vlada Volska
 * @version 1.0
 * @since 2021.12.05
 *
 */
public class Pagination {
    private Pagination() {
    }

    public static int extractPage(HttpServletRequest req) {
        int page = 1;
        String paramPage = req.getParameter("page");
        if (paramPage != null)
            page = Integer.parseInt(paramPage);

        return page;
    }

    public static int extractSize(HttpServletRequest req) {
        int pageSize = 3;
        String paramPageSize = req.getParameter("pageSize");
        if (paramPageSize != null)
            pageSize = Integer.parseInt(paramPageSize);
        return pageSize;
    }

    public static void setUpAttributes(HttpServletRequest req, int page, int pageSize,
                                       int size) {
        int pageCount = 0;
        int count = size;
        while (count > 0) {
            pageCount++;
            count -= pageSize;
        }

        int minPagePossible = size == 0 ? 0 : 1;
        int maxPagePossible = size == 0 ? 0 : pageCount;
        if (page > pageCount && pageCount != 0) {
            page = 1;

            minPagePossible = req.getParameter("minPagePossible") == null ? 1 :
                    Integer.parseInt(req.getParameter("minPagePossible"));
        }

        req.setAttribute("pageCount", pageCount);
        req.setAttribute("page", page);
        req.setAttribute("pageSize", pageSize);
        req.setAttribute("minPossiblePage", minPagePossible);
        req.setAttribute("maxPossiblePage", maxPagePossible);
    }
}
