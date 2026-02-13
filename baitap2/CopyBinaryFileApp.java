package baitap2;

import java.io.*;
import java.util.Scanner;

public class CopyBinaryFileApp {

    public static void main(String[] args) {
        Scanner sc = new Scanner(System.in);

        System.out.println("===== COPY FILE NHI PHAN =====");
        System.out.print("Nhap duong dan file nguon (source): ");
        String sourcePath = sc.nextLine().trim();

        System.out.print("Nhap duong dan file dich (target): ");
        String targetPath = sc.nextLine().trim();

        File source = new File(sourcePath);
        if (!source.exists() || !source.isFile()) {
            System.out.println("CANH BAO: File nguon khong ton tai hoac khong phai file!");
            return;
        }

        File target = new File(targetPath);
        if (target.exists()) {
            System.out.print("CANH BAO: File dich da ton tai. Ban co muon ghi de? (Y/N): ");
            String ans = sc.nextLine().trim();
            if (!ans.equalsIgnoreCase("Y")) {
                System.out.println("Da huy thao tac copy.");
                return;
            }
        } else {
            File parent = target.getParentFile();
            if (parent != null && !parent.exists()) {
                if (!parent.mkdirs()) {
                    System.out.println("Khong the tao thu muc dich!");
                    return;
                }
            }
        }

        long copiedBytes = copyBinary(source, target);
        if (copiedBytes >= 0) {
            System.out.println("Copy thanh cong!");
            System.out.println("So byte da copy: " + copiedBytes);
        } else {
            System.out.println("Copy that bai!");
        }
    }

    private static long copyBinary(File source, File target) {
        byte[] buffer = new byte[8192];
        long total = 0;

        try (InputStream in = new FileInputStream(source);
             OutputStream out = new FileOutputStream(target)) {

            int n;
            while ((n = in.read(buffer)) != -1) {
                out.write(buffer, 0, n);
                total += n;
            }
            out.flush();
            return total;

        } catch (IOException e) {
            System.out.println("Loi I/O: " + e.getMessage());
            return -1;
        }
    }
}
