package com.zjianhao.album;

import com.zjianhao.bean.GridPhoto;
import com.zjianhao.bean.Photo;

import java.util.ArrayList;
import java.util.List;

public class PhotoConverter
{

    Photo pdbToPhoto(PhotoDatabase pdb)
    {
        Photo photo;
        photo = new Photo();
        photo.setLongitude(pdb.longitude);
        photo.setLatitude(pdb.latitude);
        photo.setLocation(pdb.location);
        photo.setImgUrl("file://"+pdb.path);
        photo.setId(pdb.id);
        photo.setThumbNailUrl(pdb.thumb);
        return photo;
    }
    List<Photo> pdbToPhotoList(ArrayList<PhotoDatabase> pdb)
    {
        List<Photo> _pdb = new ArrayList<>();
        for(PhotoDatabase pd : pdb)
        {
            Photo photo = pdbToPhoto(pd);
            _pdb.add(photo);
        }
        return _pdb;
    }
}
