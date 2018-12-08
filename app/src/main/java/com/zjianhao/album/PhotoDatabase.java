package com.zjianhao.album;

import android.os.Parcel;
import android.os.Parcelable;

import java.io.Serializable;

public class PhotoDatabase implements Parcelable
{
   PhotoDatabase(){}
   public PhotoDatabase(Parcel in) {
      this.path = in.readString();
      this.id = in.readInt();
   }
   public double latitude;
   public double longitude;
   public String thumb;
   public String title;
   public String path;
   public String location;
   public String date;
   public int id;

   @Override
   public int describeContents() {
      return 0;
   }

   @Override
   public void writeToParcel(Parcel dest, int flags) {
      dest.writeString(this.path);
      dest.writeInt(this.id);
   }

   @SuppressWarnings("rawtypes")
   public static final Parcelable.Creator CREATOR = new Parcelable.Creator() {

      @Override
      public PhotoDatabase createFromParcel(Parcel in) {
         return new PhotoDatabase(in);
      }

      @Override
      public PhotoDatabase[] newArray(int size) {
         // TODO Auto-generated method stub
         return new PhotoDatabase[size];
      }

   };


}