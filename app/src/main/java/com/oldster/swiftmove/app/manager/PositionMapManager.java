package com.oldster.swiftmove.app.manager;

public class PositionMapManager {

    //position from
    private double latitudeFrom = 0;
    private double longitudeFrom = 0;
    private String nameAddressFrom;
    //position to
    private double latitudeTo = 0;
    private double longitudeTo = 0;
    private String nameAddressTo;

    private String provinceFrom;
    private String typeCar;
    private float distance;


    private static PositionMapManager instance;

    public static PositionMapManager getInstance() {
        if (instance == null)
            instance = new PositionMapManager();
        return instance;
    }


    private PositionMapManager() {

    }

    public float getDistance() {
        return distance;
    }

    public void setDistance(float distance) {
        this.distance = distance;
    }

    public double getLatitudeFrom() {
        return latitudeFrom;
    }

    public void setLatitudeFrom(double latitudeFrom) {
        this.latitudeFrom = latitudeFrom;
    }

    public double getLongitudeFrom() {
        return longitudeFrom;
    }

    public void setLongitudeFrom(double longitudeFrom) {
        this.longitudeFrom = longitudeFrom;
    }

    public String getNameAddressFrom() {
        return nameAddressFrom;
    }

    public void setNameAddressFrom(String nameAddressFrom) {
        this.nameAddressFrom = nameAddressFrom;
    }

    public double getLatitudeTo() {
        return latitudeTo;
    }

    public void setLatitudeTo(double latitudeTo) {
        this.latitudeTo = latitudeTo;
    }

    public double getLongitudeTo() {
        return longitudeTo;
    }

    public void setLongitudeTo(double longitudeTo) {
        this.longitudeTo = longitudeTo;
    }

    public String getNameAddressTo() {
        return nameAddressTo;
    }

    public void setNameAddressTo(String nameAddressTo) {
        this.nameAddressTo = nameAddressTo;
    }

    public String getProvinceFrom() {
        return provinceFrom;
    }

    public void setProvinceFrom(String provinceFrom) {
        this.provinceFrom = provinceFrom;
    }

    public String getTypeCar() {
        return typeCar;
    }

    public void setTypeCar(String typeCar) {
        this.typeCar = typeCar;
    }
}
