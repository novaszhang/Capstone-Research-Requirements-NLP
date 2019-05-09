package com.example.CoreNLPProject;

import edu.stanford.nlp.ling.CoreAnnotations;
import edu.stanford.nlp.ling.CoreLabel;
import edu.stanford.nlp.pipeline.Annotation;
import edu.stanford.nlp.pipeline.StanfordCoreNLP;

import java.io.File;
import java.io.FileWriter;
import java.io.PrintWriter;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Properties;
import java.util.Scanner;

public class CreateLabeledTrain {
    //Requirements: Text files with requirements in each one
    public static void startTSV(String sourceDir, String destDir) throws Exception {
        Properties props = new Properties();
        props.setProperty("annotators", "tokenize");

        StanfordCoreNLP pipeline = new StanfordCoreNLP(props);
        File file;
        Scanner sc;
        for(int i=0;i<163;i++){
            String data = "";
            file = new File(sourceDir+"req"+i+".txt");
            sc = new Scanner(file, "UTF-8");
            while(sc.hasNextLine()){
                data = data + sc.nextLine();
            }

            Annotation annotation = new Annotation(data);
            pipeline.annotate(annotation);

            FileWriter out = new FileWriter(destDir+"token"+i+".txt");
            for (CoreLabel token: annotation.get(CoreAnnotations.TokensAnnotation.class)) {
                // this is the text of the token
                String word = token.get(CoreAnnotations.TextAnnotation.class);
                out.write(word+"\n");
            }
            out.close();
        }
    }

    public static void main(String[] args) throws Exception{
        startTSV("C:\\Users\\novaz\\Documents\\UVA18-19\\Spring\\Research\\requirements\\",
                "C:\\Users\\novaz\\Documents\\UVA18-19\\Spring\\Research\\token_requirements\\");

    }
}
