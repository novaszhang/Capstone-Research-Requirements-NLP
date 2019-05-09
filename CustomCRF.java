package com.example.CoreNLPProject;

import edu.stanford.nlp.ie.crf.CRFClassifier;
import edu.stanford.nlp.ling.CoreLabel;
import edu.stanford.nlp.sequences.SeqClassifierFlags;
import edu.stanford.nlp.util.StringUtils;

import java.io.File;
import java.io.FileWriter;
import java.util.*;

public class CustomCRF {

    public static void trainAndWrite(String modelOutPath, String prop, String trainingFilepath) {
        Properties props = StringUtils.propFileToProperties(prop);
        props.setProperty("serializeTo", modelOutPath);
        //if input use that, else use from properties file.
        if (trainingFilepath != null) {
            props.setProperty("trainFile", trainingFilepath);
        }
        SeqClassifierFlags flags = new SeqClassifierFlags(props);
        CRFClassifier<CoreLabel> crf = new CRFClassifier<>(flags);
        crf.train();
        crf.serializeClassifier(modelOutPath);
    }

    public static CRFClassifier getModel(String modelPath) {
        return CRFClassifier.getClassifierNoExceptions(modelPath);
    }

    public static void doTagging(CRFClassifier model, String input) throws Exception{
        input = input.trim();
        FileWriter out = new FileWriter("C:\\Users\\novaz\\Documents\\UVA18-19\\Spring\\Research\\results.txt",true);
        out.write(model.classifyToString(input)+"\n");
    }

    public static void main(String[] args) throws Exception{

//        trainAndWrite("C:\\Users\\novaz\\Documents\\UVA18-19\\Spring\\Research\\custom-ner-model.ser.gz",
//                "C:\\Users\\novaz\\IdeaProjects\\CoreNLPProject\\src\\com\\example\\CoreNLPProject\\custom.properties",
//                "C:\\Users\\novaz\\Documents\\UVA18-19\\Spring\\Research\\train.tsv"
//                );

//        File file = new File("C:\\Users\\novaz\\Documents\\UVA18-19\\Spring\\Research\\test.tsv");
//        Scanner sc = new Scanner(file, "UTF-8");
////
//        while(sc.hasNextLine())
        doTagging(getModel("C:\\Users\\novaz\\Documents\\UVA18-19\\Spring\\Research\\custom-ner-model.ser.gz"),
                "A dog park with lighting shall not remain open later than 10 p.m.");
    }
}
