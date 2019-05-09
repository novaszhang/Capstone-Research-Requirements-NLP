package com.example.CoreNLPProject;

import java.util.*;

import edu.stanford.nlp.pipeline.Annotation;
import edu.stanford.nlp.pipeline.*;

public class EntityParserExample {
    public static void main(String[] args) {
        Properties props = new Properties();
        props.setProperty("annotators", "tokenize, ssplit, pos, lemma, ner, parse, dcoref, sentiment");

        StanfordCoreNLP pipeline = new StanfordCoreNLP(props);
        Annotation annotation = new Annotation("Operate the system to maintain zone temperatures down to 55°F (13°C) or up to 85°F (29°C).");
        pipeline.annotate(annotation);
    }
}
