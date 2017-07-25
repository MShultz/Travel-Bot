import java.util.ArrayList;
import java.util.EmptyStackException;
import java.util.Stack;

/**
 * Created by Mary on 7/13/2017.
 */
public class Parser {

    private Stack<Word> parseStack;
    private ArrayList<Word> parseTree;
    private Word current;
    private Word nextStackItem;
    private Word nextNextItem;


    public boolean parse(String[] input) {
        parseStack = new Stack<Word>();
        parseTree = new ArrayList<>();

        boolean hasNext;
        boolean canReduce;
        for (int i = 0; i < input.length; ++i) {
            parseStack.push(new Word(input[i], WordType.UNDEFINED));
            canReduce = true;
            while (canReduce) {
                current = parseStack.peek();
                if (hasNextStackItem()) {
                    nextStackItem = getNextStackItem();
                    if (hasNextNextStackItem()) {
                        nextNextItem = getNextNextStackItem();

                    } else
                        nextNextItem = null;

                } else {
                    nextStackItem = nextNextItem = null;
                }
                hasNext = i + 1 < input.length;


                if (nextStackItem != null && (nextStackItem.getType().equals(WordType.NOUNPHRASE) && current.getType().equals(WordType.VERBPHRASE))) {
                    reduceToSentence();
                }else if(current.getType().equals(WordType.DISTANCE) && nextStackItem != null && nextStackItem.getType().equals(WordType.DIGITS)){
                    reduceToDistancePhrase();
                }else if(current.getType().equals(WordType.NOUN) && nextStackItem != null && nextStackItem.getType().equals(WordType.DISTANCEPHRASE) && nextNextItem != null && nextNextItem.getType().equals(WordType.ARTICLE)){
                    reduceToNounPhrase();
                }else if(current.getType().equals(WordType.CITYPHRASE) && nextStackItem != null && nextStackItem.getType().equals(WordType.VERB) && nextNextItem != null && nextNextItem.equals(WordType.NOUNPHRASE)){
                    reduceToVerbPhrase();
                } else if(current.getType().equals(WordType.CITY) && nextStackItem != null && nextStackItem.getType().equals(WordType.CONJUNCTION) && nextNextItem != null && nextNextItem.getType().equals(WordType.CITY)){
                    reduceToCityPhrase();
                }else if (current.getType().equals(WordType.UNDEFINED) && current.getWord().equals("A")) {
                    current.setType(WordType.ARTICLE);
                    parseTree.add(new Word(current));
                } else if (current.getType().equals(WordType.UNDEFINED) && current.getWord().equals("road")) {
                    current.setType(WordType.NOUN);
                    parseTree.add(new Word(current));
                } else if (current.getType().equals(WordType.UNDEFINED) && current.getWord().equals("connects")) {
                    current.setType(WordType.VERB);
                    parseTree.add(new Word(current));
                } else if (current.getType().equals(WordType.UNDEFINED) && current.getWord().equals("KM")) {
                    current.setType(WordType.DISTANCE);
                } else if (current.getType().equals(WordType.UNDEFINED) && isDigits(current.getWord())) {
                    current.setType(WordType.DIGITS);
                } else if (current.getType().equals(WordType.UNDEFINED) && isCity(current.getWord())) {
                    reduceToCity();
                } else {
                    canReduce = false;
                }
            }
        }
        if (parseStack.peek().getType().equals(WordType.SENTENCE))
            return true;
        else
            return false;
    }

    private void reduceToCityPhrase(){
        Word city2 = parseStack.pop();
        Word conjunction = parseStack.pop();
        Word city1 = parseStack.pop();

        parseStack.push(new Word(city1.getWord() + " " + conjunction + " " + city2.getWord(), WordType.CITYPHRASE));
        parseTree.add(parseStack.peek());
    }

