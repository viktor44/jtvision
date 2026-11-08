# Getting started

## Maven

```xml
<dependency>
    <groupId>org.viktor44.jtvision</groupId>
    <artifactId>jtvision</artifactId>
    <version>1.1.0</version>
</dependency>
```

## Hello World

An application extends [`JtvApplication`](api/org.viktor44.jtvision.core/JtvApplication.md) and provides a menu bar and a status line. Commands arrive in `handleEvent`:

```java
public class HelloApp extends JtvApplication {

    private static final int GreetThemCmd = 100;

    private void greetingBox() {
        JtvDialog d = new JtvDialog(new JtvRect(25, 5, 55, 16), "Hello, World!");
        d.insert(new JtvStaticText(new JtvRect(3, 5, 15, 6), "How are you?"));
        d.insert(new JtvButton(new JtvRect(16, 2, 28, 4), "Terrific", cmCancel, bfNormal));
        d.insert(new JtvButton(new JtvRect(16, 4, 28, 6), "Ok", cmCancel, bfNormal));
        getDesktop().execView(d);
    }

    @Override
    public void handleEvent(JtvEvent event) {
        super.handleEvent(event);
        if (event.getWhat() == evCommand
                && event.getMessage().getCommand() == GreetThemCmd) {
            greetingBox();
            clearEvent(event);
        }
    }
}
```

See `org.viktor44.jtvision.hello.HelloApp` in the sources for the complete program.
