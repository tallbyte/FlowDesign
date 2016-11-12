package com.tallbyte.flowdesign.core__;

import com.tallbyte.flowdesign.core__.element.Flow;
import com.tallbyte.flowdesign.core__.element.RepresentativeFlow;

import java.util.Scanner;

/**
 * Created on 2016-10-27.
 */
public class Main {

    public static void main(String[] args) {
        LineReader      lineReader      = new LineReader();

        lineReader
                .addNext(new NumberParser())
                .addNext(new ConsolePrinter());


        RepresentativeFlow<Scanner, Void> numberPrinter = new RepresentativeFlow<>();
        numberPrinter.setRepresentation(
                lineReader
        );

        System.out.println("----");
        print(lineReader);
        System.out.println("----");
        print(numberPrinter);
        System.out.println("----");


        Scanner scanner = new Scanner(System.in);
        while (scanner.hasNextLine()) {
            numberPrinter.invokeChain(scanner);
        }
    }

    protected static void print(Flow<?, ?> flow) {
        print(flow, 1);
    }

    protected static void print(Flow<?, ?> flow, int depth) {
        for (int i = 0; i < depth; ++i) {
            System.out.print("    ");
        }
        System.out.println(flow);

        if (flow.hasRepresentation()) {
            print(flow.getRepresentation(), depth+1);
        }


        for (Flow<?, ?> next : flow.getNext()) {
            print(next, depth);
        }
    }
}
