import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import static com.icraus.jprocess.ProcessAdaptor.exec;

public class Main {

    public static void main(String[] args) {
        System.out.println(args.length);
        if(args.length == 0){
            System.out.println("**************************************************");
            List<String> ags = new ArrayList<>();
            Collections.addAll(ags, "AA", "BB");
            ProcessBuilder p = exec(Main.class, new ArrayList<>(), ags);
            try {
                p.redirectErrorStream(true);
                Process pn = p.start();
                try (BufferedReader bufferedReader =new  BufferedReader(new InputStreamReader(pn.getInputStream()))) {
                    bufferedReader.lines().forEach(e -> System.out.println(e));
                }
            } catch (IOException e) {
                System.out.println("Error here.");
                e.printStackTrace();
            }
            return;
        }
        for(int i = 0; i < 50; ++i){
            System.out.println("Hello world");
        }

    }
}
