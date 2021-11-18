package com.mcarmen.flickrapi;

import com.mcarmen.flickrapi.Photo;

import java.io.Serializable;
import java.util.ArrayList;

public class Photos implements Serializable {
    public String total;
    public int page;
    public int pages;
    public int perPage;
    public ArrayList<Photo> photo;


    public Photos() {
    }

    public int getPage() {
        return page;
    }

    public void setPage(int page) {
        this.page = page;
    }

    public int getPages() {
        return pages;
    }

    public void setPages(int pages) {
        this.pages = pages;
    }

    public int getPerPage() {
        return perPage;
    }

    public void setPerPage(int perPage) {
        this.perPage = perPage;
    }

    public String getTotal() {
        return total;
    }

    public void setTotal(String total) {
        this.total = total;
    }

    public ArrayList<Photo> getPhoto() {
        return photo;
    }

    public void setPhoto(ArrayList<Photo> photo) {
        this.photo = photo;
    }
}
