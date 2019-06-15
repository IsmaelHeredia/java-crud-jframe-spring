// Written by Ismael Heredia in the year 2016

package sistema.functions;

import java.security.MessageDigest;
import java.text.SimpleDateFormat;
import java.util.Calendar;

public class Funciones {

    public String md5_encode(String text) {
        // Credits : Based on http://www.avajava.com/tutorials/lessons/how-do-i-generate-an-md5-digest-for-a-string.html
        StringBuffer string_now = null;
        try {
            MessageDigest generate = MessageDigest.getInstance("MD5");
            generate.update(text.getBytes());
            byte[] result = generate.digest();
            string_now = new StringBuffer();
            for (byte line : result) {
                string_now.append(String.format("%02x", line & 0xff));
            }
        } catch (Exception e) {
            //
        }
        return string_now.toString();
    }

    public static boolean valid_number(String str) {
        try {
            double d = Double.parseDouble(str);
        } catch (NumberFormatException nfe) {
            return false;
        }
        return true;
    }

    public String fecha_del_dia() {
        String fecha = new SimpleDateFormat("yyyy-MM-dd").format(Calendar.getInstance().getTime());
        return fecha;
    }

}
