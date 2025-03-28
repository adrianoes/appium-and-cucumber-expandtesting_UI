package stepdefinitions;

import io.appium.java_client.AppiumBy;
import io.appium.java_client.AppiumDriver;
import io.cucumber.java.After;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import org.json.JSONObject;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.remote.DesiredCapabilities;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;
import java.io.IOException;
import java.net.URI;
import java.net.URL;
import java.time.Duration;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

public class HealthSteps {

    static AppiumDriver driver;

    @Given("User opens the app before health check")
    public void user_opens_the_app_before_health_check() {
        // Sempre cria uma nova instância do driver
        DesiredCapabilities cap = new DesiredCapabilities();
        try {
            cap.setCapability("platformName", "Android");
            cap.setCapability("deviceName", "Pixel_4_API_29");
            cap.setCapability("platformVersion", "10.0");
            cap.setCapability("automationName", "UIAutomator2");
            cap.setCapability("app", "./apks/apiClient.apk");
            cap.setCapability("appActivity", "com.ab.apiclient.ui.Splash");
            cap.setCapability("appWaitActivity", "com.ab.apiclient.ui.Splash, com.ab.apiclient.ui.MainActivity, com.ab.apiclient.*");
            cap.setCapability("autoGrantPermissions", true);
            cap.setCapability("enforceXPath1", true);
            cap.setCapability("appWaitDuration", 60000);
            cap.setCapability("newCommandTimeout", 120);
            cap.setCapability("adbExecTimeout", 120000);

//            cap.setCapability("autoDismissAlerts", true);
//            cap.setCapability("noReset", true);
//            cap.setCapability("adbExecTimeout", 120000);

            URL url = URI.create("http://127.0.0.1:4723").toURL();
            driver = new AppiumDriver(url, cap);

        } catch (Exception e) {
            // Capture the exception message and stack trace
            String exceptionMessage = e.getClass().getName() + ": " + (e.getMessage() != null ? e.getMessage().split("\n")[0] : "Unknown error");

            // Filter the stack trace to display only relevant lines
            StringBuilder filteredStackTrace = new StringBuilder();
            for (StackTraceElement element : e.getStackTrace()) {
                if (element.getClassName().contains("stepdefinitions")) {
                    filteredStackTrace.append("\n\tat ").append(element);
                }
            }

            // Throw a new exception with the formatted message and filtered stack trace
            throw new RuntimeException(exceptionMessage + filteredStackTrace);
        }
    }

//    @And("User configures the app before health check")
//    public void user_configures_the_app_before_health_check() {
//        WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(120));//
//        // Abrir o menu lateral
//        WebElement menuButton = wait.until(ExpectedConditions.elementToBeClickable(AppiumBy.className("android.widget.ImageButton")));
//        menuButton.click();
//
//        // Ir para as configurações
//        WebElement settingsButton = wait.until(ExpectedConditions.elementToBeClickable(AppiumBy.xpath("//android.widget.CheckedTextView[@resource-id='com.ab.apiclient:id/design_menu_item_text' and @text='Settings']")));
//        settingsButton.click();
//
//        // Definir timeout da conexão
//        WebElement timeoutField = wait.until(ExpectedConditions.visibilityOfElementLocated(AppiumBy.id("com.ab.apiclient:id/etTimeoutConnection")));
//        timeoutField.clear();
//        timeoutField.sendKeys("120");
//
//        // Definir timeout da leitura
//        WebElement timeoutReadField = wait.until(ExpectedConditions.visibilityOfElementLocated(AppiumBy.id("com.ab.apiclient:id/etTimeoutREAD")));
//        timeoutReadField.clear();
//        timeoutReadField.sendKeys("120");
//
//        // Definir timeout da escrita
//        WebElement timeoutWriteField = wait.until(ExpectedConditions.visibilityOfElementLocated(AppiumBy.id("com.ab.apiclient:id/etTimeoutWRITE")));
//        timeoutWriteField.clear();
//        timeoutWriteField.sendKeys("120");
//
//        // Fechar configurações
//        menuButton.click();
//
//        // Criar nova requisição
//        WebElement newRequestButton = wait.until(ExpectedConditions.elementToBeClickable(AppiumBy.xpath("//android.widget.CheckedTextView[@resource-id='com.ab.apiclient:id/design_menu_item_text' and @text='New Request']")));
//        newRequestButton.click();
//    }

    @When("User sends request to check API health")
    public void uUser_sends_request_to_check_API_health() {
        WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(120));

        // Input base URL and endpoint
        WebElement urlField = wait.until(ExpectedConditions.visibilityOfElementLocated(AppiumBy.androidUIAutomator("new UiSelector().resourceId(\"com.ab.apiclient:id/etUrl\")")));
        urlField.sendKeys("https://practice.expandtesting.com/notes/api/health-check");

        // Click Send Request button
        WebElement sendButton = wait.until(ExpectedConditions.elementToBeClickable(AppiumBy.id("com.ab.apiclient:id/btnSend")));
        sendButton.click();
        wait.until(ExpectedConditions.visibilityOfElementLocated(AppiumBy.androidUIAutomator("new UiSelector().text(\"Raw\")")));
        WebElement rawButton = wait.until(ExpectedConditions.elementToBeClickable(AppiumBy.androidUIAutomator("new UiSelector().text(\"Raw\")")));
        rawButton.click();
    }

    @Then("User should see API health message after health check")
    public void user_should_see_API_health_message_after_health_check() {
        WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(120));

        WebElement resultTextElement = wait.until(ExpectedConditions.visibilityOfElementLocated(AppiumBy.androidUIAutomator("new UiSelector().resourceId(\"com.ab.apiclient:id/tvResult\")")));
        String responseText = resultTextElement.getText();

        // Criando o objeto JSONObject para parsear o JSON da resposta
        JSONObject responseJson = new JSONObject(responseText);

        // Extract values from the JSON response
        boolean success = responseJson.getBoolean("success");
        int status = responseJson.getInt("status");
        String message = responseJson.getString("message");

        // Validate the extracted values
        assertTrue("Response should indicate success", success);
        assertEquals("Response should have status 200", 200, status);
        assertEquals("Response should confirm API is running", "Notes API is Running", message);
    }

//    @And("App is closed after health check")
//    public void app_is_closed_after_health_check() {
//        if (driver != null) {
//            try {
//                String packageName = "com.ab.apiclient";
//                String adbCommand = "adb -s emulator-5554 shell am force-stop " + packageName;
//                Process process = Runtime.getRuntime().exec(adbCommand);
//                process.waitFor();
//                Thread.sleep(1000);
//                driver.quit();
//                driver = null; // Garante que um novo driver seja criado no próximo cenário
//            } catch (IOException | InterruptedException e) {
//                System.out.println("Error during closing the app via ADB: " + e.getMessage());
//            }
//        }
//    }

    @After
    public void quitDriver() {
        if (driver != null) {
            try {
                String packageName = "com.ab.apiclient";
                String adbCommand = "adb -s emulator-5554 shell am force-stop " + packageName;
                Process process = Runtime.getRuntime().exec(adbCommand);
                process.waitFor();
                Thread.sleep(1000);
                driver.quit();
            } catch (IOException | InterruptedException e) {
                System.out.println("Error during closing the app via ADB: " + e.getMessage());
            }
        }
    }

}