
import java.util.Scanner;

/**
 * Created by Mary on 7/13/2017.
 */
public class Program {

    public void run() {
        Parser parser = new Parser();
        Generator gen = new Generator();
        Tokenizer tokenizer = new Tokenizer();
        System.out.println("Welcome to chat bot!");
        String input;
        Scanner scan = new Scanner(System.in);
        while (true) {
            input = scan.nextLine();

            boolean isValid = parser.parse(tokenizer.tokenize(input));
            if (isValid) {
                gen.setCurrentTree(gen.generateTree(parser.getParseTree()));
                gen.generateResponse();
            } else {
                gen.generateInvalidResponse();
            }
        }
    }


}
