/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package systemj.bootstrap;

import java.io.FileNotFoundException;
import java.nio.file.Paths;
import systemj.desktop.MainPrinter;

/**
 *
 * @author Udayanto
 */
public class MainGeneratorSOATest {
    
    private MainPrinter mp = new MainPrinter();
    private String filename;
    
    public MainGeneratorSOATest(){
        
    }
    
    public MainGeneratorSOATest(String filename){
        this.filename = filename;
    }
    
    public void setGenSunspot(){
        mp.genSunspot = true;
    }
    
    public void generateMainSDSPOT() throws FileNotFoundException{
    //String fname = Paths.get(filename).getFileName().toString();
    //int pos = fname.lastIndexOf(".");
    //if (pos > 0) {
   //     fname = fname.substring(0, pos); 
   // }
        //mp.setGen(fname+".java");
        mp.setGenTest(filename+".java");
    }
    
    public void generateMainSDSPOTSOATest() throws FileNotFoundException{
    //String fname = Paths.get(filename).getFileName().toString();
    //int pos = fname.lastIndexOf(".");
    //if (pos > 0) {
   //     fname = fname.substring(0, pos); 
   // }
        //mp.setGen(fname+".java");
        mp.setGenTest(filename+".java");
        mp.addSPOTDummyServDesc();
    }
    
    public void flushSPOT(){
         mp.flushSPOTTest();
    }
    
}