package web.util;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;

public class IOUtil {
	
	public static String toString(InputStream is) throws IOException{
		byte[] b = new byte[8096];
		int length = 0;
		ByteArrayOutputStream baos = new ByteArrayOutputStream();
		try {
			while ((length = is.read(b)) != -1) {
				baos.write(b, 0, length);
			}
			baos.flush();
			String result = new String(baos.toByteArray(), "UTF-8");
			is.close();
			baos.close();
			return result;
		} catch (Exception e) {
			System.err.println(e.getMessage());
			return "";
		} finally {
			if(is != null){
				try {
					is.close();
				} catch (Exception e) {
				}
			}
			if(baos != null){
				try {
					baos.close();
				} catch (Exception e) {
				}
			}
		}
	}
}
