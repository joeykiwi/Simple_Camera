package com.example.jongwookim.camera_app2;

/**
 * Created by jongwookim on 2/6/15.
 */
//Photo_Image class is defined for image managing
public class Photo_Image {
    public String _data;
    public String _pic;
    public Photo_Image(){
        super();
    }
    public  Photo_Image(String data, String pic) {
        super();
        _data = data;
        _pic = pic;
    }

    public String get_name(){
        return _data;
    }


    public String get_path() {
        return _pic;
    }

    public void set_name(String name) {
        _data = name;
    }
}
