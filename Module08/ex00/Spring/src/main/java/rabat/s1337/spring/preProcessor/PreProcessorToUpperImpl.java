package rabat.s1337.spring.preProcessor;

public class PreProcessorToUpperImpl implements PreProcessor{

    @Override
    public String process(String message) {
        return message.toUpperCase();
    }
}
