package org.example;

import java.io.*;
import java.util.ArrayList;
import java.util.List;

public class ProtobufGenerate {
    // protoc的目录
    public static void main(String[] args) throws Exception {
        List<String> lCommand = new ArrayList<>(4);
        lCommand.add("protoc");
        lCommand.add("--proto_path=D:/siyu/src/main/resources/proto");
        lCommand.add("--java_out=D:/siyu/src/main/java");
        lCommand.add("Message.proto");
        ProcessBuilder pb = new ProcessBuilder(lCommand);
        pb.redirectErrorStream(true);
        Process p = pb.start();
        //read the standard output
        String line;
        try(BufferedReader stdout = new BufferedReader(new InputStreamReader(p.getInputStream()));){
            while ((line = stdout.readLine()) != null) {
                System.out.println(line);
            }
        }
        int ret = p.waitFor();
        System.out.println("the return code is " + ret);
    }
}
