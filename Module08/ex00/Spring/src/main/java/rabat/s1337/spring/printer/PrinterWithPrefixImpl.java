package rabat.s1337.spring.printer;

import rabat.s1337.spring.renderer.Renderer;
import rabat.s1337.spring.renderer.RendererStandardImpl;

import java.security.PrivateKey;

public class PrinterWithPrefixImpl implements Printer{
    private Renderer renderer;
    private String prefix;

    public PrinterWithPrefixImpl(Renderer renderer){
        this.renderer = renderer;
    }

    public void setPrefix(String prefix) {
        this.prefix = prefix;
    }

    @Override
    public void print(String message) {
        message = prefix +" "+ message;
        renderer.render(message);
    }
}
