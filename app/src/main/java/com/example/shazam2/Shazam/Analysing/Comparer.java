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
    private ArrayList<HashFile> simplifiedMusic = new ArrayList<>();
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

    public Comparer(){

    }

    public void addMusic(HashFile objFile){
        simplifiedMusic.add(objFile);
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
        }

    }

    public void analyseOffline() throws AnalyseException{
        for (HashFile file : simplifiedMusic) {

            for (String hash : peaks) {
                if (file.contains(hash)) {
                    int number = counterMusic.get(n);
                    number++;
                    counterMusic.set(n, number);
                }
            }

            if (max < counterMusic.get(n)) {
                max = counterMusic.get(n);
                maxN = n;
            }

            sum += counterMusic.get(n);

            n++;


        }
    }

    public String compare(boolean isConnected, Statement state, Instant start) throws SQLException{

        sum = 0;
        max = 0;
        maxN = 0;

        n=0;

        try {



            if(isConnected) {

                onlineAnalyse(state);

            }else {
                analyseOffline();
            }

            int avrg = sum/simplifiedMusic.size();

            if(simplifiedMusic.size()>0) {


                int avrga = (sum - max) / (simplifiedMusic.size() - 1);

                if(max < avrga * 1.25 ){
                    throw new AnalyseException("");
                }

            }

            return simplifiedMusic.get(maxN).getTitle() + " srednia:"+ avrg+" max:"+max;


        }catch (AnalyseException err){
            if(err.toString().length()!=0) System.out.println("Błąd: "+err.toString());
            return "";
        }


    }


}
