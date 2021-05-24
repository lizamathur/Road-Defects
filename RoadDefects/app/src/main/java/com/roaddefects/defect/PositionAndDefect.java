package com.roaddefects.defect;

public class PositionAndDefect {
    double latitude, longitude, length, width, depth;
    String defect, subType;

    public PositionAndDefect(double latitude, double longitude, String defect, String subType, double length, double width, double depth) {
        this.latitude = latitude;
        this.longitude = longitude;
        this.defect = defect;
        this.subType = subType;
        this.length = length;
        this.width = width;
        this.depth = depth;
    }

    public double getLatitude() {
        return latitude;
    }

    public void setLatitude(double latitude) {
        this.latitude = latitude;
    }


    public double getLongitude() {
        return longitude;
    }

    public void setLongitude(double longitude) {
        this.longitude = longitude;
    }

    public String getDefect() {
        return defect;
    }

    public void setDefect(String defect) {
        this.defect = defect;
    }

    public double getLength() {
        return length;
    }

    public void setLength(double length) {
        this.length = length;
    }

    public double getWidth() {
        return width;
    }

    public void setWidth(double width) {
        this.width = width;
    }

    public double getDepth() {
        return depth;
    }

    public void setDepth(double depth) {
        this.depth = depth;
    }

    public String getSubType() {
        return subType;
    }

    public void setSubType(String subType) {
        this.subType = subType;
    }

    @Override
    public String toString() {
        return "PositionAndDefect{" +
                "latitude=" + latitude +
                ", longitude=" + longitude +
                ", defect='" + defect + '\'' +
                '}';
    }

    public String defectDetailsForMap(){
        return defect + " - " + subType + "\n" +
                "Length: " + length + " cm\n" +
                "Width: " + width + " cm\n" +
                "Depth: " + depth + " cm";
    }

}
