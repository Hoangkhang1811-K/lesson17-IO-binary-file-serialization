package baitap1;

import java.io.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

public class ProductManagerApp {
    private static final String DATA_FILE = "baitap1/products.dat";

    public static void main(String[] args) {
        Scanner sc = new Scanner(System.in);
        ProductRepository repo = new ProductRepository(DATA_FILE);

        while (true) {
            System.out.println("\n===== QUAN LY SAN PHAM (FILE NHI PHAN) =====");
            System.out.println("1. Them san pham");
            System.out.println("2. Hien thi danh sach san pham");
            System.out.println("3. Tim kiem san pham");
            System.out.println("0. Thoat");
            System.out.print("Chon: ");

            String choice = sc.nextLine().trim();
            switch (choice) {
                case "1":
                    addProduct(sc, repo);
                    break;
                case "2":
                    displayProducts(repo);
                    break;
                case "3":
                    searchProducts(sc, repo);
                    break;
                case "0":
                    System.out.println("Tam biet!");
                    return;
                default:
                    System.out.println("Lua chon khong hop le. Vui long chon lai!");
            }
        }
    }

    private static void addProduct(Scanner sc, ProductRepository repo) {
        List<Product> products = repo.loadAll();

        String code;
        while (true) {
            code = readNonEmpty(sc, "Ma san pham: ");
            if (existsCode(products, code)) {
                System.out.println("Ma san pham da ton tai! Nhap ma khac.");
            } else {
                break;
            }
        }

        String name = readNonEmpty(sc, "Ten san pham: ");
        double price = readPositiveDouble(sc, "Gia: ");
        String manufacturer = readNonEmpty(sc, "Hang san xuat: ");
        String description = readNonEmpty(sc, "Mo ta: ");

        Product p = new Product(code, name, price, manufacturer, description);
        products.add(p);
        repo.saveAll(products);

        System.out.println("Da them san pham va luu vao file nh! phan.");
    }

    private static void displayProducts(ProductRepository repo) {
        List<Product> products = repo.loadAll();
        if (products.isEmpty()) {
            System.out.println("Danh sach rong.");
            return;
        }

        System.out.println("\n----- DANH SACH SAN PHAM -----");
        System.out.printf("%-15s %-25s %-12s %-20s %-30s%n",
                "Ma", "Ten", "Gia", "Hang SX", "Mo ta");
        System.out.println("-----------------------------------------------------------------------------------------------");
        for (Product p : products) {
            System.out.printf("%-15s %-25s %-12.2f %-20s %-30s%n",
                    safe(p.getCode(), 15),
                    safe(p.getName(), 25),
                    p.getPrice(),
                    safe(p.getManufacturer(), 20),
                    safe(p.getDescription(), 30));
        }
    }

    private static void searchProducts(Scanner sc, ProductRepository repo) {
        List<Product> products = repo.loadAll();
        if (products.isEmpty()) {
            System.out.println("Danh sach rong, khong co gi de tim.");
            return;
        }

        System.out.println("\nTim kiem theo:");
        System.out.println("1. Ma san pham");
        System.out.println("2. Ten san pham (chua tu khoa)");
        System.out.print("Chon: ");
        String type = sc.nextLine().trim();

        List<Product> result = new ArrayList<>();

        if ("1".equals(type)) {
            String code = readNonEmpty(sc, "Nhap ma san pham can tim: ");
            for (Product p : products) {
                if (p.getCode().equalsIgnoreCase(code)) {
                    result.add(p);
                }
            }
        } else if ("2".equals(type)) {
            String keyword = readNonEmpty(sc, "Nhap tu khoa ten san pham: ").toLowerCase();
            for (Product p : products) {
                if (p.getName().toLowerCase().contains(keyword)) {
                    result.add(p);
                }
            }
        } else {
            System.out.println("Lua chon khong hop le.");
            return;
        }

        if (result.isEmpty()) {
            System.out.println("Khong tim thay san pham phu hop.");
        } else {
            System.out.println("\n----- KET QUA TIM KIEM -----");
            System.out.printf("%-15s %-25s %-12s %-20s %-30s%n",
                    "Ma", "Ten", "Gia", "Hang SX", "Mo ta");
            System.out.println("-----------------------------------------------------------------------------------------------");
            for (Product p : result) {
                System.out.printf("%-15s %-25s %-12.2f %-20s %-30s%n",
                        safe(p.getCode(), 15),
                        safe(p.getName(), 25),
                        p.getPrice(),
                        safe(p.getManufacturer(), 20),
                        safe(p.getDescription(), 30));
            }
        }
    }

    private static boolean existsCode(List<Product> products, String code) {
        for (Product p : products) {
            if (p.getCode().equalsIgnoreCase(code)) return true;
        }
        return false;
    }

    private static String readNonEmpty(Scanner sc, String prompt) {
        while (true) {
            System.out.print(prompt);
            String s = sc.nextLine();
            if (s != null && !s.trim().isEmpty()) return s.trim();
            System.out.println("Khong duoc de trong. Vui long nhap lai.");
        }
    }

    private static double readPositiveDouble(Scanner sc, String prompt) {
        while (true) {
            System.out.print(prompt);
            String s = sc.nextLine().trim();
            try {
                double v = Double.parseDouble(s);
                if (v > 0) return v;
                System.out.println("Gia phai > 0. Vui long nhap lai.");
            } catch (NumberFormatException e) {
                System.out.println("Gia khong hop le. Vui long nhap so.");
            }
        }
    }

    private static String safe(String s, int maxLen) {
        if (s == null) return "";
        String t = s.trim();
        if (t.length() <= maxLen) return t;
        return t.substring(0, Math.max(0, maxLen - 3)) + "...";
    }
}

class Product implements Serializable {
    private static final long serialVersionUID = 1L;

    private String code;
    private String name;
    private double price;
    private String manufacturer;
    private String description;

    public Product(String code, String name, double price, String manufacturer, String description) {
        this.code = code;
        this.name = name;
        this.price = price;
        this.manufacturer = manufacturer;
        this.description = description;
    }

    public String getCode() {
        return code;
    }

    public String getName() {
        return name;
    }

    public double getPrice() {
        return price;
    }

    public String getManufacturer() {
        return manufacturer;
    }

    public String getDescription() {
        return description;
    }
}

class ProductRepository {
    private final String filePath;

    public ProductRepository(String filePath) {
        this.filePath = filePath;
    }

    public List<Product> loadAll() {
        File f = new File(filePath);
        if (!f.exists() || f.length() == 0) return new ArrayList<>();

        try (ObjectInputStream ois = new ObjectInputStream(new FileInputStream(f))) {
            Object obj = ois.readObject();
            if (obj instanceof List) {
                @SuppressWarnings("unchecked")
                List<Product> list = (List<Product>) obj;
                return list != null ? list : new ArrayList<>();
            }
        } catch (EOFException e) {
            return new ArrayList<>();
        } catch (Exception e) {
            System.out.println("Doc file that bai: " + e.getMessage());
        }
        return new ArrayList<>();
    }

    public void saveAll(List<Product> products) {
        try (ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream(filePath))) {
            oos.writeObject(products);
            oos.flush();
        } catch (IOException e) {
            System.out.println("Ghi file that bai: " + e.getMessage());
        }
    }
}
