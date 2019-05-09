package com.example.CoreNLPProject;

import java.io.*;
import java.util.*;

import edu.stanford.nlp.coref.CorefCoreAnnotations;

import edu.stanford.nlp.coref.data.CorefChain;
import edu.stanford.nlp.io.*;
import edu.stanford.nlp.ling.*;
import edu.stanford.nlp.pipeline.*;
import edu.stanford.nlp.semgraph.SemanticGraph;
import edu.stanford.nlp.semgraph.SemanticGraphCoreAnnotations;
import edu.stanford.nlp.semgraph.SemanticGraphEdge;
import edu.stanford.nlp.sentiment.SentimentCoreAnnotations;
import edu.stanford.nlp.trees.*;
import edu.stanford.nlp.util.*;

/** This class demonstrates building and using a Stanford CoreNLP pipeline. */
public class StanfordCoreNlpDemo {

    private StanfordCoreNlpDemo() { } // static main method

    /** Usage: java -cp "*" StanfordCoreNlpDemo [inputFile [outputTextFile [outputXmlFile]]] */
    public static void main(String[] args) throws IOException {
        // set up optional output files
        PrintWriter out;
        if (args.length > 1) {
            out = new PrintWriter(args[1]);
        } else {
            out = new PrintWriter(System.out);
        }
        PrintWriter xmlOut = null;
        if (args.length > 2) {
            xmlOut = new PrintWriter(args[2]);
        }

        Properties props = new Properties();
        props.setProperty("annotators", "tokenize, ssplit, pos, lemma, parse, dcoref, sentiment");

        StanfordCoreNLP pipeline = new StanfordCoreNLP(props);

        // Initialize an Annotation
        Annotation annotation;
        if (args.length > 0) {
            annotation = new Annotation(IOUtils.slurpFileNoExceptions(args[0]));
        } else {
            annotation = new Annotation("No plat shall be approved " +
                    "by the Hearing Examiner covering any land situated in " +
                    "the Shoreline District unless in compliance with Section " +
                    "23.60A.156 and conformance to the applicable provisions of " +
                    "Section 23.60A.168.");
        }

        // run all the selected Annotators on this text
        pipeline.annotate(annotation);

        // this prints out the results of sentence analysis to file(s) in good formats
        pipeline.prettyPrint(annotation, out);
        if (xmlOut != null) {
            pipeline.xmlPrint(annotation, xmlOut);
        }

        out.println();
        out.println("The top level annotation");
        out.println(annotation.toShorterString());
        out.println();

        List<CoreMap> sentences = annotation.get(CoreAnnotations.SentencesAnnotation.class);
        if (sentences != null && ! sentences.isEmpty()) {
            CoreMap sentence = sentences.get(0);
            out.println("The keys of the first sentence's CoreMap are:");
            out.println(sentence.keySet());
            out.println();
            out.println("The first sentence is:");
            out.println(sentence.toShorterString());
            out.println();
            out.println("The first sentence tokens are:");
            for (CoreMap token : sentence.get(CoreAnnotations.TokensAnnotation.class)) {
                out.println(token.toShorterString());
            }
            Tree tree = sentence.get(TreeCoreAnnotations.TreeAnnotation.class);
            out.println();
            out.println("The first sentence parse tree is:");
            tree.pennPrint(out);
            out.println();
            out.println("The first sentence basic dependencies are:");
            out.println(sentence.get(SemanticGraphCoreAnnotations.BasicDependenciesAnnotation.class).toString(SemanticGraph.OutputFormat.LIST));
            out.println("The first sentence collapsed, CC-processed dependencies are:");
            SemanticGraph graph = sentence.get(SemanticGraphCoreAnnotations.CollapsedCCProcessedDependenciesAnnotation.class);
            out.println(graph.toString(SemanticGraph.OutputFormat.LIST));

            // Print out dependency structure
            IndexedWord node = graph.getNodeByIndexSafe(5);
            out.println("Printing dependencies around \"" + node.word() + "\" index " + node.index());
            List<SemanticGraphEdge> edgeList = graph.getIncomingEdgesSorted(node);
            if (! edgeList.isEmpty()) {
                assert edgeList.size() == 1;
                int head = edgeList.get(0).getGovernor().index();
                String headWord = edgeList.get(0).getGovernor().word();
                String deprel = edgeList.get(0).getRelation().toString();
                out.println("Parent is word \"" + headWord + "\" index " + head + " via " + deprel);
            } else  {
                out.println("Parent is ROOT via root");
            }
            edgeList = graph.outgoingEdgeList(node);
            for (SemanticGraphEdge edge : edgeList) {
                String depWord = edge.getDependent().word();
                int depIdx = edgeList.get(0).getDependent().index();
                String deprel = edge.getRelation().toString();
                out.println("Child is \"" + depWord + "\" (" + depIdx + ") via " + deprel);
            }
            out.println();

            out.println("Coreference information");
            Map<Integer, CorefChain> corefChains =
                    annotation.get(CorefCoreAnnotations.CorefChainAnnotation.class);
            if (corefChains == null) { return; }
            for (Map.Entry<Integer,CorefChain> entry: corefChains.entrySet()) {
                out.println("Chain " + entry.getKey());
                for (CorefChain.CorefMention m : entry.getValue().getMentionsInTextualOrder()) {
                    // We need to subtract one since the indices count from 1 but the Lists start from 0
                    List<CoreLabel> tokens = sentences.get(m.sentNum - 1).get(CoreAnnotations.TokensAnnotation.class);
                    // We subtract two for end: one for 0-based indexing, and one because we want last token of mention not one following.
                    out.println("  " + m + ", i.e., 0-based character offsets [" + tokens.get(m.startIndex - 1).beginPosition() +
                            ", " + tokens.get(m.endIndex - 2).endPosition() + ')');
                }
            }
            out.println();

            out.println("The first sentence overall sentiment rating is " + sentence.get(SentimentCoreAnnotations.SentimentClass.class));
        }
        IOUtils.closeIgnoringExceptions(out);
        IOUtils.closeIgnoringExceptions(xmlOut);
    }

}
