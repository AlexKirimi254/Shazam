package com.example.shazam2.Shazam.DataBase.Processing;

import com.example.shazam2.Shazam.DataBase.HashDataBase;
import com.example.shazam2.Shazam.DataBase.LoginData;

import java.sql.*;
import java.util.ArrayList;
import java.util.HashMap;

public class Preparing {
    public Preparing(){


        try (Connection connection = DriverManager.getConnection(LoginData.url, LoginData.username, LoginData.password)) {


            Statement state = connection.createStatement();

            ResultSet result = state.executeQuery("SELECT * FROM Hashe");


            StringBuilder builder = new StringBuilder();
            builder.append("INSERT INTO Hashe \n VALUES \n");

            ArrayList<HashDataBase> hashe = new ArrayList<HashDataBase>();

            while (result.next()){
                hashe.add(new HashDataBase(result.getInt("UtworId"),result.getString("HashCode"),result.getInt("TimeHash")));
            }

            int n=0;

            for(HashDataBase keys : hashe){
                n++;
                if(n == hashe.size()){
                    builder.append("(NULL," + keys.UtworId + ",'" + keys.HashCode+ "',"+keys.TimeHash+")");
                }else {
                    builder.append("(NULL," + keys.UtworId+ ",'" + keys.HashCode + "',"+keys.TimeHash+"), \n");
                }
            }
            state.executeUpdate("DELETE FROM Hashe");

            state.executeUpdate("ALTER TABLE Hashe AUTO_INCREMENT = 1");
            state.executeUpdate(builder.toString());

            connection.close();

        } catch (SQLException e) {
            throw new IllegalStateException("Cannot connect the database!", e);
        }
    }
}
