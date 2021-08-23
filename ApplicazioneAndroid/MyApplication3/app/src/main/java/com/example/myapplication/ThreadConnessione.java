package com.example.myapplication;

import android.annotation.TargetApi;
import android.icu.text.Edits;
import android.os.Build;
import android.os.Environment;
import android.widget.Spinner;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.UUID;

import entita.Pacchetto;

public class ThreadConnessione extends Thread implements Serializable {

    String pathSave;
    Spinner sp1, sp2;
    MainActivity ma;
    boolean terminato=false;

    public ThreadConnessione(MainActivity ma, String path, Spinner sp1, Spinner sp2){
        this.ma=ma;
        this.sp1=sp1;
        this.sp2=sp2;
        this.pathSave=path;
    }

 //   @TargetApi(Build.VERSION_CODES.O)
    public void run() {

        if (!sp1.getSelectedItem().toString().equals(sp2.getSelectedItem().toString())) {
            try {
                Socket s = new Socket("192.168.43.186", 8080);
                ObjectOutputStream oos = new ObjectOutputStream(s.getOutputStream());
                Pacchetto file = new Pacchetto();
                //file.fileAudio = Files.readAllBytes(Paths.get(pathSave));
                File file1 = new File(pathSave);
                FileInputStream fis = new FileInputStream(file1);
                ByteArrayOutputStream bos = new ByteArrayOutputStream();
                byte[] buf = new byte[1024];
                try {
                    for (int readNum; (readNum = fis.read(buf)) != -1; ) {
                        bos.write(buf, 0, readNum);
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }
                byte[] bytes = bos.toByteArray();
                file.setFileAudio(bytes);

                try {
                    file.setLinguaOriginale(sp1.getSelectedItem().toString());
                    file.setLinguaTraduzione(sp2.getSelectedItem().toString());
                    oos.writeObject(file);
                    oos.flush();
                    ObjectInputStream ois = new ObjectInputStream(s.getInputStream());
                    Pacchetto fileTradotto;
                    try {
                        fileTradotto = (Pacchetto) ois.readObject();
                        //                      String pathFinale = Environment.getExternalStorageDirectory().getAbsolutePath() + "/" + UUID.randomUUID().toString() + "_audio_record.3gp";
                        final String path = Environment.getExternalStorageDirectory().getAbsolutePath() + "/Traduttore/";
                        //File f = new File(path);
                        //if(!f.exists()){
                        //    f.mkdir();
                        //}
                        ma.pathFinale =  path + "audioTradotto"+ma.registrazioni+".ogg";

                        // Files.createFile(Paths.get(ma.pathFinale));
                        try {
                            File outputFile = File.createTempFile("file", "ogg", new File(path));
                            FileOutputStream fos = new FileOutputStream(ma.pathFinale);
                            fos.write(fileTradotto.getFileAudio());
                            fos.close();
                            outputFile.delete();
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                        ma.terminato = true;
                        s.close();
                    } catch (ClassNotFoundException e) {
                        e.printStackTrace();
                    }

                } catch (Exception e) {
                } //try dell'ip da internet
            } catch (
                    IOException e) {
                e.printStackTrace();
            }
        }
    }
}
