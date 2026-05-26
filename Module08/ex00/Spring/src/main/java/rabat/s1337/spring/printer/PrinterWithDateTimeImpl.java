package rabat.s1337.spring.printer;

import java.time.LocalDateTime;

import rabat.s1337.spring.render.Render;

public class PrinterWithDateTimeImpl implements Printer{
    private Render renderer;

    public PrinterWithDateTimeImpl(Render renderer){
        this.renderer = renderer;
    }
    @Override
    public void print(String message) {
        renderer.render(LocalDateTime.now().toString()+" "+ message);
    }
}
