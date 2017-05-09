package webTestTool;

public class main {

    public static void main(String[] args) {
        System.setProperty("webdriver.ie.driver",
                "D:\\pleiades4.5\\workspace\\webTestTool\\driver\\IEDriverServer.exe");
        System.setProperty("webdriver.chrome.driver",
                "D:\\pleiades4.5\\workspace\\webTestTool\\driver\\chromedriver.exe");
        System.setProperty("webdriver.gecko.driver",
                "D:\\pleiades4.5\\workspace\\webTestTool\\driver\\geckodriver.exe");

        Script script = new Script();
        script.run("D:/pleiades4.5/workspace/webTestTool/script/test2.xml", "chrome");


    }


}
