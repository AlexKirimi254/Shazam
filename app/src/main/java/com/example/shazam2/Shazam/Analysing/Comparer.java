package com.example.shazam2.Shazam.Analysing;


import com.example.shazam2.Shazam.DataBase.Analysing.RecordTable;
import com.example.shazam2.Shazam.fingerprint.AudioFile;
import com.example.shazam2.Shazam.fingerprint.hash.peak.HashedPeak;

import java.io.File;
import java.io.PrintWriter;
import java.sql.*;
import java.time.Duration;
import java.time.Instant;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

public class Comparer {
    private HashFile simplifiedMusic;
    private ArrayList<Integer> counterMusic = new ArrayList<>();
    private AudioFile recordFile;

    ResultSet maximumTimes;

    private int maxTime = 0;
    private int sum = 0;
    private int max = 0;
    private int maxN = 0;
    private String[] peaks;

    private int n = 0;

    int maxId= 0;

    int count = 0;

    public Comparer(){

    }

    public void addMusic(HashFile objFile){
        simplifiedMusic = objFile;
        counterMusic.add(0);
    }

    public void addMusicFromOnline(int id){

    }
    public void setRecordFile(String[] hashes) throws Exception {
       this.peaks = hashes;
    }
    public void onlineAnalyse(Statement state) throws SQLException, AnalyseException{
        RecordTable recordTable = new RecordTable(peaks);
        recordTable.send(state);

        ArrayList<Integer> times = new ArrayList<>();


        ResultSet result = state.executeQuery("SELECT COUNT(*) as sum ,total2.UtworId FROM (SELECT record.HashId as hid1, record.HashCode as hash1, total.HashCode as hash2, total.UtworId\n" +
                "FROM (SELECT DISTINCT HashCode,UtworId FROM Hashe) as total,\n" +
                "Record as record WHERE record.HashCode = total.HashCode) as total2 GROUP BY total2.UtworId ORDER BY sum DESC;");

        boolean firstread = false;

        while (result.next()) {
            int value = result.getInt(1);
            int id = result.getInt(2);

            if(!firstread){
                maxId = id;
                max = value;
                maxN = id - 1;
            }

            sum += value;
            firstread = true;
            count++;
        }

    }

    public String compare(boolean wykrywajCzas, Statement state, Instant start) throws SQLException{

        sum = 0;
        max = 0;
        maxN = 0;

        n=0;

        try {



            onlineAnalyse(state);


            int avrg = sum/count;

            if(count>0) {


                int avrga = (sum - max) / (count - 1);

                if(max < avrga * 1.66 ){
                    throw new AnalyseException("");
                }

            }

            simplifiedMusic = new HashFile(maxN+1, state);

            if(wykrywajCzas) {
                ComparerTime time = new ComparerTime(this);
                int maxtime = time.maxTime(true, state, simplifiedMusic.getTitle(), start);
                int dettime = time.detectTime(true, state, simplifiedMusic.getTitle(), start);

                return "Wykryto: \n '" + simplifiedMusic.getTitle() + "' \n Rok:" + simplifiedMusic.getYear() + " \n Autor:" + simplifiedMusic.getAuthor() + "\n Czas trwania: " + maxtime + "s \n Czas " + dettime + " s\n \n Okladka:" + simplifiedMusic.getAlbum();
            }else{
                return "Wykryto: \n '" + simplifiedMusic.getTitle() + "' \n Rok:" + simplifiedMusic.getYear() + " \n Autor:" + simplifiedMusic.getAuthor() + " \n \n Okladka:" + simplifiedMusic.getAlbum();
            }

        }catch (AnalyseException err){
            if(err.toString().length()!=0) System.out.println("Błąd: "+err.toString());
            return "none";
        }


    }

}
