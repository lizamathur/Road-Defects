package com.roaddefects.defect;

import android.graphics.Bitmap;

/**
 * Denotes a single defect
 */
public class Defect {
    String defect, subType, severity, description, shortDescription, imgName, status;
    double length, width, depth, latitude, longitude;
    int userId;
    Bitmap img;

    public Defect() {
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public Bitmap getImg() {
        return img;
    }

    public void setImg(Bitmap img) {
        this.img = img;
    }

    public String getDefect() {
        return defect;
    }

    public void setDefect(String defect) {
        this.defect = defect;
    }

    public String getSubType() {
        return subType;
    }

    public void setSubType(String subType) {
        this.subType = subType;
    }

    public String getSeverity() {
        return severity;
    }

    public void setSeverity(String severity) {
        this.severity = severity;
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

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getShortDescription() {
        return shortDescription;
    }

    public void setShortDescription(String shortDescription) {
        this.shortDescription = shortDescription;
    }

    public int getUserId() {
        return userId;
    }

    public void setUserId(int userId) {
        this.userId = userId;
    }

    public String getImgName() {
        return imgName;
    }

    public void setImgName(String imgName) {
        this.imgName = imgName;
    }

    @Override
    public String toString() {
        return "Defect{" +
                "defect='" + defect + '\'' +
                ", subType='" + subType + '\'' +
                ", severity='" + severity + '\'' +
                ", description='" + description + '\'' +
                ", shortDescription='" + shortDescription + '\'' +
                ", imgName='" + imgName + '\'' +
                ", length=" + length +
                ", width=" + width +
                ", depth=" + depth +
                ", latitude=" + latitude +
                ", longitude=" + longitude +
                ", userId=" + userId +
                '}';
    }

    public String defectDetailsForMap(){
        return defect + " - " + subType + "\n" +
                "Length: " + length + " cm\n" +
                "Width: " + width + " cm\n" +
                "Depth: " + depth + " cm";
    }
}
