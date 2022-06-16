package com.memy.pojo;

/**
 * Class helps {@link com.cogarden.cogarden.authenticate.adapter.CountryCodeAdapter}
 * */
public class CountryListObj {

    private String countryName;
    private int countryCode;
    private String countryNameHint;

    public String getCountryNameHint() {
        return countryNameHint;
    }

    public void setCountryNameHint(String countryNameHint) {
        this.countryNameHint = countryNameHint;
    }

    public String getCountryName()
    {
        return countryName;
    }
    public void setCountryName(String countryName)
    {
        this.countryName= countryName;
    }

    public int getCountryCode()
    {
        return countryCode;
    }
    public void setCountryCode(int countryCode)
    {
        this.countryCode =countryCode;
    }
}
