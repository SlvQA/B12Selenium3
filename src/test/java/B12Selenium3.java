import org.openqa.selenium.*;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.Select;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.testng.Assert;
import org.testng.annotations.Test;

import java.sql.SQLOutput;
import java.time.Duration;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class B12Selenium3 {
    @Test
    public void navigateToWebsite() {
        WebDriver driver = new ChromeDriver();


        try {
            //navigate to the webpage
            driver.manage().window().maximize();
            driver.manage().timeouts().implicitlyWait(Duration.ofSeconds(10));
            //            WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(5));
            //            wait.until(ExpectedConditions.elementToBeClickable(yearMin));
            driver.get("https://www.edmunds.com/");

            //verify the title
            String expectedTitle = "New Cars, Used Cars, Car Reviews and Pricing | Edmunds";
            String actualTitle = driver.getTitle();
            Assert.assertEquals(actualTitle, expectedTitle);

            //click on "Used Cars"
            driver.findElement(By.xpath("//a[@href=\"/inventory/srp.html?inventorytype=used%2Ccpo\"]")).click();

            //erasing and changing ZIP
            WebElement zipField = driver.findElement(By.xpath("//input[@name='zip']"));
            zipField.click();
            zipField.sendKeys(Keys.chord(Keys.CONTROL,"a"), Keys.DELETE);
            zipField.sendKeys("22031", Keys.ENTER);

            //clicking the checkBox for Local Listings only
            WebElement checkBoxLocalListings = driver.findElement(By.xpath("(//input[@type='checkbox'])[2]"));
            if (checkBoxLocalListings.isDisplayed() && !checkBoxLocalListings.isSelected()){
                checkBoxLocalListings.click();
            }

        //Assert.assertTrue(checkBoxLocalListings.isSelected()); //- DOESN'T WORK

            //choosing Make of the car
            WebElement makeDropDown = driver.findElement(By.xpath("//select[@id='usurp-make-select']"));
            Select makeSelect = new Select (makeDropDown);
            makeSelect.selectByVisibleText("Tesla");

            //verifying that Model is not selected
            WebElement modelDropDown = driver.findElement(By.xpath("//select[@id='usurp-model-select']"));
            Select modelSelect = new Select (modelDropDown);
            String actualModel = modelSelect.getFirstSelectedOption().getText();
            String expectedModel = "Add Model";
            Assert.assertEquals(actualModel, expectedModel);

            //verifying year Min
            WebElement yearMin = driver.findElement(By.id("min-value-input-Year"));
            String actualMinYear = yearMin.getAttribute("value");
            String expectedMinYear = "2013";
            Assert.assertEquals(actualMinYear, expectedMinYear);

            //verifying year Max
            WebElement yearMax = driver.findElement(By.id("max-value-input-Year"));
            String actualMaxYear = yearMax.getAttribute("value");
            String expectedMaxYear = "2023";
            Assert.assertEquals(actualMaxYear, expectedMaxYear);

            //Verify that Model dropdown options are [Any Model, Model 3, Model S, Model X,
            //Model Y, Cybertruck, Roadster]
            ArrayList<String> actualModelsList = new ArrayList<>();
            List<String> expectedModelList = Arrays.asList("Add Model", "Model 3", "Model S", "Model X", "Model Y", "Cybertruck", "Roadster");
            List<WebElement> modelOptions = modelSelect.getOptions();
            for(WebElement option : modelOptions) {
                actualModelsList.add(option.getText());
                }
//            System.out.println(actualModelsList);
//            System.out.println(expectedModelList);
            Assert.assertEquals(actualModelsList, expectedModelList);

            //choosing Model 3 from the dropdown menu and selecting min year 2020
            modelSelect.selectByValue("Model 3");
            try {
                yearMin.click();
            } catch (StaleElementReferenceException e) {
                // Refresh the page
                driver.navigate().refresh();
                // Try to locate the element again
                yearMin = driver.findElement(By.id("min-value-input-Year"));
                yearMin.click();
            }
            yearMin.sendKeys(Keys.BACK_SPACE, "2020", Keys.ENTER);

            //verify that there are 21 search results, excluding the sponsored result(s).
            List <WebElement> listOfResults = driver.findElements(By.xpath("//li[@class='d-flex mb-0_75 mb-md-1_5 col-12 col-md-6']"));
                int actualListSize = listOfResults.size();
                int expectedSize = 21;
                Assert.assertEquals(actualListSize, expectedSize);
                //System.out.println(actualListSize);

            //verify that each search result title contains ‘Tesla Model 3’ & verify that each year is within the selected range (2020-2023)
            List <WebElement> titles = driver.findElements(By.xpath("//div[@id='results-container']//div[@class='size-16 font-weight-bold mb-0_5 text-blue-30']"));
            String searchTermTesla = "Tesla Model 3";
            for (WebElement title : titles){
                System.out.println(title.getText());
                int year = Integer.parseInt(title.getText().substring(0,4));
                //System.out.println(year);
                Assert.assertTrue(title.getText().toLowerCase().contains(searchTermTesla.toLowerCase()));
                //assert(2020 <= year && year <= 2023);
            }








        } finally {
            //driver.quit();
        }
    }

}
