package io.github.talelin.knowledge.spider;

import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;

public class WebDriverManager {
    private static WebDriver webDriver;

    private WebDriverManager() {
        // 私有构造函数
    }

    public static WebDriver getInstance() {
        if (webDriver == null) {
            System.setProperty("webdriver.chrome.driver", "E:\\projects\\knowledge\\driver\\chromedriver.exe");
            ChromeOptions options = new ChromeOptions();
            options.addArguments("--headless");
            webDriver = new ChromeDriver(options);
        }
        return webDriver;
    }

    public static void close() {
        if (webDriver != null) {
            webDriver.quit();
            webDriver = null;
        }
    }
}