    private void reduceToDistancePhrase(){
        Word distance = parseStack.pop();
        Word digits = parseStack.pop();
        parseStack.push(new Word(digits.getWord() + " " + distance.getWord(), WordType.DISTANCEPHRASE));
        parseTree.add(parseStack.peek());

    }
    private boolean isCity(String current) {
        if (current.equals(WordType.UNDEFINED)) {
            if (nextStackItem != null) {
                if (nextStackItem.equals(WordType.CONJUNCTION)) {
                    return true;
                } else if (nextStackItem.equals(WordType.UNDEFINED)) {
                    if (nextNextItem != null && nextNextItem.equals(WordType.CONJUNCTION)) {
                        return true;
                    }
                }
            }
        } else if (current.equals(WordType.CONJUNCTION) && nextStackItem != null && nextStackItem.equals(WordType.UNDEFINED)) {
            if (nextNextItem == null || nextNextItem.equals(WordType.UNDEFINED)) {
                return true;
            }
        }
        return false;
    }

    private void reduceToCity() {
        if (current.equals(WordType.UNDEFINED)) {
            if (nextStackItem.equals(WordType.CONJUNCTION)) {
                current.setType(WordType.CITY);
            }else {
                Word city2 = parseStack.pop();
                Word city1 = parseStack.pop();
                parseStack.push(new Word(city1.getWord() + " " + city2.getWord(), WordType.CITY));
                parseTree.add(parseStack.peek());
            }
        }else if(nextStackItem != null && nextStackItem.equals(WordType.UNDEFINED)){
            if(nextNextItem == null){
                nextStackItem.setType(WordType.CITY);
            }
            else{
                Word conjunction = parseStack.pop();
                Word city2 = parseStack.pop();
                Word city1 = parseStack.pop();
                parseStack.push(new Word(city1.getWord() + " " + city2.getWord(), WordType.CITY));
                parseStack.push(conjunction);
                parseTree.add(parseStack.peek());
            }


        }
    }

    private void reduceToVerbPhrase() {
        Word cityPhrase = parseStack.pop();
        Word verb = parseStack.pop();
        parseStack.push(new Word(cityPhrase.getWord() + " " + verb.getWord(), WordType.VERBPHRASE));
        parseTree.add(parseStack.peek());
    }

    private boolean isDigits(String current) {
        try {
            Double.parseDouble(current);
        } catch (NumberFormatException e) {
            return false;
        }
        return true;
    }

    private void reduceToNounPhrase() {
        Word noun = parseStack.pop();
        Word distancePhrase = parseStack.pop();
        Word article = parseStack.pop();
        parseStack.push(new Word(article.getWord() + " " + distancePhrase.getWord() + " " + noun.getWord(), WordType.NOUNPHRASE));
        parseTree.add(parseStack.peek());
    }

    private void reduceToSentence() {
        Word verbPhrase = parseStack.pop();
        Word nounPhrase = parseStack.pop();
        parseStack.push(new Word(nounPhrase.getWord() + " " + verbPhrase.getWord(), WordType.SENTENCE));
        parseTree.add(parseStack.peek());
    }

    private Word getNextStackItem() {
        Stack<Word> tempStack = new Stack<>();
        tempStack.push(parseStack.pop());
        Word lookAhead = parseStack.peek();
        parseStack.push(tempStack.pop());
        return lookAhead;
    }

    private boolean hasNextStackItem() {
        Word temp = parseStack.pop();
        try {
            parseStack.peek();
        } catch (EmptyStackException e) {
            parseStack.push(temp);
            return false;
        }
        parseStack.push(temp);
        return true;
    }

    private boolean hasNextNextStackItem() {
        Word temp = parseStack.pop();
        Word temp2 = parseStack.pop();
        try {
            parseStack.peek();
        } catch (EmptyStackException e) {
            parseStack.push(temp2);
            parseStack.push(temp);
            return false;
        }
        parseStack.push(temp2);
        parseStack.push(temp);
        return true;
    }

    private Word getNextNextStackItem() {
        Stack<Word> tempStack = new Stack<>();
        tempStack.push(parseStack.pop());
        tempStack.push(parseStack.pop());
        Word lookAhead = parseStack.peek();
        parseStack.push(tempStack.pop());
        parseStack.push(tempStack.pop());
        return lookAhead;
    }

    public ArrayList<Word> getParseTree() {
        return parseTree;
    }
}
