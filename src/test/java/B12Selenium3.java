import org.openqa.selenium.*;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.support.ui.Select;
import org.testng.Assert;
import org.testng.annotations.Test;
import java.time.Duration;
import java.util.*;

public class B12Selenium3 {
    @Test
    public void navigateToWebsite() {
        WebDriver driver = new ChromeDriver();
        try {
            //navigate to the webpage
            driver.manage().window().maximize();
            driver.manage().timeouts().implicitlyWait(Duration.ofSeconds(10));
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
            zipField.sendKeys(Keys.chord(Keys.CONTROL, "a"), Keys.DELETE);
            zipField.sendKeys("22031", Keys.ENTER);

            //clicking the checkBox for Local Listings only
            WebElement checkBoxLocalListings = driver.findElement(By.xpath("(//input[@type='checkbox'])[2]"));
            if (checkBoxLocalListings.isDisplayed() && !checkBoxLocalListings.isSelected()) {
                checkBoxLocalListings.click();
                Assert.assertTrue(checkBoxLocalListings.isSelected());
            }

            //choosing Make of the car
            WebElement makeDropDown = driver.findElement(By.xpath("//select[@id='usurp-make-select']"));
            Select makeSelect = new Select(makeDropDown);
            makeSelect.selectByVisibleText("Tesla");

            //verifying that Model is not selected
            WebElement modelDropDown = driver.findElement(By.xpath("//select[@id='usurp-model-select']"));
            Select modelSelect = new Select(modelDropDown);
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

            //Verify that Model dropdown options are [Any Model, Model 3, Model S, Model X, Model Y, Cybertruck, Roadster]
            ArrayList<String> actualModelsList = new ArrayList<>();
            List<String> expectedModelList = Arrays.asList("Add Model", "Model 3", "Model S", "Model X", "Model Y", "Cybertruck", "Roadster");
            List<WebElement> modelOptions = modelSelect.getOptions();
            for (WebElement option : modelOptions) {
                actualModelsList.add(option.getText());
            }
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
            driver.navigate().refresh();

            //verify that there are 21 search results, excluding the sponsored result(s).
            List<WebElement> listOfResults = driver.findElements(By.xpath("//li[@class='d-flex mb-0_75 mb-md-1_5 col-12 col-md-6']"));
            int actualListSize = listOfResults.size();
            int expectedSize = 21;
            Assert.assertEquals(actualListSize, expectedSize);

            //verify that each search result title contains ‘Tesla Model 3’ & verify that each year is within the selected range (2020-2023)
            List<WebElement> titles = driver.findElements(By.xpath("//li[@class='d-flex mb-0_75 mb-md-1_5 col-12 col-md-6']//img"));
            String searchTermTesla = "Tesla Model 3";
            for (WebElement title : titles) {
                //System.out.println(title.getAttribute("alt"));
                int year = Integer.parseInt(title.getAttribute("alt").substring(0, 4));
                //System.out.println(year);
                Assert.assertTrue(title.getAttribute("alt").toLowerCase().contains(searchTermTesla.toLowerCase()));
                assert (2020 <= year && year <= 2023);
            }

            //From the dropdown on choose “Price: Low to High"
            driver.findElement(By.xpath("//select[@id='sort-by']"));
            Select select = new Select(driver.findElement(By.xpath("//select[@id='sort-by']")));
            select.selectByVisibleText("Price: Low to High");
            driver.navigate().refresh();

            //verify that the results are sorted from lowest to highest price.
            List<WebElement> findPrices = driver.findElements(By.xpath("//div[@class='pricing-details d-flex flex-column']//span[@class='heading-3']"));
            List<Integer> pricesToVerify = new ArrayList<>();
            for (WebElement p : findPrices) {
                String price = p.getText().replace("$", "").replace(",", "").split(" ")[0];
                pricesToVerify.add(Integer.parseInt(price));
            }
            int int1;
            for (int1 = 1; int1 <= pricesToVerify.size() - 1; int1++) {
                Assert.assertTrue(pricesToVerify.get(int1 - 1) <= pricesToVerify.get(int1), "Ascending order is incorrect.");
            }

            //sort by "Price High to Low"
            Select select2 = new Select(driver.findElement(By.xpath("//select[@id='sort-by']")));
            select2.selectByVisibleText("Price: High to Low");
            driver.navigate().refresh();

            //verify that the results are sorted from highest to lowest price.
            List<WebElement> findPrices1 = driver.findElements(By.xpath("//div[@class='pricing-details d-flex flex-column']//span[@class='heading-3']"));
            List<Integer> pricesToVerify1 = new ArrayList<>();
            for (WebElement p1 : findPrices1) {
                String price1 = p1.getText().replace("$", "").replace(",", "").split(" ")[0];
                pricesToVerify1.add(Integer.parseInt(price1));
            }
            int int2;
            for (int2 = 1; int2 <= pricesToVerify.size() - 1; int2++) {
                Assert.assertTrue(pricesToVerify1.get(int2 - 1) >= pricesToVerify1.get(int2), "Descending order is incorrect.");
            }

            //choose “Mileage: Low to High” and verify that the results are sorted from highest to lowest mileage
            Select select3 = new Select(driver.findElement(By.xpath("//select[@id='sort-by']")));
            select3.selectByVisibleText("Mileage: Low to High");
            driver.navigate().refresh();

            List<WebElement> findMileage = driver.findElements(By.xpath("//div[@class='key-point size-14 d-flex align-items-baseline mt-0_5 col-12']//span[@title='Car Mileage']//following-sibling::span"));
            List<Integer> mileage = new ArrayList<>();
            for (WebElement m : findMileage) {
                String miles = m.getText().replace(",", "").split(" ")[0];
                mileage.add(Integer.parseInt(miles));
            }
            int int3;
            for (int3 = 1; int3 <= mileage.size() - 1; int3++) {
                Assert.assertTrue(mileage.get(int3 - 1) <= mileage.get(int3), "Ascending order is incorrect.");
            }

            //Find the last result and store its title, price and mileage (get the last result dynamically,
            //i.e., your code should click on the last result regardless of how many results are there).
            //Click on it to open the details about the result.
            List<WebElement> listOfResults1 = driver.findElements(By.xpath("//li[@class='d-flex mb-0_75 mb-md-1_5 col-12 col-md-6']"));
            listOfResults1.get(listOfResults1.size() - 1).click();

            Map<Integer, String> map = new HashMap<>();
// Adding elements to map
            map.put(1, driver.findElement(By.xpath("//h1[@class='d-inline-block mb-0 heading-2 mt-0_25']")).getText());
            map.put(2, driver.findElement(By.xpath("//span[@data-testid='vdp-price-row']")).getText());
            map.put(3, driver.findElement(By.xpath("//ul[@class='mb-0 pl-0 pr-1 col-12 col-md-6']//div[@class='pr-0 font-weight-bold text-right ml-1 col']")).getText());
// Traversing Map
            Map<Integer, String> expectedMap = Map.of(1, "2023 Tesla Model 3", 2, "$38,990", 3, "3,416");
            Assert.assertEquals(map, expectedMap);

            //navigate to the previous page and check if the clicked result has "Viewed" element on it
            driver.navigate().back();
            WebElement lastResult = driver.findElement(By.xpath("//li[@class='d-flex mb-0_75 mb-md-1_5 col-12 col-md-6']//div[@class='bg-white text-gray-darker']"));
            Assert.assertTrue(lastResult.getText().contains("Viewed"));

        } finally {
            driver.quit();
        }
    }
}
