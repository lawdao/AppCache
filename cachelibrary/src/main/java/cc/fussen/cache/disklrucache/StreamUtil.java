package cc.fussen.cache.disklrucache;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by Fussen on 2017/12/12.
 */

public class StreamUtil {


    /**
     *
     * @param inputStream inputStream
     * @param cls cls
     * @param <D> T
     * @return D
     */
    public static  <D> List<D> readListStream(InputStream inputStream, Class<D> cls) {

        System.out.println("...... className : " + cls.getSimpleName() + " ......");

        List<D> resultList = new ArrayList<>();
        ObjectInputStream ois = null;

        try {
            ois = new ObjectInputStream(inputStream);
            ArrayList<D> list_ext = (ArrayList<D>) ois.readObject();

            for (D obj : list_ext) {
                if (obj != null) {
                    resultList.add(obj);
                }
            }
            return resultList;
        } catch (Exception e) {
            e.printStackTrace();
        } finally {

            if (ois != null) {
                try {
                    ois.close();
                } catch (IOException e2) {
                    e2.printStackTrace();
                }
            }
        }
        return resultList;
    }



    public static <D> D readStream(InputStream inputStream, Class<D> cls) {

        System.out.println("...... className : " + cls.getSimpleName() + " ......");

        ObjectInputStream ois = null;
        try {
            ois = new ObjectInputStream(inputStream);
            D object = (D) ois.readObject();
            return object;
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (ois != null) {
                try {
                    ois.close();
                } catch (IOException e2) {
                    e2.printStackTrace();
                }
            }
        }

        return null;
    }



    public static boolean writeToStream(OutputStream fos, Object object) {

        ObjectOutputStream oos = null;
        try {
            oos = new ObjectOutputStream(fos);
            oos.writeObject(object);
            oos.flush();
            return true;
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (oos != null) {
                try {
                    oos.close();
                } catch (IOException e1) {
                    e1.printStackTrace();
                }
            }
        }
        return false;
    }




    /**
     * url to diskStream
     *
     * @param imageUrl     imageUrl
     * @param outputStream outputStream
     * @return is
     */
    public static boolean writeUrlToStream(String imageUrl, OutputStream outputStream) {
        HttpURLConnection urlConnection = null;
        BufferedOutputStream out = null;
        BufferedInputStream in = null;
        try {
            final URL url = new URL(imageUrl);
            urlConnection = (HttpURLConnection) url.openConnection();
            in = new BufferedInputStream(urlConnection.getInputStream(), 8 * 1024);
            out = new BufferedOutputStream(outputStream, 8 * 1024);
            int b;
            while ((b = in.read()) != -1) {
                out.write(b);
            }
            return true;
        } catch (final IOException e) {
            e.printStackTrace();
        } finally {
            if (urlConnection != null) {
                urlConnection.disconnect();
            }
            try {
                if (out != null) {
                    out.close();
                }
                if (in != null) {
                    in.close();
                }
            } catch (final IOException e) {
                e.printStackTrace();
            }
        }
        return false;
    }
}
