package rabat.s1337.spring.renderer;

import rabat.s1337.spring.preProcessor.PreProcessor;

public class RendererStandardImpl implements Renderer{
    private PreProcessor preProcessor;

    public RendererStandardImpl(PreProcessor preProcessor) {
        this.preProcessor = preProcessor;
    }

    @Override
    public void render(String message) {
        message = preProcessor.process(message);
        System.out.println(message);
    }
}
