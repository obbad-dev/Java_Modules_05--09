package rabat.s1337.spring.render;

import rabat.s1337.spring.preProcessor.PreProcessor;

public class RenderStandardImpl implements Render{
    private PreProcessor preProcessor;

    public RenderStandardImpl(PreProcessor preProcessor) {
        this.preProcessor = preProcessor;
    }

    @Override
    public void render(String message) {
        message = preProcessor.process(message);
        System.out.println(message);
    }
}
