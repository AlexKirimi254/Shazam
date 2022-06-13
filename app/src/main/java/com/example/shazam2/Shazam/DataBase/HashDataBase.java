package com.example.shazam2.Shazam.DataBase;

public class HashDataBase {
    public final int UtworId;
    public final String HashCode;
    public final int TimeHash;

    public HashDataBase(int UtworId,String HashCode,int TimeHash){
        this.UtworId = UtworId;
        this.HashCode = HashCode;
        this.TimeHash = TimeHash;
    }
}
