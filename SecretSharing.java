import org.json.JSONObject;
import java.math.BigInteger;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

public class SecretSharing {

    public static void main(String[] args) {
        String filePath = "testcase.json"; // Path to the JSON file
        try {
            String content = new String(Files.readAllBytes(Paths.get(filePath)));
            JSONObject jsonObject = new JSONObject(content);
            int n = jsonObject.getJSONObject("keys").getInt("n");
            int k = jsonObject.getJSONObject("keys").getInt("k");

            List<Point> points = new ArrayList<>();
            for (int i = 1; i <= n; i++) {
                JSONObject point = jsonObject.getJSONObject(String.valueOf(i));
                int base = point.getInt("base");
                String value = point.getString("value");
                int x = i;
                BigInteger y = decodeValue(base, value);
                points.add(new Point(x, y));
            }

            // Use only the first k points for the polynomial
            points = points.subList(0, k);
            BigInteger c = lagrangeInterpolation(points);
            System.out.println("Secret (c): " + c);

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static BigInteger decodeValue(int base, String value) {
        return new BigInteger(value, base);
    }
    // public static int decodeValue(int base, String value) {
    //     return Integer.parseInt(value, base);
    // }

    public static BigInteger lagrangeInterpolation(List<Point> points) {
        BigInteger totalSum = BigInteger.ZERO;
        for (int i = 0; i < points.size(); i++) {
            Point p = points.get(i);
            int x_i = p.x;
            BigInteger y_i = p.y;
    
            // Calculate the Lagrange basis polynomial
            BigInteger product = BigInteger.ONE;
            for (int j = 0; j < points.size(); j++) {
                if (j != i) {
                    Point q = points.get(j);
                    product = product.multiply(BigInteger.valueOf(0 - q.x)).divide(BigInteger.valueOf(x_i - q.x));
                }
            }
            totalSum = totalSum.add(y_i.multiply(product));
        }
        return totalSum;
    }
    

    static class Point {
        int x;
        BigInteger y; // Change type to BigInteger
    
        Point(int x, BigInteger y2) {
            this.x = x;
            this.y = y2;
        }
    }
}
