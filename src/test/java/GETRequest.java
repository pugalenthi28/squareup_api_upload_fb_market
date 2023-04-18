import io.restassured.RestAssured;
import io.restassured.http.ContentType;
import io.restassured.response.Response;
import org.apache.poi.openxml4j.exceptions.InvalidFormatException;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.ss.usermodel.WorkbookFactory;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.junit.jupiter.api.Assertions;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.HashMap;
import java.util.LinkedHashMap;

import static io.restassured.RestAssured.given;


public class GETRequest {

    public static void main(String args[]) throws IOException, InvalidFormatException {

        /*Declarations Starts*/
        String excelfilePath = "FB_Template.xlsx";
        FileInputStream inputStream = new FileInputStream(excelfilePath);
        String apiKey = "EAAAEO_ZMaIZ4xTUOMmACsarvxFf5uRGqtmDvbyySpVuObGuVdg3sl_xLJXg9jt3";
        Workbook workbook = WorkbookFactory.create(inputStream);
        LinkedHashMap<Object, Object> data;
        RestAssured.baseURI = "https://connect.squareup.com/v2/catalog/";
        /*Declarations Ends*/
        Response response = given().header("Authorization", "Bearer " + apiKey).contentType(ContentType.JSON).get("/list");
        Assertions.assertEquals(200, response.statusCode());
        JSONObject object = new JSONObject(response.getBody().asString());
        JSONArray object1 = object.getJSONArray("objects");
        for (int k = 0; k < object1.length(); k++) {
            data = new LinkedHashMap<>();
            JSONObject itemdata = (JSONObject) object1.get(k);
            try {
                JSONObject location = (JSONObject) itemdata.get("item_data");
                JSONArray uri = location.getJSONArray("ecom_image_uris");
                /*Read Data from API and Place it in HashMap Starts*/
                data.put("id", itemdata.get("id"));
                data.put("title", location.get("name"));
                data.put("description", location.get("name").toString().split("]")[1]);
                data.put("availability", "In Stock");
                data.put("condition", "New");
                data.put("price", "5.99");
                data.put("link", location.get("ecom_uri"));
                String imageURL = uri.toString().replaceAll("[\\[\\]]", "").replace("\"", "").split(",")[0];
                data.put("image_link",imageURL );
                data.put("additional_image_link", imageURL);
                data.put("brand", "ShotsByPugalenthi");
                data.put("google_product_category", "783");
                data.put("fb_product_category", "147");
                data.put("quantity_to_sell_on_facebook", "100");
                data.put("sale_price", " ");
                data.put("sale_price_effective_date", " ");
                data.put("commerce_tax_category", "FB_CAMERA_PHOTO");
                /*Read Data from API and Place it in HashMap Ends*/
                /*Writing into Excel Starts*/
                Sheet sheet = workbook.getSheetAt(0);
                int rowcount = sheet.getLastRowNum();
                Row row = sheet.createRow(++rowcount);
                int columnCount = 0;
                for (HashMap.Entry entry : data.entrySet()) {
                    row.createCell(++columnCount - 1).setCellValue((String) entry.getValue());
                }
                /*Writing into Excel Ends*/
                System.out.println("*************Product Details of Item #" + k);
                System.out.println("Product ID " + itemdata.get("id"));
                System.out.println("Product Name " + location.get("name"));
                System.out.println("Product URL " + location.get("ecom_uri"));
                System.out.println("Product Image URL " + imageURL);

            } catch (JSONException e) {
            }
        }
        inputStream.close();
        FileOutputStream outputStream = new FileOutputStream("FB_Template_Updated_With_Data.xlsx");
        workbook.write(outputStream);
        outputStream.close();
    }
}