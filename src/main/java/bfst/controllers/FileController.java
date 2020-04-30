package bfst.controllers;

import java.io.*;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

public class FileController {

    public Object loadBinary(InputStream is) throws IOException, ClassNotFoundException {
        ObjectInputStream ois = new ObjectInputStream(new BufferedInputStream(is));
        Object temp = ois.readObject();
        ois.close();
        return temp;
    }

    public void saveBinary(File file, Serializable toBeSaved) throws IOException {
        ObjectOutputStream oos = new ObjectOutputStream(new BufferedOutputStream(new FileOutputStream(file)));
        oos.writeObject(toBeSaved);
        oos.close();
    }

    public File loadZip(File file) throws Exception {
        String fileName = "";
        byte[] buffer = new byte[1024];
        ZipInputStream zis = new ZipInputStream(new FileInputStream(file.getAbsolutePath()));
        ZipEntry zipEntry = zis.getNextEntry();
        String destFolder = System.getProperty("user.home") + File.separator + "Documents";
        File destDir = new File(destFolder);

        while (zipEntry != null) {
            File newFile = new File(destDir, zipEntry.getName());
            fileName = newFile.getName();
            FileOutputStream fos = new FileOutputStream(newFile);
            int len;
            while ((len = zis.read(buffer)) > 0) {
                fos.write(buffer, 0, len);
            }
            fos.close();
            zipEntry = zis.getNextEntry();
        }
        zis.closeEntry();
        zis.close();

        return new File(destFolder + File.separator + fileName);

    }

}
