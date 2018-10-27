package tw.alex.networktest3;

import java.io.Serializable;

public class Food implements Serializable {

    private String name, address, tel, hostwords, feature, coordinate, picurl;

    public void setName(String name){this.name = name; }
    public void setAddress(String address){this.address = address; }
    public void setTel(String tel){this.tel = tel; }
    public void setHostwords(String hostwords){this.hostwords = hostwords; }
    public void setFeature(String feature){this.feature = feature; }
    public void setCoordinate(String coordinate){this.coordinate = coordinate; }
    public void setPicurl(String picurl){this.picurl = picurl; }

    public String getName(){return name; }
    public String getAddress(){return address; }
    public String getTel(){return tel; }
    public String getHostwords(){return hostwords; }
    public String getFeature(){return feature; }
    public String getCoordinate(){return coordinate; }
    public String getPicurl(){return picurl; }

}

