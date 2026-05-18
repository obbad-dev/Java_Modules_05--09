package rabat.s1337.spring.printer;

import rabat.s1337.spring.renderer.Renderer;

import java.time.LocalDateTime;

public class PrinterWithDateTimeImpl implements Printer{
    private Renderer renderer;

    public PrinterWithDateTimeImpl(Renderer renderer){
        this.renderer = renderer;
    }
    @Override
    public void print(String message) {
        renderer.render(LocalDateTime.now().toString()+" "+ message);
    }
}
