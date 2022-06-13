package com.example.shazam2.Shazam.DataBase.Processing;

import com.example.shazam2.Shazam.DataBase.LoginData;

import java.sql.*;
import java.util.ArrayList;

public class AddingMusic {

    private final String tytul;
    private final String wykonawca;
    private final int rok;
    private final String okladka;

    private final int id;

    public AddingMusic(String tytul,String wykonawca,int rok, String okladka){

        this.tytul = tytul;
        this.wykonawca = wykonawca;
        this.rok = rok;
        this.okladka = okladka;

        try (Connection connection = DriverManager.getConnection(LoginData.url, LoginData.username, LoginData.password)) {


            System.out.println("Rozpoczeto dodawanie utworu "+tytul);
            Statement state = connection.createStatement();

            ResultSet result = state.executeQuery("SELECT * FROM Utwor");

            int id = getId(result,tytul);
            if(isExist(result,tytul)){
                state.executeUpdate("DELETE FROM Hashe WHERE UtworId = "+id);
                state.executeUpdate("DELETE FROM Utwor WHERE Tytul = '"+tytul+"'");
            }

            state.executeUpdate("INSERT INTO Utwor VALUES (NULL,'"+tytul+"','"+wykonawca+"','"+rok+"','"+okladka+"')");

            result = state.executeQuery("SELECT * FROM Utwor");
            this.id = getId(result,tytul);

        } catch (SQLException e) {
            throw new IllegalStateException("Cannot connect the database!", e);
        }
    }

    private int getId(ResultSet result,String tytul) throws SQLException {
        while(result.next()){
            if(result.getString("Tytul").equals(tytul)) {
                return result.getInt("UtworId");
            }
        }
        return 0;
    }
    private boolean isExist(ResultSet result, String tytul) throws SQLException {

        while(result.next()){
            if(result.getString("Tytul").equals(tytul)) return true;
        }

       return false;
    }

    public void add(ArrayList<String> hashes, ArrayList<Integer> times) throws IllegalStateException {


        try (Connection connection = DriverManager.getConnection(LoginData.url, LoginData.username, LoginData.password)) {
            Statement state = connection.createStatement();

            int size = hashes.size();

            int licznik  = 0;

            System.out.println("Aktualizowanie utworu ... "+this.tytul);

            StringBuilder build = new StringBuilder();
            build.append("INSERT INTO Hashe \n VALUES \n");

            int n=0;

            for(String hash : hashes) {

                n++;

                int time = times.get(n-1);
                time = (int)((double)time/(double) 1000);

                if(n == hashes.size()){
                    build.append("(NULL," + id + ",'" + hash + "',"+time+")");
                }else {
                    build.append("(NULL," + id + ",'" + hash + "',"+time+"), \n");
                }
            }

            state.executeUpdate(build.toString());

            System.out.println("");

        } catch (SQLException e) {
            throw new IllegalStateException("Cannot connect the database!", e);
        }
    }


}
