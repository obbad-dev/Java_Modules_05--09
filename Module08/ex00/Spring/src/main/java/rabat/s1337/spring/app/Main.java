package rabat.s1337.spring.app;

import rabat.s1337.spring.preProcessor.*;
import rabat.s1337.spring.printer.PrinterWithPrefixImpl;
import rabat.s1337.spring.renderer.*;

public class Main {
    public static void main(String[] args) {
            PreProcessor preProcessor = new PreProcessorToUpperImpl();
            Renderer renderer = new RendererErrImpl(preProcessor);
            PrinterWithPrefixImpl printer = new PrinterWithPrefixImpl(renderer);
            printer.setPrefix("Prefix");
            printer.print("Hello!");
    }
}
