package com.example.shazam2.Shazam.Analysing;

import com.example.shazam2.Shazam.fingerprint.AudioFile;
import com.example.shazam2.Shazam.fingerprint.hash.peak.HashedPeak;

import java.io.*;
import java.sql.*;
import java.util.ArrayList;

public class HashFile {

    private String title = "";
    private String author = "";
    private String year = "";
    private String album = "";

    public boolean online;
    public int idOnline;

    private File file;

    private ArrayList<String> hashes ;
    private ArrayList<Integer> times;

    public HashFile(int id,Statement statement) throws SQLException{
        online = true;
        idOnline = id;
        readFromDataBase(id,statement);
    }
    public HashFile(File file){
        online = false;
        hashes = new ArrayList<>();
        this.file = file;

    }

    public void setTitle(String title){
        this.title = title;
    }

    public void setAuthor(String author){
        this.author = author;
    }

    public void setYear(String year) throws Exception{

        char[] znaki = year.toCharArray();

        boolean isNumber = true;

        if(znaki.length==4){
            for(char znak: znaki){
                if(znak <=47 || znak >=58) isNumber = false;
            }

            if(isNumber){
                this.year = year;
            }else{
                throw new Exception("To nie jest data");
            }

        }else{
            throw new Exception("To nie jest data");
        }

    }

    public void setAlbum(String album){
        this.album = album;
    }

    public String getAlbum() {
        return album;
    }

    public String getTitle() {
        return title;
    }

    public String getYear() {
        return year;
    }

    public String getAuthor() {
        return author;
    }

    public void convert(File audioFile) throws Exception {
        AudioFile fileA = new AudioFile(audioFile.getAbsoluteFile());

        HashedPeak[] peaks = fileA.getFingerPrint().getHashes();
        ArrayList<String> peaksHex = new ArrayList<>();

        times = new ArrayList<>();

        for(HashedPeak peak : peaks){
            peaksHex.add(peak.getHashAsHex());
            times.add(peak.getTime());
        }

        hashes = peaksHex;


    }

    public boolean contains(String hashPeak){
        return hashes.contains(hashPeak);
    }

    public String[] getHashes(){
        String[] hashesPeaks = new String[hashes.size()];

        int n=0;
        for(String hash: hashes){
            hashesPeaks[n] = hash;
            n++;
        }

        return hashesPeaks;
    }

    public int[] getTimes(){
        int[] timesMe = new int[hashes.size()];

        int n=0;
        for(Integer num: times){
            timesMe[n] = num;
            n++;
        }

        return timesMe;
    }

    public void readFromDataBase(int idUtworu, Statement state) throws SQLException {


            ResultSet result = state.executeQuery("SELECT * FROM Utwor WHERE UtworId = "+idUtworu);

            while(result.next()) {
                title = result.getString("Tytul");
                author = result.getString("Wykonwaca");
                year = result.getString("Rok");
                album = result.getString("Okladka");
            }


    }

    public void read() throws IOException {
        FileInputStream stream = new FileInputStream(file);
        BufferedInputStream streamBuf = new BufferedInputStream(stream);
        InputStreamReader streamReader = new InputStreamReader(streamBuf);
        LineNumberReader reader = new LineNumberReader(streamReader);

        int n=0;
        String line ="";

        hashes.clear();
        while ((line = reader.readLine()) !=null){

            switch (n){
                case 0 :
                    title = line;
                    break;
                case 1:
                    author = line;
                    break;
                case 2:
                    year = line;
                    break;
                case 3:
                    album = line;
                    break;
                default:
                    hashes.add(line);
                    break;
            }
            n++;
        }
    }
}
