package rabat.s1337.spring.preProcessor;

public class PreProcessorToLowerImpl implements PreProcessor{
    @Override
    public String process(String message) {
        return message.toLowerCase();
    }
}
