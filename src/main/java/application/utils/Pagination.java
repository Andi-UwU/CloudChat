package application.utils;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

public class Pagination {
    public static <E> List<E> getPage(List<E> list, int page, int pageSize) throws IllegalArgumentException{
        if (pageSize <= 0) {
            throw new IllegalArgumentException("Invalid page size: " + pageSize + "!\n");
        }
        if (page  <= 0 || pageSize < page) {
            throw new IllegalArgumentException("Invalid page number: " + page + "!\n");
        }

        int startIndex = (page - 1) * pageSize;
        if (list == null || list.size() <= startIndex) {
            return Collections.emptyList();
        }

        // toIndex exclusive
        return list.subList(startIndex, Math.min(startIndex + pageSize, list.size()));
    }
    public static <E> Integer getNumberOfPages(List<E> list, Integer pageSize){
        int size = list.size();
        int mod = size % pageSize;
        int additionalPage = 0;
        if (mod > 0) additionalPage = 1;
        return (size / pageSize + additionalPage);
    }
}
