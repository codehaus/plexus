package com.opensymphony.webwork.example.i18n;

/**
 * This code is an adaptation of the I18N example from the JavaWorld article by Govind Seshadri.
 * http://www.javaworld.com/javaworld/jw-03-2000/jw-03-ssj-jsp_p.html
 */
public class CD {
    String album;
    String artist;
    String country;
    double price;

    public void setAlbum(String title) {
        album = title;
    }

    public String getAlbum() {
        return album;
    }

    public void setArtist(String group) {
        artist = group;
    }

    public String getArtist() {
        return artist;
    }

    public void setCountry(String cty) {
        country = cty;
    }

    public String getCountry() {
        return country;
    }

    public void setPrice(double p) {
        price = p;
    }

    public double getPrice() {
        return price;
    }

    public boolean equals(Object obj) {
        return ((CD) obj).getAlbum().equals(getAlbum());
    }
}
