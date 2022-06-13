package com.example.shazam2.Shazam.DataBase.Processing;

import com.example.shazam2.Shazam.DataBase.LoginData;

import java.sql.*;
import java.util.ArrayList;

public class GetMusicList {

    private Statement state;
    public GetMusicList(Statement state){
        this.state = state;
    }
    public int[] musicIds() throws SQLException {


            ResultSet result = state.executeQuery("SELECT UtworId FROM Utwor");
            ArrayList<Integer> utwory = new ArrayList<>();

            while (result.next()){
                utwory.add(result.getInt("UtworId"));
            }

            int[] array = new int[utwory.size()];
            int n=0;

            for(Integer num : utwory){
                array[n] = num;
                n++;
            }

            return array;

    }
}
