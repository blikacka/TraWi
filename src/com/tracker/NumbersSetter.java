package com.tracker;

import android.graphics.Bitmap;

/**
 *
 * @author Notasek
 */
public class NumbersSetter {

    public String number;
    public LatLongSetter parser, prevParser, prevPrevParser;
    public String alias;
    public Bitmap color, colorPrev, colorPrevPrev;

    public String getNumber() {
        return number;
    }

    public void setNumber(String number) {
        this.number = number;
    }

    public LatLongSetter getParser() {
        return parser;
    }

    public void setParser(LatLongSetter parser) {
        this.parser = parser;
    }

    public LatLongSetter getPrevParser() {
        return prevParser;
    }

    public void setPrevParser(LatLongSetter prevParser) {
        this.prevParser = prevParser;
    }

    public LatLongSetter getPrevPrevParser() {
        return prevPrevParser;
    }

    public void setPrevPrevParser(LatLongSetter prevPrevParser) {
        this.prevPrevParser = prevPrevParser;
    }

    public String getAlias() {
        return alias;
    }

    public void setAlias(String alias) {
        this.alias = alias;
    }

    public Bitmap getColor() {
        return color;
    }

    public void setColor(Bitmap color) {
        this.color = color;
    }

    public Bitmap getColorPrev() {
        return colorPrev;
    }

    public void setColorPrev(Bitmap colorPrev) {
        this.colorPrev = colorPrev;
    }

    public Bitmap getColorPrevPrev() {
        return colorPrevPrev;
    }

    public void setColorPrevPrev(Bitmap colorPrevPrev) {
        this.colorPrevPrev = colorPrevPrev;
    }

 

 
}
