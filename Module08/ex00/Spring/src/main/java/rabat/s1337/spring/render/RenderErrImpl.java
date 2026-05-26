package rabat.s1337.spring.render;

import rabat.s1337.spring.preProcessor.PreProcessor;

public class RenderErrImpl implements Render{
    private PreProcessor preProcessor;

    public RenderErrImpl(PreProcessor preProcessor) {
        this.preProcessor = preProcessor;
    }

    @Override
    public void render(String message) {
        message = preProcessor.process(message);
        System.err.println(message);
    }
}
