package webTestTool;

import system.utils.PropertiesReader;

public class Main {

    public static void main(String[] args) {
        PropertiesReader reader = PropertiesReader.load("config.properties");

        reader.entrySet().forEach(entry -> {
            System.setProperty((String) entry.getKey(), (String) entry.getValue());
        });

        Script script = new Script();

        script.run("script/test.xml", "chrome");

    }

}
