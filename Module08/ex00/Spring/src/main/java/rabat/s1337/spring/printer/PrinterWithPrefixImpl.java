package rabat.s1337.spring.printer;

import rabat.s1337.spring.render.Render;

public class PrinterWithPrefixImpl implements Printer{
    private Render renderer;
    private String prefix;

    public PrinterWithPrefixImpl(Render renderer){
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
