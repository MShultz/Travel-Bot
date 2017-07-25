/**
 * Created by Mary on 7/13/2017.
 */
public class Word {
    private String word;
    private WordType type;

    public Word(String word, WordType type){
        setWord(word);
        setType(type);
    }
    public Word(Word oldWord){
        setWord(oldWord.getWord());
        setType(oldWord.getType());
    }

    public String getWord() {
        return word;
    }

    public void setWord(String word) {
        this.word = word;
    }

    public WordType getType() {
        return type;
    }

    public void setType(WordType type) {
        this.type = type;
    }
}
