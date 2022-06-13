package com.example.shazam2.Shazam.Audio;

import com.example.shazam2.Shazam.fingerprint.AudioFile;
import com.example.shazam2.Shazam.fingerprint.hash.peak.HashedPeak;

import java.io.File;
import java.util.ArrayList;

public class ShzazamRecorder {

    ArrayList<String> hashes = new ArrayList<>();
    int k=0;

    public ShzazamRecorder(){

    }

    private void oneIter(int n,String path) throws Exception{

        AudioFile  recordFile = new AudioFile(new File(path));

        for ( HashedPeak peak : recordFile.getFingerPrint().getHashes()) {
            hashes.add(peak.getHashAsHex());

        }

    }

    public void listen(String path) throws Exception {

        oneIter(k,path);
    }

    public String[] getHashes(){
        String[] haS = new String[hashes.size()];

        for(int k=0;k<haS.length;k++){
            haS[k] = hashes.get(k);
        }

        return haS;
    }
}
