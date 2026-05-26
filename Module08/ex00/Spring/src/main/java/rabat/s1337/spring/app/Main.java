package rabat.s1337.spring.app;

import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.ClassPathBeanDefinitionScanner;
import org.springframework.context.support.ClassPathXmlApplicationContext;

import rabat.s1337.spring.preProcessor.*;
import rabat.s1337.spring.printer.*;
import rabat.s1337.spring.render.*;

public class Main {
    public static void main(String[] args) {
            // PreProcessor preProcessor = new PreProcessorToUpperImpl();
            // Render renderer = new RenderErrImpl(preProcessor);
            // PrinterWithPrefixImpl printer = new PrinterWithPrefixImpl(renderer);
            // printer.setPrefix("Prefix");
            // printer.print("Hello!");
            ApplicationContext cnx = new ClassPathXmlApplicationContext("context.xml");
            Printer printer = cnx.getBean("printerWithPrefix", Printer.class);
            printer.print("Hello!");
    }
}


